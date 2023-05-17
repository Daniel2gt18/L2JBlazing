package com.l2jmega.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.location.Location;

public class PartyMemberPosition extends L2GameServerPacket
{
	Map<Integer, Location> _locations = new HashMap<>();
	
	public PartyMemberPosition(L2Party party)
	{
		reuse(party);
	}
	
	public void reuse(L2Party party)
	{
		_locations.clear();
		for (Player member : party.getPartyMembers())
			_locations.put(member.getObjectId(), new Location(member.getX(), member.getY(), member.getZ()));
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xa7);
		writeD(_locations.size());
		for (Map.Entry<Integer, Location> entry : _locations.entrySet())
		{
			final Location loc = entry.getValue();
			
			writeD(entry.getKey());
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
		}
	}
}