package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.actor.instance.Player;

public final class RequestRecipeShopManageList extends L2GameClientPacket
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
		
		player.tryOpenWorkshop(true);
	}
}