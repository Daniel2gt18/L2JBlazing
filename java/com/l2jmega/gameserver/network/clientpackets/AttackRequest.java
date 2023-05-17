package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Chest;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.instance.GrandBoss;
import com.l2jmega.gameserver.model.actor.instance.Guard;
import com.l2jmega.gameserver.model.actor.instance.Monster;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.RaidBoss;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;

public final class AttackRequest extends L2GameClientPacket
{
	// cddddc
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX, _originY, _originZ;
	@SuppressWarnings("unused")
	private int _attackId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		_attackId = readC(); // 0 for simple click 1 for shift-click
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.isInObserverMode())
		{
			activeChar.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (TvT.is_sitForced() && activeChar._inEventTvT || CTF.is_sitForced() && activeChar._inEventCTF)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// avoid using expensive operations if not needed
		final WorldObject target;
		if (activeChar.getTargetId() == _objectId)
			target = activeChar.getTarget();
		else
			target = World.getInstance().getObject(_objectId);
		
		if (target == null)
			return;
		
		boolean target_alloewd = (!(target instanceof Guard) && !(target instanceof Monster) && !(target instanceof RaidBoss) && !(target instanceof GrandBoss) && !(target instanceof Door) && !(target instanceof Chest));
		
		if (!(target instanceof Player || target instanceof Pet || target instanceof Summon) && target_alloewd && Config.DISABLE_ATTACK_NPC_TYPE)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (target instanceof Player && !activeChar.isGM() && activeChar.getAppearance().getInvisible() && !((Player) target).getAppearance().getInvisible())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// No attacks to same team in Event
		if (TvT.is_started())
		{
			if (target instanceof Player)
			{
				if (activeChar._inEventTvT && !((Player) target)._inEventTvT)
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (TvT.is_started() && !activeChar._inEventTvT && ((Player) target)._inEventTvT)
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if ((activeChar._inEventTvT && ((Player) target)._inEventTvT) && activeChar._teamNameTvT.equals(((Player) target)._teamNameTvT))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			else if (target instanceof Summon)
			{
				if ((activeChar._inEventTvT && ((Summon) target).getOwner()._inEventTvT) && activeChar._teamNameTvT.equals(((Summon) target).getOwner()._teamNameTvT))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// No attacks to same team in Event
		if (CTF.is_started())
		{
			if (target instanceof Player)
			{
				if (activeChar._inEventCTF && !((Player) target)._inEventCTF)
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (CTF.is_started() && !activeChar._inEventCTF && ((Player) target)._inEventCTF)
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if ((activeChar._inEventCTF && ((Player) target)._inEventCTF) && activeChar._teamNameCTF.equals(((Player) target)._teamNameCTF))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			else if (target instanceof Summon)
			{
				if ((activeChar._inEventCTF && ((Summon) target).getOwner()._inEventCTF) && activeChar._teamNameCTF.equals(((Summon) target).getOwner()._teamNameCTF))
				{
					activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		if (activeChar.getTarget() != target)
			target.onAction(activeChar);
		else
		{
			if ((target.getObjectId() != activeChar.getObjectId()) && !activeChar.isInStoreMode() && activeChar.getActiveRequester() == null)
				target.onForcedAttack(activeChar);
			else
				sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}