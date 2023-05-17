package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class Craft implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.COMMON_CRAFT,
		L2SkillType.DWARVEN_CRAFT
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (activeChar == null || !(activeChar instanceof Player))
			return;
		
		Player player = (Player) activeChar;
		if (player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING);
			return;
		}
		RecipeTable.getInstance().requestBookOpen(player, skill.getSkillType() == L2SkillType.DWARVEN_CRAFT);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}