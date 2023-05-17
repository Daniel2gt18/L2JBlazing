package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectType;

/**
 * Effect will generate charges for Player targets Number of charges in "value", maximum number in "count" effect variables
 * @author DS
 */
public class EffectIncreaseCharges extends L2Effect
{
	public EffectIncreaseCharges(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.INCREASE_CHARGES;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() == null)
			return false;
		
		if (!(getEffected() instanceof Player))
			return false;
		
		((Player) getEffected()).increaseCharges((int) calc(), getCount());
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false; // abort effect even if count > 1
	}
}