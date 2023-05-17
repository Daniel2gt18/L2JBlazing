package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * An arena
 * @author durgus
 */
public class L2ArenaZone extends L2SpawnZone
{
	public L2ArenaZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			if (!character.isInsideZone(ZoneId.PVP))
				((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
		}
		
		character.setInsideZone(ZoneId.PVP, true);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.PVP, false);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		if (character instanceof Player)
		{
			if (!character.isInsideZone(ZoneId.PVP))
				((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
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