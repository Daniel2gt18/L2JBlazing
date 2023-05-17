package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.instancemanager.BoatManager;
import com.l2jmega.gameserver.model.actor.Vehicle;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.type.WeaponType;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.MoveToLocationInVehicle;
import com.l2jmega.gameserver.network.serverpackets.StopMoveInVehicle;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_targetX == _originX && _targetY == _originY && _targetZ == _originZ)
		{
			activeChar.sendPacket(new StopMoveInVehicle(activeChar, _boatId));
			return;
		}
		
		if (activeChar.isAttackingNow() && activeChar.getAttackType() == WeaponType.BOW)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isSitting() || activeChar.isMovementDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getPet() != null)
		{
			activeChar.sendPacket(SystemMessageId.RELEASE_PET_ON_BOAT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Vehicle boat;
		if (activeChar.isInBoat())
		{
			boat = activeChar.getBoat();
			if (boat.getObjectId() != _boatId)
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		else
		{
			boat = BoatManager.getInstance().getBoat(_boatId);
			if (boat == null || !boat.isInsideRadius(activeChar, 300, true, false))
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			activeChar.setVehicle(boat);
		}
		
		activeChar.getVehiclePosition().set(_targetX, _targetY, _targetZ);
		activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, _targetX, _targetY, _targetZ, _originX, _originY, _originZ));
	}
}