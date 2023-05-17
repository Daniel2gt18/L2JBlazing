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

import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.clientpackets.Say2;

public class AdminChatManager implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_quiet"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_quiet"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			try
			{
				String type = st.nextToken();
				if (type.startsWith("all"))
				{
					if (!Say2.isChatDisabled("all"))
					{
						Say2.setIsChatDisabled("all", true);
						for (Player allgms : World.getInstance().getPlayers())
						{
							allgms.sendMessage("All chats have been disabled!");
						}
					}
					else
					{
						Say2.setIsChatDisabled("all", false);
						for (Player allgms : World.getInstance().getPlayers())
						{
							allgms.sendMessage("All Chats have been enabled!");
						}
						
						for (Player player : World.getInstance().getPlayers())
						{
							if (player != null)
								if (player.isOnline())
									player.sendChatMessage(0, Say2.PARTY, "SYS", "All Chats have been enabled..");
						}
					}
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage : //quiet all");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
