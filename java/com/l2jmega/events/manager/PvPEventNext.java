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
package com.l2jmega.events.manager;

import com.l2jmega.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author MeGaPacK
 */
public class PvPEventNext
{
	protected static final Logger _log = Logger.getLogger(PvPEventNext.class.getName());
	
	public static ArrayList<String> PVP_EVENT_INTERVAL;
	
	private static PvPEventNext instance = null;
	
	private PvPEventNext()
	{
		loadPvPConfig();
	}
	
	private Calendar NextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	
	public String getNextTime()
	{
		if (NextEvent.getTime() != null)
			return format.format(NextEvent.getTime());
		return "Erro";
	}
	
	public static PvPEventNext getInstance()
	{
		
		if (instance == null)
		{
			instance = new PvPEventNext();
		}
		return instance;
		
	}
	
	public void StartCalculationOfNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;
			
			for (String timeOfDay : PVP_EVENT_INTERVAL)
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
					NextEvent = testStartTime;
				}
				
				if (timeL < flush2)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}
				
				count++;
			}
			_log.info("PvP Event Proximo Evento: " + NextEvent.getTime().toString());
		}
		catch (Exception e)
		{
			System.out.println(" PvP Next Event Info: " + e);
		}
	}
	
	public static void loadPvPConfig()
	{
		
		InputStream is = null;
		try
		{
			Properties eventSettings = new Properties();
			is = new FileInputStream(new File(Config.PVP_EVENT_FILE));
			eventSettings.load(is);
			
			// ============================================================
			
			PVP_EVENT_INTERVAL = new ArrayList<>();
			
			String[] propertySplit;
			propertySplit = eventSettings.getProperty("PvPZEventInterval", "").split(",");
			
			for (String time : propertySplit)
			{
				PVP_EVENT_INTERVAL.add(time);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
		
}
