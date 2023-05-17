package com.l2jmega.gameserver.instancemanager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.WorldRegion;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.model.zone.form.ZoneCuboid;
import com.l2jmega.gameserver.model.zone.form.ZoneCylinder;
import com.l2jmega.gameserver.model.zone.form.ZoneNPoly;
import com.l2jmega.gameserver.model.zone.type.L2BossZone;
import com.l2jmega.gameserver.model.zone.type.L2FlagZone;
import com.l2jmega.gameserver.model.zone.type.L2RaidZone;
import com.l2jmega.gameserver.model.zone.type.L2RaidZoneNoFlag;
import com.l2jmega.gameserver.model.zone.type.L2SoloZone;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ZoneManager
{
	private static final Logger _log = Logger.getLogger(ZoneManager.class.getName());
	
	private static final String DELETE_GRAND_BOSS_LIST = "DELETE FROM grandboss_list";
	private static final String INSERT_GRAND_BOSS_LIST = "INSERT INTO grandboss_list (player_id,zone) VALUES (?,?)";
	
	private final Map<Class<? extends L2ZoneType>, Map<Integer, ? extends L2ZoneType>> _classZones = new HashMap<>();
	private final Map<Integer, ItemInstance> _debugItems = new ConcurrentHashMap<>();
	
	private int _lastDynamicId = 0;
	
	protected ZoneManager()
	{
		_log.info("ZoneManager: Loading zones...");
		
		load();
	}
	
	public void reload()
	{
		// save L2BossZone
		save();
		
		// remove zones from world
		int count = 0;
		for (WorldRegion[] worldRegion : World.getInstance().getWorldRegions())
		{
			for (WorldRegion element : worldRegion)
			{
				element.getZones().clear();
				count++;
			}
		}
		
		_log.info("ZoneManager: Removed zones in " + count + " regions.");
		
		// clear
		_classZones.clear();
		clearDebugItems();
		
		// load all zones
		load();
		
		// revalidate objects in zones
		for (WorldObject o : World.getInstance().getObjects())
		{
			if (o instanceof Creature)
				((Creature) o).revalidateZone(true);
		}
	}
	
	public final void load()
	{
		// Get the world regions
		WorldRegion[][] worldRegions = World.getInstance().getWorldRegions();
		
		// Load the zone xml
		try
		{
			final File mainDir = new File("./data/xml/zones");
			if (!mainDir.isDirectory())
			{
				_log.warning("ZoneManager: Main directory " + mainDir.getAbsolutePath() + " hasn't been found.");
				return;
			}
			
			int fileCounter = 0;
			for (final File file : mainDir.listFiles())
			{
				if (file.isFile() && file.getName().endsWith(".xml"))
				{
					// Set dynamically the ID range of next XML loading file.
					_lastDynamicId = fileCounter++ * 1000;
					loadFileZone(file, worldRegions);
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "ZoneManager: Error while loading zones.", e);
			return;
		}
		
		// get size
		int size = 0;
		for (Map<Integer, ? extends L2ZoneType> map : _classZones.values())
			size += map.size();
		
		_log.info("ZoneManager: Loaded " + _classZones.size() + " zones classes and total " + size + " zones.");
	}
	
	private void loadFileZone(final File f, WorldRegion[][] worldRegions) throws Exception
	{
		final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				NamedNodeMap attrs = n.getAttributes();
				Node attribute = attrs.getNamedItem("enabled");
				if (attribute != null && !Boolean.parseBoolean(attribute.getNodeValue()))
					continue;
				
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("zone".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap nnmd = d.getAttributes();
						
						// Generate dynamically zone's ID.
						int zoneId = _lastDynamicId++;
						
						// Dynamic id is replaced by handwritten id if existing.
						attribute = nnmd.getNamedItem("id");
						if (attribute != null)
							zoneId = Integer.parseInt(attribute.getNodeValue());
						
						final String zoneType = nnmd.getNamedItem("type").getNodeValue();
						final String zoneShape = nnmd.getNamedItem("shape").getNodeValue();
						final int minZ = Integer.parseInt(nnmd.getNamedItem("minZ").getNodeValue());
						final int maxZ = Integer.parseInt(nnmd.getNamedItem("maxZ").getNodeValue());
						
						// Create the zone
						Class<?> newZone;
						try
						{
							newZone = Class.forName("com.l2jmega.gameserver.model.zone.type.L2" + zoneType);
						}
						catch (ClassNotFoundException e)
						{
							_log.warning("ZoneData: No such zone type: " + zoneType + " in file: " + f.getName());
							continue;
						}
						
						Constructor<?> zoneConstructor = newZone.getConstructor(int.class);
						L2ZoneType temp = (L2ZoneType) zoneConstructor.newInstance(zoneId);
						
						// Get the zone shape from sql
						try
						{
							List<int[]> rs = new ArrayList<>();
							
							// loading from XML first
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("node".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									int[] point = new int[2];
									point[0] = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
									point[1] = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
									rs.add(point);
								}
							}
							
							int[][] coords = rs.toArray(new int[rs.size()][]);
							
							if (coords == null || coords.length == 0)
							{
								_log.warning("ZoneData: missing data for zone: " + zoneId + " on file: " + f.getName());
								continue;
							}
							
							// Create this zone. Parsing for cuboids is a bit different than for other polygons cuboids need exactly 2 points to be defined.
							// Other polygons need at least 3 (one per vertex)
							if (zoneShape.equalsIgnoreCase("Cuboid"))
							{
								if (coords.length == 2)
									temp.setZone(new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], minZ, maxZ));
								else
								{
									_log.warning("ZoneData: Missing cuboid vertex in sql data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else if (zoneShape.equalsIgnoreCase("NPoly"))
							{
								// nPoly needs to have at least 3 vertices
								if (coords.length > 2)
								{
									final int[] aX = new int[coords.length];
									final int[] aY = new int[coords.length];
									for (int i = 0; i < coords.length; i++)
									{
										aX[i] = coords[i][0];
										aY[i] = coords[i][1];
									}
									temp.setZone(new ZoneNPoly(aX, aY, minZ, maxZ));
								}
								else
								{
									_log.warning("ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else if (zoneShape.equalsIgnoreCase("Cylinder"))
							{
								// A Cylinder zone requires a center point at x,y and a radius
								attrs = d.getAttributes();
								final int zoneRad = Integer.parseInt(attrs.getNamedItem("rad").getNodeValue());
								if (coords.length == 1 && zoneRad > 0)
									temp.setZone(new ZoneCylinder(coords[0][0], coords[0][1], minZ, maxZ, zoneRad));
								else
								{
									_log.warning("ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else
							{
								_log.warning("ZoneData: Unknown shape: " + zoneShape + " in file: " + f.getName());
								continue;
							}
						}
						catch (Exception e)
						{
							_log.log(Level.WARNING, "ZoneData: Failed to load zone " + zoneId + " coordinates: " + e.getMessage(), e);
						}
						
						// Check for additional parameters
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("stat".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								String name = attrs.getNamedItem("name").getNodeValue();
								String val = attrs.getNamedItem("val").getNodeValue();
								
								temp.setParameter(name, val);
							}
							else if ("spawn".equalsIgnoreCase(cd.getNodeName()) && temp instanceof L2SpawnZone)
							{
								attrs = cd.getAttributes();
								int spawnX = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
								int spawnY = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
								int spawnZ = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
								
								Node val = attrs.getNamedItem("isChaotic");
								if (val != null && Boolean.parseBoolean(val.getNodeValue()))
									((L2SpawnZone) temp).addChaoticSpawn(spawnX, spawnY, spawnZ);
								else
									((L2SpawnZone) temp).addSpawn(spawnX, spawnY, spawnZ);
							}
						}
						
						addZone(zoneId, temp);
						
						// Register the zone into any world region it intersects with...
						for (int x = 0; x < worldRegions.length; x++)
						{
							for (int y = 0; y < worldRegions[x].length; y++)
							{
								if (temp.getZone().intersectsRectangle(World.getRegionX(x), World.getRegionX(x + 1), World.getRegionY(y), World.getRegionY(y + 1)))
									worldRegions[x][y].addZone(temp);
							}
						}
					}
				}
			}
		}
	}
	
	public final void save()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// clear table first
			PreparedStatement ps = con.prepareStatement(DELETE_GRAND_BOSS_LIST);
			ps.executeUpdate();
			ps.close();
			
			// store actual data
			ps = con.prepareStatement(INSERT_GRAND_BOSS_LIST);
			for (L2ZoneType zone : _classZones.get(L2BossZone.class).values())
			{
				for (int player : ((L2BossZone) zone).getAllowedPlayers())
				{
					ps.setInt(1, player);
					ps.setInt(2, zone.getId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
			ps.close();
			
			_log.info("ZoneManager: Saved L2BossZone data.");
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "ZoneManager: Couldn't store boss zones to database: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Add new zone
	 * @param id
	 * @param <T>
	 * @param zone
	 */
	@SuppressWarnings("unchecked")
	public <T extends L2ZoneType> void addZone(Integer id, T zone)
	{
		// _zones.put(id, zone);
		Map<Integer, T> map = (Map<Integer, T>) _classZones.get(zone.getClass());
		if (map == null)
		{
			map = new HashMap<>();
			map.put(id, zone);
			_classZones.put(zone.getClass(), map);
		}
		else
			map.put(id, zone);
	}
	
	/**
	 * Return all zones by class type
	 * @param <T>
	 * @param zoneType Zone class
	 * @return Collection of zones
	 */
	@SuppressWarnings("unchecked")
	public <T extends L2ZoneType> Collection<T> getAllZones(Class<T> zoneType)
	{
		return (Collection<T>) _classZones.get(zoneType).values();
	}
	
	/**
	 * Get zone by ID
	 * @param id
	 * @return
	 * @see #getZoneById(int, Class)
	 */
	public L2ZoneType getZoneById(int id)
	{
		for (Map<Integer, ? extends L2ZoneType> map : _classZones.values())
		{
			if (map.containsKey(id))
				return map.get(id);
		}
		return null;
	}
	
	/**
	 * Get zone by ID and zone class
	 * @param <T>
	 * @param id
	 * @param zoneType
	 * @return zone
	 */
	@SuppressWarnings("unchecked")
	public <T extends L2ZoneType> T getZoneById(int id, Class<T> zoneType)
	{
		return (T) _classZones.get(zoneType).get(id);
	}
	
	/**
	 * Returns all zones from where the object is located
	 * @param object
	 * @return zones
	 */
	public List<L2ZoneType> getZones(WorldObject object)
	{
		return getZones(object.getX(), object.getY(), object.getZ());
	}
	
	/**
	 * Returns zone from where the object is located by type
	 * @param <T>
	 * @param object
	 * @param type
	 * @return zone
	 */
	public <T extends L2ZoneType> T getZone(WorldObject object, Class<T> type)
	{
		if (object == null)
			return null;
		
		return getZone(object.getX(), object.getY(), object.getZ(), type);
	}
	
	public final static L2FlagZone getPartyZone(Creature character)
	{
		if (character == null)
			return null;
		
		for (L2ZoneType temp : ZoneManager.getInstance().getZones(character.getX(), character.getY(), character.getZ()))
		{
			if (temp instanceof L2FlagZone && temp.isCharacterInZone(character))
				return ((L2FlagZone) temp);
		}
		
		return null;
	}
	
	public final static L2RaidZone getRaidZone(Creature character)
	{
		if (character == null)
			return null;
		
		for (L2ZoneType temp : ZoneManager.getInstance().getZones(character.getX(), character.getY(), character.getZ()))
		{
			if (temp instanceof L2RaidZone && temp.isCharacterInZone(character))
				return ((L2RaidZone) temp);
		}
		
		return null;
	}
	
	public final static L2RaidZoneNoFlag getRaidZoneNoFlag(Creature character)
	{
		if (character == null)
			return null;
		
		for (L2ZoneType temp : ZoneManager.getInstance().getZones(character.getX(), character.getY(), character.getZ()))
		{
			if (temp instanceof L2RaidZoneNoFlag && temp.isCharacterInZone(character))
				return ((L2RaidZoneNoFlag) temp);
		}
		
		return null;
	}
	
	public final static L2SoloZone getSoloZone(Creature character)
	{
		if (character == null)
			return null;
		
		for (L2ZoneType temp : ZoneManager.getInstance().getZones(character.getX(), character.getY(), character.getZ()))
		{
			if (temp instanceof L2SoloZone && temp.isCharacterInZone(character))
				return ((L2SoloZone) temp);
		}
		
		return null;
	}
	
	/**
	 * Returns all zones from given coordinates (plane)
	 * @param x
	 * @param y
	 * @return zones
	 */
	public List<L2ZoneType> getZones(int x, int y)
	{
		final List<L2ZoneType> temp = new ArrayList<>();
		for (L2ZoneType zone : World.getInstance().getRegion(x, y).getZones())
		{
			if (zone.isInsideZone(x, y))
				temp.add(zone);
		}
		return temp;
	}
	
	/**
	 * Returns all zones from given coordinates
	 * @param x
	 * @param y
	 * @param z
	 * @return zones
	 */
	public List<L2ZoneType> getZones(int x, int y, int z)
	{
		final List<L2ZoneType> temp = new ArrayList<>();
		for (L2ZoneType zone : World.getInstance().getRegion(x, y).getZones())
		{
			if (zone.isInsideZone(x, y, z))
				temp.add(zone);
		}
		return temp;
	}
	
	/**
	 * Returns zone from given coordinates
	 * @param <T>
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @return zone
	 */
	@SuppressWarnings("unchecked")
	public <T extends L2ZoneType> T getZone(int x, int y, int z, Class<T> type)
	{
		for (L2ZoneType zone : World.getInstance().getRegion(x, y).getZones())
		{
			if (zone.isInsideZone(x, y, z) && type.isInstance(zone))
				return (T) zone;
		}
		return null;
	}
	
	/**
	 * Add an item on debug list. Used to visualize zones.
	 * @param item : The item to add.
	 */
	public void addDebugItem(ItemInstance item)
	{
		_debugItems.put(item.getObjectId(), item);
	}
	
	/**
	 * Remove all debug items from the world.
	 */
	public void clearDebugItems()
	{
		for (ItemInstance item : _debugItems.values())
			item.decayMe();
		
		_debugItems.clear();
	}
	
	public static final ZoneManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneManager INSTANCE = new ZoneManager();
	}
}