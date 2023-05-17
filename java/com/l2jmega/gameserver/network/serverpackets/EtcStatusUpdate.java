package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.templates.skills.L2EffectFlag;

/**
 * @author Luca Baldi
 */
public class EtcStatusUpdate extends L2GameServerPacket
{
	private final Player _activeChar;
	
	public EtcStatusUpdate(Player activeChar)
	{
		_activeChar = activeChar;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xF3);
		writeD(_activeChar.getCharges());
		writeD(_activeChar.getWeightPenalty());
		writeD((_activeChar.isInRefusalMode() || _activeChar.getMessageRefusal() || _activeChar.isChatBanned()) ? 1 : 0);
		writeD(_activeChar.isInsideZone(ZoneId.DANGER_AREA) ? 1 : 0);
		writeD((_activeChar.getExpertiseWeaponPenalty() || _activeChar.getExpertiseArmorPenalty() > 0) ? 1 : 0);
		writeD(_activeChar.isAffected(L2EffectFlag.CHARM_OF_COURAGE) ? 1 : 0);
		writeD(_activeChar.getDeathPenaltyBuffLevel());
	}
}