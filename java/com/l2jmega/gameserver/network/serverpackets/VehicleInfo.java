package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.gameserver.model.actor.Vehicle;

public class VehicleInfo extends L2GameServerPacket
{
	private final int _objectId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	
	public VehicleInfo(Vehicle boat)
	{
		_objectId = boat.getObjectId();
		_x = boat.getX();
		_y = boat.getY();
		_z = boat.getZ();
		_heading = boat.getHeading();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0x59);
		writeD(_objectId);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
	}
}