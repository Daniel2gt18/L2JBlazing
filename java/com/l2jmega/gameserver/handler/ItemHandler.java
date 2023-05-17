package com.l2jmega.gameserver.handler;

import com.l2jmega.gameserver.handler.itemhandlers.BeastSoulShot;
import com.l2jmega.gameserver.handler.itemhandlers.BeastSpice;
import com.l2jmega.gameserver.handler.itemhandlers.BeastSpiritShot;
import com.l2jmega.gameserver.handler.itemhandlers.BlessedSpiritShot;
import com.l2jmega.gameserver.handler.itemhandlers.Book;
import com.l2jmega.gameserver.handler.itemhandlers.Calculator;
import com.l2jmega.gameserver.handler.itemhandlers.Elixir;
import com.l2jmega.gameserver.handler.itemhandlers.EnchantScrolls;
import com.l2jmega.gameserver.handler.itemhandlers.FishShots;
import com.l2jmega.gameserver.handler.itemhandlers.Harvester;
import com.l2jmega.gameserver.handler.itemhandlers.ItemSkills;
import com.l2jmega.gameserver.handler.itemhandlers.Keys;
import com.l2jmega.gameserver.handler.itemhandlers.Maps;
import com.l2jmega.gameserver.handler.itemhandlers.MercTicket;
import com.l2jmega.gameserver.handler.itemhandlers.PaganKeys;
import com.l2jmega.gameserver.handler.itemhandlers.PetFood;
import com.l2jmega.gameserver.handler.itemhandlers.Recipes;
import com.l2jmega.gameserver.handler.itemhandlers.RollingDice;
import com.l2jmega.gameserver.handler.itemhandlers.ScrollOfResurrection;
import com.l2jmega.gameserver.handler.itemhandlers.SeedHandler;
import com.l2jmega.gameserver.handler.itemhandlers.SevenSignsRecord;
import com.l2jmega.gameserver.handler.itemhandlers.SoulCrystals;
import com.l2jmega.gameserver.handler.itemhandlers.SoulShots;
import com.l2jmega.gameserver.handler.itemhandlers.SpecialXMas;
import com.l2jmega.gameserver.handler.itemhandlers.SpiritShot;
import com.l2jmega.gameserver.handler.itemhandlers.SummonItems;
import com.l2jmega.gameserver.handler.itemhandlers.aio.Aio15days;
import com.l2jmega.gameserver.handler.itemhandlers.aio.Aio24h;
import com.l2jmega.gameserver.handler.itemhandlers.aio.Aio30days;
import com.l2jmega.gameserver.handler.itemhandlers.aio.Aio7days;
import com.l2jmega.gameserver.handler.itemhandlers.aio.AioEterno;
import com.l2jmega.gameserver.handler.itemhandlers.clan.ClanLevel6;
import com.l2jmega.gameserver.handler.itemhandlers.clan.ClanLevel7;
import com.l2jmega.gameserver.handler.itemhandlers.clan.ClanLevel8;
import com.l2jmega.gameserver.handler.itemhandlers.clan.ClanReputation;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillAegis;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillAgility;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillClarity;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillCyclonicResistance;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillEmpowerment;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillEssence;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillFortitude;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillFreedom;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillGuidance;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillImperium;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillLifeblood;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillLuck;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillMagicProtection;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillMagmaticResistance;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillMarch;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillMight;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillMorale;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillShieldBoost;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillSpirituality;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillVigilance;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillVitality;
import com.l2jmega.gameserver.handler.itemhandlers.clan.SkillWithstandAttack;
import com.l2jmega.gameserver.handler.itemhandlers.custom.AllyNameChange;
import com.l2jmega.gameserver.handler.itemhandlers.custom.ClanFull;
import com.l2jmega.gameserver.handler.itemhandlers.custom.ClanNameChange;
import com.l2jmega.gameserver.handler.itemhandlers.custom.ClassItem;
import com.l2jmega.gameserver.handler.itemhandlers.custom.DeletePk;
import com.l2jmega.gameserver.handler.itemhandlers.custom.InfinityStone;
import com.l2jmega.gameserver.handler.itemhandlers.custom.LevelCoin;
import com.l2jmega.gameserver.handler.itemhandlers.custom.LuckBox;
import com.l2jmega.gameserver.handler.itemhandlers.custom.NameChange;
import com.l2jmega.gameserver.handler.itemhandlers.custom.NoblesItem;
import com.l2jmega.gameserver.handler.itemhandlers.custom.SexCoin;
import com.l2jmega.gameserver.handler.itemhandlers.custom.Skins;
import com.l2jmega.gameserver.handler.itemhandlers.hero.Hero15days;
import com.l2jmega.gameserver.handler.itemhandlers.hero.Hero24h;
import com.l2jmega.gameserver.handler.itemhandlers.hero.Hero30days;
import com.l2jmega.gameserver.handler.itemhandlers.hero.Hero7days;
import com.l2jmega.gameserver.handler.itemhandlers.hero.HeroEterno;
import com.l2jmega.gameserver.handler.itemhandlers.vip.Vip15days;
import com.l2jmega.gameserver.handler.itemhandlers.vip.Vip24h;
import com.l2jmega.gameserver.handler.itemhandlers.vip.Vip30days;
import com.l2jmega.gameserver.handler.itemhandlers.vip.Vip7days;
import com.l2jmega.gameserver.handler.itemhandlers.vip.VipEterno;
import com.l2jmega.gameserver.model.item.kind.EtcItem;

import java.util.HashMap;
import java.util.Map;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _datatable = new HashMap<>();
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected ItemHandler()
	{

		registerItemHandler(new BeastSoulShot());
		registerItemHandler(new BeastSpice());
		registerItemHandler(new BeastSpiritShot());
		registerItemHandler(new BlessedSpiritShot());
		registerItemHandler(new Book());
		registerItemHandler(new Calculator());
		registerItemHandler(new Elixir());
		registerItemHandler(new EnchantScrolls());
		registerItemHandler(new FishShots());
		registerItemHandler(new Harvester());
		registerItemHandler(new ItemSkills());
		registerItemHandler(new Keys());
		registerItemHandler(new Maps());
		registerItemHandler(new MercTicket());
		registerItemHandler(new PaganKeys());
		registerItemHandler(new PetFood());
		registerItemHandler(new Recipes());
		registerItemHandler(new RollingDice());
		registerItemHandler(new ScrollOfResurrection());
		registerItemHandler(new SeedHandler());
		registerItemHandler(new SevenSignsRecord());
		registerItemHandler(new SoulShots());
		registerItemHandler(new SpecialXMas());
		registerItemHandler(new SoulCrystals());
		registerItemHandler(new SpiritShot());
		registerItemHandler(new SummonItems());
		// custom
		registerItemHandler(new AllyNameChange());
		registerItemHandler(new ClanNameChange());
	    registerItemHandler(new Aio24h());
	    registerItemHandler(new Aio7days());
	    registerItemHandler(new Aio15days());
	    registerItemHandler(new Aio30days());
	    registerItemHandler(new AioEterno());
		registerItemHandler(new LuckBox());
		registerItemHandler(new ClanFull());
		registerItemHandler(new NoblesItem());
		registerItemHandler(new Vip24h());
		registerItemHandler(new Vip7days());
		registerItemHandler(new Vip15days());
		registerItemHandler(new Vip30days());
		registerItemHandler(new VipEterno());
		registerItemHandler(new Hero24h());
		registerItemHandler(new Hero7days());
		registerItemHandler(new Hero15days());
		registerItemHandler(new Hero30days());
		registerItemHandler(new HeroEterno());
		registerItemHandler(new ClassItem());
		registerItemHandler(new NameChange());
		registerItemHandler(new SexCoin());
		registerItemHandler(new LevelCoin());
		registerItemHandler(new Skins());
		registerItemHandler(new DeletePk());
		registerItemHandler(new InfinityStone());
        //Clan Items
		registerItemHandler(new ClanLevel6());
		registerItemHandler(new ClanLevel7());
		registerItemHandler(new ClanLevel8());
		registerItemHandler(new ClanReputation());
		registerItemHandler(new SkillAegis());
		registerItemHandler(new SkillAgility());
		registerItemHandler(new SkillClarity());
		registerItemHandler(new SkillCyclonicResistance());
		registerItemHandler(new SkillEmpowerment());
		registerItemHandler(new SkillEssence());
		registerItemHandler(new SkillFortitude());
		registerItemHandler(new SkillFreedom());
		registerItemHandler(new SkillGuidance());
		registerItemHandler(new SkillImperium());
		registerItemHandler(new SkillLifeblood());
		registerItemHandler(new SkillLuck());
		registerItemHandler(new SkillMagicProtection());
		registerItemHandler(new SkillMagmaticResistance());
		registerItemHandler(new SkillMarch());
		registerItemHandler(new SkillMight());
		registerItemHandler(new SkillMorale());
		registerItemHandler(new SkillShieldBoost());
		registerItemHandler(new SkillSpirituality());
		registerItemHandler(new SkillVigilance());
		registerItemHandler(new SkillVitality());
		registerItemHandler(new SkillWithstandAttack());
	
	}
	
	public void registerItemHandler(IItemHandler handler)
	{
		_datatable.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getItemHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _datatable.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler _instance = new ItemHandler();
	}
}