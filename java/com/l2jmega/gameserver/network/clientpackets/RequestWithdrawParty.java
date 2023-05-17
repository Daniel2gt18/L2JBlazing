package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Party.MessageType;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.network.serverpackets.ExClosePartyRoom;
import com.l2jmega.gameserver.network.serverpackets.ExPartyRoomMember;
import com.l2jmega.gameserver.network.serverpackets.PartyMatchDetail;

public final class RequestWithdrawParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final L2Party party = player.getParty();
		if (party == null)
			return;
		
		if (player.isArenaProtection())
		{
			player.sendMessage("You can't exit party in Tournament Event.");
			return;
		}
		
		party.removePartyMember(player, MessageType.Left);
		
		if (player.isInPartyMatchRoom())
		{
			PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
			if (room != null)
			{
				player.sendPacket(new PartyMatchDetail(room));
				player.sendPacket(new ExPartyRoomMember(room, 0));
				player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
				
				room.deleteMember(player);
			}
			player.setPartyRoom(0);
			player.broadcastUserInfo();
		}
	}
}