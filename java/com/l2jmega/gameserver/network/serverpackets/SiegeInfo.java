package com.l2jmega.gameserver.network.serverpackets;

import java.util.Calendar;

import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.pledge.Clan;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related?
 * @author KenM
 */
public class SiegeInfo extends L2GameServerPacket
{
	private final Castle _castle;
	
	public SiegeInfo(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	protected final void writeImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		writeC(0xc9);
		writeD(_castle.getCastleId());
		writeD((_castle.getOwnerId() == player.getClanId() && player.isClanLeader()) ? 0x01 : 0x00);
		writeD(_castle.getOwnerId());
		
		Clan clan = null;
		
		if (_castle.getOwnerId() > 0)
			clan = ClanTable.getInstance().getClan(_castle.getOwnerId());
		if (clan != null)
		{
			writeS(clan.getName());
			writeS(clan.getLeaderName());
			writeD(clan.getAllyId());
			writeS(clan.getAllyName());
		}
		else
		{
			writeS("NPC");
			writeS("");
			writeD(0);
			writeS("");
		}
		
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeD((int) (_castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		writeD(0x00);
	}
}