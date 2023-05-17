package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.l2jmega.Config;
import com.l2jmega.gameserver.NewZoneVote;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.instancemanager.VoteZone;
import com.l2jmega.gameserver.instancemanager.VoteZoneCommands;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.TutorialShowHtml;

public class VoicedVoteZone implements IVoicedCommandHandler {
  private static final String[] VOICED_COMMANDS = new String[] { "votezone" };
  
  static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  
  @Override
public boolean useVoicedCommand(String command, Player activeChar, String target) {
    if (command.startsWith("votezone")) {
      if (!Config.VOTE_PVPZONE_ENABLED)
        return false; 
      if (NewZoneVote.is_started() && !activeChar.check_obj_votezone(activeChar.getObjectId()) && !activeChar.check_hwid_votezone(activeChar.getHWID()) && !activeChar.isInObserverMode()) {
        VoteZoneCommands.ShowHtml(activeChar);
      } else if (NewZoneVote.is_started() && (activeChar.check_obj_votezone(activeChar.getObjectId()) || activeChar.check_hwid_votezone(activeChar.getHWID())) && !activeChar.isInObserverMode()) {
        VoteZoneCommands.ShowResult_init(activeChar);
      } else {
        ShowHtml(activeChar);
      } 
    } 
    return true;
  }
  
  public static void ShowHtml(Player activeChar) {
    StringBuilder sb = new StringBuilder();
    sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    if (Config.ENABLED_PVPZONE_KETRA) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Ketra Orc</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_ILHA) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Primeval Isle</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_HEINE) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Heine</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_IRISLAKE) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Iris Lake</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_ALLIGATOR) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Alligator Island</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_IMPERIAL) {
      sb.append("<table width=\"160\" bgcolor=\"000000\">");
      sb.append("<tr>");
      sb.append("<td width=170 align=center>Imperial Tomb</td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
    } 
    if (Config.ENABLED_PVPZONE_WHISPERS) {
        sb.append("<table width=\"160\" bgcolor=\"000000\">");
        sb.append("<tr>");
        sb.append("<td width=170 align=center>Field of Whispers</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<img src=\"L2UI.SquareGray\" width=160 height=1>");
      } 
    String msg = HtmCache.getInstance().getHtm("data/html/mods/Vote Zone/vote_zone_info.htm");
    msg = msg.replaceAll("%list%", sb.toString());
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
    msg = msg.replaceAll("%next%", NewZoneVote.getInstance().getNextTime());
    msg = msg.replaceAll("%time%", "Server Time : <font color=\"00ff00\">" + sdf.format(new Date(System.currentTimeMillis())) + "</font>");
    activeChar.sendPacket(new TutorialShowHtml(msg));
  }
  
  @Override
public String[] getVoicedCommandList() {
    return VOICED_COMMANDS;
  }
}
