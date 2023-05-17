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

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * An arena
 * @author durgus
 */
public class L2TournamentZone extends L2SpawnZone
{
	public L2TournamentZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.PEACE, true);
		character.setInsideZone(ZoneId.TOURNAMENT, true);
		
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			player.setTournamentTeleport(true);
			if (!player.isOlympiadProtection())
			{
				player.sendMessage("You have entered a Tournament zone.");
				player.getAppearance().setInvisible();
				player.broadcastUserInfo();
			}
			
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.TOURNAMENT, false);
		character.setInsideZone(ZoneId.PEACE, false);
		
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			
			if (!player.isArenaProtection() && !player.isOlympiadProtection())
			{
				player.setTournamentTeleport(false);
				
			if (!player.isInObserverMode() && !player.isOlympiadProtection() && !player.isGM())
					player.getAppearance().setVisible();
							
				player.sendMessage("You left a Tournament zone.");
				player.broadcastUserInfo();
			}
			
		}
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