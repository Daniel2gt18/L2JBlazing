package com.l2jmega.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.gameserver.handler.skillhandlers.BalanceLife;
import com.l2jmega.gameserver.handler.skillhandlers.Blow;
import com.l2jmega.gameserver.handler.skillhandlers.Cancel;
import com.l2jmega.gameserver.handler.skillhandlers.CombatPointHeal;
import com.l2jmega.gameserver.handler.skillhandlers.Continuous;
import com.l2jmega.gameserver.handler.skillhandlers.CpDamPercent;
import com.l2jmega.gameserver.handler.skillhandlers.Craft;
import com.l2jmega.gameserver.handler.skillhandlers.Disablers;
import com.l2jmega.gameserver.handler.skillhandlers.DrainSoul;
import com.l2jmega.gameserver.handler.skillhandlers.Dummy;
import com.l2jmega.gameserver.handler.skillhandlers.Extractable;
import com.l2jmega.gameserver.handler.skillhandlers.Fishing;
import com.l2jmega.gameserver.handler.skillhandlers.FishingSkill;
import com.l2jmega.gameserver.handler.skillhandlers.GetPlayer;
import com.l2jmega.gameserver.handler.skillhandlers.GiveSp;
import com.l2jmega.gameserver.handler.skillhandlers.Harvest;
import com.l2jmega.gameserver.handler.skillhandlers.Heal;
import com.l2jmega.gameserver.handler.skillhandlers.HealPercent;
import com.l2jmega.gameserver.handler.skillhandlers.InstantJump;
import com.l2jmega.gameserver.handler.skillhandlers.ManaHeal;
import com.l2jmega.gameserver.handler.skillhandlers.Manadam;
import com.l2jmega.gameserver.handler.skillhandlers.Mdam;
import com.l2jmega.gameserver.handler.skillhandlers.Pdam;
import com.l2jmega.gameserver.handler.skillhandlers.Resurrect;
import com.l2jmega.gameserver.handler.skillhandlers.Sow;
import com.l2jmega.gameserver.handler.skillhandlers.Spoil;
import com.l2jmega.gameserver.handler.skillhandlers.StrSiegeAssault;
import com.l2jmega.gameserver.handler.skillhandlers.SummonFriend;
import com.l2jmega.gameserver.handler.skillhandlers.Sweep;
import com.l2jmega.gameserver.handler.skillhandlers.TakeCastle;
import com.l2jmega.gameserver.handler.skillhandlers.Unlock;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class SkillHandler
{
	private final Map<Integer, ISkillHandler> _datatable = new HashMap<>();
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected SkillHandler()
	{
		registerSkillHandler(new BalanceLife());
		registerSkillHandler(new Blow());
		registerSkillHandler(new Cancel());
		registerSkillHandler(new CombatPointHeal());
		registerSkillHandler(new Continuous());
		registerSkillHandler(new CpDamPercent());
		registerSkillHandler(new Craft());
		registerSkillHandler(new Disablers());
		registerSkillHandler(new DrainSoul());
		registerSkillHandler(new Dummy());
		registerSkillHandler(new Extractable());
		registerSkillHandler(new Fishing());
		registerSkillHandler(new FishingSkill());
		registerSkillHandler(new GetPlayer());
		registerSkillHandler(new GiveSp());
		registerSkillHandler(new Harvest());
		registerSkillHandler(new Heal());
		registerSkillHandler(new HealPercent());
		registerSkillHandler(new InstantJump());
		registerSkillHandler(new Manadam());
		registerSkillHandler(new ManaHeal());
		registerSkillHandler(new Mdam());
		registerSkillHandler(new Pdam());
		registerSkillHandler(new Resurrect());
		registerSkillHandler(new Sow());
		registerSkillHandler(new Spoil());
		registerSkillHandler(new StrSiegeAssault());
		registerSkillHandler(new SummonFriend());
		registerSkillHandler(new Sweep());
		registerSkillHandler(new TakeCastle());
		registerSkillHandler(new Unlock());
	}
	
	public void registerSkillHandler(ISkillHandler handler)
	{
		for (L2SkillType t : handler.getSkillIds())
			_datatable.put(t.ordinal(), handler);
	}
	
	public ISkillHandler getSkillHandler(L2SkillType skillType)
	{
		return _datatable.get(skillType.ordinal());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler _instance = new SkillHandler();
	}
}