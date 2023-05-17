package com.l2jmega.gameserver.model.zone;

import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.entity.Castle;

/**
 * Advanced zone type for castle types zones (L2SwampZone, L2DamageZone).
 * @author Tryskell
 */
public abstract class L2CastleZoneType extends L2ZoneType
{
	private int _castleId;
	private Castle _castle;
	
	private boolean _enabled;
	
	protected L2CastleZoneType(int id)
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
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	public Castle getCastle()
	{
		if (_castleId > 0 && _castle == null)
			_castle = CastleManager.getInstance().getCastleById(_castleId);
		
		return _castle;
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public void setEnabled(boolean val)
	{
		_enabled = val;
	}
}