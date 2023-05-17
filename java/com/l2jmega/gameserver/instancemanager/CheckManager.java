/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.gameserver.instancemanager;

import java.util.Collection;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;

public class CheckManager
{
	// Enable / Disable service guard
	public static final boolean SERVER_GUARD = true;
	// Start time
	private static final int START_TIME = 60000;
	// End Time
	private static final int RESTART_TIME = 120000;
	
	private class AutoAnnounceTask implements Runnable
	{
		@Override
		public void run()
		{
			if (Config.WARN_ITEM_ENABLED)
				loadData();
		}
	}
	
	public void loadData()
	{
		Collection<Player> onlinePlayers = World.getInstance().getPlayers();
		for (Player players : onlinePlayers)
		{
			if (Config.WARN_LIST_ITEM)
			{
				ItemInstance item;
				for (int[] element : Config.PROTECT_ITEMS)
				{
					item = players.getInventory().getItemByItemId(element[0]);
					if ((item != null) && (item.getCount() >= element[1]))
					{
						if (!players.isGM())
							System.out.println("[INV]: " + players.getName() + " ~ [" + item.getCount() + "] -> " + item.getName());
						
						for (Player allgms : World.getAllGMs())
						{
							if (!players.isGM())
								allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[INV]", players.getName() + " ~ [" + item.getCount() + "] -> " + item.getName()));
						}
					}
				}
				
				for (int[] element : Config.PROTECT_ITEMS)
				{
					item = players.getWarehouse().getItemByItemId(element[0]);
					if ((item != null) && (item.getCount() >= element[1]))
					{
						if (!players.isGM())
							System.out.println("[BAU]: " + players.getName() + " ~ [" + item.getCount() + "] -> " + item.getName());
						
						for (Player allgms : World.getAllGMs())
						{
							if (!players.isGM())
								allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[BAU]", players.getName() + " ~ [" + item.getCount() + "] -> " + item.getName()));
						}
					}
				}
			}
			
			if (Config.WARN_ENCHANT_ITEM_ENABLED)
			{
				for (final ItemInstance i : players.getInventory().getItems())
				{
					if (!players.isGM())
					{
						if (i.getEnchantLevel() >= Config.WARN_ENCHANT_LEVEL)
						{
							if (!players.isGM())
								System.out.println("[INV]: " + players.getName() + " ~ " + i.getName() + " +" + i.getEnchantLevel());
							
							for (Player allgms : World.getAllGMs())
							{
								if (!players.isGM())
									allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[INV]", players.getName() + " ~ " + i.getName() + " +" + i.getEnchantLevel()));
							}
						}
						
					}
				}
				
				for (final ItemInstance i : players.getWarehouse().getItems())
				{
					if (!players.isGM())
					{
						if (i.getEnchantLevel() >= Config.WARN_ENCHANT_LEVEL)
						{
							if (!players.isGM())
								System.out.println("[BAU]: " + players.getName() + " ~ " + i.getName() + " +" + i.getEnchantLevel());
							
							for (Player allgms : World.getAllGMs())
							{
								if (!players.isGM())
									allgms.sendPacket(new CreatureSay(0, Say2.TELL, "[Warehouse]", players.getName() + " ~ " + i.getName() + " +" + i.getEnchantLevel()));
							}
						}
						
					}
				}
			}
			
		}
	}
	
	public static CheckManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		
		protected static final CheckManager _instance = new CheckManager();
	}
	
	
	private CheckManager()
	{
		if (Config.WARN_ITEM_ENABLED)
			ThreadPool.scheduleAtFixedRate(new AutoAnnounceTask(), START_TIME, RESTART_TIME);
	}
}
