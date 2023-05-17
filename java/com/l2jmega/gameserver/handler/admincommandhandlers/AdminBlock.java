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
package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.instancemanager.ChatBanManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Lan-06
 */
public class AdminBlock implements IAdminCommandHandler
{
	private static String[] ADMIN_COMMANDS =
	{
		"admin_chat_hwid",
		"admin_chat_today"
	};
	
	protected static final Logger _log = Logger.getLogger(AdminBlock.class.getName());
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_chat_hwid"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String plyr = st.nextToken();
				Player player = World.getInstance().getPlayer(plyr);
				if (player != null)
				{
					addBlockChat(activeChar, player, 4);
				}
			}
		}
		else if (command.startsWith("admin_chat_today"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String plyr = st.nextToken();
				Player player = World.getInstance().getPlayer(plyr);
				if (player != null)
				{
					addBlockChat(activeChar, player, 12);
				}
			}
		}
		
		return true;
	}
	
	private static void addBlockChat(Player activeChar, Player targetChar, int duration)
	{
		String hwid = targetChar.getHWID();
		
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (ChatBanManager.getInstance().hasBlockPrivileges(targetChar.getObjectId()) || targetChar.isChatBlocked())
		{
			for (Player player : World.getInstance().getPlayers())
			{
				String hwidz = player.getHWID();
				
				if (player.isOnline() && !player.isPhantom())
				{
					if (hwidz.equals(hwid))
					{
						player.setChatBlock(false);
						player.sendChatMessage(0, Say2.ALL, "SYS", "A restricao de Chat foi removida do seu Computador");
					}
					
				}
			}
			
			ChatBanManager.getInstance().removeBlock(targetChar.getObjectId());
			activeChar.sendMessage("SYS: Voce removeu a restricao Chat Block HWID do Jogador " + targetChar.getName());
			listCharacters(activeChar, 0);
			return;
		}
		
		ChatBanManager.getInstance().addBlock(targetChar.getObjectId(), System.currentTimeMillis() + duration * 3600000, hwid, targetChar.getAccountName(), targetChar.getName());
		
		activeChar.sendMessage("SYS: Voce bloqueou o Chat HWID do Jogador " + targetChar.getName() + ".");
		
		for (Player player : World.getInstance().getPlayers())
		{
			String hwidz = player.getHWID();
			
			if (player.isOnline() && !player.isPhantom())
			{
				if (hwidz.equals(hwid))
				{
					player.BlockChatInfo();
					player.setChatBlock(true);
					player.setChatBanTimer(System.currentTimeMillis() + duration * 3600000);
				}
			}
		}
		
		listCharacters(activeChar, 0);
		
	}
	
	private static void listCharacters(Player activeChar, int page)
	{
		final Collection<Player> allPlayers = World.getInstance().getPlayers();
		final int playersCount = allPlayers.size();
		
		final Player[] players = allPlayers.toArray(new Player[playersCount]);
		
		final int maxCharactersPerPage = 20;
		
		int maxPages = playersCount / maxCharactersPerPage;
		if (playersCount > maxCharactersPerPage * maxPages)
			maxPages++;
		
		// Check if number of users changed
		if (page > maxPages)
			page = maxPages;
		
		int charactersStart = maxCharactersPerPage * page;
		int charactersEnd = playersCount;
		
		if (charactersEnd - charactersStart > maxCharactersPerPage)
			charactersEnd = charactersStart + maxCharactersPerPage;
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setFile("data/html/admin/charlist.htm");
		StringBuilder replyMSG = new StringBuilder();
		
		for (int x = 0; x < maxPages; x++)
		{
			int pagenr = x + 1;
			replyMSG.append("<a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a>");
		}
		adminReply.replace("%pages%", replyMSG.toString());
		replyMSG.setLength(0);
		
		// Add player info into new Table row
		for (int i = charactersStart; i < charactersEnd; i++)
		{
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info " + players[i].getName() + "\">" + players[i].getName() + "</a></td><td width=110>" + players[i].getTemplate().getClassName() + "</td><td width=40>" + players[i].getLevel() + "</td></tr>");
		}
		adminReply.replace("%players%", replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * @return
	 */
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
