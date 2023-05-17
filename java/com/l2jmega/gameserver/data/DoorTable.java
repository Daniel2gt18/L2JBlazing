package com.l2jmega.gameserver.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.l2jmega.commons.geometry.Polygon;

import com.l2jmega.gameserver.geoengine.GeoEngine;
import com.l2jmega.gameserver.geoengine.geodata.ABlock;
import com.l2jmega.gameserver.geoengine.geodata.GeoStructure;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.template.DoorTemplate;
import com.l2jmega.gameserver.model.actor.template.DoorTemplate.DoorType;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DoorTable
{
	private static final Logger _log = Logger.getLogger(DoorTable.class.getName());
	
	private final Map<Integer, Door> _doors = new HashMap<>();
	
	public static DoorTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected DoorTable()
	{
		load();
	}
	
	public final void reload()
	{
		for (Door door : _doors.values())
			door.openMe();
		
		_doors.clear();
		
		for (Castle castle : CastleManager.getInstance().getCastles())
			castle.getDoors().clear();
		
		load();
		spawn();
	}
	
	private final void load()
	{
		try
		{
			// load doors
			File f = new File("./data/xml/doors.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equals(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if (d.getNodeName().equals("door"))
						{
							NamedNodeMap attrs = d.getAttributes();
							final StatsSet stats = new StatsSet();
							
							// Verify if the door got an id, else skip it
							Node att = attrs.getNamedItem("id");
							if (att == null)
							{
								_log.severe("DoorTable: Missing ID for door, skipping.");
								continue;
							}
							int id = Integer.parseInt(att.getNodeValue());
							stats.set("id", id);
							
							// Verify if the door got an id, else skip it
							att = attrs.getNamedItem("type");
							if (att == null)
							{
								_log.severe("DoorTable: Missing type for door id: " + stats.getString("id") + ", skipping.");
								continue;
							}
							stats.set("type", att.getNodeValue());
							
							// Verify if the door got a level, else skip it
							att = attrs.getNamedItem("level");
							if (att == null)
							{
								_log.severe("DoorTable: Missing level for door id: " + stats.getString("id") + ", skipping.");
								continue;
							}
							stats.set("level", att.getNodeValue());
							
							// Verify if the door got a name, else skip it
							att = attrs.getNamedItem("name");
							if (att == null)
							{
								_log.severe("DoorTable: Missing name for door id: " + stats.getString("id") + ", skipping.");
								continue;
							}
							stats.set("name", att.getNodeValue());
							
							int posX = 0;
							int posY = 0;
							int posZ = 0;
							
							List<int[]> _coords = new ArrayList<>();
							int minX = Integer.MAX_VALUE;
							int maxX = Integer.MIN_VALUE;
							int minY = Integer.MAX_VALUE;
							int maxY = Integer.MIN_VALUE;
							for (Node data = d.getFirstChild(); data != null; data = data.getNextSibling())
							{
								attrs = data.getAttributes();
								
								if (data.getNodeName().equals("castle"))
								{
									stats.set("castle", attrs.getNamedItem("id").getNodeValue());
								}
								else if (data.getNodeName().equals("position"))
								{
									posX = Integer.parseInt(attrs.getNamedItem("x").getNodeValue());
									posY = Integer.parseInt(attrs.getNamedItem("y").getNodeValue());
									posZ = Integer.parseInt(attrs.getNamedItem("z").getNodeValue());
								}
								else if (data.getNodeName().equals("coordinates"))
								{
									for (Node loc = data.getFirstChild(); loc != null; loc = loc.getNextSibling())
									{
										if (!loc.getNodeName().equals("loc"))
											continue;
										
										attrs = loc.getAttributes();
										int x = Integer.parseInt(attrs.getNamedItem("x").getNodeValue());
										int y = Integer.parseInt(attrs.getNamedItem("y").getNodeValue());
										
										_coords.add(new int[]
										{
											x,
											y
										});
										
										minX = Math.min(minX, x);
										maxX = Math.max(maxX, x);
										minY = Math.min(minY, y);
										maxY = Math.max(maxY, y);
									}
								}
								else if (data.getNodeName().equals("stats") || data.getNodeName().equals("function"))
								{
									// loads hp, pDef, height, etc stats and special function parameters
									
									// get all nodes
									for (int i = 0; i < attrs.getLength(); i++)
									{
										// add them to stats by node name and node value
										Node node = attrs.item(i);
										stats.set(node.getNodeName(), node.getNodeValue());
									}
								}
							}
							
							// create basic description of door, taking extended outer dimensions of door
							final int x = GeoEngine.getGeoX(minX) - 1;
							final int y = GeoEngine.getGeoY(minY) - 1;
							final int sizeX = (GeoEngine.getGeoX(maxX) + 1) - x + 1;
							final int sizeY = (GeoEngine.getGeoY(maxY) + 1) - y + 1;
							
							// check door Z and adjust it
							final int geoX = GeoEngine.getGeoX(posX);
							final int geoY = GeoEngine.getGeoY(posY);
							final int geoZ = GeoEngine.getInstance().getHeightNearest(geoX, geoY, posZ);
							final ABlock block = GeoEngine.getInstance().getBlock(geoX, geoY);
							final int i = block.getIndexAbove(geoX, geoY, geoZ);
							if (i != -1)
							{
								final int layerDiff = block.getHeight(i) - geoZ;
								if (stats.getInteger("height") > layerDiff)
									stats.set("height", layerDiff - GeoStructure.CELL_IGNORE_HEIGHT);
							}
							
							final int limit = stats.getEnum("type", DoorType.class) == DoorType.WALL ? GeoStructure.CELL_IGNORE_HEIGHT * 4 : GeoStructure.CELL_IGNORE_HEIGHT;
							
							// create 2D door description and calculate limit coordinates
							final boolean[][] inside = new boolean[sizeX][sizeY];
							final Polygon polygon = new Polygon(id, _coords);
							for (int ix = 0; ix < sizeX; ix++)
							{
								for (int iy = 0; iy < sizeY; iy++)
								{
									// get geodata coordinates
									int gx = x + ix;
									int gy = y + iy;
									
									// check layer height
									int z = GeoEngine.getInstance().getHeightNearest(gx, gy, posZ);
									if (Math.abs(z - posZ) > limit)
										continue;
									
									// get world coordinates
									int worldX = GeoEngine.getWorldX(gx);
									int worldY = GeoEngine.getWorldY(gy);
									
									// set inside flag
									cell:
									for (int wix = worldX - 6; wix <= worldX + 6; wix += 2)
									{
										for (int wiy = worldY - 6; wiy <= worldY + 6; wiy += 2)
										{
											if (polygon.isInside(wix, wiy))
											{
												inside[ix][iy] = true;
												break cell;
											}
										}
									}
								}
							}
							
							// set world coordinates
							stats.set("posX", posX);
							stats.set("posY", posY);
							stats.set("posZ", posZ);
							
							// set geodata coordinates and geodata
							stats.set("geoX", x);
							stats.set("geoY", y);
							stats.set("geoZ", geoZ);
							stats.set("geoData", GeoEngine.calculateGeoObject(inside));
							
							// set other required stats as default value
							stats.set("pAtk", 0);
							stats.set("mAtk", 0);
							stats.set("runSpd", 0);
							// default radius set to 16 - affects distance for melee attacks
							stats.set("radius", 16);
							
							// create door template
							final DoorTemplate template = new DoorTemplate(stats);
							
							// create door instance
							final Door door = new Door(IdFactory.getInstance().getNextId(), template);
							door.setCurrentHpMp(door.getMaxHp(), door.getMaxMp());
							door.getPosition().set(posX, posY, posZ);
							
							_doors.put(door.getDoorId(), door);
						}
					}
				}
			}
			
			_log.info("DoorTable: Loaded " + _doors.size() + " doors templates.");
		}
		catch (Exception e)
		{
			_log.warning("DoorTable: Error while creating table: " + e);
		}
	}
	
	/**
	 * Spawns doors into the world.
	 */
	public final void spawn()
	{
		// Note: keep as side-method, do not join to the load(). On initial load, the DoorTable.getInstance() is not initialized, yet L2DoorInstance is calling it during spawn process...causing NPE -> one advantage/disadvantage of singletons.
		
		// spawn doors
		for (Door door : _doors.values())
			door.spawnMe();
		
		// load doors upgrades
		for (Castle castle : CastleManager.getInstance().getCastles())
			castle.loadDoorUpgrade();
	}
	
	public Door getDoor(int id)
	{
		return _doors.get(id);
	}
	
	public Collection<Door> getDoors()
	{
		return _doors.values();
	}
	
	private static class SingletonHolder
	{
		protected static final DoorTable _instance = new DoorTable();
	}
}