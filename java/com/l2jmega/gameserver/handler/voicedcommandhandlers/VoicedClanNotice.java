package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedClanNotice implements IVoicedCommandHandler
{
	private static String[] VOICED_COMMANDS =
	{
		"setnotice",
		"noticeenable",
		"noticedisable",
		"setmes",
		"clannotice"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{		
		if (command.equals("clannotice"))
		{
			if (activeChar.getClan() != null)
			{
				if (activeChar.getClan().isNoticeEnabled())
				{
					String getNotice = "data/html/clanNotice.htm";
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile(getNotice);
					html.replace("%clan_name%", activeChar.getClan().getName());
					html.replace("%notice_text%", activeChar.getClan().getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
					activeChar.sendPacket(html);
				}
			}
			return true;
		}
		if (command.equals("setnotice"))
		{
			if ((!activeChar.isNoble()) || (!activeChar.isClanLeader()))
			{
				activeChar.sendMessage("Only a noble leader can open the notice system.");
				VoicedMenu.showMenuHtml(activeChar);
				return false;
			}
			if ((activeChar.isNoble()) || (activeChar.isClanLeader()))
			{
				mainHtml(activeChar);
				return true;
			}
		}
		else if (command.equals("noticeenable"))
		{
			if (activeChar.isClanLeader())
			{
				activeChar.getClan().setNoticeEnabled(true);
				activeChar.getClan().setNoticeEnabledAndStore(true);
				activeChar.getClan().getClanId();
				activeChar.sendMessage("Clan notice enabled!");
				mainHtml(activeChar);
				return true;
			}
		}
		else if (command.equals("noticedisable"))
		{
			if (activeChar.isClanLeader())
			{
				activeChar.getClan().setNoticeEnabled(false);
				activeChar.getClan().setNoticeEnabledAndStore(false);
				activeChar.getClan().getClanId();
				activeChar.sendMessage("Clan notice disabled!");
				mainHtml(activeChar);
				return true;
			}
		}
		else if (command.startsWith("setmes"))
		{
			if (activeChar.isClanLeader())
			{
				String notice = "";
				StringTokenizer s = new StringTokenizer(command);
				s.nextToken();
				try
				{
					while (s.hasMoreTokens())
					{
						notice = notice + s.nextToken() + " ";
					}
					activeChar.getClan().setNotice(notice);
					mainHtml(activeChar);
					return true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					
					return true;
				}
			}
			activeChar.sendMessage("You are not a noble leader of clan");
			return false;
		}
		return true;
	}
	
	private static String getNoticetxt(Clan activeChar)
	{
		String result = "Notice Disabled";
		if (activeChar.isNoticeEnabled())
		{
			result = "Notice Enabled";
		}
		return result;
	}
	
	public static void mainHtml(Player activeChar)
	{
		String htmFile = "data/html/mods/menu/ClanNotice.htm";		
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		msg.replace("%noticed%", getNoticetxt(activeChar.getClan()));
		msg.replace("%mes%", activeChar.getClan().getNotice());
		activeChar.sendPacket(msg);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
