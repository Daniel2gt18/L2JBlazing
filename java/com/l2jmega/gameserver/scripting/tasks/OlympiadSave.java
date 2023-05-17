package com.l2jmega.gameserver.scripting.tasks;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.olympiad.Olympiad;
import com.l2jmega.gameserver.scripting.Quest;

public final class OlympiadSave extends Quest implements Runnable
{
	public OlympiadSave()
	{
		super(-1, "tasks");
		
		ThreadPool.scheduleAtFixedRate(this, 900000, 1800000);
	}
	
	@Override
	public final void run()
	{
		if (Olympiad.getInstance().inCompPeriod())
		{
			Olympiad.getInstance().saveOlympiadStatus();
			_log.info("Olympiad: Data updated successfully.");
		}
	}
}