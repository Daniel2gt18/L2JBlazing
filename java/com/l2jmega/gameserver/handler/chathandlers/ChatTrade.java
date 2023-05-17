package com.l2jmega.gameserver.handler.chathandlers;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.MapRegionTable;
import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.instancemanager.ChatGlobalManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

public class ChatTrade implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		8
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String target, String text)
	{	
		if (Config.DISABLE_CHAT && !activeChar.isGM())
		{
			activeChar.sendMessage("The chat is Temporarily unavailable.");
			return;
		}
		
		if (!activeChar.isVip() && Config.ENABLE_CHAT_VIP)
		{
			activeChar.sendMessage("You need to be a VIP to speak.");
			activeChar.sendPacket(new ExShowScreenMessage("You need to be a VIP to speak", 6000));
			
			return;
		}
		
		if (Config.ENABLE_CHAT_LEVEL && activeChar.getBaseClass() == activeChar.getActiveClass() && activeChar.getLevel() < Config.CHAT_LEVEL)
		{
			activeChar.sendMessage("You cannot chat with players until you reach level "+Config.CHAT_LEVEL+".");
			return;
		}
		
		if (Say2.isChatDisabled("all") && !activeChar.isGM())
			return;
		
		if (text.equals("Thanks for using my stuff - Elfocrash"))
			return;
		
		if (activeChar.ChatProtection(activeChar.getHWID()) && activeChar.isChatBlocked() && ((activeChar.getChatBanTimer() - 1500) > System.currentTimeMillis()))
		{
			if (((activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / (1000*60) + " minute(s).");
			else
				activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
			
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if ((activeChar.getChatGlobalTimer() - 1500) > System.currentTimeMillis())
		{
			activeChar.sendMessage("You must wait " + (activeChar.getChatGlobalTimer() - System.currentTimeMillis()) / 1000 + " seconds to use trade chat.");
			return;
		}
		
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.GLOBAL_CHAT))
			return;
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, Config.MSG_CHAT_TRADE+ activeChar.getName(), text);
		
		String convert = text.toLowerCase();
		final CreatureSay disable = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), convert);
		
		final int region = MapRegionTable.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
		
		if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("ON"))
		{
			for (Player player : World.getInstance().getPlayers())
			{
				if (region == MapRegionTable.getInstance().getMapRegion(player.getX(), player.getY()))
					
					if (Config.DISABLE_CAPSLOCK && !activeChar.isGM())
						player.sendPacket(disable);
					else
						player.sendPacket(cs);
				
				if (!activeChar.isGM() && Config.CUSTOM_GLOBAL_CHAT_TIME > 0)
				{
					activeChar.setChatGlobalTimer(System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
					if (!ChatGlobalManager.getInstance().hasChatPrivileges(activeChar.getObjectId()))
						ChatGlobalManager.getInstance().addChatTime(activeChar.getObjectId(), System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
				}
			}
		}
		else if (Config.DEFAULT_TRADE_CHAT.equalsIgnoreCase("GLOBAL"))
		{
			if (Config.TRADE_CHAT_WITH_PVP)
			{
				if ((activeChar.getPvpKills() < Config.TRADE_PVP_AMOUNT) && !activeChar.isGM() && !activeChar.isVip() && Config.ENABLE_CHAT_VIP)
				{
					activeChar.sendMessage("You must have at least " + Config.TRADE_PVP_AMOUNT + " pvp kills in order to speak in trade chat");
					return;
				}
				
				for (Player player : World.getInstance().getPlayers())
				{
					if (Config.DISABLE_CAPSLOCK && !activeChar.isGM())
						player.sendPacket(disable);
					else
						player.sendPacket(cs);
					
					if (!activeChar.isGM() && Config.CUSTOM_GLOBAL_CHAT_TIME > 0)
					{
						activeChar.setChatGlobalTimer(System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
						if (!ChatGlobalManager.getInstance().hasChatPrivileges(activeChar.getObjectId()))
							ChatGlobalManager.getInstance().addChatTime(activeChar.getObjectId(), System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
					}
				}
				
			}
			else
			{
				for (Player player : World.getInstance().getPlayers())
				{
					if (Config.DISABLE_CAPSLOCK && !activeChar.isGM())
						player.sendPacket(disable);
					else
						player.sendPacket(cs);
					
					if (!activeChar.isGM() && Config.CUSTOM_GLOBAL_CHAT_TIME > 0)
					{
						activeChar.setChatGlobalTimer(System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
						if (!ChatGlobalManager.getInstance().hasChatPrivileges(activeChar.getObjectId()))
							ChatGlobalManager.getInstance().addChatTime(activeChar.getObjectId(), System.currentTimeMillis() + Config.CUSTOM_GLOBAL_CHAT_TIME * 1000);
					}
				}
				
			}
		}
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}