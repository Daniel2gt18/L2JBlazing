package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.util.StringTokenizer;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.xml.DressMeData;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.DressMe;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedTrySkin implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = new String[] { "skin", "trySkin" };
	
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target) {
		if (command.equals("skin") && Config.CMD_SKIN)
			showTrySkinHtml(activeChar); 
		if (command.startsWith("trySkin")) {
			if (!activeChar.isInsideZone(ZoneId.TOWN)) {
				activeChar.sendMessage("This command can only be used within a city.");
				return false;
			}
			
			if (activeChar.getDress() != null) {
				activeChar.sendMessage("Wait, you are experiencing a skin.");
				return false;
			}
		      
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			int skinId = Integer.parseInt(st.nextToken());
			
			final DressMe dress = DressMeData.getInstance().getItemId(skinId);
			final DressMe dress2 = DressMeData.getInstance().getItemId(0);
			
			if (dress != null) {

				activeChar.setDress(dress);
				ThreadPool.schedule(() -> {
					activeChar.setDress(dress2);
					activeChar.broadcastUserInfo();
				},3000L);
			} else {
				activeChar.sendMessage("Invalid skin.");
				return false;
			} 
		}
		
		return true;
	}
	
	private static void showTrySkinHtml(Player activeChar) {
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/trySkin.htm");
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}
