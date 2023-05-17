package com.l2jmega.loginserver.network.serverpackets;

import com.l2jmega.loginserver.SessionKey;

public final class PlayOk extends L2LoginServerPacket
{
	private final int _playOk1;
	private final int _playOk2;

	public PlayOk(final SessionKey sessionKey)
	{
		_playOk1 = sessionKey.playOkID1;
		_playOk2 = sessionKey.playOkID2;
	}

	@Override
	protected void write()
	{
		writeC(7);
		writeD(_playOk1);
		writeD(_playOk2);
	}
}
