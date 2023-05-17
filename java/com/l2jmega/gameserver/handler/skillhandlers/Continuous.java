package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.instancemanager.DuelManager;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.ShotType;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.ai.CtrlEvent;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.instance.ClanHallManagerNpc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.skills.Formulas;
import com.l2jmega.gameserver.templates.skills.L2EffectType;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class Continuous implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.BUFF,
		L2SkillType.DEBUFF,
		L2SkillType.DOT,
		L2SkillType.MDOT,
		L2SkillType.POISON,
		L2SkillType.BLEED,
		L2SkillType.HOT,
		L2SkillType.CPHOT,
		L2SkillType.MPHOT,
		L2SkillType.FEAR,
		L2SkillType.CONT,
		L2SkillType.WEAKNESS,
		L2SkillType.REFLECT,
		L2SkillType.UNDEAD_DEFENSE,
		L2SkillType.AGGDEBUFF,
		L2SkillType.FUSION
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		final Player player = activeChar.getActingPlayer();
		
		if (skill.getEffectId() != 0)
		{
			L2Skill sk = SkillTable.getInstance().getInfo(skill.getEffectId(), skill.getEffectLvl() == 0 ? 1 : skill.getEffectLvl());
			if (sk != null)
				skill = sk;
		}
		
		final boolean ss = activeChar.isChargedShot(ShotType.SOULSHOT);
		final boolean sps = activeChar.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			Creature target = ((Creature) obj);
			if (Formulas.calcSkillReflect(target, skill) == Formulas.SKILL_REFLECT_SUCCEED)
				target = activeChar;
			
			switch (skill.getSkillType())
			{
				case BUFF:
					// Target under buff immunity.
					if (target.getFirstEffect(L2EffectType.BLOCK_BUFF) != null)
						continue;
					
					final L2Party party = activeChar.getParty();
					if (!(activeChar instanceof Player && target instanceof Player && party != null && party.getPartyMembers().contains(target)))
					{
						// Anti-Buff Protection prevents you from getting buffs by other players
						if (activeChar instanceof Player && target instanceof Player && target != activeChar && target.isBuffProtected() && !skill.isHeroSkill() && (skill.getSkillType() == L2SkillType.BUFF && !(skill.getId() == 1323 || skill.getId() == 1410 || skill.getId() == 1418)))
						{
							((Player) activeChar).sendMessage(target.getName() + " is with the skill Anti-Buff active!");
							continue;
						}
						else if (activeChar instanceof Player && target instanceof Player && target != activeChar && ((Player) target).isOlympiadProtection() && !skill.isHeroSkill() && (skill.getSkillType() == L2SkillType.BUFF && !(skill.getId() == 1323 || skill.getId() == 1410 || skill.getId() == 1418)))
						{
							((Player) activeChar).sendMessage(target.getName() + " is in Olympiad Mode!");
							continue;
						}
					}
					
					// Player holding a cursed weapon can't be buffed and can't buff
					if (!(activeChar instanceof ClanHallManagerNpc) && target != activeChar)
					{
						if (target instanceof Player)
						{
							if (((Player) target).isCursedWeaponEquipped())
								continue;
						}
						else if (player != null && player.isCursedWeaponEquipped())
							continue;
					}
					break;
				
				case HOT:
				case CPHOT:
				case MPHOT:
					if (activeChar.isInvul())
						continue;
					break;
			}
			
			// Target under debuff immunity.
			if (skill.isOffensive() && target.getFirstEffect(L2EffectType.BLOCK_DEBUFF) != null)
				continue;
			
			boolean acted = true;
			byte shld = 0;
			
			if (skill.isOffensive() || skill.isDebuff())
			{
				shld = Formulas.calcShldUse(activeChar, target, skill);
				acted = Formulas.calcSkillSuccess(activeChar, target, skill, shld, bsps);
			}
			
			if (acted)
			{
				if (skill.isToggle())
					target.stopSkillEffects(skill.getId());
				
				// if this is a debuff let the duel manager know about it so the debuff
				// can be removed after the duel (player & target must be in the same duel)
				if (target instanceof Player && ((Player) target).isInDuel() && (skill.getSkillType() == L2SkillType.DEBUFF || skill.getSkillType() == L2SkillType.BUFF) && player != null && player.getDuelId() == ((Player) target).getDuelId())
				{
					DuelManager dm = DuelManager.getInstance();
					for (L2Effect buff : skill.getEffects(activeChar, target, new Env(shld, ss, sps, bsps)))
						if (buff != null)
							dm.onBuff(((Player) target), buff);
				}
				else
					skill.getEffects(activeChar, target, new Env(shld, ss, sps, bsps));
				
				if (skill.getSkillType() == L2SkillType.AGGDEBUFF)
				{
					if (target instanceof Attackable)
						target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, (int) skill.getPower());
					else if (target instanceof Playable)
					{
						if (target.getTarget() == activeChar)
							target.getAI().setIntention(CtrlIntention.ATTACK, activeChar);
						else
							target.setTarget(activeChar);
					}
				}
			}
			else
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
			
			// Possibility of a lethal strike
			Formulas.calcLethalHit(activeChar, target, skill);
		}
		
		if (skill.hasSelfEffects())
		{
			final L2Effect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (!skill.isPotion())
			activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}