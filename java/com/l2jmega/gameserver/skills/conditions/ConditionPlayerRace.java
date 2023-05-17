package com.l2jmega.gameserver.skills.conditions;

import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.skills.Env;

/**
 * @author mkizub
 */
public class ConditionPlayerRace extends Condition
{
	private final ClassRace _race;
	
	public ConditionPlayerRace(ClassRace race)
	{
		_race = race;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getPlayer() == null)
			return false;
		
		return env.getPlayer().getRace() == _race;
	}
}