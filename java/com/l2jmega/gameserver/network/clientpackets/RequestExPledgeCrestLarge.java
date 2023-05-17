package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.data.cache.CrestCache;
import com.l2jmega.gameserver.data.cache.CrestCache.CrestType;
import com.l2jmega.gameserver.network.serverpackets.ExPledgeCrestLarge;

/**
 * Fomat : chd
 * @author -Wooden-
 */
public final class RequestExPledgeCrestLarge extends L2GameClientPacket
{
	private int _crestId;
	
	@Override
	protected void readImpl()
	{
		_crestId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		byte[] data = CrestCache.getInstance().getCrest(CrestType.PLEDGE_LARGE, _crestId);
		if (data != null)
			sendPacket(new ExPledgeCrestLarge(_crestId, data));
	}
}