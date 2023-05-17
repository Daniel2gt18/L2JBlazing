package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectType;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public final class EffectDeflectBuff extends L2Effect
{
	public EffectDeflectBuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.PREVENT_BUFF;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getSkill().getSkillType() != L2SkillType.CONT)
			return false;
		
		double manaDam = calc();
		
		if (manaDam > getEffected().getCurrentMp())
		{
			getEffected().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().setIsBuffProtected(true);
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().setIsBuffProtected(false);
	}
}
