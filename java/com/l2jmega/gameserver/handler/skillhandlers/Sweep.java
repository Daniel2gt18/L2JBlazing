package com.l2jmega.gameserver.handler.skillhandlers;

import java.util.List;

import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

/**
 * @author _drunk_
 */
public class Sweep implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.SWEEP
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Attackable))
				continue;
			
			final Attackable monster = ((Attackable) target);
			if (!monster.isSpoiled())
				continue;
			
			final List<IntIntHolder> items = monster.getSweepItems();
			if (items.isEmpty())
				continue;
			
			for (IntIntHolder item : items)
			{
				if (player.isInParty())
					player.getParty().distributeItem(player, item, true, monster);
				else
					player.addItem("Sweep", item.getId(), item.getValue(), player, true);
			}
			items.clear();
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}