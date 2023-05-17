package com.l2jmega.gameserver.handler.chathandlers;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.handler.VoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

public class ChatAll implements IChatHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void handleChat(int type, Player activeChar, String params, String text)
	{
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.GLOBAL_CHAT))
			return;
		
		boolean vcd_used = false;
		if (text.startsWith("."))
		{
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";
			
			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				params = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			}
			else
			{
				command = text.substring(1);
				vch = VoicedCommandHandler.getInstance().getHandler(command);
			}
			
			if (vch != null)
				vch.useVoicedCommand(command, activeChar, params);
			else
				activeChar.sendMessage("Command not found.");
			
			vcd_used = true;			
		}
		
		if (!vcd_used)
		{
			if (activeChar.ChatProtection(activeChar.getHWID()) && activeChar.isChatBlocked() && ((activeChar.getChatBanTimer() - 1500) > System.currentTimeMillis()))
			{
				if (((activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000) >= 60)
					activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s).");
				else
					activeChar.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (activeChar.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
				
				activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			else if (text.equals("Thanks for using my stuff - Elfocrash"))
				return;
			else if (activeChar.isInArenaEvent() && !activeChar.isArenaAttack())
			{
				activeChar.sendMessage("Wait for the battle to begin..");
				return;
			}
			else if (activeChar.isEventObserver())
			{
				activeChar.sendMessage("You can not do that...");
				return;
			}
			else if (Config.ENABLE_CHAT_LEVEL && activeChar.getBaseClass() == activeChar.getActiveClass() && activeChar.getLevel() < Config.CHAT_LEVEL)
			{
				activeChar.sendMessage("You cannot chat with players until you reach level "+Config.CHAT_LEVEL+".");
				return;
			}
			else if ((activeChar.getChatAllTimer() - 1500) > System.currentTimeMillis() && Config.TALK_CHAT_ALL_CONFIG)
			{
				if (((activeChar.getChatAllTimer() - System.currentTimeMillis()) / 1000) >= 60)
				{
					activeChar.sendMessage("You need " + Config.TALK_CHAT_ALL_TIME + " minutes online to talk on chat. Finish in " + (activeChar.getChatAllTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s).");
					activeChar.sendPacket(new ExShowScreenMessage("You can talk in " + (activeChar.getChatAllTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s)", 5 * 1000));
				}
				else
				{
					activeChar.sendMessage("You need " + Config.TALK_CHAT_ALL_TIME + " minutes online to talk on chat. Finish in " + (activeChar.getChatAllTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
					activeChar.sendPacket(new ExShowScreenMessage("You can talk in " + (activeChar.getChatAllTimer() - System.currentTimeMillis()) / 1000 + " second(s)", 5 * 1000));
				}
				return;
			}
			
			CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
			
			for (Player player : activeChar.getKnownTypeInRadius(Player.class, 1250))
			{
				player.sendPacket(cs);
			}
			
			activeChar.sendPacket(cs);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static double arredondaValor(int casasDecimais, double valor)
	{
		BigDecimal decimal = new BigDecimal(valor);
		return decimal.setScale(casasDecimais, 3).doubleValue();
	}
	
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}