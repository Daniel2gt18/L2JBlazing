package com.l2jmega.gameserver.skills;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.l2jmega.commons.math.MathUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.ClanHallManager;
import com.l2jmega.gameserver.instancemanager.OlyClassDamageManager;
import com.l2jmega.gameserver.instancemanager.SevenSigns.CabalType;
import com.l2jmega.gameserver.instancemanager.SevenSignsFestival;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Cubic;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.instance.GrandBoss;
import com.l2jmega.gameserver.model.actor.instance.Monster;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.RaidBoss;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.base.ClassType;
import com.l2jmega.gameserver.model.entity.ClanHall;
import com.l2jmega.gameserver.model.entity.Siege;
import com.l2jmega.gameserver.model.entity.Siege.SiegeSide;
import com.l2jmega.gameserver.model.item.kind.Armor;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.model.item.type.WeaponType;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.model.zone.type.L2MotherTreeZone;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.skills.effects.EffectTemplate;
import com.l2jmega.gameserver.taskmanager.GameTimeTaskManager;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public final class Formulas
{
	protected static final Logger _log = Logger.getLogger(Formulas.class.getName());
	
	private static final int HP_REGENERATE_PERIOD = 3000; // 3 secs
	
	public static final byte SHIELD_DEFENSE_FAILED = 0; // no shield defense
	public static final byte SHIELD_DEFENSE_SUCCEED = 1; // normal shield defense
	public static final byte SHIELD_DEFENSE_PERFECT_BLOCK = 2; // perfect block
	
	public static final byte SKILL_REFLECT_FAILED = 0; // no reflect
	public static final byte SKILL_REFLECT_SUCCEED = 1; // normal reflect, some damage reflected some other not
	public static final byte SKILL_REFLECT_VENGEANCE = 2; // 100% of the damage affect both
	
	private static final byte MELEE_ATTACK_RANGE = 40;
	
	public static final int MAX_STAT_VALUE = 100;
	
	private static final double[] STR_COMPUTE = new double[]
		{
		1.036,
		34.845
		};
	private static final double[] INT_COMPUTE = new double[]
		{
		1.020,
		31.375
		};
	private static final double[] DEX_COMPUTE = new double[]
		{
		1.009,
		19.360
		};
	private static final double[] WIT_COMPUTE = new double[]
		{
		1.050,
		20.000
		};
	private static final double[] CON_COMPUTE = new double[]
		{
		1.030,
		27.632
		};
	private static final double[] MEN_COMPUTE = new double[]
		{
		1.010,
		-0.060
		};
	
	public static final double[] WIT_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] MEN_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] INT_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] STR_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] DEX_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] CON_BONUS = new double[MAX_STAT_VALUE];
	
	public static final double[] BASE_EVASION_ACCURACY = new double[MAX_STAT_VALUE];
	
	protected static final double[] SQRT_MEN_BONUS = new double[MAX_STAT_VALUE];
	protected static final double[] SQRT_CON_BONUS = new double[MAX_STAT_VALUE];
	
	static
	{
		for (int i = 0; i < STR_BONUS.length; i++)
			STR_BONUS[i] = Math.floor(Math.pow(STR_COMPUTE[0], i - STR_COMPUTE[1]) * 100 + .5d) / 100;
		for (int i = 0; i < INT_BONUS.length; i++)
			INT_BONUS[i] = Math.floor(Math.pow(INT_COMPUTE[0], i - INT_COMPUTE[1]) * 100 + .5d) / 100;
		for (int i = 0; i < DEX_BONUS.length; i++)
			DEX_BONUS[i] = Math.floor(Math.pow(DEX_COMPUTE[0], i - DEX_COMPUTE[1]) * 100 + .5d) / 100;
		for (int i = 0; i < WIT_BONUS.length; i++)
			WIT_BONUS[i] = Math.floor(Math.pow(WIT_COMPUTE[0], i - WIT_COMPUTE[1]) * 100 + .5d) / 100;
		for (int i = 0; i < CON_BONUS.length; i++)
			CON_BONUS[i] = Math.floor(Math.pow(CON_COMPUTE[0], i - CON_COMPUTE[1]) * 100 + .5d) / 100;
		for (int i = 0; i < MEN_BONUS.length; i++)
			MEN_BONUS[i] = Math.floor(Math.pow(MEN_COMPUTE[0], i - MEN_COMPUTE[1]) * 100 + .5d) / 100;
		
		for (int i = 0; i < BASE_EVASION_ACCURACY.length; i++)
			BASE_EVASION_ACCURACY[i] = Math.sqrt(i) * 6;
		
		// Precompute square root values
		for (int i = 0; i < SQRT_CON_BONUS.length; i++)
			SQRT_CON_BONUS[i] = Math.sqrt(CON_BONUS[i]);
		for (int i = 0; i < SQRT_MEN_BONUS.length; i++)
			SQRT_MEN_BONUS[i] = Math.sqrt(MEN_BONUS[i]);
	}
	
	private static final double[] karmaMods =
	{
		0,
		0.772184315,
		2.069377971,
		2.315085083,
		2.467800843,
		2.514219611,
		2.510075822,
		2.532083418,
		2.473028945,
		2.377178509,
		2.285526643,
		2.654635163,
		2.963434737,
		3.266100461,
		3.561112664,
		3.847320291,
		4.123878064,
		4.390194136,
		4.645886341,
		4.890745518,
		5.124704707,
		6.97914069,
		7.270620642,
		7.548951721,
		7.81438302,
		8.067235867,
		8.307889422,
		8.536768399,
		8.754332624,
		8.961068152,
		9.157479758,
		11.41901707,
		11.64989746,
		11.87007991,
		12.08015809,
		12.28072687,
		12.47237891,
		12.65570177,
		12.83127553,
		12.99967093,
		13.16144786,
		15.6563607,
		15.84513182,
		16.02782135,
		16.20501182,
		16.37727218,
		16.54515749,
		16.70920885,
		16.86995336,
		17.02790439,
		17.18356182,
		19.85792061,
		20.04235517,
		20.22556446,
		20.40806338,
		20.59035551,
		20.77293378,
		20.95628115,
		21.1408714,
		21.3271699,
		21.51563446,
		24.3895427,
		24.61486587,
		24.84389213,
		25.07711247,
		25.31501442,
		25.55808296,
		25.80680152,
		26.06165297,
		26.32312062,
		26.59168923,
		26.86784604,
		27.15208178,
		27.44489172,
		27.74677676,
		28.05824444,
		28.37981005,
		28.71199773,
		29.05534154,
		29.41038662,
		29.77769028
	};
	
	/**
	 * @param cha The character to make checks on.
	 * @return the period between 2 regenerations task (3s for Creature, 5 min for L2DoorInstance).
	 */
	public static int getRegeneratePeriod(Creature cha)
	{
		if (cha instanceof Door)
			return HP_REGENERATE_PERIOD * 100; // 5 mins
		
		return HP_REGENERATE_PERIOD; // 3s
	}
	
	/**
	 * @param cha The character to make checks on.
	 * @return the HP regen rate (base + modifiers).
	 */
	public static final double calcHpRegen(Creature cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double hpRegenMultiplier = cha.isRaid() ? Config.RAID_HP_REGEN_MULTIPLIER : Config.HP_REGEN_MULTIPLIER;
		double hpRegenBonus = 0;
		
		if (cha.isChampion())
			hpRegenMultiplier *= Config.CHAMPION_HP_REGEN;
		
		if (cha instanceof Player)
		{
			Player player = (Player) cha;
			
			if (player.isPhantomAntBot())
				return 0;
			
			// Calculate correct baseHpReg value for certain level of PC
			init += (player.getLevel() > 10) ? ((player.getLevel() - 1) / 10.0) : 0.5;
			
			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				hpRegenMultiplier *= calcFestivalRegenModifier(player);
			else if (calcSiegeRegenModifer(player))
				hpRegenMultiplier *= 1.5;
			
			if (player.isInsideZone(ZoneId.CLAN_HALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHideoutId();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + clansHall.getFunction(ClanHall.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}
			
			// Mother Tree effect is calculated at last
			if (player.isInsideZone(ZoneId.MOTHER_TREE))
			{
				L2MotherTreeZone zone = ZoneManager.getInstance().getZone(player, L2MotherTreeZone.class);
				int hpBonus = zone == null ? 0 : zone.getHpRegenBonus();
				hpRegenBonus += hpBonus;
			}
			
			// Calculate Movement bonus
			if (player.isSitting())
				hpRegenMultiplier *= 1.5; // Sitting
			else if (!player.isMoving())
				hpRegenMultiplier *= 1.1; // Staying
			else if (player.isRunning())
				hpRegenMultiplier *= 0.7; // Running
		}
		// Add CON bonus
		init *= cha.getLevelMod() * CON_BONUS[cha.getCON()];
		
		if (init < 1)
			init = 1;
		
		return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null) * hpRegenMultiplier + hpRegenBonus;
	}
	
	/**
	 * @param cha The character to make checks on.
	 * @return the MP regen rate (base + modifiers).
	 */
	public static final double calcMpRegen(Creature cha)
	{
		double init = cha.getTemplate().getBaseMpReg();
		double mpRegenMultiplier = cha.isRaid() ? Config.RAID_MP_REGEN_MULTIPLIER : Config.MP_REGEN_MULTIPLIER;
		double mpRegenBonus = 0;
		
		if (cha instanceof Player)
		{
			Player player = (Player) cha;
			
			// Calculate correct baseMpReg value for certain level of PC
			init += 0.3 * ((player.getLevel() - 1) / 10.0);
			
			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				mpRegenMultiplier *= calcFestivalRegenModifier(player);
			
			// Mother Tree effect is calculated at last
			if (player.isInsideZone(ZoneId.MOTHER_TREE))
			{
				L2MotherTreeZone zone = ZoneManager.getInstance().getZone(player, L2MotherTreeZone.class);
				int mpBonus = zone == null ? 0 : zone.getMpRegenBonus();
				mpRegenBonus += mpBonus;
			}
			
			if (player.isInsideZone(ZoneId.CLAN_HALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHideoutId();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1 + clansHall.getFunction(ClanHall.FUNC_RESTORE_MP).getLvl() / 100;
				}
			}
			
			// Calculate Movement bonus
			if (player.isSitting())
				mpRegenMultiplier *= 1.5; // Sitting
			else if (!player.isMoving())
				mpRegenMultiplier *= 1.1; // Staying
			else if (player.isRunning())
				mpRegenMultiplier *= 0.7; // Running
		}
		// Add MEN bonus
		init *= cha.getLevelMod() * MEN_BONUS[cha.getMEN()];
		
		if (init < 1)
			init = 1;
		
		return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null) * mpRegenMultiplier + mpRegenBonus;
	}
	
	/**
	 * @param player The player to make checks on.
	 * @return the CP regen rate (base + modifiers).
	 */
	public static final double calcCpRegen(Player player)
	{
		// Calculate correct baseHpReg value for certain level of PC
		double init = player.getTemplate().getBaseHpReg() + ((player.getLevel() > 10) ? ((player.getLevel() - 1) / 10.0) : 0.5);
		double cpRegenMultiplier = Config.CP_REGEN_MULTIPLIER;
		
		// Calculate Movement bonus
		if (player.isSitting())
			cpRegenMultiplier *= 1.5; // Sitting
		else if (!player.isMoving())
			cpRegenMultiplier *= 1.1; // Staying
		else if (player.isRunning())
			cpRegenMultiplier *= 0.7; // Running
		
		// Apply CON bonus
		init *= player.getLevelMod() * CON_BONUS[player.getCON()];
		
		if (init < 1)
			init = 1;
		
		return player.calcStat(Stats.REGENERATE_CP_RATE, init, null, null) * cpRegenMultiplier;
	}
	
	public static final double calcFestivalRegenModifier(Player activeChar)
	{
		final int[] festivalInfo = SevenSignsFestival.getInstance().getFestivalForPlayer(activeChar);
		final CabalType oracle = CabalType.VALUES[festivalInfo[0]];
		final int festivalId = festivalInfo[1];
		int[] festivalCenter;
		
		// If the player isn't found in the festival, leave the regen rate as it is.
		if (festivalId < 0)
			return 0;
		
		// Retrieve the X and Y coords for the center of the festival arena the player is in.
		if (oracle == CabalType.DAWN)
			festivalCenter = SevenSignsFestival.FESTIVAL_DAWN_PLAYER_SPAWNS[festivalId];
		else
			festivalCenter = SevenSignsFestival.FESTIVAL_DUSK_PLAYER_SPAWNS[festivalId];
		
		// Check the distance between the player and the player spawn point, in the center of the arena.
		double distToCenter = activeChar.getPlanDistanceSq(festivalCenter[0], festivalCenter[1]);
		
		if (Config.DEVELOPER)
			_log.info("Distance: " + distToCenter + ", RegenMulti: " + (distToCenter * 2.5) / 50);
		
		return 1.0 - (distToCenter * 0.0005); // Maximum Decreased Regen of ~ -65%;
	}
	
	/**
	 * @param activeChar the player to test on.
	 * @return true if the player is near one of his clan HQ (+50% regen boost).
	 */
	public static final boolean calcSiegeRegenModifer(Player activeChar)
	{
		if (activeChar == null || activeChar.getClan() == null)
			return false;
		
		final Siege siege = CastleManager.getInstance().getSiege(activeChar);
		if (siege == null || !siege.checkSide(activeChar.getClan(), SiegeSide.ATTACKER))
			return false;
		
		return MathUtil.checkIfInRange(200, activeChar, activeChar.getClan().getFlag(), true);
	}
	
	static SimpleDateFormat time_custom = new SimpleDateFormat("HH");
	
	/**
	 * @param attacker The attacker, from where the blow comes from.
	 * @param target The victim of the blow.
	 * @param skill The skill used.
	 * @param shld True if victim was wearign a shield.
	 * @param ss True if ss were activated.
	 * @return blow damage based on cAtk
	 */
	public static double calcBlowDamage(Creature attacker, Creature target, L2Skill skill, byte shld, boolean ss)
	{
		double defence = target.getPDef(attacker);
		// switch (shld)
		// {
		// case SHIELD_DEFENSE_SUCCEED:
		// defence += target.getShldDef();
		// break;
		
		// case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
		// return 1;
		// }
		
		final boolean isPvP = attacker instanceof Playable && target instanceof Playable;
		
		double power = skill.getPower();
		double damage = 0;
		damage += calcValakasAttribute(attacker, target, skill);
		
		if (ss)
		{
			damage *= 2.;
			
			if (skill.getSSBoost() > 0)
				power *= skill.getSSBoost();
		}
		
		
		damage += attacker.calcStat(Stats.CRITICAL_DAMAGE, (damage + power), target, skill);
		damage += attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill) * 6.5;
		damage *= target.calcStat(Stats.CRIT_VULN, 1, target, skill);
		
		// get the vulnerability for the instance due to skills (buffs, passives, toggles, etc)
		damage = target.calcStat(Stats.DAGGER_WPN_VULN, damage, target, null);
		damage *= 70. / defence;
		
		// Random weapon damage
		if (attacker instanceof Player && target instanceof Player)
		{
			if (Config.RANDOM_DAMAGE)
				damage += Rnd.nextDouble() * damage / 10;
		}
		else
			damage *= attacker.getRandomDamageMultiplier();
		
		// Dmg bonusses in PvP fight
		if (isPvP)
			damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		
		if (attacker instanceof Player && target instanceof Player)
		{
			if (Config.OLY_ENABLE_CUSTOM_ALL_DAMAGE && (attacker.isInOlympiadMode()))
				damage = damage * Config.OLY_ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
			else if (Config.ENABLE_CUSTOM_ALL_DAMAGE)
				damage = damage * Config.ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
		}
		
		if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER && Config.CUSTOM_BOSS_TASK_LIST.contains(time_custom.format(new Date(System.currentTimeMillis()))))
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage *= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage *= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage *= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage *= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
		}
		else if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER)
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage *= Config.ALT_MAGE_TO_BOSS_2;
					else
						damage *= Config.ALT_MAGE_TO_RAIDBOSS;
				}
				else
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage *= Config.ALT_FIGHTER_TO_BOSS_2;
					else
						damage *= Config.ALT_FIGHTER_TO_RAIDBOSS;
				}
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					damage *= Config.ALT_MAGE_TO_GRANDBOSS;
				}
				else
				{
					damage *= Config.ALT_FIGHTER_TO_GRANDBOSS;
				}
			}
		}
		
		if (attacker instanceof Player && target instanceof Monster)
		{
			if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage *= Config.ALT_MAGE_TO_MONSTER_2;
				else
					damage *= Config.ALT_MAGE_TO_MONSTER;
			}
			else
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage *= Config.ALT_FIGHTER_TO_MONSTER_2;
				else
					damage *= Config.ALT_FIGHTER_TO_MONSTER;
			}
		}
		
		
		if (attacker instanceof Player && target instanceof Player && !(target instanceof Monster)){
			/** Adventurer VS */
			if (((Player) attacker).isInOlympiadMode() && ((Player) target).isInOlympiadMode())
			{
				damage = damage*OlyClassDamageManager.getDamageMultiplier((Player) attacker, (Player) target);		
			}
			else
			{
				if (((Player)attacker).getClassId().getId() == 8 || ((Player)attacker).getClassId().getId() == 93)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Adventurer_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Adventurer_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Adventurer_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Adventurer_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Adventurer_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Adventurer_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Adventurer_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Adventurer_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Adventurer_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Adventurer_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Adventurer_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Adventurer_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Adventurer_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Adventurer_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Adventurer_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Adventurer_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Adventurer_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Adventurer_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Adventurer_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Adventurer_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Adventurer_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Adventurer_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Adventurer_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Adventurer_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Adventurer_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Adventurer_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Adventurer_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Adventurer_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Adventurer_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Adventurer_vs_Doomcryer;		  	
				}
				/** Wind_Rider VS */
				else if (((Player)attacker).getClassId().getId() == 23 || ((Player)attacker).getClassId().getId() == 101)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Wind_Rider_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Wind_Rider_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Wind_Rider_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Wind_Rider_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Wind_Rider_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Wind_Rider_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Wind_Rider_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Wind_Rider_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Wind_Rider_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Wind_Rider_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Wind_Rider_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Wind_Rider_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Wind_Rider_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Wind_Rider_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Wind_Rider_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Wind_Rider_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Wind_Rider_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Wind_Rider_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Wind_Rider_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Wind_Rider_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Wind_Rider_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Wind_Rider_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Wind_Rider_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Wind_Rider_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Wind_Rider_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Wind_Rider_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Wind_Rider_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Wind_Rider_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Wind_Rider_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Wind_Rider_vs_Doomcryer;		  	
				}
				
				/** Ghost_Hunter VS */ 
				else if (((Player)attacker).getClassId().getId() == 36 || ((Player)attacker).getClassId().getId() == 108)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Ghost_Hunter_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Ghost_Hunter_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Ghost_Hunter_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Ghost_Hunter_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Ghost_Hunter_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Ghost_Hunter_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Ghost_Hunter_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Ghost_Hunter_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Ghost_Hunter_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Ghost_Hunter_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Ghost_Hunter_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Ghost_Hunter_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Ghost_Hunter_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Ghost_Hunter_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Ghost_Hunter_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Ghost_Hunter_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Ghost_Hunter_vs_Wind_Rider;
					//mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Ghost_Hunter_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Ghost_Hunter_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Ghost_Hunter_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Ghost_Hunter_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Ghost_Hunter_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Ghost_Hunter_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Ghost_Hunter_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Ghost_Hunter_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Ghost_Hunter_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Ghost_Hunter_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Ghost_Hunter_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Ghost_Hunter_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Ghost_Hunter_vs_Doomcryer;		  	
				}
			}
		}
		
		
		return damage < 1 ? 1. : damage;
	}
	
	/**
	 * Calculated damage caused by ATTACK of attacker on target, called separatly for each weapon, if dual-weapon is used.
	 * @param attacker player or NPC that makes ATTACK
	 * @param target player or NPC, target of ATTACK
	 * @param skill skill used.
	 * @param shld target was using a shield or not.
	 * @param crit if the ATTACK have critical success
	 * @param ss if weapon item was charged by soulshot
	 * @return damage points
	 */
	public static final double calcPhysDam(Creature attacker, Creature target, L2Skill skill, byte shld, boolean crit, boolean ss)
	{
		if (attacker instanceof Player)
		{
			Player pcInst = (Player) attacker;
			if (pcInst.isGM() && !pcInst.getAccessLevel().canGiveDamage())
				return 0;
		}
		
		double defence = target.getPDef(attacker);
		// switch (shld)
		// {
		// case SHIELD_DEFENSE_SUCCEED:
		// if (!Config.ALT_GAME_SHIELD_BLOCKS)
		// defence += target.getShldDef();
		// break;
		
		// case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
		// return 1.;
		// }
		
		final boolean isPvP = attacker instanceof Playable && target instanceof Playable;
		double damage = attacker.getPAtk(target);
		
		damage += calcValakasAttribute(attacker, target, skill);
		
		if (ss)
			damage *= 2;
		
		if (skill != null)
		{
			double skillpower = skill.getPower(attacker);
			
			final float ssBoost = skill.getSSBoost();
			if (ssBoost > 0 && ss)
				skillpower *= ssBoost;
			
			damage += skillpower;
		}
		
		// defence modifier depending of the attacker weapon
		Weapon weapon = attacker.getActiveWeaponItem();
		Stats stat = null;
		if (weapon != null)
		{
			switch (weapon.getItemType())
			{
				case BOW:
					stat = Stats.BOW_WPN_VULN;
					break;
					
				case BLUNT:
					stat = Stats.BLUNT_WPN_VULN;
					break;
					
				case BIGSWORD:
					stat = Stats.BIGSWORD_WPN_VULN;
					break;
					
				case BIGBLUNT:
					stat = Stats.BIGBLUNT_WPN_VULN;
					break;
					
				case DAGGER:
					stat = Stats.DAGGER_WPN_VULN;
					break;
					
				case DUAL:
					stat = Stats.DUAL_WPN_VULN;
					break;
					
				case DUALFIST:
					stat = Stats.DUALFIST_WPN_VULN;
					break;
					
				case POLE:
					stat = Stats.POLE_WPN_VULN;
					break;
					
				case SWORD:
					stat = Stats.SWORD_WPN_VULN;
					break;
			}
		}
		
		if (crit)
		{
			// Finally retail like formula
			if (Config.OLY_ENABLE_CUSTOM_CRIT_POWER && attacker instanceof Player && (attacker.isInOlympiadMode()))
			{
				if (((Player) attacker).getClassId().equals(ClassId.PHANTOM_RANGER) || ((Player) attacker).getClassId().equals(ClassId.GHOST_SENTINEL))
					damage = Config.OLY_GHOSTSENTINEL_CRITICAL_POWER * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, 1, target, null) * (70 * damage / defence);
				else
					damage = Config.OLY_FIGHTER_CRITICAL_POWER * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, 1, target, null) * (70 * damage / defence);
			}
			else if (Config.ENABLE_CUSTOM_CRIT_POWER && attacker instanceof Player)
			{
				if (((Player) attacker).getClassId().equals(ClassId.PHANTOM_RANGER) || ((Player) attacker).getClassId().equals(ClassId.GHOST_SENTINEL))
					damage = Config.GHOSTSENTINEL_CRITICAL_POWER * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, 1, target, null) * (70 * damage / defence);
				else
					damage = Config.FIGHTER_CRITICAL_POWER * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, 1, target, null) * (70 * damage / defence);
			}
			else
				damage = Config.FIGHTER_CRITICAL_POWER * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, skill) * target.calcStat(Stats.CRIT_VULN, 1, target, null) * (70 * damage / defence);
			// Crit dmg add is almost useless in normal hits...
			damage += (attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill) * 70 / defence);
		}
		else
			damage = 70 * damage / defence;
		
		if (stat != null)
			damage = target.calcStat(stat, damage, target, null);
		
		// Weapon random damage ; invalid for CHARGEDAM skills.
		if (skill == null || skill.getEffectType() != L2SkillType.CHARGEDAM)
			if (attacker instanceof Player && target instanceof Player)
			{
				if (Config.RANDOM_DAMAGE)
					damage += Rnd.nextDouble() * damage / 10;
			}
			else
				damage *= attacker.getRandomDamageMultiplier();
		
		// if (shld > 0 && Config.ALT_GAME_SHIELD_BLOCKS)
		// {
		// damage -= target.getShldDef();
		// if (damage < 0)
		// damage = 0;
		// }
		
		if (target instanceof Npc)
		{
			double multiplier;
			switch (((Npc) target).getTemplate().getRace())
			{
				case BEAST:
					multiplier = 1 + ((attacker.getPAtkMonsters(target) - target.getPDefMonsters(target)) / 100);
					damage *= multiplier;
					break;
					
				case ANIMAL:
					multiplier = 1 + ((attacker.getPAtkAnimals(target) - target.getPDefAnimals(target)) / 100);
					damage *= multiplier;
					break;
					
				case PLANT:
					multiplier = 1 + ((attacker.getPAtkPlants(target) - target.getPDefPlants(target)) / 100);
					damage *= multiplier;
					break;
					
				case DRAGON:
					multiplier = 1 + ((attacker.getPAtkDragons(target) - target.getPDefDragons(target)) / 100);
					damage *= multiplier;
					break;
					
				case BUG:
					multiplier = 1 + ((attacker.getPAtkInsects(target) - target.getPDefInsects(target)) / 100);
					damage *= multiplier;
					break;
					
				case GIANT:
					multiplier = 1 + ((attacker.getPAtkGiants(target) - target.getPDefGiants(target)) / 100);
					damage *= multiplier;
					break;
					
				case MAGICCREATURE:
					multiplier = 1 + ((attacker.getPAtkMagicCreatures(target) - target.getPDefMagicCreatures(target)) / 100);
					damage *= multiplier;
					break;
			}
		}
		
		if (damage > 0 && damage < 1)
			damage = 1;
		else if (damage < 0)
			damage = 0;
		
		// Dmg bonuses in PvP fight
		if (isPvP)
		{
			if (skill == null)
				damage *= attacker.calcStat(Stats.PVP_PHYSICAL_DMG, 1, null, null);
			else
				damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		}
		
		// Weapon elemental damages
		damage += calcElemental(attacker, target, null);
		
		
		if (attacker instanceof Player && target instanceof Player)
		{
			if (Config.OLY_ENABLE_CUSTOM_ALL_DAMAGE && (attacker.isInOlympiadMode()))
				damage = damage * Config.OLY_ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
			else if (Config.ENABLE_CUSTOM_ALL_DAMAGE)
				damage = damage * Config.ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
			
		}
		
		if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER && Config.CUSTOM_BOSS_TASK_LIST.contains(time_custom.format(new Date(System.currentTimeMillis()))))
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage /= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage /= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage /= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage /= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
		}
		else if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER)
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage /= Config.ALT_MAGE_TO_BOSS_2;
					else
						damage /= Config.ALT_MAGE_TO_RAIDBOSS;
				}
				else
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage /= Config.ALT_FIGHTER_TO_BOSS_2;
					else
						damage /= Config.ALT_FIGHTER_TO_RAIDBOSS;
				}
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					damage /= Config.ALT_MAGE_TO_GRANDBOSS;
				}
				else
				{
					damage /= Config.ALT_FIGHTER_TO_GRANDBOSS;
				}
			}
		}
		
		if (attacker instanceof Player && target instanceof Monster)
		{
			if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage /= Config.ALT_MAGE_TO_MONSTER_2;
				else
					damage /= Config.ALT_MAGE_TO_MONSTER;
			}
			else
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage /= Config.ALT_FIGHTER_TO_MONSTER_2;
				else
					damage /= Config.ALT_FIGHTER_TO_MONSTER;
			}
		}
		
		if (attacker instanceof Summon)
			damage = damage * Config.ALT_PETS_PHYSICAL_DAMAGE_MULTI;
		
		
		if (attacker instanceof Player && target instanceof Player && !(target instanceof Monster)){
			/** Dueslista VS */
			if (((Player) attacker).isInOlympiadMode() && ((Player) target).isInOlympiadMode())
			{
				damage = damage*OlyClassDamageManager.getDamageMultiplier((Player) attacker, (Player) target);		
			}
			else
			{
				if (((Player)attacker).getClassId().getId() == 2 || ((Player)attacker).getClassId().getId() == 88)
				{
					if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Duelist_vs_DreadNought;		  	
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Duelist_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Duelist_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Duelist_vs_Sagittarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Duelist_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Duelist_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Duelist_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Duelist_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Duelist_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Duelist_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Duelist_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Duelist_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Duelist_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Duelist_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Duelist_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Duelist_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Duelist_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Duelist_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Duelist_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Duelist_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Duelist_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Duelist_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Duelist_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Duelist_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Duelist_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Duelist_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Duelist_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Duelist_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Duelist_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Duelist_vs_Doomcryer;		  	
				}
				/** DreadNought VS */
				else if (((Player)attacker).getClassId().getId() == 3 || ((Player)attacker).getClassId().getId() == 89)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.DreadNought_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.DreadNought_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.DreadNought_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.DreadNought_vs_Sagittarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.DreadNought_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.DreadNought_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.DreadNought_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.DreadNought_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.DreadNought_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.DreadNought_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.DreadNought_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.DreadNought_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.DreadNought_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.DreadNought_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.DreadNought_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.DreadNought_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.DreadNought_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.DreadNought_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.DreadNought_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.DreadNought_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.DreadNought_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.DreadNought_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.DreadNought_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.DreadNought_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.DreadNought_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.DreadNought_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.DreadNought_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.DreadNought_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.DreadNought_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.DreadNought_vs_Doomcryer;		  	
				}
				
				/** Phoenix_Knight VS */
				else if (((Player)attacker).getClassId().getId() == 5 || ((Player)attacker).getClassId().getId() == 90)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Phoenix_Knight_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Phoenix_Knight_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Phoenix_Knight_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Phoenix_Knight_vs_Sagittarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Phoenix_Knight_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Phoenix_Knight_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Phoenix_Knight_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Phoenix_Knight_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Phoenix_Knight_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Phoenix_Knight_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Phoenix_Knight_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Phoenix_Knight_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Phoenix_Knight_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Phoenix_Knight_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Phoenix_Knight_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Phoenix_Knight_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Phoenix_Knight_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Phoenix_Knight_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Phoenix_Knight_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Phoenix_Knight_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Phoenix_Knight_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Phoenix_Knight_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Phoenix_Knight_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Phoenix_Knight_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Phoenix_Knight_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Phoenix_Knight_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Phoenix_Knight_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Phoenix_Knight_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Phoenix_Knight_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Phoenix_Knight_vs_Doomcryer;		  	
				}
				
				/** Hell_Knight VS */ 
				else if (((Player)attacker).getClassId().getId() == 6 || ((Player)attacker).getClassId().getId() == 91)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Hell_Knight_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Hell_Knight_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Hell_Knight_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Hell_Knight_vs_Sagittarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Hell_Knight_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Hell_Knight_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Hell_Knight_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Hell_Knight_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Hell_Knight_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Hell_Knight_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Hell_Knight_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Hell_Knight_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Hell_Knight_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Hell_Knight_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Hell_Knight_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Hell_Knight_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Hell_Knight_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Hell_Knight_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Hell_Knight_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Hell_Knight_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Hell_Knight_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Hell_Knight_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Hell_Knight_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Hell_Knight_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Hell_Knight_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Hell_Knight_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Hell_Knight_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Hell_Knight_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Hell_Knight_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Hell_Knight_vs_Doomcryer;		  	
				}
				
				/** Sagittarius VS */ 
				else if (((Player)attacker).getClassId().getId() == 9 || ((Player)attacker).getClassId().getId() == 92)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Sagittarius_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Sagittarius_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Sagittarius_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Sagittarius_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Sagittarius_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Sagittarius_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Sagittarius_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Sagittarius_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Sagittarius_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Sagittarius_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Sagittarius_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Sagittarius_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Sagittarius_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Sagittarius_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Sagittarius_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Sagittarius_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Sagittarius_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Sagittarius_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Sagittarius_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Sagittarius_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Sagittarius_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Sagittarius_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Sagittarius_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Sagittarius_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Sagittarius_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Sagittarius_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Sagittarius_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Sagittarius_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Sagittarius_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Sagittarius_vs_Doomcryer;		  	
				}
				
				/** Eva_Templar VS */ 
				else if (((Player)attacker).getClassId().getId() == 20 || ((Player)attacker).getClassId().getId() == 99)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Eva_Templar_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Eva_Templar_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Eva_Templar_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Eva_Templar_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Eva_Templar_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Eva_Templar_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Eva_Templar_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Eva_Templar_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Eva_Templar_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Eva_Templar_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Eva_Templar_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Eva_Templar_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Eva_Templar_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Eva_Templar_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Eva_Templar_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Eva_Templar_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Eva_Templar_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Eva_Templar_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Eva_Templar_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Eva_Templar_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Eva_Templar_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Eva_Templar_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Eva_Templar_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Eva_Templar_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Eva_Templar_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Eva_Templar_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Eva_Templar_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Eva_Templar_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Eva_Templar_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Eva_Templar_vs_Doomcryer;		  	
				}
				
				/** Sword_Muse VS */
				else if (((Player)attacker).getClassId().getId() == 21 || ((Player)attacker).getClassId().getId() == 100)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Sword_Muse_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Sword_Muse_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Sword_Muse_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Sword_Muse_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Sword_Muse_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Sword_Muse_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Sword_Muse_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Sword_Muse_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Sword_Muse_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Sword_Muse_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Sword_Muse_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Sword_Muse_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Sword_Muse_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Sword_Muse_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Sword_Muse_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Sword_Muse_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Sword_Muse_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Sword_Muse_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Sword_Muse_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Sword_Muse_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Sword_Muse_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Sword_Muse_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Sword_Muse_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Sword_Muse_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Sword_Muse_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Sword_Muse_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Sword_Muse_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Sword_Muse_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Sword_Muse_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Sword_Muse_vs_Doomcryer;		  	
				}
				/** Adventurer VS */
				else if (((Player)attacker).getClassId().getId() == 8 || ((Player)attacker).getClassId().getId() == 93)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Adventurer_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Adventurer_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Adventurer_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Adventurer_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Adventurer_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Adventurer_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Adventurer_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Adventurer_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Adventurer_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Adventurer_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Adventurer_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Adventurer_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Adventurer_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Adventurer_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Adventurer_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Adventurer_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Adventurer_vs_Ghost_Hunter;
					// mages ==============//
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Adventurer_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Adventurer_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Adventurer_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Adventurer_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Adventurer_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Adventurer_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Adventurer_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Adventurer_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Adventurer_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Adventurer_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Adventurer_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Adventurer_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Adventurer_vs_Doomcryer;		  	
				}
				/** Wind_Rider VS */
				else if (((Player)attacker).getClassId().getId() == 23 || ((Player)attacker).getClassId().getId() == 101)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Wind_Rider_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Wind_Rider_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Wind_Rider_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Wind_Rider_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Wind_Rider_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Wind_Rider_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Wind_Rider_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Wind_Rider_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Wind_Rider_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Wind_Rider_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Wind_Rider_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Wind_Rider_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Wind_Rider_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Wind_Rider_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Wind_Rider_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Wind_Rider_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Wind_Rider_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Wind_Rider_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Wind_Rider_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Wind_Rider_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Wind_Rider_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Wind_Rider_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Wind_Rider_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Wind_Rider_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Wind_Rider_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Wind_Rider_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Wind_Rider_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Wind_Rider_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Wind_Rider_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Wind_Rider_vs_Doomcryer;		  	
				}
				
				/** Ghost_Hunter VS */ 
				else if (((Player)attacker).getClassId().getId() == 36 || ((Player)attacker).getClassId().getId() == 108)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Ghost_Hunter_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Ghost_Hunter_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Ghost_Hunter_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Ghost_Hunter_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Ghost_Hunter_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Ghost_Hunter_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Ghost_Hunter_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Ghost_Hunter_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Ghost_Hunter_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Ghost_Hunter_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Ghost_Hunter_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Ghost_Hunter_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Ghost_Hunter_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Ghost_Hunter_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Ghost_Hunter_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Ghost_Hunter_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Ghost_Hunter_vs_Wind_Rider;
					//mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Ghost_Hunter_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Ghost_Hunter_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Ghost_Hunter_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Ghost_Hunter_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Ghost_Hunter_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Ghost_Hunter_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Ghost_Hunter_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Ghost_Hunter_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Ghost_Hunter_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Ghost_Hunter_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Ghost_Hunter_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Ghost_Hunter_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Ghost_Hunter_vs_Doomcryer;		  	
				}
				/** Moonlight_Sentinel VS */
				else if (((Player)attacker).getClassId().getId() == 24 || ((Player)attacker).getClassId().getId() == 102)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Moonlight_Sentinel_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Moonlight_Sentinel_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Moonlight_Sentinel_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Moonlight_Sentinel_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Moonlight_Sentinel_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Moonlight_Sentinel_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Moonlight_Sentinel_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Moonlight_Sentinel_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Moonlight_Sentinel_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Moonlight_Sentinel_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Moonlight_Sentinel_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Moonlight_Sentinel_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Moonlight_Sentinel_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Moonlight_Sentinel_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Moonlight_Sentinel_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Moonlight_Sentinel_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Moonlight_Sentinel_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Moonlight_Sentinel_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Moonlight_Sentinel_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Moonlight_Sentinel_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Moonlight_Sentinel_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Moonlight_Sentinel_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Moonlight_Sentinel_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Moonlight_Sentinel_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Moonlight_Sentinel_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Moonlight_Sentinel_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Moonlight_Sentinel_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Moonlight_Sentinel_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Moonlight_Sentinel_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Moonlight_Sentinel_vs_Doomcryer;		  	
				}
				
				/** Shillien_Templar VS */
				else if (((Player)attacker).getClassId().getId() == 33 || ((Player)attacker).getClassId().getId() == 106)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Shillien_Templar_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Shillien_Templar_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Shillien_Templar_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Shillien_Templar_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Shillien_Templar_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Shillien_Templar_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Shillien_Templar_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Shillien_Templar_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Shillien_Templar_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Shillien_Templar_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Shillien_Templar_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Shillien_Templar_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Shillien_Templar_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Shillien_Templar_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Shillien_Templar_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Shillien_Templar_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Shillien_Templar_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Shillien_Templar_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Shillien_Templar_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Shillien_Templar_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Shillien_Templar_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Shillien_Templar_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Shillien_Templar_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Shillien_Templar_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Shillien_Templar_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Shillien_Templar_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Shillien_Templar_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Shillien_Templar_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Shillien_Templar_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Shillien_Templar_vs_Doomcryer;		  	
				}
				
				/** Spectral_Dancer VS */ 
				else if (((Player)attacker).getClassId().getId() == 34 || ((Player)attacker).getClassId().getId() == 107)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Spectral_Dancer_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Spectral_Dancer_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Spectral_Dancer_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Spectral_Dancer_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Spectral_Dancer_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Spectral_Dancer_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Spectral_Dancer_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Spectral_Dancer_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Spectral_Dancer_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Spectral_Dancer_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Spectral_Dancer_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Spectral_Dancer_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Spectral_Dancer_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Spectral_Dancer_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Spectral_Dancer_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Spectral_Dancer_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Spectral_Dancer_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Spectral_Dancer_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Spectral_Dancer_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Spectral_Dancer_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Spectral_Dancer_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Spectral_Dancer_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Spectral_Dancer_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Spectral_Dancer_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Spectral_Dancer_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Spectral_Dancer_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Spectral_Dancer_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Spectral_Dancer_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Spectral_Dancer_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Spectral_Dancer_vs_Doomcryer;		  	
				}
				
				/** Ghost_Sentinel VS */
				else if (((Player)attacker).getClassId().getId() == 37 || ((Player)attacker).getClassId().getId() == 109)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Ghost_Sentinel_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Ghost_Sentinel_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Ghost_Sentinel_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Ghost_Sentinel_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Ghost_Sentinel_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Ghost_Sentinel_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Ghost_Sentinel_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Ghost_Sentinel_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Ghost_Sentinel_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Ghost_Sentinel_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Ghost_Sentinel_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Ghost_Sentinel_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Ghost_Sentinel_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Ghost_Sentinel_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Ghost_Sentinel_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Ghost_Sentinel_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Ghost_Sentinel_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Ghost_Sentinel_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Ghost_Sentinel_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Ghost_Sentinel_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Ghost_Sentinel_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Ghost_Sentinel_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Ghost_Sentinel_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Ghost_Sentinel_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Ghost_Sentinel_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Ghost_Sentinel_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Ghost_Sentinel_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Ghost_Sentinel_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Ghost_Sentinel_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Ghost_Sentinel_vs_Doomcryer;		  	
				}
				
				/** Titan VS */  
				else if (((Player)attacker).getClassId().getId() == 46 || ((Player)attacker).getClassId().getId() == 113)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Titan_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Titan_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Titan_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Titan_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Titan_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Titan_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Titan_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Titan_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Titan_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Titan_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Titan_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Titan_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Titan_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Titan_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Titan_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Titan_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Titan_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Titan_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Titan_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Titan_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Titan_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Titan_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Titan_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Titan_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Titan_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Titan_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Titan_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Titan_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Titan_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Titan_vs_Doomcryer;		  	
				}
				
				/** Grand_Khauatari VS */ 
				else if (((Player)attacker).getClassId().getId() == 48 || ((Player)attacker).getClassId().getId() == 114)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Grand_Khauatari_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Grand_Khauatari_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Grand_Khauatari_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Grand_Khauatari_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Grand_Khauatari_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Grand_Khauatari_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Grand_Khauatari_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Grand_Khauatari_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Grand_Khauatari_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Grand_Khauatari_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Grand_Khauatari_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Grand_Khauatari_vs_Titan;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Grand_Khauatari_vs_Fortune_Seeker;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Grand_Khauatari_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Grand_Khauatari_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Grand_Khauatari_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Grand_Khauatari_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Grand_Khauatari_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Grand_Khauatari_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Grand_Khauatari_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Grand_Khauatari_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Grand_Khauatari_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Grand_Khauatari_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Grand_Khauatari_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Grand_Khauatari_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Grand_Khauatari_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Grand_Khauatari_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Grand_Khauatari_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Grand_Khauatari_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Grand_Khauatari_vs_Doomcryer;		  	
				}
				
				/** Fortune_Seeker VS */
				else if (((Player)attacker).getClassId().getId() == 57 || ((Player)attacker).getClassId().getId() == 118)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Fortune_Seeker_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Fortune_Seeker_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Fortune_Seeker_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Fortune_Seeker_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Fortune_Seeker_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Fortune_Seeker_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Fortune_Seeker_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Fortune_Seeker_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Fortune_Seeker_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Fortune_Seeker_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Fortune_Seeker_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Fortune_Seeker_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Fortune_Seeker_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Fortune_Seeker_vs_Maestro;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Fortune_Seeker_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Fortune_Seeker_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Fortune_Seeker_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Fortune_Seeker_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Fortune_Seeker_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Fortune_Seeker_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Fortune_Seeker_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Fortune_Seeker_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Fortune_Seeker_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Fortune_Seeker_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Fortune_Seeker_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Fortune_Seeker_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Fortune_Seeker_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Fortune_Seeker_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Fortune_Seeker_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Fortune_Seeker_vs_Doomcryer;		  	
				}
				
				/** Maestro VS */ 
				else if (((Player)attacker).getClassId().getId() == 55 || ((Player)attacker).getClassId().getId() == 117)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Maestro_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Maestro_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Maestro_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Maestro_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Maestro_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Maestro_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Maestro_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Maestro_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Maestro_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Maestro_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Maestro_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Maestro_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Maestro_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Maestro_vs_Fortune_Seeker;
					// daggers ==============//
						else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
							damage *= Config.Maestro_vs_Adventurer;
						else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
							damage *= Config.Maestro_vs_Wind_Rider;
						else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
							damage *= Config.Maestro_vs_Ghost_Hunter;
					// mages ==============//
						else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
							damage *= Config.Maestro_vs_Archmage;
						else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
							damage *= Config.Maestro_vs_Soultaker;
						else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
							damage *= Config.Maestro_vs_Arcana_Lord;
						else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
							damage *= Config.Maestro_vs_Cardinal;
						else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
							damage *= Config.Maestro_vs_Hierophant;
						else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
							damage *= Config.Maestro_vs_Mystic_Muse;
						else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
							damage *= Config.Maestro_vs_Elemental_Master;
						else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
							damage *= Config.Maestro_vs_Eva_s_Saint;
						else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
							damage *= Config.Maestro_vs_Storm_Screamer;
						else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
							damage *= Config.Maestro_vs_Spectral_Master;
						else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
							damage *= Config.Maestro_vs_Shillien_Saint;
						else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
							damage *= Config.Maestro_vs_Dominator;
						else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
							damage *= Config.Maestro_vs_Doomcryer;		  	
				}
			}
		}
		return damage;
	}
	
	public static final double calcMagicDam(Creature attacker, Creature target, L2Skill skill, byte shld, boolean ss, boolean bss, boolean mcrit)
	{
		if (attacker instanceof Player)
		{
			Player pcInst = (Player) attacker;
			if (pcInst.isGM() && !pcInst.getAccessLevel().canGiveDamage())
				return 0;
		}
		
		double mDef = target.getMDef(attacker, skill);
		// switch (shld)
		// {
		// case SHIELD_DEFENSE_SUCCEED:
		// mDef += target.getShldDef();
		// break;
		
		// case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
		// return 1.;
		// }
		
		double mAtk = attacker.getMAtk(target, skill);
		
		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;
		
		double damage = 91 * Math.sqrt(mAtk) / mDef * skill.getPower(attacker);
		
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill))
		{
			if (attacker instanceof Player)
			{
				if (calcMagicSuccess(attacker, target, skill) && (target.getLevel() - attacker.getLevel()) <= 9)
				{
					if (skill.getSkillType() == L2SkillType.DRAIN)
						attacker.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DRAIN_HALF_SUCCESFUL));
					else
						attacker.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
					
					damage /= 2;
				}
				else
				{
					attacker.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
					damage = 1;
				}
			}
			
			if (target instanceof Player)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RESISTED_S1_DRAIN).addCharName(attacker));
				else
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RESISTED_S1_MAGIC).addCharName(attacker));
			}
		}
		else if (mcrit)
		{
			if (attacker.isInOlympiadMode())
				damage *= Config.OLY_MAGIC_CRITICAL_POWER;
			else
				damage *= Config.MAGIC_CRITICAL_POWER;
			
		}
		// Pvp bonuses for dmg
		if (attacker instanceof Playable && target instanceof Playable)
		{
			if (skill.isMagic())
				damage *= attacker.calcStat(Stats.PVP_MAGICAL_DMG, 1, null, null);
			else
				damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		}
		
		damage *= calcElemental(attacker, target, skill);
		
		
		if (attacker instanceof Player && target instanceof Player)
		{
			if (Config.OLY_ENABLE_CUSTOM_ALL_DAMAGE && (attacker.isInOlympiadMode()))
			{
				if (((Player) attacker).isMageClass())
					damage = damage * Config.OLY_ALT_MAGES_MAGICAL_DAMAGE_MULTI;
			}
			else if (Config.ENABLE_CUSTOM_ALL_DAMAGE)
			{
				if (((Player) attacker).isMageClass())
					damage = damage * Config.ALT_MAGES_MAGICAL_DAMAGE_MULTI;
			}
			
			if (Config.RANDOM_DAMAGE)
				damage += Rnd.nextDouble() * damage / 10;
		}
		
		if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER && Config.CUSTOM_BOSS_TASK_LIST.contains(time_custom.format(new Date(System.currentTimeMillis()))))
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage /= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage /= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
					damage /= Config.ALT_MAGE_TO_BOSS_NIGHT;
				else
					damage /= Config.ALT_FIGHTER_TO_BOSS_NIGHT;
			}
		}
		else if (Config.ENABLE_CUSTOM_DAMAGE_MONSTER)
		{
			if (attacker instanceof Player && target instanceof RaidBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage /= Config.ALT_MAGE_TO_BOSS_2;
					else
						damage /= Config.ALT_MAGE_TO_RAIDBOSS;
				}
				else
				{
					if (Config.LIST_RAID_CUSTOM_DANO_IDS.contains(((RaidBoss) target).getNpcId()))
						damage /= Config.ALT_FIGHTER_TO_BOSS_2;
					else
						damage /= Config.ALT_FIGHTER_TO_RAIDBOSS;
				}
			}
			else if (attacker instanceof Player && target instanceof GrandBoss)
			{
				if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
				{
					damage /= Config.ALT_MAGE_TO_GRANDBOSS;
				}
				else
				{
					damage /= Config.ALT_FIGHTER_TO_GRANDBOSS;
				}
			}
		}
		
		if (attacker instanceof Player && target instanceof Monster)
		{
			if ((((Player) attacker).isMageClass() || (((Player) attacker).getTemplate().getRace() == ClassRace.ORC && (((Player) attacker).getClassId().getType() == ClassType.MYSTIC || ((Player) attacker).getClassId().getType() == ClassType.PRIEST))))
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage /= Config.ALT_MAGE_TO_MONSTER_2;
				else
					damage /= Config.ALT_MAGE_TO_MONSTER;
			}
			else
			{
				if (Config.LIST_MONSTER_CUSTOM_DANO_IDS.contains(((Monster) target).getNpcId()))
					damage /= Config.ALT_FIGHTER_TO_MONSTER_2;
				else
					damage /= Config.ALT_FIGHTER_TO_MONSTER;
			}
		}
		
		if (attacker instanceof Summon)
			damage = damage * Config.ALT_PETS_MAGICAL_DAMAGE_MULTI;
		
		if (attacker instanceof Player && target instanceof Player && !(target instanceof Monster)){
			/** Archmage VS */
			if (((Player) attacker).isInOlympiadMode() && ((Player) target).isInOlympiadMode())
			{
				damage = damage*OlyClassDamageManager.getDamageMultiplier((Player) attacker, (Player) target);		
			}
			else
			{
				if (((Player)attacker).getClassId().getId() == 12 || ((Player)attacker).getClassId().getId() == 94)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Archmage_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Archmage_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Archmage_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Archmage_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Archmage_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Archmage_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Archmage_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Archmage_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Archmage_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Archmage_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Archmage_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Archmage_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Archmage_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Archmage_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Archmage_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Archmage_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Archmage_vs_Ghost_Hunter;
					// mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Archmage_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Archmage_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Archmage_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Archmage_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Archmage_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Archmage_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Archmage_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Archmage_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Archmage_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Archmage_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Archmage_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Archmage_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Archmage_vs_Doomcryer;		  	
				}
				
				/** Soultaker VS */
				else if (((Player)attacker).getClassId().getId() == 13 || ((Player)attacker).getClassId().getId() == 95)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Soultaker_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Soultaker_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Soultaker_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Soultaker_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Soultaker_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Soultaker_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Soultaker_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Soultaker_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Soultaker_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Soultaker_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Soultaker_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Soultaker_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Soultaker_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Soultaker_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Soultaker_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Soultaker_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Soultaker_vs_Ghost_Hunter;
					// mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Soultaker_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Soultaker_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Soultaker_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Soultaker_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Soultaker_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Soultaker_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Soultaker_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Soultaker_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Soultaker_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Soultaker_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Soultaker_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Soultaker_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Soultaker_vs_Doomcryer;		  	
				}
				
				/** Arcana_Lord VS */
				else if (((Player)attacker).getClassId().getId() == 14 || ((Player)attacker).getClassId().getId() == 96)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Arcana_Lord_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Arcana_Lord_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Arcana_Lord_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Arcana_Lord_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Arcana_Lord_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Arcana_Lord_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Arcana_Lord_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Arcana_Lord_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Arcana_Lord_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Arcana_Lord_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Arcana_Lord_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Arcana_Lord_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Arcana_Lord_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Arcana_Lord_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Arcana_Lord_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Arcana_Lord_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Arcana_Lord_vs_Ghost_Hunter;
					// mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Arcana_Lord_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Arcana_Lord_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Arcana_Lord_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Arcana_Lord_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Arcana_Lord_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Arcana_Lord_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Arcana_Lord_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Arcana_Lord_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Arcana_Lord_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Arcana_Lord_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Arcana_Lord_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Arcana_Lord_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Arcana_Lord_vs_Doomcryer;		  	
				}
				/** Cardinal VS */
				else if (((Player)attacker).getClassId().getId() == 16 || ((Player)attacker).getClassId().getId() == 97)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Cardinal_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Cardinal_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Cardinal_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Cardinal_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Cardinal_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Cardinal_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Cardinal_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Cardinal_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Cardinal_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Cardinal_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Cardinal_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Cardinal_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Cardinal_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Cardinal_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Cardinal_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Cardinal_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Cardinal_vs_Ghost_Hunter;
					// mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Cardinal_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Cardinal_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Cardinal_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Cardinal_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Cardinal_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Cardinal_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Cardinal_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Cardinal_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Cardinal_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Cardinal_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Cardinal_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Cardinal_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Cardinal_vs_Doomcryer;		  	
				}
				
				/** Hierophant VS */
				else if (((Player)attacker).getClassId().getId() == 17 || ((Player)attacker).getClassId().getId() == 98)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Hierophant_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Hierophant_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Hierophant_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Hierophant_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Hierophant_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Hierophant_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Hierophant_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Hierophant_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Hierophant_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Hierophant_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Hierophant_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Hierophant_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Hierophant_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Hierophant_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Hierophant_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Hierophant_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Hierophant_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Hierophant_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Hierophant_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Hierophant_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Hierophant_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Hierophant_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Hierophant_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Hierophant_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Hierophant_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Hierophant_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Hierophant_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Hierophant_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Hierophant_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Hierophant_vs_Doomcryer;		  	
				}
				
				/** Mystic_Muse VS */
				else if (((Player)attacker).getClassId().getId() == 27 || ((Player)attacker).getClassId().getId() == 103)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Mystic_Muse_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Mystic_Muse_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Mystic_Muse_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Mystic_Muse_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Mystic_Muse_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Mystic_Muse_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Mystic_Muse_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Mystic_Muse_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Mystic_Muse_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Mystic_Muse_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Mystic_Muse_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Mystic_Muse_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Mystic_Muse_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Mystic_Muse_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Mystic_Muse_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Mystic_Muse_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Mystic_Muse_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Mystic_Muse_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Mystic_Muse_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Mystic_Muse_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Mystic_Muse_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Mystic_Muse_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Mystic_Muse_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Mystic_Muse_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Mystic_Muse_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Mystic_Muse_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Mystic_Muse_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Mystic_Muse_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Mystic_Muse_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Mystic_Muse_vs_Doomcryer;		  	
				}
				
				/** Elemental_Master VS */
				else if (((Player)attacker).getClassId().getId() == 28 || ((Player)attacker).getClassId().getId() == 104)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Elemental_Master_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Elemental_Master_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Elemental_Master_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Elemental_Master_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Elemental_Master_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Elemental_Master_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Elemental_Master_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Elemental_Master_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Elemental_Master_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Elemental_Master_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Elemental_Master_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Elemental_Master_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Elemental_Master_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Elemental_Master_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Elemental_Master_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Elemental_Master_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Elemental_Master_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Elemental_Master_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Elemental_Master_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Elemental_Master_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Elemental_Master_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Elemental_Master_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Elemental_Master_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Elemental_Master_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Elemental_Master_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Elemental_Master_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Elemental_Master_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Elemental_Master_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Elemental_Master_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Elemental_Master_vs_Doomcryer;		  	
				}
				
				/** Eva_s_Saint VS */
				else if (((Player)attacker).getClassId().getId() == 30 || ((Player)attacker).getClassId().getId() == 105)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Eva_s_Saint_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Eva_s_Saint_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Eva_s_Saint_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Eva_s_Saint_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Eva_s_Saint_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Eva_s_Saint_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Eva_s_Saint_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Eva_s_Saint_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Eva_s_Saint_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Eva_s_Saint_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Eva_s_Saint_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Eva_s_Saint_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Eva_s_Saint_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Eva_s_Saint_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Eva_s_Saint_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Eva_s_Saint_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Eva_s_Saint_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Eva_s_Saint_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Eva_s_Saint_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Eva_s_Saint_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Eva_s_Saint_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Eva_s_Saint_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Eva_s_Saint_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Eva_s_Saint_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Eva_s_Saint_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Eva_s_Saint_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Eva_s_Saint_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Eva_s_Saint_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Eva_s_Saint_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Eva_s_Saint_vs_Doomcryer;		  	
				}
				
				/** Storm_Screamer VS */ 
				else if (((Player)attacker).getClassId().getId() == 40 || ((Player)attacker).getClassId().getId() == 110)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Storm_Screamer_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Storm_Screamer_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Storm_Screamer_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Storm_Screamer_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Storm_Screamer_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Storm_Screamer_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Storm_Screamer_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Storm_Screamer_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Storm_Screamer_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Storm_Screamer_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Storm_Screamer_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Storm_Screamer_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Storm_Screamer_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Storm_Screamer_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Storm_Screamer_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Storm_Screamer_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Storm_Screamer_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Storm_Screamer_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Storm_Screamer_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Storm_Screamer_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Storm_Screamer_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Storm_Screamer_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Storm_Screamer_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Storm_Screamer_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Storm_Screamer_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Storm_Screamer_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Storm_Screamer_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Storm_Screamer_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Storm_Screamer_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Storm_Screamer_vs_Doomcryer;		  	
				}
				
				/** Spectral_Master VS */
				else if (((Player)attacker).getClassId().getId() == 41 || ((Player)attacker).getClassId().getId() == 111)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Spectral_Master_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Spectral_Master_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Spectral_Master_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Spectral_Master_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Spectral_Master_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Spectral_Master_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Spectral_Master_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Spectral_Master_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Spectral_Master_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Spectral_Master_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Spectral_Master_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Spectral_Master_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Spectral_Master_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Spectral_Master_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Spectral_Master_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Spectral_Master_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Spectral_Master_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Spectral_Master_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Spectral_Master_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Spectral_Master_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Spectral_Master_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Spectral_Master_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Spectral_Master_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Spectral_Master_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Spectral_Master_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Spectral_Master_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Spectral_Master_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Spectral_Master_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Spectral_Master_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Spectral_Master_vs_Doomcryer;		  	
				}
				
				/** Shillien_Saint VS */
				else if (((Player)attacker).getClassId().getId() == 43 || ((Player)attacker).getClassId().getId() == 112)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Shillien_Saint_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Shillien_Saint_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Shillien_Saint_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Shillien_Saint_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Shillien_Saint_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Shillien_Saint_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Shillien_Saint_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Shillien_Saint_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Shillien_Saint_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Shillien_Saint_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Shillien_Saint_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Shillien_Saint_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Shillien_Saint_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Shillien_Saint_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Shillien_Saint_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Shillien_Saint_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Shillien_Saint_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Shillien_Saint_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Shillien_Saint_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Shillien_Saint_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Shillien_Saint_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Shillien_Saint_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Shillien_Saint_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Shillien_Saint_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Shillien_Saint_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Shillien_Saint_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Shillien_Saint_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Shillien_Saint_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Shillien_Saint_vs_Dominator;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Shillien_Saint_vs_Doomcryer;		  	
				}
				
				/** Dominator VS */
				else if (((Player)attacker).getClassId().getId() == 51 || ((Player)attacker).getClassId().getId() == 115)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Dominator_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Dominator_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Dominator_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Dominator_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Dominator_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Dominator_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Dominator_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Dominator_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Dominator_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Dominator_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Dominator_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Dominator_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Dominator_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Dominator_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Dominator_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Dominator_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Dominator_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Dominator_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Dominator_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Dominator_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Dominator_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Dominator_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Dominator_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Dominator_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Dominator_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Dominator_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Dominator_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Dominator_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Dominator_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 52 || ((Player)target).getClassId().getId() == 116)
						damage *= Config.Dominator_vs_Doomcryer;		  	
				}
				
				/** Doomcryer VS */
				else if (((Player)attacker).getClassId().getId() == 52 || ((Player)attacker).getClassId().getId() == 116)
				{
					if (((Player)target).getClassId().getId() == 2 || ((Player)target).getClassId().getId() == 88)
						damage *= Config.Doomcryer_vs_Duelist;		  	
					else if (((Player)target).getClassId().getId() == 3 || ((Player)target).getClassId().getId() == 89)
						damage *= Config.Doomcryer_vs_DreadNought;
					else if (((Player)target).getClassId().getId() == 5 || ((Player)target).getClassId().getId() == 90)
						damage *= Config.Doomcryer_vs_Phoenix_Knight;
					else if (((Player)target).getClassId().getId() == 6 || ((Player)target).getClassId().getId() == 91)
						damage *= Config.Doomcryer_vs_Hell_Knight;
					else if (((Player)target).getClassId().getId() == 9 || ((Player)target).getClassId().getId() == 92)
						damage *= Config.Doomcryer_vs_Sagitarius;
					else if (((Player)target).getClassId().getId() == 20 || ((Player)target).getClassId().getId() == 99)
						damage *= Config.Doomcryer_vs_Eva_Templar;
					else if (((Player)target).getClassId().getId() == 21 || ((Player)target).getClassId().getId() == 100)
						damage *= Config.Doomcryer_vs_Sword_Muse;
					else if (((Player)target).getClassId().getId() == 24 || ((Player)target).getClassId().getId() == 102)
						damage *= Config.Doomcryer_vs_Moonlight_Sentinel;
					else if (((Player)target).getClassId().getId() == 33 || ((Player)target).getClassId().getId() == 106)
						damage *= Config.Doomcryer_vs_Shillien_Templar;
					else if (((Player)target).getClassId().getId() == 34 || ((Player)target).getClassId().getId() == 107)
						damage *= Config.Doomcryer_vs_Spectral_Dancer;
					else if (((Player)target).getClassId().getId() == 37 || ((Player)target).getClassId().getId() == 109)
						damage *= Config.Doomcryer_vs_Ghost_Sentinel;
					else if (((Player)target).getClassId().getId() == 46 || ((Player)target).getClassId().getId() == 113)
						damage *= Config.Doomcryer_vs_Titan;
					else if (((Player)target).getClassId().getId() == 48 || ((Player)target).getClassId().getId() == 114)
						damage *= Config.Doomcryer_vs_Grand_Khauatari;
					else if (((Player)target).getClassId().getId() == 57 || ((Player)target).getClassId().getId() == 118)
						damage *= Config.Doomcryer_vs_Fortune_Seeker;
					
					else if (((Player)target).getClassId().getId() == 55 || ((Player)target).getClassId().getId() == 117)
						damage *= Config.Doomcryer_vs_Maestro;
					else if (((Player)target).getClassId().getId() == 23 || ((Player)target).getClassId().getId() == 101)
						damage *= Config.Doomcryer_vs_Wind_Rider;
					else if (((Player)target).getClassId().getId() == 36 || ((Player)target).getClassId().getId() == 108)
						damage *= Config.Doomcryer_vs_Ghost_Hunter;
					//mages ==============//
					else if (((Player)target).getClassId().getId() == 8 || ((Player)target).getClassId().getId() == 93)
						damage *= Config.Doomcryer_vs_Adventurer;
					else if (((Player)target).getClassId().getId() == 12 || ((Player)target).getClassId().getId() == 94)
						damage *= Config.Doomcryer_vs_Archmage;
					else if (((Player)target).getClassId().getId() == 13 || ((Player)target).getClassId().getId() == 95)
						damage *= Config.Doomcryer_vs_Soultaker;
					else if (((Player)target).getClassId().getId() == 14 || ((Player)target).getClassId().getId() == 96)
						damage *= Config.Doomcryer_vs_Arcana_Lord;
					else if (((Player)target).getClassId().getId() == 16 || ((Player)target).getClassId().getId() == 97)
						damage *= Config.Doomcryer_vs_Cardinal;
					else if (((Player)target).getClassId().getId() == 17 || ((Player)target).getClassId().getId() == 98)
						damage *= Config.Doomcryer_vs_Hierophant;
					else if (((Player)target).getClassId().getId() == 27 || ((Player)target).getClassId().getId() == 103)
						damage *= Config.Doomcryer_vs_Mystic_Muse;
					else if (((Player)target).getClassId().getId() == 28 || ((Player)target).getClassId().getId() == 104)
						damage *= Config.Doomcryer_vs_Elemental_Master;
					else if (((Player)target).getClassId().getId() == 30 || ((Player)target).getClassId().getId() == 105)
						damage *= Config.Doomcryer_vs_Eva_s_Saint;
					else if (((Player)target).getClassId().getId() == 40 || ((Player)target).getClassId().getId() == 110)
						damage *= Config.Doomcryer_vs_Storm_Screamer;
					else if (((Player)target).getClassId().getId() == 41 || ((Player)target).getClassId().getId() == 111)
						damage *= Config.Doomcryer_vs_Spectral_Master;
					else if (((Player)target).getClassId().getId() == 43 || ((Player)target).getClassId().getId() == 112)
						damage *= Config.Doomcryer_vs_Shillien_Saint;
					else if (((Player)target).getClassId().getId() == 51 || ((Player)target).getClassId().getId() == 115)
						damage *= Config.Doomcryer_vs_Dominator;		  	
				}
			}
		}
		
		return damage;
	}
	
	public static final double calcMagicDam(Cubic attacker, Creature target, L2Skill skill, boolean mcrit, byte shld)
	{
		double mDef = target.getMDef(attacker.getOwner(), skill);
		switch (shld)
		{
			case SHIELD_DEFENSE_SUCCEED:
				mDef += target.getShldDef();
				break;
				
			case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
				return 1;
		}
		
		Player owner = attacker.getOwner();
		
		double damage = 91 / mDef * skill.getPower();
		
		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(owner, target, skill))
		{
			if (calcMagicSuccess(owner, target, skill) && (target.getLevel() - skill.getMagicLevel()) <= 9)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
					owner.sendPacket(SystemMessageId.DRAIN_HALF_SUCCESFUL);
				else
					owner.sendPacket(SystemMessageId.ATTACK_FAILED);
				
				damage /= 2;
			}
			else
			{
				owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(target).addSkillName(skill));
				damage = 1;
			}
			
			if (target instanceof Player)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RESISTED_S1_DRAIN).addCharName(owner));
				else
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.RESISTED_S1_MAGIC).addCharName(owner));
			}
		}
		else if (mcrit)
		{
			if (owner.isInOlympiadMode())
				damage *= Config.OLY_MAGIC_CRITICAL_POWER;
			else
				damage *= Config.MAGIC_CRITICAL_POWER;
			
		}
		damage *= calcElemental(owner, target, skill);
		
		return damage;
	}
	
	/**
	 * @param rate The value to make check on.
	 * @return true in case of critical hit
	 */
	public static final boolean calcCrit(double rate)
	{
		return rate > Rnd.get(1000);
	}
	
	/**
	 * Calcul value of blow success
	 * @param activeChar The character delaing the blow.
	 * @param target The victim.
	 * @param chance The base chance of landing a blow.
	 * @return true if successful, false otherwise
	 */
	public static final boolean calcBlow(Creature activeChar, Creature target, int chance)
	{
		return activeChar.calcStat(Stats.BLOW_RATE, chance * (1.0 + (activeChar.getDEX() - 20) / 100), target, null) > Rnd.get(100);
	}
	
	/**
	 * Calcul value of lethal chance
	 * @param activeChar The character delaing the blow.
	 * @param target The victim.
	 * @param baseLethal The base lethal chance of the skill.
	 * @param magiclvl
	 * @return
	 */
	public static final double calcLethal(Creature activeChar, Creature target, int baseLethal, int magiclvl)
	{
		double chance = 0;
		if (magiclvl > 0)
		{
			int delta = ((magiclvl + activeChar.getLevel()) / 2) - 1 - target.getLevel();
			
			if (delta >= -3)
				chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
			else if (delta < -3 && delta >= -9)
				chance = (-3) * (baseLethal / (delta));
			else
				chance = baseLethal / 15;
		}
		else
			chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
		
		chance = 10 * activeChar.calcStat(Stats.LETHAL_RATE, chance, target, null);
		
		if (Config.DEVELOPER)
			_log.info("Current calcLethal: " + chance + " / 1000");
		
		return chance;
	}
	
	public static final void calcLethalHit(Creature activeChar, Creature target, L2Skill skill)
	{
		if (target.isRaid() || target instanceof Door)
			return;
		
		if (target instanceof Npc && ((Npc) target).getNpcId() == Config.monsterId)
			return;
		
		// If one of following IDs is found, return false (Tyrannosaurus x 3, Headquarters)
		if (target instanceof Npc)
		{
			switch (((Npc) target).getNpcId())
			{
				case 22215:
				case 22216:
				case 22217:
				case 35062:
					return;
			}
		}
		
		// Second lethal effect (hp to 1 for npc, cp/hp to 1 for player).
		if (skill.getLethalChance2() > 0 && Rnd.get(1000) < calcLethal(activeChar, target, skill.getLethalChance2(), skill.getMagicLevel()))
		{
			if (target instanceof Npc)
				target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar, skill);
			else if (target instanceof Player) // If is a active player set his HP and CP to 1
			{
				Player player = (Player) target;
				if (!player.isInvul())
				{
					if (!(activeChar instanceof Player && (((Player) activeChar).isGM() && !((Player) activeChar).getAccessLevel().canGiveDamage())))
					{
						player.setCurrentHp(1);
						player.setCurrentCp(1);
						player.sendPacket(SystemMessageId.LETHAL_STRIKE);
					}
				}
			}
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL));
		}
		// First lethal effect (hp/2 for npc, cp to 1 for player).
		else if (skill.getLethalChance1() > 0 && Rnd.get(1000) < calcLethal(activeChar, target, skill.getLethalChance1(), skill.getMagicLevel()))
		{
			if (target instanceof Npc)
				target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar, skill);
			else if (target instanceof Player)
			{
				Player player = (Player) target;
				if (!player.isInvul())
				{
					if (!(activeChar instanceof Player && (((Player) activeChar).isGM() && !((Player) activeChar).getAccessLevel().canGiveDamage())))
					{
						player.setCurrentCp(1);
						player.sendPacket(SystemMessageId.LETHAL_STRIKE);
					}
				}
			}
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL));
		}
	}
	
	public static final boolean calcMCrit(int mRate)
	{
		if (Config.DEVELOPER)
			_log.info("Current mCritRate: " + mRate + "/1000");
		
		return mRate > Rnd.get(1000);
	}
	
	/**
	 * Check if casting process is canceled due to hit.
	 * @param target The target to make checks on.
	 * @param dmg The amount of dealt damages.
	 */
	public static final void calcCastBreak(Creature target, double dmg)
	{
		// Don't go further for invul characters or raid bosses.
		if (target.isRaid() || target.isInvul())
			return;
		
		// Break automatically the skill cast if under attack.
		if (target instanceof Player && ((Player) target).getFusionSkill() != null)
		{
			target.breakCast();
			return;
		}
		
		// Initialization to 15% for magical skills ; don't go further for ppl casting a physical skill
		if (!target.isCastingNow() && (target.getLastSkillCast() != null && !target.getLastSkillCast().isMagic()))
			return;
		
		// Calculate all modifiers for ATTACK_CANCEL ; chance to break is higher with higher dmg, and is affected by target MEN.
		double rate = target.calcStat(Stats.ATTACK_CANCEL, 15 + Math.sqrt(13 * dmg) - (MEN_BONUS[target.getMEN()] * 100 - 100), null, null);
		
		// Adjust the rate to be between 1 and 99
		if (rate > 99)
			rate = 99;
		else if (rate < 1)
			rate = 1;
		
		if (Config.DEVELOPER)
			_log.info("calcCastBreak rate: " + (int) rate + "%");
		
		if (Rnd.get(100) < rate)
			target.breakCast();
	}
	
	/**
	 * Calculate delay (in milliseconds) before next ATTACK.
	 * @param attacker
	 * @param target
	 * @param rate
	 * @return delay in ms.
	 */
	/*public static final int calcPAtkSpd(Creature attacker, Creature target, double rate)
	{
		if (rate < 2)
			return Config.ANTI_SS_BUG_1;
		
		return (int) (Config.ANTI_SS_BUG_3 / rate);
	}*/
	   public static final int calcPAtkSpd(Creature attacker, Creature target, double rate)
	   {
	       if (rate < 2)
	           return 2700;
	       
	       return (int) (470000 / rate);
	   }
	
	/**
	 * Calculate delay (in milliseconds) for skills cast.
	 * @param attacker
	 * @param skill used to know if skill is magic or no.
	 * @param skillTime
	 * @return delay in ms.
	 */
	public static final int calcAtkSpd(Creature attacker, L2Skill skill, double skillTime)
	{
		if (skill.isMagic())
			return (int) (skillTime * 333 / attacker.getMAtkSpd());
		
		return (int) (skillTime * 333 / attacker.getPAtkSpd());
	}
	
	/**
	 * Calculate the hit/miss chance.
	 * @param attacker : The attacker to make checks on.
	 * @param target : The target to make checks on.
	 * @return true if hit is missed, false if it evaded.
	 */
	public static boolean calcHitMiss(Creature attacker, Creature target)
	{
		int chance = (80 + (2 * (attacker.getAccuracy() - target.getEvasionRate(attacker)))) * 10;
		
		double modifier = 100;
		
		// Get high or low Z bonus.
		if (attacker.getZ() - target.getZ() > 50)
			modifier += 3;
		else if (attacker.getZ() - target.getZ() < -50)
			modifier -= 3;
		
		// Get weather bonus. TODO: rain support (-3%).
		if (GameTimeTaskManager.getInstance().isNight())
			modifier -= 10;
		
		// Get position bonus.
		if (attacker.isBehindTarget())
			modifier += 10;
		else if (!attacker.isInFrontOfTarget())
			modifier += 5;
		
		chance *= modifier / 100;
		
		if (Config.DEVELOPER)
			_log.info("calcHitMiss rate: " + chance / 10 + "%, modifier : x" + modifier / 100);
		
		return Math.max(Math.min(chance, 980), 200) < Rnd.get(1000);
	}
	
	/**
	 * Test the shield use.
	 * @param attacker The attacker.
	 * @param target The victim ; make check about his shield.
	 * @param skill The skill the attacker has used.
	 * @return 0 = shield defense doesn't succeed<br>
	 *         1 = shield defense succeed<br>
	 *         2 = perfect block
	 */
	public static byte calcShldUse(Creature attacker, Creature target, L2Skill skill)
	{
		// Ignore shield skills types bypass the shield use.
		if (skill != null && skill.ignoreShield())
			return 0;
		
		Item item = target.getSecondaryWeaponItem();
		if (item == null || !(item instanceof Armor))
			return 0;
		
		double shldRate = target.calcStat(Stats.SHIELD_RATE, 0, attacker, null) * DEX_BONUS[target.getDEX()];
		if (shldRate == 0.0)
			return 0;
		
		int degreeside = (int) target.calcStat(Stats.SHIELD_DEFENCE_ANGLE, 120, null, null);
		if (degreeside < 360 && (!target.isFacing(attacker, degreeside)))
			return 0;
		
		byte shldSuccess = SHIELD_DEFENSE_FAILED;
		
		// if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
		if (attacker.getAttackType() == WeaponType.BOW)
			shldRate *= 1.3;
		
		if (shldRate > 0 && 100 - Config.ALT_PERFECT_SHLD_BLOCK < Rnd.get(100))
			shldSuccess = SHIELD_DEFENSE_PERFECT_BLOCK;
		else if (shldRate > Rnd.get(100))
			shldSuccess = SHIELD_DEFENSE_SUCCEED;
		
		if (target instanceof Player)
		{
			switch (shldSuccess)
			{
				case SHIELD_DEFENSE_SUCCEED:
					((Player) target).sendPacket(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL);
					break;
					
				case SHIELD_DEFENSE_PERFECT_BLOCK:
					((Player) target).sendPacket(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
					break;
			}
		}
		
		return shldSuccess;
	}
	
	public static boolean calcMagicAffected(Creature actor, Creature target, L2Skill skill)
	{
		L2SkillType type = skill.getSkillType();
		if (target.isRaid() && !calcRaidAffected(type))
			return false;
		
		double defence = 0;
		
		if (skill.isActive() && skill.isOffensive())
			defence = target.getMDef(actor, skill);
		
		double attack = 2 * actor.getMAtk(target, skill) * calcSkillVulnerability(actor, target, skill, type);
		double d = (attack - defence) / (attack + defence);
		
		d += 0.5 * Rnd.nextGaussian();
		return d > 0;
	}
	
	public static double calcSkillVulnerability(Creature attacker, Creature target, L2Skill skill, L2SkillType type)
	{
		double multiplier = 1;
		
		// Get the elemental damages.
		if (skill.getElement() > 0)
			multiplier *= Math.sqrt(calcElemental(attacker, target, skill));
		
		// Get the skillType to calculate its effect in function of base stats of the target.
		switch (type)
		{
			case BLEED:
				multiplier = target.calcStat(Stats.BLEED_VULN, multiplier, target, null);
				break;
				
			case POISON:
				multiplier = target.calcStat(Stats.POISON_VULN, multiplier, target, null);
				break;
				
			case STUN:
				multiplier = target.calcStat(Stats.STUN_VULN, multiplier, target, null);
				break;
				
			case PARALYZE:
				multiplier = target.calcStat(Stats.PARALYZE_VULN, multiplier, target, null);
				break;
				
			case ROOT:
				multiplier = target.calcStat(Stats.ROOT_VULN, multiplier, target, null);
				break;
				
			case SLEEP:
				multiplier = target.calcStat(Stats.SLEEP_VULN, multiplier, target, null);
				break;
				
			case MUTE:
			case FEAR:
			case BETRAY:
			case AGGDEBUFF:
			case AGGREDUCE_CHAR:
			case ERASE:
			case CONFUSION:
				multiplier = target.calcStat(Stats.DERANGEMENT_VULN, multiplier, target, null);
				break;
				
			case DEBUFF:
			case WEAKNESS:
				multiplier = target.calcStat(Stats.DEBUFF_VULN, multiplier, target, null);
				break;
				
			case CANCEL:
				multiplier = target.calcStat(Stats.CANCEL_VULN, multiplier, target, null);
				break;
		}
		
		// Return a multiplier (exemple with resist shock : 1 + (-0,4 stun vuln) = 0,6%
		return multiplier;
	}
	
	private static double calcSkillStatModifier(L2SkillType type, Creature target)
	{
		double multiplier = 1;
		
		switch (type)
		{
			case STUN:
			case BLEED:
			case POISON:
				multiplier = 2 - SQRT_CON_BONUS[target.getStat().getCON()];
				break;
				
			case SLEEP:
			case DEBUFF:
			case WEAKNESS:
			case ERASE:
			case ROOT:
			case MUTE:
			case FEAR:
			case BETRAY:
			case CONFUSION:
			case AGGREDUCE_CHAR:
			case PARALYZE:
				multiplier = 2 - SQRT_MEN_BONUS[target.getStat().getMEN()];
				break;
		}
		
		return Math.max(0, multiplier);
	}
	
	public static double getSTRBonus(Creature activeChar)
	{
		return STR_BONUS[activeChar.getSTR()];
	}
	
	private static double getLevelModifier(Creature attacker, Creature target, L2Skill skill)
	{
		if (skill.getLevelDepend() == 0)
			return 1;
		
		int delta = (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()) + skill.getLevelDepend() - target.getLevel();
		return 1 + ((delta < 0 ? 0.01 : 0.005) * delta);
	}
	
	private static double getMatkModifier(Creature attacker, Creature target, L2Skill skill, boolean bss)
	{
		double mAtkModifier = 1;
		
		if (skill.isMagic())
		{
			final double mAtk = attacker.getMAtk(target, skill);
			double val = mAtk;
			if (bss)
				val = mAtk * 4.0;
			
			mAtkModifier = (Math.sqrt(val) / target.getMDef(attacker, skill)) * 11.0;
		}
		return mAtkModifier;
	}
	
	public static boolean calcEffectSuccess(Creature attacker, Creature target, EffectTemplate effect, L2Skill skill, byte shld, boolean bss)
	{
		if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) // perfect block
			return false;
		
		final L2SkillType type = effect.effectType;
		final double baseChance = effect.effectPower;
		
		if (type == null)
			return Rnd.get(100) < baseChance;
		
		if (type.equals(L2SkillType.CANCEL)) // CANCEL type lands always
			return true;
		
		final double statModifier = calcSkillStatModifier(type, target);
		final double skillModifier = calcSkillVulnerability(attacker, target, skill, type);
		final double mAtkModifier = getMatkModifier(attacker, target, skill, bss);
		final double lvlModifier = getLevelModifier(attacker, target, skill);
		double rate = Math.max(1, Math.min((baseChance * statModifier * skillModifier * mAtkModifier * lvlModifier), 99));
		
		if (Config.DEVELOPER)
			_log.info("calcEffectSuccess(): Name:" + skill.getName() + " eff.type:" + type.toString() + " power:" + baseChance + " statMod:" + String.format("%1.2f", statModifier) + " skillMod:" + String.format("%1.2f", skillModifier) + " mAtkMod:" + String.format("%1.2f", mAtkModifier) + " lvlMod:" + String.format("%1.2f", lvlModifier) + " total:" + String.format("%1.2f", rate) + "%");
		
		if (Config.ENABLE_CUSTOM_CHANCE_SKILL && !(target instanceof Monster))
		{
			float physics_mult = getChanceMultiplier(skill);
			rate *= physics_mult;
		}

		if (Config.SKILL_MAX_CHANCE && Config.SKILL_LIST_SUCCESS_IN_OLY.contains(skill.getId()) && target.isInOlympiadMode() && attacker.isInOlympiadMode()){
			if(rate > Config.SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD)
			rate = Config.SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD;
			else
			if(rate < Config.SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD)
			rate = Config.SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD;
			
		}else if (Config.SKILL_MAX_CHANCE && Config.SKILL_LIST_SUCCESS.contains(skill.getId()) && !(target instanceof Monster)){
			if(rate > Config.SKILLS_MAX_CHANCE_SUCCESS)
			rate = Config.SKILLS_MAX_CHANCE_SUCCESS;
			else
			if(rate < Config.SKILLS_MIN_CHANCE_SUCCESS)
			rate = Config.SKILLS_MIN_CHANCE_SUCCESS;
		}
		
		if(Config.CUSTOM_CHANCE_FOR_ALL_SKILL){
		if(rate > Config.SKILLS_MAX_CHANCE)
		rate = Config.SKILLS_MAX_CHANCE;
		else
		if(rate < Config.SKILLS_MIN_CHANCE)
		rate = Config.SKILLS_MIN_CHANCE;
		}
		
		if (attacker instanceof Player && attacker.isGM())
			((Player) attacker).sendMessage("EffectType: " + effect.effectType + " Chance: " + String.format("%1.2f", rate) + "%");
		
		return (Rnd.get(100) < rate);
	}
	
	public static boolean calcSkillSuccess(Creature attacker, Creature target, L2Skill skill, byte shld, boolean bss)
	{
		if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) // perfect block
			return false;
		
		final L2SkillType type = skill.getEffectType();
		
		if (target.isRaid() && !calcRaidAffected(type))
			return false;
		
		final double baseChance = skill.getEffectPower();
		if (skill.ignoreResists())
			return (Rnd.get(100) < baseChance);
		
		final double statModifier = calcSkillStatModifier(type, target);
		final double skillModifier = calcSkillVulnerability(attacker, target, skill, type);
		final double mAtkModifier = getMatkModifier(attacker, target, skill, bss);
		final double lvlModifier = getLevelModifier(attacker, target, skill);
		double rate = Math.max(1, Math.min((baseChance * statModifier * skillModifier * mAtkModifier * lvlModifier), 99));
		
		if (Config.DEVELOPER)
			_log.info("calcSkillSuccess(): Name:" + skill.getName() + " type:" + skill.getSkillType().toString() + " power:" + baseChance + " statMod:" + String.format("%1.2f", statModifier) + " skillMod:" + String.format("%1.2f", skillModifier) + " mAtkMod:" + String.format("%1.2f", mAtkModifier) + " lvlMod:" + String.format("%1.2f", lvlModifier) + " total:" + String.format("%1.2f", rate) + "%");
		
		if (Config.ENABLE_CUSTOM_CHANCE_SKILL && !(target instanceof Monster))
		{
			float physics_mult = getChanceMultiplier(skill);
			rate *= physics_mult;
		}
		
		if (Config.SKILL_MAX_CHANCE && Config.SKILL_LIST_SUCCESS_IN_OLY.contains(skill.getId()) && target.isInOlympiadMode() && attacker.isInOlympiadMode()){
			if(rate > Config.SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD)
			rate = Config.SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD;
			else
			if(rate < Config.SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD)
			rate = Config.SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD;
			
		}else if (Config.SKILL_MAX_CHANCE && Config.SKILL_LIST_SUCCESS.contains(skill.getId()) && !(target instanceof Monster)){
			if(rate > Config.SKILLS_MAX_CHANCE_SUCCESS)
			rate = Config.SKILLS_MAX_CHANCE_SUCCESS;
			else
			if(rate < Config.SKILLS_MIN_CHANCE_SUCCESS)
			rate = Config.SKILLS_MIN_CHANCE_SUCCESS;
		}
		
		if(rate > Config.SKILLS_MAX_CHANCE)
		rate = Config.SKILLS_MAX_CHANCE;
		else
		if(rate < Config.SKILLS_MIN_CHANCE)
		rate = Config.SKILLS_MIN_CHANCE;
		
		if (attacker instanceof Player && attacker.isGM())
			((Player) attacker).sendMessage("Skill: " + skill.getName() + " Chance: " + String.format("%1.2f", rate) + "%");
		
		return (Rnd.get(100) < rate);
	}
	
	public static float getChanceMultiplier(L2Skill skill)
	{
		
		float multiplier = 1;
		
		if (skill != null && skill.getSkillType() != null)
		{	
			// Surrender to fire
			if (skill.getId() == 1083)
				multiplier *= Config.SURRENDER_TO_FIRE;		
			// Vortex to fire
			if (skill.getId() == 1339)
				multiplier *= Config.VORTEX_TO_FIRE;		
			// Surrender to wind
			if (skill.getId() == 1074)
				multiplier *= Config.SURRENDER_TO_WIND;			
			// Wind vortex
			if (skill.getId() == 1341)
				multiplier *= Config.WIND_VORTEX;		
			// Curse gloom
			if (skill.getId() == 1263)
				multiplier *= Config.CURSE_GLOOM;			
			// Dark vortex
			if (skill.getId() == 1343)
				multiplier *= Config.DARK_VORTEX;		
			// Silence
			if (skill.getId() == 1064)
				multiplier *= Config.SILENCE;		
			// Sleep
			if (skill.getId() == 1069)
				multiplier *= Config.SLEEP;	
			// Curse Fear
			if (skill.getId() == 1169)
				multiplier *= Config.CURSE_FEAR;		
			// Anchor
			if (skill.getId() == 1170)
				multiplier *= Config.ANCHOR;		
			// Curse of Doom
			if (skill.getId() == 1336)
				multiplier *= Config.CURSE_OF_DOOM;		
			// Curse of Abyss
			if (skill.getId() == 1337)
				multiplier *= Config.CURSE_OF_ABYSS;			
			// Cancellation
			if (skill.getId() == 1056)
				multiplier *= Config.CANCELLATION;			
			// Mass Slow
			if (skill.getId() == 1298)
				multiplier *= Config.MASS_SLOW;			
			// Mass Fear
			if (skill.getId() == 1381)
				multiplier *= Config.MASS_FEAR;			
			// Mass Gloom
			if (skill.getId() == 1382)
				multiplier *= Config.MASS_GLOOM;			
			// Sleeping Cloud
			if (skill.getId() == 1072)
				multiplier *= Config.SLEEPING_CLOUD;			
			// Surrender to water
			if (skill.getId() == 1071)
				multiplier *= Config.SURRENDER_TO_WATER;			
			// Ice Vortex
			if (skill.getId() == 1340)
				multiplier *= Config.ICE_VORTEX;		
			// Light Vortex
			if (skill.getId() == 1342)
				multiplier *= Config.LIGHT_VORTEX;			
			// Heroic Grandeur
			if (skill.getId() == 1375)
				multiplier *= Config.HEROIC_GRANDEUR;			
			// Heroic Dread
			if (skill.getId() == 1376)
				multiplier *= Config.HEROIC_DREAD;			
			// Stunning shot
			if (skill.getId() == 101)
				multiplier *= Config.STUNNING_SHOT;			
			// Hex
			if (skill.getId() == 122)
				multiplier *= Config.HEX;		
			// Shock stomp
			if (skill.getId() == 452)
				multiplier *= Config.SHOCK_STOMP;			
			// Thunder Storm
			if (skill.getId() == 48)
				multiplier *= Config.THUNDER_STORM;			
			// Shield Stun
			if (skill.getId() == 92)
				multiplier *= Config.SHIELD_STUN;			
			// Shield slam
			if (skill.getId() == 353)
				multiplier *= Config.SHIELD_SLAM;		
			// Shackle
			if (skill.getId() == 403)
				multiplier *= Config.SHACKLE;		
			// Shackle
			if (skill.getId() == 404)
				multiplier *= Config.MASS_SHACKLING;			
			// Arrest
			if (skill.getId() == 402)
				multiplier *= Config.ARREST;			
			// Bluff
			if (skill.getId() == 358)
				multiplier *= Config.BLUFF;		
			// Switch
			if (skill.getId() == 12)
				multiplier *= Config.SWITCH;			
			// Stunning Fist
			if (skill.getId() == 120)
				multiplier *= Config.STUNNING_FIST;			
			// Fear Over
			if (skill.getId() == 1092)
				multiplier *= Config.FEAR_OVER;
			// Seal of Silence
			if (skill.getId() == 1246)
				multiplier *= Config.SEAL_OF_SILENCE;
			// Seal of suspension
			if (skill.getId() == 1248)
				multiplier *= Config.SEAL_OF_SUSPENSION;
			// Stun attack
			if (skill.getId() == 100)
				multiplier *= Config.STUN_ATTACK;
			// Armor crush
			if (skill.getId() == 362)
				multiplier *= Config.ARMOR_CRUSH;		
			// Slow
			if (skill.getId() == 1160)
				multiplier *= Config.SLOW;
			// Seal of Despair
			if (skill.getId() == 1366)
				multiplier *= Config.SEAL_OF_DESPAIR;
			// Dreaming Spirit
			if (skill.getId() == 1097)
				multiplier *= Config.DREAMING_SPIRIT;
			// Seal of binding
			if (skill.getId() == 1208)
				multiplier *= Config.SEAL_OF_BINDING;			
			// Mass Warrior Bane
			if (skill.getId() == 1344)
				multiplier *= Config.MASS_WARRIOR_BANE;			
			// Mass Mage Bane
			if (skill.getId() == 1345)
				multiplier *= Config.MASS_MAGE_BANE;
			// Shield Bash
			if (skill.getId() == 352)
				multiplier *= Config.SHIELD_BASH;
			// Shock Blast
			if (skill.getId() == 361)
				multiplier *= Config.SHOCK_BLAST;
			
		}
		
		return multiplier;
		
	}
	
	public static boolean calcCubicSkillSuccess(Cubic attacker, Creature target, L2Skill skill, byte shld, boolean bss)
	{
		// if target reflect this skill then the effect will fail
		if (calcSkillReflect(target, skill) != SKILL_REFLECT_FAILED)
			return false;
		
		if (shld == SHIELD_DEFENSE_PERFECT_BLOCK) // perfect block
			return false;
		
		final L2SkillType type = skill.getEffectType();
		
		if (target.isRaid() && !calcRaidAffected(type))
			return false;
		
		final double baseChance = skill.getEffectPower();
		
		if (skill.ignoreResists())
			return Rnd.get(100) < baseChance;
		
		double mAtkModifier = 1;
		
		// Add Matk/Mdef Bonus
		if (skill.isMagic())
		{
			final double mAtk = attacker.getMAtk();
			double val = mAtk;
			if (bss)
				val = mAtk * 4.0;
			
			mAtkModifier = (Math.sqrt(val) / target.getMDef(null, null)) * 11.0;
		}
		
		final double statModifier = calcSkillStatModifier(type, target);
		final double skillModifier = calcSkillVulnerability(attacker.getOwner(), target, skill, type);
		final double lvlModifier = getLevelModifier(attacker.getOwner(), target, skill);
		final double rate = Math.max(1, Math.min((baseChance * statModifier * skillModifier * mAtkModifier * lvlModifier), 99));
		
		if (Config.DEVELOPER)
			_log.info("calcCubicSkillSuccess(): Name:" + skill.getName() + " type:" + skill.getSkillType().toString() + " power:" + String.valueOf(baseChance) + " statMod:" + String.format("%1.2f", statModifier) + " skillMod:" + String.format("%1.2f", skillModifier) + " mAtkMod:" + String.format("%1.2f", mAtkModifier) + " lvlMod:" + String.format("%1.2f", lvlModifier) + " total:" + String.format("%1.2f", rate) + "%");
		
		return (Rnd.get(100) < rate);
	}
	
	public static boolean calcMagicSuccess(Creature attacker, Creature target, L2Skill skill)
	{
		int lvlDifference = target.getLevel() - ((skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()) + skill.getLevelDepend());
		double rate = 100;
		
		if (lvlDifference > 0)
			rate = (Math.pow(1.3, lvlDifference)) * 100;
		
		if (attacker instanceof Player && ((Player) attacker).getExpertiseWeaponPenalty())
			rate += 6000;
		
		if (Config.DEVELOPER)
			_log.info("calcMagicSuccess(): Name:" + skill.getName() + " lvlDiff:" + lvlDifference + " fail:" + String.format("%1.2f", rate / 100) + "%");
		
		rate = Math.min(rate, 9900);
		
		return (Rnd.get(10000) > rate);
	}
	
	public static double calcManaDam(Creature attacker, Creature target, L2Skill skill, boolean ss, boolean bss)
	{
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		double mp = target.getMaxMp();
		
		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;
		
		double damage = (Math.sqrt(mAtk) * skill.getPower(attacker) * (mp / 97)) / mDef;
		damage *= calcSkillVulnerability(attacker, target, skill, skill.getSkillType());
		return damage;
	}
	
	public static double calculateSkillResurrectRestorePercent(double baseRestorePercent, Creature caster)
	{
		if (baseRestorePercent == 0 || baseRestorePercent == 100)
			return baseRestorePercent;
		
		double restorePercent = baseRestorePercent * WIT_BONUS[caster.getWIT()];
		if (restorePercent - baseRestorePercent > 20.0)
			restorePercent += 20.0;
		
		restorePercent = Math.max(restorePercent, baseRestorePercent);
		restorePercent = Math.min(restorePercent, 90.0);
		
		return restorePercent;
	}
	
	public static boolean calcPhysicalSkillEvasion(Creature target, L2Skill skill)
	{
		if (skill.isMagic())
			return false;
		
		return Rnd.get(100) < target.calcStat(Stats.P_SKILL_EVASION, 0, null, skill);
	}
	
	public static boolean calcSkillMastery(Creature actor, L2Skill sk)
	{
		// Pointless check for Creature other than players, as initial value will stay 0.
		if (!(actor instanceof Player))
			return false;
		
		if (sk.getSkillType() == L2SkillType.FISHING)
			return false;
		
		double val = actor.getStat().calcStat(Stats.SKILL_MASTERY, 0, null, null);
		
		if (((Player) actor).isMageClass())
			val *= INT_BONUS[actor.getINT()];
		else
			val *= STR_BONUS[actor.getSTR()];
		
		return Rnd.get(100) < val;
	}
	
	public static double calcValakasAttribute(Creature attacker, Creature target, L2Skill skill)
	{
		double calcPower = 0;
		double calcDefen = 0;
		
		if (skill != null && skill.getAttributeName().contains("valakas"))
		{
			calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
			calcDefen = target.calcStat(Stats.VALAKAS_RES, calcDefen, target, skill);
		}
		else
		{
			calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
			if (calcPower > 0)
			{
				calcPower = attacker.calcStat(Stats.VALAKAS, calcPower, target, skill);
				calcDefen = target.calcStat(Stats.VALAKAS_RES, calcDefen, target, skill);
			}
		}
		return calcPower - calcDefen;
	}
	
	/**
	 * Calculate elemental modifier. There are 2 possible cases :
	 * <ul>
	 * <li>the check emanates from a skill : the result will be a multiplier, including an amount of attacker element, and the target vuln/prof.</li>
	 * <li>the check emanates from a weapon : the result is an addition of all elements, lowered/enhanced by the target vuln/prof</li>
	 * </ul>
	 * @param attacker : The attacker used to retrieve elemental attacks.
	 * @param target : The victim used to retrieve elemental protections.
	 * @param skill : If different of null, it will be considered as a skill resist check.
	 * @return A multiplier or a sum of damages.
	 */
	public static double calcElemental(Creature attacker, Creature target, L2Skill skill)
	{
		if (skill != null)
		{
			final byte element = skill.getElement();
			if (element > 0)
				return 1 + (((attacker.getAttackElementValue(element) / 10.0) / 100.0) - (1 - target.getDefenseElementValue(element)));
			
			return 1;
		}
		
		double elemDamage = 0;
		for (byte i = 1; i < 7; i++)
		{
			final int attackerBonus = attacker.getAttackElementValue(i);
			elemDamage += Math.max(0, (attackerBonus - (attackerBonus * (target.getDefenseElementValue(i) / 100.0))));
		}
		return elemDamage;
	}
	
	/**
	 * Calculate skill reflection according to these three possibilities:
	 * <ul>
	 * <li>Reflect failed</li>
	 * <li>Normal reflect (just effects).</li>
	 * <li>Vengeance reflect (100% damage reflected but damage is also dealt to actor).</li>
	 * </ul>
	 * @param target : The skill's target.
	 * @param skill : The skill to test.
	 * @return SKILL_REFLECTED_FAILED, SKILL_REFLECT_SUCCEED or SKILL_REFLECT_VENGEANCE
	 */
	public static byte calcSkillReflect(Creature target, L2Skill skill)
	{
		// Some special skills (like hero debuffs...) or ignoring resistances skills can't be reflected.
		if (skill.ignoreResists() || !skill.canBeReflected())
			return SKILL_REFLECT_FAILED;
		
		// Only magic and melee skills can be reflected.
		if (!skill.isMagic() && (skill.getCastRange() == -1 || skill.getCastRange() > MELEE_ATTACK_RANGE))
			return SKILL_REFLECT_FAILED;
		
		byte reflect = SKILL_REFLECT_FAILED;
		
		// Check for non-reflected skilltypes, need additional retail check.
		switch (skill.getSkillType())
		{
			case BUFF:
			case REFLECT:
			case HEAL_PERCENT:
			case MANAHEAL_PERCENT:
			case HOT:
			case CPHOT:
			case MPHOT:
			case UNDEAD_DEFENSE:
			case AGGDEBUFF:
			case CONT:
				return SKILL_REFLECT_FAILED;
				
			case PDAM:
			case BLOW:
			case MDAM:
			case DEATHLINK:
			case CHARGEDAM:
				final double venganceChance = target.getStat().calcStat((skill.isMagic()) ? Stats.VENGEANCE_SKILL_MAGIC_DAMAGE : Stats.VENGEANCE_SKILL_PHYSICAL_DAMAGE, 0, target, skill);
				if (venganceChance > Rnd.get(100))
					reflect |= SKILL_REFLECT_VENGEANCE;
				break;
		}
		
		final double reflectChance = target.calcStat((skill.isMagic()) ? Stats.REFLECT_SKILL_MAGIC : Stats.REFLECT_SKILL_PHYSIC, 0, null, skill);
		if (Rnd.get(100) < reflectChance)
			reflect |= SKILL_REFLECT_SUCCEED;
		
		return reflect;
	}
	
	/**
	 * @param cha : The character affected.
	 * @param fallHeight : The height the NPC fallen.
	 * @return the damage, based on max HPs and falling height.
	 */
	public static double calcFallDam(Creature cha, int fallHeight)
	{
		if (!Config.ENABLE_FALLING_DAMAGE || fallHeight < 0)
			return 0;
		
		return cha.calcStat(Stats.FALL, fallHeight * cha.getMaxHp() / 1000, null, null);
	}
	
	/**
	 * @param type : The L2SkillType to test.
	 * @return true if the L2SkillType can affect a raid boss, false otherwise.
	 */
	public static boolean calcRaidAffected(L2SkillType type)
	{
		switch (type)
		{
			case MANADAM:
			case MDOT:
				return true;
				
			case CONFUSION:
			case ROOT:
			case STUN:
			case MUTE:
			case FEAR:
			case DEBUFF:
			case PARALYZE:
			case SLEEP:
			case AGGDEBUFF:
			case AGGREDUCE_CHAR:
				if (Rnd.get(1000) == 1)
					return true;
		}
		return false;
	}
	
	/**
	 * Calculates karma lost upon death.
	 * @param playerLevel : The level of the PKer.
	 * @param exp : The amount of xp earned.
	 * @return The amount of karma player has lost.
	 */
	public static int calculateKarmaLost(int playerLevel, long exp)
	{
		return (int) (exp / karmaMods[playerLevel] / 15);
	}
	
	/**
	 * Calculates karma gain upon player kill.
	 * @param pkCount : The current number of PK kills.
	 * @param isSummon : Does the victim is a summon or no (lesser karma gain if true).
	 * @return karma points that will be added to the player.
	 */
	public static int calculateKarmaGain(int pkCount, boolean isSummon)
	{
		int result = 14400;
		if (pkCount < 100)
			result = (int) (((((pkCount - 1) * 0.5) + 1) * 60) * 4);
		else if (pkCount < 180)
			result = (int) (((((pkCount + 1) * 0.125) + 37.5) * 60) * 4);
		
		if (isSummon)
			result = ((pkCount & 3) + result) >> 2;
		
		return result;
	}
}