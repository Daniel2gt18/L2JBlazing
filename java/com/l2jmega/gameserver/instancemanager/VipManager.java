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
public class VipManager
{
	private static final Logger _log = Logger.getLogger(VipManager.class.getName());
	
	private final Map<Integer, Long> _vips;
	protected final Map<Integer, Long> _vipsTask;
	private ScheduledFuture<?> _scheduler;
	
	public static VipManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected VipManager()
	{
		_vips = new ConcurrentHashMap<>();
		_vipsTask = new ConcurrentHashMap<>();
		_scheduler = ThreadPool.scheduleAtFixedRate(new VipTask(), 1000, 1000);
		load();
	}
	
	public void reload()
	{
		_vips.clear();
		_vipsTask.clear();
		if (_scheduler != null)
			_scheduler.cancel(true);
		_scheduler = ThreadPool.scheduleAtFixedRate(new VipTask(), 1000, 1000);
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT objectId, duration FROM character_vip ORDER BY objectId");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				_vips.put(rs.getInt("objectId"), rs.getLong("duration"));
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: VipManager load: " + e.getMessage());
		}
		
		_log.info("VipManager: Loaded " + _vips.size() + " characters with vip privileges.");
	}
	
	public void addVip(int objectId, long duration)
	{
		_vips.put(objectId, duration);
		_vipsTask.put(objectId, duration);
		addVipPrivileges(objectId, true);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO character_vip (objectId, duration) VALUES (?, ?)");
			statement.setInt(1, objectId);
			statement.setLong(2, duration);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: VipManager addVip: " + e.getMessage());
		}
	}
	
	public void updateVip(int objectId, long duration)
	{
		_vips.put(objectId, duration);
		_vipsTask.put(objectId, duration);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE character_vip SET duration = ? WHERE objectId = ?");
			statement.setLong(1, duration);
			statement.setInt(2, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: VipManager updateVip: " + e.getMessage());
		}
	}
	
	public void removeVip(int objectId)
	{
		_vips.remove(objectId);
		_vipsTask.remove(objectId);
		removeVipPrivileges(objectId, false);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_vip WHERE objectId = ?");
			statement.setInt(1, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: VipManager removeVip: " + e.getMessage());
		}
	}
	
	public boolean hasVipPrivileges(int objectId)
	{
		return _vips.containsKey(objectId);
	}
	
	public long getVipDuration(int objectId)
	{
		return _vips.get(objectId);
	}
	
	public void addVipTask(int objectId, long duration)
	{
		_vipsTask.put(objectId, duration);
	}
	
	public void removeVipTask(int objectId)
	{
		_vipsTask.remove(objectId);
	}
	
	public void addVipPrivileges(int objectId, boolean apply)
	{
		final Player player = World.getInstance().getPlayer(objectId);
		player.setVip(true);
		player.broadcastUserInfo();
	}
	
	public void removeVipPrivileges(int objectId, boolean apply)
	{
		final Player player = World.getInstance().getPlayer(objectId);
		player.setVip(false);
		player.broadcastUserInfo();
	}
	
	public class VipTask implements Runnable
	{
		@Override
		public final void run()
		{
			if (_vipsTask.isEmpty())
				return;
			
			for (Map.Entry<Integer, Long> entry : _vipsTask.entrySet())
			{
				final long duration = entry.getValue();
				if (System.currentTimeMillis() > duration)
				{
					final int objectId = entry.getKey();
					removeVip(objectId);
					
					final Player player = World.getInstance().getPlayer(objectId);
					player.sendPacket(new ExShowScreenMessage("Your Vip privileges were removed.", 10000));
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final VipManager _instance = new VipManager();
	}
}
