package com.l2jmega.gameserver.handler.chathandlers;



import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.model.BlockList;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class ChatTell implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		2
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (target == null)
			return;
		
		final Player receiver = World.getInstance().getPlayer(target);
		if (receiver == null || receiver.getClient().isDetached() && !receiver.isPhantom())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return;
		}
		
		if (receiver.getClient().isDetached() && !receiver.isPhantom())
		{
			activeChar.sendMessage("Player is in offline mode.");
			return;
		}
		
		if (Config.ENABLE_CHAT_LEVEL && activeChar.getBaseClass() == activeChar.getActiveClass() && activeChar.getLevel() < Config.CHAT_LEVEL)
		{
			activeChar.sendMessage("You cannot chat with players until you reach level "+Config.CHAT_LEVEL+".");
			return;
		}
		
		if (activeChar.equals(receiver))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		if (activeChar.ChatProtection(activeChar.getHWID()) && activeChar.isChatBlocked() && ((activeChar.getChatBanTimer() - 1500) > System.currentTimeMillis()))
		{
			if (((activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s).");
			else
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
			
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (receiver.isInJail() || receiver.isChatBanned())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_CHAT_BANNED);
			return;
		}
		
		if (!activeChar.isGM() && (receiver.isInRefusalMode() || receiver.getMessageRefusal() || BlockList.isBlocked(receiver, activeChar)))
		{
			activeChar.sendPacket(SystemMessageId.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			return;
		}
		
		if (receiver.isPhantom())
		{
			for (Player ppl : World.getAllGMs())
			{
				if (ppl != null && ppl.isOnline())
					ppl.sendPacket(new CreatureSay(activeChar.getObjectId(), type, "(Chat)" + activeChar.getName(), text));
			}
		}
		
		receiver.sendPacket(new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text));
		activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), type, "->" + receiver.getName(), text));
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}