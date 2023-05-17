package com.l2jmega.loginserver;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class GameServerTable
{
	private static Logger _log;
	private static Map<Integer, String> _serverNames;
	private final Map<Integer, GameServerInfo> _gameServerTable;
	private KeyPair[] _keyPairs;

	protected GameServerTable()
	{
		_gameServerTable = new ConcurrentHashMap<>();
		loadServerNames();
		GameServerTable._log.info("Loaded " + GameServerTable._serverNames.size() + " server names.");
		loadRegisteredGameServers();
		GameServerTable._log.info("Loaded " + _gameServerTable.size() + " registered gameserver(s).");
		initRSAKeys();
		GameServerTable._log.info("Cached " + _keyPairs.length + " RSA keys for gameserver communication.");
	}

	private void initRSAKeys()
	{
		try
		{
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4));
			_keyPairs = new KeyPair[10];
			for (int i = 0; i < 10; ++i)
				_keyPairs[i] = keyGen.genKeyPair();
		}
		catch (Exception e)
		{
			GameServerTable._log.severe("GameServerTable: Error loading RSA keys for Game Server communication!");
		}
	}

	private static void loadServerNames()
	{
		try
		{
			final File f = new File("servername.xml");
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			final Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				if (d.getNodeName().equalsIgnoreCase("server"))
				{
					final NamedNodeMap attrs = d.getAttributes();
					final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
					final String name = attrs.getNamedItem("name").getNodeValue();
					GameServerTable._serverNames.put(id, name);
				}
		}
		catch (Exception e)
		{
			GameServerTable._log.warning("GameServerTable: servername.xml could not be loaded.");
		}
	}

	private void loadRegisteredGameServers()
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM gameservers");
			final ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				final int id = rset.getInt("server_id");
				_gameServerTable.put(id, new GameServerInfo(id, stringToHex(rset.getString("hexid"))));
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			GameServerTable._log.severe("GameServerTable: Error loading registered game servers!");
		}
	}

	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return _gameServerTable;
	}

	public GameServerInfo getRegisteredGameServerById(final int id)
	{
		return _gameServerTable.get(id);
	}

	public boolean hasRegisteredGameServerOnId(final int id)
	{
		return _gameServerTable.containsKey(id);
	}

	public boolean registerWithFirstAvaliableId(final GameServerInfo gsi)
	{
		synchronized (_gameServerTable)
		{
			for (final Map.Entry<Integer, String> entry : GameServerTable._serverNames.entrySet())
				if (!_gameServerTable.containsKey(entry.getKey()))
				{
					_gameServerTable.put(entry.getKey(), gsi);
					gsi.setId(entry.getKey());
					return true;
				}
		}
		return false;
	}

	public boolean register(final int id, final GameServerInfo gsi)
	{
		synchronized (_gameServerTable)
		{
			if (!_gameServerTable.containsKey(id))
			{
				_gameServerTable.put(id, gsi);
				gsi.setId(id);
				return true;
			}
		}
		return false;
	}

	public void registerServerOnDB(final GameServerInfo gsi)
	{
		this.registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getExternalHost());
	}

	public void registerServerOnDB(final byte[] hexId, final int id, final String externalHost)
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			final PreparedStatement statement = con.prepareStatement("INSERT INTO gameservers (hexid,server_id,host) values (?,?,?)");
			statement.setString(1, hexToString(hexId));
			statement.setInt(2, id);
			statement.setString(3, externalHost);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			GameServerTable._log.warning("GameServerTable: SQL error while saving gameserver: " + e);
		}
	}

	public String getServerNameById(final int id)
	{
		return getServerNames().get(id);
	}

	public Map<Integer, String> getServerNames()
	{
		return GameServerTable._serverNames;
	}

	public KeyPair getKeyPair()
	{
		return _keyPairs[Rnd.get(10)];
	}

	private static byte[] stringToHex(final String string)
	{
		return new BigInteger(string, 16).toByteArray();
	}

	private static String hexToString(final byte[] hex)
	{
		if (hex == null)
			return "null";
		return new BigInteger(hex).toString(16);
	}

	public static GameServerTable getInstance()
	{
		return SingletonHolder._instance;
	}

	static
	{
		GameServerTable._log = Logger.getLogger(GameServerTable.class.getName());
		GameServerTable._serverNames = new HashMap<>();
	}

	public static class GameServerInfo
	{
		private int _id;
		private final byte[] _hexId;
		private boolean _isAuthed;
		private GameServerThread _gst;
		private int _status;
		private String _internalIp;
		private String _externalIp;
		private String _externalHost;
		private int _port;
		private boolean _isTestServer;
		private boolean _isShowingClock;
		private boolean _isShowingBrackets;
		private int _maxPlayers;

		public GameServerInfo(final int id, final byte[] hexId, final GameServerThread gst)
		{
			_id = id;
			_hexId = hexId;
			_gst = gst;
			_status = 4;
		}

		public GameServerInfo(final int id, final byte[] hexId)
		{
			this(id, hexId, null);
		}

		public void setId(final int id)
		{
			_id = id;
		}

		public int getId()
		{
			return _id;
		}

		public byte[] getHexId()
		{
			return _hexId;
		}

		public void setAuthed(final boolean isAuthed)
		{
			_isAuthed = isAuthed;
		}

		public boolean isAuthed()
		{
			return _isAuthed;
		}

		public void setGameServerThread(final GameServerThread gst)
		{
			_gst = gst;
		}

		public GameServerThread getGameServerThread()
		{
			return _gst;
		}

		public void setStatus(final int status)
		{
			_status = status;
		}

		public int getStatus()
		{
			return _status;
		}

		public int getCurrentPlayerCount()
		{
			if (_gst == null)
				return 0;
			return _gst.getPlayerCount();
		}

		public void setInternalIp(final String internalIp)
		{
			_internalIp = internalIp;
		}

		public String getInternalHost()
		{
			return _internalIp;
		}

		public void setExternalIp(final String externalIp)
		{
			_externalIp = externalIp;
		}

		public String getExternalIp()
		{
			return _externalIp;
		}

		public void setExternalHost(final String externalHost)
		{
			_externalHost = externalHost;
		}

		public String getExternalHost()
		{
			return _externalHost;
		}

		public int getPort()
		{
			return _port;
		}

		public void setPort(final int port)
		{
			_port = port;
		}

		public void setMaxPlayers(final int maxPlayers)
		{
			_maxPlayers = maxPlayers;
		}

		public int getMaxPlayers()
		{
			return _maxPlayers;
		}

		public boolean isPvp()
		{
			return true;
		}

		public void setTestServer(final boolean val)
		{
			_isTestServer = val;
		}

		public boolean isTestServer()
		{
			return _isTestServer;
		}

		public void setShowingClock(final boolean clock)
		{
			_isShowingClock = clock;
		}

		public boolean isShowingClock()
		{
			return _isShowingClock;
		}

		public void setShowingBrackets(final boolean val)
		{
			_isShowingBrackets = val;
		}

		public boolean isShowingBrackets()
		{
			return _isShowingBrackets;
		}

		public void setDown()
		{
			setAuthed(false);
			setPort(0);
			setGameServerThread(null);
			setStatus(4);
		}
	}

	private static class SingletonHolder
	{
		protected static final GameServerTable _instance;

		static
		{
			_instance = new GameServerTable();
		}
	}
}
