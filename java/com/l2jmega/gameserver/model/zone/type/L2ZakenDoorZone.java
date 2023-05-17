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
 * this program. If not, see <[url="http://www.gnu.org/licenses/>."]http://www.gnu.org/licenses/>.[/url]
 */
package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2ZoneType;

public class L2ZakenDoorZone extends L2ZoneType
{
	public L2ZakenDoorZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			if (!DoorTable.getInstance().getDoor(Integer.valueOf(21240006)).isOpened())
			{
				((Player)character).teleToLocation(83597, 147888, -3405, 0);
				((Player)character).broadcastUserInfo();
			}
		}
	}

	@Override
	protected void onExit(Creature character)
	{
		
	}

	@Override
	public void onDieInside(Creature character)
	{
		
	}

	@Override
	public void onReviveInside(Creature character)
	{
		//onEnter(character);
	}
}
