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
package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;


public class VoicedColor implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"colormanager",
		"namecolor",
		"titlecolor"
	};
	public static final Logger _log = Logger.getLogger(VoicedColor.class.getName());
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if ((activeChar._inEventTvT && (TvT.is_started() || TvT.is_teleport())) || (activeChar._inEventCTF && (CTF.is_started() || CTF.is_teleport())))
		{
			activeChar.sendMessage("You can not do this in TvT Event!");
			return false;
		}
		else if (command.equalsIgnoreCase("colormanager"))
			color_html(activeChar);
		else if (command.startsWith("namecolor"))
		{
			if (!Config.COLOR_WITH_ITEM)
			{
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.ALL, activeChar.getName(), "." + command);
				activeChar.sendPacket(cs);
				return false;
			}
			
			if (activeChar.isVip() || activeChar.destroyItemByItemId("NameColor", Config.COLOR_ITEM_ID, Config.COLOR_NAME_ITEM_AMOUNT, null, true))
				nameColor(command, activeChar);
			
			color_html(activeChar);
		}
		else if (command.startsWith("titlecolor"))
		{
			if (!Config.COLOR_WITH_ITEM)
			{
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.ALL, activeChar.getName(), "." + command);
				activeChar.sendPacket(cs);
				return false;
			}
			
			if (activeChar.isVip() || activeChar.destroyItemByItemId("TitleColor", Config.COLOR_ITEM_ID, Config.COLOR_TITLE_ITEM_AMOUNT, null, true))
				titleColor(command, activeChar);
			
			color_html(activeChar);
		}
		return false;
	}
	
	private static void nameColor(String command, Player player)
	{
		try
		{
			String nameColor = command.substring(10);
			player.getAppearance().setNameColor(Integer.decode(nameColor));
			
			player.sendMessage("A cor do seu nome foi alterado!");
			player.broadcastUserInfo();
			
			color_html(player);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(NAME_UPDATE);
				statement.setString(1, nameColor);
				statement.setString(2, player.getName());
				statement.setInt(3, player.getObjectId());
				statement.executeUpdate();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "nameColor", e);
			}
			
		}
		catch (IndexOutOfBoundsException e)
		{
		}
	}
	
	private static void titleColor(String command, Player player)
	{
		try
		{
			String titleColor = command.substring(11);
			player.getAppearance().setTitleColor(Integer.decode(titleColor));
			
			player.sendMessage("A cor do seu title foi alterado!");
			player.broadcastUserInfo();
			
			color_html(player);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(TITLE_UPDATE);
				statement.setString(1, titleColor);
				statement.setString(2, player.getName());
				statement.setInt(3, player.getObjectId());
				statement.executeUpdate();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "titleColor", e);
			}
			
		}
		catch (IndexOutOfBoundsException e)
		{
		}
	}
	
	static String NAME_UPDATE = "UPDATE characters SET color_name=?, char_name=? WHERE obj_Id=?";
	
	static String TITLE_UPDATE = "UPDATE characters SET color_title=?, char_name=? WHERE obj_Id=?";
	
	private static void color_html(Player activeChar)
	{
		String itemName = ItemTable.getInstance().getTemplate(Config.COLOR_ITEM_ID).getName();
		String htmFile = "data/html/mods/menu/ColorManager.htm";
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		msg.replace("%player%", activeChar.getName());
		
		if (activeChar.isVip())
			msg.replace("%name%", "Free /");
		else
			msg.replace("%name%", String.valueOf(Config.COLOR_NAME_ITEM_AMOUNT));
		
		if (activeChar.isVip())
			msg.replace("%item%", "Gratis");
		else
			msg.replace("%item%", itemName);
		
		if (activeChar.isVip())
			msg.replace("%title%", "Free /");
		else
			msg.replace("%title%", String.valueOf(Config.COLOR_TITLE_ITEM_AMOUNT));
		
		activeChar.sendPacket(msg);
	}
	
	public boolean useVoicedCommand(String command, String all, Player activeChar, String text)
	{
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
