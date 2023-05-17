package com.l2jmega.gameserver.skills.l2skills;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.PetItemList;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.templates.StatsSet;

/**
 * @author Nemesiss
 */
public class L2SkillCreateItem extends L2Skill
{
	private final int[] _createItemId;
	private final int _createItemCount;
	private final int _randomCount;
	
	public L2SkillCreateItem(StatsSet set)
	{
		super(set);
		_createItemId = set.getIntegerArray("create_item_id");
		_createItemCount = set.getInteger("create_item_count", 0);
		_randomCount = set.getInteger("random_count", 1);
	}
	
	/**
	 * @see com.l2jmega.gameserver.model.L2Skill#useSkill(com.l2jmega.gameserver.model.actor.Creature, com.l2jmega.gameserver.model.WorldObject[])
	 */
	@Override
	public void useSkill(Creature activeChar, WorldObject[] targets)
	{
		Player player = activeChar.getActingPlayer();
		if (activeChar.isAlikeDead())
			return;
		
		if (activeChar instanceof Playable)
		{
			if (_createItemId == null || _createItemCount == 0)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_PREPARED_FOR_REUSE);
				sm.addSkillName(this);
				activeChar.sendPacket(sm);
				return;
			}
			
			int count = _createItemCount + Rnd.get(_randomCount);
			int rndid = Rnd.get(_createItemId.length);
			
			if (activeChar instanceof Player)
				player.addItem("Skill", _createItemId[rndid], count, activeChar, true);
			else if (activeChar instanceof Pet)
			{
				activeChar.getInventory().addItem("Skill", _createItemId[rndid], count, player, activeChar);
				player.sendPacket(new PetItemList((Pet) activeChar));
			}
		}
	}
}