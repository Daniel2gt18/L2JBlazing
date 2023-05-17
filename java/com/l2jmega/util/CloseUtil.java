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

import java.io.Closeable;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * little 'hack' :)
 * @author ProGramMoS
 */
public final class CloseUtil
{
	private final static Logger _log = Logger.getLogger(CloseUtil.class.getName());
	
	public static void close(Connection con)
	{
		if (con != null)
			try
			{
				con.close();
				con = null;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				_log.severe(e.getMessage());
			}
	}
	
	public static void close(Closeable closeable)
	{
		if (closeable != null)
			try
			{
				closeable.close();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				_log.severe(e.getMessage());
			}
	}
}
