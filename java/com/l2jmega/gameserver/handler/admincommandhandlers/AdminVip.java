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

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.instancemanager.VipManager;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.util.CloseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * @author MeGaPacK
 */
public class AdminVip implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
        "admin_setvip",
		"admin_remove_vip"
	
	};
	
	private final static Logger _log = Logger.getLogger(AdminVip.class.getName());
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 7)
			return false;
		
		final WorldObject target = activeChar.getTarget();
		if (target == null || !(target instanceof Player))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		if (command.startsWith("admin_setvip"))
		{
			if (target instanceof Player)
			{
				Player targetPlayer = (Player) target;
				boolean newVip = !targetPlayer.isVip();
				
				if (newVip)
				{
					if (VipManager.getInstance().hasVipPrivileges(targetPlayer.getObjectId()))
					{
						removeVip(activeChar, targetPlayer);
					}
					
					targetPlayer.setVip(true);
					targetPlayer.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "[Vip System]", "Voce se tornou um Vip ETERNO."));
					updateDatabase(targetPlayer, true);
					targetPlayer.broadcastUserInfo();
					
				}
				else
				{
					targetPlayer.setVip(false);
					targetPlayer.sendMessage("[Vip System]: Seu Vip ETERNO foi removido.");
					updateDatabase(targetPlayer, false);

					targetPlayer.broadcastUserInfo();
				}
				
				targetPlayer = null;
			}
			else
			{
				activeChar.sendMessage("[Vip System]: Impossible to set a non Player Target as Vip.");
				_log.info("[Vip System]: GM: " + activeChar.getName() + " is trying to set a non Player Target as Vip.");
				
				return false;
			}
			
		}
		
		else if (command.equalsIgnoreCase("admin_remove_vip"))
			removeVip(activeChar, (Player) target);
		
		return true;
	}
	
	public static void removeVip(Player activeChar, Player targetChar)
	{
		if (!VipManager.getInstance().hasVipPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have Vip privileges.");
			return;
		}
		
		VipManager.getInstance().removeVip(targetChar.getObjectId());
		activeChar.sendMessage("You have removed Vip privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your Vip privileges were removed by the admin.", 10000));
		targetChar.setVip(false);
		
	}
	
	/**
	 * @param player
	 * @param newVip
	 */
	public static void updateDatabase(Player player, boolean newVip)
	{
		// prevents any NPE.
		if (player == null)
			return;
		
		Connection con = null;
		try
		{
			// Database Connection
			// --------------------------------
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(newVip ? INSERT_DATA : DEL_DATA);
			
			// if it is a new donator insert proper data
			// --------------------------------------------
			if (newVip)
			{
				
				stmt.setInt(1, player.getObjectId());
				stmt.setString(2, player.getName());
				stmt.setInt(3, 1);
				stmt.execute();
				stmt.close();
				stmt = null;
			}
			else
			// deletes from database
			{
				stmt.setInt(1, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			CloseUtil.close(con);
		}
	}

	// Updates That Will be Executed by MySQL
	// ----------------------------------------
	static String INSERT_DATA = "REPLACE INTO characters_vip_eterno (obj_Id, char_name, vip) VALUES (?,?,?)";
	static String DEL_DATA = "UPDATE characters_vip_eterno SET vip = 0 WHERE obj_Id=?";
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}