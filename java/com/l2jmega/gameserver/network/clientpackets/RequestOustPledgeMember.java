package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.model.pledge.ClanMember;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public final class RequestOustPledgeMember extends L2GameClientPacket
{
	private String _target;
	
	@Override
	protected void readImpl()
	{
		_target = readS();
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
		
		final ClanMember member = clan.getClanMember(_target);
		if (member == null)
		{
			_log.warning(_target + " is not " + clan.getName() + "'s clan member, dismiss aborted.");
			return;
		}
		
		if ((activeChar.getClanPrivileges() & Clan.CP_CL_DISMISS) != Clan.CP_CL_DISMISS)
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (activeChar.getName().equalsIgnoreCase(_target))
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DISMISS_YOURSELF);
			return;
		}
		
		if (member.isOnline() && member.getPlayerInstance().isInCombat())
		{
			activeChar.sendPacket(SystemMessageId.CLAN_MEMBER_CANNOT_BE_DISMISSED_DURING_COMBAT);
			return;
		}
		
		// this also updates the database
		clan.removeClanMember(member.getObjectId(), System.currentTimeMillis() + Config.ALT_CLAN_JOIN_MINUTES * 60000L);
		clan.setCharPenaltyExpiryTime(System.currentTimeMillis() + Config.ALT_CLAN_JOIN_MINUTES * 60000L);
		clan.updateClanInDB();
		
		// Remove the player from the members list.
		if (clan.isSubPledgeLeader(member.getObjectId()))
			clan.broadcastClanStatus(); // refresh clan tab
		else
			clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(_target));
		
		clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_EXPELLED).addString(member.getName()));
		activeChar.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_EXPELLING_CLAN_MEMBER);
		activeChar.sendPacket(SystemMessageId.YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER);
		
		if (member.isOnline())
			member.getPlayerInstance().sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
	}
}