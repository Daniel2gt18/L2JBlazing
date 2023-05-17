package com.l2jmega.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jmega.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.CursedWeapon;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.network.serverpackets.ExCursedWeaponLocation;
import com.l2jmega.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;

/**
 * Format: (ch)
 * @author -Wooden-
 */
public final class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final List<CursedWeaponInfo> list = new ArrayList<>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			if (!cw.isActive())
				continue;
			
			Location pos = cw.getWorldPosition();
			if (pos != null)
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
		}
		
		if (!list.isEmpty())
			activeChar.sendPacket(new ExCursedWeaponLocation(list));
	}
}