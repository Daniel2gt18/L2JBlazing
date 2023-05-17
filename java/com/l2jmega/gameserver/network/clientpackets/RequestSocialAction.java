package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;

public class RequestSocialAction extends L2GameClientPacket
{
	private int _actionId;
	
	@Override
	protected void readImpl()
	{
		_actionId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.SOCIAL))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		if (_actionId < 2 || _actionId > 13)
			return;
		
		if (activeChar.isInStoreMode() || activeChar.getActiveRequester() != null || activeChar.isAlikeDead() || activeChar.getAI().getIntention() != CtrlIntention.IDLE)
			return;
		
		activeChar.broadcastPacket(new SocialAction(activeChar, _actionId));
	}
}