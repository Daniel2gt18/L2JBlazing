package com.l2jmega.gameserver.model.actor.status;

import com.l2jmega.gameserver.model.actor.Playable;

public class PlayableStatus extends CreatureStatus
{
	public PlayableStatus(Playable activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public Playable getActiveChar()
	{
		return (Playable) super.getActiveChar();
	}
}