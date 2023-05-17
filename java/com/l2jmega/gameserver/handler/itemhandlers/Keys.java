package com.l2jmega.gameserver.handler.itemhandlers;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Chest;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * That handler is used for the different types of keys. Such items aren't consumed until the skill is definitively launched.
 * @author Tryskell
 */
public class Keys implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player activeChar = (Player) playable;
		if (activeChar.isSitting())
		{
			activeChar.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		
		if (activeChar.isMovementDisabled())
			return;
		
		final Creature target = (Creature) activeChar.getTarget();
		
		// Target must be a valid chest (not dead or already interacted).
		if (!(target instanceof Chest) || target.isDead() || ((Chest) target).isInteracted())
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
		{
			_log.info(item.getName() + " does not have registered any skill for handler.");
			return;
		}
		
		for (IntIntHolder skillInfo : skills)
		{
			if (skillInfo == null)
				continue;
			
			final L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill == null)
				continue;
			
			// Key consumption is made on skill call, not on item call.
			playable.useMagic(itemSkill, false, false);
		}
	}
}