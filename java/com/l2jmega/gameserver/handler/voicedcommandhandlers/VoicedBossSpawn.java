package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.instancemanager.GrandBossManager;
import com.l2jmega.gameserver.instancemanager.RaidBossInfoManager;
import com.l2jmega.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.templates.StatsSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;



public class VoicedBossSpawn
  implements IVoicedCommandHandler
{
  private static Logger _log = Logger.getLogger(VoicedBossSpawn.class.getName());
  private static final String[] VOICED_COMMANDS = 
  {
	  "raidinfo",
	  "checkZone"
  };

  @Override
public boolean useVoicedCommand(String command, Player activeChar, String target) 
  {
    if (command.startsWith("raidinfo"))
    {
      showInfoPage(activeChar);
    }
	else if (command.startsWith("checkZone"))
	{
		try
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			int bossId = Integer.parseInt(st.nextToken());
			String type = st.nextToken();
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/mods/CheckZone.htm");
			htm.replace("%content%", generateZoneCheckHtml(bossId, type));
			htm.replace("%bossname%", NpcTable.getInstance().getTemplate(bossId) != null ? NpcTable.getInstance().getTemplate(bossId).getName() : "Boss");
			activeChar.sendPacket(htm);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
    return true;
  }
  
	public String generateZoneCheckHtml(int bossId, String type)
	{
		try
		{
			L2Spawn boss = null;
			int x = 0, y = 0;
			if (type.equals("RaidBoss"))
			{
				boss = RaidBossSpawnManager.getInstance().getSpawns().get(bossId);
				x = boss.getLocX();
				y = boss.getLocY();
			}
			else if (type.equals("GrandBoss"))
			{
				StatsSet stats = GrandBossManager.getInstance().getStatsSet(bossId);
				if (stats != null)
				
				{
					x = stats.getInteger("loc_x");
					y = stats.getInteger("loc_y");
				}
				else
				{
					return "Grand Boss is Waiting!";
				}
				
			}
			
			List<Player> insideRadius = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			Map<String, List<Player>> cInsideRadius = new HashMap<>();
			
			for (Player player : World.getInstance().getPlayers())
			{
				if (player.isInsideRadius(x, y, 10000, false))
				{
					insideRadius.add(player);
				}
			}
			for (Player player : insideRadius)
			{
				if (player.getClan() != null)
				{
					
					if (player.getClan().getAllyId() != 0 && !cInsideRadius.containsKey(player.getClan().getAllyName()))
					{
						cInsideRadius.put(player.getClan().getAllyName(), new ArrayList<Player>());
						cInsideRadius.get(player.getClan().getAllyName()).add(player);
					}
					else if (player.getClan().getAllyId() != 0 && cInsideRadius.containsKey(player.getClan().getAllyName()))
					{
						cInsideRadius.get(player.getClan().getAllyName()).add(player);
					}
					else if (player.getClan().getAllyId() == 0 && !cInsideRadius.containsKey(player.getClan().getName()))
					{
						cInsideRadius.put(player.getClan().getName(), new ArrayList<Player>());
						cInsideRadius.get(player.getClan().getName()).add(player);
					}
					else if (player.getClan().getAllyId() == 0 && cInsideRadius.containsKey(player.getClan().getName()))
					{
						cInsideRadius.get(player.getClan().getName()).add(player);
					}
					
				}
			}
			for (Map.Entry<String, List<Player>> entry : cInsideRadius.entrySet())
			{
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
				sb.append("<table width=270 >");
				sb.append("<tr>");
				sb.append("<td width=100 align=center>");
				sb.append(ClanTable.getInstance().getAllianceNames().contains(entry.getKey()) ? "Ally" : "Clan");
				sb.append("</td>");
				sb.append("<td width=130 align=center>");
				sb.append(entry.getKey());
				sb.append("</td>");
				sb.append("<td width=100 align=center>");
				sb.append(String.valueOf(entry.getValue().size()));
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				
			}
			return sb.toString();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Can't Show Html, please contact and Admin!";
		}
		
	}
  
  private static void showInfoPage(Player activeChar) {
    StringBuilder tb = new StringBuilder();
    tb.append("<html><title>Boss Info</title><body>");
    tb.append("<table width=80>");
    tb.append("<tr>");
    tb.append("<td><button value=\"\" action=\"bypass voiced_menu \" width=35 height=23 back=\"L2UI_CH3.calculate2_bs_down\" fore=\"L2UI_CH3.calculate2_bs\"></td>");
    tb.append("<td> Menu </td>");
    tb.append("</tr>");
    tb.append("</table><center>");
    tb.append("<img src=\"L2UI.SquareGray\" width=300 height=1>");
    
    tb.append("<table width=\"300\" bgcolor=\"000000\">");
    tb.append("<tr>");
    tb.append("<td><center>SVR Data: <font color=\"ff4d4d\">"+ (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())) +"</font></center></td>");
    tb.append("<td><center></center></td>");
    tb.append("<td><center>SVR Time: <font color=\"ff4d4d\">"+ (new SimpleDateFormat("HH:mm:ss")).format(new Date(System.currentTimeMillis())) +"</font></center></td>");
    tb.append("</tr>");
    tb.append("</table>");
    tb.append("<center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
    
    tb.append("<center><table width=\"260\">");
    
    for (Iterator<Integer> i$ = Config.RAID_INFO_IDS_LIST.iterator(); i$.hasNext(); ) {
      
      int boss = i$.next().intValue();
      
      String name = "";
      NpcTemplate template = null;
      if ((template = NpcTable.getInstance().getTemplate(boss)) != null) {
        
        name = template.getName();
        
        
            if (name.length() > 18)
        	name = name.substring(0, 18) + "...";
        
      }
      else {
        
        _log.warning("Raid Info: Raid Boss with ID " + boss + " is not defined into NpcXml");
        
        continue;
      } 
      long delay = 0L;
	  String type = null;
		if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
		{
			delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
			type = "RaidBoss";
		} else {
            continue;
          }
      if (delay <= System.currentTimeMillis()) {
        tb.append("<tr>");
        tb.append("<td width=\"146\" align=\"left\"><font color=\"FFFFFF\">" + name + "</font></td>");
        tb.append("<td width=\"55\" align=\"right\"><font color=\"00FF00\">Alive</font></td>");
        tb.append("<td width=\"55\" align=\"right\"><a action=\"bypass -h voiced_checkZone " + boss + " " + type + "\">Check</a></td>");
        tb.append("</tr>");
        continue;
      }
        tb.append("<tr>");
        tb.append("<td width=\"146\" align=\"left\"><font color=\"00FFFF\">" + name + "</font></td>");

        int hours = (int)((delay - System.currentTimeMillis()) / 1000L / 60L / 60L);
        int mins = (int)((delay - (hours * 60 * 60 * 1000) - System.currentTimeMillis()) / 1000L / 60L);
        int seconts = (int)((delay - (hours * 60 * 60 * 1000 + mins * 60 * 1000) - System.currentTimeMillis()) / 1000L);
       // tb.append("<td width=\"110\" align=\"right\"><font color=\"FB5858\"> " + (new SimpleDateFormat("MMM dd, HH:mm")).format(new Date(delay)) + "</font></td>");
        tb.append("<td width=\"110\" align=\"right\"><font color=\"FB5858\"> " + hours + ":" + mins + ":" + seconts + "</font></td>");
        tb.append("</tr>");
    }
    
    	tb.append("</table>");
    	tb.append("<br>");

    	tb.append("<center>");
    	tb.append("<img src=\"L2UI.SquareGray\" width=270 height=1>");
    	tb.append("<br>");
    	tb.append("<center><table width=\"260\">");
      
      for (Iterator<Integer> i$ = Config.GRANDBOSS_INFO_IDS_LIST.iterator(); i$.hasNext(); ) {
        
        int boss = i$.next().intValue();
        
        String name = "";
        NpcTemplate template = null;
        if ((template = NpcTable.getInstance().getTemplate(boss)) != null) {
          
          name = template.getName();
        }
        else {
          
          _log.warning("Grad Boss Info: Raid Boss with ID " + boss + " is not defined into NpcXml");
          
          continue;
        } 
        StatsSet actual_boss_stat = null;
        GrandBossManager.getInstance().getStatsSet(boss);
        long delay = 0L;
		String type = null;
        if (NpcTable.getInstance().getTemplate(boss).isType("GrandBoss")) {
            
            actual_boss_stat = GrandBossManager.getInstance().getStatsSet(boss);
            if (actual_boss_stat != null) {
              delay = actual_boss_stat.getLong("respawn_time");
              type = "GrandBoss";
            }
          } else {
            continue;
          }
        if (delay <= System.currentTimeMillis()) {

        tb.append("<tr>");
        tb.append("<td width=\"146\" align=\"left\"><font color=\"FFFFFF\">" + name + "</font></td>");
        tb.append("<td width=\"55\" align=\"right\"><font color=\"00FF00\">Alive</font></td>");
        tb.append("<td width=\"55\" align=\"right\"><a action=\"bypass -h voiced_checkZone " + boss + " " + type + "\">Check</a></td>");
     	tb.append("</tr>");
     	continue;
        } 
        tb.append("<tr>");
        tb.append("<td width=\"146\" align=\"left\"><font color=\"00FFFF\">" + name + "</font></td>");
 
        int hours = (int)((delay - System.currentTimeMillis()) / 1000L / 60L / 60L);
        int mins = (int)((delay - (hours * 60 * 60 * 1000) - System.currentTimeMillis()) / 1000L / 60L);
        int seconts = (int)((delay - (hours * 60 * 60 * 1000 + mins * 60 * 1000) - System.currentTimeMillis()) / 1000L);
       // tb.append("<td width=\"110\" align=\"right\"><font color=\"FB5858\"> " + (new SimpleDateFormat("MMM dd, HH:mm")).format(new Date(delay)) + "</font></td>");
        tb.append("<td width=\"110\" align=\"right\"><font color=\"FB5858\"> " + hours + ":" + mins + ":" + seconts + "</font></td>");
        tb.append("</tr>");
        
      }

      	tb.append("</table>");
      	tb.append("<img src=\"L2UI.SquareGray\" width=270 height=1>");
      	tb.append("</body></html>");
    
    NpcHtmlMessage msg = new NpcHtmlMessage(1);
    msg.setHtml(tb.toString());
    
    activeChar.sendPacket(msg);
  }

     @Override
   public String[] getVoicedCommandList() 
   {
	  return VOICED_COMMANDS; 
   }

}