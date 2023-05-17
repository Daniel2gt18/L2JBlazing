package com.l2jmega.gameserver.util;

import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.WorldRegion;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;

public final class Broadcast
{
	/**
	 * Send a packet to all known players of the Creature that have the Character targetted.
	 * @param character : The character to make checks on.
	 * @param packet : The packet to send.
	 */
	public static void toPlayersTargettingMyself(Creature character, L2GameServerPacket packet)
	{
		for (Player player : character.getKnownType(Player.class))
		{
			if (player.getTarget() != character)
				continue;
			
			player.sendPacket(packet);
		}
	}
	
	/**
	 * Send a packet to all known players of the Creature.
	 * @param character : The character to make checks on.
	 * @param packet : The packet to send.
	 */
	public static void toKnownPlayers(Creature character, L2GameServerPacket packet)
	{
		for (Player player : character.getKnownType(Player.class))
			player.sendPacket(packet);
	}
	
	/**
	 * Send a packet to all known players, in a specified radius, of the Creature.
	 * @param character : The character to make checks on.
	 * @param packet : The packet to send.
	 * @param radius : The given radius.
	 */
	public static void toKnownPlayersInRadius(Creature character, L2GameServerPacket packet, int radius)
	{
		if (radius < 0)
			radius = 1500;
		
		for (Player player : character.getKnownTypeInRadius(Player.class, radius))
			player.sendPacket(packet);
	}
	
	/**
	 * Send a packet to all known players of the Creature and to the specified Creature.
	 * @param character : The character to make checks on.
	 * @param packet : The packet to send.
	 */
	public static void toSelfAndKnownPlayers(Creature character, L2GameServerPacket packet)
	{
		if (character instanceof Player)
			character.sendPacket(packet);
		
		toKnownPlayers(character, packet);
	}
	
	/**
	 * Send a packet to all known players, in a specified radius, of the Creature and to the specified Creature.
	 * @param character : The character to make checks on.
	 * @param packet : The packet to send.
	 * @param radius : The given radius.
	 */
	public static void toSelfAndKnownPlayersInRadius(Creature character, L2GameServerPacket packet, int radius)
	{
		if (radius < 0)
			radius = 600;
		
		if (character instanceof Player)
			character.sendPacket(packet);
		
		for (Player player : character.getKnownTypeInRadius(Player.class, radius))
			player.sendPacket(packet);
	}
	
	/**
	 * Send a packet to all players present in the world.
	 * @param packet : The packet to send.
	 */
	public static void toAllOnlinePlayers(L2GameServerPacket packet)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendPacket(packet);
		}
	}
	
	/**
	 * Send a packet to all players in a specific region.
	 * @param region : The region to send packets.
	 * @param packets : The packets to send.
	 */
	public static void toAllPlayersInRegion(WorldRegion region, L2GameServerPacket... packets)
	{
		for (WorldObject object : region.getObjects())
		{
			if (object instanceof Player)
			{
				final Player player = (Player) object;
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Send a packet to all players in a specific zone type.
	 * @param <T> L2ZoneType.
	 * @param zoneType : The zone type to send packets.
	 * @param packets : The packets to send.
	 */
	public static <T extends L2ZoneType> void toAllPlayersInZoneType(Class<T> zoneType, L2GameServerPacket... packets)
	{
		for (L2ZoneType temp : ZoneManager.getInstance().getAllZones(zoneType))
		{
			for (Player player : temp.getKnownTypeInside(Player.class))
			{
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
			}
		}
	}
	
	public static void announceToOnlinePlayers(String text)
	{
		toAllOnlinePlayers(new CreatureSay(0, Say2.ANNOUNCEMENT, "", text));
	}
	
	public static void announceToOnlinePlayers(String text, int value)
	{
		if (value == 0)
			toAllOnlinePlayers(new CreatureSay(0, Say2.ANNOUNCEMENT, "", text));
		else if (value == 1)
			toAllOnlinePlayers(new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", text));
		else if (value == 2)
			toAllOnlinePlayers(new CreatureSay(0, Say2.SHOUT, "SYS", text));
		else if (value == 3)
			toAllOnlinePlayers(new CreatureSay(0, Say2.TRADE, "SYS", text));
		else if (value == 4)
			toAllOnlinePlayers(new CreatureSay(0, Say2.HERO_VOICE, "SYS", text));
		else if (value == 5)
			toAllOnlinePlayers(new CreatureSay(0, Say2.TELL, "SYS", text));
		else if (value == 6)
			toAllOnlinePlayers(new CreatureSay(0, Say2.PARTY, "SYS", text));
		else if (value == 7)
			toAllOnlinePlayers(new CreatureSay(0, Say2.CLAN, "SYS", text));
		else if (value == 8)
			toAllOnlinePlayers(new CreatureSay(0, Say2.ALLIANCE, "SYS", text));
	}
	
	public static void toAllOnlineGMs(L2GameServerPacket mov)
	{
		for (Player player : World.getAllGMs())
		{
			if (player.isOnline())
				player.sendPacket(mov);
		}
	}	
}