package com.l2jmega.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.instancemanager.VoteZone;
import com.l2jmega.gameserver.instancemanager.VoteZoneCommands;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;

public class NewZoneVote {
  protected static ArrayList<L2Spawn> npc = new ArrayList<>();
  
  public static boolean _started = false;
  
  public static boolean _vote = false;
  
  private static NewZoneVote _instance = null;
  
  protected static final Logger _log = Logger.getLogger(NewZoneVote.class.getName());
  
  private Calendar NextEvent;
  
  private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
  
  public static NewZoneVote getInstance() {
    if (_instance == null)
      _instance = new NewZoneVote(); 
    return _instance;
  }
  
  public String getNextTime() {
    if (this.NextEvent.getTime() != null)
      return this.format.format(this.NextEvent.getTime()); 
    return "Erro";
  }
  
  public void StartNextEventTime() {
    try {
      Calendar currentTime = Calendar.getInstance();
      Calendar testStartTime = null;
      long flush2 = 0L, timeL = 0L;
      int count = 0;
      for (String timeOfDay : Config.VOTE_PVPZONE_INTERVAL_BY_TIME_OF_DAY) {
        testStartTime = Calendar.getInstance();
        testStartTime.setLenient(true);
        String[] splitTimeOfDay = timeOfDay.split(":");
        testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
        testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
        testStartTime.set(13, 0);
        if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
          testStartTime.add(5, 1); 
        timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (count == 0) {
          flush2 = timeL;
          this.NextEvent = testStartTime;
        } 
        if (timeL < flush2) {
          flush2 = timeL;
          this.NextEvent = testStartTime;
        } 
        count++;
      } 
      _log.info("[VoteZone]: Next Vote: " + this.NextEvent.getTime().toString());
      ThreadPool.schedule(new StartEventTask(), flush2);
    } catch (Exception e) {
      System.out.println("VoteZone: " + e);
    } 
  }
  
  class StartEventTask implements Runnable {
    @Override
	public void run() {
      NewZoneVote.Vote();
    }
  }
  
  static void Vote() {
    ThreadPool.schedule(new Runnable() {
          @Override
		public void run() {
            NewZoneVote._started = true;
            NewZoneVote._vote = true;
            for (Player player : World.getInstance().getPlayers()) {
              if (player != null && player.isOnline() && !player.isInObserverMode())
                VoteZoneCommands.ShowHtml(player); 
            } 
            NewZoneVote.waiter((Config.VOTE_PVPZONE_TIME * 60 * 1000));
            NewZoneVote._log.info("[VoteZone]: Starded!");
          }
        },  1L);
    Announcement.Announce("VoteZone: Vote for new PvpZone!");
    Announcement.Announce("VoteZone: Voting ends in " + Config.VOTE_PVPZONE_TIME + " minute(s)!");
    Announcement.Announce("VoteZone: Command .votezone");
    NextEvent();
  }
  
  public static void NextEvent() {
    ThreadPool.schedule(new Runnable() {
          @Override
		public void run() {
            NewZoneVote.getInstance().StartNextEventTime();
          }
        },  1000L);
  }
  
  public static void waiter(long interval) {
    long startWaiterTime = System.currentTimeMillis();
    int seconds = (int)(interval / 1000L);
    while (startWaiterTime + interval > System.currentTimeMillis() && _started) {
      seconds--;
      switch (seconds) {
        case 3600:
          Announcement.Announce("VoteZone: Vote for new PvpZone!");
          Announcement.Announce("VoteZone: Voting ends in " + (seconds / 60 / 60) + " hour(s)!");
          Announcement.Announce("VoteZone: Command .votezone");
          break;
        case 60:
        case 120:
        case 180:
        case 240:
        case 300:
        case 600:
        case 900:
        case 1200:
        case 1500:
        case 1800:
          Announcement.Announce("VoteZone: Vote for new PvpZone!");
          Announcement.Announce("VoteZone: Voting ends in " + (seconds / 60) + " minute(s)!");
          Announcement.Announce("VoteZone: Command .votezone");
          break;
        case 15:
        case 30:
          Announcement.Announce("VoteZone: Vote for new PvpZone!");
          Announcement.Announce("VoteZone: Voting ends in " + seconds + " second(s)!");
          Announcement.Announce("VoteZone: Command .votezone");
          break;
        case 2:
        case 3:
        case 10:
          Announcement.Announce("VoteZone: Voting ends in " + seconds + " second(s)!");
          break;
        case 1:
          Announcement.Announce("VoteZone: Voting ends in " + seconds + " second(s)!");
          Announcement.Announce("VoteZone: The Voting was closed!");
          _started = false;
          _vote = false;
          ThreadPool.schedule(new Runnable() {
                @Override
				public void run() {
                  VoteZone.SelectZone();
                }
              },  500L);
          try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("TRUNCATE votezone_hwid");
            statement.execute();
            statement.close();
          } catch (SQLException e) {
            _log.warning("VoteZone --> " + e);
          } 
          break;
      } 
      long startOneSecondWaiterStartTime = System.currentTimeMillis();
      while (startOneSecondWaiterStartTime + 1000L > System.currentTimeMillis()) {
        try {
          Thread.sleep(1L);
        } catch (InterruptedException ie) {
          ie.printStackTrace();
        } 
      } 
    } 
  }
  
  public static boolean is_started() {
    return _started;
  }
  
  public static boolean is_vote() {
    return _vote;
  }
}
