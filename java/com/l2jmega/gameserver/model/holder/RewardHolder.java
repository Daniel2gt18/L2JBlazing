package com.l2jmega.gameserver.model.holder;

/**
 * @author MeGaPacK
 */
public class RewardHolder
{
	private int _id;
	private int _min;
	private int _max;
	private int _chance;
	
	/**
	 * @param rewardId
	 * @param min 
	 * @param max 
	 */
	public RewardHolder(int rewardId,int min,int max)
	{
		_id = rewardId;
		_min = min;
		_max = max;
		_chance = 100;
	}
	
	/**
	 * @param rewardId
	 * @param min 
	 * @param max 
	 * @param rewardChance
	 */
	public RewardHolder(int rewardId, int min, int max, int rewardChance)
	{
		_id = rewardId;
		_min = min;
		_max = max;
		_chance = rewardChance;
	}
	
	public int getRewardId()
	{
		return _id;
	}
	
	public int getMin()
	{
		return _min;
	}
	public int getMax()
	{
		return _max;
	}
	
	public int getRewardChance()
	{
		return _chance;
	}
	
	public void setId(int id)
	{
		_id = id;
	}
	
	public void setMin(int min)
	{
		_min = min;
	}
	
	public void setMax(int max)
	{
		_max = max;
	}
	
	public void setChance(int chance)
	{
		_chance = chance;
	}
}