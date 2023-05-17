package com.l2jmega.loginserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.loginserver.crypt.NewCrypt;
import com.l2jmega.loginserver.network.gameserverpackets.BlowFishKey;
import com.l2jmega.loginserver.network.gameserverpackets.ChangeAccessLevel;
import com.l2jmega.loginserver.network.gameserverpackets.GameServerAuth;
import com.l2jmega.loginserver.network.gameserverpackets.PlayerAuthRequest;
import com.l2jmega.loginserver.network.gameserverpackets.PlayerInGame;
import com.l2jmega.loginserver.network.gameserverpackets.PlayerLogout;
import com.l2jmega.loginserver.network.gameserverpackets.ServerStatus;
import com.l2jmega.loginserver.network.loginserverpackets.AuthResponse;
import com.l2jmega.loginserver.network.loginserverpackets.InitLS;
import com.l2jmega.loginserver.network.loginserverpackets.KickPlayer;
import com.l2jmega.loginserver.network.loginserverpackets.LoginServerFail;
import com.l2jmega.loginserver.network.loginserverpackets.PlayerAuthResponse;
import com.l2jmega.loginserver.network.serverpackets.ServerBasePacket;

public class GameServerThread extends Thread
{
	protected static final Logger _log;
	private final Socket _connection;
	private InputStream _in;
	private OutputStream _out;
	private final RSAPublicKey _publicKey;
	private final RSAPrivateKey _privateKey;
	private NewCrypt _blowfish;
	private byte[] _blowfishKey;
	private final String _connectionIp;
	private GameServerTable.GameServerInfo _gsi;
	private final Set<String> _accountsOnGameServer;
	private String _connectionIPAddress;

	@Override
	public void run()
	{
		_connectionIPAddress = _connection.getInetAddress().getHostAddress();
		if (isBannedGameserverIP(_connectionIPAddress))
		{
			GameServerThread._log.info("GameServer with banned IP " + _connectionIPAddress + " tries to register.");
			forceClose(1);
			return;
		}
		try
		{
			sendPacket(new InitLS(_publicKey.getModulus().toByteArray()));
			int lengthHi = 0;
			int lengthLo = 0;
			int length = 0;
			boolean checksumOk = false;
			while (true)
			{
				lengthLo = _in.read();
				lengthHi = _in.read();
				length = lengthHi * 256 + lengthLo;
				if (lengthHi < 0)
					break;
				if (_connection.isClosed())
					break;
				byte[] data = new byte[length - 2];
				int receivedBytes = 0;
				for (int newBytes = 0; newBytes != -1 && receivedBytes < length - 2; newBytes = _in.read(data, 0, length - 2), receivedBytes += newBytes)
				{
				}
				if (receivedBytes != length - 2)
				{
					GameServerThread._log.warning("Incomplete packet is sent to the server, closing connection.");
					break;
				}
				data = _blowfish.decrypt(data);
				checksumOk = NewCrypt.verifyChecksum(data);
				if (!checksumOk)
				{
					GameServerThread._log.warning("Incorrect packet checksum, closing connection.");
					return;
				}
				final int packetType = data[0] & 0xFF;
				switch (packetType)
				{
					case 0:
					{
						onReceiveBlowfishKey(data);
						continue;
					}
					case 1:
					{
						onGameServerAuth(data);
						continue;
					}
					case 2:
					{
						onReceivePlayerInGame(data);
						continue;
					}
					case 3:
					{
						onReceivePlayerLogOut(data);
						continue;
					}
					case 4:
					{
						onReceiveChangeAccessLevel(data);
						continue;
					}
					case 5:
					{
						onReceivePlayerAuthRequest(data);
						continue;
					}
					case 6:
					{
						onReceiveServerStatus(data);
						continue;
					}
					default:
					{
						GameServerThread._log.warning("Unknown Opcode (" + Integer.toHexString(packetType).toUpperCase() + ") from GameServer, closing connection.");
						forceClose(6);
						continue;
					}
				}
			}
		}
		catch (IOException e)
		{
			final String serverName = getServerId() != -1 ? "[" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()) : "(" + _connectionIPAddress + ")";
			GameServerThread._log.info("GameServer " + serverName + ": " + e.getMessage() + ".");
		}
		finally
		{
			if (isAuthed())
			{
				_gsi.setDown();
				GameServerThread._log.info("GameServer [" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()) + " is now set as disconnected.");
			}
			L2LoginServer.getInstance().getGameServerListener().removeGameServer(this);
			L2LoginServer.getInstance().getGameServerListener().removeFloodProtection(_connectionIp);
		}
	}

	private void onReceiveBlowfishKey(final byte[] data)
	{
		final BlowFishKey bfk = new BlowFishKey(data, _privateKey);
		_blowfishKey = bfk.getKey();
		_blowfish = new NewCrypt(_blowfishKey);
	}

	private void onGameServerAuth(final byte[] data)
	{
		handleRegProcess(new GameServerAuth(data));
		if (isAuthed())
			sendPacket(new AuthResponse(_gsi.getId()));
	}

	private void onReceivePlayerInGame(final byte[] data)
	{
		if (isAuthed())
		{
			final PlayerInGame pig = new PlayerInGame(data);
			for (final String account : pig.getAccounts())
				_accountsOnGameServer.add(account);
		}
		else
			forceClose(6);
	}

	private void onReceivePlayerLogOut(final byte[] data)
	{
		if (isAuthed())
		{
			final PlayerLogout plo = new PlayerLogout(data);
			_accountsOnGameServer.remove(plo.getAccount());
		}
		else
			forceClose(6);
	}

	private void onReceiveChangeAccessLevel(final byte[] data)
	{
		if (isAuthed())
		{
			final ChangeAccessLevel cal = new ChangeAccessLevel(data);
			LoginController.getInstance().setAccountAccessLevel(cal.getAccount(), cal.getLevel());
			GameServerThread._log.info("Changed " + cal.getAccount() + " access level to " + cal.getLevel() + ".");
		}
		else
			forceClose(6);
	}

	private void onReceivePlayerAuthRequest(final byte[] data)
	{
		if (isAuthed())
		{
			final PlayerAuthRequest par = new PlayerAuthRequest(data);
			final SessionKey key = LoginController.getInstance().getKeyForAccount(par.getAccount());
			if (key != null && key.equals(par.getKey()))
			{
				LoginController.getInstance().removeAuthedLoginClient(par.getAccount());
				sendPacket(new PlayerAuthResponse(par.getAccount(), true));
			}
			else
				sendPacket(new PlayerAuthResponse(par.getAccount(), false));
		}
		else
			forceClose(6);
	}

	private void onReceiveServerStatus(final byte[] data)
	{
		if (isAuthed())
			new ServerStatus(data, getServerId());
		else
			forceClose(6);
	}

	private void handleRegProcess(final GameServerAuth gameServerAuth)
	{
		final int id = gameServerAuth.getDesiredID();
		final byte[] hexId = gameServerAuth.getHexID();
		GameServerTable.GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(id);
		if (gsi != null)
		{
			if (Arrays.equals(gsi.getHexId(), hexId))
				synchronized (gsi)
				{
					if (gsi.isAuthed())
						forceClose(7);
					else
						attachGameServerInfo(gsi, gameServerAuth);
				}
			else if (Config.ACCEPT_NEW_GAMESERVER && gameServerAuth.acceptAlternateID())
			{
				gsi = new GameServerTable.GameServerInfo(id, hexId, this);
				if (GameServerTable.getInstance().registerWithFirstAvaliableId(gsi))
				{
					attachGameServerInfo(gsi, gameServerAuth);
					GameServerTable.getInstance().registerServerOnDB(gsi);
				}
				else
					forceClose(5);
			}
			else
				forceClose(3);
		}
		else if (Config.ACCEPT_NEW_GAMESERVER)
		{
			gsi = new GameServerTable.GameServerInfo(id, hexId, this);
			if (GameServerTable.getInstance().register(id, gsi))
			{
				attachGameServerInfo(gsi, gameServerAuth);
				GameServerTable.getInstance().registerServerOnDB(gsi);
			}
			else
				forceClose(4);
		}
		else
			forceClose(3);
	}

	public boolean hasAccountOnGameServer(final String account)
	{
		return _accountsOnGameServer.contains(account);
	}

	public int getPlayerCount()
	{
		return _accountsOnGameServer.size();
	}

	private void attachGameServerInfo(final GameServerTable.GameServerInfo gsi, final GameServerAuth gameServerAuth)
	{
		setGameServerInfo(gsi);
		gsi.setGameServerThread(this);
		gsi.setPort(gameServerAuth.getPort());
		setGameHosts(gameServerAuth.getExternalHost(), gameServerAuth.getInternalHost());
		gsi.setMaxPlayers(gameServerAuth.getMaxPlayers());
		gsi.setAuthed(true);
	}

	private void forceClose(final int reason)
	{
		sendPacket(new LoginServerFail(reason));
		try
		{
			_connection.close();
		}
		catch (IOException e)
		{
			GameServerThread._log.finer("GameServerThread: Failed disconnecting banned server, server already disconnected.");
		}
	}

	public static boolean isBannedGameserverIP(final String ipAddress)
	{
		InetAddress netAddress = null;
		try
		{
			netAddress = InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return LoginController.getInstance().isBannedAddress(netAddress);
	}

	public GameServerThread(final Socket con)
	{
		_accountsOnGameServer = new HashSet<>();
		_connection = con;
		_connectionIp = con.getInetAddress().getHostAddress();
		try
		{
			_in = _connection.getInputStream();
			_out = new BufferedOutputStream(_connection.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		final KeyPair pair = GameServerTable.getInstance().getKeyPair();
		_privateKey = (RSAPrivateKey) pair.getPrivate();
		_publicKey = (RSAPublicKey) pair.getPublic();
		_blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\u0000");
		start();
	}

	private void sendPacket(final ServerBasePacket sl)
	{
		try
		{
			byte[] data = sl.getContent();
			NewCrypt.appendChecksum(data);
			data = _blowfish.crypt(data);
			final int len = data.length + 2;
			synchronized (_out)
			{
				_out.write(len & 0xFF);
				_out.write(len >> 8 & 0xFF);
				_out.write(data);
				_out.flush();
			}
		}
		catch (IOException e)
		{
			GameServerThread._log.severe("IOException while sending packet " + sl.getClass().getSimpleName() + ".");
		}
	}

	public void kickPlayer(final String account)
	{
		sendPacket(new KickPlayer(account));
	}

	public void removeAcc(final String account)
	{
		_accountsOnGameServer.remove(account);
	}

	public void setGameHosts(final String gameExternalHost, final String gameInternalHost)
	{
		final String oldInternal = _gsi.getInternalHost();
		final String oldExternal = _gsi.getExternalHost();
		_gsi.setExternalHost(gameExternalHost);
		_gsi.setInternalIp(gameInternalHost);
		if (!gameExternalHost.equals("*"))
			try
			{
				_gsi.setExternalIp(InetAddress.getByName(gameExternalHost).getHostAddress());
			}
			catch (UnknownHostException e)
			{
				GameServerThread._log.warning("Couldn't resolve hostname \"" + gameExternalHost + "\"");
			}
		else
			_gsi.setExternalIp(_connectionIp);
		if (!gameInternalHost.equals("*"))
			try
			{
				_gsi.setInternalIp(InetAddress.getByName(gameInternalHost).getHostAddress());
			}
			catch (UnknownHostException e)
			{
				GameServerThread._log.warning("Couldn't resolve hostname \"" + gameInternalHost + "\"");
			}
		else
			_gsi.setInternalIp(_connectionIp);
		GameServerThread._log.info("Hooked gameserver: [" + getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getServerId()));
		GameServerThread._log.info("Internal/External IP(s): " + (oldInternal == null ? gameInternalHost : oldInternal) + "/" + (oldExternal == null ? gameExternalHost : oldExternal));
	}

	public boolean isAuthed()
	{
		return _gsi != null && _gsi.isAuthed();
	}

	public void setGameServerInfo(final GameServerTable.GameServerInfo gsi)
	{
		_gsi = gsi;
	}

	public GameServerTable.GameServerInfo getGameServerInfo()
	{
		return _gsi;
	}

	public String getConnectionIpAddress()
	{
		return _connectionIPAddress;
	}

	private int getServerId()
	{
		return _gsi == null ? -1 : _gsi.getId();
	}

	static
	{
		_log = Logger.getLogger(GameServerThread.class.getName());
	}
}
