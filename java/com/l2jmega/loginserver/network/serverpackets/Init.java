package com.l2jmega.loginserver.network.serverpackets;

import com.l2jmega.loginserver.L2LoginClient;

public final class Init extends L2LoginServerPacket
{
	private final int _sessionId;
	private final byte[] _publicKey;
	private final byte[] _blowfishKey;

	public Init(final L2LoginClient client)
	{
		this(client.getScrambledModulus(), client.getBlowfishKey(), client.getSessionId());
	}

	public Init(final byte[] publickey, final byte[] blowfishkey, final int sessionId)
	{
		_sessionId = sessionId;
		_publicKey = publickey;
		_blowfishKey = blowfishkey;
	}

	@Override
	protected void write()
	{
		writeC(0);
		writeD(_sessionId);
		writeD(50721);
		writeB(_publicKey);
		writeD(702387534);
		writeD(2009308412);
		writeD(-1750223328);
		writeD(129884407);
		writeB(_blowfishKey);
		writeC(0);
	}
}
