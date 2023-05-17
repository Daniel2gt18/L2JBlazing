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
package com.l2jmega.gameserver.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.math.MathUtil;

public class TradeLog
{
	static
	{
		new File("log/Player Log/TradeLog").mkdirs();
	}
	
	private static final Logger _log = Logger.getLogger(TradeLog.class.getName());
	
	public static void Log(String player_name, String player_trade, String itemname, int enchant, int cont, int obj_id, String params)
	{
		final File file = new File("log/Player Log/TradeLog/" + player_name + ".txt");
		if (!file.exists())
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
			}
		
		try (FileWriter save = new FileWriter(file, true))
		{
			if (enchant > 0)
				save.write("Data ["+MathUtil.formatDate(new Date(), "dd/MM/yyyy H:mm:ss") + "] >> Player [" + player_name + "] >> Trade com  [" + player_trade + "] >> Item: [" + itemname + "] +" + enchant + " >> Obj_id: [" + obj_id + "]\r\n");
			else
				save.write("Data ["+MathUtil.formatDate(new Date(), "dd/MM/yyyy H:mm:ss") + "] >> Player [" + player_name + "] >> Trade com  [" + player_trade + "] >> Item: [" + cont + "] " + itemname + " >> Obj_id: [" + obj_id + "]\r\n");
			
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "TradeLog for Player " + player_name + " could not be saved: ", e);
		}
	}
	
	public static void Log(String player_name, String player_trade, String itemname, int enchant, int cont, int obj_id)
	{
		Log(player_name, player_trade, itemname, enchant, cont, obj_id, "");
	}
}