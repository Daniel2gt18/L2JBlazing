package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2CastleZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * A zone where your speed is affected.
 * @author kerberos
 */
public class L2SwampZone extends L2CastleZoneType
{
	private int _moveBonus;
	
	public L2SwampZone(int id)
	{
		super(id);
		
		// Setup default speed reduce (in %)
		_moveBonus = -50;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("move_bonus"))
			_moveBonus = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		// Castle traps are active only during siege, or if they're activated.
		if (getCastle() != null && (!isEnabled() || !getCastle().getSiege().isInProgress()))
			return;
		
		character.setInsideZone(ZoneId.SWAMP, true);
		if (character instanceof Player)
			((Player) character).broadcastUserInfo();
	}
	
	@Override
	protected void onExit(Creature character)
	{
		// don't broadcast info if not needed
		if (character.isInsideZone(ZoneId.SWAMP))
		{
			character.setInsideZone(ZoneId.SWAMP, false);
			if (character instanceof Player)
				((Player) character).broadcastUserInfo();
		}
	}
	
	public int getMoveBonus()
	{
		return _moveBonus;
	}
}