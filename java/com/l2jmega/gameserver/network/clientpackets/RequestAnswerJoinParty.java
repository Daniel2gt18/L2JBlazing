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

import com.l2jmega.gameserver.model.L2Party.MessageType;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExManagePartyRoomMember;
import com.l2jmega.gameserver.network.serverpackets.JoinParty;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * format cdd
 */
public final class RequestAnswerJoinParty extends L2GameClientPacket
{
	private int _response;
	
	@Override
	protected void readImpl()
	{
		_response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
			return;
		
		requestor.sendPacket(new JoinParty(_response));
		
		if (_response == 1)
		{
			if (requestor.isInParty())
			{
				if (requestor.getParty().getMemberCount() >= 9)
				{
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_FULL);
					player.sendPacket(sm);
					requestor.sendPacket(sm);
					return;
				}
			}
			player.joinParty(requestor.getParty());
			
			if (requestor.isInPartyMatchRoom() && player.isInPartyMatchRoom())
			{
				final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
				if (list != null && (list.getPlayerRoomId(requestor) == list.getPlayerRoomId(player)))
				{
					final PartyMatchRoom room = list.getPlayerRoom(requestor);
					if (room != null)
					{
						final ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
						for (Player member : room.getPartyMembers())
						{
							if (member != null)
								member.sendPacket(packet);
						}
					}
				}
			}
			else if (requestor.isInPartyMatchRoom() && !player.isInPartyMatchRoom())
			{
				final PartyMatchRoomList list = PartyMatchRoomList.getInstance();
				if (list != null)
				{
					final PartyMatchRoom room = list.getPlayerRoom(requestor);
					if (room != null)
					{
						room.addMember(player);
						ExManagePartyRoomMember packet = new ExManagePartyRoomMember(player, room, 1);
						for (Player member : room.getPartyMembers())
						{
							if (member != null)
								member.sendPacket(packet);
						}
						player.setPartyRoom(room.getId());
						player.broadcastUserInfo();
					}
				}
			}
		}
		else
		{
			// activate garbage collection if there are no other members in party (happens when we were creating new one)
			if (requestor.isInParty() && requestor.getParty().getMemberCount() == 1)
				requestor.getParty().removePartyMember(requestor, MessageType.None);
		}
		
		if (requestor.isInParty())
			requestor.getParty().setPendingInvitation(false);
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}