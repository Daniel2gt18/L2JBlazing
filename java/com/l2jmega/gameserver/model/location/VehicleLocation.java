package com.l2jmega.gameserver.model.location;

/**
 * A datatype extending {@link Location} used for boats. It notably holds move speed and rotation speed.
 */
public class VehicleLocation extends Location
{
	private int _moveSpeed;
	private int _rotationSpeed;
	
	public VehicleLocation(int x, int y, int z)
	{
		super(x, y, z);
		
		_moveSpeed = 350;
		_rotationSpeed = 4000;
	}
	
	public VehicleLocation(int x, int y, int z, int moveSpeed, int rotationSpeed)
	{
		super(x, y, z);
		
		_moveSpeed = moveSpeed;
		_rotationSpeed = rotationSpeed;
	}
	
	public int getMoveSpeed()
	{
		return _moveSpeed;
	}
	
	public int getRotationSpeed()
	{
		return _rotationSpeed;
	}
}