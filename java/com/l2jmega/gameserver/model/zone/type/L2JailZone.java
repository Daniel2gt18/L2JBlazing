package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * A jail zone
 * @author durgus
 */
public class L2JailZone extends L2ZoneType
{
	public L2JailZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.JAIL, true);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
			character.setInsideZone(ZoneId.NO_STORE, true);
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.JAIL, false);
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
			character.setInsideZone(ZoneId.NO_STORE, false);
			
			final Player player = ((Player) character);
			if (player.isInJail() && !player.isInsideZone(ZoneId.JAIL))
			{
				// when a player wants to exit jail even if he is still jailed, teleport him back to jail
				ThreadPool.schedule(new BackToJail(character), 2000);
				player.sendMessage("You cannot cheat your way out of here. You must wait until your jail time is over.");
			}
		}
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	static class BackToJail implements Runnable
	{
		private final Player _activeChar;
		
		BackToJail(Creature character)
		{
			_activeChar = (Player) character;
		}
		
		@Override
		public void run()
		{
			_activeChar.teleToLocation(-114356, -249645, -2984, 0);
		}
	}
}