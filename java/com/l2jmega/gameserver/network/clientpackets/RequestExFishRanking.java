package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.instancemanager.FishingChampionshipManager;
import com.l2jmega.gameserver.model.actor.instance.Player;

/**
 * Format: (ch)
 * @author -Wooden-
 */
public final class RequestExFishRanking extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance().showMidResult(activeChar);
	}
}