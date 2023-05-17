package com.l2jmega.gameserver.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.MinionData;
import com.l2jmega.gameserver.model.PetDataEntry;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.actor.template.PetTemplate;
import com.l2jmega.gameserver.model.item.DropCategory;
import com.l2jmega.gameserver.model.item.DropData;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NpcTable
{
	private static final Logger _log = Logger.getLogger(NpcTable.class.getName());
	
	private final Map<Integer, NpcTemplate> _npcs = new HashMap<>();
	
	protected NpcTable()
	{
		load();
	}
	
	public void reloadAllNpc()
	{
		_npcs.clear();
		load();
	}
	
	  private static void hashFiles(String dirname, List<File> hash) {
		    File dir = new File("./data/xml/" + dirname);
		    if (!dir.isDirectory()) {
		      _log.config("Dir " + dir.getAbsolutePath() + " doesn't exist.");
		      return;
		    } 
		    File[] files = dir.listFiles();
		    for (File f : files) {
		      if (f.getName().endsWith(".xml"))
		        hash.add(f); 
		    } 
		  }
	
	private void load()
	{
		try
		{
		      List<File> files = new ArrayList<>();
		      hashFiles("npcs", files);
		      hashFiles("npcs/raidboss", files);
		      hashFiles("npcs/grandboss", files);
		      hashFiles("npcs/farmzone", files);
		      hashFiles("npcs/custom", files);
		      hashFiles("npcs/events", files);
		      
			final StatsSet set = new StatsSet();
			final StatsSet petSet = new StatsSet();
			
			for (File file : files)
			{
				final Document doc = XMLDocumentFactory.getInstance().loadDocument(file);
				
				Node list = doc.getFirstChild();
				for (Node npc = list.getFirstChild(); npc != null; npc = npc.getNextSibling())
				{
					if ("npc".equalsIgnoreCase(npc.getNodeName()))
					{
						NamedNodeMap attrs = npc.getAttributes();
						
						boolean mustUsePetTemplate = false; // Used to define template type.
						
						final int npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
						final int templateId = attrs.getNamedItem("idTemplate") == null ? npcId : Integer.parseInt(attrs.getNamedItem("idTemplate").getNodeValue());
						
						set.set("id", npcId);
						set.set("idTemplate", templateId);
						set.set("name", attrs.getNamedItem("name").getNodeValue());
						set.set("title", attrs.getNamedItem("title").getNodeValue());
						
						for (Node cat = npc.getFirstChild(); cat != null; cat = cat.getNextSibling())
						{
							if ("ai".equalsIgnoreCase(cat.getNodeName()))
							{
								attrs = cat.getAttributes();
								
								set.set("aiType", attrs.getNamedItem("type").getNodeValue());
								set.set("ssCount", Integer.parseInt(attrs.getNamedItem("ssCount").getNodeValue()));
								set.set("ssRate", Integer.parseInt(attrs.getNamedItem("ssRate").getNodeValue()));
								set.set("spsCount", Integer.parseInt(attrs.getNamedItem("spsCount").getNodeValue()));
								set.set("spsRate", Integer.parseInt(attrs.getNamedItem("spsRate").getNodeValue()));
								set.set("aggro", Integer.parseInt(attrs.getNamedItem("aggro").getNodeValue()));
								
								// Verify if the parameter exists.
								if (attrs.getNamedItem("clan") != null)
								{
									set.set("clan", attrs.getNamedItem("clan").getNodeValue().split(";"));
									set.set("clanRange", Integer.parseInt(attrs.getNamedItem("clanRange").getNodeValue()));
									
									// Verify if the parameter exists.
									if (attrs.getNamedItem("ignoredIds") != null)
										set.set("ignoredIds", attrs.getNamedItem("ignoredIds").getNodeValue());
								}
								
								set.set("canMove", Boolean.parseBoolean(attrs.getNamedItem("canMove").getNodeValue()));
								set.set("seedable", Boolean.parseBoolean(attrs.getNamedItem("seedable").getNodeValue()));
							}
							else if ("drops".equalsIgnoreCase(cat.getNodeName()))
							{
								final String type = set.getString("type");
								final boolean isRaid = type.equalsIgnoreCase("L2RaidBoss") || type.equalsIgnoreCase("L2GrandBoss");
								
								final List<DropCategory> drops = new ArrayList<>();
								
								for (Node dropCat = cat.getFirstChild(); dropCat != null; dropCat = dropCat.getNextSibling())
								{
									if ("category".equalsIgnoreCase(dropCat.getNodeName()))
									{
										attrs = dropCat.getAttributes();
										
										final DropCategory category = new DropCategory(Integer.parseInt(attrs.getNamedItem("id").getNodeValue()));
										
										for (Node item = dropCat.getFirstChild(); item != null; item = item.getNextSibling())
										{
											if ("drop".equalsIgnoreCase(item.getNodeName()))
											{
												attrs = item.getAttributes();
												
												final DropData data = new DropData();
												data.setItemId(Integer.parseInt(attrs.getNamedItem("itemid").getNodeValue()));
												data.setMinDrop(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
												data.setMaxDrop(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
												data.setChance(Integer.parseInt(attrs.getNamedItem("chance").getNodeValue()));
												
												if (ItemTable.getInstance().getTemplate(data.getItemId()) == null)
												{
													_log.warning("Droplist data for undefined itemId: " + data.getItemId());
													continue;
												}
												category.addDropData(data, isRaid);
											}
										}
										drops.add(category);
									}
								}
								set.set("drops", drops);
							}
							else if ("minions".equalsIgnoreCase(cat.getNodeName()))
							{
								final List<MinionData> minions = new ArrayList<>();
								
								for (Node minion = cat.getFirstChild(); minion != null; minion = minion.getNextSibling())
								{
									if ("minion".equalsIgnoreCase(minion.getNodeName()))
									{
										attrs = minion.getAttributes();
										
										final MinionData data = new MinionData();
										data.setMinionId(Integer.parseInt(attrs.getNamedItem("id").getNodeValue()));
										data.setAmountMin(Integer.parseInt(attrs.getNamedItem("min").getNodeValue()));
										data.setAmountMax(Integer.parseInt(attrs.getNamedItem("max").getNodeValue()));
										
										minions.add(data);
									}
								}
								set.set("minions", minions);
							}
							else if ("petdata".equalsIgnoreCase(cat.getNodeName()))
							{
								mustUsePetTemplate = true;
								
								attrs = cat.getAttributes();
								
								set.set("food1", Integer.parseInt(attrs.getNamedItem("food1").getNodeValue()));
								set.set("food2", Integer.parseInt(attrs.getNamedItem("food2").getNodeValue()));
								
								set.set("autoFeedLimit", Double.parseDouble(attrs.getNamedItem("autoFeedLimit").getNodeValue()));
								set.set("hungryLimit", Double.parseDouble(attrs.getNamedItem("hungryLimit").getNodeValue()));
								set.set("unsummonLimit", Double.parseDouble(attrs.getNamedItem("unsummonLimit").getNodeValue()));
								
								final Map<Integer, PetDataEntry> entries = new HashMap<>();
								
								for (Node petCat = cat.getFirstChild(); petCat != null; petCat = petCat.getNextSibling())
								{
									if ("stat".equalsIgnoreCase(petCat.getNodeName()))
									{
										attrs = petCat.getAttributes();
										
										// Get all nodes.
										for (int i = 0; i < attrs.getLength(); i++)
										{
											// Add them to stats by node name and node value.
											Node node = attrs.item(i);
											petSet.set(node.getNodeName(), node.getNodeValue());
										}
										
										entries.put(petSet.getInteger("level"), new PetDataEntry(petSet));
										petSet.clear();
									}
								}
								set.set("petData", entries);
							}
							else if ("set".equalsIgnoreCase(cat.getNodeName()))
							{
								attrs = cat.getAttributes();
								
								set.set(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("val").getNodeValue());
							}
							else if ("skills".equalsIgnoreCase(cat.getNodeName()))
							{
								final List<L2Skill> skills = new ArrayList<>();
								
								for (Node skillCat = cat.getFirstChild(); skillCat != null; skillCat = skillCat.getNextSibling())
								{
									if ("skill".equalsIgnoreCase(skillCat.getNodeName()))
									{
										attrs = skillCat.getAttributes();
										
										final int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
										final int level = Integer.parseInt(attrs.getNamedItem("level").getNodeValue());
										
										// Setup the npc's race. Don't register the skill.
										if (skillId == L2Skill.SKILL_NPC_RACE)
										{
											set.set("raceId", level);
											continue;
										}
										
										final L2Skill data = SkillTable.getInstance().getInfo(skillId, level);
										if (data == null)
											continue;
										
										skills.add(data);
									}
								}
								set.set("skills", skills);
							}
							else if ("teachTo".equalsIgnoreCase(cat.getNodeName()))
								set.set("teachTo", cat.getAttributes().getNamedItem("classes").getNodeValue());
						}
						
						_npcs.put(npcId, (mustUsePetTemplate) ? new PetTemplate(set) : new NpcTemplate(set));
					}
					set.clear();
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "NpcTable: Error parsing NPC templates : ", e);
		}
		_log.info("NpcTable: Loaded " + _npcs.size() + " NPC templates.");
	}
	
	public NpcTemplate getTemplate(int id)
	{
		return _npcs.get(id);
	}
	
	/**
	 * @param name to search.
	 * @return the template list of NPCs for a given name.
	 */
	public NpcTemplate getTemplateByName(String name)
	{
		for (NpcTemplate npcTemplate : _npcs.values())
		{
			if (npcTemplate.getName().equalsIgnoreCase(name))
				return npcTemplate;
		}
		return null;
	}
	
	/**
	 * Gets all templates matching the filter.
	 * @param filter
	 * @return the template list for the given filter
	 */
	public List<NpcTemplate> getTemplates(Predicate<NpcTemplate> filter)
	{
		return _npcs.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public Collection<NpcTemplate> getAllNpcs()
	{
		return _npcs.values();
	}
	
	public static NpcTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcTable _instance = new NpcTable();
	}
}