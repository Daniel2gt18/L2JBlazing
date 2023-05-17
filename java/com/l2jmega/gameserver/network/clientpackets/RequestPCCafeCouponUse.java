package com.l2jmega.gameserver.network.clientpackets;

/**
 * Format: (ch) S
 * @author -Wooden-
 */
public final class RequestPCCafeCouponUse extends L2GameClientPacket
{
	private String _str;
	
	@Override
	protected void readImpl()
	{
		_str = readS();
	}
	
	@Override
	protected void runImpl()
	{
		// TODO : implement it
		_log.info("C5: RequestPCCafeCouponUse: " + _str);
	}
}