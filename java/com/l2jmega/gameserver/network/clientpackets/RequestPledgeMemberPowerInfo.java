package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.model.pledge.ClanMember;
import com.l2jmega.gameserver.network.serverpackets.PledgeReceivePowerInfo;

/**
 * Format: (ch) dS
 * @author -Wooden-
 */
public final class RequestPledgeMemberPowerInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _pledgeType;
	private String _player;
	
	@Override
	protected void readImpl()
	{
		_pledgeType = readD();
		_player = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Clan clan = activeChar.getClan();
		if (clan == null)
			return;
		
		final ClanMember member = clan.getClanMember(_player);
		if (member == null)
			return;
		
		activeChar.sendPacket(new PledgeReceivePowerInfo(member));
	}
}