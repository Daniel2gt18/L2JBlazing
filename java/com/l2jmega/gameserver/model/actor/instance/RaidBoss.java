package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jmega.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jmega.gameserver.instancemanager.RaidBossSpawnManager.StatusEnum;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.entity.Hero;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.Earthquake;
import com.l2jmega.gameserver.network.serverpackets.ExRedSky;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;

import java.util.concurrent.ScheduledFuture;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

/**
 * This class manages all RaidBoss. In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public class RaidBoss extends Monster
{
	private StatusEnum _raidStatus;
	private ScheduledFuture<?> _maintenanceTask;
	
	/**
	 * Constructor of L2RaidBossInstance (use Creature and L2NpcInstance constructor).
	 * <ul>
	 * <li>Call the Creature constructor to set the _template of the L2RaidBossInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2RaidBossInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @param template L2NpcTemplate to apply to the NPC
	 */
	public RaidBoss(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setIsRaid(true);
	}
	
	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		if (killer != null)
		{
			final Player player = killer.getActingPlayer();
			if (player != null)
			{
				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
				broadcastPacket(new PlaySound("systemmsg_e.1209"));
				
		        if (player.getClan() != null && Config.RAID_INFO_IDS_LIST.contains(Integer.valueOf(getNpcId())))
		            player.getClan().addclanBossScore(1);
				
				final L2Party party = player.getParty();
				if (party != null)
				{
					for (Player member : party.getPartyMembers())
					{
						RaidBossPointsManager.getInstance().addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
						if (member.isNoble())
							Hero.getInstance().setRBkilled(member.getObjectId(), getNpcId());
					}
				}
				else
				{
					RaidBossPointsManager.getInstance().addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
					if (player.isNoble())
						Hero.getInstance().setRBkilled(player.getObjectId(), getNpcId());
				}
				
				if (!player.isGM())
				{
					for (Player pl : World.getInstance().getPlayers())
					{
						if (player.getClan() != null)
							pl.sendChatMessage(0, Config.ANNOUNCE_ID, "Raid Boss ", getName() + " was killed by " + player.getName() + " of the clan " + player.getClan().getName());
						else
							pl.sendChatMessage(0, Config.ANNOUNCE_ID, "Raid Boss ", getName() + " was killed by " + player.getName());
						
						if(Config.EARTH_QUAKE){
							pl.broadcastPacket(new ExRedSky(10));
							pl.broadcastPacket(new Earthquake(pl.getX(), pl.getY(), pl.getZ(), 14, 10));
							}
					}
				}
				
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						for (Player pl : player.getKnownTypeInRadius(Player.class, 6000))
						{
							if (!pl.isInsideZone(ZoneId.PEACE))
								PvpFlagTaskManager.getInstance().add(pl, 60000);
						}
					}
				}, 3000);
				
				
				if (Config.ALLOW_AUTO_NOBLESS_FROM_BOSS && getNpcId() == Config.BOSS_ID)
				{
					if (player.getParty() != null)
					{
						for (Player member : player.getParty().getPartyMembers())
						{
							if (member.isNoble())
								member.sendMessage("Your party gained nobless status for defeating " + getName() + "!");
							else if (member.isInsideRadius(getX(), getY(), getZ(), Config.RADIUS_TO_RAID, false, false))
							{
								member.broadcastPacket(new SocialAction(member.getObjectId(), 16));
								member.setNoble(true, true);
								member.sendMessage("Your party gained nobless status for defeating " + getName() + "!");
							}
							else
							{
								member.sendMessage("Your party killed " + getName() + "! But you were to far away and earned nothing...");
							}
						}
						
					}
					else if (player.getParty() == null && !player.isNoble())
					{
						player.setNoble(true, true);
						player.sendMessage("You gained nobless status for defeating");
					}
				}
			}
		
		}	
		
			
		if (!getSpawn().is_customBossInstance())
			RaidBossSpawnManager.getInstance().updateStatus(this, true);
		return true;
	}

	@Override
	public void deleteMe()
	{
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		super.deleteMe();
	}
	
	/**
	 * Spawn minions.<br>
	 * Also if boss is too far from home location at the time of this check, teleport it to home.
	 */
	@Override
	protected void startMaintenanceTask()
	{
		super.startMaintenanceTask();
		
		_maintenanceTask = ThreadPool.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				// If the boss is dead, movement disabled, is Gordon or is in combat, return.
				if (isDead() || isMovementDisabled() || getNpcId() == 29095 || isInCombat())
					return;
				
				// Spawn must exist.
				final L2Spawn spawn = getSpawn();
				if (spawn == null)
					return;
				
				// If the boss is above drift range (or 200 minimum), teleport him on his spawn.
				if (!isInsideRadius(spawn.getLocX(), spawn.getLocY(), spawn.getLocZ(), Math.max(Config.MAX_DRIFT_RANGE, 200), true, false))
					teleToLocation(spawn.getLoc(), 0);
			}
		}, 60000, 30000);
	}
	
	  protected static L2Spawn spawnNPC(int xPos, int yPos, int zPos, int npcId)
	  {
	    NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
	    try
	    {
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(xPos, yPos, zPos, 0);
			spawn.setRespawnDelay(0);
			
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			
			spawn.setRespawnState(true);
			spawn.doSpawn(false);
			spawn.getNpc().isAggressive();
			spawn.getNpc().decayMe();
			spawn.getNpc().spawnMe(spawn.getNpc().getX(), spawn.getNpc().getY(), spawn.getNpc().getZ());
			spawn.getNpc().broadcastPacket(new MagicSkillUse(spawn.getNpc(), spawn.getNpc(), 1034, 1, 1, 1));
			return spawn;
	    }
		catch (Exception e)
		{
			return null;
		}
	}
	  
		public static void Announce(String text)
		{
			CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID, "", "" + text);
			
			for (Player player : World.getInstance().getPlayers())
			{
				if (player != null)
					if (player.isOnline())
						player.sendPacket(cs);
				
			}
			cs = null;
		}
	
	public StatusEnum getRaidStatus()
	{
		return _raidStatus;
	}
	
	public void setRaidStatus(StatusEnum status)
	{
		_raidStatus = status;
	}
}