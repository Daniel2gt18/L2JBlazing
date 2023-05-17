package com.l2jmega.gameserver.handler;

import java.util.logging.Logger;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public interface ISkillHandler
{
	public static Logger _log = Logger.getLogger(ISkillHandler.class.getName());
	
	/**
	 * this is the worker method that is called when using a skill.
	 * @param activeChar The Creature who uses that skill.
	 * @param skill The skill object itself.
	 * @param targets Eventual targets.
	 */
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets);
	
	/**
	 * this method is called at initialization to register all the skill ids automatically
	 * @return all known itemIds
	 */
	public L2SkillType[] getSkillIds();
}