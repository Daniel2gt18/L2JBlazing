package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.model.L2Fishing;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.ShotType;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class FishingSkill implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.PUMPING,
		L2SkillType.REELING
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		final boolean isReelingSkill = skill.getSkillType() == L2SkillType.REELING;
		
		final L2Fishing fish = player.getFishCombat();
		if (fish == null)
		{
			player.sendPacket((isReelingSkill) ? SystemMessageId.CAN_USE_REELING_ONLY_WHILE_FISHING : SystemMessageId.CAN_USE_PUMPING_ONLY_WHILE_FISHING);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Weapon weaponItem = player.getActiveWeaponItem();
		final ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		
		if (weaponInst == null || weaponItem == null)
			return;
		
		final int ssBonus = (activeChar.isChargedShot(ShotType.FISH_SOULSHOT)) ? 2 : 1;
		final double gradeBonus = 1 + weaponItem.getCrystalType().getId() * 0.1;
		
		int damage = (int) (skill.getPower() * gradeBonus * ssBonus);
		int penalty = 0;
		
		// Fish expertise penalty if skill level is superior or equal to 3.
		if (skill.getLevel() - player.getSkillLevel(1315) >= 3)
		{
			penalty = 50;
			damage -= penalty;
			
			player.sendPacket(SystemMessageId.REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY);
		}
		
		if (ssBonus > 1)
			weaponInst.setChargedShot(ShotType.FISH_SOULSHOT, false);
		
		if (isReelingSkill)
			fish.useRealing(damage, penalty);
		else
			fish.usePomping(damage, penalty);
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}