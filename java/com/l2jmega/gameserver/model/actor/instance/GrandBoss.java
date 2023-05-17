package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.entity.Hero;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.Earthquake;
import com.l2jmega.gameserver.network.serverpackets.ExRedSky;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages all Grand Bosses.
 */
public final class GrandBoss extends Monster
{
	/**
	 * Constructor for L2GrandBossInstance. This represent all grandbosses.
	 * @param objectId ID of the instance
	 * @param template L2NpcTemplate of the instance
	 */
	public GrandBoss(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setIsRaid(true);
	}
	
	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		final Player player = killer.getActingPlayer();
		if (player != null)
		{
			broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			broadcastPacket(new PlaySound("systemmsg_e.1209"));
			
	        if (player.getClan() != null && Config.GRANDBOSS_INFO_IDS_LIST.contains(Integer.valueOf(getNpcId())))
	            player.getClan().addclanBossScore(1);
			
			if (!player.isGM())
			{
				for (Player pl : World.getInstance().getPlayers())
				{
				if (player.getClan() != null)
					pl.sendChatMessage(0, Config.ANNOUNCE_ID, "Grand Boss ", getName() + " was killed by " + player.getName() + " of the clan " + player.getClan().getName());
				else
					pl.sendChatMessage(0, Config.ANNOUNCE_ID, "Grand Boss ", getName() + " was killed by " + player.getName());
				
				if(Config.EARTH_QUAKE){
					pl.broadcastPacket(new ExRedSky(10));
					pl.broadcastPacket(new Earthquake(pl.getX(), pl.getY(), pl.getZ(), 14, 10));
					}
				}
			}
			
			final L2Party party = player.getParty();
			if (party != null)
			{
				for (Player member : party.getPartyMembers())
				{
					RaidBossPointsManager.getInstance().addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
					if (member.isNoble())
						Hero.getInstance().setRBkilled(member.getObjectId(), getNpcId());
				}
			}
			else
			{
				RaidBossPointsManager.getInstance().addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
				if (player.isNoble())
					Hero.getInstance().setRBkilled(player.getObjectId(), getNpcId());
			}
		}
		
		return true;
	}
	
	@Override
	public boolean returnHome(boolean cleanAggro)
	{
		return false;
	}
}