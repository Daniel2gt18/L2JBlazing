package com.l2jmega.gameserver.taskmanager;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * Controls game time, informs spawn manager about day/night spawns and players about daytime change. Informs players about their extended activity in game.
 * @author Hasha
 */
public final class GameTimeTaskManager implements Runnable
{
	private static final int MINUTES_PER_DAY = 24 * 60; // 24h * 60m
	
	public static final int HOURS_PER_GAME_DAY = 4; // 4h is 1 game day
	public static final int MINUTES_PER_GAME_DAY = HOURS_PER_GAME_DAY * 60; // 240m is 1 game day
	public static final int SECONDS_PER_GAME_DAY = MINUTES_PER_GAME_DAY * 60; // 14400s is 1 game day
	private static final int MILLISECONDS_PER_GAME_MINUTE = SECONDS_PER_GAME_DAY / (MINUTES_PER_DAY) * 1000; // 10000ms is 1 game minute
	
	private static final int TAKE_BREAK_HOURS = 2; // each 2h
	private static final int TAKE_BREAK_GAME_MINUTES = TAKE_BREAK_HOURS * MINUTES_PER_DAY / HOURS_PER_GAME_DAY; // 2h of real time is 720 game minutes
	
	private static final int SHADOW_SENSE = 294;
	
	private int _time;
	protected boolean _night;
	private final Map<Player, Integer> _players = new ConcurrentHashMap<>();
	
	public static final GameTimeTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected GameTimeTaskManager()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		_time = (int) (System.currentTimeMillis() - cal.getTimeInMillis()) / MILLISECONDS_PER_GAME_MINUTE;
		_night = isNight();
		
		// Run task each 10 seconds.
		ThreadPool.scheduleAtFixedRate(this, MILLISECONDS_PER_GAME_MINUTE, MILLISECONDS_PER_GAME_MINUTE);
	}
	
	/**
	 * Returns how many game days have left since last server start.
	 * @return int : Game day.
	 */
	public final int getGameDay()
	{
		return _time / MINUTES_PER_DAY;
	}
	
	/**
	 * Returns game time in minute format (0-1439).
	 * @return int : Game time.
	 */
	public final int getGameTime()
	{
		return _time % MINUTES_PER_DAY;
	}
	
	/**
	 * Returns game hour (0-23).
	 * @return int : Game hour.
	 */
	public final int getGameHour()
	{
		return (_time % MINUTES_PER_DAY) / 60;
	}
	
	/**
	 * Returns game minute (0-59).
	 * @return int : Game minute.
	 */
	public final int getGameMinute()
	{
		return _time % 60;
	}
	
	/**
	 * Returns game time standard format (00:00-23:59).
	 * @return String : Game time.
	 */
	public final String getGameTimeFormated()
	{
		return String.format("%02d:%02d", getGameHour(), getGameMinute());
	}
	
	/**
	 * Returns game daytime. Night is between 00:00 and 06:00.
	 * @return boolean : True, when there is night.
	 */
	public final boolean isNight()
	{
		return getGameTime() < 360;
	}
	
	/**
	 * Adds {@link Player} to the GameTimeTask to control is activity.
	 * @param player : {@link Player} to be added and checked.
	 */
	public final void add(Player player)
	{
		_players.put(player, _time + TAKE_BREAK_GAME_MINUTES);
	}
	
	/**
	 * Removes {@link Player} from the GameTimeTask.
	 * @param player : {@link Player} to be removed.
	 */
	public final void remove(Creature player)
	{
		_players.remove(player);
	}
	
	@Override
	public final void run()
	{
		// Tick time.
		_time++;
		
		// Shadow Sense skill, if set then perform day/night info.
		L2Skill skill = null;
		
		// Day/night has changed.
		if (_night != isNight())
		{
			// Change day/night.
			_night = !_night;
			
			// Inform day/night spawn manager.
			DayNightSpawnManager.getInstance().notifyChangeMode();
			
			// Set Shadow Sense skill to apply/remove effect from players.
			skill = SkillTable.getInstance().getInfo(SHADOW_SENSE, 1);
		}
		
		// List is empty, skip.
		if (_players.isEmpty())
			return;
		
		// Loop all players.
		for (Map.Entry<Player, Integer> entry : _players.entrySet())
		{
			// Get player.
			final Player player = entry.getKey();
			
			// Player isn't online, skip.
			if (!player.isOnline())
				continue;
			
			// Shadow Sense skill is set and player has Shadow Sense skill, activate/deactivate its effect.
			if (skill != null && player.getSkillLevel(SHADOW_SENSE) > 0)
			{
				// Remove and add Shadow Sense to activate/deactivate effect.
				player.removeSkill(skill, false);
				player.addSkill(skill, false);
				
				// Inform player about effect change.
				player.sendPacket(SystemMessage.getSystemMessage(_night ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(SHADOW_SENSE));
			}
			
			// Activity time has passed already.
			if (_time >= entry.getValue())
			{
				// Inform player about his activity.
				player.sendPacket(SystemMessageId.PLAYING_FOR_LONG_TIME);
				
				// Update activity time.
				entry.setValue(_time + TAKE_BREAK_GAME_MINUTES);
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeTaskManager _instance = new GameTimeTaskManager();
	}
}