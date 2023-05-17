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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.util.CloseUtil;

public class ResetDaily
{
	private static ResetDaily _instance = null;
	protected static final Logger _log = Logger.getLogger(ResetDaily.class.getName());
	private Calendar NEXT;
	
	public static ResetDaily getInstance()
	{
		if (_instance == null)
			_instance = new ResetDaily();
		return _instance;
	}
	
	private ResetDaily()
	{
	}
	
	public void StartReset()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;
			
			for (String timeOfDay : Config.RESET_DAILY_TIME)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(Calendar.SECOND, 00);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				
				if (count == 0)
				{
					flush2 = timeL;
					NEXT = testStartTime;
				}
				
				if (timeL < flush2)
				{
					flush2 = timeL;
					NEXT = testStartTime;
				}
				
				count++;
			}
			_log.info("Reset_Daily: Proximo Reset: " + NEXT.getTime().toString());
			ThreadPool.schedule(new StartEventTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("Reset_Daily: " + e);
		}
	}
	
	class StartEventTask implements Runnable
	{
		@Override
		public void run()
		{
			Clear();
		}
	}
	
	static void Clear()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				Connection con = null;
				try
				{
					con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement stmt = con.prepareStatement("UPDATE characters SET second_log=? WHERE second_log like '0'");
					stmt.setInt(1, 1);
					stmt.execute();
					stmt.close();
					stmt = null;
				}
				catch (Exception e)
				{
					_log.log(Level.SEVERE, "[Reset_Daily]:  ", e);
				}
				finally
				{
					CloseUtil.close(con);
				}
				
				_log.info("----------------------------------------------------------------------------");
				_log.info("Reset_Daily: Tabela second_log Resetada.");
				_log.info("----------------------------------------------------------------------------");
				
			}
			
		}, 1);
		
		NextReset();
		
	}
	
	public static void NextReset()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				ResetDaily.getInstance().StartReset();
			}
			
		}, 1 * 1000);
	}
}
