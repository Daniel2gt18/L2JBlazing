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

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author rapfersan92
 */
public class ClassItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player activeChar = (Player) playable;
		
		if (activeChar.isOlympiadProtection())
		{
			activeChar.sendMessage("You can not do that.");
			return;
		}
	
		activeChar.setClassChangeItemId(item.getItemId());
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/Coin Custom/aviso.htm");
		activeChar.sendPacket(html);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
