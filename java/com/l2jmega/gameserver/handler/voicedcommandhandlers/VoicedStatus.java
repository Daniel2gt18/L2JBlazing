package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.GMViewCharacterInfo;
import com.l2jmega.gameserver.network.serverpackets.GMViewHennaInfo;
import com.l2jmega.gameserver.network.serverpackets.GMViewItemList;
import com.l2jmega.gameserver.network.serverpackets.GMViewSkillInfo;

public class VoicedStatus implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"status",
		"inventory",
		"skills"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("status") && Config.STATUS_CMD)
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof Player))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			Creature targetCharacter = (Creature) activeChar.getTarget();
			Player targetPlayer = targetCharacter.getActingPlayer();
			
			activeChar.sendPacket(new GMViewCharacterInfo(targetPlayer));
			activeChar.sendPacket(new GMViewHennaInfo(targetPlayer));
			return true;
		}
		else if (command.startsWith("inventory") && Config.STATUS_CMD)
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof Player))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			Creature targetCharacter = (Creature) activeChar.getTarget();
			Player targetPlayer = targetCharacter.getActingPlayer();
			
			activeChar.sendPacket(new GMViewItemList(targetPlayer));
			return true;
		}
		else if (command.startsWith("skills") && Config.STATUS_CMD)
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof Player))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			Creature targetCharacter = (Creature) activeChar.getTarget();
			Player targetPlayer = targetCharacter.getActingPlayer();
			
			activeChar.sendPacket(new GMViewSkillInfo(targetPlayer));
			return true;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}