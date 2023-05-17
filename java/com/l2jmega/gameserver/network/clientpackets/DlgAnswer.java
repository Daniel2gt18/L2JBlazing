package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminRecallAll;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedEvent;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * @author Dezmond_snz Format: cddd
 */
public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
			activeChar.reviveAnswer(_answer);
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
		{
			if (AdminRecallAll.isAdminSummoning == true && this._answer == 1) {
				activeChar.teleToLocation(AdminRecallAll.x, AdminRecallAll.y, AdminRecallAll.z, 250);
			}else {
				activeChar.teleportAnswer(this._answer, this._requesterId);
			} 
		}
		else if (Announcement.isSummoning == true && this._answer == 1) {
			activeChar.teleToLocation(Config.Tournament_locx, Config.Tournament_locy, Config.Tournament_locz, 125);
		} else if (Announcement.pvp_register == true && _answer == 1) {
			activeChar.teleToLocation(Config.pvp_locx, Config.pvp_locy, Config.pvp_locz, 125);
		}
		else if (_messageId == SystemMessageId.EVENT.getId())
		{
			if (Announcement.tvt_register == true && _answer == 1)
				VoicedEvent.JoinTvT(activeChar);
		}
		else if (_messageId == 1983 && Config.ALLOW_WEDDING)
			activeChar.engageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			activeChar.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			activeChar.activateGate(_answer, 0);
	}
}