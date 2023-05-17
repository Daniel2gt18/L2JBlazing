package com.l2jmega.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

public class Arena5x5 implements Runnable
{
	// list of participants
	public static List<Pair> registered;
	// number of Arenas
	int free = Config.ARENA_EVENT_COUNT_5X5;
	// Arenas
	Arena[] arenas = new Arena[Config.ARENA_EVENT_COUNT_5X5];
	// list of fights going on
	Map<Integer, String> fights = new HashMap<>(Config.ARENA_EVENT_COUNT_5X5);
	
	public Arena5x5()
	{
		registered = new ArrayList<>();
		int[] coord;
		for (int i = 0; i < Config.ARENA_EVENT_COUNT_5X5; i++)
		{
			coord = Config.ARENA_EVENT_LOCS_5X5[i];
			arenas[i] = new Arena(i, coord[0], coord[1], coord[2]);
		}
	}
	
	public static Arena5x5 getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public boolean register(Player player, Player assist, Player assist2, Player assist3, Player assist4)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player)
			{
				player.sendMessage("Tournament: You already registered!");
				return false;
			}
			else if (p.getLeader() == assist || p.getAssist() == assist)
			{
				player.sendMessage("Tournament: " + assist.getName() + " already registered!");
				return false;
			}
			else if (p.getLeader() == assist2 || p.getAssist2() == assist2)
			{
				player.sendMessage("Tournament: " + assist2.getName() + " already registered!");
				return false;
			}
			else if (p.getLeader() == assist3 || p.getAssist3() == assist3)
			{
				player.sendMessage("Tournament: " + assist3.getName() + " already registered!");
				return false;
			}
			else if (p.getLeader() == assist4 || p.getAssist4() == assist4)
			{
				player.sendMessage("Tournament: " + assist4.getName() + " already registered!");
				return false;
			}
		}
		return registered.add(new Pair(player, assist, assist2, assist3, assist4));
	}
	
	public boolean isRegistered(Player player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player || p.getAssist4() == player)
			{
				return true;
			}
		}
		return false;
	}
	
	public Map<Integer, String> getFights()
	{
		return fights;
	}
	
	public boolean remove(Player player)
	{
		for (Pair p : registered)
		{
			if (p.getLeader() == player || p.getAssist() == player || p.getAssist2() == player || p.getAssist3() == player  || p.getAssist4() == player)
			{
				p.removeMessage();
				registered.remove(p);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized void run()
	{
		boolean load = true;
		
		// while server is running
		while (load)
		{
			if (!ArenaTask.is_started())
				load = false;
			
			// if no have participants or arenas are busy wait 1 minute
			if (registered.size() < 2 || free == 0)
			{
				try
				{
					Thread.sleep(Config.ARENA_CALL_INTERVAL * 1000);
				}
				catch (InterruptedException e)
				{
				}
				continue;
			}
			List<Pair> opponents = selectOpponents();
			if (opponents != null && opponents.size() == 2)
			{
				Thread T = new Thread(new EvtArenaTask(opponents));
				T.setDaemon(true);
				T.start();
			}
			// wait 1 minute for not stress server
			try
			{
				Thread.sleep(Config.ARENA_CALL_INTERVAL * 1000);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	@SuppressWarnings("null")
	private List<Pair> selectOpponents()
	{
		List<Pair> opponents = new ArrayList<>();
		Pair pairOne = null, pairTwo = null;
		int tries = 3;
		do
		{
			int first = 0, second = 0;
			if (getRegisteredCount() < 2)
				return opponents;
			
			if (pairOne == null)
			{
				first = Rnd.get(getRegisteredCount());
				pairOne = registered.get(first);
				if (pairOne.check())
				{
					opponents.add(0, pairOne);
					registered.remove(first);
				}
				else
				{
					pairOne = null;
					registered.remove(first);
					return null;
				}
				
			}
			if (pairTwo == null)
			{
				second = Rnd.get(getRegisteredCount());
				pairTwo = registered.get(second);
				if (pairTwo.check())
				{
					opponents.add(1, pairTwo);
					registered.remove(second);
				}
				else
				{
					pairTwo = null;
					registered.remove(second);
					return null;
				}
				
			}
		}
		while ((pairOne == null || pairTwo == null) && --tries > 0);
		return opponents;
	}
	
	public void clear()
	{
		registered.clear();
	}
	
	public int getRegisteredCount()
	{
		return registered.size();
	}
	
	private class Pair
	{
		Player leader;
		Player assist;
		Player assist2;
		Player assist3;
		Player assist4;
		
		public Pair(Player leader, Player assist, Player assist2, Player assist3, Player assist4)
		{
			this.leader = leader;
			this.assist = assist;
			this.assist2 = assist2;
			this.assist3 = assist3;
			this.assist4 = assist4;
		}
		
		public Player getAssist()
		{
			return assist;
		}
		
		public Player getAssist2()
		{
			return assist2;
		}
		
		public Player getAssist3()
		{
			return assist3;
		}
		
		public Player getAssist4()
		{
			return assist4;
		}
		
		public Player getLeader()
		{
			return leader;
		}
		
		public boolean check()
		{
			if ((leader == null || !leader.isOnline()))
			{
				if (assist != null || assist.isOnline())
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist2 != null || assist2.isOnline())
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist3 != null || assist3.isOnline())
					assist3.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist4 != null || assist4.isOnline())
					assist4.sendMessage("Tournament: You participation in Event was Canceled.");
				
				return false;
			}
			else if (((assist == null || !assist.isOnline()) || (assist2 == null || !assist2.isOnline()) || (assist3 == null || !assist3.isOnline()) || (assist4 == null || !assist4.isOnline())) && (leader != null && leader.isOnline()))
			{
				leader.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist != null || assist.isOnline())
					assist.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist2 != null || assist2.isOnline())
					assist2.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist3 != null || assist3.isOnline())
					assist3.sendMessage("Tournament: You participation in Event was Canceled.");
				
				if (assist4 != null || assist4.isOnline())
					assist4.sendMessage("Tournament: You participation in Event was Canceled.");
				
				return false;
			}
			return true;
		}
		
		public boolean isDead()
		{
			if (Config.ARENA_PROTECT)
			{
				if (leader != null && leader.isOnline() && leader.isArenaAttack() && !leader.isDead() && !leader.isInsideZone(ZoneId.ARENA_EVENT))
					leader.logout();
				
				if (assist != null && assist.isOnline() && assist.isArenaAttack() && !assist.isDead() && !assist.isInsideZone(ZoneId.ARENA_EVENT))
					assist.logout();
				
				if (assist2 != null && assist2.isOnline() && assist2.isArenaAttack() && !assist2.isDead() && !assist2.isInsideZone(ZoneId.ARENA_EVENT))
					assist2.logout();
				
				if (assist3 != null && assist3.isOnline() && assist3.isArenaAttack() && !assist3.isDead() && !assist3.isInsideZone(ZoneId.ARENA_EVENT))
					assist3.logout();
				
				if (assist4 != null && assist4.isOnline() && assist4.isArenaAttack() && !assist4.isDead() && !assist4.isInsideZone(ZoneId.ARENA_EVENT))
					assist4.logout();
			}
			
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()) && (assist4 == null || assist4.isDead() || !assist4.isOnline() || !assist4.isInsideZone(ZoneId.ARENA_EVENT) || !assist4.isArenaAttack()))
				return false;
			
			return !(leader.isDead() && assist.isDead() && assist2.isDead() && assist3.isDead() && assist4.isDead());
		}
		
		public boolean isAlive()
		{						
			if ((leader == null || leader.isDead() || !leader.isOnline() || !leader.isInsideZone(ZoneId.ARENA_EVENT) || !leader.isArenaAttack()) && (assist == null || assist.isDead() || !assist.isOnline() || !assist.isInsideZone(ZoneId.ARENA_EVENT) || !assist.isArenaAttack()) && (assist2 == null || assist2.isDead() || !assist2.isOnline() || !assist2.isInsideZone(ZoneId.ARENA_EVENT) || !assist2.isArenaAttack()) && (assist3 == null || assist3.isDead() || !assist3.isOnline() || !assist3.isInsideZone(ZoneId.ARENA_EVENT) || !assist3.isArenaAttack()) && (assist4 == null || assist4.isDead() || !assist4.isOnline() || !assist4.isInsideZone(ZoneId.ARENA_EVENT) || !assist4.isArenaAttack()))
				return false;
			
			return !(leader.isDead() && assist.isDead() && assist2.isDead() && assist3.isDead() && assist4.isDead());
		}
		
		public void teleportTo(int x, int y, int z)
		{
			if (leader != null && leader.isOnline())
			{
				leader.getAppearance().setInvisible();
				leader.setCurrentCp(leader.getMaxCp());
				leader.setCurrentHp(leader.getMaxHp());
				leader.setCurrentMp(leader.getMaxMp());
				
				if (leader.isInObserverMode())
				{
					leader.setLastCords(x, y, z);
					leader.leaveOlympiadObserverMode();
				}
				else if (!leader.isInJail())
					leader.teleToLocation(x, y, z, 0);
				
				leader.broadcastUserInfo();
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.getAppearance().setInvisible();
				assist.setCurrentCp(assist.getMaxCp());
				assist.setCurrentHp(assist.getMaxHp());
				assist.setCurrentMp(assist.getMaxMp());
				
				if (assist.isInObserverMode())
				{
					assist.setLastCords(x, y + 50, z);
					assist.leaveOlympiadObserverMode();
				}
				else if (!assist.isInJail())
					assist.teleToLocation(x, y + 50, z, 0);
				
				assist.broadcastUserInfo();
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2.getAppearance().setInvisible();
				assist2.setCurrentCp(assist2.getMaxCp());
				assist2.setCurrentHp(assist2.getMaxHp());
				assist2.setCurrentMp(assist2.getMaxMp());
				
				if (assist2.isInObserverMode())
				{
					assist2.setLastCords(x, y - 100, z);
					assist2.leaveOlympiadObserverMode();
				}
				else if (!assist2.isInJail())
					assist2.teleToLocation(x, y - 100, z, 0);
				
				assist2.broadcastUserInfo();
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.getAppearance().setInvisible();
				assist3.setCurrentCp(assist3.getMaxCp());
				assist3.setCurrentHp(assist3.getMaxHp());
				assist3.setCurrentMp(assist3.getMaxMp());
				
				if (assist3.isInObserverMode())
				{
					assist3.setLastCords(x, y - 50, z);
					assist3.leaveOlympiadObserverMode();
				}
				else if (!assist3.isInJail())
					assist3.teleToLocation(x, y - 50, z, 0);
				
				assist3.broadcastUserInfo();
			}
			if (assist4 != null && assist4.isOnline())
			{
				assist4.getAppearance().setInvisible();
				assist4.setCurrentCp(assist4.getMaxCp());
				assist4.setCurrentHp(assist4.getMaxHp());
				assist4.setCurrentMp(assist4.getMaxMp());
				
				if (assist4.isInObserverMode())
				{
					assist4.setLastCords(x, y + 100, z);
					assist4.leaveOlympiadObserverMode();
				}
				else if (!assist4.isInJail())
					assist4.teleToLocation(x, y + 100, z, 0);
				
				assist4.broadcastUserInfo();
			}
		}
		
		public void EventTitle(String title, String color)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setTitle(title);
				leader.getAppearance().setTitleColor(Integer.decode("0x" + color));
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.setTitle(title);
				assist.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setTitle(title);
				assist2.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
				
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setTitle(title);
				assist3.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist3.broadcastUserInfo();
				assist3.broadcastTitleInfo();
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setTitle(title);
				assist4.getAppearance().setTitleColor(Integer.decode("0x" + color));
				assist4.broadcastUserInfo();
				assist4.broadcastTitleInfo();
			}
		}
		
		
		public void saveTitle()
		{
			if (leader != null && leader.isOnline())
			{
				leader._originalTitleColorTournament = leader.getAppearance().getTitleColor();
				leader._originalTitleTournament = leader.getTitle();
			}
			
			if (assist != null && assist.isOnline())
			{
				assist._originalTitleColorTournament = assist.getAppearance().getTitleColor();
				assist._originalTitleTournament = assist.getTitle();
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2._originalTitleColorTournament = assist2.getAppearance().getTitleColor();
				assist2._originalTitleTournament = assist2.getTitle();
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3._originalTitleColorTournament = assist3.getAppearance().getTitleColor();
				assist3._originalTitleTournament = assist3.getTitle();
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4._originalTitleColorTournament = assist4.getAppearance().getTitleColor();
				assist4._originalTitleTournament = assist4.getTitle();
			}
		}
		
		public void backTitle()
		{
			if (leader != null && leader.isOnline())
			{
				leader.setTitle(leader._originalTitleTournament);
				leader.getAppearance().setTitleColor(leader._originalTitleColorTournament);
				leader.broadcastUserInfo();
				leader.broadcastTitleInfo();
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.setTitle(assist._originalTitleTournament);
				assist.getAppearance().setTitleColor(assist._originalTitleColorTournament);
				assist.broadcastUserInfo();
				assist.broadcastTitleInfo();
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setTitle(assist2._originalTitleTournament);
				assist2.getAppearance().setTitleColor(assist2._originalTitleColorTournament);
				assist2.broadcastUserInfo();
				assist2.broadcastTitleInfo();
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setTitle(assist3._originalTitleTournament);
				assist3.getAppearance().setTitleColor(assist3._originalTitleColorTournament);
				assist3.broadcastUserInfo();
				assist3.broadcastTitleInfo();
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setTitle(assist4._originalTitleTournament);
				assist4.getAppearance().setTitleColor(assist4._originalTitleColorTournament);
				assist4.broadcastUserInfo();
				assist4.broadcastTitleInfo();
			}
			
		}
		
		public void rewards()
		{
			
			if (leader != null && leader.isOnline())
			{
				if (leader.isVip())
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, leader, true);
				else
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5, leader, true);
				
		        if (leader.getClan() != null && !leader.isOnline())
		        	leader.getClan().addclan5x5Score(1); 
		        
			       if (Config.ACTIVE_MISSION) {
			           if (!this.leader.check_obj_mission(this.leader.getObjectId()))
			             this.leader.updateMission(); 
			           if (!this.leader.is5x5Completed() && this.leader.getTournament5x5Cont() < Config.MISSION_5X5_CONT)
			             this.leader.setTournament5x5Cont(this.leader.getTournament5x5Cont() + 1); 
			         }
			}
			
			if (assist != null && assist.isOnline())
			{
				if (assist.isVip())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist, true);
				else
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5, assist, true);
		
		        if (assist.getClan() != null && !assist.isOnline())
		        	assist.getClan().addclan5x5Score(1); 
				
		        if (Config.ACTIVE_MISSION) {
		            if (!this.assist.check_obj_mission(this.assist.getObjectId()))
		              this.assist.updateMission(); 
		            if (!this.assist.is5x5Completed() && this.assist.getTournament5x5Cont() < Config.MISSION_5X5_CONT)
		              this.assist.setTournament5x5Cont(this.assist.getTournament5x5Cont() + 1); 
		          } 
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				if (assist2.isVip())
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist2, true);
				else
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5, assist2, true);
			
		        if (assist2.getClan() != null && !assist2.isOnline())
		        	assist2.getClan().addclan5x5Score(1); 
				
		        if (Config.ACTIVE_MISSION) {
		            if (!this.assist2.check_obj_mission(this.assist2.getObjectId()))
		              this.assist2.updateMission(); 
		            if (!this.assist2.is5x5Completed() && this.assist2.getTournament5x5Cont() < Config.MISSION_5X5_CONT)
		              this.assist2.setTournament5x5Cont(this.assist2.getTournament5x5Cont() + 1); 
		          } 
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				if (assist3.isVip())
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist3, true);
				else
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5, assist3, true);
		
		        if (assist3.getClan() != null && !assist3.isOnline())
		        	assist3.getClan().addclan5x5Score(1); 
				
		        if (Config.ACTIVE_MISSION) {
		            if (!this.assist3.check_obj_mission(this.assist3.getObjectId()))
		              this.assist3.updateMission(); 
		            if (!this.assist3.is5x5Completed() && this.assist3.getTournament5x5Cont() < Config.MISSION_5X5_CONT)
		              this.assist3.setTournament5x5Cont(this.assist3.getTournament5x5Cont() + 1); 
		          } 
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				if (assist4.isVip())
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist4, true);
				else
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_WIN_REWARD_COUNT_5X5, assist4, true);
		
		        if (assist4.getClan() != null && !assist4.isOnline())
		        	assist4.getClan().addclan5x5Score(1); 
				
		        if (Config.ACTIVE_MISSION) {
		            if (!this.assist4.check_obj_mission(this.assist4.getObjectId()))
		              this.assist4.updateMission(); 
		            if (!this.assist4.is5x5Completed() && this.assist4.getTournament5x5Cont() < Config.MISSION_5X5_CONT)
		              this.assist4.setTournament5x5Cont(this.assist4.getTournament5x5Cont() + 1); 
		          } 
			}
			
			sendPacket("Winner =)", 5);
            leader.broadcastPacket(new MagicSkillUse(leader, leader, 2024, 1, 1, 0));
            assist.broadcastPacket(new MagicSkillUse(assist, assist, 2024, 1, 1, 0));
            assist2.broadcastPacket(new MagicSkillUse(assist, assist, 2024, 1, 1, 0));
            assist3.broadcastPacket(new MagicSkillUse(assist, assist, 2024, 1, 1, 0));
            assist4.broadcastPacket(new MagicSkillUse(assist, assist, 2024, 1, 1, 0));
		}
		
		public void rewardsLost()
		{
			if (leader != null && leader.isOnline())
				if (leader.isVip())
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, leader, true);
				else
					leader.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5, leader, true);
			
			if (assist != null && assist.isOnline())
				if (assist.isVip())
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist, true);
				else
					assist.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5, assist, true);
			
			if (assist2 != null && assist2.isOnline())
				if (assist2.isVip())
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist2, true);
				else
					assist2.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5, assist2, true);
			
			if (assist3 != null && assist3.isOnline())
				if (assist3.isVip())
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist3, true);
				else
					assist3.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5, assist3, true);
			
			if (assist4 != null && assist4.isOnline())
				if (assist4.isVip())
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5 * Config.RATE_DROP_VIP, assist4, true);
				else
					assist4.addItem("Arena_Event", Config.ARENA_REWARD_ID, Config.ARENA_LOST_REWARD_COUNT_5X5, assist4, true);
	
			sendPacket("Loser =(", 5);
			
		}
		
		public void setInTournamentEvent(boolean val)
		{
			if (leader != null && leader.isOnline())
				leader.setInArenaEvent(val);
			
			if (assist != null && assist.isOnline())
				assist.setInArenaEvent(val);
			
			if (assist2 != null && assist2.isOnline())
				assist2.setInArenaEvent(val);
			
			if (assist3 != null && assist3.isOnline())
				assist3.setInArenaEvent(val);
			
			if (assist4 != null && assist4.isOnline())
				assist4.setInArenaEvent(val);
		}
		
		public void removeMessage()
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendMessage("Tournament: Your participation has been removed.");
				leader.setArenaProtection(false);
				leader.setArena5x5(false);
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.sendMessage("Tournament: Your participation has been removed.");
				assist.setArenaProtection(false);
				assist.setArena5x5(false);
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2.sendMessage("Tournament: Your participation has been removed.");
				assist2.setArenaProtection(false);
				assist2.setArena5x5(false);
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.sendMessage("Tournament: Your participation has been removed.");
				assist3.setArenaProtection(false);
				assist3.setArena5x5(false);
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.sendMessage("Tournament: Your participation has been removed.");
				assist4.setArenaProtection(false);
				assist4.setArena5x5(false);
			}
			
		}
		
		public void setArenaProtection(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaProtection(val);
				leader.setArena5x5(val);
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.setArenaProtection(val);
				assist.setArena5x5(val);
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setArenaProtection(val);
				assist2.setArena5x5(val);
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setArenaProtection(val);
				assist3.setArena5x5(val);
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setArenaProtection(val);
				assist4.setArena5x5(val);
			}
		}
		
		public void revive()
		{
			if (leader != null && leader.isOnline() && leader.isDead())
				leader.doRevive();
			
			if (assist != null && assist.isOnline() && assist.isDead())
				assist.doRevive();
			
			if (assist2 != null && assist2.isOnline() && assist2.isDead())
				assist2.doRevive();
			
			if (assist3 != null && assist3.isOnline() && assist3.isDead())
				assist3.doRevive();
			
			if (assist4 != null && assist4.isOnline() && assist4.isDead())
				assist4.doRevive();
		}
		
		public void setImobilised(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setIsInvul(val);
				leader.setStopArena(val);
			}
			if (assist != null && assist.isOnline())
			{
				assist.setIsInvul(val);
				assist.setStopArena(val);
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setIsInvul(val);
				assist2.setStopArena(val);
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setIsInvul(val);
				assist3.setStopArena(val);
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setIsInvul(val);
				assist4.setStopArena(val);
			}
		}
		
		public void setArenaAttack(boolean val)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setArenaAttack(val);
				leader.broadcastUserInfo();
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.setArenaAttack(val);
				assist.broadcastUserInfo();
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setArenaAttack(val);
				assist2.broadcastUserInfo();
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setArenaAttack(val);
				assist3.broadcastUserInfo();
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setArenaAttack(val);
				assist4.broadcastUserInfo();
			}
		}
		
		@SuppressWarnings("unused")
		public void setTeam(int x)
		{
			if (leader != null && leader.isOnline())
				leader.setTeam(x);
			
			if (assist != null && assist.isOnline())
				assist.setTeam(x);
			
			if (assist2 != null && assist2.isOnline())
				assist2.setTeam(x);
			
			if (assist3 != null && assist3.isOnline())
				assist3.setTeam(x);
			
			if (assist4 != null && assist4.isOnline())
				assist4.setTeam(x);
		}
		
		public void removePet()
		{
			if (leader != null && leader.isOnline())
			{
				// Remove Summon's buffs
				if (leader.getPet() != null)
				{
					Summon summon = leader.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof Pet)
						summon.unSummon(leader);
					
				}
				
				if (leader.getMountType() == 1 || leader.getMountType() == 2)
					leader.dismount();
			}
			if (assist != null && assist.isOnline())
			{
				// Remove Summon's buffs
				if (assist.getPet() != null)
				{
					Summon summon = assist.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof Pet)
						summon.unSummon(assist);
					
				}
				
				if (assist.getMountType() == 1 || assist.getMountType() == 2)
					assist.dismount();
				
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				// Remove Summon's buffs
				if (assist2.getPet() != null)
				{
					Summon summon = assist2.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof Pet)
						summon.unSummon(assist2);
					
				}
				
				if (assist2.getMountType() == 1 || assist2.getMountType() == 2)
					assist2.dismount();
				
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				// Remove Summon's buffs
				if (assist3.getPet() != null)
				{
					Summon summon = assist3.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof Pet)
						summon.unSummon(assist3);
				}
				
				if (assist3.getMountType() == 1 || assist3.getMountType() == 2)
					assist3.dismount();
				
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				// Remove Summon's buffs
				if (assist4.getPet() != null)
				{
					Summon summon = assist4.getPet();
					if (summon != null)
						summon.unSummon(summon.getOwner());
					
					if (summon instanceof Pet)
						summon.unSummon(assist4);
				}
				
				if (assist4.getMountType() == 1 || assist4.getMountType() == 2)
					assist4.dismount();
				
			}
			
		}
		
		public void removeSkills()
		{
			if (!(leader.getClassId() == ClassId.SHILLIEN_ELDER || leader.getClassId() == ClassId.SHILLIEN_SAINT || leader.getClassId() == ClassId.BISHOP || leader.getClassId() == ClassId.CARDINAL || leader.getClassId() == ClassId.ELVEN_ELDER || leader.getClassId() == ClassId.EVAS_SAINT))
			{
				for (L2Effect effect : leader.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						leader.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (!(assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT))
			{
				for (L2Effect effect : assist.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (!(assist2.getClassId() == ClassId.SHILLIEN_ELDER || assist2.getClassId() == ClassId.SHILLIEN_SAINT || assist2.getClassId() == ClassId.BISHOP || assist2.getClassId() == ClassId.CARDINAL || assist2.getClassId() == ClassId.ELVEN_ELDER || assist2.getClassId() == ClassId.EVAS_SAINT))
			{
				for (L2Effect effect : assist2.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist2.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (!(assist3.getClassId() == ClassId.SHILLIEN_ELDER || assist3.getClassId() == ClassId.SHILLIEN_SAINT || assist3.getClassId() == ClassId.BISHOP || assist3.getClassId() == ClassId.CARDINAL || assist3.getClassId() == ClassId.ELVEN_ELDER || assist3.getClassId() == ClassId.EVAS_SAINT))
			{
				for (L2Effect effect : assist3.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist3.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (!(assist4.getClassId() == ClassId.SHILLIEN_ELDER || assist4.getClassId() == ClassId.SHILLIEN_SAINT || assist4.getClassId() == ClassId.BISHOP || assist4.getClassId() == ClassId.CARDINAL || assist4.getClassId() == ClassId.ELVEN_ELDER || assist4.getClassId() == ClassId.EVAS_SAINT))
			{
				for (L2Effect effect : assist4.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist4.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
		}
		
		public void sendPacket(String message, int duration)
		{
			if (leader != null && leader.isOnline())
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist != null && assist.isOnline())
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist2 != null && assist2.isOnline())
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist3 != null && assist3.isOnline())
				assist3.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
			if (assist4 != null && assist4.isOnline())
				assist4.sendPacket(new ExShowScreenMessage(message, duration * 1000));
			
		}
		
		public void inicarContagem(int duration)
		{
			if (leader != null && leader.isOnline())
				ThreadPool.schedule(new countdown(leader, duration), 0);
			
			if (assist != null && assist.isOnline())
				ThreadPool.schedule(new countdown(assist, duration), 0);
			
			if (assist2 != null && assist2.isOnline())
				ThreadPool.schedule(new countdown(assist2, duration), 0);
			
			if (assist3 != null && assist3.isOnline())
				ThreadPool.schedule(new countdown(assist3, duration), 0);
			
			if (assist4 != null && assist4.isOnline())
				ThreadPool.schedule(new countdown(assist4, duration), 0);
		}
		
		public void sendPacketinit(String message, int duration)
		{
			if (leader != null && leader.isOnline())
			{
				leader.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				
				if ((leader.getClassId() == ClassId.SHILLIEN_ELDER || leader.getClassId() == ClassId.SHILLIEN_SAINT || leader.getClassId() == ClassId.BISHOP || leader.getClassId() == ClassId.CARDINAL || leader.getClassId() == ClassId.ELVEN_ELDER || leader.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_5X5 == 0)
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							leader.getClient().closeNow();
						}
					}, 100);
				}
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				
				if ((assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_5X5 == 0)
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							assist.getClient().closeNow();
						}
					}, 100);
				}
				
			}
			
			if (assist2 != null && assist2.isOnline())
			{
				assist2.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				if ((assist2.getClassId() == ClassId.SHILLIEN_ELDER || assist2.getClassId() == ClassId.SHILLIEN_SAINT || assist2.getClassId() == ClassId.BISHOP || assist2.getClassId() == ClassId.CARDINAL || assist2.getClassId() == ClassId.ELVEN_ELDER || assist2.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_5X5 == 0)
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							assist2.getClient().closeNow();
						}
					}, 100);
				}
			}
			
			if (assist3 != null && assist3.isOnline())
			{
				assist3.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				if ((assist3.getClassId() == ClassId.SHILLIEN_ELDER || assist3.getClassId() == ClassId.SHILLIEN_SAINT || assist3.getClassId() == ClassId.BISHOP || assist3.getClassId() == ClassId.CARDINAL || assist3.getClassId() == ClassId.ELVEN_ELDER || assist3.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_5X5 == 0)
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							assist3.getClient().closeNow();
						}
					}, 100);
				}
			}
			
			if (assist4 != null && assist4.isOnline())
			{
				assist4.sendPacket(new ExShowScreenMessage(message, duration * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				if ((assist4.getClassId() == ClassId.SHILLIEN_ELDER || assist4.getClassId() == ClassId.SHILLIEN_SAINT || assist4.getClassId() == ClassId.BISHOP || assist4.getClassId() == ClassId.CARDINAL || assist4.getClassId() == ClassId.ELVEN_ELDER || assist4.getClassId() == ClassId.EVAS_SAINT) && Config.bs_COUNT_5X5 == 0)
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							assist4.getClient().closeNow();
						}
					}, 100);
				}
			}
		}

		public void TourAura(int color)
		{
			if (leader != null && leader.isOnline())
			{
				leader.setTeamTour(color);
			}
			
			if (assist != null && assist.isOnline())
			{
				assist.setTeamTour(color);
			}
			if (assist2 != null && assist2.isOnline())
			{
				assist2.setTeamTour(color);
			}
			if (assist3 != null && assist3.isOnline())
			{
				assist3.setTeamTour(color);
			}
			if (assist4 != null && assist4.isOnline())
			{
				assist4.setTeamTour(color);
			}
		}
		
	}
	
	private class EvtArenaTask implements Runnable
	{
		private final Pair pairOne;
		private final Pair pairTwo;
		private final int pOneX, pOneY, pOneZ, pTwoX, pTwoY, pTwoZ;
		private Arena arena;
		
		public EvtArenaTask(List<Pair> opponents)
		{
			pairOne = opponents.get(0);
			pairTwo = opponents.get(1);
			Player leader = pairOne.getLeader();
			pOneX = leader.getX();
			pOneY = leader.getY();
			pOneZ = leader.getZ();
			leader = pairTwo.getLeader();
			pTwoX = leader.getX();
			pTwoY = leader.getY();
			pTwoZ = leader.getZ();
		}
		
		@Override
		public void run()
		{
			free--;
			pairOne.saveTitle();
			pairTwo.saveTitle();
			pairOne.TourAura(Config.TOUR_AURA_TEAM1);
			pairTwo.TourAura(Config.TOUR_AURA_TEAM2);
			portPairsToArena();
			pairOne.inicarContagem(Config.ARENA_WAIT_INTERVAL_5X5);
			pairTwo.inicarContagem(Config.ARENA_WAIT_INTERVAL_5X5);
			try
			{
				Thread.sleep(Config.ARENA_WAIT_INTERVAL_5X5 * 1000);
			}
			catch (InterruptedException e1)
			{
			}
			pairOne.sendPacketinit("Started. Good Fight!", 3);
			pairTwo.sendPacketinit("Started. Good Fight!", 3);
			pairOne.EventTitle(Config.MSG_TEAM1, Config.TITLE_COLOR_TEAM1);
			pairTwo.EventTitle(Config.MSG_TEAM2, Config.TITLE_COLOR_TEAM2);
			pairOne.setImobilised(false);
			pairTwo.setImobilised(false);
			pairOne.setArenaAttack(true);
			pairTwo.setArenaAttack(true);
			
			while (check())
			{
				// check players status each seconds
				try
				{
					Thread.sleep(Config.ARENA_CHECK_INTERVAL);
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
			finishDuel();
			free++;
		}
		
		private void finishDuel()
		{
			fights.remove(arena.id);
			rewardWinner();
			pairOne.revive();
			pairTwo.revive();
			pairOne.teleportTo(pOneX, pOneY, pOneZ);
			pairTwo.teleportTo(pTwoX, pTwoY, pTwoZ);
			pairOne.TourAura(0);
			pairTwo.TourAura(0);
			pairOne.backTitle();
			pairTwo.backTitle();
			pairOne.setInTournamentEvent(false);
			pairTwo.setInTournamentEvent(false);
			pairOne.setArenaProtection(false);
			pairTwo.setArenaProtection(false);
			pairOne.setArenaAttack(false);
			pairTwo.setArenaAttack(false);
			arena.setFree(true);
		}
		
		private void rewardWinner()
		{
			if (pairOne.isAlive() && !pairTwo.isAlive())
			{
				Player leader1 = pairOne.getLeader();
				Player leader2 = pairTwo.getLeader();
				
				if (leader1.getClan() != null && leader2.getClan() != null && Config.TOURNAMENT_EVENT_ANNOUNCE)
				{
					final CreatureSay a = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "(5X5)", "(" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!"); // 8D
					final CreatureSay b = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "(5X5)", "(" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!"); // 8D
					for (final Player player : World.getInstance().getPlayers())
					{
						if (player != null && player.isOnline())
						{
							if (Config.PVP_COLOR_ANNOUNCE == 18 || Config.PVP_COLOR_ANNOUNCE == 10)
								player.sendPacket(a);
							else
								player.sendPacket(b);
						}
					}
				}
				pairOne.rewards();
				pairTwo.rewardsLost();
			}
			else if (pairTwo.isAlive() && !pairOne.isAlive())
			{
				Player leader1 = pairTwo.getLeader();
				Player leader2 = pairOne.getLeader();
				
				if (leader1.getClan() != null && leader2.getClan() != null && Config.TOURNAMENT_EVENT_ANNOUNCE)
				{
					final CreatureSay a = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "(5X5)", "(" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!"); // 8D
					final CreatureSay b = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "(5X5)", "(" + leader1.getClan().getName() + " VS " + leader2.getClan().getName() + ") ~> " + leader1.getClan().getName() + " win!"); // 8D
					for (final Player player : World.getInstance().getPlayers())
					{
						if (player != null && player.isOnline())
						{
							if (Config.PVP_COLOR_ANNOUNCE == 18 || Config.PVP_COLOR_ANNOUNCE == 10)
								player.sendPacket(a);
							else
								player.sendPacket(b);
						}
					}
				}
				pairTwo.rewards();
				pairOne.rewardsLost();
			}
		}
		
		private boolean check()
		{
			return (pairOne.isDead() && pairTwo.isDead());
		}
		
		private void portPairsToArena()
		{
			for (Arena arena : arenas)
			{
				if (arena.isFree)
				{
					this.arena = arena;
					arena.setFree(false);
					pairOne.removePet();
					pairTwo.removePet();
					pairOne.teleportTo(arena.x - 850, arena.y, arena.z);
					pairTwo.teleportTo(arena.x + 850, arena.y, arena.z);
					pairOne.setImobilised(true);
					pairTwo.setImobilised(true);
					pairOne.setInTournamentEvent(true);
					pairTwo.setInTournamentEvent(true);
					pairOne.removeSkills();
					pairTwo.removeSkills();
					fights.put(this.arena.id, pairOne.getLeader().getName() + " vs " + pairTwo.getLeader().getName());
					break;
				}
			}
		}
	}
	
	private class Arena
	{
		protected int x, y, z;
		protected boolean isFree = true;
		int id;
		
		public Arena(int id, int x, int y, int z)
		{
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public void setFree(boolean val)
		{
			isFree = val;
		}
	}
	
	protected class countdown implements Runnable
	{
		private final Player _player;
		private int _time;
		
		public countdown(Player player, int time)
		{
			_time = time;
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isOnline())
			{
				
				switch (_time)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 57:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 60 second(s)..", 4 * 1000));
							_player.sendMessage("60 second(s) to start the battle.");
						}
						break;
					case 45:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 45 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 27:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 30 second(s)..", 4 * 1000));
							_player.sendMessage("30 second(s) to start the battle.");
						}
						break;
					case 20:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 20 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 15:
						if (_player.isOnline())
						{
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 15 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						}
						break;
					case 10:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 10 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 5:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 5 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 4:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 4 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 3:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 3 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 2:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 2 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
					case 1:
						if (_player.isOnline())
							_player.sendPacket(new ExShowScreenMessage("The battle starts in 1 second(s)..", 4 * 1000));
							_player.sendMessage(_time + " second(s) to start the battle!");
						break;
				}
				if (_time > 1)
				{
					ThreadPool.schedule(new countdown(_player, _time - 1), 1000);
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final Arena5x5 INSTANCE = new Arena5x5();
	}
	
}