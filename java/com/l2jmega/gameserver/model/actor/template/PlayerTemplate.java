package com.l2jmega.gameserver.model.actor.template;

import java.util.ArrayList;
import java.util.List;

import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.templates.StatsSet;

public class PlayerTemplate extends CreatureTemplate
{
	private final ClassId _classId;
	
	private final int _fallingHeight;
	
	private final int _baseSwimSpd;
	
	private final double _collisionRadiusFemale;
	private final double _collisionHeightFemale;
	
	private final Location _spawn;
	
	private final int _classBaseLevel;
	
	private final double[] _hpTable;
	private final double[] _mpTable;
	private final double[] _cpTable;
	
	private final List<Item> _items = new ArrayList<>();
	
	public PlayerTemplate(ClassId classId, StatsSet set)
	{
		super(set);
		
		_classId = classId;
		
		_baseSwimSpd = set.getInteger("swimSpd", 1);
		
		_fallingHeight = set.getInteger("falling_height", 333);
		
		_collisionRadiusFemale = set.getDouble("radiusFemale");
		_collisionHeightFemale = set.getDouble("heightFemale");
		
		_spawn = new Location(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ"));
		
		_classBaseLevel = set.getInteger("baseLvl");
		
		// Feed HPs array from a String split.
		final String[] hpTable = set.getString("hpTable").split(";");
		
		_hpTable = new double[hpTable.length];
		for (int i = 0; i < hpTable.length; i++)
			_hpTable[i] = Double.parseDouble(hpTable[i]);
		
		// Feed MPs array from a String split.
		final String[] mpTable = set.getString("mpTable").split(";");
		
		_mpTable = new double[mpTable.length];
		for (int i = 0; i < mpTable.length; i++)
			_mpTable[i] = Double.parseDouble(mpTable[i]);
		
		// Feed CPs array from a String split.
		final String[] cpTable = set.getString("cpTable").split(";");
		
		_cpTable = new double[cpTable.length];
		for (int i = 0; i < cpTable.length; i++)
			_cpTable[i] = Double.parseDouble(cpTable[i]);
	}
	
	/**
	 * Add starter equipement.
	 * @param itemId the item to add if template is found
	 */
	public final void addItem(int itemId)
	{
		final Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item != null)
			_items.add(item);
	}
	
	/**
	 * @return itemIds of all the starter equipment
	 */
	public final List<Item> getItems()
	{
		return _items;
	}
	
	public final ClassId getClassId()
	{
		return _classId;
	}
	
	public final ClassRace getRace()
	{
		return _classId.getRace();
	}
	
	public final String getClassName()
	{
		return _classId.toString();
	}
	
	public final int getFallHeight()
	{
		return _fallingHeight;
	}
	
	public final int getBaseSwimSpeed()
	{
		return _baseSwimSpd;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionRadiusBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionRadius : _collisionRadiusFemale;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionHeightBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionHeight : _collisionHeightFemale;
	}
	
	public final Location getSpawn()
	{
		return _spawn;
	}
	
	public final int getClassBaseLevel()
	{
		return _classBaseLevel;
	}
	
	@Override
	public final double getBaseHpMax(int level)
	{
		return _hpTable[level - 1];
	}
	
	@Override
	public final double getBaseMpMax(int level)
	{
		return _mpTable[level - 1];
	}
	
	public final double getBaseCpMax(int level)
	{
		return _cpTable[level - 1];
	}
}