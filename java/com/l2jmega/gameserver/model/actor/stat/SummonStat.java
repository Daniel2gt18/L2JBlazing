package com.l2jmega.gameserver.model.actor.stat;

import com.l2jmega.gameserver.model.actor.Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public Summon getActiveChar()
	{
		return (Summon) super.getActiveChar();
	}
}