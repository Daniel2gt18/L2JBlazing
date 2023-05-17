package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.events.ArenaTask;
import com.l2jmega.gameserver.instancemanager.SevenSignsFestival;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GameClient.GameClientState;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.CharSelectInfo;
import com.l2jmega.gameserver.network.serverpackets.RestartResponse;
import com.l2jmega.gameserver.taskmanager.AttackStanceTaskManager;

public final class RequestRestart extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.getActiveEnchantItem() != null || player.isLocked() || player.isInStoreMode())
		{
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if ((player._inEventTvT || player._inEventCTF) && !player.isGM())
		{
			player.sendMessage("You can't restart during Event.");
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if ((player.isInArenaEvent() || player.isArenaProtection()) && ArenaTask.is_started())
		{
			player.sendMessage("You cannot restart while in Tournament Event!");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_RESTART) && !player.isGM() && !player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.NO_RESTART_HERE);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player) && !player.isGM())
		{
			player.sendPacket(SystemMessageId.CANT_RESTART_WHILE_FIGHTING);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		if (player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized() && !player.isGM())
		{
			player.sendPacket(SystemMessageId.NO_RESTART_HERE);
			sendPacket(RestartResponse.valueOf(false));
			return;
		}
		
		player.removeFromBossZone();
		
		// delete box from the world
		if (player._active_boxes != -1)
			player.decreaseBoxes();
		
		final L2GameClient client = getClient();
		
		// detach the client from the char so that the connection isnt closed in the deleteMe
		player.setClient(null);
		
		// removing player from the world
		player.deleteMe();
		
		client.setActiveChar(null);
		client.setState(GameClientState.AUTHED);
		
		sendPacket(RestartResponse.valueOf(true));
		
		// send char list
		final CharSelectInfo cl = new CharSelectInfo(client.getAccountName(), client.getSessionId().playOkID1);
		sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}