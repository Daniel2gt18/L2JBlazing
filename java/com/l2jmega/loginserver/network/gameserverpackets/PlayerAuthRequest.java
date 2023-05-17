package com.l2jmega.loginserver.network.gameserverpackets;

import com.l2jmega.loginserver.SessionKey;
import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerAuthRequest extends ClientBasePacket
{
	private final String _account;
	private final SessionKey _sessionKey;

	public PlayerAuthRequest(final byte[] decrypt)
	{
		super(decrypt);
		_account = readS();
		final int playKey1 = readD();
		final int playKey2 = readD();
		final int loginKey1 = readD();
		final int loginKey2 = readD();
		_sessionKey = new SessionKey(loginKey1, loginKey2, playKey1, playKey2);
	}

	public String getAccount()
	{
		return _account;
	}

	public SessionKey getKey()
	{
		return _sessionKey;
	}
}
