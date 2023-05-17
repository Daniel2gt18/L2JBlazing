/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.events;

import com.l2jmega.Config;
import com.l2jmega.gameserver.ArenaEvent;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminCustom;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

import com.l2jmega.commons.concurrent.ThreadPool;


public abstract class ArenaTask
{
	public static L2Spawn _npcSpawn1;
	public static L2Spawn _npcSpawn2;
	
	public static int _bossHeading = 0;
	
	/** The _in progress. */
	public static boolean _started = false;
	
	public static boolean _aborted = false;
	
	public static void SpawnEvent()
	{
		Arena1x1.getInstance().clear();
		Arena2x2.getInstance().clear();
		Arena5x5.getInstance().clear();
		Arena9x9.getInstance().clear();
		
		spawnNpc1();
		spawnNpc2();
		Announcement.ArenaAnnounce("Event Party vs Party");
		Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Teleport in the GK to (Tour) Zone");
		Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Duration: " + Config.TOURNAMENT_TIME + " minute(s)!");
		
		_aborted = false;
		_started = true;
		ThreadPool.schedule(Arena1x1.getInstance(), 5000);
		ThreadPool.schedule(Arena2x2.getInstance(), 5000);
		ThreadPool.schedule(Arena9x9.getInstance(), 5000);
		ThreadPool.schedule(Arena5x5.getInstance(), 5000);
		
		waiter(Config.TOURNAMENT_TIME * 60 * 1000); // minutes for event time
		
		if (!_aborted)
			finishEvent();
	}
	
	public static void finishEvent()
	{
		Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Event Finished!");
		
		unspawnNpc1();
		unspawnNpc2();
		_started = false;
		
		if (!AdminCustom._arena_manual)
		{
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else
		{
			AdminCustom._arena_manual = false;
		}
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				if (player.isArenaProtection())
				{
					ThreadPool.schedule(new Runnable()
					{
						
						@Override
						public void run()
						{
							if (player.isOnline() && !player.isInArenaEvent() && !player.isArenaAttack())
							{
								if (player.isArena1x1())
									Arena1x1.getInstance().remove(player);
								if (player.isArena2x2())
									Arena2x2.getInstance().remove(player);
								if (player.isArena5x5())
									Arena5x5.getInstance().remove(player);
								if (player.isArena9x9())
									Arena9x9.getInstance().remove(player);
								
								player.setArenaProtection(false);
							}
						}
					}, 25000);
				}
				
				ArenaEvent.getInstance().getNextTime();
				
			}
		}
		
	}
	
	public static void spawnNpc1()
	{
		NpcTemplate tmpl = NpcTable.getInstance().getTemplate(Config.ARENA_NPC);
		
		try
		{
			_npcSpawn1 = new L2Spawn(tmpl);
			_npcSpawn1.setLoc(loc1x(), loc1y(), loc1z(), Config.NPC_Heading);
			_npcSpawn1.setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn1, false);
			
			_npcSpawn1.setRespawnState(true);
			_npcSpawn1.doSpawn(false);
			_npcSpawn1.getNpc().getStatus().setCurrentHp(999999999);
			_npcSpawn1.getNpc().isAggressive();
			_npcSpawn1.getNpc().decayMe();
			_npcSpawn1.getNpc().spawnMe(_npcSpawn1.getNpc().getX(), _npcSpawn1.getNpc().getY(), _npcSpawn1.getNpc().getZ());
			_npcSpawn1.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn1.getNpc(), _npcSpawn1.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void spawnNpc2()
	{
		NpcTemplate tmpl = NpcTable.getInstance().getTemplate(Config.ARENA_NPC);
		
		try
		{
			_npcSpawn2 = new L2Spawn(tmpl);
			_npcSpawn2.setLoc(loc2x(), loc2y(), loc2z(), Config.NPC_Heading2);
			_npcSpawn2.setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn2, false);
			
			_npcSpawn2.setRespawnState(true);
			_npcSpawn2.doSpawn(false);
			_npcSpawn2.getNpc().getStatus().setCurrentHp(999999999);
			_npcSpawn2.getNpc().isAggressive();
			_npcSpawn2.getNpc().decayMe();
			_npcSpawn2.getNpc().spawnMe(_npcSpawn2.getNpc().getX(), _npcSpawn2.getNpc().getY(), _npcSpawn2.getNpc().getZ());
			_npcSpawn2.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn2.getNpc(), _npcSpawn2.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	public static void unspawnNpc1()
	{
		if (_npcSpawn1 == null)
			return;
		
		_npcSpawn1.getNpc().deleteMe();
		_npcSpawn1.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn1, true);
	}
	
	public static void unspawnNpc2()
	{
		if (_npcSpawn2 == null)
			return;
		
		_npcSpawn2.getNpc().deleteMe();
		_npcSpawn2.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn2, true);
	}
	
	public static int loc1x()
	{
		int loc1x = Config.NPC_locx;
		return loc1x;
	}
	
	public static int loc1y()
	{
		int loc1y = Config.NPC_locy;
		return loc1y;
	}
	
	public static int loc1z()
	{
		int loc1z = Config.NPC_locz;
		return loc1z;
	}
	
	public static int loc2x()
	{
		int loc2x = Config.NPC_locx2;
		return loc2x;
	}
	
	public static int loc2y()
	{
		int loc2y = Config.NPC_locy2;
		return loc2y;
	}
	
	public static int loc2z()
	{
		int loc2z = Config.NPC_locz2;
		return loc2z;
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					
					if (_started)
					{
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Party Event PvP");
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Teleport in the GK to (Tour) Zone");
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" Reward: " + ItemTable.getInstance().getTemplate(Config.ARENA_REWARD_ID).getName());
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" " + seconds / 60 / 60 + " hour(s) till event finish!");
					}
					break;
				case 1800: // 30 minutes left
				case 900: // 15 minutes left
				case 600: // 10 minutes left
				case 300: // 5 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					// removeOfflinePlayers();
					
					if (_started)
					{
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" " + seconds / 60 + " minute(s) till event finish!");
					}
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
				case 10: // 10 seconds left
				case 3: // 3 seconds left
				case 2: // 2 seconds left
				case 1: // 1 seconds left
					if (_started)
						Announcement.AnnounceEvents(""+Config.NAME_TOUR+" " + seconds + " second(s) till event finish!");
					
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
}