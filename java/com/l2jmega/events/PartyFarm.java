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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

public class PartyFarm
{	
	public static void spawnMonsters()
	{
		openSpoilDoors();
		closeSpoilDoors();
		
		int[] coord;
		for (int i = 0; i < Config.MONSTER_LOCS_COUNT; i++)
		{
			coord = Config.MONSTER_LOCS[i];
			monsters.add(spawnNPC(coord[0], coord[1], coord[2], Config.monsterId));
		}
	}
		
	private static void closeSpoilDoors()
	{
		if (Config.CLOSE_DOORS_PARTY_FARM)
		{
			for (int i : Config.PARTY_CLOSE_DOORS)
			{
				Door door = DoorTable.getInstance().getDoor(i);
				door.closeMe();
			}
		}
	}
	
	private static void openSpoilDoors()
	{
		if (Config.OPEN_DOORS_PARTY_FARM)
		{
			for (int i : Config.PARTY_OPEN_DOORS)
			{
				Door door = DoorTable.getInstance().getDoor(i);
				door.openMe();
			}
		}
	}
	
	protected static L2Spawn spawnNPC(int xPos, int yPos, int zPos, int npcId)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
		
		try
		{
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(xPos, yPos, zPos, 0);
			spawn.setRespawnDelay(Config.PARTY_FARM_MONSTER_DALAY);
			
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			
			spawn.setRespawnState(true);
			spawn.doSpawn(false);
			spawn.getNpc().isAggressive();
			spawn.getNpc().decayMe();
			spawn.getNpc().spawnMe(spawn.getNpc().getX(), spawn.getNpc().getY(), spawn.getNpc().getZ());
			spawn.getNpc().broadcastPacket(new MagicSkillUse(spawn.getNpc(), spawn.getNpc(), 1034, 1, 1, 1));
			return spawn;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public static List<L2Spawn> monsters = new CopyOnWriteArrayList<>();
	
	protected static void unSpawnMonsters()
	{
		for (L2Spawn s : monsters)
		{
			if (s == null)
			{
				monsters.remove(s);
				continue;
			}
			
			s.getNpc().deleteMe();
			s.setRespawnState(false);
			SpawnTable.getInstance().deleteSpawn(s, true);
			monsters.remove(s);
		}
	}
}