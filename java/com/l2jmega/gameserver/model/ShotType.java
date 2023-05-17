package com.l2jmega.gameserver.model;

/**
 * @author UnAfraid
 */
public enum ShotType
{
	SOULSHOT,
	SPIRITSHOT,
	BLESSED_SPIRITSHOT,
	FISH_SOULSHOT;
	
	private final int _mask;
	
	private ShotType()
	{
		_mask = (1 << ordinal());
	}
	
	public int getMask()
	{
		return _mask;
	}
}