package com.l2jmega.gameserver.skills.conditions;

import java.util.List;

import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.skills.Env;

/**
 * The Class ConditionTargetNpcId.
 */
public class ConditionTargetNpcId extends Condition
{
	private final List<Integer> _npcIds;
	
	/**
	 * Instantiates a new condition target npc id.
	 * @param npcIds the npc ids
	 */
	public ConditionTargetNpcId(List<Integer> npcIds)
	{
		_npcIds = npcIds;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getTarget() instanceof Npc)
			return _npcIds.contains(((Npc) env.getTarget()).getNpcId());
		
		if (env.getTarget() instanceof Door)
			return _npcIds.contains(((Door) env.getTarget()).getDoorId());
		
		return false;
	}
}