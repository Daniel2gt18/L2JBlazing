package com.l2jmega.gameserver.model.actor.instance;

import java.util.List;

import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.geoengine.GeoEngine;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.entity.Siege.SiegeSide;
import com.l2jmega.gameserver.model.zone.L2CastleZoneType;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.MoveToPawn;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public class FlameTower extends Npc
{
	private int _upgradeLevel;
	private List<Integer> _zoneList;
	
	public FlameTower(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAttackable()
	{
		// Attackable during siege by attacker only
		return getCastle() != null && getCastle().getSiege().isInProgress();
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		// Attackable during siege by attacker only
		return attacker instanceof Player && getCastle() != null && getCastle().getSiege().isInProgress() && getCastle().getSiege().checkSide(((Player) attacker).getClan(), SiegeSide.ATTACKER);
	}
	
	@Override
	public void onForcedAttack(Player player)
	{
		onAction(player);
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (isAutoAttackable(player) && Math.abs(player.getZ() - getZ()) < 100 && GeoEngine.getInstance().canSeeTarget(player, this))
			{
				// Notify the Player AI with INTERACT
				player.getAI().setIntention(CtrlIntention.ATTACK, this);
			}
			else
			{
				// Rotate the player to face the instance
				player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
				
				// Send ActionFailed to the player in order to avoid he stucks
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		enableZones(false);
		
		if (getCastle() != null)
		{
			// Message occurs only if the trap was triggered first.
			if (_zoneList != null && _upgradeLevel != 0)
				getCastle().getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.A_TRAP_DEVICE_HAS_BEEN_STOPPED), false);
			
			// Spawn a little version of it. This version is a simple NPC, cleaned on siege end.
			try
			{
				final L2Spawn spawn = new L2Spawn(NpcTable.getInstance().getTemplate(13005));
				spawn.setLoc(getPosition());
				
				final Npc tower = spawn.doSpawn(false);
				tower.setCastle(getCastle());
				
				getCastle().getSiege().getDestroyedTowers().add(tower);
			}
			catch (Exception e)
			{
				_log.warning(getClass().getName() + ": Cannot spawn control tower! " + e);
			}
		}
		
		return super.doDie(killer);
	}
	
	@Override
	public void deleteMe()
	{
		enableZones(false);
		super.deleteMe();
	}
	
	public final void enableZones(boolean state)
	{
		if (_zoneList != null && _upgradeLevel != 0)
		{
			final int maxIndex = _upgradeLevel * 2;
			for (int i = 0; i < maxIndex; i++)
			{
				final L2ZoneType zone = ZoneManager.getInstance().getZoneById(_zoneList.get(i));
				if (zone != null && zone instanceof L2CastleZoneType)
					((L2CastleZoneType) zone).setEnabled(state);
			}
		}
	}
	
	public final void setUpgradeLevel(int level)
	{
		_upgradeLevel = level;
	}
	
	public final void setZoneList(List<Integer> list)
	{
		_zoneList = list;
		enableZones(true);
	}
}