package com.l2jmega.gameserver.handler.usercommandhandlers;

import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.handler.IUserCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;

public class Escape implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		52
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (activeChar.isCastingNow() || activeChar.isSitting() || activeChar.isMovementDisabled() || activeChar.isOutOfControl() || activeChar.isInOlympiadMode() || activeChar.isInObserverMode() || activeChar.isFestivalParticipant() || activeChar.isInJail())
		{
			activeChar.sendPacket(SystemMessageId.NO_UNSTUCK_PLEASE_SEND_PETITION);
			return false;
		}
		
		if ((TvT.is_started() && activeChar._inEventTvT) || (CTF.is_started() && activeChar._inEventCTF))
		{
			activeChar.sendMessage("You may not use an escape skill in event.");
			return false;
		}
		
		if (activeChar.isArenaProtection() || activeChar.isInsideZone(ZoneId.TOURNAMENT))
		{
			activeChar.sendMessage("You cannot use this skill in Tournament Event/Zone.");
			return false;
		}
		
		activeChar.stopMove(null);
		
		// Official timer 5 minutes, for GM 1 second
		if (activeChar.isGM())
			activeChar.doCast(SkillTable.getInstance().getInfo(2100, 1));
		else
		{
			activeChar.sendPacket(new PlaySound("systemmsg_e.809"));
			activeChar.doCast(SkillTable.getInstance().getInfo(2099, 1));
			activeChar.sendMessage("You use Escape: 30 secunds");
		}
		
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}