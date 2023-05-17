package com.l2jmega.loginserver.network.serverpackets;

import java.util.logging.Logger;

import com.l2jmega.Config;

public final class GGAuth extends L2LoginServerPacket
{
	static final Logger _log;
	public static final int SKIP_GG_AUTH_REQUEST = 11;
	private final int _response;

	public GGAuth(final int response)
	{
		_response = response;
		if (Config.DEBUG)
			GGAuth._log.warning("Reason Hex: " + Integer.toHexString(response));
	}

	@Override
	protected void write()
	{
		writeC(11);
		writeD(_response);
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
	}

	static
	{
		_log = Logger.getLogger(GGAuth.class.getName());
	}
}
