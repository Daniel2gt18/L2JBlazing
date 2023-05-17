package com.l2jmega.gameserver.scripting.scripts.ai.group;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.EventType;
import com.l2jmega.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * Summon minions the first time being hitten.<br>
 * For Orcs case, send also a message.
 */
public class SummonMinions extends L2AttackableAIScript
{
	private static final String[] ORCS_WORDS =
	{
		"Come out, you children of darkness!",
		"Destroy the enemy, my brothers!",
		"Show yourselves!",
		"Forces of darkness! Follow me!"
	};
	
	private static final Map<Integer, int[]> MINIONS = new HashMap<>();
	
	static
	{
		MINIONS.put(20767, new int[]
		{
			20768,
			20769,
			20770
		}); // Timak Orc Troop
		MINIONS.put(21524, new int[]
		{
			21525
		}); // Blade of Splendor
		MINIONS.put(21531, new int[]
		{
			21658
		}); // Punishment of Splendor
		MINIONS.put(21539, new int[]
		{
			21540
		}); // Wailing of Splendor
	}
	
	public SummonMinions()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(MINIONS.keySet(), EventType.ON_ATTACK, EventType.ON_KILL);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.isScriptValue(0))
		{
			final int npcId = npc.getNpcId();
			if (npcId != 20767)
			{
				for (int val : MINIONS.get(npcId))
				{
					Attackable newNpc = (Attackable) addSpawn(val, npc, true, 0, false);
					attack(newNpc, attacker);
				}
			}
			else
			{
				for (int val : MINIONS.get(npcId))
					addSpawn(val, npc, true, 0, false);
				
				npc.broadcastNpcSay(Rnd.get(ORCS_WORDS));
			}
			npc.setScriptValue(1);
		}
		
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
}