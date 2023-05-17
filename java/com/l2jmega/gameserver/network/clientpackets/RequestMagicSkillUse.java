package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.ai.CtrlEvent;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.ai.NextAction;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public final class RequestMagicSkillUse extends L2GameClientPacket
{
	private int _magicId;
	protected boolean _ctrlPressed;
	protected boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_magicId = readD(); // Identifier of the used skill
		_ctrlPressed = readD() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = readC() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
		// Get the current player
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		// Get the level of the used skill
		final int level = activeChar.getSkillLevel(_magicId);
		if (level <= 0)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Get the L2Skill template corresponding to the skillID received from the client
		final L2Skill skill = SkillTable.getInstance().getInfo(_magicId, level);
		if (skill == null)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			_log.warning("No skill found with id " + _magicId + " and level " + level + ".");
			return;
		}
		
		// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
		if (skill.getSkillType() == L2SkillType.RECALL && !Config.KARMA_PLAYER_CAN_TELEPORT && activeChar.getKarma() > 0)
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// players mounted on pets cannot use any toggle skills
		if (skill.isToggle() && activeChar.isMounted())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isAttackingNow())
		{
			activeChar.getAI().setNextAction(new NextAction(CtrlEvent.EVT_READY_TO_ACT, CtrlIntention.CAST, new Runnable()
			{
				@Override
				public void run()
				{
					activeChar.useMagic(skill, _ctrlPressed, _shiftPressed);
				}
			}));
		}
		else
			activeChar.useMagic(skill, _ctrlPressed, _shiftPressed);
	}
}