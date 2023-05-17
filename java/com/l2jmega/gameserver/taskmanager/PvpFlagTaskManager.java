package com.l2jmega.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;

/**
 * Updates and clears PvP flag of {@link Player} after specified time.
 * @author Tryskell, Hasha
 */
public final class PvpFlagTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	public static final PvpFlagTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected PvpFlagTaskManager()
	{
		// Run task each second.
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	/**
	 * Adds {@link Player} to the PvpFlagTask.
	 * @param player : Player to be added and checked.
	 * @param time : Time in ms, after which the PvP flag is removed.
	 */
	public final void add(Player player, long time)
	{
		_players.put(player, System.currentTimeMillis() + time);
	}
	
	/**
	 * Removes {@link Player} from the PvpFlagTask.
	 * @param player : {@link Player} to be removed.
	 */
	public final void remove(Player player)
	{
		_players.remove(player);
	}
	
	@Override
	public final void run() {
		if (_players.isEmpty())
			return; 
		long currentTime = System.currentTimeMillis();
		for (Map.Entry<Player, Long> entry : _players.entrySet()) {
			Player player = entry.getKey();
			
			if (Config.ENABLE_FLAGZONE && (player.isInsideZone(ZoneId.FLAG) || player.isInsideZone(ZoneId.RAID))) {
				_players.remove(player);
				continue;
			}
			
			long timeLeft = entry.getValue().longValue();
			if (currentTime > timeLeft) {
				player.updatePvPFlag(0);
				_players.remove(player);
				continue;
			}
			if (currentTime > timeLeft - 5000L) {
				player.updatePvPFlag(2);
				continue;
			} 
			player.updatePvPFlag(1);
		} 
	}
	
	private static class SingletonHolder
	{
		protected static final PvpFlagTaskManager _instance = new PvpFlagTaskManager();
	}
}