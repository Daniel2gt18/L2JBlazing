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
package com.l2jmega.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author rapfersan92
 */
public class HeroManager
{
	private static final Logger _log = Logger.getLogger(HeroManager.class.getName());
	
	private final Map<Integer, Long> _heros;
	protected final Map<Integer, Long> _herosTask;
	private ScheduledFuture<?> _scheduler;
	
	public static HeroManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected HeroManager()
	{
		_heros = new ConcurrentHashMap<>();
		_herosTask = new ConcurrentHashMap<>();
		_scheduler = ThreadPool.scheduleAtFixedRate(new HeroTask(), 1000, 1000);
		load();
	}
	
	public void reload()
	{
		_heros.clear();
		_herosTask.clear();
		if (_scheduler != null)
			_scheduler.cancel(true);
		_scheduler = ThreadPool.scheduleAtFixedRate(new HeroTask(), 1000, 1000);
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT objectId, duration FROM character_hero ORDER BY objectId");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				_heros.put(rs.getInt("objectId"), rs.getLong("duration"));
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: HeroManager load: " + e.getMessage());
		}
		
		_log.info("HeroManager: Loaded " + _heros.size() + " characters with hero privileges.");
	}
	
	public void addHero(int objectId, long duration)
	{
		_heros.put(objectId, duration);
		_herosTask.put(objectId, duration);
		addHeroPrivileges(objectId, true);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO character_hero (objectId, duration) VALUES (?, ?)");
			statement.setInt(1, objectId);
			statement.setLong(2, duration);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: HeroManager addHero: " + e.getMessage());
		}
	}
	
	public void updateHero(int objectId, long duration)
	{
		_heros.put(objectId, duration);
		_herosTask.put(objectId, duration);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE character_hero SET duration = ? WHERE objectId = ?");
			statement.setLong(1, duration);
			statement.setInt(2, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: HeroManager updateHero: " + e.getMessage());
		}
	}
	
	public void removeHero(int objectId)
	{
		_heros.remove(objectId);
		_herosTask.remove(objectId);
		removeHeroPrivileges(objectId, false);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_hero WHERE objectId = ?");
			statement.setInt(1, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: HeroManager removeHero: " + e.getMessage());
		}
	}
	
	public boolean hasHeroPrivileges(int objectId)
	{
		return _heros.containsKey(objectId);
	}
	
	public long getHeroDuration(int objectId)
	{
		return _heros.get(objectId);
	}
	
	public void addHeroTask(int objectId, long duration)
	{
		_herosTask.put(objectId, duration);
	}
	
	public void removeHeroTask(int objectId)
	{
		_herosTask.remove(objectId);
	}
	
	public void addHeroPrivileges(int objectId, boolean apply)
	{
		final Player player = World.getInstance().getPlayer(objectId);
		player.setHero(true);
		player.setNoble(true, true);
		player.broadcastUserInfo();
		player.addItem("Hero Item", 6842, 1, player, true);
	}
	
	public void removeHeroPrivileges(int objectId, boolean apply)
	{
		final Player player = World.getInstance().getPlayer(objectId);
		player.setHero(false);
		player.broadcastUserInfo();
		player.getInventory().destroyItemByItemId("", 6842, 1, player, null);
	}
	
	public class HeroTask implements Runnable
	{
		@Override
		public final void run()
		{
			if (_herosTask.isEmpty())
				return;
			
			for (Map.Entry<Integer, Long> entry : _herosTask.entrySet())
			{
				final long duration = entry.getValue();
				if (System.currentTimeMillis() > duration)
				{
					final int objectId = entry.getKey();
					removeHero(objectId);
					
					final Player player = World.getInstance().getPlayer(objectId);
					player.sendPacket(new ExShowScreenMessage("Your Hero privileges were removed.", 10000));
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final HeroManager _instance = new HeroManager();
	}
}
