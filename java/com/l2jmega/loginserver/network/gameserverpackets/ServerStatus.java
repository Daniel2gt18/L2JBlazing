package com.l2jmega.loginserver.network.gameserverpackets;

import java.util.logging.Logger;

import com.l2jmega.loginserver.GameServerTable;
import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class ServerStatus extends ClientBasePacket
{
	protected static Logger _log;
	public static final String[] STATUS_STRING;
	public static final int SERVER_LIST_STATUS = 1;
	public static final int SERVER_LIST_CLOCK = 2;
	public static final int SERVER_LIST_SQUARE_BRACKET = 3;
	public static final int MAX_PLAYERS = 4;
	public static final int TEST_SERVER = 5;
	public static final int STATUS_AUTO = 0;
	public static final int STATUS_GOOD = 1;
	public static final int STATUS_NORMAL = 2;
	public static final int STATUS_FULL = 3;
	public static final int STATUS_DOWN = 4;
	public static final int STATUS_GM_ONLY = 5;
	public static final int ON = 1;
	public static final int OFF = 0;

	public ServerStatus(final byte[] decrypt, final int serverId)
	{
		super(decrypt);
		final GameServerTable.GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServers().get(serverId);
		if (gsi != null)
			for (int size = readD(), i = 0; i < size; ++i)
			{
				final int type = readD();
				final int value = readD();
				switch (type)
				{
					case 1:
					{
						gsi.setStatus(value);
						break;
					}
					case 2:
					{
						gsi.setShowingClock(value == 1);
						break;
					}
					case 3:
					{
						gsi.setShowingBrackets(value == 1);
						break;
					}
					case 5:
					{
						gsi.setTestServer(value == 1);
						break;
					}
					case 4:
					{
						gsi.setMaxPlayers(value);
						break;
					}
				}
			}
	}

	static
	{
		ServerStatus._log = Logger.getLogger(ServerStatus.class.getName());
		STATUS_STRING = new String[]
		{
			"Auto",
			"Good",
			"Normal",
			"Full",
			"Down",
			"Gm Only"
		};
	}
}
