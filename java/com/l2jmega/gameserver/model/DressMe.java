package com.l2jmega.gameserver.model;

import com.l2jmega.gameserver.templates.StatsSet;



public class DressMe
{
	private final int _itemId;
	private int _hairId;
	private final int _chestId;
	private final int _legsId;
	private final int _glovesId;
	private final int _feetId;
	
	public DressMe(StatsSet set)
	{
		_itemId = set.getInteger("itemId", 0);
		_hairId = set.getInteger("hairId", 0);
		_chestId = set.getInteger("chestId", 0);
		_legsId = set.getInteger("legsId", 0);
		_glovesId = set.getInteger("glovesId", 0);
		_feetId = set.getInteger("feetId", 0);
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public int getHairId()
	{
		return _hairId;
	}
	

	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getLegsId()
	{
		return _legsId;
	}
	
	public int getGlovesId()
	{
		return _glovesId;
	}
	
	public int getFeetId()
	{
		return _feetId;
	}
	
	public void setHairId(int val) {
		_hairId = val;
	}
	
	public int setChestId(int val)
	{
		return _chestId;
	}
	
	public int setLegsId(int val)
	{
		return _legsId;
	}
	
	public int setGlovesId(int val)
	{
		return _glovesId;
	}
	
	public int setFeetId(int val)
	{
		return _feetId;
	}
}