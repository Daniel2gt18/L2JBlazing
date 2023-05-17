package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.commons.util.ArraysUtil;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * A mother-trees zone
 * @author durgus
 */
public class L2MotherTreeZone extends L2ZoneType
{
	private int _enterMsg;
	private int _leaveMsg;
	private int _mpRegen;
	private int _hpRegen;
	private int[] _race;
	
	public L2MotherTreeZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("enterMsgId"))
			_enterMsg = Integer.valueOf(value);
		else if (name.equals("leaveMsgId"))
			_leaveMsg = Integer.valueOf(value);
		else if (name.equals("MpRegenBonus"))
			_mpRegen = Integer.valueOf(value);
		else if (name.equals("HpRegenBonus"))
			_hpRegen = Integer.valueOf(value);
		else if (name.equals("affectedRace"))
		{
			final String[] races = value.split(",");
			
			_race = new int[races.length];
			
			int i = 0;
			for (String race : races)
				_race[i++] = Integer.parseInt(race);
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected boolean isAffected(Creature character)
	{
		if (character instanceof Player && _race != null)
		{
			if (!ArraysUtil.contains(_race, ((Player) character).getRace().ordinal()))
				return false;
		}
		return true;
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			Player player = (Player) character;
			
			player.setInsideZone(ZoneId.MOTHER_TREE, true);
			
			if (_enterMsg != 0)
				player.sendPacket(SystemMessage.getSystemMessage(_enterMsg));
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Player)
		{
			Player player = (Player) character;
			
			player.setInsideZone(ZoneId.MOTHER_TREE, false);
			
			if (_leaveMsg != 0)
				player.sendPacket(SystemMessage.getSystemMessage(_leaveMsg));
		}
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	public int getMpRegenBonus()
	{
		return _mpRegen;
	}
	
	public int getHpRegenBonus()
	{
		return _hpRegen;
	}
}