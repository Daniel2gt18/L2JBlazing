package com.l2jmega.gameserver.model.location;

/**
 * A datatype used to retain a 3D (x/y/z) point. It got the capability to be set and cleaned.
 */
public class Location
{
	public static final Location DUMMY_LOC = new Location(0, 0, 0);
	
	public volatile int _x;
	public volatile int _y;
	public volatile int _z;
	
	public Location(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	public Location(Location loc)
	{
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
	}
	
	@Override
	public String toString()
	{
		return _x + ", " + _y + ", " + _z;
	}
	
	@Override
	public int hashCode()
	{
		return _x ^ _y ^ _z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Location)
		{
			Location loc = (Location) o;
			return (loc.getX() == _x && loc.getY() == _y && loc.getZ() == _z);
		}
		
		return false;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public int getZ()
	{
		return _z;
	}
	
	public void set(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	public void set(Location loc)
	{
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
	}
	
	public void clean()
	{
		_x = 0;
		_y = 0;
		_z = 0;
	}
}