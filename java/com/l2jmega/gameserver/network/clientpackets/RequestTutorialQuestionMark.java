package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.actor.instance.ClassMaster;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.QuestState;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	int _number;
	
	@Override
	protected void readImpl()
	{
		_number = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
	    ClassMaster.onTutorialQuestionMark(player, _number);
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("QM" + _number + "", null, player);
	}
}