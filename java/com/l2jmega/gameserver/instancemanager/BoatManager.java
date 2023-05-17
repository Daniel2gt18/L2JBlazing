package com.l2jmega.gameserver.instancemanager;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Vehicle;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.CreatureTemplate;
import com.l2jmega.gameserver.model.location.VehicleLocation;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.templates.StatsSet;

public class BoatManager
{
	private final Map<Integer, Vehicle> _boats = new HashMap<>();
	private final boolean[] _docksBusy = new boolean[3];
	
	public static final int TALKING_ISLAND = 0;
	public static final int GLUDIN_HARBOR = 1;
	public static final int RUNE_HARBOR = 2;
	
	public static final int BOAT_BROADCAST_RADIUS = 20000;
	
	public static final BoatManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected BoatManager()
	{
	}
	
	public Vehicle getNewBoat(int boatId, int x, int y, int z, int heading)
	{
		StatsSet npcDat = new StatsSet();
		npcDat.set("id", boatId);
		npcDat.set("level", 0);
		
		npcDat.set("str", 0);
		npcDat.set("con", 0);
		npcDat.set("dex", 0);
		npcDat.set("int", 0);
		npcDat.set("wit", 0);
		npcDat.set("men", 0);
		
		npcDat.set("hp", 50000);
		npcDat.set("mp", 0);
		
		npcDat.set("hpRegen", 3.e-3f);
		npcDat.set("mpRegen", 3.e-3f);
		
		npcDat.set("radius", 0);
		npcDat.set("height", 0);
		npcDat.set("type", "");
		
		npcDat.set("exp", 0);
		npcDat.set("sp", 0);
		
		npcDat.set("pAtk", 0);
		npcDat.set("mAtk", 0);
		npcDat.set("pDef", 100);
		npcDat.set("mDef", 100);
		
		npcDat.set("rHand", 0);
		npcDat.set("lHand", 0);
		
		npcDat.set("walkSpd", 0);
		npcDat.set("runSpd", 0);
		
		CreatureTemplate template = new CreatureTemplate(npcDat);
		Vehicle boat = new Vehicle(IdFactory.getInstance().getNextId(), template);
		
		_boats.put(boat.getObjectId(), boat);
		
		boat.setHeading(heading);
		boat.spawnMe(x, y, z);
		
		return boat;
	}
	
	/**
	 * @param boatId
	 * @return
	 */
	public Vehicle getBoat(int boatId)
	{
		return _boats.get(boatId);
	}
	
	/**
	 * Lock/unlock dock so only one ship can be docked
	 * @param h Dock Id
	 * @param value True if dock is locked
	 */
	public void dockShip(int h, boolean value)
	{
		_docksBusy[h] = value;
	}
	
	/**
	 * Check if dock is busy
	 * @param h Dock Id
	 * @return Trye if dock is locked
	 */
	public boolean dockBusy(int h)
	{
		return _docksBusy[h];
	}
	
	/**
	 * Broadcast one packet in both path points
	 * @param point1
	 * @param point2
	 * @param packet The packet to broadcast.
	 */
	public void broadcastPacket(VehicleLocation point1, VehicleLocation point2, L2GameServerPacket packet)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			double dx = (double) player.getX() - point1.getX();
			double dy = (double) player.getY() - point1.getY();
			
			if (Math.sqrt(dx * dx + dy * dy) < BOAT_BROADCAST_RADIUS)
				player.sendPacket(packet);
			else
			{
				dx = (double) player.getX() - point2.getX();
				dy = (double) player.getY() - point2.getY();
				
				if (Math.sqrt(dx * dx + dy * dy) < BOAT_BROADCAST_RADIUS)
					player.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Broadcast several packets in both path points
	 * @param point1
	 * @param point2
	 * @param packets The packets to broadcast.
	 */
	public void broadcastPackets(VehicleLocation point1, VehicleLocation point2, L2GameServerPacket... packets)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			double dx = (double) player.getX() - point1.getX();
			double dy = (double) player.getY() - point1.getY();
			
			if (Math.sqrt(dx * dx + dy * dy) < BOAT_BROADCAST_RADIUS)
			{
				for (L2GameServerPacket p : packets)
					player.sendPacket(p);
			}
			else
			{
				dx = (double) player.getX() - point2.getX();
				dy = (double) player.getY() - point2.getY();
				
				if (Math.sqrt(dx * dx + dy * dy) < BOAT_BROADCAST_RADIUS)
					for (L2GameServerPacket p : packets)
						player.sendPacket(p);
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final BoatManager _instance = new BoatManager();
	}
}