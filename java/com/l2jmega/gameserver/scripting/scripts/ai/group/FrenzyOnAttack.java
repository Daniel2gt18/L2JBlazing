package com.l2jmega.gameserver.scripting.scripts.ai.group;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * Frenzy behavior, so far 5 types of orcs.<br>
 * Few others monsters got that skillid, need to investigate later :
 * <ul>
 * <li>Halisha's Officer</li>
 * <li>Executioner of Halisha</li>
 * <li>Alpine Kookaburra</li>
 * <li>Alpine Buffalo</li>
 * <li>Alpine Cougar</li>
 * </ul>
 */
public class FrenzyOnAttack extends L2AttackableAIScript
{
	private static final L2Skill ULTIMATE_BUFF = SkillTable.getInstance().getInfo(4318, 1);
	
	private static final String[] ORCS_WORDS =
	{
		"Dear ultimate power!!!",
		"The battle has just begun!",
		"I never thought I'd use this against a novice!",
		"You won't take me down easily."
	};
	
	public FrenzyOnAttack()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttackId(20270, 20495, 20588, 20778, 21116);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		// The only requirements are HPs < 25% and not already under the buff. It's not 100% aswell.
		if (npc.getCurrentHp() / npc.getMaxHp() < 0.25 && npc.getFirstEffect(ULTIMATE_BUFF) == null && Rnd.get(10) == 0)
		{
			npc.broadcastNpcSay(Rnd.get(ORCS_WORDS));
			npc.setTarget(npc);
			npc.doCast(ULTIMATE_BUFF);
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
}