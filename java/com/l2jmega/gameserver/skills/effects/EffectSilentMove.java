package com.l2jmega.gameserver.skills.effects;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.templates.skills.L2EffectFlag;
import com.l2jmega.gameserver.templates.skills.L2EffectType;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class EffectSilentMove extends L2Effect
{
	public EffectSilentMove(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean onStart()
	{
		super.onStart();
		return true;
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.SILENT_MOVE;
	}
	
	@Override
	public boolean onActionTime()
	{
		// Only cont skills shouldn't end
		if (getSkill().getSkillType() != L2SkillType.CONT)
			return false;
		
		if (getEffected().isDead())
			return false;
		
		double manaDam = calc();
		
		if (manaDam > getEffected().getCurrentMp())
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
			getEffected().sendPacket(sm);
			return false;
		}
		
		getEffected().reduceCurrentMp(manaDam);
		return true;
	}
	
	@Override
	public int getEffectFlags()
	{
		return L2EffectFlag.SILENT_MOVE.getMask();
	}
}