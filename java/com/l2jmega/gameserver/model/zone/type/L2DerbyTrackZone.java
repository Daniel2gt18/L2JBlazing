package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * The Monster Derby Track Zone
 * @author durgus
 */
public class L2DerbyTrackZone extends L2PeaceZone
{
	public L2DerbyTrackZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Playable)
		{
			character.setInsideZone(ZoneId.MONSTER_TRACK, true);
			character.setInsideZone(ZoneId.PEACE, true);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Playable)
		{
			character.setInsideZone(ZoneId.MONSTER_TRACK, false);
			character.setInsideZone(ZoneId.PEACE, false);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		}
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
}