package com.l2jmega.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.mmocore.SelectorConfig;
import com.l2jmega.commons.mmocore.SelectorThread;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.L2jMega;

public class L2LoginServer
{
	private static final Logger _log;
	public static final int PROTOCOL_REV = 258;
	private static L2LoginServer loginServer;
	private GameServerListener _gameServerListener;
	private SelectorThread<L2LoginClient> _selectorThread;
	
	public static void main(final String[] args) throws Exception
	{
		L2LoginServer.loginServer = new L2LoginServer();
	}
	
	public static L2LoginServer getInstance()
	{
		return L2LoginServer.loginServer;
	}
	
	public L2LoginServer() throws Exception
	{		
		// Create log folder
		new File("./log").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File(Config.LOGGING)))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		StringUtil.printSection("Project 2022");
		L2jMega.info();
		StringUtil.printSection("Base aCis");
		Config.loadLoginServer();
		L2DatabaseFactory.getInstance();
		StringUtil.printSection("LoginController");
		LoginController.load();
		GameServerTable.getInstance();
		StringUtil.printSection("Ban List");
		loadBanFile();
		StringUtil.printSection("IP, Ports & Socket infos");
		InetAddress bindAddress = null;
		if (!Config.LOGIN_BIND_ADDRESS.equals("*"))
			try
			{
				bindAddress = InetAddress.getByName(Config.LOGIN_BIND_ADDRESS);
			}
			catch (UnknownHostException e1)
			{
				L2LoginServer._log.severe("WARNING: The LoginServer bind address is invalid, using all available IPs. Reason: " + e1.getMessage());
				if (Config.DEVELOPER)
					e1.printStackTrace();
			}
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		final L2LoginPacketHandler lph = new L2LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			_selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException e2)
		{
			L2LoginServer._log.severe("FATAL: Failed to open selector. Reason: " + e2.getMessage());
			if (Config.DEVELOPER)
				e2.printStackTrace();
			System.exit(1);
		}
		try
		{
			(_gameServerListener = new GameServerListener()).start();
			L2LoginServer._log.info("Listening for gameservers on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
		}
		catch (IOException e2)
		{
			L2LoginServer._log.severe("FATAL: Failed to start the gameserver listener. Reason: " + e2.getMessage());
			if (Config.DEVELOPER)
				e2.printStackTrace();
			System.exit(1);
		}
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_LOGIN);
		}
		catch (IOException e2)
		{
			L2LoginServer._log.severe("FATAL: Failed to open server socket. Reason: " + e2.getMessage());
			if (Config.DEVELOPER)
				e2.printStackTrace();
			System.exit(1);
		}
		_selectorThread.start();
		L2LoginServer._log.info("Loginserver ready on " + (bindAddress == null ? "*" : bindAddress.getHostAddress()) + ":" + Config.PORT_LOGIN);
		StringUtil.printSection("Waiting for gameserver answer");
	}
	
	public GameServerListener getGameServerListener()
	{
		return _gameServerListener;
	}
	
	private static void loadBanFile()
	{
		final File banFile = new File("config/Banned_ip.ini");
		if (banFile.exists() && banFile.isFile())
		{
			try (final LineNumberReader reader = new LineNumberReader(new FileReader(banFile)))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					if (line.length() > 0 && line.charAt(0) != '#')
					{
						String[] parts = line.split("#");
						line = parts[0];
						parts = line.split(" ");
						final String address = parts[0];
						long duration = 0L;
						if (parts.length > 1)
							try
							{
								duration = Long.parseLong(parts[1]);
							}
							catch (NumberFormatException e2)
							{
								L2LoginServer._log.warning("Skipped: Incorrect ban duration (" + parts[1] + ") on Banned_ip.ini. Line: " + reader.getLineNumber());
								continue;
							}
						try
						{
							LoginController.getInstance().addBanForAddress(address, duration);
						}
						catch (UnknownHostException e3)
						{
							L2LoginServer._log.warning("Skipped: Invalid address (" + parts[0] + ") on Banned_ip.ini. Line: " + reader.getLineNumber());
						}
					}
				}
			}
			catch (IOException e)
			{
				L2LoginServer._log.warning("Error while reading Banned_ip.ini. Details: " + e.getMessage());
				if (Config.DEVELOPER)
					e.printStackTrace();
			}
			L2LoginServer._log.info("Loaded " + LoginController.getInstance().getBannedIps().size() + " IP(s) from Banned_ip.ini.");
		}
		else
			L2LoginServer._log.warning("Banned_ip.ini is missing. Ban listing is skipped.");
	}
	
	public void shutdown(final boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
	
	static
	{
		_log = Logger.getLogger(L2LoginServer.class.getName());
	}
}
