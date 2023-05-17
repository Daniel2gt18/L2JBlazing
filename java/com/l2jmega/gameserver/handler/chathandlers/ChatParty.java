package com.l2jmega.gameserver.handler.chathandlers;

import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class ChatParty implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		3
	};
	
	@Override
	public void handleChat(int type, Player player, String target, String text)
	{
		if (!player.isInParty())
			return;
		
		if (player.ChatProtection(player.getHWID()) && player.isChatBlocked() && ((player.getChatBanTimer() - 1500) > System.currentTimeMillis()))
		{
			if (((player.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
				player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s).");
			else
				player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
			
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		player.getParty().broadcastToPartyMembers(new CreatureSay(player.getObjectId(), type, player.getName(), text));
		
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}