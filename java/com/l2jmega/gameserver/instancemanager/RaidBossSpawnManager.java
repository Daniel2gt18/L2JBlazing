package com.l2jmega.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.data.xml.RaidSpawnTable;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.RaidBoss;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.util.Util;

/**
 * @author godson
 **/
public class RaidBossSpawnManager
{
	protected static final Logger _log = Logger.getLogger(RaidBossSpawnManager.class.getName());
	
	protected static final Map<Integer, RaidBoss> _bosses = new HashMap<>();
	protected static final Map<Integer, L2Spawn> _spawns = new HashMap<>();
	protected static final Map<Integer, StatsSet> _storedInfo = new HashMap<>();
	protected static final Map<Integer, ScheduledFuture<?>> _schedules = new HashMap<>();
	
	public static enum StatusEnum
	{
		ALIVE,
		DEAD,
		UNDEFINED
	}
	
	public RaidBossSpawnManager()
	{
		init();
	}
	
	public static RaidBossSpawnManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private void init()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * from raidboss_spawnlist ORDER BY boss_id");
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				final NpcTemplate template = getValidTemplate(rset.getInt("boss_id"));
				if (template != null)
				{
					final L2Spawn spawnDat = new L2Spawn(template);
					spawnDat.setLoc(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_z"), rset.getInt("heading"));
					if(Config.RESPAWN_CUSTOM && Config.RAID_RESPAWN_IDS_LIST.contains(Integer.valueOf(template.getNpcId()))){
						spawnDat.setRespawnMinDelay(Config.MIN_RESPAWN);
						spawnDat.setRespawnMaxDelay(Config.MAX_RESPAWN);
					}else{
						spawnDat.setRespawnMinDelay(rset.getInt("spawn_time"));
						spawnDat.setRespawnMaxDelay(rset.getInt("random_time"));
					}
					
					addNewSpawn(spawnDat, rset.getLong("respawn_time"), rset.getDouble("currentHP"), rset.getDouble("currentMP"), false);
				}
				else
				{
					_log.warning("RaidBossSpawnManager: Could not load raidboss #" + rset.getInt("boss_id") + " from DB");
				}
			}
			
			_log.info("RaidBossSpawnManager: Loaded " + _bosses.size() + " instances.");
			_log.info("RaidBossSpawnManager: Scheduled " + _schedules.size() + " instances.");
			
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning("RaidBossSpawnManager: Couldnt load raidboss_spawnlist table.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Error while initializing RaidBossSpawnManager: " + e.getMessage(), e);
		}
	}
	
	private static class spawnSchedule implements Runnable
	{
		private final int bossId;
		
		public spawnSchedule(int npcId)
		{
			bossId = npcId;
		}
		
		@Override
		public void run()
		{
			RaidBoss raidboss = null;
			
			if (bossId == 25328)
				raidboss = DayNightSpawnManager.getInstance().handleBoss(_spawns.get(bossId));
			else
				raidboss = (RaidBoss) _spawns.get(bossId).doSpawn(false);
			
			if (raidboss != null)
			{
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				final StatsSet info = new StatsSet();
				info.set("currentHP", raidboss.getCurrentHp());
				info.set("currentMP", raidboss.getCurrentMp());
				info.set("respawnTime", 0L);
				
				_storedInfo.put(bossId, info);
				
				_log.info("RaidBoss: " + raidboss.getName() + " has spawned.");
				
				if (Config.ANNOUNCE_TO_ALL_SPAWN_RB && Config.LIST_RAID_ANNOUNCE.contains(raidboss.getNpcId()))
				{
					for (Player player : World.getInstance().getPlayers())
					{
						//CreatureSay a = new CreatureSay(player.getObjectId(), Config.ANNOUNCE_ID, "", "Raid Boss " + raidboss.getName() + " is alive!"); // 8D
						CreatureSay b = new CreatureSay(player.getObjectId(),Say2.TELL, "", "Raid Boss " + raidboss.getName() + " is alive!"); // 8D
						
						//if (Config.ANNOUNCE_ID == 18 || Config.ANNOUNCE_ID == 10)
						//	player.sendPacket(a);
						//else
						player.sendPacket(b);
					}
					
				}
				
				_bosses.put(bossId, raidboss);
			}
			
			_schedules.remove(bossId);
			
			RaidBossInfoManager.getInstance().updateRaidBossInfo(bossId, 0);
			
		}
	}
	
	
	public void updateStatus(RaidBoss boss, boolean isBossDead)
	{
		if (!_storedInfo.containsKey(boss.getNpcId()))
			return;
		
		StatsSet info = _storedInfo.get(boss.getNpcId());
		
		if (isBossDead)
		{
			boss.setRaidStatus(StatusEnum.DEAD);
			int respawnDelay = 0;
			long respawnTime = 0L;
			if (RaidSpawnTable.getInstance().containsBoss(boss.getNpcId())) {
				List<String> _newSpawn = RaidSpawnTable.getInstance().getBossSpawn(boss.getNpcId());
				Calendar currentTime = Calendar.getInstance();
				Calendar nextStartTime = null;
				Calendar testStartTime = null;
				for (String timeOfDay : _newSpawn) {
					testStartTime = Calendar.getInstance();
					testStartTime.setLenient(true);
					String[] splitTimeOfDay = timeOfDay.split(":");
					testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
					testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
					testStartTime.set(13, 0);
					if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
						testStartTime.add(5, 1); 
					if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
						nextStartTime = testStartTime; 
					respawnTime = nextStartTime.getTimeInMillis();
				} 
			}
			else {
				// getRespawnMinDelay() is used as fixed timer, while getRespawnMaxDelay() is used as random timer.
				respawnDelay = boss.getSpawn().getRespawnMinDelay() + Rnd.get(-boss.getSpawn().getRespawnMaxDelay(), boss.getSpawn().getRespawnMaxDelay());
				respawnTime = Calendar.getInstance().getTimeInMillis() + (respawnDelay * 3600000);
			}
			info.set("currentHP", boss.getMaxHp());
			info.set("currentMP", boss.getMaxMp());
			info.set("respawnTime", respawnTime);
			
			if (!_schedules.containsKey(boss.getNpcId()))
			{
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(respawnTime);
				
				if (RaidSpawnTable.getInstance().containsBoss(boss.getNpcId())) {
					_log.info("RaidBoss: " + boss.getName() + " - " + Util.formatDate(time.getTime(), "d MMM yyyy HH:mm"));
					_schedules.put(Integer.valueOf(boss.getNpcId()), ThreadPool.schedule(new spawnSchedule(boss.getNpcId()), respawnTime - System.currentTimeMillis()));
				} else {
					_log.info("RaidBoss: " + boss.getName() + " - " + Util.formatDate(time.getTime(), "d MMM yyyy HH:mm") + " (" + respawnDelay + "h).");
					_schedules.put(Integer.valueOf(boss.getNpcId()), ThreadPool.schedule(new spawnSchedule(boss.getNpcId()), (respawnDelay * 3600000)));
				} 
				updateDb();
			}
			
			RaidBossInfoManager.getInstance().updateRaidBossInfo(boss.getNpcId(), respawnTime);			
		}
		else
		{
			boss.setRaidStatus(StatusEnum.ALIVE);
			info.set("currentHP", boss.getCurrentHp());
			info.set("currentMP", boss.getCurrentMp());
			info.set("respawnTime", 0L);
		}
		
		_storedInfo.put(boss.getNpcId(), info);
	}
	
	public void addNewSpawn(L2Spawn spawnDat, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
	{
		if (spawnDat == null)
			return;
		
		final int bossId = spawnDat.getNpcId();
		if (_spawns.containsKey(bossId))
			return;
		
		final long time = Calendar.getInstance().getTimeInMillis();
		
		SpawnTable.getInstance().addNewSpawn(spawnDat, false);
		
		if (respawnTime == 0L || (time > respawnTime))
		{
			RaidBoss raidboss = null;
			
			if (bossId == 25328)
				raidboss = DayNightSpawnManager.getInstance().handleBoss(spawnDat);
			else
				raidboss = (RaidBoss) spawnDat.doSpawn(false);
			
			if (raidboss != null)
			{
				currentHP = (currentHP == 0) ? raidboss.getMaxHp() : currentHP;
				currentMP = (currentMP == 0) ? raidboss.getMaxMp() : currentMP;
				
				raidboss.setCurrentHp(currentHP);
				raidboss.setCurrentMp(currentMP);
				raidboss.setRaidStatus(StatusEnum.ALIVE);
				
				_bosses.put(bossId, raidboss);
				
				final StatsSet info = new StatsSet();
				info.set("currentHP", currentHP);
				info.set("currentMP", currentMP);
				info.set("respawnTime", 0L);
				
				_storedInfo.put(bossId, info);
			}
		}
		else
		{
			long spawnTime = respawnTime - Calendar.getInstance().getTimeInMillis();
			_schedules.put(bossId, ThreadPool.schedule(new spawnSchedule(bossId), spawnTime));
		}
		
		_spawns.put(bossId, spawnDat);
		
		if (storeInDb)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("INSERT INTO raidboss_spawnlist (boss_id,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?)");
				statement.setInt(1, spawnDat.getNpcId());
				statement.setInt(2, spawnDat.getLocX());
				statement.setInt(3, spawnDat.getLocY());
				statement.setInt(4, spawnDat.getLocZ());
				statement.setInt(5, spawnDat.getHeading());
				statement.setLong(6, respawnTime);
				statement.setDouble(7, currentHP);
				statement.setDouble(8, currentMP);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with storing spawn
				_log.log(Level.WARNING, "RaidBossSpawnManager: Could not store raidboss #" + bossId + " in the DB:" + e.getMessage(), e);
			}
		}
	}
	
	public void deleteSpawn(L2Spawn spawnDat, boolean updateDb)
	{
		if (spawnDat == null)
			return;
		
		final int bossId = spawnDat.getNpcId();
		if (!_spawns.containsKey(bossId))
			return;
		
		SpawnTable.getInstance().deleteSpawn(spawnDat, false);
		_spawns.remove(bossId);
		
		if (_bosses.containsKey(bossId))
			_bosses.remove(bossId);
		
		if (_schedules.containsKey(bossId))
		{
			final ScheduledFuture<?> f = _schedules.remove(bossId);
			f.cancel(true);
		}
		
		if (_storedInfo.containsKey(bossId))
			_storedInfo.remove(bossId);
		
		if (updateDb)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("DELETE FROM raidboss_spawnlist WHERE boss_id=?");
				statement.setInt(1, bossId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with deleting spawn
				_log.log(Level.WARNING, "RaidBossSpawnManager: Could not remove raidboss #" + bossId + " from DB: " + e.getMessage(), e);
			}
		}
	}
	
	private void updateDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE raidboss_spawnlist SET respawn_time = ?, currentHP = ?, currentMP = ? WHERE boss_id = ?");
			
			for (Map.Entry<Integer, StatsSet> infoEntry : _storedInfo.entrySet())
			{
				final int bossId = infoEntry.getKey();
				
				final RaidBoss boss = _bosses.get(bossId);
				if (boss == null)
					continue;
				
				if (boss.getRaidStatus().equals(StatusEnum.ALIVE))
					updateStatus(boss, false);
				
				final StatsSet info = infoEntry.getValue();
				if (info == null)
					continue;
				
				statement.setLong(1, info.getLong("respawnTime"));
				statement.setDouble(2, info.getDouble("currentHP"));
				statement.setDouble(3, info.getDouble("currentMP"));
				statement.setInt(4, bossId);
				statement.executeUpdate();
				statement.clearParameters();
			}
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "RaidBossSpawnManager: Couldnt update raidboss_spawnlist table " + e.getMessage(), e);
		}
	}
	
	public StatusEnum getRaidBossStatusId(int bossId)
	{
		if (_bosses.containsKey(bossId))
			return _bosses.get(bossId).getRaidStatus();
		
		if (_schedules.containsKey(bossId))
			return StatusEnum.DEAD;
		
		return StatusEnum.UNDEFINED;
	}
	
	public NpcTemplate getValidTemplate(int bossId)
	{
		NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
		if (template == null)
			return null;
		
		if (!template.isType("RaidBoss"))
			return null;
		
		return template;
	}
	
	public void notifySpawnNightBoss(RaidBoss raidboss)
	{
		final StatsSet info = new StatsSet();
		info.set("currentHP", raidboss.getCurrentHp());
		info.set("currentMP", raidboss.getCurrentMp());
		info.set("respawnTime", 0L);
		
		raidboss.setRaidStatus(StatusEnum.ALIVE);
		
		_storedInfo.put(raidboss.getNpcId(), info);
		_bosses.put(raidboss.getNpcId(), raidboss);
		
		_log.info("RaidBossSpawnManager: Spawning Night Raid Boss " + raidboss.getName());
	}
	
	public boolean isDefined(int bossId)
	{
		return _spawns.containsKey(bossId);
	}
	
	public Map<Integer, RaidBoss> getBosses()
	{
		return _bosses;
	}
	
	public Map<Integer, L2Spawn> getSpawns()
	{
		return _spawns;
	}
	
	public void reloadBosses()
	{
		init();
	}
	
	/**
	 * Saves all raidboss status and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		updateDb();
		
		_bosses.clear();
		
		if (!_schedules.isEmpty())
		{
			for (ScheduledFuture<?> f : _schedules.values())
				f.cancel(true);
			
			_schedules.clear();
		}
		
		_storedInfo.clear();
		_spawns.clear();
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossSpawnManager _instance = new RaidBossSpawnManager();
	}
}