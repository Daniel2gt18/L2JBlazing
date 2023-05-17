package com.l2jmega.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class MissionReset {
	private static MissionReset _instance = null;
	
	protected static final Logger _log = Logger.getLogger(MissionReset.class.getName());
	
	private Calendar NextEvent;
	
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	
	public static MissionReset getInstance() {
		if (_instance == null)
			_instance = new MissionReset(); 
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
			for (String timeOfDay : Config.CLEAR_MISSION_INTERVAL_BY_TIME_OF_DAY) {
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
			_log.info("Mission Reset: Proximo Reset: " + this.NextEvent.getTime().toString());
			ThreadPool.schedule(new StartEventTask(), flush2);
		} catch (Exception e) {
			System.out.println("Mission Reset: " + e);
		} 
	}
	
	class StartEventTask implements Runnable {
		@Override
		public void run() {
			MissionReset.Clear();
		}
	}
	
	static void Clear() {
		ThreadPool.schedule(new Runnable() {
			@Override
			public void run() {
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tvt_event=?,tvt_completed=?,tvt_hwid=? WHERE tvt_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - tvt_completed]:  ", e);
				} 
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET pvp_event=?,pvp_completed=?,pvp_hwid=? WHERE pvp_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - pvp_completed]:  ", e);
				}
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET raid_event=?,raid_completed=?,raid_hwid=? WHERE raid_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - raid_completed]:  ", e);
				} 
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET mobs=?,mob_completed=?,mob_hwid=? WHERE mob_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - mob_completed]:  ", e);
				} 
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET party_mobs=?,party_mob_completed=?,party_mob_hwid=? WHERE party_mob_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - party_mob_completed]:  ", e);
				}
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_1x1_event=?,tournament_1x1_completed=?,tournament_1x1_hwid=? WHERE tournament_1x1_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - tournament_1x1_completed]:  ", e);
				} 
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_2x2_event=?,tournament_2x2_completed=?,tournament_2x2_hwid=? WHERE tournament_2x2_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - tournament_2x2_completed]:  ", e);
				}
				
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_5x5_event=?,tournament_5x5_completed=?,tournament_5x5_hwid=? WHERE tournament_5x5_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - tournament_5x5_completed]:  ", e);
				} 
				try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_9x9_event=?,tournament_9x9_completed=?,tournament_9x9_hwid=? WHERE tournament_9x9_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setString(3, " ");
					stmt.execute();
					stmt.close();
					stmt = null;
				} catch (Exception e) {
					e.printStackTrace();
					MissionReset._log.log(Level.SEVERE, "[characters_mission: - tournament_9x9_completed]:  ", e);
				} 
				for (Player player : World.getInstance().getPlayers()) {
					if (player != null && 
						player.isOnline()) {
						
						player._tvt_cont = 0;
						player.setTvTCompleted(false);
						
						player._pvp_cont = 0;
						player.setPvPCompleted(false);
						
						player._raid_cont = 0;
						player.setRaidCompleted(false);
						
						player._monsterKills = 0;
						player.setMobCompleted(false);
						
						player._partyKills = 0;
						player.setPartyMobCompleted(false);
						
						player._tournament_1x1_cont = 0;
						player.set1x1Completed(false);
						
						player._tournament_2x2_cont = 0;
						player.set2x2Completed(false);
						
						player._tournament_5x5_cont = 0;
						player.set5x5Completed(false);
						
						player._tournament_9x9_cont = 0;
						player.set9x9Completed(false);
						
						player.ReloadMission();
						
						CreatureSay cs = new CreatureSay(player.getObjectId(), 2, "SYS", "Daily activities were restarted!");
						player.sendPacket(cs);
					} 
				} 
				MissionReset._log.info("----------------------------------------------------------------------------");
				MissionReset._log.info("Mission Reset: Tarefas Diarias Resetadas.");
				MissionReset._log.info("----------------------------------------------------------------------------");
			}
		},1L);
		NextEvent();
	}
	
	public static void NextEvent() {
		ThreadPool.schedule(new Runnable() {
			@Override
			public void run() {
				MissionReset.getInstance().StartNextEventTime();
			}
		},  1000L);
	}
}
