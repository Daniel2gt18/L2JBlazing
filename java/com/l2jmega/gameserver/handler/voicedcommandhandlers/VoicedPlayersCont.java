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

import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;

public class VoicedPlayersCont implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"playerscont"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("playerscont"))
		{
			if (!activeChar.isGM())
				return false;
			
			if (activeChar.getAccessLevel().getLevel() < 7)
				return false;
			
			if (activeChar.isPlayerCont())
			{
				activeChar.setPlayerCont(false);
				activeChar.sendMessage("SYS: Aguarde um momento..");
			}
			else
			{
				activeChar.setPlayerCont(true);
				activeChar.startContPlayers();
				activeChar.sendMessage("SYS: Aguarde um momento..");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}