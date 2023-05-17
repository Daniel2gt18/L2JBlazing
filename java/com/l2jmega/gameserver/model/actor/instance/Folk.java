package com.l2jmega.gameserver.model.actor.instance;

import java.util.List;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTable.FrequentSkill;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2EnchantSkillData;
import com.l2jmega.gameserver.model.L2EnchantSkillLearn;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.L2SkillLearn;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.status.FolkStatus;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.AcquireSkillList;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ExEnchantSkillList;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.effects.EffectBuff;
import com.l2jmega.gameserver.skills.effects.EffectDebuff;

public class Folk extends Npc
{
	public Folk(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public FolkStatus getStatus()
	{
		return (FolkStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new FolkStatus(this));
	}
	
	@Override
	public void addEffect(L2Effect newEffect)
	{
		if (newEffect instanceof EffectDebuff || newEffect instanceof EffectBuff)
			super.addEffect(newEffect);
		else if (newEffect != null)
			newEffect.stopEffectTask();
	}
	
	/**
	 * This method displays SkillList to the player.
	 * @param player The player who requested the method.
	 * @param npc The L2Npc linked to the request.
	 * @param classId The classId asked. Used to sort available skill list.
	 */
	public static void showSkillList(Player player, Npc npc, ClassId classId)
	{
		if (!npc.getTemplate().canTeach(classId))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/trainer/" + npc.getTemplate().getNpcId() + "-noskills.htm");
			player.sendPacket(html);
			return;
		}
		
		AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.SkillType.Usual);
		boolean empty = true;
		
		for (L2SkillLearn sl : SkillTreeTable.getInstance().getAvailableSkills(player, classId))
		{
			L2Skill sk = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if (sk == null)
				continue;
			
			asl.addSkill(sl.getId(), sl.getLevel(), sl.getLevel(), sl.getSpCost(), 0);
			empty = false;
		}
		
		if (empty)
		{
			int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player, classId);
			
			if (minlevel > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(minlevel));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * This method displays EnchantSkillList to the player.
	 * @param player The player who requested the method.
	 * @param npc The L2Npc linked to the request.
	 * @param classId The classId asked. Used to sort available enchant skill list.
	 */
	public static void showEnchantSkillList(Player player, Npc npc, ClassId classId)
	{
		if (!npc.getTemplate().canTeach(classId))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/trainer/" + npc.getTemplate().getNpcId() + "-noskills.htm");
			player.sendPacket(html);
			return;
		}
		
		if (player.getClassId().level() < 3)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml("<html><body> You must have 3rd class change quest completed.</body></html>");
			player.sendPacket(html);
			return;
		}
		
		ExEnchantSkillList esl = new ExEnchantSkillList();
		boolean empty = true;
		
		List<L2EnchantSkillLearn> esll = SkillTreeTable.getInstance().getAvailableEnchantSkills(player);
		for (L2EnchantSkillLearn skill : esll)
		{
			L2Skill sk = SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel());
			if (sk == null)
				continue;
			
			L2EnchantSkillData data = SkillTreeTable.getInstance().getEnchantSkillData(skill.getEnchant());
			if (data == null)
				continue;
			
			esl.addSkill(skill.getId(), skill.getLevel(), data.getCostSp(), data.getCostExp());
			empty = false;
		}
		
		if (empty)
		{
			player.sendPacket(SystemMessageId.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT);
			
			if (player.getLevel() < 74)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(74));
			else
				player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(esl);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void giveBlessingSupport(Player player)
	{
		if (player == null)
			return;
		
		// Select the player
		setTarget(player);
		
		// If the player is too high level, display a message and return
		if (player.getLevel() > 39 || player.getClassId().level() >= 2)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Newbie Guide:<br>I'm sorry, but you are not eligible to receive the protection blessing.<br1>It can only be bestowed on <font color=\"LEVEL\">characters below level 39 who have not made a seccond transfer.</font></body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		doCast(FrequentSkill.BLESSING_OF_PROTECTION.getSkill());
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("SkillList"))
		{
			player.setSkillLearningClassId(player.getClassId());
			showSkillList(player, this, player.getClassId());
		}
		else if (command.startsWith("EnchantSkillList"))
			showEnchantSkillList(player, this, player.getClassId());
		else if (command.startsWith("GiveBlessing"))
			giveBlessingSupport(player);
		else
			super.onBypassFeedback(player, command);
	}
}