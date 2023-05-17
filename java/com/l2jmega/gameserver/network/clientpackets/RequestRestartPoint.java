package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.MapRegionTable;
import com.l2jmega.gameserver.data.MapRegionTable.TeleportType;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.ClanHallManager;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.ClanHall;
import com.l2jmega.gameserver.model.entity.ClanHall.ClanHallFunction;
import com.l2jmega.gameserver.model.entity.Siege;
import com.l2jmega.gameserver.model.entity.Siege.SiegeSide;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.pledge.Clan;

public final class RequestRestartPoint extends L2GameClientPacket
{
	protected static final Location JAIL_LOCATION = new Location(-114356, -249645, -2984);
	protected static final Location GIRAN_LOCATION_1 = new Location(82856, 148152, -3464);
	protected static final Location GIRAN_LOCATION_2 = new Location(82856, 149080, -3464);
	protected static final Location GIRAN_LOCATION_3 = new Location(81027, 149141, -3472);
	protected static final Location GIRAN_LOCATION_4 = new Location(81032, 148104, -3464);
	
	protected int _requestType;
	
	@Override
	protected void readImpl()
	{
		_requestType = readD();
	}
	
	class DeathTask implements Runnable
	{
		final Player _player;
		
		DeathTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			final Clan clan = _player.getClan();
			
			Location loc = null;
			
			long check = _player.getDeathTimer() + 2000;
			
			if (!_player._inEventTvT && !_player._inEventCTF && System.currentTimeMillis() < check)
			{
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						for (Player p : World.getInstance().getPlayers())
						{
							String all_hwids = p.getHWID();
							
							if (_player.isOnline())
							{
								if (all_hwids.equals(_player.getHWID()))
									p.logout();
							}
						}
					}
				}, 100);
				return;
			}
			
			if ((TvT.is_started() && _player._inEventTvT) || (CTF.is_started() && _player._inEventCTF))
				return;
			
			// Enforce type.
			if (_player.isInJail())
				_requestType = 27;
			else if (_player.isFestivalParticipant())
				_requestType = 4;
			
			// To clanhall.
			if (_requestType == 1)
			{
				if (clan == null || !clan.hasHideout())
				{
					_log.warning(_player.getName() + " called RestartPointPacket - To Clanhall while he doesn't have clan / Clanhall.");
					return;
				}
				
				loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.CLAN_HALL);
				
				final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
				if (ch != null)
				{
					final ClanHallFunction function = ch.getFunction(ClanHall.FUNC_RESTORE_EXP);
					if (function != null)
						_player.restoreExp(function.getLvl());
				}
			}
			// To castle.
			else if (_requestType == 2)
			{
				final Siege siege = CastleManager.getInstance().getSiege(_player);
				if (siege != null)
				{
					switch (siege.getSide(clan))
					{
						case DEFENDER:
						case OWNER:
							loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.CASTLE);
							break;
						
						case ATTACKER:
							loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.TOWN);
							break;
						
						default:
							_log.warning(_player.getName() + " called RestartPointPacket - To Castle while he isn't registered to any castle siege.");
							return;
					}
				}
				else
				{
					if (clan == null || !clan.hasCastle())
						return;
					
					loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.CASTLE);
				}
			}
			// To siege flag.
			else if (_requestType == 3)
				loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.SIEGE_FLAG);
			// Fixed.
			else if (_requestType == 4)
			{
				if (_player.isGM())
					loc = _player.getPosition();
				else
				{
					int rnd;
					rnd = 1 + (int) (Math.random() * 4);
					if (rnd == 1)
						loc = GIRAN_LOCATION_1;
					else if (rnd == 2)
						loc = GIRAN_LOCATION_2;
					else if (rnd == 3)
						loc = GIRAN_LOCATION_3;
					else if (rnd == 4)
						loc = GIRAN_LOCATION_4;
					else
						loc = GIRAN_LOCATION_1;
				}
				
			}
			// To jail.
			else if (_requestType == 27)
			{
				if (!_player.isInJail())
					return;
				
				loc = JAIL_LOCATION;
			}
			// Nothing has been found, use regular "To town" behavior.
			else
				loc = MapRegionTable.getInstance().getLocationToTeleport(_player, TeleportType.TOWN);
			
			_player.setIsIn7sDungeon(false);
			
			if (_player.isDead())
				_player.doRevive();
			
			_player.teleToLocation(loc, 100);
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.isFakeDeath())
		{
			player.stopFakeDeath(true);
			return;
		}
		
		if (!player.isDead())
			return;
		
		// Schedule a respawn delay if player is part of a clan registered in an active siege.
		if (player.getClan() != null)
		{
			final Siege siege = CastleManager.getInstance().getSiege(player);
			if (siege != null && siege.checkSide(player.getClan(), SiegeSide.ATTACKER))
			{
				ThreadPool.schedule(new DeathTask(player), Config.ATTACKERS_RESPAWN_DELAY);
				
				if (Config.ATTACKERS_RESPAWN_DELAY > 0)
					player.sendMessage("You will be teleported in " + Config.ATTACKERS_RESPAWN_DELAY / 1000 + " seconds.");
				
				return;
			}
		}
		
		// Run the task immediately (no need to schedule).
		new DeathTask(player).run();
	}
}