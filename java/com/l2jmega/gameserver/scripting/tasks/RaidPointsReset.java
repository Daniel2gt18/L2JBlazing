package com.l2jmega.gameserver.scripting.tasks;

import java.util.Map;

import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.scripting.ScheduledQuest;

public final class RaidPointsReset extends ScheduledQuest
{
	public RaidPointsReset()
	{
		super(-1, "tasks");
	}
	
	@Override
	public final void onStart()
	{
		// reward clan reputation points
		Map<Integer, Integer> rankList = RaidBossPointsManager.getInstance().getRankList();
		for (Clan c : ClanTable.getInstance().getClans())
		{
			for (Map.Entry<Integer, Integer> entry : rankList.entrySet())
			{
				if (entry.getValue() <= 100 && c.isMember(entry.getKey()))
				{
					int reputation = 0;
					switch (entry.getValue())
					{
						case 1:
							reputation = 1250;
							break;
						case 2:
							reputation = 900;
							break;
						case 3:
							reputation = 700;
							break;
						case 4:
							reputation = 600;
							break;
						case 5:
							reputation = 450;
							break;
						case 6:
							reputation = 350;
							break;
						case 7:
							reputation = 300;
							break;
						case 8:
							reputation = 200;
							break;
						case 9:
							reputation = 150;
							break;
						case 10:
							reputation = 100;
							break;
						default:
							if (entry.getValue() <= 50)
								reputation = 25;
							else
								reputation = 12;
							break;
					}
					c.addReputationScore(reputation);
				}
			}
		}
		
		RaidBossPointsManager.getInstance().cleanUp();
		_log.info("RaidPointsReset: Raid boss points were added to clan reputation score.");
	}
	
	@Override
	public final void onEnd()
	{
	}
}