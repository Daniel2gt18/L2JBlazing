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
package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.Config;
import com.l2jmega.gameserver.instancemanager.SoloZoneManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;

public class L2SoloZone extends L2SpawnZone
{
	public L2SoloZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(Creature character)
	{ 	
		character.setInsideZone(ZoneId.SOLO_CUSTOM, true);
		
		 if (character instanceof Player) {
		   Player activeChar = (Player)character;
          
		   if(!activeChar.isPhantom())
			SoloZoneManager.getInstance().checkPlayersArea_ip(activeChar, Integer.valueOf(2), World.getInstance().getPlayers(), Boolean.valueOf(true)); 

			
			if (Config.SOLOZONE_HWID_PROTECT && !activeChar.isPhantom())
				MaxPlayersOnArea(activeChar);
		 }
		 character.sendMessage("You have entered a Solo Zone!");
	}
	
	public boolean MaxPlayersOnArea(Player activeChar)
	{
		return SoloZoneManager.getInstance().checkPlayersArea(activeChar, Config.MAX_BOX_IN_SOLOZONE, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.SOLO_CUSTOM, false);
		character.sendMessage("You have left a Solo Zone!");
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
}