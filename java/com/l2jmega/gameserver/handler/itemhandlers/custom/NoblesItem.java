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
package com.l2jmega.gameserver.handler.itemhandlers.custom;

import java.util.logging.Logger;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Mayke
 */
public class NoblesItem implements IItemHandler
{
	
	protected static final Logger LOGGER = Logger.getLogger(NoblesItem.class.getName());
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("This Item Cannot Be Used On Olympiad Games.");
			return;
		}
		
		if (activeChar.isNoble())
		{
			activeChar.sendMessage("[Noble System]: Voce ja possue o status Nobles.");
		}
		else
		{
			activeChar.setNoble(true, true);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			activeChar.broadcastUserInfo();
			activeChar.sendPacket(new ExShowScreenMessage("Congratulations. You became Noble.", 6000, 0x02, true));			
		}
		activeChar = null;
	}
}
