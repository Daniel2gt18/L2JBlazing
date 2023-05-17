package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;

public class SiegeSummon extends Servitor
{
	public static final int SIEGE_GOLEM_ID = 14737;
	public static final int HOG_CANNON_ID = 14768;
	public static final int SWOOP_CANNON_ID = 14839;
	
	public SiegeSummon(int objectId, NpcTemplate template, Player owner, L2Skill skill)
	{
		super(objectId, template, owner, skill);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		if (!isInsideZone(ZoneId.SIEGE))
		{
			unSummon(getOwner());
			getOwner().sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
		}
	}
}