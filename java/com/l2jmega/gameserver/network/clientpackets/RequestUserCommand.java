package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.handler.IUserCommandHandler;
import com.l2jmega.gameserver.handler.UserCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;

public class RequestUserCommand extends L2GameClientPacket
{
	private int _command;
	
	@Override
	protected void readImpl()
	{
		_command = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);
		if (handler != null)
			handler.useUserCommand(_command, getClient().getActiveChar());
	}
}