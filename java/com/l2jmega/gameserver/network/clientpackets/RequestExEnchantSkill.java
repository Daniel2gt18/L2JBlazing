package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.model.L2EnchantSkillData;
import com.l2jmega.gameserver.model.L2EnchantSkillLearn;
import com.l2jmega.gameserver.model.L2ShortCut;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Folk;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ShortCutRegister;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.network.serverpackets.UserInfo;

/**
 * Format chdd
 * @author -Wooden-
 */
public final class RequestExEnchantSkill extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLevel;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLevel = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_skillId <= 0 || _skillLevel <= 0)
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.getClassId().level() < 3 || activeChar.getLevel() < 76)
			return;
		
		final Npc trainer = activeChar.getCurrentFolkNPC();
		if (trainer == null)
			return;
		
		if (!activeChar.isInsideRadius(trainer, Npc.INTERACTION_DISTANCE, false, false) && !activeChar.isGM())
			return;
		
		if (activeChar.getSkillLevel(_skillId) >= _skillLevel)
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
		if (skill == null)
			return;
		
		L2EnchantSkillData data = null;
		int baseLvl = 0;
		
		// Try to find enchant skill.
		for (L2EnchantSkillLearn esl : SkillTreeTable.getInstance().getAvailableEnchantSkills(activeChar))
		{
			if (esl == null)
				continue;
			
			if (esl.getId() == _skillId && esl.getLevel() == _skillLevel)
			{
				data = SkillTreeTable.getInstance().getEnchantSkillData(esl.getEnchant());
				baseLvl = esl.getBaseLevel();
				break;
			}
		}
		
		if (data == null)
			return;
		
		// Check exp and sp neccessary to enchant skill.
		if (activeChar.getSp() < data.getCostSp())
		{
			activeChar.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		if (activeChar.getExp() < data.getCostExp())
		{
			activeChar.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		// Check item restriction, and try to consume item.
		if (Config.ES_SP_BOOK_NEEDED)
			if (data.getItemId() != 0 && data.getItemCount() != 0)
			{
				if (!activeChar.destroyItemByItemId("SkillEnchant", data.getItemId(), data.getItemCount(), trainer, true))
				{
					activeChar.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
					return;
				}
			}
		
		// All conditions fulfilled, consume exp and sp.
		activeChar.removeExpAndSp(data.getCostExp(), data.getCostSp());
		
		// Try to enchant skill.
		if (Rnd.get(100) <= data.getRate(activeChar.getLevel()))
		{
			activeChar.addSkill(skill, true);
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
		}
		else
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
			if (_skillLevel > 100)
			{
				_skillLevel = baseLvl;
				activeChar.addSkill(SkillTable.getInstance().getInfo(_skillId, _skillLevel), true);
			}
		}
		activeChar.sendSkillList();
		activeChar.sendPacket(new UserInfo(activeChar));
		
		// Update shortcuts.
		for (L2ShortCut sc : activeChar.getAllShortCuts())
		{
			if (sc.getId() == _skillId && sc.getType() == L2ShortCut.TYPE_SKILL)
			{
				L2ShortCut newsc = new L2ShortCut(sc.getSlot(), sc.getPage(), L2ShortCut.TYPE_SKILL, _skillId, _skillLevel, 1);
				activeChar.sendPacket(new ShortCutRegister(newsc));
				activeChar.registerShortCut(newsc);
			}
		}
		
		// Show enchant skill list.
		Folk.showEnchantSkillList(activeChar, trainer, activeChar.getClassId());
	}
}