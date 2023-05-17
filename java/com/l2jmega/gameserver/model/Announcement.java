package com.l2jmega.gameserver.model;

import java.util.concurrent.ScheduledFuture;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.location.SpawnLocation;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.util.Broadcast;
import com.l2jmega.util.Rnd;

/**
 * A datatype used to retain informations for announcements. It notably holds a {@link ScheduledFuture}.
 */
public class Announcement implements Runnable
{
	protected final String _message;
	
	protected int _value;
	protected boolean _auto;
	protected boolean _unlimited;
	
	protected int _initialDelay;
	protected int _delay;
	protected int _limit;
	protected int _tempLimit; // Temporary limit, used by current timer.
	
	protected ScheduledFuture<?> _task;
	
	public Announcement(String message, int value)
	{
		_message = message;
		_value = value;
	}
	
	public Announcement(String message, int value, boolean auto, int initialDelay, int delay, int limit)
	{
		_message = message;
		_value = value;
		_auto = auto;
		_initialDelay = initialDelay;
		_delay = delay;
		_limit = limit;
		
		if (_auto)
		{
			switch (_limit)
			{
				case 0: // unlimited
					_task = ThreadPool.scheduleAtFixedRate(this, _initialDelay * 1000, _delay * 1000); // self schedule at fixed rate
					_unlimited = true;
					break;
					
				default:
					_task = ThreadPool.schedule(this, _initialDelay * 1000); // self schedule (initial)
					_tempLimit = _limit;
					break;
			}
		}
	}
	
	@Override
	public void run()
	{
		if (!_unlimited)
		{
			if (_tempLimit == 0)
				return;
			
			_task = ThreadPool.schedule(this, _delay * 1000); // self schedule (worker)
			_tempLimit--;
		}
		Broadcast.announceToOnlinePlayers(_message, _value);
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public int isCustomValue()
	{
		return _value;
	}
	
	public boolean isAuto()
	{
		return _auto;
	}
	
	public int getInitialDelay()
	{
		return _initialDelay;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public int getLimit()
	{
		return _limit;
	}
	
	public void stopTask()
	{
		if (_task != null)
		{
			_task.cancel(true);
			_task = null;
		}
	}
	
	public void reloadTask()
	{
		stopTask();
		
		if (_auto)
		{
			switch (_limit)
			{
				case 0: // unlimited
					_task = ThreadPool.scheduleAtFixedRate(this, _initialDelay * 1000, _delay * 1000); // self schedule at fixed rate
					_unlimited = true;
					break;
					
				default:
					_task = ThreadPool.schedule(this, _initialDelay * 1000); // self schedule (initial)
					_tempLimit = _limit;
					break;
			}
		}
	}
	
	public static void announceToPlayers(String message)
	{
		for (Player player : World.getInstance().getPlayers())
			player.sendMessage(message);
	}
	
	public static void announceToPlayers(SystemMessage sm)
	{
		Broadcast.toAllOnlinePlayers(sm);
	}
	
	public static void announcePvP(String message)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (!player.isGM())
				player.sendMessage(message);
		}
	}
	
	public static void AnnounceEvents(String text)
	{
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static void Announce(String text)
	{
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static void AnnounceDropItem(SystemMessage msg)
	{
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID, "", "" + msg);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static void AnnounceScreen(String text)
	{
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static void SelfAnnounce(String text)
	{
		CreatureSay cs = new CreatureSay(0, 17, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static void SelfAnnounceScreen(String text)
	{
		ExShowScreenMessage msg = new ExShowScreenMessage("SYS:" + text, 6000);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(msg);
		}
	}
	
	public static void AnnouncePM(String text)
	{
		CreatureSay cs = new CreatureSay(0, Say2.TELL, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public static boolean pvp_register = false;
	
	public static void PvPAnnounce(String text)
	{
		pvp_register = true;
		
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				player.sendPacket(cs);
				
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						pvp_register = false;
					}
				}, 46000);
				
				final boolean bishop = (player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.ELVEN_ELDER);
				
				if (!(player.isOlympiadProtection() || player.isCursedWeaponEquipped() || player.isInObserverMode() || player.getFirstLog() || player._inEventCTF || player._inEventTvT || player.isInsideZone(ZoneId.PVP_CUSTOM) || player.isInsideZone(ZoneId.CUSTOM) || player.isInsideZone(ZoneId.RAID) || player.isInsideZone(ZoneId.RAID_NO_FLAG) || player.isInsideZone(ZoneId.FLAG) || player.isAio() || bishop) && Config.SCREN_MSG_PVP)
				{
					SpawnLocation _position = new SpawnLocation(Config.pvp_locx + Rnd.get(-100, 100), Config.pvp_locy + Rnd.get(-100, 100), Config.pvp_locz, 0);

					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.EVENT.getId());
					confirm.addString("Let's go to PvpZone?");
					confirm.addZoneName(_position);
					confirm.addTime(45000);
					confirm.addRequesterId(player.getObjectId());
					player.sendPacket(confirm);
				}
			}
		}
		
		cs = null;
	}
	
	public static boolean tvt_register = false;
	
	public static void TvTAnnounce(String text)
	{
		tvt_register = true;
		
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				player.sendPacket(cs);
				
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						tvt_register = false;
					}
				}, 46000);
				
				final boolean bishop = (player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.ELVEN_ELDER);
				
				if (!(player.isOlympiadProtection() || player.getLevel() < TvT.get_minlvl() || player.getLevel() > TvT.get_maxlvl() || player.isCursedWeaponEquipped() || player.isInObserverMode() || player.getFirstLog() || player._inEventCTF || player._inEventTvT || player.isAio() || bishop) && Config.SCREN_MSG)
				{
					SpawnLocation _position = new SpawnLocation(TvT._npcX, TvT._npcY, TvT._npcX, 0);
					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.EVENT.getId());
					confirm.addString("Do you want to register on TvT?");
					confirm.addZoneName(_position);
					confirm.addTime(45000);
					confirm.addRequesterId(player.getObjectId());
					player.sendPacket(confirm);
				}
			}
		}
		
		cs = null;
	}
	
	public static boolean isSummoning = false;
	
	public static void ArenaAnnounce(String text)
	{
		isSummoning = true;
		
		CreatureSay cs = new CreatureSay(0, Config.ANNOUNCE_ID_EVENT, "", "" + text);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				player.sendPacket(cs);
				
				if (Config.TOURNAMENT_EVENT_SUMMON)
				{
					if (!player.isAio() || !player.isAioEterno() || !(player.isInsideZone(ZoneId.TOURNAMENT) || player.isDead() || player._inEventTvT  || player._inEventCTF || player.isInArenaEvent() || player.isInsideZone(ZoneId.BOSS) || player.isInsideZone(ZoneId.RAID) || player.isInStoreMode() || player.isRooted() || player.getKarma() > 0 || player.isInOlympiadMode() || player.isOlympiadProtection() || player.isFestivalParticipant() || player.getFirstLog()))
					{
						
						ThreadPool.schedule(new Runnable()
						{
							@Override
							public void run()
							{
								isSummoning = false;
							}
						}, 31000);
						
						SpawnLocation _position = new SpawnLocation(Config.Tournament_locx + Rnd.get(-100, 100), Config.Tournament_locy + Rnd.get(-100, 100), Config.Tournament_locz, 0);
						
						ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.EVENT.getId());
						confirm.addString("Do you want to go to the Tournament?");
						confirm.addZoneName(_position);
						confirm.addTime(30000);
						confirm.addRequesterId(player.getObjectId());
						player.sendPacket(confirm);
					}
					
				}
			}
		}
		
		cs = null;
	}
}