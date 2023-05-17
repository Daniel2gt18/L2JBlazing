package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.data.MapRegionTable.TeleportType;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.olympiad.OlympiadGameTask;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import com.l2jmega.gameserver.network.serverpackets.ExOlympiadUserInfo;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * An olympiad stadium
 * @author durgus, DS
 */
public class L2OlympiadStadiumZone extends L2SpawnZone
{
	OlympiadGameTask _task = null;
	
	public L2OlympiadStadiumZone(int id)
	{
		super(id);
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public final void broadcastStatusUpdate(Player player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (Player plyr : getKnownTypeInside(Player.class))
		{
			if (!plyr.isInsideZone(ZoneId.TOURNAMENT) && plyr.isInObserverMode() || plyr.getOlympiadSide() != player.getOlympiadSide())
				plyr.sendPacket(packet);
		}
	}
	
	public final void broadcastPacketToObservers(L2GameServerPacket packet)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (!player.isInsideZone(ZoneId.TOURNAMENT) && player.isInObserverMode())
				player.sendPacket(packet);
		}
	}
	
	@Override
	protected final void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		character.setInsideZone(ZoneId.NO_RESTART, true);
		character.setInsideZone(ZoneId.OLYMPIAD, true);
		if (character instanceof Player)
		{
			final Player player = character.getActingPlayer();
			{
				if (player.isOlympiadProtection() || player.isInObserverMode())
				{
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							if (!player.isInsideZone(ZoneId.PEACE))
							{
								if (_task != null)
								{
									if (_task.isBattleStarted())
									{
										character.setInsideZone(ZoneId.PVP, true);
										if (character instanceof Player)
										{
											character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE));
											_task.getGame().sendOlympiadInfo(character);
										}
									}
								}
								
								if (character instanceof Playable)
								{
									// only participants, observers and GMs allowed
									if (!player.isGM() && !player.isInOlympiadMode() && !player.isInObserverMode())
										ThreadPool.schedule(new KickPlayer(player), 2000);
								}
							}
						}
					}, 2000);
					
				}
			}
		}
	}
	
	@Override
	protected final void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		character.setInsideZone(ZoneId.NO_RESTART, false);
		character.setInsideZone(ZoneId.OLYMPIAD, false);
		
		if (character instanceof Player)
		{
			final Player player = character.getActingPlayer();
			{
				if (player.isOlympiadProtection() || player.isInObserverMode())
				{
					if (_task != null)
					{
						if (_task.isBattleStarted())
						{
							character.setInsideZone(ZoneId.PVP, false);
							if (character instanceof Player)
							{
								character.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE));
								character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
							}
						}
					}
				}
			}
		}
		
	}
	
	public final void updateZoneStatusForCharactersInside()
	{
		if (_task == null)
			return;
		
		final boolean battleStarted = _task.isBattleStarted();
		final SystemMessage sm;
		if (battleStarted)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ENTERED_COMBAT_ZONE);
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.LEFT_COMBAT_ZONE);
		
		for (Creature character : _characterList.values())
		{
			if (battleStarted)
			{
				character.setInsideZone(ZoneId.PVP, true);
				if (character instanceof Player)
					character.sendPacket(sm);
			}
			else
			{
				character.setInsideZone(ZoneId.PVP, false);
				if (character instanceof Player)
				{
					character.sendPacket(sm);
					character.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	public class KickPlayer implements Runnable
	{
		private Player _player;
		
		public KickPlayer(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player != null)
			{
				final Summon summon = _player.getPet();
				if (summon != null)
					summon.unSummon(_player);
				
				_player.teleToLocation(TeleportType.TOWN);
				_player = null;
			}
		}
	}
}