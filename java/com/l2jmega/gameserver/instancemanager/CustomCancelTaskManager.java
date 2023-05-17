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

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.instance.Player;

 /**
 * @author Anarchy, Bluur
 *
 */
public class CustomCancelTaskManager implements Runnable
{
    private Player player = null;
    private ArrayList<L2Skill> buffsCanceled = null;

    public CustomCancelTaskManager(Player p, ArrayList<L2Skill> skill)
    {
        player = p;
        buffsCanceled = skill;
    }
    
    @Override
    public void run()
    {
        if (player != null && player.isOnline() && !player.isOlympiadProtection())  
        {    
        	for (L2Skill skill : buffsCanceled)
        	{
        		player.setCancel(false);                   
        		if (skill == null)
        			continue;          
        		skill.getEffects(player, player);
        	}
        }
    }
}
