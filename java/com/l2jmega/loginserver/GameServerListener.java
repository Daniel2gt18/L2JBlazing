package com.l2jmega.loginserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2jmega.Config;

public class GameServerListener extends FloodProtectedListener
{
	private static Logger _log;
	private static List<GameServerThread> _gameServers;

	public GameServerListener() throws IOException
	{
		super(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT);
	}

	@Override
	public void addClient(final Socket s)
	{
		if (Config.DEBUG)
			GameServerListener._log.info("Received gameserver connection from: " + s.getInetAddress().getHostAddress());
		final GameServerThread gst = new GameServerThread(s);
		GameServerListener._gameServers.add(gst);
	}

	public void removeGameServer(final GameServerThread gst)
	{
		GameServerListener._gameServers.remove(gst);
	}

	static
	{
		GameServerListener._log = Logger.getLogger(GameServerListener.class.getName());
		GameServerListener._gameServers = new ArrayList<>();
	}
}
