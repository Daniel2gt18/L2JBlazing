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
package com.l2jmega.gameserver.handler.itemhandlers.vip;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminVip;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

import java.util.logging.Logger;

/**
 * @author MeGaPacK
 */
public class VipEterno implements IItemHandler
{
	
	protected static final Logger LOGGER = Logger.getLogger(VipEterno.class.getName());
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("SYS: Voce nao pode fazer isso.");
			return;
		}
		
		if (activeChar.isVip())
		{
			activeChar.sendMessage("SYS: Voce ja esta com status Vip.");
			return;
		}
		
		playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		activeChar.setVip(true);
		activeChar.setNoble(true, true);
		AdminVip.updateDatabase(activeChar, true);
		
		activeChar.broadcastUserInfo();
		
		for (Player allgms : World.getAllGMs())
			allgms.sendPacket(new CreatureSay(0, Say2.SHOUT, "(Vip Manager)", activeChar.getName() + " ativou Vip Eterno."));

	}
	
}
