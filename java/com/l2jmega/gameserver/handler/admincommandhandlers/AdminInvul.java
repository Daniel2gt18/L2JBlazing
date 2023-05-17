package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 */
public class AdminInvul implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminInvul.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invul",
		"admin_setinvul"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_invul"))
			handleInvul(activeChar);
		if (command.equals("admin_setinvul"))
		{
			WorldObject target = activeChar.getTarget();
			if (target instanceof Player)
				handleInvul((Player) target);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void handleInvul(Player activeChar)
	{
		String text;
		if (activeChar.isInvul())
		{
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal.";
			if (Config.DEBUG)
				_log.fine("GM: Gm removed invul mode from character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		else
		{
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable.";
			if (Config.DEBUG)
				_log.fine("GM: Gm activated invul mode for character " + activeChar.getName() + "(" + activeChar.getObjectId() + ")");
		}
		activeChar.sendMessage(text);
	}
}