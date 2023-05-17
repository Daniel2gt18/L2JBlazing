package com.l2jmega.gameserver.skills.conditions;

import com.l2jmega.gameserver.skills.Env;

/**
 * Used for Trap skills.
 * @author Tryskell
 */
public class ConditionTargetHpMinMax extends Condition
{
	private final int _minHp, _maxHp;
	
	public ConditionTargetHpMinMax(int minHp, int maxHp)
	{
		_minHp = minHp;
		_maxHp = maxHp;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getTarget() == null)
			return false;
		
		int _currentHp = (int) env.getTarget().getCurrentHp() * 100 / env.getTarget().getMaxHp();
		return _currentHp >= _minHp && _currentHp <= _maxHp;
	}
}