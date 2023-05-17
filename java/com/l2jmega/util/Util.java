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
package com.l2jmega.util;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.instance.Player;

public class Util
{
	private static final Logger _log = Logger.getLogger(Util.class.getName());
	
	public static boolean isInternalIP(String ipAddress)
	{
		java.net.InetAddress addr = null;
		try
		{
			addr = java.net.InetAddress.getByName(ipAddress);
			return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static String printData(byte[] data, int len)
	{
		StringBuilder result = new StringBuilder();
		
		int counter = 0;
		
		for (int i = 0; i < len; i++)
		{
			if (counter % 16 == 0)
				result.append(fillHex(i, 4) + ": ");
			
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16)
			{
				result.append("   ");
				
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++)
				{
					int t1 = data[charpoint++];
					
					if (t1 > 0x1f && t1 < 0x80)
						result.append((char) t1);
					else
						result.append('.');
				}
				
				result.append("\n");
				counter = 0;
			}
		}
		
		int rest = data.length % 16;
		if (rest > 0)
		{
			for (int i = 0; i < 17 - rest; i++)
				result.append("   ");
			
			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++)
			{
				int t1 = data[charpoint++];
				
				if (t1 > 0x1f && t1 < 0x80)
					result.append((char) t1);
				else
					result.append('.');
			}
			
			result.append("\n");
		}
		return result.toString();
	}
	
	public static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);
		
		for (int i = number.length(); i < digits; i++)
		{
			number = "0" + number;
		}
		
		return number;
	}
	
	public static String printData(byte[] raw)
	{
		return printData(raw, raw.length);
	}
	
	public static void printSection(String s)
	{
		s = "=[ " + s + " ]";
		while (s.length() < 78)
			s = "-" + s;
		_log.info(s);
	}
	
	/**
	 * @param <T> The Object type.
	 * @param array - the array to look into.
	 * @param obj - the object to search for.
	 * @return {@code true} if the array contains the object, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		if (array == null || array.length == 0)
			return false;
		
		for (T element : array)
			if (element.equals(obj))
				return true;
		
		return false;
	}
	
	  public static boolean isValidName(String text, String regex) {
		    Pattern pattern;
		    try {
		      pattern = Pattern.compile(regex);
		    } catch (PatternSyntaxException e) {
		      pattern = Pattern.compile(".*");
		    } 
		    Matcher regexp = pattern.matcher(text);
		    return regexp.matches();
		  }
		  
		  public static boolean isValidPlayerName(String text) {
		    return isValidName(text, "^[A-Za-z0-9]{1,16}$");
		  }
		  
			/**
			 * Format the given date on the given format
			 * @param date : the date to format.
			 * @param format : the format to correct by.
			 * @return a string representation of the formatted date.
			 */
			public static String formatDate(Date date, String format)
			{
				final DateFormat dateFormat = new SimpleDateFormat(format);
				if (date != null)
					return dateFormat.format(date);
				
				return null;
			}
			
			public static void handleIllegalPlayerAction(Player actor, String message, int punishment)
			{
				ThreadPool.schedule(new IllegalPlayerAction(actor, message, punishment), 5000);
			}
			
			/**
			 * @param array - the array to look into.
			 * @param obj - the integer to search for.
			 * @return {@code true} if the array contains the integer, {@code false} otherwise.
			 */
			public static boolean contains(int[] array, int obj)
			{
				if (array == null || array.length == 0)
					return false;
				
				for (int element : array)
					if (element == obj)
						return true;
				
				return false;
			}
}