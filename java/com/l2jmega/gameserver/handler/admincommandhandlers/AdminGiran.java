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

import java.util.logging.Logger;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;

/**
 * @author Mayke
 */
public class AdminGiran implements IAdminCommandHandler
{
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_giran"
	};
	
	protected static final Logger _log = Logger.getLogger(AdminGiran.class.getName());
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		
		if ((activeChar.getTarget() == null))
		{
			activeChar.sendMessage("Select a target");
			return false;
		}
		
		WorldObject target = activeChar.getTarget();
		
		if (!(target instanceof Player))
		{
			activeChar.sendMessage("Target need to be player");
			return false;
		}
		
		Player player = activeChar.getTarget().getActingPlayer();
		
		if (command.equals("admin_giran"))
		{
			player.teleToLocation(82840, 147996, -3469, 50); // Giran
			activeChar.sendMessage("O Jogador " + player.getName() + " foi teleportado pra giran");
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
