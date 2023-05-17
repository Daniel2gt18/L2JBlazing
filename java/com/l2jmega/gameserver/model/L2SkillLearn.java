package com.l2jmega.gameserver.model;

public final class L2SkillLearn
{
	private final int _id;
	private final int _level;
	private final int _spCost;
	private final int _minLevel;
	private final int _costid;
	private final int _costcount;
	
	public L2SkillLearn(int id, int lvl, int minLvl, int cost, int costid, int costcount)
	{
		_id = id;
		_level = lvl;
		_minLevel = minLvl;
		_spCost = cost;
		_costid = costid;
		_costcount = costcount;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public int getSpCost()
	{
		return _spCost;
	}
	
	public int getIdCost()
	{
		return _costid;
	}
	
	public int getCostCount()
	{
		return _costcount;
	}
}