package com.l2jmega.gameserver.scripting.tasks;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.instancemanager.SevenSigns;
import com.l2jmega.gameserver.instancemanager.SevenSignsFestival;
import com.l2jmega.gameserver.scripting.Quest;

public final class SevenSignsUpdate extends Quest implements Runnable
{
	public SevenSignsUpdate()
	{
		super(-1, "tasks");
		
		ThreadPool.scheduleAtFixedRate(this, 3600000, 3600000);
	}
	
	@Override
	public final void run()
	{
		if (!SevenSigns.getInstance().isSealValidationPeriod())
			SevenSignsFestival.getInstance().saveFestivalData(false);
		
		SevenSigns.getInstance().saveSevenSignsData();
		SevenSigns.getInstance().saveSevenSignsStatus();
		
		_log.info("SevenSigns: Data has been successfully saved.");
	}
}