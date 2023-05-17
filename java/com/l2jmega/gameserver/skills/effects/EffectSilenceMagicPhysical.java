package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectFlag;
import com.l2jmega.gameserver.templates.skills.L2EffectType;

public class EffectSilenceMagicPhysical extends L2Effect
{
	public EffectSilenceMagicPhysical(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SILENCE_MAGIC_PHYSICAL;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().startMuted();
		getEffected().startPhysicalMuted();
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopMuted(false);
		getEffected().stopPhysicalMuted(false);
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.MUTED.getMask() | L2EffectFlag.PHYSICAL_MUTED.getMask();
	}
}