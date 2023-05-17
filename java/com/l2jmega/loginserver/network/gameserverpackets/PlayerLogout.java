package com.l2jmega.loginserver.network.gameserverpackets;

import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerLogout extends ClientBasePacket
{
	private final String _account;

	public PlayerLogout(final byte[] decrypt)
	{
		super(decrypt);
		_account = readS();
	}

	public String getAccount()
	{
		return _account;
	}
}
