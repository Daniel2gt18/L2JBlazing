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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedRepair implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"showrepair",
		"repair"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("showrepair"))
		{
			showRepairWindow(activeChar);
		}
		else if (command.startsWith("repair"))
		{
			if (command.equals("repair"))
			{
				activeChar.sendMessage("Please first select character to be repaired.");
				return false;
			}
			
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String repairChar = st.nextToken();
			if (repairChar == null || repairChar.equals(""))
			{
				activeChar.sendMessage("Please first select character to be repaired.");
				return false;
			}
			
			if (checkAcc(activeChar, repairChar))
			{
				if (checkChar(activeChar, repairChar))
				{
					activeChar.sendMessage("You cannot repair your self.");
					return false;
				}
				
				repairBadCharacter(repairChar);
				
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/repaired.htm");
				activeChar.sendPacket(html);
				
			}
			else
			{
				activeChar.sendMessage("Something went wrong. Please contact with the server's administrator.");
				return false;
			}
		}
		
		return true;
	}
	
	private static void showRepairWindow(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/repair.htm");
		html.replace("%acc_chars%", getCharList(activeChar));
		activeChar.sendPacket(html);
	}
	
	@SuppressWarnings("resource")
	private static String getCharList(Player activeChar)
	{
		String result = "";
		String repCharAcc = activeChar.getAccountName();
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?");
			statement.setString(1, repCharAcc);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				if (activeChar.getName().compareTo(rset.getString(1)) != 0)
				{
					result = result + rset.getString(1) + ";";
				}
			}
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return result;
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		return result;
	}
	
	@SuppressWarnings("null")
	private static boolean checkAcc(Player activeChar, String repairChar)
	{
		boolean result = false;
		String repCharAcc = "";
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
			statement.setString(1, repairChar);
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				repCharAcc = rset.getString(1);
			}
			rset.close();
			statement.close();
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			if (activeChar.getAccountName().compareTo(repCharAcc) != 0)
			{
				return result;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return result;
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		result = true;
		return result;
	}
	
	private static boolean checkChar(Player activeChar, String repairChar)
	{
		boolean result = false;
		if (activeChar.getName().compareTo(repairChar) == 0)
		{
			result = true;
		}
		return result;
	}
	
	private static void repairBadCharacter(String charName)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, charName);
			ResultSet rset = statement.executeQuery();
			
			int objId = 0;
			if (rset.next())
			{
				objId = rset.getInt(1);
			}
			rset.close();
			statement.close();
			if (objId == 0)
			{
				con.close();
				return;
			}
			
			statement = con.prepareStatement("UPDATE characters SET x=-114356, y=-249645, z=-2984, punish_level=2, punish_timer=15000, curHp=3500 WHERE obj_Id=?");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=? AND loc=\"PAPERDOLL\"");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			return;
		}
		catch (Exception e)
		{
			System.out.println("GameServer: could not repair character:" + e);
		}
		finally
		{
			
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}