package com.l2jmega.gameserver.skills.conditions;

import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.taskmanager.GameTimeTaskManager;

/**
 * @author mkizub
 */
public class ConditionGameTime extends Condition
{
	private final boolean _night;
	
	public ConditionGameTime(boolean night)
	{
		_night = night;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return GameTimeTaskManager.getInstance().isNight() == _night;
	}
}