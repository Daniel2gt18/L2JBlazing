package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.commons.math.MathUtil;

import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

/**
 * @authors BiTi, Sami
 */
public class SummonFriend implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.SUMMON_FRIEND
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final Player player = (Player) activeChar;
		
		// Check player status.
		if (!Player.checkSummonerStatus(player))
			return;
		
		if (player.isArenaProtection())
		{
			player.sendMessage("You cannot use this skill in Tournament Event.");
			return;
		}
		
		if ((TvT.is_started() && player._inEventTvT) || (CTF.is_started() && player._inEventCTF))
		{
			player.sendMessage("You cannot use this skill in Event.");
			return;
		}
		
		for (WorldObject obj : targets)
		{
			// The target must be a player.
			if (!(obj instanceof Player))
				continue;
			
			// Can't summon yourself.
			final Player target = ((Player) obj);
			if (activeChar == target)
				continue;
			
			// Check target status.
			if (!Player.checkSummonTargetStatus(target, player))
				continue;
			
			// Check target distance.
			if (MathUtil.checkIfInRange(50, activeChar, target, false))
				continue;
			
			// Check target teleport request status.
			if (!target.teleportRequest(player, skill))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_SUMMONED).addCharName(target));
				continue;
			}
			
			// Send a request for Summon Friend skill.
			if (skill.getId() == 1403)
			{
				final ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
				confirm.addCharName(player);
				confirm.addZoneName(activeChar.getPosition());
				confirm.addTime(30000);
				confirm.addRequesterId(player.getObjectId());
				target.sendPacket(confirm);
			}
			else
			{
				Player.teleToTarget(target, player, skill);
				target.teleportRequest(null, null);
			}
		}
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}