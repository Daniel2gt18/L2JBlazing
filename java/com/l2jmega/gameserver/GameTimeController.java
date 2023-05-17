/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.gameserver;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.ai.CtrlEvent;
import com.l2jmega.gameserver.model.actor.ai.type.CreatureAI;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * Game Time controller class.
 * @author Forsaiken
 */
public final class GameTimeController extends Thread
{
	protected static final Logger _log = Logger.getLogger(GameTimeController.class.getName());
	
	public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	public static final int IG_DAYS_PER_DAY = 6;
	public static final int MILLIS_PER_IG_DAY = (3600000 * 24) / IG_DAYS_PER_DAY;
	public static final int SECONDS_PER_IG_DAY = MILLIS_PER_IG_DAY / 1000;
	public static final int MINUTES_PER_IG_DAY = SECONDS_PER_IG_DAY / 60;
	public static final int TICKS_PER_IG_DAY = SECONDS_PER_IG_DAY * TICKS_PER_SECOND;
	public static final int TICKS_SUN_STATE_CHANGE = TICKS_PER_IG_DAY / 4;
	
	private final Map<Integer, Creature> _movingObjects = new ConcurrentHashMap<>();
	private final long _referenceTime;
	
	public static final GameTimeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected GameTimeController()
	{
		super("GameTimeController");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		_referenceTime = c.getTimeInMillis();
		
		super.start();
	}
	
	public final int getGameTime()
	{
		return (getGameTicks() % TICKS_PER_IG_DAY) / MILLIS_IN_TICK;
	}
	
	public final int getGameHour()
	{
		return getGameTime() / 60;
	}
	
	public final int getGameMinute()
	{
		return getGameTime() % 60;
	}
	
	public final boolean isNight()
	{
		return getGameHour() < 6;
	}
	
	/**
	 * The true GameTime tick. Directly taken from current time. This represents the tick of the time.
	 * @return
	 */
	public final int getGameTicks()
	{
		return (int) ((System.currentTimeMillis() - _referenceTime) / MILLIS_IN_TICK);
	}
	
	/**
	 * Add a L2Character to movingObjects of GameTimeController.
	 * @param cha The L2Character to add to movingObjects of GameTimeController
	 */
	public final void registerMovingObject(final Creature cha)
	{
		if (cha == null)
			return;
		
		if (_movingObjects.get(cha.getObjectId()) == null)
			_movingObjects.put(cha.getObjectId(), cha);
	}
	
	public final void stopTimer()
	{
		super.interrupt();
		_log.log(Level.INFO, "Stopping " + getClass().getSimpleName());
	}
	
	@Override
	public final void run()
	{
		_log.log(Level.CONFIG, getClass().getSimpleName() + ": Started.");
		
		long nextTickTime, sleepTime;
		boolean isNight = isNight();
		
		if (isNight)
		{
			ThreadPool.execute(new Runnable()
			{
				@Override
				public final void run()
				{
					DayNightSpawnManager.getInstance().notifyChangeMode();
				}
			});
		}
		
		while (true)
		{
			nextTickTime = ((System.currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;
			
			try
			{
				for (Map.Entry<Integer, Creature> e : _movingObjects.entrySet())
				{
					Creature character = e.getValue();
					
					if (character.updatePosition())
					{
						// Destination reached. Remove from map and execute arrive event.
						_movingObjects.remove(e.getKey());
						
						final CreatureAI ai = character.getAI();
						if (ai == null)
							return;
						
						ThreadPool.execute(new Runnable()
						{
							@Override
							public final void run()
							{
								try
								{
									ai.notifyEvent(CtrlEvent.EVT_ARRIVED);
								}
								catch (final Throwable e)
								{
									_log.log(Level.WARNING, "", e);
								}
							}
						});
					}
				}
			}
			catch (final Throwable e)
			{
				_log.log(Level.WARNING, "", e);
			}
			
			sleepTime = nextTickTime - System.currentTimeMillis();
			if (sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (final InterruptedException e)
				{
					
				}
			}
			
			if (isNight() != isNight)
			{
				isNight = !isNight;
				
				ThreadPool.execute(new Runnable()
				{
					@Override
					public final void run()
					{
						DayNightSpawnManager.getInstance().notifyChangeMode();
						
						// "Activate" shadow sense at 00h00 (night) and 06h00 (sunrise)
						for (Player player : World.getInstance().getPlayers())
						{
							// if a player is a DE, verify if he got the skill
							if (player != null && player.getRace() == ClassRace.DARK_ELF)
							{
								final L2Skill skill = SkillTable.getInstance().getInfo(294, 1);
								if (skill != null && player.getSkillLevel(294) == 1)
								{
									player.sendPacket(SystemMessage.getSystemMessage((isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(294));
									
									// You saw nothing and that pack doesn't even exist w_w.
									player.removeSkill(skill, false);
									player.addSkill(skill, false);
								}
							}
						}
					}
				});
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeController _instance = new GameTimeController();
	}
}