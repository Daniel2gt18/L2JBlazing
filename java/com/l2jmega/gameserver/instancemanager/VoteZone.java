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
package com.l2jmega.gameserver.instancemanager;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.NewZoneVote;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GameClient.GameClientState;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;
import com.l2jmega.util.CloseUtil;
import com.l2jmega.util.Rnd;

import phantom.Phantom_Attack;

/**
 * @author Computador
 */
public class VoteZone
{
	protected static final Logger _log = Logger.getLogger(VoteZone.class.getName());
	
	public static boolean startzone_1 = false;
	public static boolean startzone_2 = false;
	public static boolean startzone_3 = false;
	public static boolean startzone_4 = false;
	public static boolean startzone_5 = false;
	public static boolean startzone_6 = false;
	public static boolean startzone_7 = false;
	public static boolean startzone_8 = false;
	
	public static boolean empate = false;
	
	public static boolean zone_1 = false;
	public static int _zone_1_locsCount = 0;
	public static ArrayList<Location> _zone_1_Loc = new ArrayList<>();
	
	public static boolean zone_2 = false;
	public static int _zone_2_locsCount = 0;
	public static ArrayList<Location> _zone_2_Loc = new ArrayList<>();
	
	public static boolean zone_3 = false;
	public static int _zone_3_locsCount = 0;
	public static ArrayList<Location> _zone_3_Loc = new ArrayList<>();
	
	public static boolean zone_4 = false;
	public static int _zone_4_locsCount = 0;
	public static ArrayList<Location> _zone_4_Loc = new ArrayList<>();
	
	public static boolean zone_5 = false;
	public static int _zone_5_locsCount = 0;
	public static ArrayList<Location> _zone_5_Loc = new ArrayList<>();
	
	public static boolean zone_6 = false;
	public static int _zone_6_locsCount = 0;
	public static ArrayList<Location> _zone_6_Loc = new ArrayList<>();
	
	public static boolean zone_7 = false;
	public static int _zone_7_locsCount = 0;
	public static ArrayList<Location> _zone_7_Loc = new ArrayList<>();
	
	public static boolean zone_8 = false;
	public static int _zone_8_locsCount = 0;
	public static ArrayList<Location> _zone_8_Loc = new ArrayList<>();
	
	public static boolean selected_zone_1 = false;	  
	public static boolean selected_zone_2 = false;	  
	public static boolean selected_zone_3 = false;	  
	public static boolean selected_zone_4 = false;
	public static boolean selected_zone_5 = false;	  
	public static boolean selected_zone_6 = false;	  
	public static boolean selected_zone_7 = false;	
	
	private static VoteZone _instance;
	
	public static VoteZone getInstance()
	{
		if (_instance == null)
		{
			_instance = new VoteZone();
		}
		return _instance;
	}
	
	public VoteZone()
	{
		_log.info("PvpZone: Loading zones...");
		getInitRandomZone();
		LocsZone1();
		LocsZone2();
		LocsZone3();
		LocsZone4();
		LocsZone5();
		LocsZone6();
		LocsZone7();
	}
	
	  public static void SelectZone()
	  {
	    startzone_1 = false;
	    startzone_2 = false;
	    startzone_3 = false;
	    startzone_4 = false;
	    startzone_5 = false;
	    startzone_6 = false;
	    startzone_7 = false;
	    if ((VoteZoneCommands.getVote_1() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_2() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_3() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_4() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_5() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_6() >= Config.VOTE_PVPZONE_MIN_VOTE) || (VoteZoneCommands.getVote_7() >= Config.VOTE_PVPZONE_MIN_VOTE))
	    {
	      getRandomZone();
	     if (empate)
	      {
	        empate = false;
	        for (Player player : World.getInstance().getPlayers())
	        {
	          if (!player.isInObserverMode()) {
	            VoteZoneCommands.ShowResult_init(player);
	          }
	          player.setVotePlayer(false);
	          
	          CreatureSay cs = new CreatureSay(0, 2, "VoteZone", "The vote ended with a tie..");
	          player.sendPacket(cs);
	        }
	        ThreadPool.schedule(new Runnable()
	        {
	          @Override
			public void run()
	          {
	            VoteZoneCommands.cleanVote_1(0);
	            VoteZoneCommands.cleanVote_2(0);
	            VoteZoneCommands.cleanVote_3(0);
	            VoteZoneCommands.cleanVote_4(0);
	            VoteZoneCommands.cleanVote_5(0);
	            VoteZoneCommands.cleanVote_6(0);
	            VoteZoneCommands.cleanVote_7(0);
	          }
	        }, 500L);
	        
	        ThreadPool.schedule(new Runnable()
	        {
	          @SuppressWarnings("resource")
			@Override
			public void run()
	          {
	            Connection con = null;
	            try
	            {
	              con = L2DatabaseFactory.getInstance().getConnection();
	              PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE votes > 0");
	              stmt.setInt(1, 0);
	              stmt.execute();
	              stmt.close();
	              stmt = null;
	            }
	            catch (Exception e)
	            {
	              VoteZone._log.log(Level.SEVERE, "[Clean Votes]:  ", e);
	            }
	            finally
	            {
	              CloseUtil.close(con);
	            }
	            if (((VoteZone.selected_zone_1()) || (!Config.ENABLED_PVPZONE_KETRA)) && ((VoteZone.selected_zone_2()) || (!Config.ENABLED_PVPZONE_ILHA)) && ((VoteZone.selected_zone_3()) || (!Config.ENABLED_PVPZONE_HEINE)) && ((VoteZone.selected_zone_4()) || (!Config.ENABLED_PVPZONE_IRISLAKE)) && ((VoteZone.selected_zone_5()) || (!Config.ENABLED_PVPZONE_ALLIGATOR)) && ((VoteZone.selected_zone_6()) || (!Config.ENABLED_PVPZONE_IMPERIAL)) && ((VoteZone.selected_zone_7()) || (!Config.ENABLED_PVPZONE_WHISPERS))) {
	              try
	              {
	                con = L2DatabaseFactory.getInstance().getConnection();
	                PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE active_zone > 0");
	                stmt.setInt(1, 0);
	                stmt.execute();
	                stmt.close();
	                stmt = null;
	              }
	              catch (Exception e)
	              {
	                VoteZone._log.log(Level.SEVERE, "[active_zone]:  ", e);
	              }
	              finally
	              {
	                CloseUtil.close(con);
	              }
	            }
	          }
	        }, 1000L);
	        
	        ThreadPool.schedule(new Runnable()
	        {
	          @Override
			public void run()
	          {
	            Announcement.AnnounceEvents("VoteZone: Vote for new PvpZone!");
	            Announcement.AnnounceEvents("VoteZone: Voting ends in 3 minute(s)!");
	            Announcement.AnnounceEvents("VoteZone: Command .votezone");
	            VoteZone.StartVoteZone();
	          }
	        }, 3000L);
	        
	        return;
	      }
	      for (Player player : World.getInstance().getPlayers())
	      {
	        player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	        player.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        if ((!player.isInsideZone(ZoneId.CUSTOM)) || (!player.isVotePlayer()))
	        {
	          if ((player.isVotePlayer()) && (!player._inEventTvT) && (!player.isAio()) && (!player.isAioEterno())) {
	            if ((player.isInArenaEvent()) || 
	              (player.isInStoreMode()) || 
	              (player.getKarma() > 0) || 
	              (player.isInOlympiadMode()) || 
	              (player.isInObserverMode()) || 
	              (player.isPhantom()) || 
	              (player.isOlympiadProtection()) || 
	              (player.isFestivalParticipant()) || 
	              ((player.isInsideZone(ZoneId.FLAG)) && (!player.isInsideZone(ZoneId.CUSTOM))) || 
	              (player.isInsideZone(ZoneId.RAID)) || 
	              (player.isInsideZone(ZoneId.RAID_NO_FLAG)) || 
	              (player.isInsideZone(ZoneId.SIEGE)) || 
	              (player.getClassId() == ClassId.BISHOP) || (player.getClassId() == ClassId.CARDINAL) || (player.getClassId() == ClassId.SHILLIEN_ELDER) || (player.getClassId() == ClassId.SHILLIEN_SAINT)) {}
	          }
	        }
	        else
	        {
	          Location zone1 = player.getRandomLoc1();
	          Location zone2 = player.getRandomLoc2();
	          Location zone3 = player.getRandomLoc3();
	          Location zone4 = player.getRandomLoc4();
	          Location zone5 = player.getRandomLoc5();
	          Location zone6 = player.getRandomLoc6(); 
	          Location zone7 = player.getRandomLoc7(); 
	          
				for (Player bot : World.getInstance().getPlayers())
				{
					if (bot.isPhantom() && !bot.isFarmArchMage() && !bot.isFarmMysticMuse() && !bot.isFarmStormScream() && bot.isInsideZone(ZoneId.CUSTOM))
					{
						if (bot.isPhantomArchMage() || bot.isPhantomMysticMuse() || bot.isPhantomStormScream())
						Phantom_Attack.removePhantom(bot);
						
					final L2GameClient client = bot.getClient();
					// detach the client from the char so that the connection isnt closed in the deleteMe
					bot.setClient(null);
					// removing player from the world
					bot.deleteMe();
					client.setActiveChar(null);
					client.setState(GameClientState.AUTHED);
					}
				}
				
	          if (is_zone_1())
	          {
	            int getX = zone1.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone1.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone1.getZ(), 20);
	          }
	          else if (is_zone_2())
	          {
	            int getX = zone2.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone2.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone2.getZ(), 20);
	          }
	          else if (is_zone_3())
	          {
	            int getX = zone3.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone3.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone3.getZ(), 20);
	          }
	          else if (is_zone_4())
	          {
	            int getX = zone4.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone4.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone4.getZ(), 20);
	          }
	          else if (is_zone_5())
	          {
	            int getX = zone5.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone5.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone5.getZ(), 20);
	          }
	          else if (is_zone_6())
	          {
	            int getX = zone6.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone6.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone6.getZ(), 20);
	          }  
	          else if (is_zone_7())
	          {
	            int getX = zone7.getX() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);int getY = zone7.getY() + Rnd.get(-Config.VOTEZONE_RANDOM_RANGE, Config.VOTEZONE_RANDOM_RANGE);
	            player.teleToLocation(getX, getY, zone7.getZ(), 20);
	          } 
				
	          ThreadPool.schedule(new Runnable()
	          {
	            @Override
				public void run()
	            {
	              PvpFlagTaskManager.getInstance().add(player, System.currentTimeMillis() + 40000L);
	              player.broadcastUserInfo();
	            }
	          }, 3000L);
	        }
	        
	        CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "VoteZone", "The pvp area was changed.");
	        player.sendPacket(cs);
	        CreatureSay cs1 = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "VoteZone", "Next VoteZone: " + NewZoneVote.getInstance().getNextTime() + " GMT-3 .");
	        player.sendPacket(cs1);
	        if (!player.isInObserverMode()) {
	          VoteZoneCommands.ShowResult(player);
	        }
	        player.setVotePlayer(false);
	        
	        ThreadPool.schedule(new Runnable()
	        {
	          @Override
			public void run()
	          {
	            VoteZoneCommands.cleanVote_1(0);
	            VoteZoneCommands.cleanVote_2(0);
	            VoteZoneCommands.cleanVote_3(0);
	            VoteZoneCommands.cleanVote_4(0);
	            VoteZoneCommands.cleanVote_5(0);
	            VoteZoneCommands.cleanVote_6(0);
	            VoteZoneCommands.cleanVote_7(0);
	            if (((VoteZone.selected_zone_1()) || (!Config.ENABLED_PVPZONE_KETRA)) && ((VoteZone.selected_zone_2()) || (!Config.ENABLED_PVPZONE_ILHA)) && ((VoteZone.selected_zone_3()) || (!Config.ENABLED_PVPZONE_HEINE)) && ((VoteZone.selected_zone_4()) || (!Config.ENABLED_PVPZONE_IRISLAKE)) && ((VoteZone.selected_zone_5()) || (!Config.ENABLED_PVPZONE_ALLIGATOR)) && ((VoteZone.selected_zone_6()) || (!Config.ENABLED_PVPZONE_IMPERIAL)) && ((VoteZone.selected_zone_7()) || (!Config.ENABLED_PVPZONE_WHISPERS)))
	            {
	              VoteZone.selected_zone_1 = false;
	              VoteZone.selected_zone_2 = false;
	              VoteZone.selected_zone_3 = false;
	              VoteZone.selected_zone_4 = false;
	              VoteZone.selected_zone_5 = false;
	              VoteZone.selected_zone_6 = false;
	              VoteZone.selected_zone_7 = false;
	            }
	          }
	        }, 2000L);
	      }
			if(Config.ALLOW_PHANTOM_PLAYERS_MASS_PVP)
			{
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_Attack.init();
					_log.info("[Phantom Mass PvP]: Starded!");
				}
				
			}, 500);
			}
	      ThreadPool.schedule(new Runnable()
	      {
	        @SuppressWarnings("resource")
			@Override
			public void run()
	        {
	          Connection con = null;
	          try
	          {
	            con = L2DatabaseFactory.getInstance().getConnection();
	            PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE votes > 0");
	            stmt.setInt(1, 0);
	            stmt.execute();
	            stmt.close();
	            stmt = null;
	          }
	          catch (Exception e)
	          {
	            VoteZone._log.log(Level.SEVERE, "[Clean Votes]:  ", e);
	          }
	          finally
	          {
	            CloseUtil.close(con);
	          }
	          if (((VoteZone.selected_zone_1()) || (!Config.ENABLED_PVPZONE_KETRA)) && ((VoteZone.selected_zone_2()) || (!Config.ENABLED_PVPZONE_ILHA)) && ((VoteZone.selected_zone_3()) || (!Config.ENABLED_PVPZONE_HEINE)) && ((VoteZone.selected_zone_4()) || (!Config.ENABLED_PVPZONE_IRISLAKE)) && ((VoteZone.selected_zone_5()) || (!Config.ENABLED_PVPZONE_ALLIGATOR)) && ((VoteZone.selected_zone_6()) || (!Config.ENABLED_PVPZONE_IMPERIAL)) && ((VoteZone.selected_zone_7()) || (!Config.ENABLED_PVPZONE_WHISPERS))) {
	            try
	            {
	              con = L2DatabaseFactory.getInstance().getConnection();
	              PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE active_zone > 0");
	              stmt.setInt(1, 0);
	              stmt.execute();
	              stmt.close();
	              stmt = null;
	            }
	            catch (Exception e)
	            {
	              VoteZone._log.log(Level.SEVERE, "[active_zone]:  ", e);
	            }
	            finally
	            {
	              CloseUtil.close(con);
	            }
	          }
	        }
	      }, 1000L);
	    }
	    else
	    {
	      Announcement.AnnounceEvents("Not enough votes for modify the PvpZone! Min Requested : " + Config.VOTE_PVPZONE_MIN_VOTE);
	      ThreadPool.schedule(new Runnable()
	      {
	        @Override
			public void run()
	        {
	          VoteZoneCommands.cleanVote_1(0);
	          VoteZoneCommands.cleanVote_2(0);
	          VoteZoneCommands.cleanVote_3(0);
	          VoteZoneCommands.cleanVote_4(0);
	          VoteZoneCommands.cleanVote_5(0);
	          VoteZoneCommands.cleanVote_6(0);
	          VoteZoneCommands.cleanVote_7(0);
	          if (((VoteZone.selected_zone_1()) || (!Config.ENABLED_PVPZONE_KETRA)) && ((VoteZone.selected_zone_2()) || (!Config.ENABLED_PVPZONE_ILHA)) && ((VoteZone.selected_zone_3()) || (!Config.ENABLED_PVPZONE_HEINE)) && ((VoteZone.selected_zone_4()) || (!Config.ENABLED_PVPZONE_IRISLAKE)) && ((VoteZone.selected_zone_5()) || (!Config.ENABLED_PVPZONE_ALLIGATOR)) && ((VoteZone.selected_zone_6()) || (!Config.ENABLED_PVPZONE_IMPERIAL)) && ((VoteZone.selected_zone_7()) || (!Config.ENABLED_PVPZONE_WHISPERS)))
	          {
	            VoteZone.selected_zone_1 = false;
	            VoteZone.selected_zone_2 = false;
	            VoteZone.selected_zone_3 = false;
	            VoteZone.selected_zone_4 = false;
	            VoteZone.selected_zone_5 = false;
	            VoteZone.selected_zone_6 = false;
	            VoteZone.selected_zone_7 = false;
	          }
	        }
	      }, 2000L);
	      
	      ThreadPool.schedule(new Runnable()
	      {
	        @SuppressWarnings("resource")
			@Override
			public void run()
	        {
	          Connection con = null;
	          try
	          {
	            con = L2DatabaseFactory.getInstance().getConnection();
	            PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE votes > 0");
	            stmt.setInt(1, 0);
	            stmt.execute();
	            stmt.close();
	            stmt = null;
	          }
	          catch (Exception e)
	          {
	            VoteZone._log.log(Level.SEVERE, "[Clean Votes]:  ", e);
	          }
	          finally
	          {
	            CloseUtil.close(con);
	          }
	          if (((VoteZone.selected_zone_1()) || (!Config.ENABLED_PVPZONE_KETRA)) && ((VoteZone.selected_zone_2()) || (!Config.ENABLED_PVPZONE_ILHA)) && ((VoteZone.selected_zone_3()) || (!Config.ENABLED_PVPZONE_HEINE)) && ((VoteZone.selected_zone_4()) || (!Config.ENABLED_PVPZONE_IRISLAKE)) && ((VoteZone.selected_zone_5()) || (!Config.ENABLED_PVPZONE_ALLIGATOR)) && ((VoteZone.selected_zone_6()) || (!Config.ENABLED_PVPZONE_IMPERIAL)) && ((VoteZone.selected_zone_7()) || (!Config.ENABLED_PVPZONE_WHISPERS))) {
	            try
	            {
	              con = L2DatabaseFactory.getInstance().getConnection();
	              PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE active_zone > 0");
	              stmt.setInt(1, 0);
	              stmt.execute();
	              stmt.close();
	              stmt = null;
	            }
	            catch (Exception e)
	            {
	              VoteZone._log.log(Level.SEVERE, "[active_zone]:  ", e);
	            }
	            finally
	            {
	              CloseUtil.close(con);
	            }
	          }
	        }
	      }, 1000L);
	      
	      Announcement.AnnounceEvents("Next VoteZone: " + NewZoneVote.getInstance().getNextTime() + " GMT-3 .");
	    }
	  }
	
	public static boolean is_zone_1()
	{
		return zone_1;
	}
	
	public static boolean is_zone_2()
	{
		return zone_2;
	}
	
	public static boolean is_zone_3()
	{
		return zone_3;
	}
	
	public static boolean is_zone_4()
	{
		return zone_4;
	}
	
	public static boolean is_zone_5()
	{
		return zone_5;
	}
	
	public static boolean is_zone_6()
	{
		return zone_6;
	}
	
	public static boolean is_zone_7()
	{
		return zone_7;
	}
	
	  public static boolean selected_zone_1() {
		    return selected_zone_1;
		  }
		  
		  public static boolean selected_zone_2() {
		    return selected_zone_2;
		  }
		  
		  public static boolean selected_zone_3() {
		    return selected_zone_3;
		  }
		  
		  public static boolean selected_zone_4() {
		    return selected_zone_4;
		  }
		  
		  public static boolean selected_zone_5() {
		    return selected_zone_5;
		  }
		  
		  public static boolean selected_zone_6() {
		    return selected_zone_6;
		  }
		  
		  public static boolean selected_zone_7() {
			    return selected_zone_7;
			  }
	
	@SuppressWarnings("resource")
	private static void LocsZone1()
	{
		_zone_1_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Ketra_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_1_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_1_locsCount = _zone_1_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone2()
	{
		_zone_2_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Primeval_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_2_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_2_locsCount = _zone_2_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone3()
	{
		_zone_3_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Heine_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_3_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_3_locsCount = _zone_3_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone4()
	{
		_zone_4_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/IrisLake_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_4_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_4_locsCount = _zone_4_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone5()
	{
		_zone_5_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Alligator_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_5_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_5_locsCount = _zone_5_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone6()
	{
		_zone_6_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Imperial_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_6_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_6_locsCount = _zone_6_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	@SuppressWarnings("resource")
	private static void LocsZone7()
	{
		_zone_7_Loc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/pvpzone/Whispers_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_zone_7_Loc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_zone_7_locsCount = _zone_7_Loc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	
	public void getInitRandomZone()
	{
		if (Config.PVPZONE_INIT == 1)
		{
			zone_1 = true;
			startzone_1 = true;
		}
		else if (Config.PVPZONE_INIT == 2)
		{
			zone_2 = true;
			startzone_2 = true;
		}
		else if (Config.PVPZONE_INIT == 3)
		{
			zone_3 = true;
			startzone_3 = true;
		}
		else if (Config.PVPZONE_INIT == 4)
		{
			zone_4 = true;
			startzone_4 = true;
		}
		else if (Config.PVPZONE_INIT == 5)
		{
			zone_5 = true;
			startzone_5 = true;
		}
		else if (Config.PVPZONE_INIT == 6)
		{
			zone_6 = true;
			startzone_6 = true;
		}
		else if (Config.PVPZONE_INIT == 7)
		{
			zone_7 = true;
			startzone_7 = true;
		}
		else if (Config.PVPZONE_INIT < 1 || Config.PVPZONE_INIT > 7)
		{
			zone_2 = true;
			startzone_2 = true;
		}
		
	}
	
	  public static void getRandomZone() {
		    if (VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_6() && VoteZoneCommands.getVote_1() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_1() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = true;
		        zone_2 = false;
		        zone_3 = false;
		        zone_4 = false;
		        zone_5 = false;
		        zone_6 = false;
		        zone_7 = false;
		        selected_zone_1 = true;
		        System.out.println("ZoneRandom: Select Zone (Ketra Orc)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 1);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    } else if (VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_6() && VoteZoneCommands.getVote_2() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_2() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = false;
		        zone_2 = true;
		        zone_3 = false;
		        zone_4 = false;
		        zone_5 = false;
		        zone_6 = false;
		        zone_7 = false;
		        selected_zone_2 = true;
		        System.out.println("ZoneRandom: Select Zone (ILHA)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 2);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    } else if (VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_6() && VoteZoneCommands.getVote_3() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_3() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = false;
		        zone_2 = false;
		        zone_3 = true;
		        zone_4 = false;
		        zone_5 = false;
		        zone_6 = false;
		        zone_7 = false;
		        selected_zone_3 = true;
		        System.out.println("ZoneRandom: Select Zone (HEINE)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 3);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    } else if (VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_6() && VoteZoneCommands.getVote_4() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_4() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = false;
		        zone_2 = false;
		        zone_3 = false;
		        zone_4 = true;
		        zone_5 = false;
		        zone_6 = false;
		        zone_7 = false;
		        selected_zone_4 = true;
		        System.out.println("ZoneRandom: Select Zone (Iris Lake)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 4);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    } else if (VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_6() && VoteZoneCommands.getVote_5() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_5() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = false;
		        zone_2 = false;
		        zone_3 = false;
		        zone_4 = false;
		        zone_5 = true;
		        zone_6 = false;
		        zone_7 = false;
		        selected_zone_5 = true;
		        System.out.println("ZoneRandom: Select Zone (Alligator Island)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 5);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    } else if (VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_6() > VoteZoneCommands.getVote_7()) {
		      if (VoteZoneCommands.getVote_6() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		        zone_1 = false;
		        zone_2 = false;
		        zone_3 = false;
		        zone_4 = false;
		        zone_5 = false;
		        zone_6 = true;
		        zone_7 = false;
		        selected_zone_6 = true;
		        System.out.println("ZoneRandom: Select Zone (Imperial Tomb)");
		        Connection con = null;
		        try {
		          con = L2DatabaseFactory.getInstance().getConnection();
		          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
		          stmt.setInt(1, 1);
		          stmt.setInt(2, 6);
		          stmt.execute();
		          stmt.close();
		          stmt = null;
		        } catch (Exception e) {
		          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
		        } finally {
		          CloseUtil.close(con);
		        } 
		      } 
		    }
		    else if (VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_1() && VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_2() && VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_3() && VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_4() && VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_5() && VoteZoneCommands.getVote_7() > VoteZoneCommands.getVote_6()) {
			      if (VoteZoneCommands.getVote_7() >= Config.VOTE_PVPZONE_MIN_VOTE) {
			        zone_1 = false;
			        zone_2 = false;
			        zone_3 = false;
			        zone_4 = false;
			        zone_5 = false;
			        zone_6 = false;
					zone_7 = true;
			        selected_zone_7 = true;
			        System.out.println("ZoneRandom: Select Zone (Field of Whispers)");
			        Connection con = null;
			        try {
			          con = L2DatabaseFactory.getInstance().getConnection();
			          PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET active_zone=? WHERE zone_id=?");
			          stmt.setInt(1, 1);
			          stmt.setInt(2, 7);
			          stmt.execute();
			          stmt.close();
			          stmt = null;
			        } catch (Exception e) {
			          _log.log(Level.SEVERE, "ADD Active Zone:  ", e);
			        } finally {
			          CloseUtil.close(con);
			        } 
			      } 
			    }else if (VoteZoneCommands.getVote_1() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_2() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_3() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_4() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_5() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_6() >= Config.VOTE_PVPZONE_MIN_VOTE || VoteZoneCommands.getVote_7() >= Config.VOTE_PVPZONE_MIN_VOTE) {
		      empate = true;
		    } 
		  }
		  
		  public static void StartVoteZone() {
		    ThreadPool.schedule(new Runnable() {
		          @Override
				public void run() {
		            NewZoneVote._started = true;
		            NewZoneVote._vote = true;
		            for (Player player : World.getInstance().getPlayers()) {
		              if (player != null && player.isOnline() && !player.isInObserverMode())
		                VoteZoneCommands.ShowHtml(player); 
		            } 
		            NewZoneVote.waiter(180000L);
		          }
		        },  1L);
		  }
	
}
