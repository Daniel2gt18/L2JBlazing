package com.l2jmega.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.gameserver.handler.usercommandhandlers.ChannelDelete;
import com.l2jmega.gameserver.handler.usercommandhandlers.ChannelLeave;
import com.l2jmega.gameserver.handler.usercommandhandlers.ChannelListUpdate;
import com.l2jmega.gameserver.handler.usercommandhandlers.ClanPenalty;
import com.l2jmega.gameserver.handler.usercommandhandlers.ClanWarsList;
import com.l2jmega.gameserver.handler.usercommandhandlers.DisMount;
import com.l2jmega.gameserver.handler.usercommandhandlers.Escape;
import com.l2jmega.gameserver.handler.usercommandhandlers.Loc;
import com.l2jmega.gameserver.handler.usercommandhandlers.Mount;
import com.l2jmega.gameserver.handler.usercommandhandlers.OlympiadStat;
import com.l2jmega.gameserver.handler.usercommandhandlers.PartyInfo;
import com.l2jmega.gameserver.handler.usercommandhandlers.SiegeStatus;
import com.l2jmega.gameserver.handler.usercommandhandlers.Time;

public class UserCommandHandler
{
	private final Map<Integer, IUserCommandHandler> _datatable = new HashMap<>();
	
	public static UserCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected UserCommandHandler()
	{
		registerUserCommandHandler(new ChannelDelete());
		registerUserCommandHandler(new ChannelLeave());
		registerUserCommandHandler(new ChannelListUpdate());
		registerUserCommandHandler(new ClanPenalty());
		registerUserCommandHandler(new ClanWarsList());
		registerUserCommandHandler(new DisMount());
		registerUserCommandHandler(new Escape());
		registerUserCommandHandler(new Loc());
		registerUserCommandHandler(new Mount());
		registerUserCommandHandler(new OlympiadStat());
		registerUserCommandHandler(new PartyInfo());
		registerUserCommandHandler(new SiegeStatus());
		registerUserCommandHandler(new Time());
	}
	
	public void registerUserCommandHandler(IUserCommandHandler handler)
	{
		for (int id : handler.getUserCommandList())
			_datatable.put(id, handler);
	}
	
	public IUserCommandHandler getUserCommandHandler(int userCommand)
	{
		return _datatable.get(userCommand);
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final UserCommandHandler _instance = new UserCommandHandler();
	}
}