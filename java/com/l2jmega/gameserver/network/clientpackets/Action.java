package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Folk;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;

public final class Action extends L2GameClientPacket
{
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX, _originY, _originZ;
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		_actionId = readC();
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
		
		if (activeChar.getActiveRequester() != null || activeChar.isOutOfControl())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final WorldObject obj = (activeChar.getTargetId() == _objectId) ? activeChar.getTarget() : World.getInstance().getObject(_objectId);
		
		// avoid using expensive operations if not needed
		final WorldObject target;
		if (activeChar.getTargetId() == _objectId)
			target = activeChar.getTarget();
		else
			target = World.getInstance().getObject(_objectId);
		
		if (TvT.is_sitForced() && target instanceof Player && activeChar._inEventTvT && ((Player) target)._inEventTvT && !activeChar._teamNameTvT.equals(((Player) target)._teamNameTvT))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (TvT.is_started() && target instanceof Player && !activeChar._inEventTvT && ((Player) target)._inEventTvT)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (TvT.is_started() && target instanceof Player && activeChar._inEventTvT && !((Player) target)._inEventTvT)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (CTF.is_sitForced() && target instanceof Player && activeChar._inEventCTF && ((Player) target)._inEventCTF && !activeChar._teamNameCTF.equals(((Player) target)._teamNameCTF))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (CTF.is_started() && target instanceof Player && activeChar._inEventCTF && !((Player) target)._inEventCTF)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (CTF.is_started() && target instanceof Player && !activeChar._inEventCTF && ((Player) target)._inEventCTF)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((CTF.is_started() || CTF.is_sitForced()) && target instanceof Folk && activeChar._inEventCTF && ((Npc) target)._isCTF_throneSpawn)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (target instanceof Player && !activeChar.isGM() && activeChar.getAppearance().getInvisible() && !((Player) target).getAppearance().getInvisible())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (obj == null)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		switch (_actionId)
		{
			case 0:
				obj.onAction(activeChar);
				break;
			
			case 1:
				obj.onActionShift(activeChar);
				break;
			
			default:
				// Invalid action detected (probably client cheating), log this
				_log.warning(activeChar.getName() + " requested invalid action: " + _actionId);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				break;
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}