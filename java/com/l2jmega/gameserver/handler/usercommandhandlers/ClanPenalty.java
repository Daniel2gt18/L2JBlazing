package com.l2jmega.gameserver.handler.usercommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.l2jmega.commons.lang.StringUtil;

import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.handler.IUserCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Support for clan penalty user command.
 * @author Tempy
 */
public class ClanPenalty implements IUserCommandHandler
{
	private static final String NO_PENALTY = "<tr><td width=170>No penalty is imposed.</td><td width=100 align=center></td></tr>";
	
	private static final int[] COMMAND_IDS =
	{
		100
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final StringBuilder sb = new StringBuilder();
		
		// Join a clan penalty.
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			StringUtil.append(sb, "<tr><td width=170>Unable to join a clan.</td><td width=100 align=center>", sdf.format(activeChar.getClanJoinExpiryTime()), "</td></tr>");
		
		// Create a clan penalty.
		if (activeChar.getClanCreateExpiryTime() > System.currentTimeMillis())
			StringUtil.append(sb, "<tr><td width=170>Unable to create a clan.</td><td width=100 align=center>", sdf.format(activeChar.getClanCreateExpiryTime()), "</td></tr>");
		
		final Clan clan = activeChar.getClan();
		if (clan != null)
		{
			// Invitation in a clan penalty.
			if (clan.getCharPenaltyExpiryTime() > System.currentTimeMillis())
				StringUtil.append(sb, "<tr><td width=170>Unable to invite a clan member.</td><td width=100 align=center>", sdf.format(clan.getCharPenaltyExpiryTime()), "</td></tr>");
			
			// War penalty.
			if (!clan.getWarPenalty().isEmpty())
			{
				for (Map.Entry<Integer, Long> entry : clan.getWarPenalty().entrySet())
				{
					if (entry.getValue() > System.currentTimeMillis())
					{
						final Clan enemyClan = ClanTable.getInstance().getClan(entry.getKey());
						if (enemyClan != null)
							StringUtil.append(sb, "<tr><td width=170>Unable to attack ", enemyClan.getName(), " clan.</td><td width=100 align=center>", sdf.format(entry.getValue()), "</td></tr>");
					}
				}
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/clan_penalty.htm");
		html.replace("%content%", (sb.length() == 0) ? NO_PENALTY : sb.toString());
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}