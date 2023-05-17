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
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * An arena
 * @author durgus
 */
public class L2ArenaEventZone extends L2SpawnZone
{
	public L2ArenaEventZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.ARENA_EVENT, true);
		character.setInsideZone(ZoneId.PVP, true);
		
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			
			if (player.isArenaProtection())
			{
				if (player.getPvpFlag() > 0)
					PvpFlagTaskManager.getInstance().remove(player);
				
				player.updatePvPFlag(1);
				player.broadcastUserInfo();
			}
			
			player.sendPacket(new SystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
			
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.ARENA_EVENT, false);
		character.setInsideZone(ZoneId.PVP, false);
		
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			
			player.updatePvPFlag(0);
			player.broadcastUserInfo();
			
			player.sendPacket(new SystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
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