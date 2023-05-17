package com.l2jmega.gameserver.model.zone.type;

import java.util.concurrent.Future;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2CastleZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.Stats;

/**
 * A damage zone
 * @author durgus
 */
public class L2DamageZone extends L2CastleZoneType
{
	private int _hpDps;
	private Future<?> _task;
	
	private int _startTask;
	private int _reuseTask;
	
	private String _target = "Playable"; // default only playable
	
	public L2DamageZone(int id)
	{
		super(id);
		
		_hpDps = 100; // setup default damage
		
		// Setup default start / reuse time
		_startTask = 10;
		_reuseTask = 5000;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("dmgSec"))
			_hpDps = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("initialDelay"))
			_startTask = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("reuse"))
			_reuseTask = Integer.parseInt(value);
		else if (name.equals("targetClass"))
			_target = value;
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected boolean isAffected(Creature character)
	{
		// check obj class
		try
		{
			if (!(Class.forName("com.l2jmega.gameserver.model.actor." + _target).isInstance(character)))
				return false;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (_task == null && _hpDps != 0)
		{
			// Castle traps are active only during siege, or if they're activated.
			if (getCastle() != null && (!isEnabled() || !getCastle().getSiege().isInProgress()))
				return;
			
			synchronized (this)
			{
				if (_task == null)
				{
					_task = ThreadPool.scheduleAtFixedRate(new ApplyDamage(this), _startTask, _reuseTask);
					
					// Message for castle traps.
					if (getCastle() != null)
						getCastle().getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.A_TRAP_DEVICE_HAS_BEEN_TRIPPED), false);
				}
			}
		}
		
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.DANGER_AREA, true);
			character.sendPacket(new EtcStatusUpdate((Player) character));
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (character instanceof Player)
		{
			character.setInsideZone(ZoneId.DANGER_AREA, false);
			if (!character.isInsideZone(ZoneId.DANGER_AREA))
				character.sendPacket(new EtcStatusUpdate((Player) character));
		}
	}
	
	protected int getHpDps()
	{
		return _hpDps;
	}
	
	protected void stopTask()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
	}
	
	class ApplyDamage implements Runnable
	{
		private final L2DamageZone _dmgZone;
		
		ApplyDamage(L2DamageZone zone)
		{
			_dmgZone = zone;
		}
		
		@Override
		public void run()
		{
			// Cancels the task if config has changed, if castle isn't in siege anymore, or if zone isn't enabled.
			if (_dmgZone.getHpDps() <= 0 || (_dmgZone.getCastle() != null && (!_dmgZone.isEnabled() || !_dmgZone.getCastle().getSiege().isInProgress())))
			{
				_dmgZone.stopTask();
				return;
			}
			
			// Cancels the task if characters list is empty.
			if (_dmgZone.getCharactersInside().isEmpty())
			{
				_dmgZone.stopTask();
				return;
			}
			
			// Effect all people inside the zone.
			for (Creature temp : _dmgZone.getCharactersInside())
			{
				if (temp != null && !temp.isDead())
					temp.reduceCurrentHp(_dmgZone.getHpDps() * (1 + (temp.calcStat(Stats.DAMAGE_ZONE_VULN, 0, null, null) / 100)), null, null);
			}
		}
	}
}