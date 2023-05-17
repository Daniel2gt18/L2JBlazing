package com.l2jmega.loginserver.network.serverpackets;

import com.l2jmega.loginserver.SessionKey;

public final class LoginOk extends L2LoginServerPacket
{
	private final int _loginOk1;
	private final int _loginOk2;

	public LoginOk(final SessionKey sessionKey)
	{
		_loginOk1 = sessionKey.loginOkID1;
		_loginOk2 = sessionKey.loginOkID2;
	}

	@Override
	protected void write()
	{
		writeC(3);
		writeD(_loginOk1);
		writeD(_loginOk2);
		writeD(0);
		writeD(0);
		writeD(1002);
		writeD(0);
		writeD(0);
		writeD(0);
		writeB(new byte[16]);
	}
}
