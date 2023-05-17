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

/**
 * @author rapfersan92
 */
public class ChatGlobalManager
{
	private static final Logger _log = Logger.getLogger(ChatGlobalManager.class.getName());
	
	private final Map<Integer, Long> _chats;
	protected final Map<Integer, Long> _chatsTask;
	private ScheduledFuture<?> _scheduler;
	
	public static ChatGlobalManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected ChatGlobalManager()
	{
		_chats = new ConcurrentHashMap<>();
		_chatsTask = new ConcurrentHashMap<>();
		_scheduler = ThreadPool.scheduleAtFixedRate(new ChatTask(), 1000, 1000);
		load();
	}
	
	public void reload()
	{
		_chats.clear();
		_chatsTask.clear();
		if (_scheduler != null)
			_scheduler.cancel(true);
		_scheduler = ThreadPool.scheduleAtFixedRate(new ChatTask(), 1000, 1000);
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT objectId, duration FROM chat_global_manager ORDER BY objectId");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
				_chats.put(rs.getInt("objectId"), rs.getLong("duration"));
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: ChatManager load: " + e.getMessage());
		}
		
		_log.info("ChatGlobal_Manager: Loaded " + _chats.size() + " characters with chat time.");
	}
	
	public void addChatTime(int objectId, long duration)
	{
		_chats.put(objectId, duration);
		_chatsTask.put(objectId, duration);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO chat_global_manager (objectId, duration) VALUES (?, ?)");
			statement.setInt(1, objectId);
			statement.setLong(2, duration);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: addChatTime: " + e.getMessage());
		}
	}
	
	public void updateChatTime(int objectId, long duration)
	{
		duration += _chats.get(objectId);
		_chats.put(objectId, duration);
		_chatsTask.put(objectId, duration);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE chat_global_manager SET duration = ? WHERE objectId = ?");
			statement.setLong(1, duration);
			statement.setInt(2, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("updateChatTime: " + e.getMessage());
		}
	}
	
	public void removeChatTime(int objectId)
	{
		_chats.remove(objectId);
		_chatsTask.remove(objectId);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM chat_global_manager WHERE objectId = ?");
			statement.setInt(1, objectId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("removeChatTime: " + e.getMessage());
		}
	}
	
	public boolean hasChatPrivileges(int objectId)
	{
		return _chats.containsKey(objectId);
	}
	
	public long getChatDuration(int objectId)
	{
		return _chats.get(objectId);
	}
	
	public void addChatTask(int objectId, long duration)
	{
		_chatsTask.put(objectId, duration);
	}
	
	public void removeChatTask(int objectId)
	{
		_chatsTask.remove(objectId);
	}
	
	public class ChatTask implements Runnable
	{
		@Override
		public final void run()
		{
			if (_chatsTask.isEmpty())
				return;
			
			for (Map.Entry<Integer, Long> entry : _chatsTask.entrySet())
			{
				final long duration = entry.getValue();
				if (System.currentTimeMillis() > duration)
				{
					final int objectId = entry.getKey();
                    removeChatTime(objectId);
					final Player player = World.getInstance().getPlayer(objectId);	
					player.sendMessage("global chat available.");
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final ChatGlobalManager _instance = new ChatGlobalManager();
	}
}
