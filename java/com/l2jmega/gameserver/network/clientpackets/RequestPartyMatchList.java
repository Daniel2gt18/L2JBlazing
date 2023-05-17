package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.model.partymatching.PartyMatchWaitingList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExPartyRoomMember;
import com.l2jmega.gameserver.network.serverpackets.PartyMatchDetail;

public class RequestPartyMatchList extends L2GameClientPacket
{
	private int _roomid;
	private int _membersmax;
	private int _lvlmin;
	private int _lvlmax;
	private int _loot;
	private String _roomtitle;
	
	@Override
	protected void readImpl()
	{
		_roomid = readD();
		_membersmax = readD();
		_lvlmin = readD();
		_lvlmax = readD();
		_loot = readD();
		_roomtitle = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_roomid > 0)
		{
			PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(_roomid);
			if (room != null)
			{
				_log.info("PartyMatchRoom #" + room.getId() + " changed by " + activeChar.getName());
				room.setMaxMembers(_membersmax);
				room.setMinLvl(_lvlmin);
				room.setMaxLvl(_lvlmax);
				room.setLootType(_loot);
				room.setTitle(_roomtitle);
				
				for (Player member : room.getPartyMembers())
				{
					if (member == null)
						continue;
					
					member.sendPacket(new PartyMatchDetail(room));
					member.sendPacket(SystemMessageId.PARTY_ROOM_REVISED);
				}
			}
		}
		else
		{
			int maxid = PartyMatchRoomList.getInstance().getMaxId();
			
			PartyMatchRoom room = new PartyMatchRoom(maxid, _roomtitle, _loot, _lvlmin, _lvlmax, _membersmax, activeChar);
			
			_log.info("PartyMatchRoom #" + maxid + " created by " + activeChar.getName());
			
			// Remove from waiting list, and add to current room
			PartyMatchWaitingList.getInstance().removePlayer(activeChar);
			PartyMatchRoomList.getInstance().addPartyMatchRoom(maxid, room);
			
			if (activeChar.isInParty())
			{
				for (Player ptmember : activeChar.getParty().getPartyMembers())
				{
					if (ptmember == null || ptmember == activeChar)
						continue;
					
					ptmember.setPartyRoom(maxid);
					
					room.addMember(ptmember);
				}
			}
			
			activeChar.sendPacket(new PartyMatchDetail(room));
			activeChar.sendPacket(new ExPartyRoomMember(room, 1));
			
			activeChar.sendPacket(SystemMessageId.PARTY_ROOM_CREATED);
			
			activeChar.setPartyRoom(maxid);
			activeChar.broadcastUserInfo();
		}
	}
}