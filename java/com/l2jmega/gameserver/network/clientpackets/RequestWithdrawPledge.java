package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public final class RequestWithdrawPledge extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Clan clan = activeChar.getClan();
		if (clan == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
			return;
		}
		
		if (activeChar.isClanLeader())
		{
			activeChar.sendPacket(SystemMessageId.CLAN_LEADER_CANNOT_WITHDRAW);
			return;
		}
		
		if (activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_LEAVE_DURING_COMBAT);
			return;
		}
		
		clan.removeClanMember(activeChar.getObjectId(), System.currentTimeMillis() + Config.ALT_CLAN_JOIN_MINUTES * 60000L);
		clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addCharName(activeChar));
		
		// Remove the player from the members list.
		if (clan.isSubPledgeLeader(activeChar.getObjectId()))
			clan.broadcastClanStatus(); // refresh list
		else
			clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(activeChar.getName()));
		
		activeChar.sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_CLAN);
		activeChar.sendPacket(new ExShowScreenMessage("You will be able to join a clan again in " + Config.ALT_CLAN_JOIN_MINUTES + " minute(s)..", 15000));
		activeChar.sendMessage("You will be able to join a clan again in " + Config.ALT_CLAN_JOIN_MINUTES + " minute(s)..");
	}
}