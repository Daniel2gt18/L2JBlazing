package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.data.xml.SpellbookData;
import com.l2jmega.gameserver.model.L2PledgeSkillLearn;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.L2SkillLearn;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.AcquireSkillInfo;

public class RequestAcquireSkillInfo extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLevel;
	private int _skillType;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLevel = readD();
		_skillType = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_skillId <= 0 || _skillLevel <= 0)
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Npc trainer = activeChar.getCurrentFolkNPC();
		if (trainer == null)
			return;
		
		if (!activeChar.isInsideRadius(trainer, Npc.INTERACTION_DISTANCE, false, false) && !activeChar.isGM())
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
		if (skill == null)
			return;
		
		switch (_skillType)
		{
			// General skills
			case 0:
				int skillLvl = activeChar.getSkillLevel(_skillId);
				if (skillLvl >= _skillLevel)
					return;
				
				if (Math.max(skillLvl, 0) + 1 != _skillLevel)
					return;
				
				if (!trainer.getTemplate().canTeach(activeChar.getSkillLearningClassId()))
					return;
				
				for (L2SkillLearn sl : SkillTreeTable.getInstance().getAvailableSkills(activeChar, activeChar.getSkillLearningClassId()))
				{
					if (sl.getId() == _skillId && sl.getLevel() == _skillLevel)
					{
						AcquireSkillInfo asi = new AcquireSkillInfo(_skillId, _skillLevel, sl.getSpCost(), 0);
						int spellbookItemId = SpellbookData.getInstance().getBookForSkill(_skillId, _skillLevel);
						if (spellbookItemId != 0)
							asi.addRequirement(99, spellbookItemId, 1, 50);
						sendPacket(asi);
						break;
					}
				}
				break;
			// Common skills
			case 1:
				skillLvl = activeChar.getSkillLevel(_skillId);
				if (skillLvl >= _skillLevel)
					return;
				
				if (Math.max(skillLvl, 0) + 1 != _skillLevel)
					return;
				
				for (L2SkillLearn sl : SkillTreeTable.getInstance().getAvailableFishingDwarvenCraftSkills(activeChar))
				{
					if (sl.getId() == _skillId && sl.getLevel() == _skillLevel)
					{
						AcquireSkillInfo asi = new AcquireSkillInfo(_skillId, _skillLevel, sl.getSpCost(), 1);
						asi.addRequirement(4, sl.getIdCost(), sl.getCostCount(), 0);
						sendPacket(asi);
						break;
					}
				}
				break;
			// Pledge skills.
			case 2:
				if (!activeChar.isClanLeader())
					return;
				
				for (L2PledgeSkillLearn psl : SkillTreeTable.getInstance().getAvailablePledgeSkills(activeChar))
				{
					if (psl.getId() == _skillId && psl.getLevel() == _skillLevel)
					{
						AcquireSkillInfo asi = new AcquireSkillInfo(skill.getId(), skill.getLevel(), psl.getRepCost(), 2);
						if (Config.LIFE_CRYSTAL_NEEDED && psl.getItemId() != 0)
							asi.addRequirement(1, psl.getItemId(), 1, 0);
						sendPacket(asi);
						break;
					}
				}
				break;
		}
	}
}