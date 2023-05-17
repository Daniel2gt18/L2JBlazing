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

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.LeaveWorld;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.util.CloseUtil;



public class VoicedPassword implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"password",
		"change_password"
	};
	
	public static final Logger LOGGER = Logger.getLogger(VoicedPassword.class.getName());
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		
		if(command.startsWith("password"))
			ShowHtml(activeChar);
		if (command.startsWith("change_password"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String curPass = null;
			String newPass = null;
			String repPass = null;
			try
			{
				if (st.hasMoreTokens())
				{
					curPass = st.nextToken();
					newPass = st.nextToken();
					repPass = st.nextToken();
				}
				else
				{
					activeChar.sendMessage("Please fill in all the blanks before requesting for a password change.");
					return false;
				}
				changePassword(curPass, newPass, repPass, activeChar);
			}
			catch (Exception e)
			{
			}
		}
		return true;
	}
	
	@SuppressWarnings("null")
	public static boolean changePassword(String currPass, String newPass, String repeatNewPass, Player activeChar)
	{
		if (newPass.length() < 3)
		{
			activeChar.sendMessage("The new password is too short!");
			return false;
		}
		if (newPass.length() > 16)
		{
			activeChar.sendMessage("The new password is too long!");
			return false;
		}
		if (!newPass.equals(repeatNewPass))
		{
			activeChar.sendMessage("Repeated password doesn't match the new password.");
			return false;
		}
		
		Connection con = null;
		String password = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = currPass.getBytes("UTF-8");
			raw = md.digest(raw);
			String currPassEncoded = Base64.getEncoder().encodeToString(raw);
			
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT password FROM accounts WHERE login=?");
			statement.setString(1, activeChar.getAccountName());
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				password = rset.getString("password");
			}
			rset.close();
			statement.close();
			byte[] password2 = null;
			if (currPassEncoded.equals(password))
			{
				password2 = newPass.getBytes("UTF-8");
				password2 = md.digest(password2);
				
				PreparedStatement statement2 = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?");
				statement2.setString(1, Base64.getEncoder().encodeToString(password2));
				statement2.setString(2, activeChar.getAccountName());
				statement2.executeUpdate();
				statement2.close();
				
				activeChar.sendMessage("Your password has been changed successfully! For security reasons, You will be disconnected. Please login again!");
				try
				{
					Thread.sleep(3000L);
				}
				catch (Exception e)
				{
				}
				
				activeChar.deleteMe();
				activeChar.sendPacket(new LeaveWorld());
			}
			else
			{
				activeChar.sendMessage("The current password you've inserted is incorrect! Please try again!");
				
				return password2 != null;
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("could not update the password of account: " + activeChar.getAccountName());
		}
		finally
		{
			CloseUtil.close(con);
		}
		
		return true;
	}
	
	private static void ShowHtml(Player activeChar)
	{
		String htmFile = "data/html/mods/menu/password.htm";
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		activeChar.sendPacket(msg);
	}

@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}

}
