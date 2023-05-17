package com.l2jmega.loginserver.network.gameserverpackets;

import java.util.logging.Logger;

import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class GameServerAuth extends ClientBasePacket
{
	protected static Logger _log;
	private final byte[] _hexId;
	private final int _desiredId;
	private final boolean _hostReserved;
	private final boolean _acceptAlternativeId;
	private final int _maxPlayers;
	private final int _port;
	private final String _externalHost;
	private final String _internalHost;

	public GameServerAuth(final byte[] decrypt)
	{
		super(decrypt);
		_desiredId = readC();
		_acceptAlternativeId = readC() != 0;
		_hostReserved = readC() != 0;
		_externalHost = readS();
		_internalHost = readS();
		_port = readH();
		_maxPlayers = readD();
		final int size = readD();
		_hexId = readB(size);
	}

	public byte[] getHexID()
	{
		return _hexId;
	}

	public boolean getHostReserved()
	{
		return _hostReserved;
	}

	public int getDesiredID()
	{
		return _desiredId;
	}

	public boolean acceptAlternateID()
	{
		return _acceptAlternativeId;
	}

	public int getMaxPlayers()
	{
		return _maxPlayers;
	}

	public String getExternalHost()
	{
		return _externalHost;
	}

	public String getInternalHost()
	{
		return _internalHost;
	}

	public int getPort()
	{
		return _port;
	}

	static
	{
		GameServerAuth._log = Logger.getLogger(GameServerAuth.class.getName());
	}
}
