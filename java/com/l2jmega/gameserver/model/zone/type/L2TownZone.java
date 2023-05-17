package com.l2jmega.gameserver.model.zone.type;

import com.l2jmega.Config;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.L2SpawnZone;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.EtcStatusUpdate;

public class L2TownZone extends L2SpawnZone
{
	private int _townId;
	private int _castleId;
	private boolean _isPeaceZone;
	
	public L2TownZone(int id)
	{
		super(id);
		
		// Default peace zone
		_isPeaceZone = true;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("townId"))
			_townId = Integer.parseInt(value);
		else if (name.equals("castleId"))
			_castleId = Integer.parseInt(value);
		else if (name.equals("isPeaceZone"))
			_isPeaceZone = Boolean.parseBoolean(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		if (character instanceof Player)
		{
			final Player player = ((Player) character);

			if (player.getMountType() == 2 && Config.WYVERN_PROTECTION)
			{
				player.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
				player.enteredNoLanding(5);
			}
			
			
		      if (player.isAio() || AioManager.getInstance().hasAioPrivileges(player.getObjectId())) {
		          player.sendPacket(new EtcStatusUpdate(player));
		          player.sendSkillList();
		          player.broadcastUserInfo();
		        } 

			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if (((Player) character).getSiegeState() != 0 && Config.ZONE_TOWN == 1)
				return;
		}
		
		if (_isPeaceZone && Config.ZONE_TOWN != 2)
			character.setInsideZone(ZoneId.PEACE, true);
		
		character.setInsideZone(ZoneId.TOWN, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		if (_isPeaceZone)
			character.setInsideZone(ZoneId.PEACE, false);
		
		character.setInsideZone(ZoneId.TOWN, false);
		
		/*  if (character instanceof Player) {
	    	Player player = (Player)character;
	    	
	       if (player.isAio() || AioManager.getInstance().hasAioPrivileges(player.getObjectId())) {
	          ThreadPool.schedule(new AioProtection(player), 7000L);
	          player.sendPacket(new EtcStatusUpdate(player));
	          player.sendSkillList();
	          player.broadcastUserInfo();
	        } 
	       
	      }  */
	}
	
	@Override
	public void onDieInside(Creature character)
	{
	}
	
	@Override
	public void onReviveInside(Creature character)
	{
	}
	
	/**
	 * @return the zone town id (if any)
	 */
	public int getTownId()
	{
		return _townId;
	}
	
	/**
	 * @return the castle id (used to retrieve taxes).
	 */
	public final int getCastleId()
	{
		return _castleId;
	}
	
	public final boolean isPeaceZone()
	{
		return _isPeaceZone;
	}
}