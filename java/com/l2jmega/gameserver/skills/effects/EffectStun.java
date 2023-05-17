package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectFlag;
import com.l2jmega.gameserver.templates.skills.L2EffectType;

/**
 * @author mkizub
 */
final class EffectStun extends L2Effect
{
	public EffectStun(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.STUN;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startStunning();
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopStunning(false);
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public boolean onSameEffect(L2Effect effect)
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.STUNNED.getMask();
	}
}