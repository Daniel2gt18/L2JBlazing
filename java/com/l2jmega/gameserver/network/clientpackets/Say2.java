package com.l2jmega.gameserver.network.clientpackets;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.ChatHandler;
import com.l2jmega.gameserver.handler.IChatHandler;
import com.l2jmega.gameserver.instancemanager.ChatBanManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.PunishLevel;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public final class Say2 extends L2GameClientPacket
{
	private static final Logger CHAT_LOG = Logger.getLogger("chat");
	
	public static final int ALL = 0;
	public static final int SHOUT = 1; // !
	public static final int TELL = 2;
	public static final int PARTY = 3; // #
	public static final int CLAN = 4; // @
	public static final int GM = 5;
	public static final int PETITION_PLAYER = 6;
	public static final int PETITION_GM = 7;
	public static final int TRADE = 8; // +
	public static final int ALLIANCE = 9; // $
	public static final int ANNOUNCEMENT = 10;
	public static final int BOAT = 11;
	public static final int L2FRIEND = 12;
	public static final int MSNCHAT = 13;
	public static final int PARTYMATCH_ROOM = 14;
	public static final int PARTYROOM_COMMANDER = 15; // (Yellow)
	public static final int PARTYROOM_ALL = 16; // (Red)
	public static final int HERO_VOICE = 17;
	public static final int CRITICAL_ANNOUNCE = 18;
	
	private static final String[] CHAT_NAMES =
	{
		"ALL",
		"SHOUT",
		"TELL",
		"PARTY",
		"CLAN",
		"GM",
		"PETITION_PLAYER",
		"PETITION_GM",
		"TRADE",
		"ALLIANCE",
		"ANNOUNCEMENT", // 10
		"BOAT",
		"WILLCRASHCLIENT:)",
		"FAKEALL?",
		"PARTYMATCH_ROOM",
		"PARTYROOM_COMMANDER",
		"PARTYROOM_ALL",
		"HERO_VOICE",
		"CRITICAL_ANNOUNCEMENT"
	};
	
	private static final String[] WALKER_COMMAND_LIST =
	{
		"USESKILL",
		"USEITEM",
		"BUYITEM",
		"SELLITEM",
		"SAVEITEM",
		"LOADITEM",
		"MSG",
		"DELAY",
		"LABEL",
		"JMP",
		"CALL",
		"RETURN",
		"MOVETO",
		"NPCSEL",
		"NPCDLG",
		"DLGSEL",
		"CHARSTATUS",
		"POSOUTRANGE",
		"POSINRANGE",
		"GOHOME",
		"SAY",
		"EXIT",
		"PAUSE",
		"STRINDLG",
		"STRNOTINDLG",
		"CHANGEWAITTYPE",
		"FORCEATTACK",
		"ISMEMBER",
		"REQUESTJOINPARTY",
		"REQUESTOUTPARTY",
		"QUITPARTY",
		"MEMBERSTATUS",
		"CHARBUFFS",
		"ITEMCOUNT",
		"FOLLOWTELEPORT"
	};
	private static boolean _chatDisabled = false;
	private static boolean _globalChatDisabled = false;
	private static boolean _tradeChatDisabled = false;
	private static boolean _heroChatDisabled = false;
	
	private String _text;
	private int _type;
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_text = readS();
		_type = readD();
		_target = (_type == TELL) ? readS() : null;
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_type < 0 || _type >= CHAT_NAMES.length)
		{
			_log.warning("Say2: Invalid type: " + _type + " Player : " + activeChar.getName() + " text: " + _text);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		
		if (_text.isEmpty())
		{
			_log.warning(activeChar.getName() + ": sending empty text. Possible packet hack.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			activeChar.logout();
			return;
		}
		
		if (_text.length() > 100)
			return;
		
		if (Config.L2WALKER_PROTECTION && _type == TELL && checkBot(_text))
			return;
		
		if (!activeChar.isGM() && _type == ANNOUNCEMENT)
		{
			_log.warning(activeChar.getName() + " tried to use announcements without GM statut.");
			return;
		}
		
		if (Config.USE_SAY_FILTER && !activeChar.isGM())
			checkText(activeChar);
		
		if (activeChar.isInJail())
		{
			if (_type == TELL || _type == SHOUT || _type == TRADE || _type == HERO_VOICE)
			{
				activeChar.sendMessage("You can not chat with players outside of the jail.");
				return;
			}
		}
		
		if (_type == PETITION_PLAYER && activeChar.isGM())
			_type = PETITION_GM;
		
		if (Config.LOG_CHAT)
		{
			LogRecord record = new LogRecord(Level.INFO, _text);
			record.setLoggerName("chat");
			
			if (_type == TELL)
				record.setParameters(new Object[]
				{
					CHAT_NAMES[_type],
					"[" + activeChar.getName() + " to " + _target + "]"
				});
			else
				record.setParameters(new Object[]
				{
					CHAT_NAMES[_type],
					"[" + activeChar.getName() + "]"
				});
			
			CHAT_LOG.log(record);
		}
		
		_text = _text.replaceAll("\\\\n", "");
		
		IChatHandler handler = ChatHandler.getInstance().getChatHandler(_type);
		if (handler != null)
			handler.handleChat(_type, activeChar, _target, _text);
		else
			_log.warning(activeChar.getName() + " tried to use unregistred chathandler type: " + _type + ".");
	}
	
	private static boolean checkBot(String text)
	{
		for (String botCommand : WALKER_COMMAND_LIST)
		{
			if (text.startsWith(botCommand))
				return true;
		}
		return false;
	}
	
	public static boolean isChatDisabled(String chatType)
	{
		boolean chatDisabled = false;
		
		if (chatType.equalsIgnoreCase("all"))
			chatDisabled = _chatDisabled;
		else if (chatType.equalsIgnoreCase("hero"))
			chatDisabled = _heroChatDisabled;
		else if (chatType.equalsIgnoreCase("trade"))
			chatDisabled = _tradeChatDisabled;
		else if (chatType.equalsIgnoreCase("global"))
			chatDisabled = _globalChatDisabled;
		
		return chatDisabled;
	}
	
	public static void setIsChatDisabled(String chatType, boolean chatDisabled)
	{
		if (chatType.equalsIgnoreCase("all"))
			_chatDisabled = chatDisabled;
		else if (chatType.equalsIgnoreCase("hero"))
			_heroChatDisabled = chatDisabled;
		else if (chatType.equalsIgnoreCase("trade"))
			_tradeChatDisabled = chatDisabled;
		else if (chatType.equalsIgnoreCase("global"))
			_globalChatDisabled = chatDisabled;
	}
	
	private void checkText(Player activeChar)
	{
		if (Config.USE_SAY_FILTER)
		{
			String filteredText = _text.toLowerCase();
			
			for (String pattern : Config.FILTER_LIST)
			{
				filteredText = filteredText.replaceAll("(?i)" + pattern, Config.CHAT_FILTER_CHARS);
			}
			
			if (!filteredText.equalsIgnoreCase(_text))
			{
				if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("chat"))
				{
					
					if (!activeChar.isChatBlocked())
					{
						ChatBanManager.getInstance().addBlock(activeChar.getObjectId(), System.currentTimeMillis() + Config.CHAT_FILTER_PUNISHMENT_PARAM1 * 60000, activeChar.getHWID(), activeChar.getAccountName(), activeChar.getName());
						
						for (Player player : World.getInstance().getPlayers())
						{
							String hwidz = player.getHWID();
							String hwid = activeChar.getHWID();
							
							if (player.isOnline() && !player.isPhantom())
							{
								if (hwidz.equals(hwid))
								{
									player.setChatBlock(true);
									player.setChatBanTimer(System.currentTimeMillis() + Config.CHAT_FILTER_PUNISHMENT_PARAM1 * 60000);
									
									if (!player.equals(activeChar))
									{
										if (((player.getChatBanTimer() - System.currentTimeMillis()) / 3600000) >= 1)
											player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / 86400000 + " day(s).");
										else if (((player.getChatBanTimer() - System.currentTimeMillis()) / 3600000) >= 1)
											player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / 3600000 + " hour(s).");
										else if (((player.getChatBanTimer() - System.currentTimeMillis()) / 60000) >= 1)
											player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / 60000 + " minute(s).");
										else
											player.sendChatMessage(0, Say2.TELL, "SYS", "Your chat was suspended for " + (player.getChatBanTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
									}
								}
							}
						}
					}
					
				}
				else if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("karma"))
				{
					activeChar.setKarma(Config.CHAT_FILTER_PUNISHMENT_PARAM2);
					activeChar.sendMessage("SYS: You have get " + Config.CHAT_FILTER_PUNISHMENT_PARAM2 + " karma for bad words");
				}
				else if (Config.CHAT_FILTER_PUNISHMENT.equalsIgnoreCase("jail"))
					activeChar.setPunishLevel(PunishLevel.JAIL, Config.CHAT_FILTER_PUNISHMENT_PARAM1);
				
				for (Player allgms : World.getAllGMs())
					allgms.sendPacket(new CreatureSay(0, Say2.ALLIANCE, activeChar.getName(), _text));
				
				_text = filteredText;
			}
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}