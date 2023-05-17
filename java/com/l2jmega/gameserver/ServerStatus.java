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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.lang.StringUtil;

import com.l2jmega.gameserver.model.World;


/**
 * @author Admin
 */
public class ServerStatus
{
	protected static final Logger _log = Logger.getLogger("Loader");
	protected ScheduledFuture<?> _scheduledTask;
	
	protected ServerStatus()
	{
		_scheduledTask = ThreadPool.scheduleAtFixedRate(new ServerStatusTask(), 1800000, 3600000);
	}
	
	protected class ServerStatusTask implements Runnable
	{
		protected final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");
		
		@Override
		public void run()
		{
			StringUtil.printSection("Server Status");
			_log.info("Server Time: " + fmt.format(new Date(System.currentTimeMillis())));
			_log.info("Admins Online: " + World.getAllGMs());
		
			long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
			long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
			
			_log.info("Free Memory: " + totalMem + " MB");
			_log.info("Used memory: " + usedMem + " MB");

			StringUtil.printSection("Server Status");
		}
	}
	
	public static ServerStatus getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerStatus _instance = new ServerStatus();
	}
}