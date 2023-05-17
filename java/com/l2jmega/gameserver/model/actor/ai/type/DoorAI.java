package com.l2jmega.gameserver.model.actor.ai.type;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.ai.CtrlEvent;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.instance.SiegeGuard;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.location.SpawnLocation;

public class DoorAI extends CreatureAI
{
	public DoorAI(Door door)
	{
		super(door);
	}
	
	@Override
	protected void onIntentionIdle()
	{
	}
	
	@Override
	protected void onIntentionActive()
	{
	}
	
	@Override
	protected void onIntentionRest()
	{
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{
	}
	
	@Override
	protected void onIntentionCast(L2Skill skill, WorldObject target)
	{
	}
	
	@Override
	protected void onIntentionMoveTo(Location loc)
	{
	}
	
	@Override
	protected void onIntentionFollow(Creature target)
	{
	}
	
	@Override
	protected void onIntentionPickUp(WorldObject item)
	{
	}
	
	@Override
	protected void onIntentionInteract(WorldObject object)
	{
	}
	
	@Override
	protected void onEvtThink()
	{
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker)
	{
		ThreadPool.execute(new onEventAttackedDoorTask((Door) _actor, attacker));
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
	
	@Override
	protected void onEvtStunned(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtSleeping(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtRooted(Creature attacker)
	{
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
	}
	
	@Override
	protected void onEvtArrived()
	{
	}
	
	@Override
	protected void onEvtArrivedBlocked(SpawnLocation loc)
	{
	}
	
	@Override
	protected void onEvtCancel()
	{
	}
	
	@Override
	protected void onEvtDead()
	{
	}
	
	private class onEventAttackedDoorTask implements Runnable
	{
		private final Door _door;
		private final Creature _attacker;
		
		public onEventAttackedDoorTask(Door door, Creature attacker)
		{
			_door = door;
			_attacker = attacker;
		}
		
		@Override
		public void run()
		{
			for (SiegeGuard guard : _door.getKnownType(SiegeGuard.class))
			{
				if (_actor.isInsideRadius(guard, guard.getTemplate().getClanRange(), false, true) && Math.abs(_attacker.getZ() - guard.getZ()) < 200)
					guard.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _attacker, 15);
			}
		}
	}
}