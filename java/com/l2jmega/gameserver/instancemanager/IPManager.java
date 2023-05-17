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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.clientpackets.CloseGame;
import com.l2jmega.gameserver.skills.AbnormalEffect;

public class IPManager
{
    private static final Logger _log = Logger.getLogger(IPManager.class.getName());

    public IPManager()
    {
        _log.log(Level.INFO, "IPManager - Loaded.");
    }

    private static boolean multiboxKickTask(Player activeChar, Integer numberBox, Collection<Player> world)
    {
        Map<String, List<Player>> ipMap = new HashMap<>();
        for (Player player : world)
        {
            if (player.getClient() == null || player.getClient().isDetached())
                continue;
			String ip = activeChar.getClient().getConnection().getInetAddress().getHostAddress();
			String playerIp = player.getClient().getConnection().getInetAddress().getHostAddress();
			
			if (ip.equals(playerIp))
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

    public boolean validBox(Player activeChar, Integer numberBox, Collection<Player> world, Boolean forcedLogOut)
    {
        if (multiboxKickTask(activeChar, numberBox, world))
        {
            if (forcedLogOut)
            {
                L2GameClient client = activeChar.getClient();
                _log.warning("Multibox Protection: " + client.getConnection().getInetAddress().getHostAddress() + " was trying to use over " + numberBox + " clients!");
    			System.out.println("DUAL BOX IP: " + activeChar.getName() + " Disconnected..");
    			activeChar.sendMessage("I'm sorry, but multibox is not allowed here.");
    		    activeChar.startAbnormalEffect(2048);
    		    activeChar.startAbnormalEffect(AbnormalEffect.ROOT);
    		    ThreadPool.schedule(new CloseGame(activeChar, 10), 0L);
            }
            return true;
        }
        return false;
    }
    
    private static class SingletonHolder
    {
        protected static final IPManager _instance = new IPManager();
    }

    public static final IPManager getInstance()
    {
        return SingletonHolder._instance;
    }
}