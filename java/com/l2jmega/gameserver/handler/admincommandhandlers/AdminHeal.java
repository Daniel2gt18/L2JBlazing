package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands: - heal = restores HP/MP/CP on target, name or radius
 */
public class AdminHeal implements IAdminCommandHandler
{
	private static Logger _log = Logger.getLogger(AdminHeal.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_heal"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 7)
			return false;
		
		if (command.equals("admin_heal"))
			handleRes(activeChar);
		else if (command.startsWith("admin_heal"))
		{
			try
			{
				String healTarget = command.substring(11);
				handleRes(activeChar, healTarget);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				if (Config.DEVELOPER)
					System.out.println("Heal error: " + e);
				activeChar.sendMessage("Incorrect target/radius specified.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void handleRes(Player activeChar)
	{
		handleRes(activeChar, null);
	}
	
	private static void handleRes(Player activeChar, String player)
	{
		WorldObject obj = activeChar.getTarget();
		if (player != null)
		{
			Player plyr = World.getInstance().getPlayer(player);
			
			if (plyr != null)
				obj = plyr;
			else
			{
				try
				{
					int radius = Integer.parseInt(player);
					for (Creature character : activeChar.getKnownType(Creature.class))
					{
						character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
						if (character instanceof Player)
							character.setCurrentCp(character.getMaxCp());
					}
					activeChar.sendMessage("Healed within " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException nbe)
				{
				}
			}
		}
		
		if (obj == null)
			obj = activeChar;
		
		if (obj instanceof Creature)
		{
			Creature target = (Creature) obj;
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			
			if (target instanceof Player)
				target.setCurrentCp(target.getMaxCp());
			
			if (Config.DEBUG)
				_log.fine("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") healed character " + target.getName());
		}
		else
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
	}
}