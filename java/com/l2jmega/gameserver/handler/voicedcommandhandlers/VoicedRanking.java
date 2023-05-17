package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.pvpevent.PvPEvent;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedRanking implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = new String[] { "pvpevent","pvpEvent","pvp", "pks", "clan", "ranking", "5x5", "9x9" };
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target) {
		if (command.equals("ranking")) {
			showRankingHtml(activeChar);
		}
		if ((command.equals("pvpEvent") || command.equals("pvpevent")) && Config.PVP_EVENT_ENABLED){
			PvPEvent.getTopHtml(activeChar); 
		}
		else if (command.equals("pvp")) {
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Character</center></td>");
			tb.append("<td><center>Pvp's</center></td>");
			tb.append("<td><center>Status</center></td>");
			tb.append("</tr>");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,pvpkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;
				while (result.next()) {
					String status, pvps = result.getString("pvpkills");
					String name = result.getString("char_name");
					if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWW") || name.equals("WWWWWWWWWWW") || name.equals("WWWWWWWWWW") || name.equals("WWWWWWWWW") || name.equals("WWWWWWWW") || name.equals("WWWWWWW") || name.equals("WWWWWW")) {
						name = name.substring(0, 3) + "..";
					} else if (name.length() > 14) {
						name = name.substring(0, 14) + "..";
					} 
					pos++;
					String statu = result.getString("online");
					if (statu.equals("1")) {
						status = "<font color=00FF00>Online</font>";
					} else {
						status = "<font color=FF0000>Offline</font>";
					} 
					tb.append("<tr>");
					tb.append("<td><center><font color =\"AAAAAA\">" + pos + "</font></center></td>");
					tb.append("<td><center><font color=00FFFF>" + name + "</font></center></td>");
					tb.append("<td><center>" + pvps + "</center></td>");
					tb.append("<td><center>" + status + "</center></td>");
					tb.append("</tr>");
				} 
				statement.close();
				result.close();
			} catch (Exception exception) {}
			
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
			
		} else if (command.equals("pks")) {
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Character</center></td>");
			tb.append("<td><center>Pk's</center></td>");
			tb.append("<td><center>Status</center></td>");
			tb.append("</tr>");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,pkkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pkkills desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;
				while (result.next()) {
					String status, pks = result.getString("pkkills");
					String name = result.getString("char_name");
					if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWW") || name.equals("WWWWWWWWWWW") || name.equals("WWWWWWWWWW") || name.equals("WWWWWWWWW") || name.equals("WWWWWWWW") || name.equals("WWWWWWW") || name.equals("WWWWWW")) {
						name = name.substring(0, 3) + "..";
					} else if (name.length() > 14) {
						name = name.substring(0, 14) + "..";
					} 
					pos++;
					String statu = result.getString("online");
					if (statu.equals("1")) {
						status = "<font color=00FF00>Online</font>";
					} else {
						status = "<font color=FF0000>Offline</font>";
					} 
					tb.append("<tr>");
					tb.append("<td><center><font color =\"AAAAAA\">" + pos + "</font></center></td>");
					tb.append("<td><center><font color=00FFFF>" + name + "</font></center></td>");
					tb.append("<td><center>" + pks + "</center></td>");
					tb.append("<td><center>" + status + "</center></td>");
					tb.append("</tr>");
				} 
				statement.close();
				result.close();
			} catch (Exception exception) {}
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		} else if (command.equals("clan")) {
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Clan</center></td>");
			tb.append("<td><center>Leader</center></td>");
			tb.append("<td><center>Kill Boss</center></td>");
			tb.append("</tr>");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
				PreparedStatement statement = con.prepareStatement("SELECT clan_id,clan_name,ally_name,boss_win FROM clan_data WHERE boss_win>0 order by boss_win desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;
				String leader_name = "N/A";
				while (result.next()) {
					int clan_id = result.getInt("clan_id");
					String clan_name = result.getString("clan_name");
					if (clan_name.equals("WWWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWW") || clan_name.equals("WWWWWWWWWW") || clan_name.equals("WWWWWWWWW") || clan_name.equals("WWWWWWWW") || clan_name.equals("WWWWWWW") || clan_name.equals("WWWWWW")) {
						clan_name = clan_name.substring(0, 3) + "..";
					} else if (clan_name.length() > 14) {
						clan_name = clan_name.substring(0, 14) + "..";
					} 
					String ally_name = result.getString("ally_name");
					if (ally_name == null)
						ally_name = "N/A"; 
					if (ally_name.equals("WWWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWW") || ally_name.equals("WWWWWWWWWW") || ally_name.equals("WWWWWWWWW") || ally_name.equals("WWWWWWWW") || ally_name.equals("WWWWWWW") || ally_name.equals("WWWWWW")) {
						ally_name = ally_name.substring(0, 3) + "..";
					} else if (ally_name.length() > 6) {
						ally_name = ally_name.substring(0, 6) + "..";
					} 
					Clan owner = ClanTable.getInstance().getClan(clan_id);
					if (owner != null)
						leader_name = owner.getLeaderName(); 
					if (leader_name.equals("WWWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWW") || leader_name.equals("WWWWWWWWWW") || leader_name.equals("WWWWWWWWW") || leader_name.equals("WWWWWWWW") || leader_name.equals("WWWWWWW") || leader_name.equals("WWWWWW")) {
						leader_name = leader_name.substring(0, 3) + "..";
					} else if (leader_name.length() > 10) {
						leader_name = leader_name.substring(0, 10) + "..";
					} 
					String win = result.getString("boss_win");
					pos++;
					tb.append("<tr><td><center>" + pos + "</center></td><td><center>" + clan_name + "</center></td><td><center>" + leader_name + "</center></td><td><center>" + win + "</center></td></tr>");
				} 
				statement.close();
				result.close();
			} catch (Exception exception) {}
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		} else if (command.equals("5x5")) {
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Clan</center></td>");
			tb.append("<td><center>Leader</center></td>");
			tb.append("<td><center>5x5 Win</center></td>");
			tb.append("</tr>");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
				PreparedStatement statement = con.prepareStatement("SELECT clan_id,clan_name,ally_name,5x5_win FROM clan_data WHERE 5x5_win>0 order by 5x5_win desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;
				String leader_name = "N/A";
				while (result.next()) {
					int clan_id = result.getInt("clan_id");
					String clan_name = result.getString("clan_name");
					if (clan_name.equals("WWWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWW") || clan_name.equals("WWWWWWWWWW") || clan_name.equals("WWWWWWWWW") || clan_name.equals("WWWWWWWW") || clan_name.equals("WWWWWWW") || clan_name.equals("WWWWWW")) {
						clan_name = clan_name.substring(0, 3) + "..";
					} else if (clan_name.length() > 14) {
						clan_name = clan_name.substring(0, 14) + "..";
					} 
					String ally_name = result.getString("ally_name");
					if (ally_name == null)
						ally_name = "N/A"; 
					if (ally_name.equals("WWWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWW") || ally_name.equals("WWWWWWWWWW") || ally_name.equals("WWWWWWWWW") || ally_name.equals("WWWWWWWW") || ally_name.equals("WWWWWWW") || ally_name.equals("WWWWWW")) {
						ally_name = ally_name.substring(0, 3) + "..";
					} else if (ally_name.length() > 6) {
						ally_name = ally_name.substring(0, 6) + "..";
					} 
					Clan owner = ClanTable.getInstance().getClan(clan_id);
					if (owner != null)
						leader_name = owner.getLeaderName(); 
					if (leader_name.equals("WWWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWW") || leader_name.equals("WWWWWWWWWW") || leader_name.equals("WWWWWWWWW") || leader_name.equals("WWWWWWWW") || leader_name.equals("WWWWWWW") || leader_name.equals("WWWWWW")) {
						leader_name = leader_name.substring(0, 3) + "..";
					} else if (leader_name.length() > 10) {
						leader_name = leader_name.substring(0, 10) + "..";
					} 
					String win = result.getString("5x5_win");
					pos++;
					tb.append("<tr><td><center>" + pos + "</center></td><td><center>" + clan_name + "</center></td><td><center>" + leader_name + "</center></td><td><center>" + win + "</center></td></tr>");
				} 
				statement.close();
				result.close();
			} catch (Exception exception) {}
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		} else if (command.equals("9x9")) {
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Clan</center></td>");
			tb.append("<td><center>Leader</center></td>");
			tb.append("<td><center>9x9 Win</center></td>");
			tb.append("</tr>");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
				PreparedStatement statement = con.prepareStatement("SELECT clan_id,clan_name,ally_name,9x9_win FROM clan_data WHERE 9x9_win>0 order by 9x9_win desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;
				String leader_name = "N/A";
				while (result.next()) {
					int clan_id = result.getInt("clan_id");
					String clan_name = result.getString("clan_name");
					if (clan_name.equals("WWWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWW") || clan_name.equals("WWWWWWWWWW") || clan_name.equals("WWWWWWWWW") || clan_name.equals("WWWWWWWW") || clan_name.equals("WWWWWWW") || clan_name.equals("WWWWWW")) {
						clan_name = clan_name.substring(0, 3) + "..";
					} else if (clan_name.length() > 14) {
						clan_name = clan_name.substring(0, 14) + "..";
					} 
					String ally_name = result.getString("ally_name");
					if (ally_name == null)
						ally_name = "N/A"; 
					if (ally_name.equals("WWWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWWW") || ally_name.equals("WWWWWWWWWWW") || ally_name.equals("WWWWWWWWWW") || ally_name.equals("WWWWWWWWW") || ally_name.equals("WWWWWWWW") || ally_name.equals("WWWWWWW") || ally_name.equals("WWWWWW")) {
						ally_name = ally_name.substring(0, 3) + "..";
					} else if (ally_name.length() > 6) {
						ally_name = ally_name.substring(0, 6) + "..";
					} 
					Clan owner = ClanTable.getInstance().getClan(clan_id);
					if (owner != null)
						leader_name = owner.getLeaderName(); 
					if (leader_name.equals("WWWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWWW") || leader_name.equals("WWWWWWWWWWW") || leader_name.equals("WWWWWWWWWW") || leader_name.equals("WWWWWWWWW") || leader_name.equals("WWWWWWWW") || leader_name.equals("WWWWWWW") || leader_name.equals("WWWWWW")) {
						leader_name = leader_name.substring(0, 3) + "..";
					} else if (leader_name.length() > 10) {
						leader_name = leader_name.substring(0, 10) + "..";
					} 
					String win = result.getString("9x9_win");
					pos++;
					tb.append("<tr><td><center>" + pos + "</center></td><td><center>" + clan_name + "</center></td><td><center>" + leader_name + "</center></td><td><center>" + win + "</center></td></tr>");
				} 
				statement.close();
				result.close();
			} catch (Exception exception) {}
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		return true;
	}
	
	private static void showRankingHtml(Player activeChar) {
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Ranking.htm");
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
