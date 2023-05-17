package com.l2jmega.gameserver.network.clientpackets;

import java.util.Map;

import com.l2jmega.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ExGetBossRecord;

/**
 * Format: (ch) d
 * @author -Wooden-
 */
public class RequestGetBossRecord extends L2GameClientPacket
{
	private int _bossId;
	
	@Override
	protected void readImpl()
	{
		_bossId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		// should be always 0, log it if isn't 0 for future research
		if (_bossId != 0)
			_log.info("C5: RequestGetBossRecord: d: " + _bossId + " ActiveChar: " + activeChar);
		
		int points = RaidBossPointsManager.getInstance().getPointsByOwnerId(activeChar.getObjectId());
		int ranking = RaidBossPointsManager.getInstance().calculateRanking(activeChar.getObjectId());
		
		Map<Integer, Integer> list = RaidBossPointsManager.getInstance().getList(activeChar);
		
		// trigger packet
		activeChar.sendPacket(new ExGetBossRecord(ranking, points, list));
	}
}