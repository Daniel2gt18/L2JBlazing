package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * A castle zone handles following spawns type :
 * <ul>
 * <li>Generic spawn locs : owner_restart_point_list (spawns used on siege, to respawn on mass gatekeeper room.</li>
 * <li>Chaotic spawn locs : banish_point_list (spawns used to banish players on regular owner maintenance).</li>
 * </ul>
 */
public class L2CastleZone extends L2SpawnZone
{
	private int _castleId;
	
	public L2CastleZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.CASTLE, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.CASTLE, false);
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	/**
	 * Removes all foreigners from the castle
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		if (_characterList.isEmpty())
			return;
		
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.getClanId() == owningClanId)
				continue;
			
			player.teleToLocation(getChaoticSpawnLoc(), 20);
		}
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
}