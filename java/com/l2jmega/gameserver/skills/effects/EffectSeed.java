package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectType;

public final class EffectSeed extends L2Effect
{
	private int _power = 1;
	
	public EffectSeed(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SEED;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	public int getPower()
	{
		return _power;
	}
	
	public void increasePower()
	{
		_power++;
	}
}