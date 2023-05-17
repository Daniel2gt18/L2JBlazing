package com.l2jmega.loginserver.network.loginserverpackets;

import com.l2jmega.loginserver.network.serverpackets.ServerBasePacket;

public class InitLS extends ServerBasePacket
{
	public InitLS(final byte[] publickey)
	{
		writeC(0);
		writeD(258);
		writeD(publickey.length);
		writeB(publickey);
	}

	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
