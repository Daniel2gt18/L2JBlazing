package com.l2jmega.gameserver.handler.chathandlers;

import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class ChatPartyMatchRoom implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		14
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (!activeChar.isInPartyMatchRoom())
			return;
		
		final PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(activeChar);
		if (room == null)
			return;
		
		if (activeChar.ChatProtection(activeChar.getHWID()) && activeChar.isChatBlocked() && ((activeChar.getChatBanTimer()-1500) > System.currentTimeMillis()))
		{			
			if (((activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / (1000*60) + " minute(s).");
			else
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
			
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
	
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		for (Player member : room.getPartyMembers())
			member.sendPacket(cs);
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}