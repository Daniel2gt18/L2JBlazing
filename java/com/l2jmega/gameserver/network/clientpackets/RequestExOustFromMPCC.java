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
package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * @author -Wooden-
 */
public final class RequestExOustFromMPCC extends L2GameClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_name);
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return;
		}
		
		if (activeChar.equals(target))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final L2Party playerParty = activeChar.getParty();
		final L2Party targetParty = target.getParty();
		
		if (playerParty != null && playerParty.isInCommandChannel() && targetParty != null && targetParty.isInCommandChannel() && playerParty.getCommandChannel().getChannelLeader().equals(activeChar))
		{
			targetParty.getCommandChannel().removeParty(targetParty);
			targetParty.broadcastToPartyMembers(SystemMessage.getSystemMessage(SystemMessageId.DISMISSED_FROM_COMMAND_CHANNEL));
			
			// check if CC has not been canceled
			if (playerParty.isInCommandChannel())
				playerParty.getCommandChannel().broadcastToChannelMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_PARTY_DISMISSED_FROM_COMMAND_CHANNEL).addCharName(targetParty.getLeader()));
		}
		else
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
	}
}