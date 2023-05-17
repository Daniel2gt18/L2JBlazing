package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

/**
 * @author Forsaiken
 */
public class GiveSp implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.GIVE_SP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		for (WorldObject obj : targets)
		{
			Creature target = (Creature) obj;
			if (target != null)
			{
				int spToAdd = (int) skill.getPower();
				target.addExpAndSp(0, spToAdd);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}