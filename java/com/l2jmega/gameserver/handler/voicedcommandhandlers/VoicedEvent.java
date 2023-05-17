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

import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;


public class VoicedEvent implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"tvtjoin",
		"tvtleave",
		"ctfjoin",
		"ctfleave",
		"register",
		"unregister"

	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
	    if (command.startsWith("register") || command.startsWith("tvtjoin")) {
	        if (TvT.is_joining() || TvT.is_teleport() || TvT.is_started()) {
	          JoinTvT(activeChar);
	        } else {
	          activeChar.sendMessage("There are no events currently available");
	        } 
	      } else if (command.startsWith("unregister") || command.startsWith("tvtleave")) {
	        if (TvT.is_joining() || TvT.is_teleport() || TvT.is_started()) {
	          LeaveTvT(activeChar);
	        } else {
	          activeChar.sendMessage("There are no events currently available");
	        } 
	      }
		else if (command.startsWith("ctfjoin"))
		{
			if (CTF.is_joining() || CTF.is_teleport() || CTF.is_started())
				JoinCTF(activeChar);
			else
				activeChar.sendMessage("There are no CTF events currently available!");
		}
		else if (command.startsWith("ctfleave"))
		{
			if (CTF.is_joining() || CTF.is_teleport() || CTF.is_started())
				LeaveCTF(activeChar);
			else
				activeChar.sendMessage("There are no CTF events currently available!");
			
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	public static boolean JoinTvT(Player activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!TvT.is_joining())
		{
			activeChar.sendMessage("There are no events currently available.");
			return false;
		}
		else if (TvT.is_joining() && activeChar._inEventTvT)
		{
			activeChar.sendMessage("You are already registered.");
			return false;
		}
		else if (activeChar.isCursedWeaponEquipped())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
			return false;
		}
		else if (activeChar.isOlympiadProtection())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
			return false;
		}
		else if (activeChar.getLevel() < TvT.get_minlvl())
		{
			activeChar.sendMessage(" You are not allowed to participate to the event because your level is too low.");
			return false;
		}
		else if (activeChar.getLevel() > TvT.get_maxlvl())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because your level is too high.");
			return false;
		}
		else if (activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
			return false;
		}
		else if (TvT.is_teleport() || TvT.is_started())
		{
			activeChar.sendMessage("TvT Event registration period is over. You can't register now.");
			return false;
		}
		else
		{
			TvT.addPlayer(activeChar, "");
			return false;
		}
	}
	
	public boolean LeaveTvT(Player activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!TvT.is_joining())
		{
			activeChar.sendMessage("There are no events currently available.");
			return false;
		}
		else if ((TvT.is_teleport() || TvT.is_started()) && activeChar._inEventTvT)
		{
			activeChar.sendMessage("You can not leave now because TvT event has started.");
			return false;
		}
		else if (TvT.is_joining() && !activeChar._inEventTvT)
		{
			activeChar.sendMessage("You aren't registered in the TvT Event.");
			return false;
		}
		else
		{
			TvT.removePlayer(activeChar);
			return true;
		}
	}
	
	public static boolean JoinCTF(Player activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!CTF.is_joining())
		{
			activeChar.sendMessage("There are no events currently available.");
			return false;
		}
		else if (CTF.is_joining() && activeChar._inEventCTF)
		{
			activeChar.sendMessage("You are already registered.");
			return false;
		}
		else if (activeChar.isCursedWeaponEquipped())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are holding a Cursed Weapon.");
			return false;
		}
		else if (activeChar.isOlympiadProtection())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you are in Olympiad.");
			return false;
		}
		else if (activeChar.getLevel() < CTF.get_minlvl())
		{
			activeChar.sendMessage(" You are not allowed to participate to the event because your level is too low.");
			return false;
		}
		else if (activeChar.getLevel() > CTF.get_maxlvl())
		{
			activeChar.sendMessage("You are not allowed to participate to the event because your level is too high.");
			return false;
		}
		else if (activeChar.getKarma() > 0)
		{
			activeChar.sendMessage("You are not allowed to participate to the event because you have Karma.");
			return false;
		}
		else if (CTF.is_teleport() || CTF.is_started())
		{
			activeChar.sendMessage("CTF Event registration period is over. You can't register now.");
			return false;
		}
		else
		{
			CTF.addPlayer(activeChar, "");
			return false;
		}
	}
	
	public boolean LeaveCTF(Player activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (!CTF.is_joining())
		{
			activeChar.sendMessage("There are no events currently available.");
			return false;
		}
		else if ((CTF.is_teleport() || CTF.is_started()) && activeChar._inEventCTF)
		{
			activeChar.sendMessage("You can not leave now because CTF event has started.");
			return false;
		}
		else if (CTF.is_joining() && !activeChar._inEventCTF)
		{
			activeChar.sendMessage("You aren't registered in the CTF Event.");
			return false;
		}
		else
		{
			CTF.removePlayer(activeChar);
			return true;
		}
	}
}