package com.l2jmega.gameserver.scripting.tasks;

import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.scripting.ScheduledQuest;

public final class ClansLadder extends ScheduledQuest
{
	public ClansLadder()
	{
		super(-1, "tasks");
	}
	
	@Override
	public final void onStart()
	{
		ClanTable.getInstance().refreshClansLadder(true);
	}
	
	@Override
	public final void onEnd()
	{
	}
}