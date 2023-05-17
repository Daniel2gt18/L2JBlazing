package com.l2jmega.gameserver.scripting.scripts.ai.group;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class SearchingMaster extends L2AttackableAIScript
{
	private static final int[] MOBS =
	{
		20965,
		20966,
		20967,
		20968,
		20969,
		20970,
		20971,
		20972,
		20973
	};
	
	public SearchingMaster()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(MOBS);
	}
	
	@Override
	public String onAttack(Npc npc, Player player, int damage, boolean isPet, L2Skill skill)
	{
		if (player == null)
			return null;
		
		attack(((Attackable) npc), player);
		return super.onAttack(npc, player, damage, isPet, skill);
	}
}