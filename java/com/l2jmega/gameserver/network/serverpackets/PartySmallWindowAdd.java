/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.actor.instance.Player;

public final class PartySmallWindowAdd extends L2GameServerPacket
{
	private final Player _member;
	private final int _leaderId;
	private final int _distribution;
	
	public PartySmallWindowAdd(Player member, L2Party party)
	{
		_member = member;
		_leaderId = party.getPartyLeaderOID();
		_distribution = party.getLootDistribution();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x4f);
		writeD(_leaderId); // c3
		writeD(_distribution); // c3
		writeD(_member.getObjectId());
		writeS(_member.getName());
		writeD((int) _member.getCurrentCp()); // c4
		writeD(_member.getMaxCp()); // c4
		writeD((int) _member.getCurrentHp());
		writeD(_member.getMaxHp());
		writeD((int) _member.getCurrentMp());
		writeD(_member.getMaxMp());
		writeD(_member.getLevel());
		writeD(_member.getClassId().getId());
		writeD(0);// writeD(0x01); ??
		writeD(0);
	}
}