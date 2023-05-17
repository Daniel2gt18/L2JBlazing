package com.l2jmega.gameserver.handler.chathandlers;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.instancemanager.ChatHeroManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class ChatHeroVoice implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		17
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{
		if (!activeChar.isGM() && (Config.DISABLE_CHAT))
		{
			activeChar.sendMessage("The chat is Temporarily unavailable.");
			return;
			
		}
		
        if (Say2.isChatDisabled("all") && !activeChar.isGM())				
			return;

		if (text.equals("Thanks for using my stuff - Elfocrash"))
			return;
		
		if (activeChar.ChatProtection(activeChar.getHWID()) && activeChar.isChatBlocked()&& ((activeChar.getChatBanTimer()-1500) > System.currentTimeMillis()))
		{
			if (((activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / (1000*60) + " minute(s).");
			else
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
			
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (!activeChar.isHero() && !activeChar.isGM())
			return;
		
		if ((activeChar.getChatHeroTimer()-1500) > System.currentTimeMillis())
		{
			activeChar.sendMessage("You must wait " + (activeChar.getChatHeroTimer() - System.currentTimeMillis()) / 1000 + " seconds to use Hero chat.");
			return;
		}
		
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.HERO_VOICE))
			return;
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		
		String convert = text.toLowerCase();
		final CreatureSay disable = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), convert);
		
		for (Player player : World.getInstance().getPlayers())
			if (Config.DISABLE_CAPSLOCK && !activeChar.isGM())
				player.sendPacket(disable);
			else
				player.sendPacket(cs);
		
		if (!activeChar.isGM() && Config.CUSTOM_HERO_CHAT_TIME > 0)
		{
			activeChar.setChatHeroTimer(System.currentTimeMillis() + Config.CUSTOM_HERO_CHAT_TIME * 1000);
			if (!ChatHeroManager.getInstance().hasChatPrivileges(activeChar.getObjectId()))
				ChatHeroManager.getInstance().addChatTime(activeChar.getObjectId(), System.currentTimeMillis() + Config.CUSTOM_HERO_CHAT_TIME * 1000);
		}
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}