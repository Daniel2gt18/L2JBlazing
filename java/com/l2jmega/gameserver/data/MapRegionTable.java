package com.l2jmega.gameserver.data;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.ClanHallManager;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.entity.ClanHall;
import com.l2jmega.gameserver.model.entity.Siege;
import com.l2jmega.gameserver.model.entity.Siege.SiegeSide;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.model.zone.type.L2ArenaZone;
import com.l2jmega.gameserver.model.zone.type.L2ClanHallZone;
import com.l2jmega.gameserver.model.zone.type.L2FlagZone;
import com.l2jmega.gameserver.model.zone.type.L2PvPZone;
import com.l2jmega.gameserver.model.zone.type.L2RaidZone;
import com.l2jmega.gameserver.model.zone.type.L2RaidZoneNoFlag;
import com.l2jmega.gameserver.model.zone.type.L2SoloZone;
import com.l2jmega.gameserver.model.zone.type.L2TownZone;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MapRegionTable
{
	public static enum TeleportType
	{
		CASTLE,
		CLAN_HALL,
		SIEGE_FLAG,
		TOWN
	}
	
	private static Logger _log = Logger.getLogger(MapRegionTable.class.getName());
	
	private static final int REGIONS_X = 11;
	private static final int REGIONS_Y = 16;
	
	private static final Location MDT_LOCATION = new Location(12661, 181687, -3560);
	
	private final int[][] _regions = new int[REGIONS_X][REGIONS_Y];
	
	protected MapRegionTable()
	{
		int count = 0;
		
		try
		{
			File f = new File("./data/xml/map_region.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("map"))
				{
					NamedNodeMap attrs = d.getAttributes();
					int rY = Integer.valueOf(attrs.getNamedItem("geoY").getNodeValue()) - 10;
					for (int rX = 0; rX < REGIONS_X; rX++)
					{
						_regions[rX][rY] = Integer.valueOf(attrs.getNamedItem("geoX_" + (rX + 16)).getNodeValue());
						count++;
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "MapRegionTable: Error while loading \"map_region.xml\".", e);
		}
		_log.info("MapRegionTable: Loaded " + count + " regions.");
	}
	
	public final int getMapRegion(int posX, int posY)
	{
		return _regions[getMapRegionX(posX)][getMapRegionY(posY)];
	}
	
	public static final int getMapRegionX(int posX)
	{
		// +4 to shift coords center
		return (posX >> 15) + 4;
	}
	
	public static final int getMapRegionY(int posY)
	{
		// +8 to shift coords center
		return (posY >> 15) + 8;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return the castle id associated to the town, based on X/Y points.
	 */
	public final int getAreaCastle(int x, int y)
	{
		switch (getMapRegion(x, y))
		{
			case 0: // Talking Island Village
			case 5: // Town of Gludio
			case 6: // Gludin Village
				return 1;
			
			case 7: // Town of Dion
				return 2;
			
			case 8: // Town of Giran
			case 12: // Giran Harbor
				return 3;
			
			case 1: // Elven Village
			case 2: // Dark Elven Village
			case 9: // Town of Oren
			case 17: // Floran Village
				return 4;
			
			case 10: // Town of Aden
			case 11: // Hunters Village
			default: // Town of Aden
				return 5;
			
			case 13: // Heine
				return 6;
			
			case 15: // Town of Goddard
				return 7;
			
			case 14: // Rune Township
			case 18: // Primeval Isle Wharf
				return 8;
			
			case 3: // Orc Village
			case 4: // Dwarven Village
			case 16: // Town of Schuttgart
				return 9;
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return a String consisting of town name, based on X/Y points.
	 */
	public String getClosestTownName(int x, int y)
	{
		return getClosestTownName(getMapRegion(x, y));
	}
	
	public String getClosestTownName(int townId)
	{
		switch (townId)
		{
			case 0:
				return "Talking Island Village";
			
			case 1:
				return "Elven Village";
			
			case 2:
				return "Dark Elven Village";
			
			case 3:
				return "Orc Village";
			
			case 4:
				return "Dwarven Village";
			
			case 5:
				return "Town of Gludio";
			
			case 6:
				return "Gludin Village";
			
			case 7:
				return "Town of Dion";
			
			case 8:
				return "Town of Giran";
			
			case 9:
				return "Town of Oren";
			
			case 10:
				return "Town of Aden";
			
			case 11:
				return "Hunters Village";
			
			case 12:
				return "Giran Harbor";
			
			case 13:
				return "Heine";
			
			case 14:
				return "Rune Township";
			
			case 15:
				return "Town of Goddard";
			
			case 16:
				return "Town of Schuttgart";
			
			case 17:
				return "Floran Village";
			
			case 18:
				return "Primeval Isle";
			
			default:
				return "Town of Aden";
		}
	}
	
	/**
	 * @param character : The type of character to check.
	 * @param teleportType : The type of teleport to check.
	 * @return a Location based on character and teleport types.
	 */
	public Location getLocationToTeleport(Creature character, TeleportType teleportType)
	{
		// The character isn't a player, bypass all checks and retrieve a random spawn location on closest town.
		if (!(character instanceof Player))
			return getClosestTown(character.getX(), character.getY()).getSpawnLoc();
		
		final Player player = ((Player) character);
		
		// The player is in MDT, move him out.
		if (player.isInsideZone(ZoneId.MONSTER_TRACK))
			return MDT_LOCATION;
		
		if (teleportType != TeleportType.TOWN && player.getClan() != null)
		{
			if (teleportType == TeleportType.CLAN_HALL)
			{
				final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(player.getClan());
				if (ch != null)
				{
					final L2ClanHallZone zone = ch.getZone();
					if (zone != null)
						return zone.getSpawnLoc();
				}
			}
			else if (teleportType == TeleportType.CASTLE)
			{
				// Check if the player is part of a castle owning clan.
				Castle castle = CastleManager.getInstance().getCastleByOwner(player.getClan());
				if (castle == null)
				{
					// If not, check if he is in defending side.
					castle = CastleManager.getInstance().getCastle(player);
					if (!(castle != null && castle.getSiege().isInProgress() && castle.getSiege().checkSides(player.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER)))
						castle = null;
				}
				
				if (castle != null && castle.getCastleId() > 0)
					return castle.getCastleZone().getSpawnLoc();
			}
			else if (teleportType == TeleportType.SIEGE_FLAG)
			{
				final Siege siege = CastleManager.getInstance().getSiege(player);
				if (siege != null)
				{
					final Npc flag = siege.getFlag(player.getClan());
					if (flag != null)
						return flag.getPosition();
				}
			}
		}
		
		// Check if the player needs to be teleported in second closest town, during an active siege.
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle != null && castle.getSiege().isInProgress())
			return (player.getKarma() > 0) ? castle.getSiegeZone().getChaoticSpawnLoc() : castle.getSiegeZone().getSpawnLoc();
		
		// Karma player lands out of city.
		if (player.getKarma() > 0)
			return getClosestTown(player).getChaoticSpawnLoc();
		
		// Check if player is in arena.
		final L2ArenaZone arena = ZoneManager.getInstance().getZone(player, L2ArenaZone.class);
		if (arena != null)
			return arena.getSpawnLoc();
	
		// Check if player is in PvP Zone.
		final L2PvPZone pvpZone = ZoneManager.getInstance().getZone(player, L2PvPZone.class);
		if (pvpZone != null)
			return pvpZone.getSpawnLoc();
		
		// Checking if in Party Zerg Zone
		L2FlagZone flag = ZoneManager.getPartyZone(player);
		if (flag != null)
			return flag.getSpawnLoc();
		
		// Checking if in Raid Zerg Zone
		L2RaidZone raid = ZoneManager.getRaidZone(player);
		if (raid != null)
			return raid.getSpawnLoc();
		
		// Checking if in Raid Zerg Zone
		L2RaidZoneNoFlag raid2 = ZoneManager.getRaidZoneNoFlag(player);
		if (raid2 != null)
			return raid2.getSpawnLoc();
		
		// Checking if in Raid Zerg Zone
		L2SoloZone solo = ZoneManager.getSoloZone(player);
		if (solo != null)
			return solo.getSpawnLoc();
		
		// Retrieve a random spawn location of the nearest town.
		return getClosestTown(player).getSpawnLoc();
	}
	
	/**
	 * A specific method, used ONLY by players. There's a Race condition.
	 * @param player : The player used to find race, x and y.
	 * @return the closest L2TownZone based on a X/Y location.
	 */
	private final L2TownZone getClosestTown(Player player)
	{
		switch (getMapRegion(player.getX(), player.getY()))
		{
			case 0: // TI
				return getTown(2);
			
			case 1:// Elven
				return getTown((player.getTemplate().getRace() == ClassRace.DARK_ELF) ? 1 : 3);
			
			case 2:// DE
				return getTown((player.getTemplate().getRace() == ClassRace.ELF) ? 3 : 1);
			
			case 3: // Orc
				return getTown(4);
			
			case 4:// Dwarven
				return getTown(6);
			
			case 5:// Gludio
				return getTown(7);
			
			case 6:// Gludin
				return getTown(5);
			
			case 7: // Dion
				return getTown(8);
			
			case 8: // Giran
			case 12: // Giran Harbor
				return getTown(9);
			
			case 9: // Oren
				return getTown(10);
			
			case 10: // Aden
				return getTown(12);
			
			case 11: // HV
				return getTown(11);
			
			case 13: // Heine
				return getTown(15);
			
			case 14: // Rune
				return getTown(14);
			
			case 15: // Goddard
				return getTown(13);
			
			case 16: // Schuttgart
				return getTown(17);
			
			case 17:// Floran
				return getTown(16);
			
			case 18:// Primeval Isle
				return getTown(19);
		}
		return getTown(16); // Default to floran
	}
	
	/**
	 * @param x : The current character's X location.
	 * @param y : The current character's Y location.
	 * @return the closest L2TownZone based on a X/Y location.
	 */
	private final L2TownZone getClosestTown(int x, int y)
	{
		switch (getMapRegion(x, y))
		{
			case 0: // TI
				return getTown(2);
			
			case 1:// Elven
				return getTown(3);
			
			case 2:// DE
				return getTown(1);
			
			case 3: // Orc
				return getTown(4);
			
			case 4:// Dwarven
				return getTown(6);
			
			case 5:// Gludio
				return getTown(7);
			
			case 6:// Gludin
				return getTown(5);
			
			case 7: // Dion
				return getTown(8);
			
			case 8: // Giran
			case 12: // Giran Harbor
				return getTown(9);
			
			case 9: // Oren
				return getTown(10);
			
			case 10: // Aden
				return getTown(12);
			
			case 11: // HV
				return getTown(11);
			
			case 13: // Heine
				return getTown(15);
			
			case 14: // Rune
				return getTown(14);
			
			case 15: // Goddard
				return getTown(13);
			
			case 16: // Schuttgart
				return getTown(17);
			
			case 17:// Floran
				return getTown(16);
			
			case 18:// Primeval Isle
				return getTown(19);
		}
		return getTown(16); // Default to floran
	}
	
	/**
	 * @param x : The current character's X location.
	 * @param y : The current character's Y location.
	 * @return the second closest L2TownZone based on a X/Y location.
	 */
	public final L2TownZone getSecondClosestTown(int x, int y)
	{
		switch (getMapRegion(x, y))
		{
			case 0: // TI
			case 1: // Elven
			case 2: // DE
			case 5: // Gludio
			case 6: // Gludin
				return getTown(5);
			
			case 3: // Orc
				return getTown(4);
			
			case 4: // Dwarven
			case 16: // Schuttgart
				return getTown(6);
			
			case 7: // Dion
				return getTown(7);
			
			case 8: // Giran
			case 9: // Oren
			case 10:// Aden
			case 11: // HV
				return getTown(11);
			
			case 12: // Giran Harbour
			case 13: // Heine
			case 17:// Floran
				return getTown(16);
			
			case 14: // Rune
				return getTown(13);
			
			case 15: // Goddard
				return getTown(12);
			
			case 18: // Primeval Isle
				return getTown(19);
		}
		return getTown(16); // Default to floran
	}
	
	/**
	 * @param x : The current character's X location.
	 * @param y : The current character's Y location.
	 * @return the closest region based on a X/Y location.
	 */
	public final int getClosestLocation(int x, int y)
	{
		switch (getMapRegion(x, y))
		{
			case 0: // TI
				return 1;
			
			case 1: // Elven
				return 4;
			
			case 2: // DE
				return 3;
			
			case 3: // Orc
			case 4: // Dwarven
			case 16:// Schuttgart
				return 9;
			
			case 5: // Gludio
			case 6: // Gludin
				return 2;
			
			case 7: // Dion
				return 5;
			
			case 8: // Giran
			case 12: // Giran Harbor
				return 6;
			
			case 9: // Oren
				return 10;
			
			case 10: // Aden
				return 13;
			
			case 11: // HV
				return 11;
			
			case 13: // Heine
				return 12;
			
			case 14: // Rune
				return 14;
			
			case 15: // Goddard
				return 15;
		}
		return 0;
	}
	
	/**
	 * @param townId the townId to match.
	 * @return a L2TownZone based on the overall list of L2TownZone, matching the townId.
	 */
	public static final L2TownZone getTown(int townId)
	{
		for (L2TownZone temp : ZoneManager.getInstance().getAllZones(L2TownZone.class))
		{
			if (temp.getTownId() == townId)
				return temp;
		}
		return null;
	}
	
	/**
	 * @param x coords to check.
	 * @param y coords to check.
	 * @param z coords to check.
	 * @return a L2TownZone based on the overall list of zones, matching a 3D location.
	 */
	public static final L2TownZone getTown(int x, int y, int z)
	{
		return ZoneManager.getInstance().getZone(x, y, z, L2TownZone.class);
	}
	
	public static MapRegionTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MapRegionTable INSTANCE = new MapRegionTable();
	}
}