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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.util.Rnd;

public class SoloZoneManager
{
    private static final Logger _log = Logger.getLogger(SoloZoneManager.class.getName());

    public SoloZoneManager()
    {
        _log.log(Level.INFO, "SoloZoneManager - Loaded.");
    }

    private static boolean checkPlayersKickTask(Player activeChar, Integer numberBox)
    {
        Map<String, List<Player>> map = new HashMap<>();
        
        if (activeChar != null)
        {
        	for (Player player : World.getInstance().getPlayers())
        	{
        		if (!player.isInsideZone(ZoneId.SOLO_CUSTOM) || player.getHWID() == null)
        			continue;
				String ip1 = activeChar.getHWID();
				String ip2 = player.getHWID();

				if (ip1.equals(ip2))
				{
					if (map.get(ip1) == null)
						map.put(ip1, new ArrayList<Player>());

					map.get(ip1).add(player);

					if (map.get(ip1).size() > numberBox)
						return true;
				}
        	}
        }
        return false;
    }
    
    
    private static boolean checkPlayersKickTask_ip(Player activeChar, Integer numberBox, Collection<Player> world)
    {
        Map<String, List<Player>> ipMap = new HashMap<>();
        for (Player player : world)
        {
            if (!player.isInsideZone(ZoneId.SOLO_CUSTOM) || player.getClient() == null || player.getClient().isDetached())
                continue;
			String ip = activeChar.getClient().getConnection().getInetAddress().getHostAddress();
			String playerIp = player.getClient().getConnection().getInetAddress().getHostAddress();
			
			if (IPProtection(playerIp))
			{
			    if (ipMap.get(ip) == null)
			        ipMap.put(ip, new ArrayList<Player>());
			    
			    ipMap.get(ip).add(player);
			    
			    if (ipMap.get(ip).size() >= numberBox)
			        return true;
			}
        }
        return false;
    }

    public boolean checkPlayersArea_ip(Player activeChar, Integer numberBox, Collection<Player> world, Boolean forcedLogOut)
    {
        if (checkPlayersKickTask_ip(activeChar, numberBox, world))
        {
            if (forcedLogOut)
            {
            	activeChar.sendPacket(new CreatureSay(0, Say2.TELL, "SYS"," Double box not allowed in Solo Zone!"));
				for (Player allgms : World.getAllGMs())
				{
					if (!activeChar.isGM())
						allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[Double IP]", activeChar.getName() +" in Solo Zone!"));
				}
            	RandomTeleport(activeChar);
            }
            return true;
        }
        return false;
    }
    
    public boolean checkPlayersArea(Player activeChar, Integer numberBox, Boolean forcedTeleport)
    {
        if (checkPlayersKickTask(activeChar, numberBox))
        {
            if (forcedTeleport)
            {
            	activeChar.sendPacket(new CreatureSay(0, Say2.TELL, "SYS"," Allowed only " + numberBox + " Client in Solo Zone!"));
				for (Player allgms : World.getAllGMs())
				{
					if (!activeChar.isGM())
						allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[Double HWID]", activeChar.getName() +" in Solo Zone!"));
				}
            	RandomTeleport(activeChar);
            }
            return true;
        }
        return false;
    }
    
	public static synchronized boolean IPProtection(final String ip)
	{
		boolean result = true;
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			final PreparedStatement statement = con.prepareStatement("SELECT ip FROM ip_protect_solozone WHERE ip=?");
			statement.setString(1, ip);
			final ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println("SelectorHelper -> IPProtection: " + e.getMessage());
		}
		return result;
	}
    
    //Giran Coord's
	public void RandomTeleport(Player activeChar)
	{
		switch (Rnd.get(5))
		{
		    case 0:
		    {
		    	int x = 82533 + Rnd.get(100);
		    	int y = 149122 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3474, 0);
		    	break;
		    }
		    case 1:
		    {
		    	int x = 82571 + Rnd.get(100);
		    	int y = 148060 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3467, 0);
		    	break;
		    }
		    case 2:
		    {
		    	int x = 81376 + Rnd.get(100);
		    	int y = 148042 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3474, 0);
		    	break;
		    }
		    case 3:
		    {
		    	int x = 81359 + Rnd.get(100);
		    	int y = 149218 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3474, 0);
		    	break;
		    }
		    case 4:
		    {
		    	int x = 82862 + Rnd.get(100);
		    	int y = 148606 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3474, 0);
		    	break;
		    }
	    }
	}
	
    private static class SingletonHolder
    {
        protected static final SoloZoneManager _instance = new SoloZoneManager();
    }

    public static final SoloZoneManager getInstance()
    {
        return SingletonHolder._instance;
    }
}