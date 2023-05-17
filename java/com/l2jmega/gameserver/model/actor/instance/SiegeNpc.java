package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class SiegeNpc extends Folk
{
	public SiegeNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		String htmFile = "data/html/mods/menu/CastleManager.htm";
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		player.sendPacket(msg);
	}
}