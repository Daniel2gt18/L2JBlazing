package com.l2jmega.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

import com.l2jmega.commons.mmocore.MMOClient;
import com.l2jmega.commons.mmocore.MMOConnection;
import com.l2jmega.commons.mmocore.SendablePacket;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.loginserver.crypt.LoginCrypt;
import com.l2jmega.loginserver.crypt.ScrambledKeyPair;
import com.l2jmega.loginserver.network.serverpackets.L2LoginServerPacket;
import com.l2jmega.loginserver.network.serverpackets.LoginFail;
import com.l2jmega.loginserver.network.serverpackets.PlayFail;

public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>>
{
	private static Logger _log;
	private LoginClientState _state;
	private final LoginCrypt _loginCrypt;
	private final ScrambledKeyPair _scrambledPair;
	private final byte[] _blowfishKey;
	private String _account;
	private int _accessLevel;
	private int _lastServer;
	private boolean _usesInternalIP;
	private SessionKey _sessionKey;
	private final int _sessionId;
	private boolean _joinedGS;
	private final long _connectionStartTime;

	public L2LoginClient(final MMOConnection<L2LoginClient> con)
	{
		super(con);
		_state = LoginClientState.CONNECTED;
		final String ip = getConnection().getInetAddress().getHostAddress();
		if (ip.startsWith("192.168") || ip.startsWith("10.0") || ip.equals("127.0.0.1"))
			_usesInternalIP = true;
		_scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		_blowfishKey = LoginController.getInstance().getBlowfishKey();
		_sessionId = Rnd.nextInt();
		_connectionStartTime = System.currentTimeMillis();
		(_loginCrypt = new LoginCrypt()).setKey(_blowfishKey);
	}

	public boolean usesInternalIP()
	{
		return _usesInternalIP;
	}

	@Override
	public boolean decrypt(final ByteBuffer buf, final int size)
	{
		boolean ret = false;
		try
		{
			ret = _loginCrypt.decrypt(buf.array(), buf.position(), size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			super.getConnection().close((SendablePacket<L2LoginClient>) null);
			return false;
		}
		if (!ret)
		{
			final byte[] dump = new byte[size];
			System.arraycopy(buf.array(), buf.position(), dump, 0, size);
			L2LoginClient._log.warning("Wrong checksum from client: " + toString());
			super.getConnection().close((SendablePacket<L2LoginClient>) null);
		}
		return ret;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, int size)
	{
		final int offset = buf.position();
		try
		{
			size = _loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		buf.position(offset + size);
		return true;
	}

	public LoginClientState getState()
	{
		return _state;
	}

	public void setState(final LoginClientState state)
	{
		_state = state;
	}

	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}

	public byte[] getScrambledModulus()
	{
		return _scrambledPair.getScrambledModulus();
	}

	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair.getKeyPair().getPrivate();
	}

	public String getAccount()
	{
		return _account;
	}

	public void setAccount(final String account)
	{
		_account = account;
	}

	public void setAccessLevel(final int accessLevel)
	{
		_accessLevel = accessLevel;
	}

	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setLastServer(final int lastServer)
	{
		_lastServer = lastServer;
	}

	public int getLastServer()
	{
		return _lastServer;
	}

	public int getSessionId()
	{
		return _sessionId;
	}

	public boolean hasJoinedGS()
	{
		return _joinedGS;
	}

	public void setJoinedGS(final boolean val)
	{
		_joinedGS = val;
	}

	public void setSessionKey(final SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}

	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}

	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}

	public void sendPacket(final L2LoginServerPacket lsp)
	{
		getConnection().sendPacket(lsp);
	}

	public void close(final LoginFail.LoginFailReason reason)
	{
		getConnection().close(new LoginFail(reason));
	}

	public void close(final PlayFail.PlayFailReason reason)
	{
		getConnection().close(new PlayFail(reason));
	}

	public void close(final L2LoginServerPacket lsp)
	{
		getConnection().close(lsp);
	}

	@Override
	public void onDisconnection()
	{
		if (Config.DEBUG)
			L2LoginClient._log.info("DISCONNECTED: " + toString());
		if (!hasJoinedGS() || getConnectionStartTime() + 60000L < System.currentTimeMillis())
			LoginController.getInstance().removeAuthedLoginClient(getAccount());
	}

	@Override
	public String toString()
	{
		final InetAddress address = getConnection().getInetAddress();
		if (getState() == LoginClientState.AUTHED_LOGIN)
			return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";
		return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
	}

	@Override
	protected void onForcedDisconnection()
	{
	}

	static
	{
		L2LoginClient._log = Logger.getLogger(L2LoginClient.class.getName());
	}

	public enum LoginClientState
	{
		CONNECTED,
		AUTHED_GG,
		AUTHED_LOGIN;
	}
}
