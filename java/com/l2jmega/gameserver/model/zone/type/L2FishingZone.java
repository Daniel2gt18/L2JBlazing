package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.zone.L2ZoneType;

/**
 * A fishing zone
 * @author durgus
 */
public class L2FishingZone extends L2ZoneType
{
	public L2FishingZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
	}
	
	@Override
	protected void onExit(Creature character)
	{
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	/*
	 * getWaterZ() this added function returns the Z value for the water surface. In effect this simply returns the upper Z value of the zone. This required some modification of L2ZoneForm, and zone form extentions.
	 */
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}