package com.l2jmega.gameserver.model.actor.stat;

import com.l2jmega.gameserver.model.actor.Vehicle;

public class VehicleStat extends CreatureStat
{
	private int _moveSpeed = 0;
	private int _rotationSpeed = 0;
	
	public VehicleStat(Vehicle activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public float getMoveSpeed()
	{
		return _moveSpeed;
	}
	
	public final void setMoveSpeed(int speed)
	{
		_moveSpeed = speed;
	}
	
	public final int getRotationSpeed()
	{
		return _rotationSpeed;
	}
	
	public final void setRotationSpeed(int speed)
	{
		_rotationSpeed = speed;
	}
}