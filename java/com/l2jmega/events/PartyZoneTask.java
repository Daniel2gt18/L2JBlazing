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
package com.l2jmega.events;

import com.l2jmega.Config;
import com.l2jmega.gameserver.PartyFarmEvent;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminCustom;
import com.l2jmega.gameserver.model.Announcement;

public abstract class PartyZoneTask
{
	/** The _in progress. */
	public static boolean _started = false;
	public static boolean _aborted = false;
	
	public static void SpawnEvent()
	{
		//PartyFarm.spawnMonsters();
		
		Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Special Rewards.");
		Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Teleport Now!");
		Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Duration: " + Config.PARTYZONE_TIME + " minute(s)!");
		
		_aborted = false;
		_started = true;
		
		waiter(Config.PARTYZONE_TIME * 60 * 1000); // minutes for event time
		
		if (!_aborted)
			finishEvent();
	}
	
	public static void finishEvent()
	{
		Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Event Finished!");
		//PartyFarm.unSpawnMonsters();
		
		_started = false;
		
		if (!AdminCustom._partytask_manual)
			PartyFarmEvent.getInstance().StartCalculationOfNextEventTime();
		else
			AdminCustom._partytask_manual = false;
		
		PartyFarmEvent.getInstance().getNextTime();
		
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					
					if (_started)
					{
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Special Rewards.");
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Teleport Now!");
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" " + seconds / 60 / 60 + " hour(s) till event finish!");
					}
					break;
				case 1800: // 30 minutes left
				case 900: // 15 minutes left
				case 600: // 10 minutes left
				case 300: // 5 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					// removeOfflinePlayers();
					
					if (_started)
					{
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Special Rewards.");
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" Teleport Now!");
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" " + seconds / 60 + " minute(s) till event finish!");
					}
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
				case 10: // 10 seconds left
				case 3: // 3 seconds left
				case 2: // 2 seconds left
				case 1: // 1 seconds left
					if (_started)
						Announcement.AnnounceEvents(""+Config.NAME_EVENT+" " + seconds + " second(s) till event finish!");
					
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
}