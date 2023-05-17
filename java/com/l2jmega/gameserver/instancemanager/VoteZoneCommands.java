package com.l2jmega.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.NewZoneVote;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmega.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmega.util.CloseUtil;

public class VoteZoneCommands {
  public static final VoteZoneCommands getInstance() {
    return SingletonHolder._instance;
  }
  
  public static final Logger _log = Logger.getLogger(VoteZoneCommands.class.getName());
  
  private static int vote_1 = 0;
  
  static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  
  public static int getVote_1() {
    return vote_1;
  }
  
  public static void addVote_1(int to) {
    if (!check_vote(1)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Ketra Orc");
        stmt.setInt(2, 1);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(1)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_1 + to);
        stmt.setInt(2, 1);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_1 += to;
  }
  
  public static void cleanVote_1(int to) {
    vote_1 = to;
  }
  
  private static int vote_2 = 0;
  
  public static int getVote_2() {
    return vote_2;
  }
  
  public static void addVote_2(int to) {
    if (!check_vote(2)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Primeval Isle");
        stmt.setInt(2, 2);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(2)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_2 + to);
        stmt.setInt(2, 2);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_2 += to;
  }
  
  public static void cleanVote_2(int to) {
    vote_2 = to;
  }
  
  private static int vote_3 = 0;
  
  public static int getVote_3() {
    return vote_3;
  }
  
  public static void addVote_3(int to) {
    if (!check_vote(3)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Heine");
        stmt.setInt(2, 3);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(3)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_3 + to);
        stmt.setInt(2, 3);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_3 += to;
  }
  
  public static void cleanVote_3(int to) {
    vote_3 = to;
  }
  
  private static int vote_4 = 0;
  
  public static int getVote_4() {
    return vote_4;
  }
  
  public static void addVote_4(int to) {
    if (!check_vote(4)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Iris Lake");
        stmt.setInt(2, 4);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(4)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_4 + to);
        stmt.setInt(2, 4);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_4 += to;
  }
  
  public static void cleanVote_4(int to) {
    vote_4 = to;
  }
  
  private static int vote_5 = 0;
  
  public static int getVote_5() {
    return vote_5;
  }
  
  public static void addVote_5(int to) {
    if (!check_vote(5)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Alligator Island");
        stmt.setInt(2, 5);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(5)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_5 + to);
        stmt.setInt(2, 5);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_5 += to;
  }
  
  public static void cleanVote_5(int to) {
    vote_5 = to;
  }
  
  private static int vote_6 = 0;
  
  public static int getVote_6() {
    return vote_6;
  }
  
  public static void addVote_6(int to) {
    if (!check_vote(6)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Imperial Tomb");
        stmt.setInt(2, 6);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(6)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_6 + to);
        stmt.setInt(2, 6);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_6 += to;
  }
  
  public static void cleanVote_6(int to) {
    vote_6 = to;
  }
  
  private static int vote_7 = 0;
  
  public static int getVote_7() {
    return vote_7;
  }
  
  public static void addVote_7(int to) {
    if (!check_vote(7)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("REPLACE INTO ranking_votezone (zone_name, zone_id, votes, active_zone) VALUES (?,?,?,?)");
        stmt.setString(1, "Field of Whispers");
        stmt.setInt(2, 7);
        stmt.setInt(3, to);
        stmt.setInt(4, 0);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } else if (check_vote(7)) {
      Connection con = null;
      try {
        con = L2DatabaseFactory.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE ranking_votezone SET votes=? WHERE zone_id=?");
        stmt.setInt(1, vote_7 + to);
        stmt.setInt(2, 7);
        stmt.execute();
        stmt.close();
        stmt = null;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        CloseUtil.close(con);
      } 
    } 
    vote_7 += to;
  }
  
  public static void cleanVote_7(int to) {
    vote_7 = to;
  }
  
  public void Classes(String command, Player activeChar) {
	    if (command.startsWith("close")) {
	      activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	    } else {
	      if (command.startsWith("vote_1")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_1() || !Config.ENABLED_PVPZONE_KETRA) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_1(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      } 
	      if (command.startsWith("vote_2")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_2() || !Config.ENABLED_PVPZONE_ILHA) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_2(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      } 
	      if (command.startsWith("vote_3")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_3() || !Config.ENABLED_PVPZONE_HEINE) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_3(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      } 
	      if (command.startsWith("vote_4")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_4() || !Config.ENABLED_PVPZONE_IRISLAKE) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_4(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      } 
	      if (command.startsWith("vote_5")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_5() || !Config.ENABLED_PVPZONE_ALLIGATOR) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_5(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      } 
	      if (command.startsWith("vote_6")) {
	        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_6() || !Config.ENABLED_PVPZONE_IMPERIAL) {
	          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	          return;
	        } 
	        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
	          ShowResult_init(activeChar);
	          return;
	        } 
	        activeChar.setVotePlayer(true);
	        addVote_6(1);
	        if (!activeChar.isGM())
	          activeChar.setVoteZone(activeChar.getHWID()); 
	        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
	        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
	        ShowResult_init(activeChar);
	        return;
	      }
	      if (command.startsWith("vote_7")) {
		        if (!NewZoneVote.is_vote() || VoteZone.selected_zone_7() || !Config.ENABLED_PVPZONE_WHISPERS) {
		          activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		          return;
		        } 
		        if (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) {
		          ShowResult_init(activeChar);
		          return;
		        } 
		        activeChar.setVotePlayer(true);
		        addVote_7(1);
		        if (!activeChar.isGM())
		          activeChar.setVoteZone(activeChar.getHWID()); 
		        activeChar.sendChatMessage(0, 2, "VoteZone", "Vote added successfully!");
		        activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
		        ShowResult_init(activeChar);
		        return;
		      } 
	    } 
	  }
  
  public static final void Link(Player player, String request) {
    getInstance().Classes(request, player);
  }
  
  public static void ShowHtml(Player activeChar) {
    StringBuilder sb = new StringBuilder();
    sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
    if (Config.ENABLED_PVPZONE_KETRA)
      if (!VoteZone.selected_zone_1() && (!VoteZone.is_zone_1() || VoteZone.startzone_1)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Ketra Orc</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_1\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=80 align=center>Ketra Orc</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }  
    if (Config.ENABLED_PVPZONE_ILHA)
      if (!VoteZone.selected_zone_2() && (!VoteZone.is_zone_2() || VoteZone.startzone_2)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Primeval Isle</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_2\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=85 align=center>Primeval Isle</td>");
        sb.append("<td fixwidth=12></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }  
    if (Config.ENABLED_PVPZONE_HEINE)
      if (!VoteZone.selected_zone_3() && (!VoteZone.is_zone_3() || VoteZone.startzone_3)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Heine</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_3\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=80 align=center>Heine</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }  
    if (Config.ENABLED_PVPZONE_IRISLAKE)
      if (!VoteZone.selected_zone_4() && (!VoteZone.is_zone_4() || VoteZone.startzone_4)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Iris Lake</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_4\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=80 align=center>Iris Lake</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }  
    if (Config.ENABLED_PVPZONE_ALLIGATOR)
      if (!VoteZone.selected_zone_5() && (!VoteZone.is_zone_5() || VoteZone.startzone_5)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Alligator Island</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_5\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=100 align=center>Alligator Island</td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=6></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }  
    if (Config.ENABLED_PVPZONE_IMPERIAL)
      if (!VoteZone.selected_zone_6() && (!VoteZone.is_zone_6() || VoteZone.startzone_6)) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Imperial Tomb</td>");
        sb.append("<td fixwidth=20></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_6\" value=\"Vote\">");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      } else {
        sb.append("<table width=\"200\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=95 align=center>Imperial Tomb</td>");
        sb.append("<td fixwidth=6></td>");
        
        sb.append("<td fixwidth=50 height=10>");
        sb.append("<center><font color=\"ff0000\"> Voted</font>");
        sb.append("</center></td>");
        sb.append("<td fixwidth=5></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
      }
    if (Config.ENABLED_PVPZONE_WHISPERS)
        if (!VoteZone.selected_zone_7() && (!VoteZone.is_zone_7() || VoteZone.startzone_7)) {
          sb.append("<table width=\"160\" bgcolor=\"000000\">");
          sb.append("<tr>");
          sb.append("<td width=170 align=center>Field of Whispers</td>");
          sb.append("<td fixwidth=20></td>");
          
          sb.append("<td fixwidth=50 height=10>");
          sb.append("<center><button width=\"75\" height=\"21\" back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\" action=\"link vote_7\" value=\"Vote\">");
          sb.append("</center></td>");
          sb.append("<td fixwidth=5></td>");
          sb.append("</tr>");
          sb.append("</table>");
          sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
        } else {
          sb.append("<table width=\"200\" bgcolor=\"000000\">");
          sb.append("<tr>");
          sb.append("<td width=95 align=center>Field of Whispers</td>");
          sb.append("<td fixwidth=6></td>");
          
          sb.append("<td fixwidth=50 height=10>");
          sb.append("<center><font color=\"ff0000\"> Voted</font>");
          sb.append("</center></td>");
          sb.append("<td fixwidth=5></td>");
          sb.append("</tr>");
          sb.append("</table>");
          sb.append("<img src=\"L2UI.SquareGray\" width=200 height=1>");
        } 
    String msg = HtmCache.getInstance().getHtm("data/html/mods/Vote Zone/vote_zone.htm");
    msg = msg.replaceAll("%list%", sb.toString());
    msg = msg.replaceAll("%1%", "" + getVote_1() + "");
    msg = msg.replaceAll("%2%", "" + getVote_2() + "");
    msg = msg.replaceAll("%3%", "" + getVote_3() + "");
    msg = msg.replaceAll("%4%", "" + getVote_4() + "");
    msg = msg.replaceAll("%5%", "" + getVote_5() + "");
    msg = msg.replaceAll("%6%", "" + getVote_6() + "");
    msg = msg.replaceAll("%7%", "" + getVote_7() + "");
    msg = msg.replaceAll("%vote%", "" + Config.VOTE_PVPZONE_MIN_VOTE + "");
    if (VoteZone.is_zone_1()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Ketra Orc");
    } else if (VoteZone.is_zone_2()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Primeval Isle");
    } else if (VoteZone.is_zone_3()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Heine");
    } else if (VoteZone.is_zone_4()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Iris Lake");
    } else if (VoteZone.is_zone_5()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Alligator Island");
    } else if (VoteZone.is_zone_6()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Imperial Tomb");
    }else if (VoteZone.is_zone_7()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Field of Whispers");
      }else if (!VoteZone.is_zone_1() && !VoteZone.is_zone_2() && !VoteZone.is_zone_3() && !VoteZone.is_zone_4() && !VoteZone.is_zone_5() && !VoteZone.is_zone_6() && !VoteZone.is_zone_7()) {
      msg = msg.replaceAll("%zone%", "");
    } 
    activeChar.sendPacket(new TutorialShowHtml(msg));
  }
  
  public static void ShowResult_init(Player activeChar) {
    StringBuilder _ranking = new StringBuilder();
    _ranking.append("<center>");
    _ranking.append("<table border=1 width=\"250\">");
    _ranking.append("<tr>");
    _ranking.append("<td><center>Rank</center></td>");
    _ranking.append("<td><center>Zone Name</center></td>");
    _ranking.append("<td><center>Vote's</center></td>");
    _ranking.append("</tr>");
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT zone_name,zone_id,votes FROM ranking_votezone WHERE votes>0 order by votes desc limit 15");
      ResultSet result = statement.executeQuery();
      int pos = 0;
      while (result.next()) {
        String name = result.getString("zone_name");
        String votes = result.getString("votes");
        pos++;
        _ranking.append("<tr>");
        _ranking.append("<td><center><font color =\"AAAAAA\">" + pos + "</font></center></td>");
        _ranking.append("<td><center><font color=009900>" + name + "</font></center></td>");
        _ranking.append("<td><center><font color=LEVEL>" + votes + "</font></center></td>");
        _ranking.append("</tr>");
      } 
      statement.close();
      result.close();
    } catch (Exception exception) {}
    _ranking.append("</table>");
    _ranking.append("</center>");
    String msg = HtmCache.getInstance().getHtm("data/html/mods/Vote Zone/vote_zone_result_1.htm");
    msg = msg.replaceAll("%list%", _ranking.toString());
    msg = msg.replaceAll("%1%", "" + getVote_1() + "");
    msg = msg.replaceAll("%2%", "" + getVote_2() + "");
    msg = msg.replaceAll("%3%", "" + getVote_3() + "");
    msg = msg.replaceAll("%4%", "" + getVote_4() + "");
    msg = msg.replaceAll("%5%", "" + getVote_5() + "");
    msg = msg.replaceAll("%6%", "" + getVote_6() + "");
    msg = msg.replaceAll("%7%", "" + getVote_7() + "");
    msg = msg.replaceAll("%vote%", "" + Config.VOTE_PVPZONE_MIN_VOTE + "");
    msg = msg.replaceAll("%time%", "Server Time : <font color=\"00ff00\">" + sdf.format(new Date(System.currentTimeMillis())) + "</font>");
    if (VoteZone.is_zone_1()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Ketra Orc");
    } else if (VoteZone.is_zone_2()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Primeval Isle");
    } else if (VoteZone.is_zone_3()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Heine");
    } else if (VoteZone.is_zone_4()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Iris Lake");
    } else if (VoteZone.is_zone_5()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Alligator Island");
    } else if (VoteZone.is_zone_6()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Imperial Tomb");
    } else if (VoteZone.is_zone_7()) {
      msg = msg.replaceAll("%zone%", "PvpZone: Field of Whispers");
      } else if (!VoteZone.is_zone_1() && !VoteZone.is_zone_2() && !VoteZone.is_zone_3() && !VoteZone.is_zone_4() && !VoteZone.is_zone_5() && !VoteZone.is_zone_6() && !VoteZone.is_zone_7()) {
      msg = msg.replaceAll("%zone%", "");
    } 
    msg = msg.replaceAll("%next%", NewZoneVote.getInstance().getNextTime());
    activeChar.sendPacket(new TutorialShowHtml(msg));
  }
  
  public static void ShowResult(Player activeChar) {
    StringBuilder _ranking = new StringBuilder();
    _ranking.append("<center>");
    _ranking.append("<table border=1 width=\"250\">");
    _ranking.append("<tr>");
    _ranking.append("<td><center>Rank</center></td>");
    _ranking.append("<td><center>Zone Name</center></td>");
    _ranking.append("<td><center>Vote's</center></td>");
    _ranking.append("</tr>");
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT zone_name,zone_id,votes FROM ranking_votezone WHERE votes>0 order by votes desc limit 15");
      ResultSet result = statement.executeQuery();
      int pos = 0;
      while (result.next()) {
        String name = result.getString("zone_name");
        String votes = result.getString("votes");
        pos++;
        _ranking.append("<tr>");
        _ranking.append("<td><center><font color =\"AAAAAA\">" + pos + "</font></center></td>");
        _ranking.append("<td><center><font color=009900>" + name + "</font></center></td>");
        _ranking.append("<td><center><font color=LEVEL>" + votes + "</font></center></td>");
        _ranking.append("</tr>");
      } 
      statement.close();
      result.close();
    } catch (Exception exception) {}
    _ranking.append("</table>");
    _ranking.append("</center>");
    String msg = HtmCache.getInstance().getHtm("data/html/mods/Vote Zone/vote_zone_result.htm");
    msg = msg.replaceAll("%list%", _ranking.toString());
    msg = msg.replaceAll("%1%", "" + getVote_1() + "");
    msg = msg.replaceAll("%2%", "" + getVote_2() + "");
    msg = msg.replaceAll("%3%", "" + getVote_3() + "");
    msg = msg.replaceAll("%4%", "" + getVote_4() + "");
    msg = msg.replaceAll("%5%", "" + getVote_5() + "");
    msg = msg.replaceAll("%6%", "" + getVote_6() + "");
    msg = msg.replaceAll("%7%", "" + getVote_7() + "");
    msg = msg.replaceAll("%time%", "Server Time : <font color=\"00ff00\">" + sdf.format(new Date(System.currentTimeMillis())) + "</font>");
    msg = msg.replaceAll("%vote%", "" + Config.VOTE_PVPZONE_MIN_VOTE + "");
    if (VoteZone.is_zone_1()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Ketra Orc");
    } else if (VoteZone.is_zone_2()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Primeval Isle");
    } else if (VoteZone.is_zone_3()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Heine");
    } else if (VoteZone.is_zone_4()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Iris Lake");
    } else if (VoteZone.is_zone_5()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Alligator Island");
    } else if (VoteZone.is_zone_6()) {
      msg = msg.replaceAll("%zone%", "New PvpZone: Imperial Tomb");
    } else if (VoteZone.is_zone_7()) {
        msg = msg.replaceAll("%zone%", "New PvpZone: Field of Whispers");
    }
    else if (!VoteZone.is_zone_1() && !VoteZone.is_zone_2() && !VoteZone.is_zone_3() && !VoteZone.is_zone_4() && !VoteZone.is_zone_5()) {
      if (((!VoteZone.is_zone_6() ? 1 : 0) & (!VoteZone.is_zone_7() ? 1 : 0)) != 0)
        msg = msg.replaceAll("%zone%", ""); 
    } 
    msg = msg.replaceAll("%next%", NewZoneVote.getInstance().getNextTime());
    activeChar.sendPacket(new TutorialShowHtml(msg));
  }
  
  public static synchronized boolean check_vote(int charid) {
    boolean result = true;
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT zone_id FROM ranking_votezone WHERE zone_id=?");
      statement.setInt(1, charid);
      ResultSet rset = statement.executeQuery();
      result = rset.next();
      rset.close();
      statement.close();
    } catch (SQLException e) {
      _log.log(Level.WARNING, "check_vote: " + e.getMessage(), e);
    } 
    return result;
  }
  
  private static class SingletonHolder {
    protected static final VoteZoneCommands _instance = new VoteZoneCommands();
  }
}
