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
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * An arena
 * @author durgus
 */
public class L2CustomZone extends L2SpawnZone
{
	public L2CustomZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.CUSTOM, true);
		character.setInsideZone(ZoneId.NO_STORE, true);
		
		if (character instanceof Player)
		{
			final Player player = ((Player) character);
			
			if (player.getMountType() == 2 && !Config.WYVERN_PVPZONE)
			{
				player.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
				player.enteredNoLanding(5);
			}
			
			if (!player.isInObserverMode() && !player.isGM())
				player.getAppearance().setVisible();
			
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.CUSTOM, false);
		character.setInsideZone(ZoneId.NO_STORE, false);
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