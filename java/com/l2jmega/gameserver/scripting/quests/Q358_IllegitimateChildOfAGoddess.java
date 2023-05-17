package com.l2jmega.gameserver.scripting.quests;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.Quest;
import com.l2jmega.gameserver.scripting.QuestState;

public class Q358_IllegitimateChildOfAGoddess extends Quest
{
	private static final String qn = "Q358_IllegitimateChildOfAGoddess";
	
	// Item
	private static final int SCALE = 5868;
	
	// Reward
	private static final int REWARD[] =
	{
		6329,
		6331,
		6333,
		6335,
		6337,
		6339,
		5364,
		5366
	};
	
	public Q358_IllegitimateChildOfAGoddess()
	{
		super(358, "Illegitimate Child of a Goddess");
		
		setItemsIds(SCALE);
		
		addStartNpc(30862); // Oltlin
		addTalkId(30862);
		
		addKillId(20672, 20673); // Trives, Falibati
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30862-05.htm"))
		{
			st.setState(STATE_STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 63) ? "30862-01.htm" : "30862-02.htm";
				break;
			
			case STATE_STARTED:
				if (st.getInt("cond") == 1)
					htmltext = "30862-06.htm";
				else
				{
					htmltext = "30862-07.htm";
					st.takeItems(SCALE, -1);
					st.giveItems(REWARD[Rnd.get(REWARD.length)], 1);
					st.playSound(QuestState.SOUND_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		QuestState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
			return null;
		
		if (st.dropItems(SCALE, 1, 108, (npc.getNpcId() == 20672) ? 680000 : 660000))
			st.set("cond", "2");
		
		return null;
	}
}