package com.l2jmega.loginserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.crypto.Cipher;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.loginserver.crypt.ScrambledKeyPair;
import com.l2jmega.loginserver.network.serverpackets.LoginFail.LoginFailReason;

public class LoginController
{
	protected static final Logger _log;
	private static LoginController _instance;
	public static final int LOGIN_TIMEOUT = 60000;
	protected HashSet<L2LoginClient> _clients;
	protected Map<String, L2LoginClient> _loginServerClients;
	private final Map<InetAddress, BanInfo> _bannedIps;
	private final Map<InetAddress, FailedLoginAttempt> _hackProtection;
	protected ScrambledKeyPair[] _keyPairs;
	private final Thread _purge;
	protected byte[][] _blowfishKeys;
	
	public static void load() throws GeneralSecurityException
	{
		if (LoginController._instance == null)
		{
			LoginController._instance = new LoginController();
			return;
		}
		throw new IllegalStateException("LoginController can only be loaded a single time.");
	}
	
	public static LoginController getInstance()
	{
		return LoginController._instance;
	}
	
	private LoginController() throws GeneralSecurityException
	{
		_clients = new HashSet<>();
		_loginServerClients = new ConcurrentHashMap<>();
		_bannedIps = new ConcurrentHashMap<>();
		LoginController._log.info("Loading LoginController...");
		_hackProtection = new HashMap<>();
		_keyPairs = new ScrambledKeyPair[10];
		KeyPairGenerator keygen = null;
		keygen = KeyPairGenerator.getInstance("RSA");
		final RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		keygen.initialize(spec);
		for (int i = 0; i < 10; ++i)
			_keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
		LoginController._log.info("Cached 10 KeyPairs for RSA communication.");
		testCipher((RSAPrivateKey) _keyPairs[0].getKeyPair().getPrivate());
		generateBlowFishKeys();
		(_purge = new PurgeThread()).setDaemon(true);
		_purge.start();
	}
	
	private static void testCipher(final RSAPrivateKey key) throws GeneralSecurityException
	{
		final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
		rsaCipher.init(2, key);
	}
	
	private void generateBlowFishKeys()
	{
		_blowfishKeys = new byte[20][16];
		for (int i = 0; i < 20; ++i)
			for (int j = 0; j < _blowfishKeys[i].length; ++j)
				_blowfishKeys[i][j] = (byte) (Rnd.get(255) + 1);
		LoginController._log.info("Stored " + _blowfishKeys.length + " keys for Blowfish communication.");
	}
	
	public byte[] getBlowfishKey()
	{
		return _blowfishKeys[(int) (Math.random() * 20.0)];
	}
	
	public void removeLoginClient(final L2LoginClient client)
	{
		synchronized (_clients)
		{
			_clients.remove(client);
		}
	}
	
	public SessionKey assignSessionKeyToClient(final String account, final L2LoginClient client)
	{
		final SessionKey key = new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
		_loginServerClients.put(account, client);
		return key;
	}
	
	public void removeAuthedLoginClient(final String account)
	{
		if (account == null)
			return;
		_loginServerClients.remove(account);
	}
	
	public L2LoginClient getAuthedClient(final String account)
	{
		return _loginServerClients.get(account);
	}
	
	public AuthLoginResult tryAuthLogin(final String account, final String password, final L2LoginClient client)
	{
		AuthLoginResult ret = AuthLoginResult.INVALID_PASSWORD;
		if (loginValid(account, password, client))
		{
			ret = AuthLoginResult.ALREADY_ON_GS;
			if (!isAccountInAnyGameServer(account))
			{
				ret = AuthLoginResult.ALREADY_ON_LS;
				synchronized (_loginServerClients)
				{
					if (!_loginServerClients.containsKey(account))
					{
						_loginServerClients.put(account, client);
						ret = AuthLoginResult.AUTH_SUCCESS;
						removeLoginClient(client);
					}
				}
			}
		}
		else if (client.getAccessLevel() < 0)
			ret = AuthLoginResult.ACCOUNT_BANNED;
		return ret;
	}
	
	public void addBanForAddress(final String address, final long expiration) throws UnknownHostException
	{
		final InetAddress netAddress = InetAddress.getByName(address);
		if (!_bannedIps.containsKey(netAddress))
			_bannedIps.put(netAddress, new BanInfo(netAddress, expiration));
	}
	
	public void addBanForAddress(final InetAddress address, final long duration)
	{
		if (!_bannedIps.containsKey(address))
			_bannedIps.put(address, new BanInfo(address, System.currentTimeMillis() + duration));
	}
	
	public boolean isBannedAddress(final InetAddress address)
	{
		final BanInfo bi = _bannedIps.get(address);
		if (bi == null)
			return false;
		if (bi.hasExpired())
		{
			_bannedIps.remove(address);
			return false;
		}
		return true;
	}
	
	public Map<InetAddress, BanInfo> getBannedIps()
	{
		return _bannedIps;
	}
	
	public boolean removeBanForAddress(final InetAddress address)
	{
		return _bannedIps.remove(address) != null;
	}
	
	public boolean removeBanForAddress(final String address)
	{
		try
		{
			return this.removeBanForAddress(InetAddress.getByName(address));
		}
		catch (UnknownHostException e)
		{
			return false;
		}
	}
	
	public SessionKey getKeyForAccount(final String account)
	{
		final L2LoginClient client = _loginServerClients.get(account);
		if (client != null)
			return client.getSessionKey();
		return null;
	}
	
	public boolean isAccountInAnyGameServer(final String account)
	{
		final Collection<GameServerTable.GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (final GameServerTable.GameServerInfo gsi : serverList)
		{
			final GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
				return true;
		}
		return false;
	}
	
	public GameServerTable.GameServerInfo getAccountOnGameServer(final String account)
	{
		final Collection<GameServerTable.GameServerInfo> serverList = GameServerTable.getInstance().getRegisteredGameServers().values();
		for (final GameServerTable.GameServerInfo gsi : serverList)
		{
			final GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
				return gsi;
		}
		return null;
	}
	
	public boolean isLoginPossible(final L2LoginClient client, final int serverId)
	{
		final GameServerTable.GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(serverId);
		final int access = client.getAccessLevel();
		if (gsi != null && gsi.isAuthed())
		{
			final boolean loginOk = gsi.getCurrentPlayerCount() < gsi.getMaxPlayers() && gsi.getStatus() != 5 || access > 0;
			if (loginOk && client.getLastServer() != serverId)
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					final PreparedStatement statement = con.prepareStatement("UPDATE accounts SET lastServer = ? WHERE login = ?");
					statement.setInt(1, serverId);
					statement.setString(2, client.getAccount());
					statement.executeUpdate();
					statement.close();
				}
				catch (Exception e)
				{
					LoginController._log.log(Level.WARNING, "Could not set lastServer: " + e.getMessage(), e);
				}
			return loginOk;
		}
		return false;
	}
	
	public void setAccountAccessLevel(final String account, final int banLevel)
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			final PreparedStatement statement = con.prepareStatement("UPDATE accounts SET access_level=? WHERE login=?");
			statement.setInt(1, banLevel);
			statement.setString(2, account);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			LoginController._log.log(Level.WARNING, "Could not set accessLevel: " + e.getMessage(), e);
		}
	}
	
	public ScrambledKeyPair getScrambledRSAKeyPair()
	{
		return _keyPairs[Rnd.get(10)];
	}
	
	@SuppressWarnings("resource")
	public boolean loginValid(final String user, final String password, final L2LoginClient client)
	{
		boolean ok = false;
		final InetAddress address = client.getConnection().getInetAddress();
		if (address == null || user == null)
			return false;
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			final MessageDigest md = MessageDigest.getInstance("SHA");
			final byte[] raw = password.getBytes("UTF-8");
			final byte[] hash = md.digest(raw);
			byte[] expected = null;
			int access = 0;
			int lastServer = 1;
			PreparedStatement statement = con.prepareStatement("SELECT password, access_level, lastServer FROM accounts WHERE login=?");
			statement.setString(1, user);
			final ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				expected = Base64.getDecoder().decode(rset.getString("password"));
				access = rset.getInt("access_level");
				lastServer = rset.getInt("lastServer");
				if (lastServer <= 0)
					lastServer = 1;
			}
			rset.close();
			statement.close();
			if (!isValidLogin(user) && access == 0)
				return false;
			if (expected == null)
			{
				if (!Config.AUTO_CREATE_ACCOUNTS)
				{
					LoginController._log.warning("Account missing for user " + user + " (" + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + ")");
					final FailedLoginAttempt failedAttempt = _hackProtection.get(address);
					int failedCount;
					if (failedAttempt == null)
					{
						_hackProtection.put(address, new FailedLoginAttempt(address, password));
						failedCount = 1;
					}
					else
					{
						failedAttempt.increaseCounter();
						failedCount = failedAttempt.getCount();
					}
					if (failedCount >= Config.LOGIN_TRY_BEFORE_BAN)
					{
						LoginController._log.info("Banning '" + address.getHostAddress() + "' for " + Config.LOGIN_BLOCK_AFTER_BAN + " seconds due to " + failedCount + " invalid user name attempts");
						this.addBanForAddress(address, Config.LOGIN_BLOCK_AFTER_BAN * 1000);
					}
					return false;
				}
				if (user.length() >= 2 && user.length() <= 14)
				{
					statement = con.prepareStatement("INSERT INTO accounts (login,password,lastactive,access_level) values(?,?,?,?)");
					statement.setString(1, user);
					statement.setString(2, Base64.getEncoder().encodeToString(hash));
					statement.setLong(3, System.currentTimeMillis());
					statement.setInt(4, 0);
					statement.execute();
					statement.close();
					LoginController._log.info("New account has been created for " + user + ".");
					return true;
				}
				LoginController._log.warning("Invalid username creation/use attempt: " + user + ".");
				return false;
			}
			if (access < 0)
			{
				client.setAccessLevel(access);
				return false;
			}
			ok = true;
			for (int i = 0; i < expected.length; ++i)
				if (hash[i] != expected[i])
				{
					ok = false;
					break;
				}
			if (ok)
			{
				client.setAccessLevel(access);
				client.setLastServer(lastServer);
				statement = con.prepareStatement("UPDATE accounts SET lastactive=? WHERE login=?");
				statement.setLong(1, System.currentTimeMillis());
				statement.setString(2, user);
				statement.execute();
				statement.close();
			}
		}
		catch (Exception e)
		{
			LoginController._log.log(Level.WARNING, "Could not check password:" + e.getMessage(), e);
			ok = false;
		}
		if (!ok)
		{
			final FailedLoginAttempt failedAttempt2 = _hackProtection.get(address);
			int failedCount2;
			if (failedAttempt2 == null)
			{
				_hackProtection.put(address, new FailedLoginAttempt(address, password));
				failedCount2 = 1;
			}
			else
			{
				failedAttempt2.increaseCounter(password);
				failedCount2 = failedAttempt2.getCount();
			}
			if (failedCount2 >= Config.LOGIN_TRY_BEFORE_BAN)
			{
				LoginController._log.info("Banning '" + address.getHostAddress() + "' for " + Config.LOGIN_BLOCK_AFTER_BAN + " seconds due to " + failedCount2 + " invalid user/pass attempts");
				this.addBanForAddress(address, Config.LOGIN_BLOCK_AFTER_BAN * 1000);
			}
		}
		else
			_hackProtection.remove(address);
		return ok;
	}
	
	public static boolean isValidLogin(final String text)
	{
		return isValidPattern(text, "^[A-Za-z0-9]{1,16}$");
	}
	
	public static boolean isValidPattern(final String text, final String regex)
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(regex);
		}
		catch (PatternSyntaxException e)
		{
			pattern = Pattern.compile(".*");
		}
		final Matcher regexp = pattern.matcher(text);
		return regexp.matches();
	}
	
	static
	{
		_log = Logger.getLogger(LoginController.class.getName());
	}
	
	public enum AuthLoginResult
	{
		INVALID_PASSWORD,
		ACCOUNT_BANNED,
		ALREADY_ON_LS,
		ALREADY_ON_GS,
		AUTH_SUCCESS;
	}
	
	class FailedLoginAttempt
	{
		private int _count;
		private long _lastAttempTime;
		private String _lastPassword;
		
		public FailedLoginAttempt(final InetAddress address, final String lastPassword)
		{
			_count = 1;
			_lastAttempTime = System.currentTimeMillis();
			_lastPassword = lastPassword;
		}
		
		public void increaseCounter(final String password)
		{
			if (!_lastPassword.equals(password))
			{
				if (System.currentTimeMillis() - _lastAttempTime < 300000L)
					++_count;
				else
					_count = 1;
				_lastPassword = password;
				_lastAttempTime = System.currentTimeMillis();
			}
			else
				_lastAttempTime = System.currentTimeMillis();
		}
		
		public int getCount()
		{
			return _count;
		}
		
		public void increaseCounter()
		{
			++_count;
		}
	}
	
	class BanInfo
	{
		private final InetAddress _ipAddress;
		private final long _expiration;
		
		public BanInfo(final InetAddress ipAddress, final long expiration)
		{
			_ipAddress = ipAddress;
			_expiration = expiration;
		}
		
		public InetAddress getAddress()
		{
			return _ipAddress;
		}
		
		public boolean hasExpired()
		{
			return System.currentTimeMillis() > _expiration && _expiration > 0L;
		}
	}
	
	private class PurgeThread extends Thread
	{
		public PurgeThread()
		{
			setName("PurgeThread");
		}
		
		@Override
		public void run()
		{
			while (!isInterrupted())
			{
				for (final L2LoginClient client : _loginServerClients.values())
				{
					if (client == null)
						continue;
					
					if (client.getConnectionStartTime() + LOGIN_TIMEOUT < System.currentTimeMillis())
						client.close(LoginFailReason.REASON_ACCESS_FAILED);
				}
				
				try
				{
					Thread.sleep(LOGIN_TIMEOUT / 2);
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		}
	}
}