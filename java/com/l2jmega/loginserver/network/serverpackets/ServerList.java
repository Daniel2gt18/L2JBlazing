package com.l2jmega.loginserver.network.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.l2jmega.loginserver.GameServerTable;
import com.l2jmega.loginserver.L2LoginClient;

public final class ServerList extends L2LoginServerPacket
{
	private final List<ServerData> _servers;
	private final int _lastServer;

	public ServerList(final L2LoginClient client)
	{
		_servers = new ArrayList<>();
		_lastServer = client.getLastServer();
		for (final GameServerTable.GameServerInfo gsi : GameServerTable.getInstance().getRegisteredGameServers().values())
			if (gsi.getStatus() == 5 && client.getAccessLevel() > 0)
				addServer(client.usesInternalIP() ? gsi.getInternalHost() : gsi.getExternalHost(), gsi.getPort(), gsi.isPvp(), gsi.isTestServer(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), gsi.isShowingClock(), gsi.getStatus(), gsi.getId());
			else if (gsi.getStatus() != 5)
				addServer(client.usesInternalIP() ? gsi.getInternalHost() : gsi.getExternalHost(), gsi.getPort(), gsi.isPvp(), gsi.isTestServer(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), gsi.isShowingClock(), gsi.getStatus(), gsi.getId());
			else
				addServer(client.usesInternalIP() ? gsi.getInternalHost() : gsi.getExternalHost(), gsi.getPort(), gsi.isPvp(), gsi.isTestServer(), gsi.getCurrentPlayerCount(), gsi.getMaxPlayers(), gsi.isShowingBrackets(), gsi.isShowingClock(), 4, gsi.getId());
	}

	public void addServer(final String ip, final int port, final boolean pvp, final boolean testServer, final int currentPlayer, final int maxPlayer, final boolean brackets, final boolean clock, final int status, final int server_id)
	{
		_servers.add(new ServerData(ip, port, pvp, testServer, currentPlayer, maxPlayer, brackets, clock, status, server_id));
	}

	@Override
	public void write()
	{
		writeC(4);
		writeC(_servers.size());
		writeC(_lastServer);
		for (final ServerData server : _servers)
		{
			writeC(server._serverId);
			try
			{
				final InetAddress i4 = InetAddress.getByName(server._ip);
				final byte[] raw = i4.getAddress();
				writeC(raw[0] & 0xFF);
				writeC(raw[1] & 0xFF);
				writeC(raw[2] & 0xFF);
				writeC(raw[3] & 0xFF);
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				writeC(127);
				writeC(0);
				writeC(0);
				writeC(1);
			}
			writeD(server._port);
			writeC(0);
			writeC(server._pvp ? 1 : 0);
			writeH(server._currentPlayers);
			writeH(server._maxPlayers);
			writeC(server._status != 4 ? 1 : 0);
			int bits = 0;
			if (server._testServer)
				bits |= 0x4;
			if (server._clock)
				bits |= 0x2;
			writeD(bits);
			writeC(server._brackets ? 1 : 0);
		}
	}

	class ServerData
	{
		protected String _ip;
		protected int _port;
		protected boolean _pvp;
		protected int _currentPlayers;
		protected int _maxPlayers;
		protected boolean _testServer;
		protected boolean _brackets;
		protected boolean _clock;
		protected int _status;
		protected int _serverId;

		ServerData(final String pIp, final int pPort, final boolean pPvp, final boolean pTestServer, final int pCurrentPlayers, final int pMaxPlayers, final boolean pBrackets, final boolean pClock, final int pStatus, final int pServer_id)
		{
			_ip = pIp;
			_port = pPort;
			_pvp = pPvp;
			_testServer = pTestServer;
			_currentPlayers = pCurrentPlayers;
			_maxPlayers = pMaxPlayers;
			_brackets = pBrackets;
			_clock = pClock;
			_status = pStatus;
			_serverId = pServer_id;
		}
	}
}
