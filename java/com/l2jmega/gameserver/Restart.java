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
import java.util.Calendar;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;

/**
 * This Config for Auto Restart GameServer Initialize class getInstance() Set Time in Config File Thank You L2JServer | L2JRussia
 * @author L2JRussia
 */
public class Restart
{
	// Variaveis globais
	private static Restart _instance = null;
	protected static final Logger _log = Logger.getLogger(Restart.class.getName());
	private Calendar NextRestart;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	protected static boolean _started = false;

	// Singleton
	public static Restart getInstance()
	{
		if (_instance == null)
			_instance = new Restart();
		return _instance;
	}
	
	public String getRestartNextTime()
	{
		if (NextRestart != null)
			return format.format(NextRestart.getTime());
		return "--";
	}
	
	// Connstrutor
	private Restart()
	{
		// :D
	}
	
	public void StartCalculationOfNextRestartTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;
			
			for (String timeOfDay : Config.RESTART_INTERVAL_BY_TIME_OF_DAY)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(Calendar.SECOND, 00);
				// Verifica a validade to tempo
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				// TimeL Recebe o quanto falta de milisegundos para o restart
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				
				// Verifica qual horario sera o proximo restart
				if (count == 0)
				{
					flush2 = timeL;
					NextRestart = testStartTime;
				}
				
				if (timeL < flush2)
				{
					flush2 = timeL;
					NextRestart = testStartTime;
				}
				
				count++;
			}
			_log.info("AutoRestart: Next Restart Time: " + NextRestart.getTime().toString());
			ThreadPool.schedule(new StartRestartTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("[AutoRestart]: The restart automated server presented error in load restarts period config !");
		}
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	class StartRestartTask implements Runnable
	{
		@Override
		public void run()
		{	
			_log.info("Restart System: iniciando....");
			_started = true;
			Shutdown.getInstance().autoRestart(Config.RESTART_SECONDS);
		}
	}
}
