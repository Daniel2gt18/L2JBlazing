package com.l2jmega.gameserver.scripting.tasks;

import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.UserInfo;
import com.l2jmega.gameserver.scripting.ScheduledQuest;

public final class Recommendation extends ScheduledQuest
{
	public Recommendation()
	{
		super(-1, "tasks");
	}
	
	@Override
	public final void onStart()
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.restartRecom();
			player.sendPacket(new UserInfo(player));
		}
		
		_log.config("Recommendation: Recommendation has been reset.");
	}
	
	@Override
	public final void onEnd()
	{
	}
}