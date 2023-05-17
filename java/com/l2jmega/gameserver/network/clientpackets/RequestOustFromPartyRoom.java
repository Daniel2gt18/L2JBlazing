package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.data.MapRegionTable;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.model.partymatching.PartyMatchWaitingList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExClosePartyRoom;
import com.l2jmega.gameserver.network.serverpackets.PartyMatchList;

public final class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _charid;
	
	@Override
	protected void readImpl()
	{
		_charid = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Player member = World.getInstance().getPlayer(_charid);
		if (member == null)
			return;
		
		final PartyMatchRoom _room = PartyMatchRoomList.getInstance().getPlayerRoom(member);
		if (_room == null)
			return;
		
		if (_room.getOwner() != activeChar)
			return;
		
		if (activeChar.isInParty() && member.isInParty() && activeChar.getParty().getPartyLeaderOID() == member.getParty().getPartyLeaderOID())
			activeChar.sendPacket(SystemMessageId.CANNOT_DISMISS_PARTY_MEMBER);
		else
		{
			_room.deleteMember(member);
			member.setPartyRoom(0);
			
			// Close the PartyRoom window
			member.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			
			// Add player back on waiting list
			PartyMatchWaitingList.getInstance().addPlayer(member);
			
			// Send Room list
			member.sendPacket(new PartyMatchList(member, 0, MapRegionTable.getInstance().getClosestLocation(member.getX(), member.getY()), member.getLevel()));
			
			// Clean player's LFP title
			member.broadcastUserInfo();
			
			member.sendPacket(SystemMessageId.OUSTED_FROM_PARTY_ROOM);
		}
	}
}