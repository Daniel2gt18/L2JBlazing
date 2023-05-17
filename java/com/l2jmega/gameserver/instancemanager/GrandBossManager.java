package com.l2jmega.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.GrandBoss;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Antharas;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Sailren;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Valakas;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Zaken;
import com.l2jmega.gameserver.templates.StatsSet;

/**
 * This class handles the status of all Grand Bosses, and manages L2BossZone zones.
 * @author DaRkRaGe, Emperorc
 */
public class GrandBossManager
{
	protected static Logger _log = Logger.getLogger(GrandBossManager.class.getName());
	
	private static final String SELECT_GRAND_BOSS_DATA = "SELECT * from grandboss_data ORDER BY boss_id";
	private static final String UPDATE_GRAND_BOSS_DATA = "UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?";
	private static final String UPDATE_GRAND_BOSS_DATA2 = "UPDATE grandboss_data set status = ? where boss_id = ?";
	
	private final Map<Integer, GrandBoss> _bosses = new HashMap<>();
	private final Map<Integer, StatsSet> _storedInfo = new HashMap<>();
	private final Map<Integer, Integer> _bossStatus = new HashMap<>();
	
	public static GrandBossManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected GrandBossManager()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT_GRAND_BOSS_DATA);
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				StatsSet info = new StatsSet();
				
				int bossId = rset.getInt("boss_id");
				
				info.set("loc_x", rset.getInt("loc_x"));
				info.set("loc_y", rset.getInt("loc_y"));
				info.set("loc_z", rset.getInt("loc_z"));
				info.set("heading", rset.getInt("heading"));
				info.set("respawn_time", rset.getLong("respawn_time"));
				info.set("currentHP", rset.getDouble("currentHP"));
				info.set("currentMP", rset.getDouble("currentMP"));
				
				_bossStatus.put(bossId, rset.getInt("status"));
				_storedInfo.put(bossId, info);
			}
			rset.close();
			statement.close();
			
			_log.info("GrandBossManager: Loaded " + _storedInfo.size() + " GrandBosses instances.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Could not load grandboss data: " + e.getMessage(), e);
		}
	}
	
	public int getBossStatus(int bossId)
	{
		return _bossStatus.get(bossId);
	}

	public static boolean _announce;
	
	public void setBossStatus(int bossId, int status)
	{
		_bossStatus.put(bossId, status);
		_log.info("GrandBossManager: Updated " + NpcTable.getInstance().getTemplate(bossId).getName() + " (id: " + bossId + ") status to " + status);
		updateDb(bossId, true);
		
		if (bossId == 29022 && status == 0)
		{
			AnnounceGrandBoss("Zaken Is alive!");
			
			DoorTable.getInstance().getDoor(21240006).openMe();
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					DoorTable.getInstance().getDoor(21240006).closeMe();
				}
			}, Config.WAIT_TIME_ZAKEN);
			Zaken._open = true;
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Zaken.waiter(Config.WAIT_TIME_ZAKEN);
				}
			}, 1);		
		}
		else if (bossId == 29019 && status == 1)
		{
			_announce = true;
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Antharas.waiter(Config.WAIT_TIME_ANTHARAS);
				}
			}, 1);
		}
		else if (bossId == 29028 && status == 1)
		{
			_announce = true;
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Valakas.waiter(Config.WAIT_TIME_VALAKAS);
				}
			}, 1);
		}
		else if (bossId == 29065 && status == 1)
		{
			_announce = true;
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Sailren.waiter(Config.WAIT_TIME_SAILREN);
				}
			}, 1);
		}
		else if ((bossId == 29020) && status == 1)
			AnnounceGrandBoss("Baium as Awake!");
		else if ((bossId == 29001) && status == 0)
			AnnounceGrandBoss("QueenAnt is Alive!");
		else if ((bossId == 29006) && status == 0)
			AnnounceGrandBoss("Core is Alive!");
		else if ((bossId == 29014) && status == 0)
			AnnounceGrandBoss("Orfen is Alive!");	
	}
	
	/**
	 * Adds a L2GrandBossInstance to the list of bosses.
	 * @param boss The boss to add.
	 */
	public void addBoss(GrandBoss boss)
	{
		if (boss != null)
			_bosses.put(boss.getNpcId(), boss);
	}
	
	/**
	 * Adds a L2GrandBossInstance to the list of bosses. Using this variant of addBoss, we can impose a npcId.
	 * @param npcId The npcId to use for registration.
	 * @param boss The boss to add.
	 */
	public void addBoss(int npcId, GrandBoss boss)
	{
		if (boss != null)
			_bosses.put(npcId, boss);
	}
	
	public GrandBoss getBoss(int bossId)
	{
		return _bosses.get(bossId);
	}
	
	public StatsSet getStatsSet(int bossId)
	{
		return _storedInfo.get(bossId);
	}
	
	public void setStatsSet(int bossId, StatsSet info)
	{
		_storedInfo.put(bossId, info);
		updateDb(bossId, false);

			RaidBossInfoManager.getInstance().updateRaidBossInfo(bossId, info.getLong("respawn_time"));
	}
	
	private void storeToDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement updateStatement1 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2);
			PreparedStatement updateStatement2 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA);
			
			for (Map.Entry<Integer, StatsSet> infoEntry : _storedInfo.entrySet())
			{
				final int bossId = infoEntry.getKey();
				
				GrandBoss boss = _bosses.get(bossId);
				StatsSet info = infoEntry.getValue();
				if (boss == null || info == null)
				{
					updateStatement1.setInt(1, _bossStatus.get(bossId));
					updateStatement1.setInt(2, bossId);
					updateStatement1.executeUpdate();
					updateStatement1.clearParameters();
				}
				else
				{
					updateStatement2.setInt(1, boss.getX());
					updateStatement2.setInt(2, boss.getY());
					updateStatement2.setInt(3, boss.getZ());
					updateStatement2.setInt(4, boss.getHeading());
					updateStatement2.setLong(5, info.getLong("respawn_time"));
					updateStatement2.setDouble(6, (boss.isDead()) ? boss.getMaxHp() : boss.getCurrentHp());
					updateStatement2.setDouble(7, (boss.isDead()) ? boss.getMaxMp() : boss.getCurrentMp());
					updateStatement2.setInt(8, _bossStatus.get(bossId));
					updateStatement2.setInt(9, bossId);
					updateStatement2.executeUpdate();
					updateStatement2.clearParameters();
				}
			}
			updateStatement1.close();
			updateStatement2.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Couldn't store grandbosses to database:" + e.getMessage(), e);
		}
	}
	
	private void updateDb(int bossId, boolean statusOnly)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			GrandBoss boss = _bosses.get(bossId);
			StatsSet info = _storedInfo.get(bossId);
			PreparedStatement statement = null;
			
			if (statusOnly || boss == null || info == null)
			{
				statement = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2);
				statement.setInt(1, _bossStatus.get(bossId));
				statement.setInt(2, bossId);
			}
			else
			{
				statement = con.prepareStatement(UPDATE_GRAND_BOSS_DATA);
				statement.setInt(1, boss.getX());
				statement.setInt(2, boss.getY());
				statement.setInt(3, boss.getZ());
				statement.setInt(4, boss.getHeading());
				statement.setLong(5, info.getLong("respawn_time"));
				statement.setDouble(6, (boss.isDead()) ? boss.getMaxHp() : boss.getCurrentHp());
				statement.setDouble(7, (boss.isDead()) ? boss.getMaxMp() : boss.getCurrentMp());
				statement.setInt(8, _bossStatus.get(bossId));
				statement.setInt(9, bossId);
			}
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Couldn't update grandbosses to database:" + e.getMessage(), e);
		}
	}
	
	/**
	 * Saves all Grand Boss info and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		storeToDb();
		
		_bosses.clear();
		_storedInfo.clear();
		_bossStatus.clear();
	}
	
	private static class SingletonHolder
	{
		protected static final GrandBossManager _instance = new GrandBossManager();
	}
	
	public static void AnnounceGrandBoss(String text)
	{
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID, "Grand Boss", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
}