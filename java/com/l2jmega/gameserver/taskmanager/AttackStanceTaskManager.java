package com.l2jmega.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Cubic;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.AutoAttackStop;

/**
 * Turns off attack stance of {@link Creature} after PERIOD ms.
 * @author Luca Baldi, Hasha
 */
public final class AttackStanceTaskManager implements Runnable
{
	private static final long ATTACK_STANCE_PERIOD = 15000; // 15 seconds
	
	private final Map<Creature, Long> _characters = new ConcurrentHashMap<>();
	
	public static final AttackStanceTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected AttackStanceTaskManager()
	{
		// Run task each second.
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	/**
	 * Adds {@link Creature} to the AttackStanceTask.
	 * @param character : {@link Creature} to be added and checked.
	 */
	public final void add(Creature character)
	{
		if (character instanceof Playable)
		{
			for (Cubic cubic : character.getActingPlayer().getCubics().values())
				if (cubic.getId() != Cubic.LIFE_CUBIC)
					cubic.doAction();
		}
		
		_characters.put(character, System.currentTimeMillis() + ATTACK_STANCE_PERIOD);
	}
	
	/**
	 * Removes {@link Creature} from the AttackStanceTask.
	 * @param character : {@link Creature} to be removed.
	 */
	public final void remove(Creature character)
	{
		if (character instanceof Summon)
			character = character.getActingPlayer();
		
		_characters.remove(character);
	}
	
	/**
	 * Tests if {@link Creature} is in AttackStanceTask.
	 * @param character : {@link Creature} to be removed.
	 * @return boolean : True when {@link Creature} is in attack stance.
	 */
	public final boolean isInAttackStance(Creature character)
	{
		if (character instanceof Summon)
			character = character.getActingPlayer();
		
		return _characters.containsKey(character);
	}
	
	@Override
	public final void run()
	{
		// List is empty, skip.
		if (_characters.isEmpty())
			return;
		
		// Get current time.
		final long time = System.currentTimeMillis();
		
		// Loop all characters.
		for (Map.Entry<Creature, Long> entry : _characters.entrySet())
		{
			// Time hasn't passed yet, skip.
			if (time < entry.getValue())
				continue;
			
			// Get character.
			final Creature character = entry.getKey();
			
			// Stop character attack stance animation.
			character.broadcastPacket(new AutoAttackStop(character.getObjectId()));
			
			// Stop pet attack stance animation.
			if (character instanceof Player && ((Player) character).getPet() != null)
				((Player) character).getPet().broadcastPacket(new AutoAttackStop(((Player) character).getPet().getObjectId()));
			
			// Inform character AI and remove task.
			character.getAI().setAutoAttacking(false);
			_characters.remove(character);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final AttackStanceTaskManager _instance = new AttackStanceTaskManager();
	}
}