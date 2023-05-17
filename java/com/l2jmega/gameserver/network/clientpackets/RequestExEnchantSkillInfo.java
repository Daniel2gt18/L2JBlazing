package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.model.L2EnchantSkillData;
import com.l2jmega.gameserver.model.L2EnchantSkillLearn;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ExEnchantSkillInfo;

/**
 * Format chdd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfo extends L2GameClientPacket
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
		
		Player activeChar = getClient().getActiveChar();
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
		
		if (!trainer.getTemplate().canTeach(activeChar.getClassId()))
			return;
		
		// Try to find enchant skill.
		for (L2EnchantSkillLearn esl : SkillTreeTable.getInstance().getAvailableEnchantSkills(activeChar))
		{
			if (esl == null)
				continue;
			
			if (esl.getId() == _skillId && esl.getLevel() == _skillLevel)
			{
				L2EnchantSkillData data = SkillTreeTable.getInstance().getEnchantSkillData(esl.getEnchant());
				// Enchant skill or enchant data not found.
				if (data == null)
					return;
				
				// Send ExEnchantSkillInfo packet.
				ExEnchantSkillInfo esi = new ExEnchantSkillInfo(_skillId, _skillLevel, data.getCostSp(), data.getCostExp(), data.getRate(activeChar.getLevel()));
				if (Config.ES_SP_BOOK_NEEDED)
					if (data.getItemId() != 0 && data.getItemCount() != 0)
						esi.addRequirement(4, data.getItemId(), data.getItemCount(), 0);
				sendPacket(esi);
				
				break;
			}
		}
	}
}