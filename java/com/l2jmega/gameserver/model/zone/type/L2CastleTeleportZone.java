package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * A castle teleporter zone used for Mass Gatekeepers.
 */
public class L2CastleTeleportZone extends L2ZoneType
{
	private final int[] _spawnLoc;
	private int _castleId;
	
	public L2CastleTeleportZone(int id)
	{
		super(id);
		
		_spawnLoc = new int[5];
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("spawnMinX"))
			_spawnLoc[0] = Integer.parseInt(value);
		else if (name.equals("spawnMaxX"))
			_spawnLoc[1] = Integer.parseInt(value);
		else if (name.equals("spawnMinY"))
			_spawnLoc[2] = Integer.parseInt(value);
		else if (name.equals("spawnMaxY"))
			_spawnLoc[3] = Integer.parseInt(value);
		else if (name.equals("spawnZ"))
			_spawnLoc[4] = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	public void oustAllPlayers()
	{
		if (_characterList.isEmpty())
			return;
		
		for (Player player : getKnownTypeInside(Player.class))
			player.teleToLocation(Rnd.get(_spawnLoc[0], _spawnLoc[1]), Rnd.get(_spawnLoc[2], _spawnLoc[3]), _spawnLoc[4], 0);
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
}