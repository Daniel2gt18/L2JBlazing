package com.l2jmega.gameserver.model;

public final class L2EnchantSkillLearn
{
	private final int _id;
	private final int _level;
	private final int _baseLvl;
	private final int _prevLevel;
	private final int _enchant;
	
	public L2EnchantSkillLearn(int id, int lvl, int baseLvl, int prevLvl, int enchant)
	{
		_id = id;
		_level = lvl;
		_baseLvl = baseLvl;
		_prevLevel = prevLvl;
		_enchant = enchant;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return Returns the minLevel.
	 */
	public int getBaseLevel()
	{
		return _baseLvl;
	}
	
	/**
	 * @return Returns the minSkillLevel.
	 */
	public int getPrevLevel()
	{
		return _prevLevel;
	}
	
	/**
	 * @return Returns the minSkillLevel.
	 */
	public int getEnchant()
	{
		return _enchant;
	}
}