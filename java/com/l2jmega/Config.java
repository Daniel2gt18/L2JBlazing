package com.l2jmega;

import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.holder.RewardHolder;
import com.l2jmega.gameserver.model.olympiad.OlympiadPeriod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.l2jmega.commons.config.ExProperties;
import com.l2jmega.commons.math.MathUtil;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * @author mkizub
 */
public final class Config
{
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	
	/** Main */
	public static final String NETWORK_FILE = "./config/gameserver.ini";
	public static final String BOSS_FILE = "./config/main/boss.ini";
	public static final String CLANS_FILE = "./config/main/clans.ini";
	public static final String EVENTS_FILE = "./config/main/events.ini";
	public static final String GEOENGINE_FILE = "./config/main/geoengine.ini";
	public static final String GM_FILE = "./config/custom/Admin.ini";
	public static final String NPCS_FILE = "./config/main/npcs.ini";
	public static final String OPTIONS_FILE = "./config/main/options.ini";
	public static final String PARTY_FILE = "./config/main/party.ini";
	public static final String PLAYERS_FILE = "./config/main/players.ini";
	public static final String PVP_FILE = "./config/main/pvp.ini";
	public static final String RATES_FILE = "./config/main/rates.ini";
	public static final String SIEGE_FILE = "./config/main/siege.ini";
	public static final String SKILLS_FILE = "./config/main/skills.ini";
	public static final String HEXID_FILE = "./config/hexid.txt";	
	public static final String FILTER_FILE = "./config/chatfilter.txt";	
	public static final String LOGIN_CONFIGURATION_FILE = "./config/loginserver.ini";
	public static final String LOGGING = "./config/logging.ini";
	
	/** Custom */
	public static final String L2JMEGA_FILE = "./config/custom/L2_jMega.ini";
	public static final String ENCHANT_FILE = "./config/custom/Enchant.ini";
	public static final String PHYSICS_FILE = "./config/custom/balance/Physics.ini";
	public static final String BALANCE_SKILLS_FILE = "./config/custom/balance/Skills_chance_modifier.ini";
	public static final String OLY_CLASS_DAMAGES_FILE = "./config/custom/balance/Damages_inOly.ini";
	public static final String OLYMPIAD_PHYSICS_FILE = "./config/custom/balance/Physics_inOly.ini";
	public static final String TVT_FILE = "./config/custom/events/TvT.ini";
	public static final String CTF_FILE = "./config/custom/events/Ctf.ini";
	public static final String PVP_EVENT_FILE = "./config/custom/events/PvP.ini";
	public static final String PARTY_ZONE_FILE = "./config/custom/events/PartyZone.ini";
	public static final String TOURNAMENT_FILE = "./config/custom/events/Tournament.ini";
	public static final String PHANTOM_FILE = "./config/custom/phantom/PhantomPlayers.ini";
	public static final String PVPZONE_SYSTEM = "./config/custom/pvpzone/PvpZone.ini";
	public static final String PROTECT_FILE = "./config/custom/Protect.ini";
	public static final String PC_BANG_FILE = "./config/custom/events/PcBang.ini";
	public static final String MISSION_FILE = "./config/custom/events/Mission.ini";
	public static final String PVPKING_FILE = "./config/custom/events/PvPKing24hrs.ini";
	public static final String D_GUARD_FILE = "./config/custom/Protect.ini";
	
	// balance por x1 by MeGaPacK
	public static final String Duelist_FILE = "./config/custom/balance/classes/duelist_vs_class.ini";
	public static final String DreadNought_FILE = "./config/custom/balance/classes/DreadNought_vs_class.ini";
	public static final String Phoenix_Knight_FILE = "./config/custom/balance/classes/tankers/phoenix_knight_vs_class.ini";
	public static final String Hell_Knight_FILE = "./config/custom/balance/classes/tankers/hell_knight_vs_class.ini";
	public static final String Sagittarius_FILE = "./config/custom/balance/classes/archers/sagitarius_vs_class.ini";
	public static final String Eva_Templar_FILE = "./config/custom/balance/classes/tankers/eva_templar_vs_class.ini";
	public static final String Sword_Muse_FILE = "./config/custom/balance/classes/tankers/sword_muse_vs_class.ini";
	public static final String Moonlight_Sentinel_FILE = "./config/custom/balance/classes/archers/moonlight_sentinel_vs_class.ini";
	public static final String Shillien_Templar_FILE = "./config/custom/balance/classes/tankers/shillien_templar_vs_class.ini";
	private static final String Spectral_Dancer_FILE = "./config/custom/balance/classes/spectral_dancer_vs_class.ini";
	private static final String Ghost_Sentinel_FILE = "./config/custom/balance/classes/archers/ghost_sentinel_vs_class.ini";
	private static final String Titan_FILE = "./config/custom/balance/classes/titan_vs_class.ini";
	private static final String Grand_Khauatari_FILE = "./config/custom/balance/classes/grand_khavatari_vs_class.ini";
	private static final String Fortune_Seeker_FILE = "./config/custom/balance/classes/fortune_seeker_vs_class.ini";
	private static final String Maestro_FILE = "./config/custom/balance/classes/maestro_vs_class.ini";
	private static final String Adventurer_FILE = "./config/custom/balance/classes/daggers/adventurer_vs_class.ini";
	private static final String Wind_Rider_FILE = "./config/custom/balance/classes/daggers/wind_rider_vs_class.ini";
	private static final String Ghost_Hunter_FILE = "./config/custom/balance/classes/daggers/ghost_hunter_vs_class.ini";
	private static final String Archmage_FILE = "./config/custom/balance/classes/mages/archmage_vs_class.ini";
	private static final String Soultaker_FILE = "./config/custom/balance/classes/mages/soultaker_vs_class.ini";
	private static final String Arcana_Lord_FILE = "./config/custom/balance/classes/mages/arcana_lord_vs_class.ini";
	private static final String Cardinal_FILE = "./config/custom/balance/classes/mages/cardinal_vs_class.ini";
	private static final String Hierophant_FILE = "./config/custom/balance/classes/mages/hierophant_vs_class.ini";
	private static final String Mystic_Muse_FILE = "./config/custom/balance/classes/mages/mystic_muse_vs_class.ini";
	private static final String Elemental_Master_FILE = "./config/custom/balance/classes/mages/elemental_master_vs_class.ini";
	private static final String Eva_s_Saint_FILE = "./config/custom/balance/classes/mages/eva_s_saint_vs_class.ini";
	private static final String Storm_Screamer_FILE = "./config/custom/balance/classes/mages/storm_scream_vs_class.ini";
	private static final String Spectral_Master_FILE = "./config/custom/balance/classes/mages/spectral_master_vs_class.ini";
	private static final String Shillien_Saint_FILE = "./config/custom/balance/classes/mages/shillien_saint_vs_class.ini";
	private static final String Dominator_FILE = "./config/custom/balance/classes/mages/dominator_vs_class.ini";
	private static final String Doomcryer_FILE = "./config/custom/balance/classes/mages/doomcryer_vs_class.ini";

	
	public static String RAID_RESPAWN_IDS;
	public static List<Integer> RAID_RESPAWN_IDS_LIST;
	
	public static int MIN_RESPAWN;
	public static int MAX_RESPAWN;
	
	public static boolean RESPAWN_CUSTOM;

	public static boolean ANNOUNCE_DROP_ITEM;
	public static boolean GM_VIEW_PL_ON;
	public static String NAME_TVT;
	public static String NAME_TOUR;
	public static String NAME_PVP;
	public static String NAME_CTF;
	public static int ANNOUNCE_ID_EVENT;
	
	public static boolean PVP_EVENT_ENABLED;	  
	public static String[] PVP_EVENT_INTERVAL;	  
	public static int PVP_EVENT_RUNNING_TIME;	  
	public static int[][] PVP_EVENT_REWARDS; 
	public static boolean ALLOW_SPECIAL_PVP_REWARD;
	public static List<int[]> PVP_SPECIAL_ITEMS_REWARD;
	public static boolean SCREN_MSG_PVP;
	public static int pvp_locx;
	public static int pvp_locy;
	public static int pvp_locz;
	
	public static int PC_1x1;
	public static int PC_PVP;
	public static int MISSION_PVP_REWARD_ID;
	public static int MISSION_PVP_REWARD_AMOUNT;
	public static int MISSION_PVP_CONT;
	public static boolean ACTIVE_MISSION_PVP;
	
	public static int ID_RAID;
	public static int PC_PARTYZONE;
	public static int PC_MOBS;
	public static int PC_2x2;
	public static int PC_5x5;
	public static int PC_9x9;
	public static int PC_RAID_IND;
	public static int PC_TVT;
	
	public static boolean REWARD_PCPOINT;
	public static boolean ENABLE_GEODATA;
	
	public static boolean BLOCK_AIOX_PVPZONE;
	public static boolean USE_AIOX_OUTSIDE_PEACEZONE;
	public static boolean LEAVE_BUFF_SOLOFARM;
	
	public static boolean ENABLE_FLAGZONE;
	public static boolean ENABLE_DWARF_ENCHANT_BONUS;
	public static int DWARF_ENCHANT_MIN_LEVEL;
	public static int DWARF_ENCHANT_BONUS;
	
	public static boolean ENABLE_CHAT_LEVEL;
	public static int CHAT_LEVEL;
	public static boolean CMD_SKIN;
	public static int CHANCE_FAIRY_LEAF;
	
	public static int MIN_ENCHANT;
	public static int MAX_ENCHANT;
	
	public static int MIN_MEMBERS_LV6;
	public static int MIN_MEMBERS_LV7;
	public static int MIN_MEMBERS_LV8;
	
	public static int MIN_FLAMESTONE_GIANT;
	public static int MAX_FLAMESTONE_GIANT;
	public static int MIN_THEMIS_SCALE;
	public static int MAX_THEMIS_SCALE;
	public static int MIN_HEKATON_PRIME;
	public static int MAX_HEKATON_PRIME;
	public static int MIN_GARGOYLE;
	public static int MAX_GARGOYLE;
	public static int MIN_GLAKI;
	public static int MAX_GLAKI;
	public static int MIN_RAHHA;
	public static int MAX_RAHHA;
	
	public static boolean SKILL_MAX_CHANCE;
	public static List<Integer> SKILL_LIST_SUCCESS_IN_OLY = new ArrayList<>();
	public static List<Integer> SKILL_LIST_SUCCESS = new ArrayList<>();
	public static double SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD;
	public static double SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD;
	public static double SKILLS_MAX_CHANCE_SUCCESS;
	public static double SKILLS_MIN_CHANCE_SUCCESS;
	public static boolean CUSTOM_CHANCE_FOR_ALL_SKILL;
	public static double SKILLS_MAX_CHANCE;
	public static double SKILLS_MIN_CHANCE;
	
	public static boolean ENABLE_AIO_SYSTEM;
	public static String AIO_TITLE; 
	public static boolean CHANGE_AIO_NAME; 
	public static Map<Integer, Integer> AIO_SKILLS; 
	public static boolean ALLOW_AIO_NCOLOR;	  
	public static int AIO_NCOLOR;	  
	public static boolean ALLOW_AIO_TCOLOR;
	public static int AIO_TCOLOR;
	public static boolean ALLOW_AIO_ITEM;
	public static int AIO_ITEMID; 
	public static boolean ALLOW_AIOX_SET_ITEM;
	public static List<int[]> AIO_CHAR_ITENS = new ArrayList<>();
	
	public static String TITLE_MONSTER_SOLO;
	
	public static int TOUR_AURA_TEAM1;
	public static int TOUR_AURA_TEAM2;
	/** Drops enchantados */
	public static String ID_ITEM_ENCHANT;
	public static ArrayList<Integer> LIST_ENCHANT_ITEM = new ArrayList<>();
	public static int ENCHANT_LEVEL;
	
	public static String NEWS_1;
	public static String NEWS_2;
	public static String NEWS_3;
	public static String NEWS_4;
	public static String DONATE_URL;
	public static String INFO_URL;
	
	//----------------------------------------
	//   PvPKing24hrs.properties
	//----------------------------------------
	/** Character Killing Monument */
	public static boolean CKM_ENABLED;
	public static long CKM_CYCLE_LENGTH;
	public static String CKM_PVP_NPC_TITLE;
	public static int CKM_PVP_NPC_TITLE_COLOR;
	public static int CKM_PVP_NPC_NAME_COLOR;
	public static String CKM_PK_NPC_TITLE;
	public static int CKM_PK_NPC_TITLE_COLOR;
	public static int CKM_PK_NPC_NAME_COLOR;
	public static int[][] CKM_PLAYER_REWARDS;
	//----------------------------------------
	//   Mission.properties
	//----------------------------------------
	/** Mission Event */
	public static boolean ACTIVE_MISSION;	  
	public static boolean ACTIVE_MISSION_TVT;
	public static int MISSION_TVT_CONT;	  
	public static int MISSION_TVT_REWARD_ID;	  
	public static int MISSION_TVT_REWARD_AMOUNT;	  
	public static boolean ACTIVE_MISSION_RAID;	  
	public static int MISSION_RAID_CONT;	  
	public static int MISSION_RAID_REWARD_ID;	  
	public static int MISSION_RAID_REWARD_AMOUNT;	  
	public static boolean ACTIVE_MISSION_MOB;	  
	public static int MISSION_MOB_CONT;	  
	public static boolean ACTIVE_MISSION_PARTY_MOB;	  
	public static int MISSION_PARTY_MOB_CONT;	  
	public static int MISSION_PARTY_MOB_REWARD_ID;	  
	public static int MISSION_PARTY_MOB_REWARD_AMOUNT;
	public static String MISSION_LIST_MOBS;  
	public static ArrayList<Integer> MISSION_LIST_MONSTER = new ArrayList<>();	  
	public static int MISSION_MOB_REWARD_ID;  
	public static int MISSION_MOB_REWARD_AMOUNT;  
	
	public static boolean ACTIVE_MISSION_1X1;  
	public static int MISSION_1X1_CONT;	  
	public static int MISSION_1X1_REWARD_ID;	  
	public static int MISSION_1X1_REWARD_AMOUNT;
	
	public static boolean ACTIVE_MISSION_2X2;  
	public static int MISSION_2X2_CONT;	  
	public static int MISSION_2X2_REWARD_ID;	  
	public static int MISSION_2X2_REWARD_AMOUNT;	  
	public static boolean ACTIVE_MISSION_5X5;	  
	public static int MISSION_5X5_CONT;  
	public static int MISSION_5X5_REWARD_ID;	  
	public static int MISSION_5X5_REWARD_AMOUNT;	  
	public static boolean ACTIVE_MISSION_9X9;	  
	public static int MISSION_9X9_CONT;	  
	public static int MISSION_9X9_REWARD_ID;	  
	public static int MISSION_9X9_REWARD_AMOUNT;	    
	public static String[] CLEAR_MISSION_INTERVAL_BY_TIME_OF_DAY;
	
	//----------------------------------------
	//   boss.properties
	//----------------------------------------
	/** Raid Boss */
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_DEFENCE_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	
	/** Quest grandboss */
	public static int QUEST_BAIUM;
	public static int QUEST_VALAKAS;
	public static int QUEST_ANTHARAS;
	public static int QUEST_SAILREN;
	public static int QUEST_FRINTEZZA;
	
	/** Grand Boss */
	public static boolean EARTH_QUAKE;
	public static int SPAWN_INTERVAL_AQ;
	public static int RANDOM_SPAWN_TIME_AQ;	
	public static int SPAWN_INTERVAL_ANTHARAS;
	public static int RANDOM_SPAWN_TIME_ANTHARAS;
	public static int WAIT_TIME_ANTHARAS;	
	public static int SPAWN_INTERVAL_BAIUM;
	public static int RANDOM_SPAWN_TIME_BAIUM;	
	public static int SPAWN_INTERVAL_CORE;
	public static int RANDOM_SPAWN_TIME_CORE;	
	public static int SPAWN_INTERVAL_ORFEN;
	public static int RANDOM_SPAWN_TIME_ORFEN;	
	public static int SPAWN_INTERVAL_VALAKAS;
	public static int RANDOM_SPAWN_TIME_VALAKAS;
	public static int WAIT_TIME_VALAKAS;	
	public static int SPAWN_INTERVAL_ZAKEN;
	public static int RANDOM_SPAWN_TIME_ZAKEN;
	public static int WAIT_TIME_ZAKEN;	
	public static int SPAWN_INTERVAL_FRINTEZZA;
	public static int RANDOM_SPAWN_TIME_FRINTEZZA;
	public static boolean BYPASS_FRINTEZZA_PARTIES_CHECK;
	public static int FRINTEZZA_MIN_PARTIES;
	public static int FRINTEZZA_MAX_PARTIES;
	public static int FRINTEZZA_TIME_CHALLENGE;
	public static int WAIT_TIME_FRINTEZZA;
	public static int DESPAWN_TIME_FRINTEZZA;	
	public static int SPAWN_INTERVAL_SAILREN;
	public static int RANDOM_SPAWN_TIME_SAILREN;
	public static int WAIT_TIME_SAILREN;
	
	/** Protection zone Raid Boss */
	public static String RAID_BOSS_ZONE_IDS;
	public static ArrayList<Integer> LIST_RAID_BOSS_ZONE_IDS = new ArrayList<>();
	
	public static String RAID_INFO_IDS;
	public static List<Integer> RAID_INFO_IDS_LIST;
	
	public static String GRANDBOSS_INFO_IDS;
	public static List<Integer> GRANDBOSS_INFO_IDS_LIST;
	
	/** Barakiel Nobless */
	public static int BOSS_ID;
	public static boolean ALLOW_AUTO_NOBLESS_FROM_BOSS;
	public static int RADIUS_TO_RAID;
	
	/** Announce RB Color */
	public static boolean ANNOUNCE_TO_ALL_SPAWN_RB;
	public static int ANNOUNCE_ID;
	public static String RAID_ID_ANNOUNCE;
	public static ArrayList<Integer> LIST_RAID_ANNOUNCE = new ArrayList<>();
	
	/** GRAND BOSSES */
	public static boolean AQ_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> AQ_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int AQ_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean ANTHARAS_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> ANTHARAS_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean CORE_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> CORE_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int CORE_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean ORFEN_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> ORFEN_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean ZAKEN_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> ZAKEN_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int ZAKEN_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean FRINTEZZA_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> FRINTEZZA_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int FRINTEZZA_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean BAIUM_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> BAIUM_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean VALAKAS_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> VALAKAS_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL;
	public static boolean SAILREN_CUSTOM_SPAWN_ENABLED;
	public static ArrayList<Calendar> SAILREN_CUSTOM_SPAWN_TIMES = new ArrayList<>();
	public static int SAILREN_CUSTOM_SPAWN_RANDOM_INTERVAL;
	
	//----------------------------------------
	//   Clans.properties
	//----------------------------------------
	/** Clans */
	public static int ITEM_ID_BUY_CLAN_HALL;
	public static boolean ALLOW_CLAN_FULL;
	public static boolean DISABLE_ROYAL;
	public static int ALT_MAX_NUM_OF_MEMBERS_IN_CLAN;
	public static int ALT_NUM_OF_MEMBERS_IN_CLAN;
	public static int ALT_CLAN_JOIN_MINUTES;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static int ALT_CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static boolean REMOVE_CASTLE_CIRCLETS;
	
	/** Manor */
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_MIN;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	
	/** Clan Hall function */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	//----------------------------------------
	//   Enchant.properties
	//----------------------------------------
	/** Enchant */
	public static HashMap<Integer, Integer> NORMAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_WEAPON_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_WEAPON_ENCHANT_LEVEL = new HashMap<>();	
	public static HashMap<Integer, Integer> NORMAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_ARMOR_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_ARMOR_ENCHANT_LEVEL = new HashMap<>();	
	public static HashMap<Integer, Integer> NORMAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> BLESS_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> CRYSTAL_JEWELRY_ENCHANT_LEVEL = new HashMap<>();
	public static HashMap<Integer, Integer> DONATOR_JEWELRY_ENCHANT_LEVEL = new HashMap<>();	
	public static boolean ENCHANT_HERO_WEAPON;	
	public static boolean SCROLL_STACKABLE;	
	public static int GOLD_WEAPON;
	public static int GOLD_ARMOR;	
	public static int ENCHANT_SAFE_MAX;
	public static int ENCHANT_SAFE_MAX_FULL;	
	public static int ENCHANT_WEAPON_MAX;
	public static int ENCHANT_ARMOR_MAX;
	public static int ENCHANT_JEWELRY_MAX;	
	public static int BLESSED_ENCHANT_WEAPON_MAX;
	public static int BLESSED_ENCHANT_ARMOR_MAX;
	public static int BLESSED_ENCHANT_JEWELRY_MAX;	
	public static int BREAK_ENCHANT;	
	public static int CRYSTAL_ENCHANT_MIN;
	public static int CRYSTAL_ENCHANT_WEAPON_MAX;
	public static int CRYSTAL_ENCHANT_ARMOR_MAX;
	public static int CRYSTAL_ENCHANT_JEWELRY_MAX;
	public static int DONATOR_ENCHANT_MIN;
	public static int DONATOR_ENCHANT_WEAPON_MAX;
	public static int DONATOR_ENCHANT_ARMOR_MAX;
	public static int DONATOR_ENCHANT_JEWELRY_MAX;	
	public static boolean DONATOR_DECREASE_ENCHANT;
	public static int DONATOR_BREAK_ENCHANT;
	public static boolean DONATOR_LOST_ENCHANT;
	public static boolean ENABLE_ENCHANT_ANNOUNCE;
	public static String ENCHANT_ANNOUNCE_LEVEL;
	public static ArrayList<Integer> LIST_ENCHANT_ANNOUNCE_LEVEL = new ArrayList<>();
	public static int ALT_OLY_ENCHANT_LIMIT;
	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	//----------------------------------------
	//   events.properties
	//----------------------------------------
	/** Olympiad */
	public static boolean OLY_SKILL_PROTECT;
	public static List<Integer> OLY_SKILL_LIST = new ArrayList<>();	
	public static ArrayList<Integer> OLY_PROTECT_ITEMS = new ArrayList<>();	
	public static boolean OLLY_GRADE_A;	
	public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
	public static OlympiadPeriod ALT_OLY_PERIOD;
	public static int ALT_OLY_PERIOD_MULTIPLIER;	
	public static boolean OLYMPIAD_PERIOD;	
	public static boolean OLY_FIGHT;	
	public static boolean OLY_CLASSED_FIGHT;	
	public static List<int[]> CUSTOM_BUFFS_M = new ArrayList<>();
	public static List<int[]> CUSTOM_BUFFS_F = new ArrayList<>();
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_OLY_WAIT_BATTLE;
	public static int ALT_OLY_WAIT_END;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	
	/** SevenSigns Festival */
	public static boolean ALT_GAME_CASTLE_DAWN;
	public static boolean ALT_GAME_CASTLE_DUSK;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	
	/** Four Sepulchers */
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	/** Wedding system */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Lottery */
	public static int ALT_LOTTERY_PRIZE;
	public static int ALT_LOTTERY_TICKET_PRICE;
	public static double ALT_LOTTERY_5_NUMBER_RATE;
	public static double ALT_LOTTERY_4_NUMBER_RATE;
	public static double ALT_LOTTERY_3_NUMBER_RATE;
	public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static int COORD_SYNCHRONIZE;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static String PATHFIND_BUFFERS;
	public static int BASE_WEIGHT;
	public static int DIAGONAL_WEIGHT;
	public static int HEURISTIC_WEIGHT;
	public static int OBSTACLE_MULTIPLIER;
	public static int MAX_ITERATIONS;
	public static boolean DEBUG_PATH;
	public static boolean DEBUG_GEO_NODE;
	
	
	//----------------------------------------
	//   gm.properties
	//----------------------------------------
	/** GMs & Admin Stuff */
	public static boolean GM_STARTUP;
	public static int DEFAULT_ACCESS_LEVEL;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	
	//----------------------------------------
	//   npcs.properties
	//----------------------------------------
	/** Champion Mod */
	public static int CHAMPION_FREQUENCY;
	public static int CHAMP_MIN_LVL;
	public static int CHAMP_MAX_LVL;
	public static int CHAMPION_HP;
	public static int CHAMPION_REWARDS;
	public static int CHAMPION_ADENAS_REWARDS;
	public static double CHAMPION_HP_REGEN;
	public static double CHAMPION_ATK;
	public static double CHAMPION_SPD_ATK;
	public static int CHAMPION_REWARD;
	public static int CHAMPION_REWARD_ID;
	public static int CHAMPION_REWARD_QTY;
	
	/** Misc */
	public static boolean ALLOW_CLASS_MASTERS;
	public static boolean ALTERNATE_CLASS_MASTER;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	
	/** Wyvern Manager */
	public static boolean WYVERN_ALLOW_UPGRADER;
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int MAX_DRIFT_RANGE;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	
	public static boolean DISABLE_ATTACK_NPC_TYPE;
	
	public static String NPC_WITH_EFFECT;
	public static List<Integer> LIST_NPC_WITH_EFFECT = new ArrayList<>();
	
	//----------------------------------------
	//   options.properties
	//----------------------------------------
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	
	public static int DELETE_DAYS;
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean ALT_DEV_NO_SPAWNS;
	public static boolean DEBUG;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	public static String IP_PROTECTION;
	public static boolean IP_PROTECTION_CNFG;
	
	public static boolean BRUT_PROTECTION_ENABLED;
	
	public static boolean ANTI_LOGIN_FLOOD;
	public static int LOGIN_FLOOD_BLOCK_TIME;
	
	/** Deadlock Detector */
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean GMAUDIT;
	
	/** Community Board */
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	
	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;
	public static boolean DISABLE_TUTORIAL;
	
	/** Chat Filter **/
	public static int CHAT_FILTER_PUNISHMENT_PARAM1;
	public static int CHAT_FILTER_PUNISHMENT_PARAM2;
	public static int CHAT_FILTER_PUNISHMENT_PARAM3;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static String CHAT_FILTER_PUNISHMENT;
	public static ArrayList<String> FILTER_LIST = new ArrayList<>();
	
	//----------------------------------------
	//   party.properties
	//----------------------------------------
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_LEAVE_PARTY_LEADER;
	
	//----------------------------------------
	//   player.properties
	//----------------------------------------
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_CP;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALT_GAME_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;
	
	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double ALT_WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean ALT_GAME_FREIGHTS;
	public static int ALT_GAME_FREIGHT_PRICE;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	
	//----------------------------------------
	//  pvp.properties
	//----------------------------------------
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_BE_KILLED_IN_PZ;
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean PLAYER_FLAG_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	public static boolean KARMA_PLAYER_CAN_USE_WH;
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;	
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	public static boolean SHOW_HP_PVP;
	public static boolean ANNOUNCE_PVP_KILL;
	public static boolean ANNOUNCE_PK_KILL;
	public static List<String> LIST_OF_TYPES_PVP = new ArrayList<>();
	public static List<String> LIST_OF_TYPES_PK = new ArrayList<>();
	public static String ANNOUNCE_TYPE_MSG_PVP;
	public static String ANNOUNCE_TYPE_MSG_PK;
	public static boolean ENABLE_PVP_COLOR;
	public static boolean ENABLE_PK_COLOR;
	public static boolean ALLOW_QUAKE_SYSTEM;
	public static int PVP_COLOR_ANNOUNCE;
	public static boolean PVP_REWARD_ENABLED;
	public static int PVP_REWARD_ID;
	public static int PVP_REWARD_AMOUNT;
	public static boolean PK_REWARD_ENABLED;
	public static int PK_REWARD_ID;
	public static int PK_REWARD_AMOUNT;
	public static boolean ANTI_FARM_ENABLED;
	public static boolean ANTI_FARM_CLAN_ALLY_ENABLED;
	public static boolean ANTI_FARM_LVL_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_LVL_DIFF;
	public static boolean ANTI_FARM_PDEF_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_PDEF_DIFF;
	public static boolean ANTI_FARM_PATK_DIFF_ENABLED;
	public static int ANTI_FARM_MAX_PATK_DIFF;
	public static boolean ANTI_FARM_PARTY_ENABLED;
	public static boolean ANTI_FARM_IP_ENABLED;
	public static boolean ANTI_FARM_SUMMON;
	public static boolean LEAVE_BUFFS_ON_DIE;
	public static boolean CUSTOM_TELEGIRAN_ON_DIE;
	
	//----------------------------------------
	//   rates.properties
	//----------------------------------------
	/** Rate control */
	public static float RATE_DROP_SEAL_STONES;
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_ADENA;
	public static double RATE_CONSUMABLE_COST;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	public static double RATE_DROP_COMMON_HERBS;
	public static double RATE_DROP_HP_HERBS;
	public static double RATE_DROP_MP_HERBS;
	public static double RATE_DROP_SPECIAL_HERBS;
	
	//----------------------------------------
	//   siege.properties
	//----------------------------------------
	/** Sieges */
	public static boolean ENABLE_WINNNER_REWARD_SIEGE_CLAN;
	public static int[][] REWARD_WINNER_SIEGE_CLAN;
	public static int[][] LEADER_REWARD_WINNER_SIEGE_CLAN;
	public static int SIEGE_LENGTH;
	public static int MINIMUM_CLAN_LEVEL;
	public static int MAX_ATTACKERS_NUMBER;
	public static int MAX_DEFENDERS_NUMBER;
	public static int ATTACKERS_RESPAWN_DELAY;
	public static int DAY_TO_SIEGE;
	public static int SIEGE_HOUR_GLUDIO;
	public static int SIEGE_HOUR_DION;
	public static int SIEGE_HOUR_GIRAN;
	public static int SIEGE_HOUR_OREN;
	public static int SIEGE_HOUR_ADEN;
	public static int SIEGE_HOUR_INNADRIL;
	public static int SIEGE_HOUR_GODDARD;
	public static int SIEGE_HOUR_RUNE;
	public static int SIEGE_HOUR_SCHUT;
	public static boolean SIEGE_GLUDIO;
	public static boolean SIEGE_DION;
	public static boolean SIEGE_GIRAN;
	public static boolean SIEGE_OREN;
	public static boolean SIEGE_ADEN;
	public static boolean SIEGE_INNADRIL;
	public static boolean SIEGE_GODDARD;
	public static boolean SIEGE_RUNE;
	public static boolean SIEGE_SCHUT;
	
	public static int SIEGE_DAY_GLUDIO;
	public static int SIEGE_DAY_DION;
	public static int SIEGE_DAY_GIRAN;
	public static int SIEGE_DAY_OREN;
	public static int SIEGE_DAY_ADEN;
	public static int SIEGE_DAY_INNADRIL;
	public static int SIEGE_DAY_GODDARD;
	public static int SIEGE_DAY_RUNE;
	public static int SIEGE_DAY_SCHUT;
	
	
	//----------------------------------------
	//   skills.properties
	//----------------------------------------
	/** Skills & Classes **/
	public static boolean ALT_SHOW_REUSE_MSG;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static int BUFFS_MAX_AMOUNT;
	
	/** Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	public static String FIGHTER_BUFF;
	public static ArrayList<Integer> FIGHTER_BUFF_LIST = new ArrayList<>();
	public static String MAGE_BUFF;
	public static ArrayList<Integer> MAGE_BUFF_LIST = new ArrayList<>();
	public static String PROTECT_BUFF;
	public static ArrayList<Integer> PROTECT_BUFF_LIST = new ArrayList<>();
	public static int HEROIC_COIN_ID;
	public static int HEROIC_COIN_PRICE;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static HashMap<Integer, Integer> SKILL_DURATION_LIST;
	public static int ALLOWED_SUBCLASS;
	public static boolean ADD_SKILL_NOBLES;
	public static boolean ALT_RESTORE_EFFECTS_ON_SUBCLASS_CHANGE;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	
	/** Check skill */
	public static boolean CHECK_SKILLS_ON_ENTER;
	public static String NON_CHECK_SKILLS;
	public static List<Integer> LIST_NON_CHECK_SKILLS = new ArrayList<>();
	public static boolean CHECK_NOBLE_SKILLS;
	public static boolean CHECK_HERO_SKILLS;
	
	//----------------------------------------
	//   l2jmega.properties
	//----------------------------------------
	
	/** Color item */
	public static boolean COLOR_WITH_ITEM;
	public static int COLOR_ITEM_ID;
	public static int COLOR_NAME_ITEM_AMOUNT;
	public static int COLOR_TITLE_ITEM_AMOUNT;
	
	/** Bank system golg bar */
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBAR_ID;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	
	/** offline trade */
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	
	
	/** Infinity SS and Arrows */
	public static boolean INFINITY_SS;	  
	public static boolean INFINITY_ARROWS;
	
	/** Observation cost */
	public static int ITEM_OBSERVER;
	public static int ITEM_BUY_QUANT_OBSERVER;
	
	/** Start vip for all pl for 24 hr */
	public static boolean START_VIP;
	
	public static boolean STATUS_CMD;
	
	/** Luck Box */
	public static int ITEM_1;
	public static int CHANCE_ITEM_1;
	public static int ITEM_QUANT_1;
	public static int ITEM_2;
	public static int CHANCE_ITEM_2;
	public static int ITEM_QUANT_2;
	public static int ITEM_3;
	public static int CHANCE_ITEM_3;
	public static int ITEM_QUANT_3;
	public static int ITEM_4;
	public static int CHANCE_ITEM_4;
	public static int ITEM_QUANT_4;
	public static int ITEM_5;
	public static int CHANCE_ITEM_5;
	public static int ITEM_QUANT_5;
	public static int ITEM_6;
	public static int CHANCE_ITEM_6;
	public static int ITEM_QUANT_6;
	public static int ITEM_7;
	public static int CHANCE_ITEM_7;
	public static int ITEM_QUANT_7;
	public static int ITEM_8;
	public static int CHANCE_ITEM_8;
	public static int ITEM_QUANT_8;
	public static int ITEM_9;
	public static int CHANCE_ITEM_9;
	public static int ITEM_QUANT_9;
	public static int ITEM_10;
	public static int CHANCE_ITEM_10;
	public static int ITEM_QUANT_10;
	public static int ITEM_11;
	public static int CHANCE_ITEM_11;
	public static int ITEM_QUANT_11;
	
	/** Msg Chat VIP */
	public static String MSG_CHAT_TRADE;
	public static boolean ENABLE_CHAT_VIP;
	
	/** Hero subclass */
	public static boolean HERO_SKILL_SUB;
	
	/** server in available */
	public static boolean ALLOW_MANUTENCAO;
	public static String MANUTENCAO_TEXT;
	
	/** Char config */
	public static boolean CHAR_TITLE;
	public static String ADD_CHAR_TITLE;
	public static String ADD_NAME_COLOR;
	public static String ADD_TITLE_COLOR;
	public static boolean SPAWN_CHAR;
	public static boolean SPAWN_CHAR_STARTUP;
	public static int SPAWN_X_1;
	public static int SPAWN_Y_1;
	public static int SPAWN_Z_1;
	public static int SPAWN_X_2;
	public static int SPAWN_Y_2;
	public static int SPAWN_Z_2;
	public static int SPAWN_X_3;
	public static int SPAWN_Y_3;
	public static int SPAWN_Z_3;
	public static int SPAWN_X_4;
	public static int SPAWN_Y_4;
	public static int SPAWN_Z_4;
	public static boolean ALLOW_CREATE_LVL;
	public static int CHAR_CREATE_LVL;
	public static int CUSTOM_SUBCLASS_LVL;
	public static boolean CUSTOM_STARTER_ITEMS_ENABLED;
	public static List<int[]> STARTING_CUSTOM_ITEMS_F = new ArrayList<>();
	public static List<int[]> STARTING_CUSTOM_ITEMS_M = new ArrayList<>();
	
	/** Chat config */
	public static boolean DISABLE_CHAT;
	public static boolean DISABLE_CAPSLOCK;
	public static boolean TRADE_CHAT_WITH_PVP;
	public static int TRADE_PVP_AMOUNT;
	public static boolean GLOBAL_CHAT_WITH_PVP;
	public static int GLOBAL_PVP_AMOUNT;
	public static int CUSTOM_GLOBAL_CHAT_TIME;
	public static int CUSTOM_HERO_CHAT_TIME;
	public static boolean TALK_CHAT_ALL_CONFIG;
	public static int TALK_CHAT_ALL_TIME;
	
	/** Protect zone */
	public static boolean WYVERN_PVPZONE;
	public static boolean PARTY_PVPZONE;
	public static boolean PARTY_FLAGZONE;
	
	/** Open url client */
	public static boolean OPEN_URL_ENABLE;
	public static String OPEN_URL_SITE;
	
	/** Dragon protection zone */
	public static boolean WYVERN_PROTECTION;
	public static String ID_RESTRICT;
	public static List<Integer> LISTID_RESTRICT;
	
	/** custom droplist */
	public static String CUSTOM_DROP_ITEM;
	public static List<Integer> CUSTOM_DROP_LIST;
	public static String CUSTOM_MONSTER;
	public static List<Integer> LIST_NPC_CUSTOM_MONSTER = new ArrayList<>();
	public static List<int[]> CUSTOM_MONSTER_DROP = new ArrayList<>();
	public static int DROP_PROTECTED_TIME;
	public static boolean CUSTOM_DROP;
	
	/** Penalty config */
	public static boolean DISABLE_GRADE_PENALTY;
	public static boolean DISABLE_WEIGHT_PENALTY;
	
	/** CLEAN SECOND LOG */
	public static boolean RESET_DAILY_ENABLED;
	public static String[] RESET_DAILY_TIME;
	public static boolean RESTART_BY_TIME_OF_DAY;
	public static int RESTART_SECONDS;
	public static String[] RESTART_INTERVAL_BY_TIME_OF_DAY;
	
	//----------------------------------------
	//   announce.properties
	//----------------------------------------
	/** Announce castle lord */
	public static boolean ANNOUNCE_CASTLE_LORDS;
	public static boolean ANNOUNCE_HALL_OWNERS;
	public static boolean ANNOUNCE_HERO_ON_ENTER;
	public static boolean ANNOUNCE_HERO_ON_CUSTOM;
	
	//----------------------------------------
	//   Skills_chance_modifier.properties
	//----------------------------------------
	/** Chance modifier skills */
	public static boolean ENABLE_CUSTOM_CHANCE_SKILL;
	public static float SURRENDER_TO_FIRE;
	public static float VORTEX_TO_FIRE;	
	public static float SURRENDER_TO_WIND;
	public static float WIND_VORTEX;	
	public static float CURSE_GLOOM;
	public static float DARK_VORTEX;
	public static float SURRENDER_TO_WATER;
	public static float ICE_VORTEX;
	public static float LIGHT_VORTEX;
	public static float SILENCE;
	public static float SLEEP;
	public static float CURSE_FEAR;
	public static float ANCHOR;
	public static float CURSE_OF_DOOM;
	public static float CURSE_OF_ABYSS;
	public static float CANCELLATION;
	public static float MASS_SLOW;
	public static float MASS_FEAR;
	public static float MASS_GLOOM;
	public static float SLEEPING_CLOUD;
	public static float HEROIC_GRANDEUR;
	public static float HEROIC_DREAD;
	public static float STUNNING_SHOT;	
	public static float HEX;
	public static float SHOCK_STOMP;
	public static float THUNDER_STORM;
	public static float SHIELD_STUN;
	public static float SHIELD_SLAM;
	public static float SHACKLE;
	public static float MASS_SHACKLING;
	public static float ARREST;
	public static float BLUFF;
	public static float SWITCH;
	public static float STUNNING_FIST;
	public static float FEAR_OVER;
	public static float SEAL_OF_SILENCE;
	public static float SEAL_OF_SUSPENSION;
	public static float STUN_ATTACK;
	public static float ARMOR_CRUSH;
	public static float SLOW;
	public static float SEAL_OF_DESPAIR;
	public static float DREAMING_SPIRIT;
	public static float SEAL_OF_BINDING;
	public static float MASS_WARRIOR_BANE;
	public static float MASS_MAGE_BANE;
	public static float SHIELD_BASH;
	public static float SHOCK_BLAST;
	
	//----------------------------------------
	//   physics.properties
	//----------------------------------------
	/** Dagger Skills */
	public static int BLOW_ATTACK_FRONT;	  
	public static int BLOW_ATTACK_SIDE;	  
	public static int BLOW_ATTACK_BEHIND;	  
	public static int BACKSTAB_ATTACK_FRONT;	  
	public static int BACKSTAB_ATTACK_SIDE;	  
	public static int BACKSTAB_ATTACK_BEHIND;
	/** Anti bug SS */
	public static int ANTI_SS_BUG_1;
	public static int ANTI_SS_BUG_2;
	public static int ANTI_SS_BUG_3;
	
	/** Physics Config */
	public static int ADD_MATKSPEED;
	public static int ADD_ATKSPEED;
	public static int ADD_HP;
	public static int ADD_MP;
	public static int ADD_CP;
	
	public static int MAX_PATK_SPEED;
	public static int MAX_PATK_SPEED_GHOST;
	public static int MAX_PATK_SPEED_MOONL;
	public static int MAX_MATK_SPEED;
	public static boolean RANDOM_DAMAGE;
	public static boolean ENABLE_CUSTOM_CRIT_POWER;
	public static float MAGIC_CRITICAL_POWER;
	public static float FIGHTER_CRITICAL_POWER;
	public static float GHOSTSENTINEL_CRITICAL_POWER;
	public static boolean ENABLE_CUSTOM_ALL_DAMAGE;
	public static float ALT_MAGES_MAGICAL_DAMAGE_MULTI;
	public static float ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
	
	public static boolean ENABLE_CUSTOM_CRIT;
	public static int MAX_PCRIT_RATE;
	public static int PCRIT_RATE_ArcherHuman;
	public static int PCRIT_RATE_ArcherElfo;
	public static int PCRIT_RATE_ArcherDarkElfo;
	
	public static int MCRIT_RATE_Archmage;
	public static int MCRIT_RATE_Soultaker;
	public static int MCRIT_RATE_Mystic_Muse;
	public static int MCRIT_RATE_Storm_Screamer;
	public static int MCRIT_RATE_Dominator;
	public static int MAX_MCRIT_RATE;
	
	public static boolean ENABLE_CUSTOM_DAMAGE_MONSTER;
	public static float ALT_FIGHTER_TO_MONSTER;
	public static float ALT_MAGE_TO_MONSTER;
	public static String MONSTER_CUSTOM_DANO;
	public static ArrayList<Integer> LIST_MONSTER_CUSTOM_DANO_IDS = new ArrayList<>();
	public static float ALT_FIGHTER_TO_MONSTER_2;
	public static float ALT_MAGE_TO_MONSTER_2;
	public static float ALT_FIGHTER_TO_RAIDBOSS;
	public static float ALT_MAGE_TO_RAIDBOSS;
	public static String RAID_CUSTOM_DANO;
	public static ArrayList<Integer> LIST_RAID_CUSTOM_DANO_IDS = new ArrayList<>();
	public static float ALT_FIGHTER_TO_BOSS_2;
	public static float ALT_MAGE_TO_BOSS_2;
	public static float ALT_FIGHTER_TO_GRANDBOSS;
	public static float ALT_MAGE_TO_GRANDBOSS;
	public static float ALT_FIGHTER_TO_BOSS_NIGHT;
	public static float ALT_MAGE_TO_BOSS_NIGHT;
	public static String CUSTOM_BOSS_TASK;
	public static ArrayList<String> CUSTOM_BOSS_TASK_LIST = new ArrayList<>();	
	//----------------------------------------
	//   physics_inOly.properties
	//----------------------------------------
	public static boolean OLY_ENABLE_CUSTOM_CRIT_POWER;
	public static float OLY_MAGIC_CRITICAL_POWER;
	public static float OLY_FIGHTER_CRITICAL_POWER;
	public static float OLY_GHOSTSENTINEL_CRITICAL_POWER;
	public static boolean OLY_ENABLE_CUSTOM_ALL_DAMAGE;
	public static float OLY_ALT_MAGES_MAGICAL_DAMAGE_MULTI;
	public static float OLY_ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
	public static boolean OLY_ENABLE_CUSTOM_CRIT;
	public static int OLY_MAX_PCRIT_RATE;
	public static int OLY_PCRIT_RATE_ArcherHuman;
	public static int OLY_PCRIT_RATE_ArcherElfo;
	public static int OLY_PCRIT_RATE_ArcherDarkElfo;
	public static int OLY_MCRIT_RATE_Archmage;
	public static int OLY_MCRIT_RATE_Soultaker;
	public static int OLY_MCRIT_RATE_Mystic_Muse;
	public static int OLY_MCRIT_RATE_Storm_Screamer;
	public static int OLY_MCRIT_RATE_Dominator;
	public static int OLY_MAX_MCRIT_RATE;
	public static List<Integer> OLYMPIAD_SKILL_LIST_DANO = new ArrayList<>();
	public static int OLYMPIAD_SKILL_DANO;
	public static float ALT_PETS_PHYSICAL_DAMAGE_MULTI;
	public static float ALT_PETS_MAGICAL_DAMAGE_MULTI;
	
	//----------------------------------------
	//   tvt.properties
	//----------------------------------------
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_SKILL_PROTECT;
	public static List<Integer> TVT_SKILL_LIST = new ArrayList<>();
	public static boolean DEBUG_TVT;
	public static String TVT_EVEN_TEAMS;
	public static boolean TVT_ALLOW_INTERFERENCE;
	public static boolean TVT_ALLOW_POTIONS;
	public static boolean TVT_ALLOW_SUMMON;
	public static boolean TVT_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean TVT_ON_START_UNSUMMON_PET;
	public static boolean TVT_REVIVE_RECOVERY;
	public static boolean TVT_ANNOUNCE_TEAM_STATS;
	public static boolean TVT_ANNOUNCE_REWARD;
	public static boolean TVT_ANNOUNCE_LVL;
	public static boolean TVT_PRICE_NO_KILLS;
	public static boolean TVT_JOIN_CURSED;
	public static boolean TVT_COMMAND;
	public static long TVT_REVIVE_DELAY;
	public static boolean TVT_OPEN_FORT_DOORS;
	public static boolean TVT_CLOSE_FORT_DOORS;
	public static boolean TVT_OPEN_ADEN_COLOSSEUM_DOORS;
	public static boolean TVT_CLOSE_ADEN_COLOSSEUM_DOORS;
	public static int TVT_TOP_KILLER_REWARD;
	public static int TVT_TOP_KILLER_QTY;
	public static boolean TVT_AURA;
	public static boolean TVT_STATS_LOGGER;
	public static boolean Allow_Same_HWID_On_tvt;
	public static boolean SCREN_MSG;
	public static int TVT_OBSERVER_X;
	public static int TVT_OBSERVER_Y;
	public static int TVT_OBSERVER_Z;
	
	//----------------------------------------
	//   ctf.properties
	//----------------------------------------
	public static boolean CTF_EVENT_ENABLED;
	public static String CTF_EVEN_TEAMS;
	public static boolean CTF_ALLOW_INTERFERENCE;
	public static boolean CTF_ALLOW_POTIONS;
	public static boolean CTF_ALLOW_SUMMON;
	public static boolean CTF_ON_START_REMOVE_ALL_EFFECTS;
	public static boolean CTF_ON_START_UNSUMMON_PET;
	public static boolean CTF_ANNOUNCE_TEAM_STATS;
	public static boolean CTF_ANNOUNCE_REWARD;
	public static boolean CTF_JOIN_CURSED;
	public static boolean CTF_REVIVE_RECOVERY;
	public static boolean CTF_COMMAND;
	public static long CTF_REVIVE_DELAY;
	public static boolean CTF_AURA;
	public static boolean CTF_STATS_LOGGER;
	public static int CTF_SPAWN_OFFSET;
	public static boolean CTF_REMOVE_BUFFS_ON_DIE;
	public static boolean Allow_Same_HWID_On_ctf;
	public static boolean CTF_ANNOUNCE_LVL;
	public static int CTF_REWARD_TIE;
	public static int CTF_REWARD_TIE_AMOUNT;
	public static List<Integer> CTF_SKILL_LIST = new ArrayList<>();
	public static int CTF_OBSERVER_X;
	public static int CTF_OBSERVER_Y;
	public static int CTF_OBSERVER_Z;
	
	//----------------------------------------
	//   tournament.properties
	//----------------------------------------
	public static boolean TOUR_GRADE_A_1X1;
	public static boolean TOUR_GRADE_A_2X2;
	public static boolean TOUR_GRADE_A_5X5;
	public static boolean TOUR_GRADE_A_9X9;
	public static int ALT_TOUR_ENCHANT_LIMIT;	
	public static boolean TOURNAMENT_EVENT_START;
	public static boolean TOURNAMENT_EVENT_TIME;
	public static boolean TOURNAMENT_EVENT_ANNOUNCE;
	public static boolean TOURNAMENT_EVENT_SUMMON;
	public static int TOURNAMENT_TIME;
	public static String[] TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY;	
	public static String TITLE_COLOR_TEAM1;
	public static String TITLE_COLOR_TEAM2;	
	public static String MSG_TEAM1;
	public static String MSG_TEAM2;	
	public static boolean Allow_Same_HWID_On_Tournament;
	public static int ARENA_NPC;
	public static int NPC_locx;
	public static int NPC_locy;
	public static int NPC_locz;
	public static int NPC_Heading;
	public static int NPC_locx2;
	public static int NPC_locy2;
	public static int NPC_locz2;
	public static int NPC_Heading2;
	public static int Tournament_locx;
	public static int Tournament_locy;
	public static int Tournament_locz;
	public static boolean ALLOW_1X1_REGISTER;
	public static boolean ALLOW_2X2_REGISTER;
	public static boolean ALLOW_5X5_REGISTER;
	public static boolean ALLOW_9X9_REGISTER;
	public static boolean ALLOW_5X5_LOSTBUFF;
	public static boolean ARENA_MESSAGE_ENABLED;
	public static String ARENA_MESSAGE_TEXT;
	public static int ARENA_MESSAGE_TIME;
	public static int ARENA_EVENT_COUNT_1X1;
	public static int[][] ARENA_EVENT_LOCS_1X1;
	public static int ARENA_EVENT_COUNT;
	public static int[][] ARENA_EVENT_LOCS;
	public static int ARENA_EVENT_COUNT_5X5;
	public static int[][] ARENA_EVENT_LOCS_5X5;
	public static int ARENA_EVENT_COUNT_9X9;
	public static int[][] ARENA_EVENT_LOCS_9X9;
	public static int duelist_COUNT_5X5;
	public static int dreadnought_COUNT_5X5;
	public static int tanker_COUNT_5X5;
	public static int dagger_COUNT_5X5;
	public static int archer_COUNT_5X5;
	public static int bs_COUNT_5X5;
	public static int archmage_COUNT_5X5;
	public static int soultaker_COUNT_5X5;
	public static int mysticMuse_COUNT_5X5;
	public static int stormScreamer_COUNT_5X5;
	public static int titan_COUNT_5X5;
	public static int dominator_COUNT_5X5;
	public static int duelist_COUNT_9X9;
	public static int dreadnought_COUNT_9X9;
	public static int tanker_COUNT_9X9;
	public static int dagger_COUNT_9X9;
	public static int archer_COUNT_9X9;
	public static int bs_COUNT_9X9;
	public static int archmage_COUNT_9X9;
	public static int soultaker_COUNT_9X9;
	public static int mysticMuse_COUNT_9X9;
	public static int stormScreamer_COUNT_9X9;
	public static int titan_COUNT_9X9;
	public static int dominator_COUNT_9X9;
	public static int ARENA_PVP_AMOUNT;
	public static int ARENA_REWARD_ID_1X1;
	public static int ARENA_WIN_REWARD_COUNT_1X1;
	public static int ARENA_LOST_REWARD_COUNT_1X1;
	public static int ARENA_REWARD_ID;
	public static int ARENA_WIN_REWARD_COUNT;
	public static int ARENA_LOST_REWARD_COUNT;
	public static int ARENA_WIN_REWARD_COUNT_5X5;
	public static int ARENA_LOST_REWARD_COUNT_5X5;
	public static int ARENA_WIN_REWARD_COUNT_9X9;
	public static int ARENA_LOST_REWARD_COUNT_9X9;
	public static int ARENA_CHECK_INTERVAL_1X1;
	public static int ARENA_CALL_INTERVAL_1X1;
	public static int ARENA_WAIT_INTERVAL_1X1;
	public static int ARENA_CHECK_INTERVAL;
	public static int ARENA_CALL_INTERVAL;
	public static int ARENA_WAIT_INTERVAL_5X5;
	public static int ARENA_WAIT_INTERVAL_9X9;
	public static int ARENA_WAIT_INTERVAL;
	public static String TOURNAMENT_ID_RESTRICT;
	public static List<Integer> TOURNAMENT_LISTID_RESTRICT;
	public static boolean ARENA_SKILL_PROTECT;
	public static List<Integer> ARENA_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_DISABLE_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_STOP_SKILL_LIST = new ArrayList<>();
	public static List<Integer> ARENA_DISABLE_SKILL_LIST_PERM = new ArrayList<>();
	public static int PARTY_SEARCH_5X5_TIME;
	public static int PARTY_SEARCH_9X9_TIME;
	public static boolean ARENA_PROTECT;
	
	//----------------------------------------
	//   PartyZone.properties
	//----------------------------------------
	/** Party zone */	
	public static String NAME_EVENT;
	public static List<RewardHolder> PARTY_ZONE_REWARDS = new ArrayList<>();
	public static List<RewardHolder> PARTY_ZONE_REWARDS2 = new ArrayList<>();
	public static int RADIUS_TO_PARTYZONE;
	/** Party Farm Event */
	public static boolean START_PARTY;
	public static boolean START_AUTO_PARTY;
	public static int PARTYZONE_TIME;
	public static String[] PARTYZONE_INTERVAL_BY_TIME_OF_DAY;
	public static int PARTY_FARM_MONSTER_DALAY;
	public static String PARTY_FARM_MESSAGE_TEXT;
	public static int PARTY_FARM_MESSAGE_TIME;
	public static boolean OPEN_DOORS_PARTY_FARM;
	public static ArrayList<Integer> PARTY_OPEN_DOORS = new ArrayList<>();
	public static boolean CLOSE_DOORS_PARTY_FARM;
	public static ArrayList<Integer> PARTY_CLOSE_DOORS = new ArrayList<>();
	public static int monsterId;
	public static int MONSTER_LOCS_COUNT;
	public static int[][] MONSTER_LOCS;
	
	//----------------------------------------
	//   PhantomPlayers.properties
	//----------------------------------------
	public static boolean WALK_PHANTOM_TOWN = false;
	public static boolean WALK_PHANTOM_TOWN2 = false;
	public static boolean ALLOW_PHANTOM_PLAYERS = false;
	public static boolean ALLOW_PHANTOM_PLAYERS_FARM = false;
	public static boolean ALLOW_PHANTOM_PLAYERS_MASS_PVP = false;
	public static boolean ALLOW_PHANTOM_PLAYERS_TVT = false;
	public static boolean ALLOW_PHANTOM_PLAYERS_PVPEVENT = false;
	public static boolean ALLOW_PHANTOM_PLAYERS_EFFECT_SHOT = false;
	public static int PHANTOM_CHANCE_SIT;
	
	public static long PHANTOM_DISCONNETC_DELAY;
	public static long FARM_DISCONNETC_DELAY;
	public static long DISCONNETC_DELAY;
	public static boolean PHANTOM_PLAYERS_SOULSHOT_ANIM;
	public static boolean PHANTOM_PLAYERS_ARGUMENT_ANIM;
	public static int PHANTOM_ATK_SPEED;
	public static int PHANTOM_SPEED;
	public static int PHANTOM_PLAYERS_ENCHANT_MIN;
	public static int PHANTOM_PLAYERS_ENCHANT_MAX;
	public static String NAME_COLOR;
	public static String TITLE_COLOR;
	public static String PHANTOM_NAME_CLOLORS;
	public static String PHANTOM_TITLE_CLOLORS;
	public static ArrayList<String> PHANTOM_PLAYERS_NAME_CLOLORS = new ArrayList<>();
	public static ArrayList<String> PHANTOM_PLAYERS_TITLE_CLOLORS = new ArrayList<>();
	public static boolean PHANTOM_TITLE_PHANTOM;
	public static boolean PHANTOM_TITLE_PHANTOM_ATK;
	public static boolean PHANTOM_TITLE_CONFIG;
	public static String PHANTOM_TITLE_MSG;
	public static List<String> PHANTOM_TITLE = new ArrayList<>();
	public static boolean ALLOW_PHANTOM_FACE;
	public static String PHANTOM_FACE;
	public static List<Integer> LIST_PHANTOM_FACE;
	public static boolean ALLOW_PHANTOM_HAIR;
	public static String PHANTOM_HAIR;
	public static List<Integer> LIST_PHANTOM_HAIR;
	
	public static int PHANTOM_RANDOM_WALK;
	public static int PHANTOM_MAGE_RANDOM_WALK;
	public static int PHANTOM_MAGE_INTERVAL_WALK;
	public static int PHANTOM_MAGE_INTERVAL_TARGET;
	public static int PHANTOM_MAGE_INTERVAL_CHECK_TARGET;
	public static int PHANTOM_MAGE_RANGE;
	public static int PHANTOM_SURRENDER_INTERVAL;
	public static int FARM_RANGE;
	public static int POWER_PHANTOM;
	public static int PDEF_PHANTOM;
	public static int MDEF_PHANTOM;
	public static int ATKSPEED_PHANTOM;
	public static float POWER_PHANTOM_ARCHER;
	public static int PHANTOM_ARCHER_CRITICO_CHANCE;
	public static int CHANCE_MOVE_ARCHER;
	public static int PDEF_PHANTOM_ARCHER;
	public static int MDEF_PHANTOM_ARCHER;
	
	public static boolean ALLOW_PHANTOM_RETAIL_ARMOR = false;
	
	public static int PHANTOM_ARCHMAGE_PERCENTAGE;
	public static int PHANTOM_ARCHMAGE_DANO_INTERVAL;
	public static int PHANTOM_ARCHMAGE_EFFECT;
	public static int PHANTOM_SPELLSINGER_PERCENTAGE;
	public static int PHANTOM_SPELLSINGER_DANO_INTERVAL;
	public static int PHANTOM_SPELLSINGER_EFFECT;
	public static int PHANTOM_SPELLHOLLER_PERCENTAGE;
	public static int PHANTOM_SPELLHOLLER_DANO_INTERVAL;
	public static int PHANTOM_SPELLHOLLER_EFFECT;
	public static boolean ALLOW_PHANTOM_CREST;
	public static int PHANTOM_PLAYERS_CREST;
	public static boolean ALLOW_PHANTOM_CREST_ATK;
	public static int PHANTOM_PLAYERS_CREST_ATK;
	public static String CLAN_ID;
	public static List<Integer> LIST_CLAN_ID;
	public static int PHANTOM_CHANCE_MALARIA;
	public static int PHANTOM_CHANCE_TITLE;
	public static int COUNT_TOWN;
	public static int COUNT_FARM_ARCHMAGE;
	public static int COUNT_FARM_MYSTICMUSE;
	public static int COUNT_FARM_STORMSCREAM;
	public static int COUNT_PVP_ARCHMAGE;
	public static int COUNT_PVP_MYSTICMUSE;
	public static int COUNT_PVP_STORMSCREAM;
	public static int COUNT_TVT_ARCHMAGE;
	public static int COUNT_TVT_MYSTICMUSE;
	public static int COUNT_TVT_STORMSCREAM;
	
	//----------------------------------------
	//   PvPZone.properties
	//----------------------------------------
	public static int VOTEZONE_INIT;
	public static boolean VOTE_PVPZONE_ENABLED;	  
	public static boolean ENABLED_PVPZONE_KETRA;	  
	public static boolean ENABLED_PVPZONE_ILHA;
	public static boolean ENABLED_PVPZONE_HEINE;	  
	public static boolean ENABLED_PVPZONE_IRISLAKE;	  
	public static boolean ENABLED_PVPZONE_ALLIGATOR;	  
	public static boolean ENABLED_PVPZONE_IMPERIAL;	  
	public static boolean ENABLED_PVPZONE_WHISPERS;	  
	public static int VOTE_PVPZONE_MIN_VOTE;	  
	public static int VOTEZONE_RANDOM_RANGE;
	public static String[] VOTE_PVPZONE_INTERVAL_BY_TIME_OF_DAY; 
	public static int VOTE_PVPZONE_TIME;
	
	public static int PVPZONE_INIT;
	public static int ZONE_1X;
	public static int ZONE_1Y;
	public static int ZONE_1Z;
	public static int ZONE_2X;
	public static int ZONE_2Y;
	public static int ZONE_2Z;
	public static int ZONE_3X;
	public static int ZONE_3Y;
	public static int ZONE_3Z;
	public static int ZONE_4X;
	public static int ZONE_4Y;
	public static int ZONE_4Z;
	public static int ZONE_5X;
	public static int ZONE_5Y;
	public static int ZONE_5Z;
	public static int ZONE_6X;
	public static int ZONE_6Y;
	public static int ZONE_6Z;
	public static int ZONE_7X;
	public static int ZONE_7Y;
	public static int ZONE_7Z;
	public static int ZONE_8X;
	public static int ZONE_8Y;
	public static int ZONE_8Z;
	
	
	//----------------------------------------
	//   Protect.properties
	//----------------------------------------
	public static boolean BOSSZONE_HWID_PROTECT;	  
	public static int MAX_BOX_IN_BOSSZONE;	
	public static boolean FLAGZONE_HWID_PROTECT;
	public static int MAX_BOX_IN_FLAGZONE;	   
	public static boolean SOLOZONE_HWID_PROTECT;
	public static int MAX_BOX_IN_SOLOZONE;
	
	
	/**Variavel de nao equipar item muito rapido Inicio**/
	public static int USER_ITEM_TIME_WEAPON;
	/**Variavel de nao equipar item muito rapido Fim**/
	
	public static boolean ANTIZERG_RES;	
	
	public static boolean ANTZERG_CHECK_PARTY_INVITE;  
	public static int MAX_HEALER_PARTY;	  
	public static boolean ALLOW_HEALER_COUNT;
	public static boolean BOTS_PREVENTION;
	public static int KILLS_COUNTER;
	public static int KILLS_COUNTER_EXTRA;
	public static int EXTREME_CAPTCHA;
	public static boolean PVP_BOTS_PREVENTION;
	public static int PVP_KILLS_COUNTER;
	public static int PVP_KILLS_COUNTER_EXTRA;
	public static int VALIDATION_TIME;
	
	public static String NO_DELETE;
	public static List<Integer> LIST_NO_DELETE_ITEM;
	public static String NO_SELL;
	public static List<Integer> LIST_NO_SELL_ITEM;
	public static String NO_DEPOSITE;
	public static List<Integer> LIST_NO_DEPOSITE_ITEM;
	public static String NO_DROP;
	public static List<Integer> LIST_NO_DROP_ITEM;
	public static String NO_TRADE;
	public static List<Integer> LIST_NO_TRADE_ITEM;
	public static int ENCHANT_PROTECT;
	/* Remove equip during subclass change */
	public static boolean REMOVE_WEAPON;
	public static boolean REMOVE_CHEST;
	public static boolean REMOVE_LEG;
	public static boolean ALLOW_LIGHT_USE_HEAVY;
	public static String NOTALLOWCLASS;
	public static List<Integer> NOTALLOWEDUSEHEAVY;
	public static boolean ALLOW_HEAVY_USE_LIGHT;
	public static String NOTALLOWCLASSE;
	public static List<Integer> NOTALLOWEDUSELIGHT;
	public static boolean ALT_DISABLE_BOW_CLASSES_OLY;
	public static boolean ALT_DISABLE_BOW_CLASSES;
	public static String DISABLE_BOW_CLASSES_STRING;
	public static ArrayList<Integer> DISABLE_BOW_CLASSES = new ArrayList<>();
	public static boolean WARN_ITEM_ENABLED;
	public static boolean WARN_LIST_ITEM;
	public static List<int[]> PROTECT_ITEMS = new ArrayList<>();
	public static boolean WARN_ENCHANT_ITEM_ENABLED;
	public static int WARN_ENCHANT_LEVEL;
	public static boolean ALLOW_DUALBOX;
	public static int ALLOWED_BOXES;
	public static boolean ALLOW_DUALBOX_OLY;
	public static boolean MULTIBOX_PROTECTION_ENABLED;	  
	public static int MULTIBOX_PROTECTION_CLIENTS_PER_PC;
	public static String MAGE_ID_RESTRICT;
	public static List<Integer> MAGE_LISTID_RESTRICT;
	public static String FIGHTER_ID_RESTRICT;
	public static List<Integer> FIGHTER_LISTID_RESTRICT;
	
	//----------------------------------------
	//   Donate.properties
	//----------------------------------------
	/** donate */
	
	public static boolean DONATESYSTEM;
	public static boolean DONATE_HTML;
	public static int DONATE_COIN_ID;
	public static boolean VIP_SKINS;	
	public static boolean VIP_SKILL;
	public static boolean VIP_EFFECT;	
	public static boolean ENABLE_DROP_VIP;
	public static int VIP_XP_SP_RATES;
	public static String DROP_ITEM_VIP;
	public static List<Integer> DROP_LIST_VIP;
	public static int RATE_DROP_VIP;	
	public static int VIP_30_DAYS_PRICE;
	public static int VIP_60_DAYS_PRICE;
	public static int VIP_90_DAYS_PRICE;
	public static int VIP_ETERNAL_PRICE;
	public static int HERO_30_DAYS_PRICE;
	public static int HERO_60_DAYS_PRICE;
	public static int HERO_90_DAYS_PRICE;
	public static int HERO_ETERNAL_PRICE;
	
	public static int DONATE_ENCHANT_PRICE;
	public static int DONATE_NOBLE_PRICE;
	public static int DONATE_CLASS_PRICE;
	public static int DONATE_NAME_PRICE;
	public static int DONATE_SEX_PRICE;
	public static int DONATE_ITEM;
	public static int DONATE_ITEM_COUNT;
	public static int DONATE_ITEM_SKILL;
	public static int DONATE_ITEM_COUNT_SKILL;
	
	//----------------------------------------
	//   PcBang.properties
	//----------------------------------------
	public static boolean PCB_ENABLE;
	public static int PCB_MIN_LEVEL;
	public static int PCB_POINT_MIN;
	public static int PCB_POINT_MAX;
	public static int PCB_CHANCE_DUAL_POINT;
	public static int PCB_INTERVAL;
	
	// --------------------------------------------------
	// HexID
	// --------------------------------------------------
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// --------------------------------------------------
	// Loginserver
	// --------------------------------------------------
	
	public static String LOGIN_BIND_ADDRESS;
	public static int PORT_LOGIN;
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static boolean ACCEPT_NEW_GAMESERVER;
	public static boolean SHOW_LICENCE;
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static boolean LOG_LOGIN_CONTROLLER;
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// --------------------------------------------------
	// Server
	// --------------------------------------------------
	
	public static String GAMESERVER_HOSTNAME;
	public static int PORT_GAME;
	public static String EXTERNAL_HOSTNAME;
	public static String INTERNAL_HOSTNAME;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	
	/** Access to database */
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int MAXIMUM_ONLINE_USERS;
	
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;
	
	// --------------------------------------------------
	// Those "hidden" settings haven't configs to avoid admins to fuck their server
	// You still can experiment changing values here. But don't say I didn't warn you.
	// --------------------------------------------------
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false; // default false
	
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
	public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
	public static int MMO_MAX_READ_PER_PASS = 80; // default 80
	public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = 80; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = 70; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 15; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 120; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 5; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 5; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5	
	// --------------------------------------------------
	
	public static float Duelist_vs_DreadNought;
	public static float Duelist_vs_Phoenix_Knight;
	public static float Duelist_vs_Hell_Knight;
	public static float Duelist_vs_Sagittarius;
	public static float Duelist_vs_Eva_Templar;
	public static float Duelist_vs_Sword_Muse;
	public static float Duelist_vs_Moonlight_Sentinel;
	public static float Duelist_vs_Shillien_Templar;
	public static float Duelist_vs_Spectral_Dancer;
	public static float Duelist_vs_Ghost_Sentinel;
	public static float Duelist_vs_Titan;
	public static float Duelist_vs_Grand_Khauatari;
	public static float Duelist_vs_Fortune_Seeker;
	public static float Duelist_vs_Maestro;
	public static float Duelist_vs_Adventurer;
	public static float Duelist_vs_Wind_Rider;
	public static float Duelist_vs_Ghost_Hunter;
	public static float Duelist_vs_Archmage;
	public static float Duelist_vs_Soultaker;
	public static float Duelist_vs_Arcana_Lord;
	public static float Duelist_vs_Cardinal;
	public static float Duelist_vs_Hierophant;
	public static float Duelist_vs_Mystic_Muse;
	public static float Duelist_vs_Elemental_Master;
	public static float Duelist_vs_Eva_s_Saint;
	public static float Duelist_vs_Storm_Screamer;
	public static float Duelist_vs_Spectral_Master;
	public static float Duelist_vs_Shillien_Saint;
	public static float Duelist_vs_Dominator;
	public static float Duelist_vs_Doomcryer;
	
	public static float DreadNought_vs_Duelist;
	public static float DreadNought_vs_Phoenix_Knight;
	public static float DreadNought_vs_Hell_Knight;
	public static float DreadNought_vs_Sagittarius;
	public static float DreadNought_vs_Eva_Templar;
	public static float DreadNought_vs_Sword_Muse;
	public static float DreadNought_vs_Moonlight_Sentinel;
	public static float DreadNought_vs_Shillien_Templar;
	public static float DreadNought_vs_Spectral_Dancer;
	public static float DreadNought_vs_Ghost_Sentinel;
	public static float DreadNought_vs_Titan;
	public static float DreadNought_vs_Grand_Khauatari;
	public static float DreadNought_vs_Fortune_Seeker;
	public static float DreadNought_vs_Maestro;
	public static float DreadNought_vs_Adventurer;
	public static float DreadNought_vs_Wind_Rider;
	public static float DreadNought_vs_Ghost_Hunter;
	public static float DreadNought_vs_Archmage;
	public static float DreadNought_vs_Soultaker;
	public static float DreadNought_vs_Arcana_Lord;
	public static float DreadNought_vs_Cardinal;
	public static float DreadNought_vs_Hierophant;
	public static float DreadNought_vs_Mystic_Muse;
	public static float DreadNought_vs_Elemental_Master;
	public static float DreadNought_vs_Eva_s_Saint;
	public static float DreadNought_vs_Storm_Screamer;
	public static float DreadNought_vs_Spectral_Master;
	public static float DreadNought_vs_Shillien_Saint;
	public static float DreadNought_vs_Dominator;
	public static float DreadNought_vs_Doomcryer;
	
	public static float Phoenix_Knight_vs_Duelist;
	public static float Phoenix_Knight_vs_DreadNought;
	public static float Phoenix_Knight_vs_Hell_Knight;
	public static float Phoenix_Knight_vs_Sagittarius;
	public static float Phoenix_Knight_vs_Eva_Templar;
	public static float Phoenix_Knight_vs_Sword_Muse;
	public static float Phoenix_Knight_vs_Moonlight_Sentinel;
	public static float Phoenix_Knight_vs_Shillien_Templar;
	public static float Phoenix_Knight_vs_Spectral_Dancer;
	public static float Phoenix_Knight_vs_Ghost_Sentinel;
	public static float Phoenix_Knight_vs_Titan;
	public static float Phoenix_Knight_vs_Grand_Khauatari;
	public static float Phoenix_Knight_vs_Fortune_Seeker;
	public static float Phoenix_Knight_vs_Maestro;
	public static float Phoenix_Knight_vs_Adventurer;
	public static float Phoenix_Knight_vs_Wind_Rider;
	public static float Phoenix_Knight_vs_Ghost_Hunter;
	public static float Phoenix_Knight_vs_Archmage;
	public static float Phoenix_Knight_vs_Soultaker;
	public static float Phoenix_Knight_vs_Arcana_Lord;
	public static float Phoenix_Knight_vs_Cardinal;
	public static float Phoenix_Knight_vs_Hierophant;
	public static float Phoenix_Knight_vs_Mystic_Muse;
	public static float Phoenix_Knight_vs_Elemental_Master;
	public static float Phoenix_Knight_vs_Eva_s_Saint;
	public static float Phoenix_Knight_vs_Storm_Screamer;
	public static float Phoenix_Knight_vs_Spectral_Master;
	public static float Phoenix_Knight_vs_Shillien_Saint;
	public static float Phoenix_Knight_vs_Dominator;
	public static float Phoenix_Knight_vs_Doomcryer;
	
	public static float Hell_Knight_vs_Duelist;
	public static float Hell_Knight_vs_DreadNought;
	public static float Hell_Knight_vs_Phoenix_Knight;
	public static float Hell_Knight_vs_Sagittarius;
	public static float Hell_Knight_vs_Eva_Templar;
	public static float Hell_Knight_vs_Sword_Muse;
	public static float Hell_Knight_vs_Moonlight_Sentinel;
	public static float Hell_Knight_vs_Shillien_Templar;
	public static float Hell_Knight_vs_Spectral_Dancer;
	public static float Hell_Knight_vs_Ghost_Sentinel;
	public static float Hell_Knight_vs_Titan;
	public static float Hell_Knight_vs_Grand_Khauatari;
	public static float Hell_Knight_vs_Fortune_Seeker;
	public static float Hell_Knight_vs_Maestro;
	public static float Hell_Knight_vs_Adventurer;
	public static float Hell_Knight_vs_Wind_Rider;
	public static float Hell_Knight_vs_Ghost_Hunter;
	public static float Hell_Knight_vs_Archmage;
	public static float Hell_Knight_vs_Soultaker;
	public static float Hell_Knight_vs_Arcana_Lord;
	public static float Hell_Knight_vs_Cardinal;
	public static float Hell_Knight_vs_Hierophant;
	public static float Hell_Knight_vs_Mystic_Muse;
	public static float Hell_Knight_vs_Elemental_Master;
	public static float Hell_Knight_vs_Eva_s_Saint;
	public static float Hell_Knight_vs_Storm_Screamer;
	public static float Hell_Knight_vs_Spectral_Master;
	public static float Hell_Knight_vs_Shillien_Saint;
	public static float Hell_Knight_vs_Dominator;
	public static float Hell_Knight_vs_Doomcryer;
	
	public static float Sagittarius_vs_Duelist;
	public static float Sagittarius_vs_DreadNought;
	public static float Sagittarius_vs_Phoenix_Knight;
	public static float Sagittarius_vs_Hell_Knight;
	public static float Sagittarius_vs_Eva_Templar;
	public static float Sagittarius_vs_Sword_Muse;
	public static float Sagittarius_vs_Moonlight_Sentinel;
	public static float Sagittarius_vs_Shillien_Templar;
	public static float Sagittarius_vs_Spectral_Dancer;
	public static float Sagittarius_vs_Ghost_Sentinel;
	public static float Sagittarius_vs_Titan;
	public static float Sagittarius_vs_Grand_Khauatari;
	public static float Sagittarius_vs_Fortune_Seeker;
	public static float Sagittarius_vs_Maestro;
	public static float Sagittarius_vs_Adventurer;
	public static float Sagittarius_vs_Wind_Rider;
	public static float Sagittarius_vs_Ghost_Hunter;
	public static float Sagittarius_vs_Archmage;
	public static float Sagittarius_vs_Soultaker;
	public static float Sagittarius_vs_Arcana_Lord;
	public static float Sagittarius_vs_Cardinal;
	public static float Sagittarius_vs_Hierophant;
	public static float Sagittarius_vs_Mystic_Muse;
	public static float Sagittarius_vs_Elemental_Master;
	public static float Sagittarius_vs_Eva_s_Saint;
	public static float Sagittarius_vs_Storm_Screamer;
	public static float Sagittarius_vs_Spectral_Master;
	public static float Sagittarius_vs_Shillien_Saint;
	public static float Sagittarius_vs_Dominator;
	public static float Sagittarius_vs_Doomcryer;
	
	public static float Eva_Templar_vs_Duelist;
	public static float Eva_Templar_vs_DreadNought;
	public static float Eva_Templar_vs_Phoenix_Knight;
	public static float Eva_Templar_vs_Hell_Knight;
	public static float Eva_Templar_vs_Sagitarius;
	public static float Eva_Templar_vs_Sword_Muse;
	public static float Eva_Templar_vs_Moonlight_Sentinel;
	public static float Eva_Templar_vs_Shillien_Templar;
	public static float Eva_Templar_vs_Spectral_Dancer;
	public static float Eva_Templar_vs_Ghost_Sentinel;
	public static float Eva_Templar_vs_Titan;
	public static float Eva_Templar_vs_Grand_Khauatari;
	public static float Eva_Templar_vs_Fortune_Seeker;
	public static float Eva_Templar_vs_Maestro;
	public static float Eva_Templar_vs_Adventurer;
	public static float Eva_Templar_vs_Wind_Rider;
	public static float Eva_Templar_vs_Ghost_Hunter;
	public static float Eva_Templar_vs_Archmage;
	public static float Eva_Templar_vs_Soultaker;
	public static float Eva_Templar_vs_Arcana_Lord;
	public static float Eva_Templar_vs_Cardinal;
	public static float Eva_Templar_vs_Hierophant;
	public static float Eva_Templar_vs_Mystic_Muse;
	public static float Eva_Templar_vs_Elemental_Master;
	public static float Eva_Templar_vs_Eva_s_Saint;
	public static float Eva_Templar_vs_Storm_Screamer;
	public static float Eva_Templar_vs_Spectral_Master;
	public static float Eva_Templar_vs_Shillien_Saint;
	public static float Eva_Templar_vs_Dominator;
	public static float Eva_Templar_vs_Doomcryer;
	
	public static float Sword_Muse_vs_Duelist;
	public static float Sword_Muse_vs_DreadNought;
	public static float Sword_Muse_vs_Phoenix_Knight;
	public static float Sword_Muse_vs_Hell_Knight;
	public static float Sword_Muse_vs_Sagitarius;
	public static float Sword_Muse_vs_Eva_Templar;
	public static float Sword_Muse_vs_Moonlight_Sentinel;
	public static float Sword_Muse_vs_Shillien_Templar;
	public static float Sword_Muse_vs_Spectral_Dancer;
	public static float Sword_Muse_vs_Ghost_Sentinel;
	public static float Sword_Muse_vs_Titan;
	public static float Sword_Muse_vs_Grand_Khauatari;
	public static float Sword_Muse_vs_Fortune_Seeker;
	public static float Sword_Muse_vs_Maestro;
	public static float Sword_Muse_vs_Adventurer;
	public static float Sword_Muse_vs_Wind_Rider;
	public static float Sword_Muse_vs_Ghost_Hunter;
	public static float Sword_Muse_vs_Archmage;
	public static float Sword_Muse_vs_Soultaker;
	public static float Sword_Muse_vs_Arcana_Lord;
	public static float Sword_Muse_vs_Cardinal;
	public static float Sword_Muse_vs_Hierophant;
	public static float Sword_Muse_vs_Mystic_Muse;
	public static float Sword_Muse_vs_Elemental_Master;
	public static float Sword_Muse_vs_Eva_s_Saint;
	public static float Sword_Muse_vs_Storm_Screamer;
	public static float Sword_Muse_vs_Spectral_Master;
	public static float Sword_Muse_vs_Shillien_Saint;
	public static float Sword_Muse_vs_Dominator;
	public static float Sword_Muse_vs_Doomcryer;
	
	public static float Moonlight_Sentinel_vs_Duelist;
	public static float Moonlight_Sentinel_vs_DreadNought;
	public static float Moonlight_Sentinel_vs_Phoenix_Knight;
	public static float Moonlight_Sentinel_vs_Hell_Knight;
	public static float Moonlight_Sentinel_vs_Sagitarius;
	public static float Moonlight_Sentinel_vs_Eva_Templar;
	public static float Moonlight_Sentinel_vs_Sword_Muse;
	public static float Moonlight_Sentinel_vs_Shillien_Templar;
	public static float Moonlight_Sentinel_vs_Spectral_Dancer;
	public static float Moonlight_Sentinel_vs_Ghost_Sentinel;
	public static float Moonlight_Sentinel_vs_Titan;
	public static float Moonlight_Sentinel_vs_Grand_Khauatari;
	public static float Moonlight_Sentinel_vs_Fortune_Seeker;
	public static float Moonlight_Sentinel_vs_Maestro;
	public static float Moonlight_Sentinel_vs_Adventurer;
	public static float Moonlight_Sentinel_vs_Wind_Rider;
	public static float Moonlight_Sentinel_vs_Ghost_Hunter;
	public static float Moonlight_Sentinel_vs_Archmage;
	public static float Moonlight_Sentinel_vs_Soultaker;
	public static float Moonlight_Sentinel_vs_Arcana_Lord;
	public static float Moonlight_Sentinel_vs_Cardinal;
	public static float Moonlight_Sentinel_vs_Hierophant;
	public static float Moonlight_Sentinel_vs_Mystic_Muse;
	public static float Moonlight_Sentinel_vs_Elemental_Master;
	public static float Moonlight_Sentinel_vs_Eva_s_Saint;
	public static float Moonlight_Sentinel_vs_Storm_Screamer;
	public static float Moonlight_Sentinel_vs_Spectral_Master;
	public static float Moonlight_Sentinel_vs_Shillien_Saint;
	public static float Moonlight_Sentinel_vs_Dominator;
	public static float Moonlight_Sentinel_vs_Doomcryer;
	
	public static float Shillien_Templar_vs_Duelist;
	public static float Shillien_Templar_vs_DreadNought;
	public static float Shillien_Templar_vs_Phoenix_Knight;
	public static float Shillien_Templar_vs_Hell_Knight;
	public static float Shillien_Templar_vs_Sagitarius;
	public static float Shillien_Templar_vs_Eva_Templar;
	public static float Shillien_Templar_vs_Sword_Muse;
	public static float Shillien_Templar_vs_Moonlight_Sentinel;
	public static float Shillien_Templar_vs_Spectral_Dancer;
	public static float Shillien_Templar_vs_Ghost_Sentinel;
	public static float Shillien_Templar_vs_Titan;
	public static float Shillien_Templar_vs_Grand_Khauatari;
	public static float Shillien_Templar_vs_Fortune_Seeker;
	public static float Shillien_Templar_vs_Maestro;
	public static float Shillien_Templar_vs_Adventurer;
	public static float Shillien_Templar_vs_Wind_Rider;
	public static float Shillien_Templar_vs_Ghost_Hunter;
	public static float Shillien_Templar_vs_Archmage;
	public static float Shillien_Templar_vs_Soultaker;
	public static float Shillien_Templar_vs_Arcana_Lord;
	public static float Shillien_Templar_vs_Cardinal;
	public static float Shillien_Templar_vs_Hierophant;
	public static float Shillien_Templar_vs_Mystic_Muse;
	public static float Shillien_Templar_vs_Elemental_Master;
	public static float Shillien_Templar_vs_Eva_s_Saint;
	public static float Shillien_Templar_vs_Storm_Screamer;
	public static float Shillien_Templar_vs_Spectral_Master;
	public static float Shillien_Templar_vs_Shillien_Saint;
	public static float Shillien_Templar_vs_Dominator;
	public static float Shillien_Templar_vs_Doomcryer;
	
	public static float Spectral_Dancer_vs_Duelist;
	public static float Spectral_Dancer_vs_DreadNought;
	public static float Spectral_Dancer_vs_Phoenix_Knight;
	public static float Spectral_Dancer_vs_Hell_Knight;
	public static float Spectral_Dancer_vs_Sagitarius;
	public static float Spectral_Dancer_vs_Eva_Templar;
	public static float Spectral_Dancer_vs_Sword_Muse;
	public static float Spectral_Dancer_vs_Moonlight_Sentinel;
	public static float Spectral_Dancer_vs_Shillien_Templar;
	public static float Spectral_Dancer_vs_Ghost_Sentinel;
	public static float Spectral_Dancer_vs_Titan;
	public static float Spectral_Dancer_vs_Grand_Khauatari;
	public static float Spectral_Dancer_vs_Fortune_Seeker;
	public static float Spectral_Dancer_vs_Maestro;
	public static float Spectral_Dancer_vs_Adventurer;
	public static float Spectral_Dancer_vs_Wind_Rider;
	public static float Spectral_Dancer_vs_Ghost_Hunter;
	public static float Spectral_Dancer_vs_Archmage;
	public static float Spectral_Dancer_vs_Soultaker;
	public static float Spectral_Dancer_vs_Arcana_Lord;
	public static float Spectral_Dancer_vs_Cardinal;
	public static float Spectral_Dancer_vs_Hierophant;
	public static float Spectral_Dancer_vs_Mystic_Muse;
	public static float Spectral_Dancer_vs_Elemental_Master;
	public static float Spectral_Dancer_vs_Eva_s_Saint;
	public static float Spectral_Dancer_vs_Storm_Screamer;
	public static float Spectral_Dancer_vs_Spectral_Master;
	public static float Spectral_Dancer_vs_Shillien_Saint;
	public static float Spectral_Dancer_vs_Dominator;
	public static float Spectral_Dancer_vs_Doomcryer;
	
	public static float Ghost_Sentinel_vs_Duelist;
	public static float Ghost_Sentinel_vs_DreadNought;
	public static float Ghost_Sentinel_vs_Phoenix_Knight;
	public static float Ghost_Sentinel_vs_Hell_Knight;
	public static float Ghost_Sentinel_vs_Sagitarius;
	public static float Ghost_Sentinel_vs_Eva_Templar;
	public static float Ghost_Sentinel_vs_Sword_Muse;
	public static float Ghost_Sentinel_vs_Moonlight_Sentinel;
	public static float Ghost_Sentinel_vs_Shillien_Templar;
	public static float Ghost_Sentinel_vs_Spectral_Dancer;
	public static float Ghost_Sentinel_vs_Titan;
	public static float Ghost_Sentinel_vs_Grand_Khauatari;
	public static float Ghost_Sentinel_vs_Fortune_Seeker;
	public static float Ghost_Sentinel_vs_Maestro;
	public static float Ghost_Sentinel_vs_Adventurer;
	public static float Ghost_Sentinel_vs_Wind_Rider;
	public static float Ghost_Sentinel_vs_Ghost_Hunter;
	public static float Ghost_Sentinel_vs_Archmage;
	public static float Ghost_Sentinel_vs_Soultaker;
	public static float Ghost_Sentinel_vs_Arcana_Lord;
	public static float Ghost_Sentinel_vs_Cardinal;
	public static float Ghost_Sentinel_vs_Hierophant;
	public static float Ghost_Sentinel_vs_Mystic_Muse;
	public static float Ghost_Sentinel_vs_Elemental_Master;
	public static float Ghost_Sentinel_vs_Eva_s_Saint;
	public static float Ghost_Sentinel_vs_Storm_Screamer;
	public static float Ghost_Sentinel_vs_Spectral_Master;
	public static float Ghost_Sentinel_vs_Shillien_Saint;
	public static float Ghost_Sentinel_vs_Dominator;
	public static float Ghost_Sentinel_vs_Doomcryer;
	
	public static float Titan_vs_Duelist;
	public static float Titan_vs_DreadNought;
	public static float Titan_vs_Phoenix_Knight;
	public static float Titan_vs_Hell_Knight;
	public static float Titan_vs_Sagitarius;
	public static float Titan_vs_Eva_Templar;
	public static float Titan_vs_Sword_Muse;
	public static float Titan_vs_Moonlight_Sentinel;
	public static float Titan_vs_Shillien_Templar;
	public static float Titan_vs_Spectral_Dancer;
	public static float Titan_vs_Ghost_Sentinel;
	public static float Titan_vs_Grand_Khauatari;
	public static float Titan_vs_Fortune_Seeker;
	public static float Titan_vs_Maestro;
	public static float Titan_vs_Adventurer;
	public static float Titan_vs_Wind_Rider;
	public static float Titan_vs_Ghost_Hunter;
	public static float Titan_vs_Archmage;
	public static float Titan_vs_Soultaker;
	public static float Titan_vs_Arcana_Lord;
	public static float Titan_vs_Cardinal;
	public static float Titan_vs_Hierophant;
	public static float Titan_vs_Mystic_Muse;
	public static float Titan_vs_Elemental_Master;
	public static float Titan_vs_Eva_s_Saint;
	public static float Titan_vs_Storm_Screamer;
	public static float Titan_vs_Spectral_Master;
	public static float Titan_vs_Shillien_Saint;
	public static float Titan_vs_Dominator;
	public static float Titan_vs_Doomcryer;
	
	public static float Grand_Khauatari_vs_Duelist;
	public static float Grand_Khauatari_vs_DreadNought;
	public static float Grand_Khauatari_vs_Phoenix_Knight;
	public static float Grand_Khauatari_vs_Hell_Knight;
	public static float Grand_Khauatari_vs_Sagitarius;
	public static float Grand_Khauatari_vs_Eva_Templar;
	public static float Grand_Khauatari_vs_Sword_Muse;
	public static float Grand_Khauatari_vs_Moonlight_Sentinel;
	public static float Grand_Khauatari_vs_Shillien_Templar;
	public static float Grand_Khauatari_vs_Spectral_Dancer;
	public static float Grand_Khauatari_vs_Ghost_Sentinel;
	public static float Grand_Khauatari_vs_Titan;
	public static float Grand_Khauatari_vs_Fortune_Seeker;
	public static float Grand_Khauatari_vs_Maestro;
	public static float Grand_Khauatari_vs_Adventurer;
	public static float Grand_Khauatari_vs_Wind_Rider;
	public static float Grand_Khauatari_vs_Ghost_Hunter;
	public static float Grand_Khauatari_vs_Archmage;
	public static float Grand_Khauatari_vs_Soultaker;
	public static float Grand_Khauatari_vs_Arcana_Lord;
	public static float Grand_Khauatari_vs_Cardinal;
	public static float Grand_Khauatari_vs_Hierophant;
	public static float Grand_Khauatari_vs_Mystic_Muse;
	public static float Grand_Khauatari_vs_Elemental_Master;
	public static float Grand_Khauatari_vs_Eva_s_Saint;
	public static float Grand_Khauatari_vs_Storm_Screamer;
	public static float Grand_Khauatari_vs_Spectral_Master;
	public static float Grand_Khauatari_vs_Shillien_Saint;
	public static float Grand_Khauatari_vs_Dominator;
	public static float Grand_Khauatari_vs_Doomcryer;
	
	public static float Fortune_Seeker_vs_Duelist;
	public static float Fortune_Seeker_vs_DreadNought;
	public static float Fortune_Seeker_vs_Phoenix_Knight;
	public static float Fortune_Seeker_vs_Hell_Knight;
	public static float Fortune_Seeker_vs_Sagitarius;
	public static float Fortune_Seeker_vs_Eva_Templar;
	public static float Fortune_Seeker_vs_Sword_Muse;
	public static float Fortune_Seeker_vs_Moonlight_Sentinel;
	public static float Fortune_Seeker_vs_Shillien_Templar;
	public static float Fortune_Seeker_vs_Spectral_Dancer;
	public static float Fortune_Seeker_vs_Ghost_Sentinel;
	public static float Fortune_Seeker_vs_Titan;
	public static float Fortune_Seeker_vs_Grand_Khauatari;
	public static float Fortune_Seeker_vs_Maestro;
	public static float Fortune_Seeker_vs_Adventurer;
	public static float Fortune_Seeker_vs_Wind_Rider;
	public static float Fortune_Seeker_vs_Ghost_Hunter;
	public static float Fortune_Seeker_vs_Archmage;
	public static float Fortune_Seeker_vs_Soultaker;
	public static float Fortune_Seeker_vs_Arcana_Lord;
	public static float Fortune_Seeker_vs_Cardinal;
	public static float Fortune_Seeker_vs_Hierophant;
	public static float Fortune_Seeker_vs_Mystic_Muse;
	public static float Fortune_Seeker_vs_Elemental_Master;
	public static float Fortune_Seeker_vs_Eva_s_Saint;
	public static float Fortune_Seeker_vs_Storm_Screamer;
	public static float Fortune_Seeker_vs_Spectral_Master;
	public static float Fortune_Seeker_vs_Shillien_Saint;
	public static float Fortune_Seeker_vs_Dominator;
	public static float Fortune_Seeker_vs_Doomcryer;
	
	public static float Maestro_vs_Duelist;
	public static float Maestro_vs_DreadNought;
	public static float Maestro_vs_Phoenix_Knight;
	public static float Maestro_vs_Hell_Knight;
	public static float Maestro_vs_Sagitarius;
	public static float Maestro_vs_Eva_Templar;
	public static float Maestro_vs_Sword_Muse;
	public static float Maestro_vs_Moonlight_Sentinel;
	public static float Maestro_vs_Shillien_Templar;
	public static float Maestro_vs_Spectral_Dancer;
	public static float Maestro_vs_Ghost_Sentinel;
	public static float Maestro_vs_Titan;
	public static float Maestro_vs_Grand_Khauatari;
	public static float Maestro_vs_Fortune_Seeker;
	public static float Maestro_vs_Adventurer;
	public static float Maestro_vs_Wind_Rider;
	public static float Maestro_vs_Ghost_Hunter;
	public static float Maestro_vs_Archmage;
	public static float Maestro_vs_Soultaker;
	public static float Maestro_vs_Arcana_Lord;
	public static float Maestro_vs_Cardinal;
	public static float Maestro_vs_Hierophant;
	public static float Maestro_vs_Mystic_Muse;
	public static float Maestro_vs_Elemental_Master;
	public static float Maestro_vs_Eva_s_Saint;
	public static float Maestro_vs_Storm_Screamer;
	public static float Maestro_vs_Spectral_Master;
	public static float Maestro_vs_Shillien_Saint;
	public static float Maestro_vs_Dominator;
	public static float Maestro_vs_Doomcryer;
	
	public static float Adventurer_vs_Duelist;
	public static float Adventurer_vs_DreadNought;
	public static float Adventurer_vs_Phoenix_Knight;
	public static float Adventurer_vs_Hell_Knight;
	public static float Adventurer_vs_Sagitarius;
	public static float Adventurer_vs_Eva_Templar;
	public static float Adventurer_vs_Sword_Muse;
	public static float Adventurer_vs_Moonlight_Sentinel;
	public static float Adventurer_vs_Shillien_Templar;
	public static float Adventurer_vs_Spectral_Dancer;
	public static float Adventurer_vs_Ghost_Sentinel;
	public static float Adventurer_vs_Titan;
	public static float Adventurer_vs_Grand_Khauatari;
	public static float Adventurer_vs_Fortune_Seeker;
	public static float Adventurer_vs_Maestro;
	public static float Adventurer_vs_Wind_Rider;
	public static float Adventurer_vs_Ghost_Hunter;
	public static float Adventurer_vs_Archmage;
	public static float Adventurer_vs_Soultaker;
	public static float Adventurer_vs_Arcana_Lord;
	public static float Adventurer_vs_Cardinal;
	public static float Adventurer_vs_Hierophant;
	public static float Adventurer_vs_Mystic_Muse;
	public static float Adventurer_vs_Elemental_Master;
	public static float Adventurer_vs_Eva_s_Saint;
	public static float Adventurer_vs_Storm_Screamer;
	public static float Adventurer_vs_Spectral_Master;
	public static float Adventurer_vs_Shillien_Saint;
	public static float Adventurer_vs_Dominator;
	public static float Adventurer_vs_Doomcryer;
	
	public static float Wind_Rider_vs_Duelist;
	public static float Wind_Rider_vs_DreadNought;
	public static float Wind_Rider_vs_Phoenix_Knight;
	public static float Wind_Rider_vs_Hell_Knight;
	public static float Wind_Rider_vs_Sagitarius;
	public static float Wind_Rider_vs_Eva_Templar;
	public static float Wind_Rider_vs_Sword_Muse;
	public static float Wind_Rider_vs_Moonlight_Sentinel;
	public static float Wind_Rider_vs_Shillien_Templar;
	public static float Wind_Rider_vs_Spectral_Dancer;
	public static float Wind_Rider_vs_Ghost_Sentinel;
	public static float Wind_Rider_vs_Titan;
	public static float Wind_Rider_vs_Grand_Khauatari;
	public static float Wind_Rider_vs_Fortune_Seeker;
	public static float Wind_Rider_vs_Maestro;
	public static float Wind_Rider_vs_Adventurer;
	public static float Wind_Rider_vs_Ghost_Hunter;
	public static float Wind_Rider_vs_Archmage;
	public static float Wind_Rider_vs_Soultaker;
	public static float Wind_Rider_vs_Arcana_Lord;
	public static float Wind_Rider_vs_Cardinal;
	public static float Wind_Rider_vs_Hierophant;
	public static float Wind_Rider_vs_Mystic_Muse;
	public static float Wind_Rider_vs_Elemental_Master;
	public static float Wind_Rider_vs_Eva_s_Saint;
	public static float Wind_Rider_vs_Storm_Screamer;
	public static float Wind_Rider_vs_Spectral_Master;
	public static float Wind_Rider_vs_Shillien_Saint;
	public static float Wind_Rider_vs_Dominator;
	public static float Wind_Rider_vs_Doomcryer;
	
	public static float Ghost_Hunter_vs_Duelist;
	public static float Ghost_Hunter_vs_DreadNought;
	public static float Ghost_Hunter_vs_Phoenix_Knight;
	public static float Ghost_Hunter_vs_Hell_Knight;
	public static float Ghost_Hunter_vs_Sagitarius;
	public static float Ghost_Hunter_vs_Eva_Templar;
	public static float Ghost_Hunter_vs_Sword_Muse;
	public static float Ghost_Hunter_vs_Moonlight_Sentinel;
	public static float Ghost_Hunter_vs_Shillien_Templar;
	public static float Ghost_Hunter_vs_Spectral_Dancer;
	public static float Ghost_Hunter_vs_Ghost_Sentinel;
	public static float Ghost_Hunter_vs_Titan;
	public static float Ghost_Hunter_vs_Grand_Khauatari;
	public static float Ghost_Hunter_vs_Fortune_Seeker;
	public static float Ghost_Hunter_vs_Maestro;
	public static float Ghost_Hunter_vs_Adventurer;
	public static float Ghost_Hunter_vs_Wind_Rider;
	public static float Ghost_Hunter_vs_Archmage;
	public static float Ghost_Hunter_vs_Soultaker;
	public static float Ghost_Hunter_vs_Arcana_Lord;
	public static float Ghost_Hunter_vs_Cardinal;
	public static float Ghost_Hunter_vs_Hierophant;
	public static float Ghost_Hunter_vs_Mystic_Muse;
	public static float Ghost_Hunter_vs_Elemental_Master;
	public static float Ghost_Hunter_vs_Eva_s_Saint;
	public static float Ghost_Hunter_vs_Storm_Screamer;
	public static float Ghost_Hunter_vs_Spectral_Master;
	public static float Ghost_Hunter_vs_Shillien_Saint;
	public static float Ghost_Hunter_vs_Dominator;
	public static float Ghost_Hunter_vs_Doomcryer;
	
	public static float Archmage_vs_Duelist;
	public static float Archmage_vs_DreadNought;
	public static float Archmage_vs_Phoenix_Knight;
	public static float Archmage_vs_Hell_Knight;
	public static float Archmage_vs_Sagitarius;
	public static float Archmage_vs_Eva_Templar;
	public static float Archmage_vs_Sword_Muse;
	public static float Archmage_vs_Moonlight_Sentinel;
	public static float Archmage_vs_Shillien_Templar;
	public static float Archmage_vs_Spectral_Dancer;
	public static float Archmage_vs_Ghost_Sentinel;
	public static float Archmage_vs_Titan;
	public static float Archmage_vs_Grand_Khauatari;
	public static float Archmage_vs_Fortune_Seeker;
	public static float Archmage_vs_Maestro;
	public static float Archmage_vs_Adventurer;
	public static float Archmage_vs_Wind_Rider;
	public static float Archmage_vs_Ghost_Hunter;
	public static float Archmage_vs_Soultaker;
	public static float Archmage_vs_Arcana_Lord;
	public static float Archmage_vs_Cardinal;
	public static float Archmage_vs_Hierophant;
	public static float Archmage_vs_Mystic_Muse;
	public static float Archmage_vs_Elemental_Master;
	public static float Archmage_vs_Eva_s_Saint;
	public static float Archmage_vs_Storm_Screamer;
	public static float Archmage_vs_Spectral_Master;
	public static float Archmage_vs_Shillien_Saint;
	public static float Archmage_vs_Dominator;
	public static float Archmage_vs_Doomcryer;
	
	public static float Soultaker_vs_Duelist;
	public static float Soultaker_vs_DreadNought;
	public static float Soultaker_vs_Phoenix_Knight;
	public static float Soultaker_vs_Hell_Knight;
	public static float Soultaker_vs_Sagitarius;
	public static float Soultaker_vs_Eva_Templar;
	public static float Soultaker_vs_Sword_Muse;
	public static float Soultaker_vs_Moonlight_Sentinel;
	public static float Soultaker_vs_Shillien_Templar;
	public static float Soultaker_vs_Spectral_Dancer;
	public static float Soultaker_vs_Ghost_Sentinel;
	public static float Soultaker_vs_Titan;
	public static float Soultaker_vs_Grand_Khauatari;
	public static float Soultaker_vs_Fortune_Seeker;
	public static float Soultaker_vs_Maestro;
	public static float Soultaker_vs_Adventurer;
	public static float Soultaker_vs_Wind_Rider;
	public static float Soultaker_vs_Ghost_Hunter;
	public static float Soultaker_vs_Archmage;
	public static float Soultaker_vs_Arcana_Lord;
	public static float Soultaker_vs_Cardinal;
	public static float Soultaker_vs_Hierophant;
	public static float Soultaker_vs_Mystic_Muse;
	public static float Soultaker_vs_Elemental_Master;
	public static float Soultaker_vs_Eva_s_Saint;
	public static float Soultaker_vs_Storm_Screamer;
	public static float Soultaker_vs_Spectral_Master;
	public static float Soultaker_vs_Shillien_Saint;
	public static float Soultaker_vs_Dominator;
	public static float Soultaker_vs_Doomcryer;
	
	public static float Arcana_Lord_vs_Duelist;
	public static float Arcana_Lord_vs_DreadNought;
	public static float Arcana_Lord_vs_Phoenix_Knight;
	public static float Arcana_Lord_vs_Hell_Knight;
	public static float Arcana_Lord_vs_Sagitarius;
	public static float Arcana_Lord_vs_Eva_Templar;
	public static float Arcana_Lord_vs_Sword_Muse;
	public static float Arcana_Lord_vs_Moonlight_Sentinel;
	public static float Arcana_Lord_vs_Shillien_Templar;
	public static float Arcana_Lord_vs_Spectral_Dancer;
	public static float Arcana_Lord_vs_Ghost_Sentinel;
	public static float Arcana_Lord_vs_Titan;
	public static float Arcana_Lord_vs_Grand_Khauatari;
	public static float Arcana_Lord_vs_Fortune_Seeker;
	public static float Arcana_Lord_vs_Maestro;
	public static float Arcana_Lord_vs_Adventurer;
	public static float Arcana_Lord_vs_Wind_Rider;
	public static float Arcana_Lord_vs_Ghost_Hunter;
	public static float Arcana_Lord_vs_Archmage;
	public static float Arcana_Lord_vs_Soultaker;
	public static float Arcana_Lord_vs_Cardinal;
	public static float Arcana_Lord_vs_Hierophant;
	public static float Arcana_Lord_vs_Mystic_Muse;
	public static float Arcana_Lord_vs_Elemental_Master;
	public static float Arcana_Lord_vs_Eva_s_Saint;
	public static float Arcana_Lord_vs_Storm_Screamer;
	public static float Arcana_Lord_vs_Spectral_Master;
	public static float Arcana_Lord_vs_Shillien_Saint;
	public static float Arcana_Lord_vs_Dominator;
	public static float Arcana_Lord_vs_Doomcryer;
	
	public static float Cardinal_vs_Duelist;
	public static float Cardinal_vs_DreadNought;
	public static float Cardinal_vs_Phoenix_Knight;
	public static float Cardinal_vs_Hell_Knight;
	public static float Cardinal_vs_Sagitarius;
	public static float Cardinal_vs_Eva_Templar;
	public static float Cardinal_vs_Sword_Muse;
	public static float Cardinal_vs_Moonlight_Sentinel;
	public static float Cardinal_vs_Shillien_Templar;
	public static float Cardinal_vs_Spectral_Dancer;
	public static float Cardinal_vs_Ghost_Sentinel;
	public static float Cardinal_vs_Titan;
	public static float Cardinal_vs_Grand_Khauatari;
	public static float Cardinal_vs_Fortune_Seeker;
	public static float Cardinal_vs_Maestro;
	public static float Cardinal_vs_Adventurer;
	public static float Cardinal_vs_Wind_Rider;
	public static float Cardinal_vs_Ghost_Hunter;
	public static float Cardinal_vs_Archmage;
	public static float Cardinal_vs_Soultaker;
	public static float Cardinal_vs_Arcana_Lord;
	public static float Cardinal_vs_Hierophant;
	public static float Cardinal_vs_Mystic_Muse;
	public static float Cardinal_vs_Elemental_Master;
	public static float Cardinal_vs_Eva_s_Saint;
	public static float Cardinal_vs_Storm_Screamer;
	public static float Cardinal_vs_Spectral_Master;
	public static float Cardinal_vs_Shillien_Saint;
	public static float Cardinal_vs_Dominator;
	public static float Cardinal_vs_Doomcryer;
	
	public static float Hierophant_vs_Duelist;
	public static float Hierophant_vs_DreadNought;
	public static float Hierophant_vs_Phoenix_Knight;
	public static float Hierophant_vs_Hell_Knight;
	public static float Hierophant_vs_Sagitarius;
	public static float Hierophant_vs_Eva_Templar;
	public static float Hierophant_vs_Sword_Muse;
	public static float Hierophant_vs_Moonlight_Sentinel;
	public static float Hierophant_vs_Shillien_Templar;
	public static float Hierophant_vs_Spectral_Dancer;
	public static float Hierophant_vs_Ghost_Sentinel;
	public static float Hierophant_vs_Titan;
	public static float Hierophant_vs_Grand_Khauatari;
	public static float Hierophant_vs_Fortune_Seeker;
	public static float Hierophant_vs_Maestro;
	public static float Hierophant_vs_Adventurer;
	public static float Hierophant_vs_Wind_Rider;
	public static float Hierophant_vs_Ghost_Hunter;
	public static float Hierophant_vs_Archmage;
	public static float Hierophant_vs_Soultaker;
	public static float Hierophant_vs_Arcana_Lord;
	public static float Hierophant_vs_Cardinal;
	public static float Hierophant_vs_Mystic_Muse;
	public static float Hierophant_vs_Elemental_Master;
	public static float Hierophant_vs_Eva_s_Saint;
	public static float Hierophant_vs_Storm_Screamer;
	public static float Hierophant_vs_Spectral_Master;
	public static float Hierophant_vs_Shillien_Saint;
	public static float Hierophant_vs_Dominator;
	public static float Hierophant_vs_Doomcryer;
	
	public static float Mystic_Muse_vs_Duelist;
	public static float Mystic_Muse_vs_DreadNought;
	public static float Mystic_Muse_vs_Phoenix_Knight;
	public static float Mystic_Muse_vs_Hell_Knight;
	public static float Mystic_Muse_vs_Sagitarius;
	public static float Mystic_Muse_vs_Eva_Templar;
	public static float Mystic_Muse_vs_Sword_Muse;
	public static float Mystic_Muse_vs_Moonlight_Sentinel;
	public static float Mystic_Muse_vs_Shillien_Templar;
	public static float Mystic_Muse_vs_Spectral_Dancer;
	public static float Mystic_Muse_vs_Ghost_Sentinel;
	public static float Mystic_Muse_vs_Titan;
	public static float Mystic_Muse_vs_Grand_Khauatari;
	public static float Mystic_Muse_vs_Fortune_Seeker;
	public static float Mystic_Muse_vs_Maestro;
	public static float Mystic_Muse_vs_Adventurer;
	public static float Mystic_Muse_vs_Wind_Rider;
	public static float Mystic_Muse_vs_Ghost_Hunter;
	public static float Mystic_Muse_vs_Archmage;
	public static float Mystic_Muse_vs_Soultaker;
	public static float Mystic_Muse_vs_Arcana_Lord;
	public static float Mystic_Muse_vs_Cardinal;
	public static float Mystic_Muse_vs_Hierophant;
	public static float Mystic_Muse_vs_Elemental_Master;
	public static float Mystic_Muse_vs_Eva_s_Saint;
	public static float Mystic_Muse_vs_Storm_Screamer;
	public static float Mystic_Muse_vs_Spectral_Master;
	public static float Mystic_Muse_vs_Shillien_Saint;
	public static float Mystic_Muse_vs_Dominator;
	public static float Mystic_Muse_vs_Doomcryer;
	
	public static float Elemental_Master_vs_Duelist;
	public static float Elemental_Master_vs_DreadNought;
	public static float Elemental_Master_vs_Phoenix_Knight;
	public static float Elemental_Master_vs_Hell_Knight;
	public static float Elemental_Master_vs_Sagitarius;
	public static float Elemental_Master_vs_Eva_Templar;
	public static float Elemental_Master_vs_Sword_Muse;
	public static float Elemental_Master_vs_Moonlight_Sentinel;
	public static float Elemental_Master_vs_Shillien_Templar;
	public static float Elemental_Master_vs_Spectral_Dancer;
	public static float Elemental_Master_vs_Ghost_Sentinel;
	public static float Elemental_Master_vs_Titan;
	public static float Elemental_Master_vs_Grand_Khauatari;
	public static float Elemental_Master_vs_Fortune_Seeker;
	public static float Elemental_Master_vs_Maestro;
	public static float Elemental_Master_vs_Adventurer;
	public static float Elemental_Master_vs_Wind_Rider;
	public static float Elemental_Master_vs_Ghost_Hunter;
	public static float Elemental_Master_vs_Archmage;
	public static float Elemental_Master_vs_Soultaker;
	public static float Elemental_Master_vs_Arcana_Lord;
	public static float Elemental_Master_vs_Cardinal;
	public static float Elemental_Master_vs_Hierophant;
	public static float Elemental_Master_vs_Mystic_Muse;
	public static float Elemental_Master_vs_Eva_s_Saint;
	public static float Elemental_Master_vs_Storm_Screamer;
	public static float Elemental_Master_vs_Spectral_Master;
	public static float Elemental_Master_vs_Shillien_Saint;
	public static float Elemental_Master_vs_Dominator;
	public static float Elemental_Master_vs_Doomcryer;
	
	public static float Eva_s_Saint_vs_Duelist;
	public static float Eva_s_Saint_vs_DreadNought;
	public static float Eva_s_Saint_vs_Phoenix_Knight;
	public static float Eva_s_Saint_vs_Hell_Knight;
	public static float Eva_s_Saint_vs_Sagitarius;
	public static float Eva_s_Saint_vs_Eva_Templar;
	public static float Eva_s_Saint_vs_Sword_Muse;
	public static float Eva_s_Saint_vs_Moonlight_Sentinel;
	public static float Eva_s_Saint_vs_Shillien_Templar;
	public static float Eva_s_Saint_vs_Spectral_Dancer;
	public static float Eva_s_Saint_vs_Ghost_Sentinel;
	public static float Eva_s_Saint_vs_Titan;
	public static float Eva_s_Saint_vs_Grand_Khauatari;
	public static float Eva_s_Saint_vs_Fortune_Seeker;
	public static float Eva_s_Saint_vs_Maestro;
	public static float Eva_s_Saint_vs_Adventurer;
	public static float Eva_s_Saint_vs_Wind_Rider;
	public static float Eva_s_Saint_vs_Ghost_Hunter;
	public static float Eva_s_Saint_vs_Archmage;
	public static float Eva_s_Saint_vs_Soultaker;
	public static float Eva_s_Saint_vs_Arcana_Lord;
	public static float Eva_s_Saint_vs_Cardinal;
	public static float Eva_s_Saint_vs_Hierophant;
	public static float Eva_s_Saint_vs_Mystic_Muse;
	public static float Eva_s_Saint_vs_Elemental_Master;
	public static float Eva_s_Saint_vs_Storm_Screamer;
	public static float Eva_s_Saint_vs_Spectral_Master;
	public static float Eva_s_Saint_vs_Shillien_Saint;
	public static float Eva_s_Saint_vs_Dominator;
	public static float Eva_s_Saint_vs_Doomcryer;
	
	public static float Storm_Screamer_vs_Duelist;
	public static float Storm_Screamer_vs_DreadNought;
	public static float Storm_Screamer_vs_Phoenix_Knight;
	public static float Storm_Screamer_vs_Hell_Knight;
	public static float Storm_Screamer_vs_Sagitarius;
	public static float Storm_Screamer_vs_Eva_Templar;
	public static float Storm_Screamer_vs_Sword_Muse;
	public static float Storm_Screamer_vs_Moonlight_Sentinel;
	public static float Storm_Screamer_vs_Shillien_Templar;
	public static float Storm_Screamer_vs_Spectral_Dancer;
	public static float Storm_Screamer_vs_Ghost_Sentinel;
	public static float Storm_Screamer_vs_Titan;
	public static float Storm_Screamer_vs_Grand_Khauatari;
	public static float Storm_Screamer_vs_Fortune_Seeker;
	public static float Storm_Screamer_vs_Maestro;
	public static float Storm_Screamer_vs_Adventurer;
	public static float Storm_Screamer_vs_Wind_Rider;
	public static float Storm_Screamer_vs_Ghost_Hunter;
	public static float Storm_Screamer_vs_Archmage;
	public static float Storm_Screamer_vs_Soultaker;
	public static float Storm_Screamer_vs_Arcana_Lord;
	public static float Storm_Screamer_vs_Cardinal;
	public static float Storm_Screamer_vs_Hierophant;
	public static float Storm_Screamer_vs_Mystic_Muse;
	public static float Storm_Screamer_vs_Elemental_Master;
	public static float Storm_Screamer_vs_Eva_s_Saint;
	public static float Storm_Screamer_vs_Spectral_Master;
	public static float Storm_Screamer_vs_Shillien_Saint;
	public static float Storm_Screamer_vs_Dominator;
	public static float Storm_Screamer_vs_Doomcryer;
	
	public static float Spectral_Master_vs_Duelist;
	public static float Spectral_Master_vs_DreadNought;
	public static float Spectral_Master_vs_Phoenix_Knight;
	public static float Spectral_Master_vs_Hell_Knight;
	public static float Spectral_Master_vs_Sagitarius;
	public static float Spectral_Master_vs_Eva_Templar;
	public static float Spectral_Master_vs_Sword_Muse;
	public static float Spectral_Master_vs_Moonlight_Sentinel;
	public static float Spectral_Master_vs_Shillien_Templar;
	public static float Spectral_Master_vs_Spectral_Dancer;
	public static float Spectral_Master_vs_Ghost_Sentinel;
	public static float Spectral_Master_vs_Titan;
	public static float Spectral_Master_vs_Grand_Khauatari;
	public static float Spectral_Master_vs_Fortune_Seeker;
	public static float Spectral_Master_vs_Maestro;
	public static float Spectral_Master_vs_Adventurer;
	public static float Spectral_Master_vs_Wind_Rider;
	public static float Spectral_Master_vs_Ghost_Hunter;
	public static float Spectral_Master_vs_Archmage;
	public static float Spectral_Master_vs_Soultaker;
	public static float Spectral_Master_vs_Arcana_Lord;
	public static float Spectral_Master_vs_Cardinal;
	public static float Spectral_Master_vs_Hierophant;
	public static float Spectral_Master_vs_Mystic_Muse;
	public static float Spectral_Master_vs_Elemental_Master;
	public static float Spectral_Master_vs_Eva_s_Saint;
	public static float Spectral_Master_vs_Storm_Screamer;
	public static float Spectral_Master_vs_Shillien_Saint;
	public static float Spectral_Master_vs_Dominator;
	public static float Spectral_Master_vs_Doomcryer;
	
	public static float Shillien_Saint_vs_Duelist;
	public static float Shillien_Saint_vs_DreadNought;
	public static float Shillien_Saint_vs_Phoenix_Knight;
	public static float Shillien_Saint_vs_Hell_Knight;
	public static float Shillien_Saint_vs_Sagitarius;
	public static float Shillien_Saint_vs_Eva_Templar;
	public static float Shillien_Saint_vs_Sword_Muse;
	public static float Shillien_Saint_vs_Moonlight_Sentinel;
	public static float Shillien_Saint_vs_Shillien_Templar;
	public static float Shillien_Saint_vs_Spectral_Dancer;
	public static float Shillien_Saint_vs_Ghost_Sentinel;
	public static float Shillien_Saint_vs_Titan;
	public static float Shillien_Saint_vs_Grand_Khauatari;
	public static float Shillien_Saint_vs_Fortune_Seeker;
	public static float Shillien_Saint_vs_Maestro;
	public static float Shillien_Saint_vs_Adventurer;
	public static float Shillien_Saint_vs_Wind_Rider;
	public static float Shillien_Saint_vs_Ghost_Hunter;
	public static float Shillien_Saint_vs_Archmage;
	public static float Shillien_Saint_vs_Soultaker;
	public static float Shillien_Saint_vs_Arcana_Lord;
	public static float Shillien_Saint_vs_Cardinal;
	public static float Shillien_Saint_vs_Hierophant;
	public static float Shillien_Saint_vs_Mystic_Muse;
	public static float Shillien_Saint_vs_Elemental_Master;
	public static float Shillien_Saint_vs_Eva_s_Saint;
	public static float Shillien_Saint_vs_Storm_Screamer;
	public static float Shillien_Saint_vs_Spectral_Master;
	public static float Shillien_Saint_vs_Dominator;
	public static float Shillien_Saint_vs_Doomcryer;
	
	public static float Dominator_vs_Duelist;
	public static float Dominator_vs_DreadNought;
	public static float Dominator_vs_Phoenix_Knight;
	public static float Dominator_vs_Hell_Knight;
	public static float Dominator_vs_Sagitarius;
	public static float Dominator_vs_Eva_Templar;
	public static float Dominator_vs_Sword_Muse;
	public static float Dominator_vs_Moonlight_Sentinel;
	public static float Dominator_vs_Shillien_Templar;
	public static float Dominator_vs_Spectral_Dancer;
	public static float Dominator_vs_Ghost_Sentinel;
	public static float Dominator_vs_Titan;
	public static float Dominator_vs_Grand_Khauatari;
	public static float Dominator_vs_Fortune_Seeker;
	public static float Dominator_vs_Maestro;
	public static float Dominator_vs_Adventurer;
	public static float Dominator_vs_Wind_Rider;
	public static float Dominator_vs_Ghost_Hunter;
	public static float Dominator_vs_Archmage;
	public static float Dominator_vs_Soultaker;
	public static float Dominator_vs_Arcana_Lord;
	public static float Dominator_vs_Cardinal;
	public static float Dominator_vs_Hierophant;
	public static float Dominator_vs_Mystic_Muse;
	public static float Dominator_vs_Elemental_Master;
	public static float Dominator_vs_Eva_s_Saint;
	public static float Dominator_vs_Storm_Screamer;
	public static float Dominator_vs_Spectral_Master;
	public static float Dominator_vs_Shillien_Saint;
	public static float Dominator_vs_Doomcryer;
	
	public static float Doomcryer_vs_Duelist;
	public static float Doomcryer_vs_DreadNought;
	public static float Doomcryer_vs_Phoenix_Knight;
	public static float Doomcryer_vs_Hell_Knight;
	public static float Doomcryer_vs_Sagitarius;
	public static float Doomcryer_vs_Eva_Templar;
	public static float Doomcryer_vs_Sword_Muse;
	public static float Doomcryer_vs_Moonlight_Sentinel;
	public static float Doomcryer_vs_Shillien_Templar;
	public static float Doomcryer_vs_Spectral_Dancer;
	public static float Doomcryer_vs_Ghost_Sentinel;
	public static float Doomcryer_vs_Titan;
	public static float Doomcryer_vs_Grand_Khauatari;
	public static float Doomcryer_vs_Fortune_Seeker;
	public static float Doomcryer_vs_Maestro;
	public static float Doomcryer_vs_Adventurer;
	public static float Doomcryer_vs_Wind_Rider;
	public static float Doomcryer_vs_Ghost_Hunter;
	public static float Doomcryer_vs_Archmage;
	public static float Doomcryer_vs_Soultaker;
	public static float Doomcryer_vs_Arcana_Lord;
	public static float Doomcryer_vs_Cardinal;
	public static float Doomcryer_vs_Hierophant;
	public static float Doomcryer_vs_Mystic_Muse;
	public static float Doomcryer_vs_Elemental_Master;
	public static float Doomcryer_vs_Eva_s_Saint;
	public static float Doomcryer_vs_Storm_Screamer;
	public static float Doomcryer_vs_Spectral_Master;
	public static float Doomcryer_vs_Shillien_Saint;
	public static float Doomcryer_vs_Dominator;
	
	
	
	
	
	
	
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (IOException e)
		{
			_log.warning("Config: Error loading \"" + filename + "\" config.");
		}
		
		return result;
	}
	
	/**
	 * itemId1,itemNumber1;itemId2,itemNumber2... to the int[n][2] = [itemId1][itemNumber1],[itemId2][itemNumber2]...
	 * @param line
	 * @return an array consisting of parsed items.
	 */
	private static final int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
			return null;
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				_log.warning("Config: Error parsing entry -> \"" + valueSplit[0] + "\", should be itemId,itemNumber");
				return null;
			}
			
			result[i] = new int[2];
			try
			{
				result[i][0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item ID -> \"" + valueSplit[0] + "\"");
				return null;
			}
			
			try
			{
				result[i][1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				_log.warning("Config: Error parsing item amount -> \"" + valueSplit[1] + "\"");
				return null;
			}
			i++;
		}
		return result;
	}
	
	/**
	 * Loads boss settings.
	 */
	private static final void loadBoss()
	{
		final ExProperties boss = initProperties(BOSS_FILE);
		RAID_HP_REGEN_MULTIPLIER = boss.getProperty("RaidHpRegenMultiplier", 1.);
		RAID_MP_REGEN_MULTIPLIER = boss.getProperty("RaidMpRegenMultiplier", 1.);
		RAID_DEFENCE_MULTIPLIER = boss.getProperty("RaidDefenceMultiplier", 1.);
		RAID_MINION_RESPAWN_TIMER = boss.getProperty("RaidMinionRespawnTime", 300000);
		
		RAID_DISABLE_CURSE = boss.getProperty("DisableRaidCurse", false);
		RAID_CHAOS_TIME = boss.getProperty("RaidChaosTime", 30);
		GRAND_CHAOS_TIME = boss.getProperty("GrandChaosTime", 30);
		MINION_CHAOS_TIME = boss.getProperty("MinionChaosTime", 30);
		
		EARTH_QUAKE = boss.getProperty("EarthQuake", false);
		
		SPAWN_INTERVAL_AQ = boss.getProperty("AntQueenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_AQ = boss.getProperty("AntQueenRandomSpawn", 17);
		
		SPAWN_INTERVAL_ANTHARAS = boss.getProperty("AntharasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_ANTHARAS = boss.getProperty("AntharasRandomSpawn", 72);
		WAIT_TIME_ANTHARAS = boss.getProperty("AntharasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_BAIUM = boss.getProperty("BaiumSpawnInterval", 168);
		RANDOM_SPAWN_TIME_BAIUM = boss.getProperty("BaiumRandomSpawn", 48);
		
		SPAWN_INTERVAL_CORE = boss.getProperty("CoreSpawnInterval", 60);
		RANDOM_SPAWN_TIME_CORE = boss.getProperty("CoreRandomSpawn", 23);
		
		SPAWN_INTERVAL_ORFEN = boss.getProperty("OrfenSpawnInterval", 48);
		RANDOM_SPAWN_TIME_ORFEN = boss.getProperty("OrfenRandomSpawn", 20);
		
		SPAWN_INTERVAL_VALAKAS = boss.getProperty("ValakasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_VALAKAS = boss.getProperty("ValakasRandomSpawn", 72);
		WAIT_TIME_VALAKAS = boss.getProperty("ValakasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_ZAKEN = boss.getProperty("ZakenSpawnInterval", 60);
		RANDOM_SPAWN_TIME_ZAKEN = boss.getProperty("ZakenRandomSpawn", 20);
		WAIT_TIME_ZAKEN = boss.getProperty("ZakenWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_FRINTEZZA = boss.getProperty("FrintezzaSpawnInterval", 48);
		RANDOM_SPAWN_TIME_FRINTEZZA = boss.getProperty("FrintezzaRandomSpawn", 8);
		BYPASS_FRINTEZZA_PARTIES_CHECK = boss.getProperty("BypassPartiesCheck", false);
		FRINTEZZA_MIN_PARTIES = Integer.parseInt(boss.getProperty("FrintezzaMinParties", "4"));
		FRINTEZZA_MAX_PARTIES = Integer.parseInt(boss.getProperty("FrintezzaMaxParties", "5"));
		WAIT_TIME_FRINTEZZA = boss.getProperty("FrintezzaWaitTime", 1) * 60000;
		DESPAWN_TIME_FRINTEZZA = boss.getProperty("FrintezzaDespawnTime", 1) * 60000;
		FRINTEZZA_TIME_CHALLENGE = boss.getProperty("FrintezzaTimeChallenge", 1) * 60000;
		
		SPAWN_INTERVAL_SAILREN = boss.getProperty("SailrenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_SAILREN = boss.getProperty("SailrenRandomSpawn", 24);
		WAIT_TIME_SAILREN = boss.getProperty("SailrenWaitTime", 5) * 60000;
		
		QUEST_BAIUM = Integer.parseInt(boss.getProperty("QuestBaium", "4295"));	
		QUEST_VALAKAS = Integer.parseInt(boss.getProperty("QuestValakas", "7267"));	
		QUEST_ANTHARAS = Integer.parseInt(boss.getProperty("QuestAntharas", "3865"));	
		QUEST_SAILREN = Integer.parseInt(boss.getProperty("QuestSailren", "8784"));	
		QUEST_FRINTEZZA = Integer.parseInt(boss.getProperty("QuestFrintezza", "8073"));
		
		RAID_BOSS_ZONE_IDS = boss.getProperty("RaidList_Protection");
		LIST_RAID_BOSS_ZONE_IDS = new ArrayList<>();
		for (String id : RAID_BOSS_ZONE_IDS.trim().split(","))
		{
			LIST_RAID_BOSS_ZONE_IDS.add(Integer.parseInt(id.trim()));
		}
		
		GRANDBOSS_INFO_IDS = boss.getProperty("GradBossSpawnInfoIDs");
		GRANDBOSS_INFO_IDS_LIST = new ArrayList<>();
		for (String id : GRANDBOSS_INFO_IDS.split(","))
		{
			GRANDBOSS_INFO_IDS_LIST.add(Integer.valueOf(Integer.parseInt(id)));
		}
		
		RAID_INFO_IDS = boss.getProperty("RaidSpawnInfoIDs");
		RAID_INFO_IDS_LIST = new ArrayList<>();
		for(String id : RAID_INFO_IDS.split(","))
			RAID_INFO_IDS_LIST.add(Integer.parseInt(id));
		
		RAID_RESPAWN_IDS = boss.getProperty("Respawn_Raid_IDs");
		RAID_RESPAWN_IDS_LIST = new ArrayList<>();
		for(String id : RAID_RESPAWN_IDS.split(","))
			RAID_RESPAWN_IDS_LIST.add(Integer.parseInt(id));
		
		RESPAWN_CUSTOM = Boolean.parseBoolean(boss.getProperty("RespawnCustom", "false"));
		MIN_RESPAWN = Integer.parseInt(boss.getProperty("Respawn_raidboss", "4"));
		MAX_RESPAWN = Integer.parseInt(boss.getProperty("Respawn_random_raidboss", "4"));
		
		ALLOW_AUTO_NOBLESS_FROM_BOSS = Boolean.valueOf(boss.getProperty("AllowAutoNoblessFromBoss", "True")).booleanValue();
		BOSS_ID = Integer.parseInt(boss.getProperty("BossId", "25325"));
		RADIUS_TO_RAID = Integer.parseInt(boss.getProperty("RadiusToRaid", "1000"));
		
		ANNOUNCE_TO_ALL_SPAWN_RB = Boolean.parseBoolean(boss.getProperty("AnnounceToAllSpawnRb", "false"));
		ANNOUNCE_ID = Integer.parseInt(boss.getProperty("AnnounceId", "3"));
		
		RAID_ID_ANNOUNCE = boss.getProperty("AnnounceRaidId", "");
		
		LIST_RAID_ANNOUNCE = new ArrayList<>();
		for (String id : RAID_ID_ANNOUNCE.split(","))
		{
			LIST_RAID_ANNOUNCE.add(Integer.parseInt(id));
		}
		
		AQ_CUSTOM_SPAWN_ENABLED = boss.getProperty("AntQueenCustomSpawn", false);
		AQ_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("AntQueenRandomSpawn", "0").trim());
		AQ_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("AntQueenDaysAndHours", "").trim(), AQ_CUSTOM_SPAWN_RANDOM_INTERVAL);
		ANTHARAS_CUSTOM_SPAWN_ENABLED = boss.getProperty("AntharasCustomSpawn", false);
		ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("AntharasRandomSpawn", "0").trim());
		ANTHARAS_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("AntharasDaysAndHours", "").trim(), ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL);
		CORE_CUSTOM_SPAWN_ENABLED = boss.getProperty("CoreCustomSpawn", false);
		CORE_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("CoreRandomSpawn", "0").trim());
		CORE_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("CoreDaysAndHours", "").trim(), CORE_CUSTOM_SPAWN_RANDOM_INTERVAL);
		ORFEN_CUSTOM_SPAWN_ENABLED = boss.getProperty("OrfenCustomSpawn", false);
		ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("OrfenRandomSpawn", "0").trim());
		ORFEN_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("OrfenDaysAndHours", "").trim(), ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL);
		ZAKEN_CUSTOM_SPAWN_ENABLED = boss.getProperty("ZakenCustomSpawn", false);
		ZAKEN_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("ZakenRandomSpawn", "0").trim());
		ZAKEN_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("ZakenDaysAndHours", "").trim(), ZAKEN_CUSTOM_SPAWN_RANDOM_INTERVAL);
		FRINTEZZA_CUSTOM_SPAWN_ENABLED = boss.getProperty("FrintezzaCustomSpawn", false);
		FRINTEZZA_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("FrintezzaRandomSpawn", "0").trim());
		FRINTEZZA_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("FrintezzaDaysAndHours", "").trim(), FRINTEZZA_CUSTOM_SPAWN_RANDOM_INTERVAL);
		BAIUM_CUSTOM_SPAWN_ENABLED = boss.getProperty("BaiumCustomSpawn", false);
		BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("BaiumRandomSpawn", "0").trim());
		BAIUM_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("BaiumDaysAndHours", "").trim(), BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL);
		VALAKAS_CUSTOM_SPAWN_ENABLED = boss.getProperty("ValakasCustomSpawn", false);
		VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("ValakasRandomSpawn", "0").trim());
		VALAKAS_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("ValakasDaysAndHours", "").trim(), VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL);
		SAILREN_CUSTOM_SPAWN_ENABLED = boss.getProperty("SailrenCustomSpawn", false);
		SAILREN_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(boss.getProperty("SailrenRandomSpawn", "0").trim());
		SAILREN_CUSTOM_SPAWN_TIMES = ParseDates(boss.getProperty("SailrenDaysAndHours", "").trim(), SAILREN_CUSTOM_SPAWN_RANDOM_INTERVAL);
		
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		
		MIN_MEMBERS_LV6 = clans.getProperty("Min_Members_Raise_the_lv6", 30);
		MIN_MEMBERS_LV7 = clans.getProperty("Min_Members_Raise_the_lv7", 80);
		MIN_MEMBERS_LV8 = clans.getProperty("Min_Members_Raise_the_lv8", 120);
		
		MIN_FLAMESTONE_GIANT = clans.getProperty("Min_Flamestone_Giant", 60);
		MAX_FLAMESTONE_GIANT = clans.getProperty("Max_Flamestone_Giant", 95);
		MIN_THEMIS_SCALE = clans.getProperty("Min_Themis_Scale", 65);
		MAX_THEMIS_SCALE = clans.getProperty("Max_Themis_Scale", 100);
		MIN_HEKATON_PRIME = clans.getProperty("Min_Hekaton_Scale", 40);
		MAX_HEKATON_PRIME = clans.getProperty("Max_Hekaton_Scale", 75);
		MIN_GARGOYLE = clans.getProperty("Min_Gargoyle", 30);
		MAX_GARGOYLE = clans.getProperty("Max_Gargoyle", 65);
		MIN_GLAKI = clans.getProperty("Min_Giant_Glaki", 105);
		MAX_GLAKI = clans.getProperty("Max_Giant_Glaki", 140);
		MIN_RAHHA = clans.getProperty("Min_Rahha", 40);
		MAX_RAHHA = clans.getProperty("Max_Rahha", 75);
		
		ITEM_ID_BUY_CLAN_HALL = clans.getProperty("ItemIDBuyClanHall", 57);
		
		ALLOW_CLAN_FULL = Boolean.parseBoolean(clans.getProperty("LimiteFullClan", "false"));
		DISABLE_ROYAL = Boolean.parseBoolean(clans.getProperty("DisableRoyal", "false"));
		ALT_MAX_NUM_OF_MEMBERS_IN_CLAN = Integer.parseInt(clans.getProperty("AltMaxNumOfMembersInClan", "20"));
		ALT_NUM_OF_MEMBERS_IN_CLAN = Integer.parseInt(clans.getProperty("AltNumOfMembersOnline", "10"));
		
		ALT_CLAN_JOIN_MINUTES = clans.getProperty("MinutesBeforeJoinAClan", 1);
		ALT_CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
		ALT_MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("AltMaxNumOfClansInAlly", 3);
		ALT_CLAN_MEMBERS_FOR_WAR = clans.getProperty("AltClanMembersForWar", 15);
		ALT_CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("AltClanWarPenaltyWhenEnded", 5);
		ALT_CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
		ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("AltMembersCanWithdrawFromClanWH", false);
		REMOVE_CASTLE_CIRCLETS = clans.getProperty("RemoveCastleCirclets", true);
		
		ALT_MANOR_REFRESH_TIME = clans.getProperty("AltManorRefreshTime", 20);
		ALT_MANOR_REFRESH_MIN = clans.getProperty("AltManorRefreshMin", 0);
		ALT_MANOR_APPROVE_TIME = clans.getProperty("AltManorApproveTime", 6);
		ALT_MANOR_APPROVE_MIN = clans.getProperty("AltManorApproveMin", 0);
		ALT_MANOR_MAINTENANCE_MIN = clans.getProperty("AltManorMaintenanceMin", 6);
		ALT_MANOR_SAVE_PERIOD_RATE = clans.getProperty("AltManorSavePeriodRate", 2) * 3600000;
		
		CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000);
		CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
		CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
		CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000);
		CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
		CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
		CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
		CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
		CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
		CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
		CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
		CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
		CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
		CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
		CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
		CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
		CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
		CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
		CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
		CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
		CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
		CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
		CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
		CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
		CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
		CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
		CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
		CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
		CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
		CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
		CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
		CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
		CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
		CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
		CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
		CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
		CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
		CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
		CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
		CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
		CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000);
		CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
		CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
		CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
		CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000);
		CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
		CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
		CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000);
		CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
		CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
	}
	
	/**
	 * Loads enchant settings.
	 */
	private static final void loadEnchant()
	{
		final ExProperties enchant = initProperties(ENCHANT_FILE);
		
		ENABLE_DWARF_ENCHANT_BONUS = Boolean.parseBoolean(enchant.getProperty("EnableDwarfEnchantBonus", "False"));
		DWARF_ENCHANT_MIN_LEVEL = Integer.parseInt(enchant.getProperty("DwarfEnchantMinLevel", "80"));
		DWARF_ENCHANT_BONUS = Integer.parseInt(enchant.getProperty("DwarfEnchantBonus", "15"));
		
		String[] propertySplit = enchant.getProperty("NormalWeaponEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("BlessWeaponEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("CrystalWeaponEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("DonatorWeaponEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_WEAPON_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("NormalArmorEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("BlessArmorEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("CrystalArmorEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		propertySplit = enchant.getProperty("DonatorArmorEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_ARMOR_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("NormalJewelryEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					NORMAL_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("BlessJewelryEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					BLESS_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("CrystalJewelryEnchantLevel", "").split(";");
		for (final String readData : propertySplit)
		{
			final String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				_log.info("invalid config property");
			}
			else
			{
				try
				{
					CRYSTAL_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (final NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					
					if (!readData.equals(""))
					{
						_log.info("invalid config property");
					}
				}
			}
		}
		
		propertySplit = enchant.getProperty("DonatorJewelryEnchantLevel", "").split(";");
		for (String readData : propertySplit)
		{
			String[] writeData = readData.split(",");
			if (writeData.length != 2)
			{
				System.out.println("invalid config property");
			}
			else
			{
				try
				{
					DONATOR_JEWELRY_ENCHANT_LEVEL.put(Integer.parseInt(writeData[0]), Integer.parseInt(writeData[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (Config.DEBUG)
						nfe.printStackTrace();
					
					if (!readData.equals(""))
					{
						System.out.println("invalid config property");
					}
				}
			}
		}
		ENCHANT_HERO_WEAPON = Boolean.parseBoolean(enchant.getProperty("EnableEnchantHeroWeapons", "False"));
		
		GOLD_WEAPON = Integer.parseInt(enchant.getProperty("IdEnchantDonatorWeapon", "10010"));
		
		GOLD_ARMOR = Integer.parseInt(enchant.getProperty("IdEnchantDonatorArmor", "10011"));
		
		ENCHANT_SAFE_MAX = Integer.parseInt(enchant.getProperty("EnchantSafeMax", "3"));
		
		ENCHANT_SAFE_MAX_FULL = Integer.parseInt(enchant.getProperty("EnchantSafeMaxFull", "4"));
		
		SCROLL_STACKABLE = Boolean.parseBoolean(enchant.getProperty("ScrollStackable", "False"));
		
		ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("EnchantWeaponMax", "25"));
		ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("EnchantArmorMax", "25"));
		ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("EnchantJewelryMax", "25"));
		
		BLESSED_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantWeaponMax", "25"));
		BLESSED_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantArmorMax", "25"));
		BLESSED_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("BlessedEnchantJewelryMax", "25"));
		
		BREAK_ENCHANT = Integer.valueOf(enchant.getProperty("BreakEnchant", "0"));
		
		CRYSTAL_ENCHANT_MIN = Integer.parseInt(enchant.getProperty("CrystalEnchantMin", "20"));
		CRYSTAL_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantWeaponMax", "25"));
		CRYSTAL_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantArmorMax", "25"));
		CRYSTAL_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("CrystalEnchantJewelryMax", "25"));
		
		DONATOR_ENCHANT_MIN = Integer.parseInt(enchant.getProperty("DonatorEnchantMin", "20"));
		DONATOR_ENCHANT_WEAPON_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantWeaponMax", "25"));
		DONATOR_ENCHANT_ARMOR_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantArmorMax", "25"));
		DONATOR_ENCHANT_JEWELRY_MAX = Integer.parseInt(enchant.getProperty("DonatorEnchantJewelryMax", "25"));
		DONATOR_DECREASE_ENCHANT = Boolean.valueOf(enchant.getProperty("DonatorDecreaseEnchant", "false"));
		DONATOR_BREAK_ENCHANT = Integer.parseInt(enchant.getProperty("DonatorBreakEnchant", "25"));
		DONATOR_LOST_ENCHANT = Boolean.valueOf(enchant.getProperty("DonatorLostEnchant", "false"));
		
		
		ENABLE_ENCHANT_ANNOUNCE = Boolean.parseBoolean(enchant.getProperty("EnableEnchantAnnounce", "False"));
		ENCHANT_ANNOUNCE_LEVEL = enchant.getProperty("EnchantAnnounceLevels", "6,10,16,20");
		LIST_ENCHANT_ANNOUNCE_LEVEL = new ArrayList<>();
		for (String id : ENCHANT_ANNOUNCE_LEVEL.split(","))
		{
			LIST_ENCHANT_ANNOUNCE_LEVEL.add(Integer.parseInt(id));
		}
		
		ALT_OLY_ENCHANT_LIMIT = Integer.parseInt(enchant.getProperty("AltOlyMaxEnchant", "-1"));
		
		AUGMENTATION_NG_SKILL_CHANCE = enchant.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = enchant.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = enchant.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = enchant.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = enchant.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = enchant.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = enchant.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = enchant.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = enchant.getProperty("AugmentationBaseStatChance", 1);
	}
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		
		OLY_SKILL_PROTECT = Boolean.parseBoolean(events.getProperty("OlySkillProtect", "True"));
		for (String id : events.getProperty("OllySkillId", "0").split(","))
		{
			OLY_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		OLY_PROTECT_ITEMS = new ArrayList<>();
		for (String id : events.getProperty("OlyRestrictedItems", "0").split(","))
		{
			OLY_PROTECT_ITEMS.add(Integer.parseInt(id));
		}
		
		OLLY_GRADE_A = Boolean.parseBoolean(events.getProperty("AllowOllyGradeS", "False"));
		
		ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS = Boolean.parseBoolean(events.getProperty("AltOlyUseCustomPeriodSettings", "false"));
		ALT_OLY_PERIOD = OlympiadPeriod.valueOf(events.getProperty("AltOlyPeriod", "MONTH"));
		ALT_OLY_PERIOD_MULTIPLIER = Integer.parseInt(events.getProperty("AltOlyPeriodMultiplier", "1"));
		
		OLYMPIAD_PERIOD = events.getProperty("OlympiadPeriod_EnterWorld", false);
		
		OLY_FIGHT = events.getProperty("AllowOlympiad", false);
		
		OLY_CLASSED_FIGHT = events.getProperty("AllowClassedFight", false);
		
		String[] property_m = events.getProperty("OlympiadBuffsMage", "1204,2").split(";");
		CUSTOM_BUFFS_M.clear();
		for (String buff : property_m)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("OlympiadBuffsMage[Config.load()]: invalid config property -> OlympiadBuffsMage \"" + buff + "\"");
			else
			{
				try
				{
					CUSTOM_BUFFS_M.add(new int[]
						{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
						});
				}
				catch (NumberFormatException nfe)
				{
					if (!buff.isEmpty())
						_log.warning("OlympiadBuffsMage[Config.load()]: invalid config property -> OlympiadBuffsMage \"" + buff + "\"");
				}
			}
		}
		
		String[] property_f = events.getProperty("OlympiadBuffsFighter", "1204,2").split(";");
		CUSTOM_BUFFS_F.clear();
		for (String buff : property_f)
		{
			String[] buffSplit = buff.split(",");
			if (buffSplit.length != 2)
				_log.warning("OlympiadBuffsFighter[Config.load()]: invalid config property -> OlympiadBuffsFighter \"" + buff + "\"");
			else
			{
				try
				{
					CUSTOM_BUFFS_F.add(new int[]
						{
						Integer.parseInt(buffSplit[0]),
						Integer.parseInt(buffSplit[1])
						});
				}
				catch (NumberFormatException nfe)
				{
					if (!buff.isEmpty())
						_log.warning("OlympiadBuffsFighter[Config.load()]: invalid config property -> OlympiadBuffsFighter \"" + buff + "\"");
				}
			}
		}
		ALT_OLY_START_TIME = events.getProperty("AltOlyStartTime", 18);
		ALT_OLY_MIN = events.getProperty("AltOlyMin", 0);
		ALT_OLY_CPERIOD = events.getProperty("AltOlyCPeriod", 21600000);
		ALT_OLY_BATTLE = events.getProperty("AltOlyBattle", 180000);
		ALT_OLY_WPERIOD = events.getProperty("AltOlyWPeriod", 604800000);
		ALT_OLY_VPERIOD = events.getProperty("AltOlyVPeriod", 86400000);
		ALT_OLY_WAIT_TIME = events.getProperty("AltOlyWaitTime", 30);
		ALT_OLY_WAIT_BATTLE = events.getProperty("AltOlyWaitBattle", 60);
		ALT_OLY_WAIT_END = events.getProperty("AltOlyWaitEnd", 40);
		ALT_OLY_START_POINTS = events.getProperty("AltOlyStartPoints", 18);
		ALT_OLY_WEEKLY_POINTS = events.getProperty("AltOlyWeeklyPoints", 3);
		ALT_OLY_MIN_MATCHES = events.getProperty("AltOlyMinMatchesToBeClassed", 5);
		ALT_OLY_CLASSED = events.getProperty("AltOlyClassedParticipants", 5);
		ALT_OLY_NONCLASSED = events.getProperty("AltOlyNonClassedParticipants", 9);
		ALT_OLY_CLASSED_REWARD = parseItemsList(events.getProperty("AltOlyClassedReward", "6651,50"));
		ALT_OLY_NONCLASSED_REWARD = parseItemsList(events.getProperty("AltOlyNonClassedReward", "6651,30"));
		ALT_OLY_GP_PER_POINT = events.getProperty("AltOlyGPPerPoint", 1000);
		ALT_OLY_HERO_POINTS = events.getProperty("AltOlyHeroPoints", 300);
		ALT_OLY_RANK1_POINTS = events.getProperty("AltOlyRank1Points", 100);
		ALT_OLY_RANK2_POINTS = events.getProperty("AltOlyRank2Points", 75);
		ALT_OLY_RANK3_POINTS = events.getProperty("AltOlyRank3Points", 55);
		ALT_OLY_RANK4_POINTS = events.getProperty("AltOlyRank4Points", 40);
		ALT_OLY_RANK5_POINTS = events.getProperty("AltOlyRank5Points", 30);
		ALT_OLY_MAX_POINTS = events.getProperty("AltOlyMaxPoints", 10);
		ALT_OLY_DIVIDER_CLASSED = events.getProperty("AltOlyDividerClassed", 3);
		ALT_OLY_DIVIDER_NON_CLASSED = events.getProperty("AltOlyDividerNonClassed", 3);
		ALT_OLY_ANNOUNCE_GAMES = events.getProperty("AltOlyAnnounceGames", true);
		
		ALT_GAME_CASTLE_DAWN = events.getProperty("AltCastleForDawn", true);
		ALT_GAME_CASTLE_DUSK = events.getProperty("AltCastleForDusk", true);
		ALT_FESTIVAL_MIN_PLAYER = MathUtil.limit(events.getProperty("AltFestivalMinPlayer", 5), 2, 9);
		ALT_MAXIMUM_PLAYER_CONTRIB = events.getProperty("AltMaxPlayerContrib", 1000000);
		ALT_FESTIVAL_MANAGER_START = events.getProperty("AltFestivalManagerStart", 120000);
		ALT_FESTIVAL_LENGTH = events.getProperty("AltFestivalLength", 1080000);
		ALT_FESTIVAL_CYCLE_LENGTH = events.getProperty("AltFestivalCycleLength", 2280000);
		ALT_FESTIVAL_FIRST_SPAWN = events.getProperty("AltFestivalFirstSpawn", 120000);
		ALT_FESTIVAL_FIRST_SWARM = events.getProperty("AltFestivalFirstSwarm", 300000);
		ALT_FESTIVAL_SECOND_SPAWN = events.getProperty("AltFestivalSecondSpawn", 540000);
		ALT_FESTIVAL_SECOND_SWARM = events.getProperty("AltFestivalSecondSwarm", 720000);
		ALT_FESTIVAL_CHEST_SPAWN = events.getProperty("AltFestivalChestSpawn", 900000);
		
		FS_TIME_ATTACK = events.getProperty("TimeOfAttack", 50);
		FS_TIME_ENTRY = events.getProperty("TimeOfEntry", 3);
		FS_TIME_WARMUP = events.getProperty("TimeOfWarmUp", 2);
		FS_PARTY_MEMBER_COUNT = MathUtil.limit(events.getProperty("NumberOfNecessaryPartyMembers", 4), 2, 9);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MUTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);
		
		ALLOW_WEDDING = events.getProperty("AllowWedding", false);
		WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);
		
		ALT_LOTTERY_PRIZE = events.getProperty("AltLotteryPrize", 50000);
		ALT_LOTTERY_TICKET_PRICE = events.getProperty("AltLotteryTicketPrice", 2000);
		ALT_LOTTERY_5_NUMBER_RATE = events.getProperty("AltLottery5NumberRate", 0.6);
		ALT_LOTTERY_4_NUMBER_RATE = events.getProperty("AltLottery4NumberRate", 0.2);
		ALT_LOTTERY_3_NUMBER_RATE = events.getProperty("AltLottery3NumberRate", 0.2);
		ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("AltLottery2and1NumberPrize", 200);
		
		ALT_FISH_CHAMPIONSHIP_ENABLED = events.getProperty("AltFishChampionshipEnabled", true);
		ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("AltFishChampionshipRewardItemId", 57);
		ALT_FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("AltFishChampionshipReward1", 800000);
		ALT_FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("AltFishChampionshipReward2", 500000);
		ALT_FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("AltFishChampionshipReward3", 300000);
		ALT_FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("AltFishChampionshipReward4", 200000);
		ALT_FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("AltFishChampionshipReward5", 100000);
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		ENABLE_GEODATA = geoengine.getProperty("EnableGeoData", false);
		GEODATA_PATH = geoengine.getProperty("GeoDataPath", "./data/geodata/");
		COORD_SYNCHRONIZE = geoengine.getProperty("CoordSynchronize", -1);
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		
		PATHFIND_BUFFERS = geoengine.getProperty("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
		BASE_WEIGHT = geoengine.getProperty("BaseWeight", 10);
		DIAGONAL_WEIGHT = geoengine.getProperty("DiagonalWeight", 14);
		OBSTACLE_MULTIPLIER = geoengine.getProperty("ObstacleMultiplier", 10);
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 20);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 3500);
		DEBUG_PATH = geoengine.getProperty("DebugPath", false);
		DEBUG_GEO_NODE = geoengine.getProperty("DebugGeoNode", false);
	}
	
	/**
	 * Loads gm File settings.
	 */
	private static final void loadGmFile()
	{
		final ExProperties gmfile = initProperties(GM_FILE);
		GM_STARTUP = gmfile.getProperty("GMStartProtections", true);
		DEFAULT_ACCESS_LEVEL = gmfile.getProperty("DefaultAccessLevel", 0);
		GM_HERO_AURA = gmfile.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = gmfile.getProperty("GMStartupInvulnerable", true);
		GM_STARTUP_INVISIBLE = gmfile.getProperty("GMStartupInvisible", true);
		GM_STARTUP_SILENCE = gmfile.getProperty("GMStartupSilence", true);
		GM_STARTUP_AUTO_LIST = gmfile.getProperty("GMStartupAutoList", true);
	}
	
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);
		
		CHANCE_FAIRY_LEAF = npcs.getProperty("Chance_Take_FairyLeaf", 0);
		
		CHAMPION_FREQUENCY = npcs.getProperty("ChampionFrequency", 0);
		CHAMP_MIN_LVL = npcs.getProperty("ChampionMinLevel", 20);
		CHAMP_MAX_LVL = npcs.getProperty("ChampionMaxLevel", 70);
		CHAMPION_HP = npcs.getProperty("ChampionHp", 8);
		CHAMPION_HP_REGEN = npcs.getProperty("ChampionHpRegen", 1.);
		CHAMPION_REWARDS = npcs.getProperty("ChampionRewards", 8);
		CHAMPION_ADENAS_REWARDS = npcs.getProperty("ChampionAdenasRewards", 1);
		CHAMPION_ATK = npcs.getProperty("ChampionAtk", 1.);
		CHAMPION_SPD_ATK = npcs.getProperty("ChampionSpdAtk", 1.);
		CHAMPION_REWARD = npcs.getProperty("ChampionRewardItem", 0);
		CHAMPION_REWARD_ID = npcs.getProperty("ChampionRewardItemID", 6393);
		CHAMPION_REWARD_QTY = npcs.getProperty("ChampionRewardItemQty", 1);
		
		ALLOW_CLASS_MASTERS = npcs.getProperty("AllowClassMasters", false);
		ALTERNATE_CLASS_MASTER = npcs.getProperty("AlternateClassMaster", false);
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		if (ALLOW_CLASS_MASTERS)
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", true);
		ALT_MOB_AGRO_IN_PEACEZONE = npcs.getProperty("AltMobAgroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_ALLOW_UPGRADER = npcs.getProperty("AllowWyvernUpgrader", true);
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 300);
		MIN_NPC_ANIMATION = npcs.getProperty("MinNPCAnimation", 20);
		MAX_NPC_ANIMATION = npcs.getProperty("MaxNPCAnimation", 40);
		MIN_MONSTER_ANIMATION = npcs.getProperty("MinMonsterAnimation", 10);
		MAX_MONSTER_ANIMATION = npcs.getProperty("MaxMonsterAnimation", 40);
		
		DISABLE_ATTACK_NPC_TYPE = Boolean.parseBoolean(npcs.getProperty("DisableAttackToNpcs", "False"));
		
		NPC_WITH_EFFECT = npcs.getProperty("NpcWithEffect", "20700");
		LIST_NPC_WITH_EFFECT = new ArrayList<>();
		for (String listid : NPC_WITH_EFFECT.split(","))
		{
			LIST_NPC_WITH_EFFECT.add(Integer.parseInt(listid));
		}
		
	}
	
	/**
	 * Loads options settings.
	 */
	private static final void loadOptions()
	{
		final ExProperties options = initProperties(OPTIONS_FILE);
		
		DEFAULT_GLOBAL_CHAT = options.getProperty("GlobalChat", "ON");
		DEFAULT_TRADE_CHAT = options.getProperty("TradeChat", "ON");
		
		DELETE_DAYS = options.getProperty("DeleteCharAfterDays", 7);
		AUTO_LOOT = options.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = options.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = options.getProperty("AutoLootRaid", false);
		
		ALLOW_DISCARDITEM = options.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = options.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = options.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = options.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = options.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = options.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = options.getProperty("PlayerDroppedItemMultiplier", 1);
		
		ALLOW_FREIGHT = options.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = options.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = options.getProperty("AllowWear", true);
		WEAR_DELAY = options.getProperty("WearDelay", 5);
		WEAR_PRICE = options.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = options.getProperty("AllowLottery", true);
		ALLOW_WATER = options.getProperty("AllowWater", true);
		ALLOW_MANOR = options.getProperty("AllowManor", true);
		ALLOW_BOAT = options.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = options.getProperty("AllowCursedWeapons", true);
		
		ENABLE_FALLING_DAMAGE = options.getProperty("EnableFallingDamage", true);
		
		ALT_DEV_NO_SPAWNS = options.getProperty("NoSpawns", false);
		DEBUG = options.getProperty("Debug", false);
		DEVELOPER = options.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = options.getProperty("PacketHandlerDebug", false);
		
		DEADLOCK_DETECTOR = options.getProperty("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = options.getProperty("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = options.getProperty("RestartOnDeadlock", false);
		
		LOG_CHAT = options.getProperty("LogChat", false);
		LOG_ITEMS = options.getProperty("LogItems", false);
		GMAUDIT = options.getProperty("GMAudit", false);
		
		ENABLE_COMMUNITY_BOARD = options.getProperty("EnableCommunityBoard", false);
		BBS_DEFAULT = options.getProperty("BBSDefault", "_bbshome");
		
		ROLL_DICE_TIME = options.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = options.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = options.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = options.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = options.getProperty("ServerBypassTime", 500);
		MULTISELL_TIME = options.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = options.getProperty("ManufactureTime", 300);
		MANOR_TIME = options.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = options.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = options.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = options.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = options.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = options.getProperty("SocialTime", 2000);
		
		SCHEDULED_THREAD_POOL_COUNT = options.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = options.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = options.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = options.getProperty("ThreadsPerInstantThreadPool", 2);
		
		L2WALKER_PROTECTION = options.getProperty("L2WalkerProtection", false);
		ZONE_TOWN = options.getProperty("ZoneTown", 0);
		SERVER_NEWS = options.getProperty("ShowServerNews", false);
		DISABLE_TUTORIAL = options.getProperty("DisableTutorial", false);
		
		USE_SAY_FILTER = Boolean.parseBoolean(options.getProperty("UseChatFilter", "false"));
		CHAT_FILTER_CHARS = options.getProperty("ChatFilterChars", "[I love L2JMDS]");
		CHAT_FILTER_PUNISHMENT = options.getProperty("ChatFilterPunishment", "off");
		CHAT_FILTER_PUNISHMENT_PARAM1 = Integer.parseInt(options.getProperty("ChatFilterPunishmentParam1", "1"));
		CHAT_FILTER_PUNISHMENT_PARAM2 = Integer.parseInt(options.getProperty("ChatFilterPunishmentParam2", "1000"));
		
		LineNumberReader lnr = null;
		try
		{
			FILTER_LIST.clear();
			
			File filter_file = new File(FILTER_FILE);
			if (!filter_file.exists())
			{
				return;
			}
			
			lnr = new LineNumberReader(new BufferedReader(new FileReader(filter_file)));
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				FILTER_LIST.add(line.trim());
			}
			_log.info("Chat Filter: Loaded " + FILTER_LIST.size() + " Filter Words.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Error("Failed to Load " + FILTER_FILE + " File.");
		}
		finally
		{
			if (lnr != null)
			{
				try
				{
					lnr.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Loads party settings.
	 */
	private static final void loadParty()
	{
		final ExProperties party = initProperties(PARTY_FILE);
		PARTY_XP_CUTOFF_METHOD = party.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = party.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = party.getProperty("PartyXpCutoffLevel", 20);
		ALT_PARTY_RANGE = party.getProperty("AltPartyRange", 1600);
		ALT_PARTY_RANGE2 = party.getProperty("AltPartyRange2", 1400);
		ALT_LEAVE_PARTY_LEADER = party.getProperty("AltLeavePartyLeader", false);
	}
	
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 0);
		RESPAWN_RESTORE_CP = players.getProperty("RespawnRestoreCP", 0.7);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALT_GAME_DELEVEL = players.getProperty("Delevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_QUEST_ITEMS = players.getProperty("MaximumSlotsForQuestItems", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		ALT_WEIGHT_LIMIT = players.getProperty("AltWeightLimit", 1);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		ALT_GAME_FREIGHTS = players.getProperty("AltGameFreights", false);
		ALT_GAME_FREIGHT_PRICE = players.getProperty("AltGameFreightPrice", 1000);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		ALT_BLACKSMITH_USE_RECIPES = players.getProperty("AltBlacksmithUseRecipes", true);
		
	}
	
	/**
	 * Loads pvp settings.
	 */
	private static final void loadPvP()
	{
		final ExProperties pvp = initProperties(PVP_FILE);
		KARMA_PLAYER_CAN_BE_KILLED_IN_PZ = pvp.getProperty("KarmaPlayerCanBeKilledInPeaceZone", false);
		KARMA_PLAYER_CAN_SHOP = pvp.getProperty("KarmaPlayerCanShop", false);
		KARMA_PLAYER_CAN_USE_GK = pvp.getProperty("KarmaPlayerCanUseGK", false);
		PLAYER_FLAG_CAN_USE_GK = pvp.getProperty("FlagPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = pvp.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = pvp.getProperty("KarmaPlayerCanTrade", true);
		KARMA_PLAYER_CAN_USE_WH = pvp.getProperty("KarmaPlayerCanUseWareHouse", true);
		KARMA_DROP_GM = pvp.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = pvp.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = pvp.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = pvp.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
		KARMA_NONDROPPABLE_ITEMS = pvp.getProperty("ListOfNonDroppableItemsForPK", "1147,425,1146,461,10,2368,7,6,2370,2369");
		
		String[] array = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(array[i]);
		
		array = KARMA_NONDROPPABLE_ITEMS.split(",");
		KARMA_LIST_NONDROPPABLE_ITEMS = new int[array.length];
		
		for (int i = 0; i < array.length; i++)
			KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(array[i]);
		
		// sorting so binarySearch can be used later
		Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
		Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);
		
		PVP_NORMAL_TIME = pvp.getProperty("PvPVsNormalTime", 15000);
		PVP_PVP_TIME = pvp.getProperty("PvPVsPvPTime", 30000);
		
		SHOW_HP_PVP = Boolean.parseBoolean(pvp.getProperty("ShowHpPvP", "False"));
		ANNOUNCE_PVP_KILL = Boolean.parseBoolean(pvp.getProperty("AnnouncePvPKill", "False"));
		ANNOUNCE_PK_KILL = Boolean.parseBoolean(pvp.getProperty("AnnouncePkKill", "False"));
		ANNOUNCE_TYPE_MSG_PVP = pvp.getProperty("AnnounceTypesPvP", "Derrotou");
		LIST_OF_TYPES_PVP = new ArrayList<>();
		for (String type : ANNOUNCE_TYPE_MSG_PVP.split(","))
		{
			LIST_OF_TYPES_PVP.add(type);
		}
		ANNOUNCE_TYPE_MSG_PVP = null;
		
		ANNOUNCE_TYPE_MSG_PK = pvp.getProperty("AnnounceTypesPk", "Assassinou");
		LIST_OF_TYPES_PK = new ArrayList<>();
		for (String type2 : ANNOUNCE_TYPE_MSG_PK.split(","))
		{
			LIST_OF_TYPES_PK.add(type2);
		}
		ANNOUNCE_TYPE_MSG_PK = null;
		
		ENABLE_PVP_COLOR = pvp.getProperty("EnablePvpColor", false);
		ENABLE_PK_COLOR = pvp.getProperty("EnablePkColor", false);
		
		ALLOW_QUAKE_SYSTEM = Boolean.parseBoolean(pvp.getProperty("AllowQuakeSystem", "False"));
		PVP_COLOR_ANNOUNCE = Integer.parseInt(pvp.getProperty("ColorAnnounce", "16"));
		
		PVP_REWARD_ENABLED = Boolean.valueOf(pvp.getProperty("PvpRewardEnabled", "false"));
		PVP_REWARD_ID = Integer.parseInt(pvp.getProperty("PvpRewardItemId", "6392"));
		PVP_REWARD_AMOUNT = Integer.parseInt(pvp.getProperty("PvpRewardAmmount", "1"));
		
		PK_REWARD_ENABLED = Boolean.valueOf(pvp.getProperty("PKRewardEnabled", "false"));
		PK_REWARD_ID = Integer.parseInt(pvp.getProperty("PKRewardItemId", "6392"));
		PK_REWARD_AMOUNT = Integer.parseInt(pvp.getProperty("PKRewardAmmount", "1"));
		
		ANTI_FARM_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmEnabled", "False"));
		ANTI_FARM_CLAN_ALLY_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmClanAlly", "False"));
		ANTI_FARM_LVL_DIFF_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmLvlDiff", "False"));
		ANTI_FARM_MAX_LVL_DIFF = Integer.parseInt(pvp.getProperty("AntiFarmMaxLvlDiff", "40"));
		ANTI_FARM_PDEF_DIFF_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmPdefDiff", "False"));
		ANTI_FARM_MAX_PDEF_DIFF = Integer.parseInt(pvp.getProperty("AntiFarmMaxPdefDiff", "300"));
		ANTI_FARM_PATK_DIFF_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmPatkDiff", "False"));
		ANTI_FARM_MAX_PATK_DIFF = Integer.parseInt(pvp.getProperty("AntiFarmMaxPatkDiff", "300"));
		ANTI_FARM_PARTY_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmParty", "False"));
		ANTI_FARM_IP_ENABLED = Boolean.parseBoolean(pvp.getProperty("AntiFarmIP", "False"));
		ANTI_FARM_SUMMON = Boolean.parseBoolean(pvp.getProperty("AntiFarmSummon", "False"));
		
		LEAVE_BUFFS_ON_DIE = Boolean.parseBoolean(pvp.getProperty("LeaveBuffsOnDie", "True"));
		LEAVE_BUFF_SOLOFARM = Boolean.parseBoolean(pvp.getProperty("LeaveBuffsOnDie_inSoloFarm", "True"));
		CUSTOM_TELEGIRAN_ON_DIE = Boolean.parseBoolean(pvp.getProperty("CustomTeleportOnDie", "false"));
		
	}
	
	/**
	 * Loads rates settings.
	 */
	private static final void loadRates()
	{
		final ExProperties rates = initProperties(RATES_FILE);
		
		RATE_DROP_SEAL_STONES = Float.parseFloat(rates.getProperty("RateDropSealStones", "1.00"));
		RATE_XP = rates.getProperty("RateXp", 1.);
		RATE_SP = rates.getProperty("RateSp", 1.);
		RATE_PARTY_XP = rates.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = rates.getProperty("RatePartySp", 1.);
		RATE_DROP_ADENA = rates.getProperty("RateDropAdena", 1.);
		RATE_CONSUMABLE_COST = rates.getProperty("RateConsumableCost", 1.);
		RATE_DROP_ITEMS = rates.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = rates.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_SPOIL = rates.getProperty("RateDropSpoil", 1.);
		RATE_DROP_MANOR = rates.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = rates.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_REWARD = rates.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = rates.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = rates.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = rates.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = rates.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = rates.getProperty("RateSiegeGuardsPrice", 1.);
		RATE_DROP_COMMON_HERBS = rates.getProperty("RateCommonHerbs", 1.);
		RATE_DROP_HP_HERBS = rates.getProperty("RateHpHerbs", 1.);
		RATE_DROP_MP_HERBS = rates.getProperty("RateMpHerbs", 1.);
		RATE_DROP_SPECIAL_HERBS = rates.getProperty("RateSpecialHerbs", 1.);
		PLAYER_DROP_LIMIT = rates.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = rates.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = rates.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = rates.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = rates.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = rates.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = rates.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = rates.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = rates.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = rates.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = rates.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = rates.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = rates.getProperty("KarmaRateDropEquipWeapon", 10);
	}
	
	/**
	 * Loads siege settings.
	 */
	private static final void loadSieges()
	{
		final ExProperties sieges = initProperties(Config.SIEGE_FILE);
		
		ENABLE_WINNNER_REWARD_SIEGE_CLAN = sieges.getProperty("EnableRewardWinnerClan", false);
		REWARD_WINNER_SIEGE_CLAN = parseItemsList(sieges.getProperty("PlayerRewardsID", "57,100"));
		LEADER_REWARD_WINNER_SIEGE_CLAN = parseItemsList(sieges.getProperty("LeaderRewardsID", "57,400"));
		
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);
		
		DAY_TO_SIEGE = Integer.parseInt(sieges.getProperty("DayToSiege", "2"));
		
		SIEGE_HOUR_GLUDIO = Integer.parseInt(sieges.getProperty("HourToSiege_Gludio", "16"));
		SIEGE_HOUR_DION = Integer.parseInt(sieges.getProperty("HourToSiege_Dion", "16"));
		SIEGE_HOUR_GIRAN = Integer.parseInt(sieges.getProperty("HourToSiege_Giran", "16"));
		SIEGE_HOUR_OREN = Integer.parseInt(sieges.getProperty("HourToSiege_Oren", "16"));
		SIEGE_HOUR_ADEN = Integer.parseInt(sieges.getProperty("HourToSiege_Aden", "16"));
		SIEGE_HOUR_INNADRIL = Integer.parseInt(sieges.getProperty("HourToSiege_Innadril", "16"));
		SIEGE_HOUR_GODDARD = Integer.parseInt(sieges.getProperty("HourToSiege_Goddard", "16"));
		SIEGE_HOUR_RUNE = Integer.parseInt(sieges.getProperty("HourToSiege_Rune", "16"));
		SIEGE_HOUR_SCHUT = Integer.parseInt(sieges.getProperty("HourToSiege_Schuttgart", "16"));
		
		SIEGE_DAY_GLUDIO = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Gludio", "1"));
		SIEGE_DAY_DION = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Dion", "7"));
		SIEGE_DAY_GIRAN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Giran", "1"));
		SIEGE_DAY_OREN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Oren", "7"));
		SIEGE_DAY_ADEN = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Aden", "1"));
		SIEGE_DAY_INNADRIL = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Innadril", "7"));
		SIEGE_DAY_GODDARD = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Goddard", "1"));
		SIEGE_DAY_RUNE = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Rune", "7"));
		SIEGE_DAY_SCHUT = Integer.parseInt(sieges.getProperty("DayOfTheWeek_Schuttgart", "1"));
		
		SIEGE_GLUDIO = sieges.getProperty("Siege_Gludio", false);
		SIEGE_DION = sieges.getProperty("Siege_Dion", false);
		SIEGE_GIRAN = sieges.getProperty("Siege_Giran", false);
		SIEGE_OREN = sieges.getProperty("Siege_Oren", false);
		SIEGE_ADEN = sieges.getProperty("Siege_Aden", false);
		SIEGE_INNADRIL = sieges.getProperty("Siege_Innadril", false);
		SIEGE_GODDARD = sieges.getProperty("Siege_Goddard", false);
		SIEGE_RUNE = sieges.getProperty("Siege_Rune", false);
		SIEGE_SCHUT = sieges.getProperty("Siege_Schuttgart", false);
		
	}
	
	/**
	 * Loads Skills settings.
	 */
	private static final void loadSkills()
	{
		final ExProperties skills = initProperties(SKILLS_FILE);
		ALT_SHOW_REUSE_MSG = skills.getProperty("AltShowSkillReuseMessage", true);
		AUTO_LEARN_SKILLS = skills.getProperty("AutoLearnSkills", false);
		AUTO_LEARN_DIVINE_INSPIRATION = Boolean.parseBoolean(skills.getProperty("AutoLearnDivineInspiration", "false"));
		ALT_GAME_MAGICFAILURES = skills.getProperty("MagicFailures", true);
		ALT_GAME_SHIELD_BLOCKS = skills.getProperty("AltShieldBlocks", false);
		ALT_PERFECT_SHLD_BLOCK = skills.getProperty("AltPerfectShieldBlockRate", 10);
		LIFE_CRYSTAL_NEEDED = skills.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = skills.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = skills.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = skills.getProperty("DivineInspirationSpBookNeeded", true);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = skills.getProperty("AltSubClassWithoutQuests", false);
		
		BUFFS_MAX_AMOUNT = skills.getProperty("MaxBuffsAmount", 20);
		STORE_SKILL_COOLTIME = skills.getProperty("StoreSkillCooltime", true);
		
		BUFFER_MAX_SCHEMES = skills.getProperty("BufferMaxSchemesPerChar", 4);
		BUFFER_STATIC_BUFF_COST = skills.getProperty("BufferStaticCostPerBuff", -1);
		
		FIGHTER_BUFF = skills.getProperty("FighterBuffList", "0");
		FIGHTER_BUFF_LIST = new ArrayList<>();
		for (String id : FIGHTER_BUFF.trim().split(","))
		{
			FIGHTER_BUFF_LIST.add(Integer.parseInt(id.trim()));
		}
		
		MAGE_BUFF = skills.getProperty("MageBuffList", "0");
		MAGE_BUFF_LIST = new ArrayList<>();
		for (String id : MAGE_BUFF.trim().split(","))
		{
			MAGE_BUFF_LIST.add(Integer.parseInt(id.trim()));
		}
		
		PROTECT_BUFF = skills.getProperty("ProtectBuffList", "0");
		PROTECT_BUFF_LIST = new ArrayList<>();
		for (String id : PROTECT_BUFF.trim().split(","))
		{
			PROTECT_BUFF_LIST.add(Integer.parseInt(id.trim()));
		}
		
		HEROIC_COIN_ID = skills.getProperty("HeroicCoin", 9502);
		HEROIC_COIN_PRICE = skills.getProperty("HeroicPrice", 10);
		
		ENABLE_MODIFY_SKILL_DURATION = Boolean.parseBoolean(skills.getProperty("EnableModifySkillDuration", "false"));
		if (ENABLE_MODIFY_SKILL_DURATION)
		{
			SKILL_DURATION_LIST = new HashMap<>();
			
			String[] propert;
			propert = skills.getProperty("SkillDurationList", "").split(";");
			
			for (String skill : propert)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						if (Config.DEBUG)
							nfe.printStackTrace();
						
						if (!skill.equals(""))
						{
							System.out.println("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		
		ALLOWED_SUBCLASS = Integer.parseInt(skills.getProperty("AllowedSubclass", "3"));
		ADD_SKILL_NOBLES = Boolean.parseBoolean(skills.getProperty("Add_Skill_Noble", "False"));
		
		ALT_RESTORE_EFFECTS_ON_SUBCLASS_CHANGE = Boolean.parseBoolean(skills.getProperty("AltRestoreEffectOnSub", "False"));
		ALT_GAME_SUBCLASS_EVERYWHERE = skills.getProperty("AltSubclassEverywhere", false);
		
		/** CHECK SKILLS ON ENTER */
		CHECK_SKILLS_ON_ENTER = skills.getProperty("CheckSkillsOnEnter", false);
		CHECK_HERO_SKILLS = skills.getProperty("CheckHeroSkills", true);
		CHECK_NOBLE_SKILLS = skills.getProperty("CheckNobleSkills", true);
		NON_CHECK_SKILLS = skills.getProperty("NonCheckSkills", "10000");
		LIST_NON_CHECK_SKILLS = new ArrayList<>();
		for (String id : NON_CHECK_SKILLS.split(","))
		{
			LIST_NON_CHECK_SKILLS.add(Integer.parseInt(id));
		}
		
	}
	
	/**
	 * Loads L2JMega settings.
	 */
	private static final void loadL2JMega()
	{
		final ExProperties l2jmega = initProperties(L2JMEGA_FILE);
		
		ANNOUNCE_DROP_ITEM = Boolean.parseBoolean(l2jmega.getProperty("AnnounceDropItem", "false"));
		GM_VIEW_PL_ON = Boolean.parseBoolean(l2jmega.getProperty("PlayersOnlineScreemMSG", "false"));
		NAME_TVT = l2jmega.getProperty("NameTvTEvent", "TvT:");
		NAME_TOUR = l2jmega.getProperty("NameTournament", "Tournament:");
		NAME_CTF = l2jmega.getProperty("NameCTFEvent", "CTF:");
		NAME_PVP = l2jmega.getProperty("NamePvPEvent", "PvPEvent:");
		NAME_EVENT = l2jmega.getProperty("NamePartyZone", "PartyZone:");
		
		ANNOUNCE_ID_EVENT = Integer.parseInt(l2jmega.getProperty("AnnounceIdEvents", "3"));
		BLOCK_AIOX_PVPZONE = Boolean.parseBoolean(l2jmega.getProperty("BlockAiox_inPvpZone", "false"));
		USE_AIOX_OUTSIDE_PEACEZONE = Boolean.parseBoolean(l2jmega.getProperty("UseAioOnlyPeaceZone", "false"));
		
		ENABLE_FLAGZONE = Boolean.parseBoolean(l2jmega.getProperty("EnableFlag_inRaid_inFlagZone", "false"));
		ENABLE_CHAT_LEVEL = Boolean.parseBoolean(l2jmega.getProperty("EnableChatLevel", "True"));
		CHAT_LEVEL = Integer.parseInt(l2jmega.getProperty("ChatLevel", "80"));
		CMD_SKIN = Boolean.parseBoolean(l2jmega.getProperty("EnableSkinCMD", "True"));
		ENABLE_AIO_SYSTEM = Boolean.parseBoolean(l2jmega.getProperty("EnableAioSystem", "True"));
		AIO_TITLE = l2jmega.getProperty("AioTitle", "Aio");
		CHANGE_AIO_NAME = Boolean.parseBoolean(l2jmega.getProperty("ChangeAioName", "True"));
		ALLOW_AIO_NCOLOR = Boolean.parseBoolean(l2jmega.getProperty("AllowAioNameColor", "True"));
		AIO_NCOLOR = Integer.decode("0x" + l2jmega.getProperty("AioNameColor", "88AA88")).intValue();
		ALLOW_AIO_TCOLOR = Boolean.parseBoolean(l2jmega.getProperty("AllowAioTitleColor", "True"));
		AIO_TCOLOR = Integer.decode("0x" + l2jmega.getProperty("AioTitleColor", "88AA88")).intValue();
		AIO_ITEMID = Integer.parseInt(l2jmega.getProperty("ItemIdAio", "9945"));
		ALLOW_AIO_ITEM = Boolean.parseBoolean(l2jmega.getProperty("AllowAIOItem", "False"));
		ALLOW_AIOX_SET_ITEM = Boolean.parseBoolean(l2jmega.getProperty("AllowItems", "False"));
		if (ALLOW_AIOX_SET_ITEM) {
			String[] propertySplit4 = l2jmega.getProperty("AioxItems", "57,0").split(";");
			AIO_CHAR_ITENS.clear();
			for (String reward : propertySplit4) {
				String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2) {
					_log.warning("AioxItems[Config.load()]: invalid config property -> AioxItems \"" + reward + "\"");
				} else {
					try {
						AIO_CHAR_ITENS.add(new int[] { Integer.parseInt(rewardSplit[0]), 
							Integer.parseInt(rewardSplit[1]) });
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
						if (!reward.isEmpty())
							_log.warning("AioxItems[Config.load()]: invalid config property -> AioxItems \"" + reward + "\""); 
					} 
				} 
			} 
		} 
		if (ENABLE_AIO_SYSTEM) {
			String[] AioSkillsSplit = l2jmega.getProperty("AioSkills", "").split(";");
			AIO_SKILLS = new HashMap<>(AioSkillsSplit.length);
			for (String skill : AioSkillsSplit) {
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2) {
					System.out.println("[Aio System]: invalid config property in players.properties -> AioSkills \"" + skill + "\"");
				} else {
					try {
						AIO_SKILLS.put(Integer.valueOf(Integer.parseInt(skillSplit[0])), Integer.valueOf(Integer.parseInt(skillSplit[1])));
					} catch (NumberFormatException nfe) {
						if (!skill.equals(""))
							System.out.println("[Aio System]: invalid config property in players.props -> AioSkills \"" + skillSplit[0] + "\"" + skillSplit[1]); 
					} 
				} 
			} 
		} 
		
		DONATESYSTEM = l2jmega.getProperty("DonateSystem", false);
		DONATE_HTML = l2jmega.getProperty("Donate_Html", false);
		
		DONATE_COIN_ID = Integer.parseInt(l2jmega.getProperty("DonateCoin_Id", "9511"));
		
		ENABLE_CHAT_VIP = l2jmega.getProperty("ChatVIP", false);
		VIP_SKINS = l2jmega.getProperty("SkinsVIP", false);
		VIP_SKILL = Boolean.parseBoolean(l2jmega.getProperty("Vip_Skill", "False"));
		VIP_EFFECT = Boolean.parseBoolean(l2jmega.getProperty("Vip_Effect", "False"));
		VIP_XP_SP_RATES = l2jmega.getProperty("VipExp/SpRates", 1000);
		ENABLE_DROP_VIP = Boolean.parseBoolean(l2jmega.getProperty("EnableDropVIP", "False"));
		DROP_ITEM_VIP = l2jmega.getProperty("ListDropVIPItens", "");
		DROP_LIST_VIP = new ArrayList<>();
		for (String itemId : DROP_ITEM_VIP.split(","))
			DROP_LIST_VIP.add(Integer.parseInt(itemId));
		RATE_DROP_VIP = l2jmega.getProperty("RateDropVIP", 1);
		
		VIP_30_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Vip_30_Days_Price", "30"));
		VIP_60_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Vip_60_Days_Price", "60"));
		VIP_90_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Vip_90_Days_Price", "90"));
		VIP_ETERNAL_PRICE = Integer.parseInt(l2jmega.getProperty("Vip_Eternal_Price", "120"));
		
		HERO_30_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Hero_30_Days_Price", "30"));
		HERO_60_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Hero_60_Days_Price", "60"));
		HERO_90_DAYS_PRICE = Integer.parseInt(l2jmega.getProperty("Hero_90_Days_Price", "90"));
		HERO_ETERNAL_PRICE = Integer.parseInt(l2jmega.getProperty("Hero_Eternal_Price", "120"));
		
		MIN_ENCHANT = l2jmega.getProperty("Min_Enchant_Donate", 0);
		MAX_ENCHANT = l2jmega.getProperty("Max_Enchant_Donate", 0);
		DONATE_ENCHANT_PRICE = Integer.parseInt(l2jmega.getProperty("Enchant_Price", "120"));
		
		DONATE_NOBLE_PRICE = Integer.parseInt(l2jmega.getProperty("Noble_Price", "120"));
		DONATE_CLASS_PRICE = Integer.parseInt(l2jmega.getProperty("Change_Class_Price", "15"));
		DONATE_NAME_PRICE = Integer.parseInt(l2jmega.getProperty("Change_Name_Price", "15"));
		DONATE_SEX_PRICE = Integer.parseInt(l2jmega.getProperty("Change_Sex_Price", "15"));
		DONATE_ITEM = Integer.parseInt(l2jmega.getProperty("Coin_for_Status", "9511"));
		DONATE_ITEM_COUNT = Integer.parseInt(l2jmega.getProperty("Change_Status_Price", "15"));
		
		DONATE_ITEM_SKILL = Integer.parseInt(l2jmega.getProperty("Coin_for_Skills", "9511"));
		DONATE_ITEM_COUNT_SKILL = Integer.parseInt(l2jmega.getProperty("Change_Skills_Price", "15"));
		
		ANNOUNCE_HERO_ON_ENTER = Boolean.parseBoolean(l2jmega.getProperty("AnnounceHeroOnEnter", "false"));
		ANNOUNCE_HERO_ON_CUSTOM = Boolean.parseBoolean(l2jmega.getProperty("AnnounceHeroOnCustom", "false"));
		ANNOUNCE_CASTLE_LORDS = Boolean.parseBoolean(l2jmega.getProperty("AnnounceCastleLords", "false"));
		ANNOUNCE_HALL_OWNERS = Boolean.parseBoolean(l2jmega.getProperty("AnnounceEnterLeadersHall", "False"));
		
		ID_ITEM_ENCHANT = l2jmega.getProperty("ItemList_DropEnchanted");
		LIST_ENCHANT_ITEM = new ArrayList<>();
		for (String id : ID_ITEM_ENCHANT.trim().split(","))
		{
			LIST_ENCHANT_ITEM.add(Integer.parseInt(id.trim()));
		}
		
		ENCHANT_LEVEL = Integer.parseInt(l2jmega.getProperty("Enchant_level", "0"));
		
		START_VIP = l2jmega.getProperty("Start_vip_24hrs", false);
		
		STATUS_CMD = l2jmega.getProperty("StatusCMD", false);
		
		NEWS_1 = l2jmega.getProperty("News_url", "");
		NEWS_2 = l2jmega.getProperty("News2_url", "");
		NEWS_3 = l2jmega.getProperty("News3_url", "");
		NEWS_4 = l2jmega.getProperty("News4_url", "");
		DONATE_URL = l2jmega.getProperty("Donate_url", "");
		INFO_URL = l2jmega.getProperty("Info_url", "");
		
		ITEM_OBSERVER = Integer.parseInt(l2jmega.getProperty("Item_Observer", "0"));
		ITEM_BUY_QUANT_OBSERVER = Integer.parseInt(l2jmega.getProperty("Amount_Observer", "0"));
		
		ITEM_1 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_1", "0"));
		CHANCE_ITEM_1 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_1", "0"));
		ITEM_QUANT_1 = Integer.parseInt(l2jmega.getProperty("Amount_1", "0"));
		ITEM_2 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_2", "0"));
		CHANCE_ITEM_2 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_2", "0"));
		ITEM_QUANT_2 = Integer.parseInt(l2jmega.getProperty("Amount_2", "0"));
		ITEM_3 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_3", "0"));
		CHANCE_ITEM_3 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_3", "0"));
		ITEM_QUANT_3 = Integer.parseInt(l2jmega.getProperty("Amount_3", "0"));
		ITEM_4 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_4", "0"));
		CHANCE_ITEM_4 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_4", "0"));
		ITEM_QUANT_4 = Integer.parseInt(l2jmega.getProperty("Amount_4", "0"));
		ITEM_5 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_5", "0"));
		CHANCE_ITEM_5 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_5", "0"));
		ITEM_QUANT_5 = Integer.parseInt(l2jmega.getProperty("Amount_5", "0"));
		ITEM_6 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_6", "0"));
		CHANCE_ITEM_6 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_6", "0"));
		ITEM_QUANT_6 = Integer.parseInt(l2jmega.getProperty("Amount_6", "0"));
		ITEM_7 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_7", "0"));
		CHANCE_ITEM_7 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_7", "0"));
		ITEM_QUANT_7 = Integer.parseInt(l2jmega.getProperty("Amount_7", "0"));
		ITEM_8 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_8", "0"));
		CHANCE_ITEM_8 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_8", "0"));
		ITEM_QUANT_8 = Integer.parseInt(l2jmega.getProperty("Amount_8", "0"));
		ITEM_9 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_9", "0"));
		CHANCE_ITEM_9 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_9", "0"));
		ITEM_QUANT_9 = Integer.parseInt(l2jmega.getProperty("Amount_9", "0"));
		ITEM_10 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_10", "0"));
		CHANCE_ITEM_10 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_10", "0"));
		ITEM_QUANT_10 = Integer.parseInt(l2jmega.getProperty("Amount_10", "0"));
		ITEM_11 = Integer.parseInt(l2jmega.getProperty("LuckBox_item_11", "0"));
		CHANCE_ITEM_11 = Integer.parseInt(l2jmega.getProperty("Chance_LuckBox_11", "0"));
		ITEM_QUANT_11 = Integer.parseInt(l2jmega.getProperty("Amount_11", "0"));
		
		
		MSG_CHAT_TRADE = l2jmega.getProperty("MsgChatVip", "");
		
		INFINITY_SS = l2jmega.getProperty("InfinitySS", false);
		INFINITY_ARROWS = l2jmega.getProperty("InfinityArrows", false);
		
		HERO_SKILL_SUB = Boolean.parseBoolean(l2jmega.getProperty("AllowHeroSkills_inSubClas", "false"));
		
		ALLOW_MANUTENCAO = Boolean.parseBoolean(l2jmega.getProperty("AllowManutencao", "false"));
		MANUTENCAO_TEXT = l2jmega.getProperty("ManutencaoMessageText", "Servidor em Manutencao!");
		
		CHAR_TITLE = Boolean.parseBoolean(l2jmega.getProperty("CharTitle", "false"));
		ADD_CHAR_TITLE = l2jmega.getProperty("TitleNewChar", "Welcome");
		ADD_NAME_COLOR = l2jmega.getProperty("NameColor", "FFFFFF");
		ADD_TITLE_COLOR = l2jmega.getProperty("TitleColor", "FFFFFF");
		SPAWN_CHAR = Boolean.parseBoolean(l2jmega.getProperty("CustomSpawn", "false"));
		
		SPAWN_X_1 = Integer.parseInt(l2jmega.getProperty("SpawnX_1", ""));
		SPAWN_Y_1 = Integer.parseInt(l2jmega.getProperty("SpawnY_1", ""));
		SPAWN_Z_1 = Integer.parseInt(l2jmega.getProperty("SpawnZ_1", ""));
		
		SPAWN_X_2 = Integer.parseInt(l2jmega.getProperty("SpawnX_2", ""));
		SPAWN_Y_2 = Integer.parseInt(l2jmega.getProperty("SpawnY_2", ""));
		SPAWN_Z_2 = Integer.parseInt(l2jmega.getProperty("SpawnZ_2", ""));
		
		SPAWN_X_3 = Integer.parseInt(l2jmega.getProperty("SpawnX_3", ""));
		SPAWN_Y_3 = Integer.parseInt(l2jmega.getProperty("SpawnY_3", ""));
		SPAWN_Z_3 = Integer.parseInt(l2jmega.getProperty("SpawnZ_3", ""));
		
		SPAWN_X_4 = Integer.parseInt(l2jmega.getProperty("SpawnX_4", ""));
		SPAWN_Y_4 = Integer.parseInt(l2jmega.getProperty("SpawnY_4", ""));
		SPAWN_Z_4 = Integer.parseInt(l2jmega.getProperty("SpawnZ_4", ""));
		
		ALLOW_CREATE_LVL = Boolean.parseBoolean(l2jmega.getProperty("CustomStartingLvl", "False"));
		CHAR_CREATE_LVL = Integer.parseInt(l2jmega.getProperty("CharLvl", "80"));
		CUSTOM_SUBCLASS_LVL = l2jmega.getProperty("CustomSubclassLvl", 40);
		
		CUSTOM_STARTER_ITEMS_ENABLED = Boolean.parseBoolean(l2jmega.getProperty("CustomStarterItemsEnabled", "False"));
		if (Config.CUSTOM_STARTER_ITEMS_ENABLED)
		{
			String[] propertySplit1 = l2jmega.getProperty("StartingCustomItemsMage", "57,0").split(";");
			STARTING_CUSTOM_ITEMS_M.clear();
			for (String reward : propertySplit1)
			{
				String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2)
					_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
				else
				{
					try
					{
						STARTING_CUSTOM_ITEMS_M.add(new int[]
							{
							Integer.parseInt(rewardSplit[0]),
							Integer.parseInt(rewardSplit[1])
							});
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						if (!reward.isEmpty())
							_log.warning("StartingCustomItemsMage[Config.load()]: invalid config property -> StartingCustomItemsMage \"" + reward + "\"");
					}
				}
			}
			
			String[] propertySplit2 = l2jmega.getProperty("StartingCustomItemsFighter", "57,0").split(";");
			STARTING_CUSTOM_ITEMS_F.clear();
			for (String reward : propertySplit2)
			{
				String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2)
					_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
				else
				{
					try
					{
						STARTING_CUSTOM_ITEMS_F.add(new int[]
							{
							Integer.parseInt(rewardSplit[0]),
							Integer.parseInt(rewardSplit[1])
							});
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						
						if (!reward.isEmpty())
							_log.warning("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
					}
				}
			}
		}
		
		DISABLE_CHAT = Boolean.valueOf(l2jmega.getProperty("DisableChat", "false"));
		DISABLE_CAPSLOCK = Boolean.valueOf(l2jmega.getProperty("DisableCapsLock", "false"));
		TRADE_CHAT_WITH_PVP = Boolean.valueOf(l2jmega.getProperty("TradeChatWithPvP", "false"));
		TRADE_PVP_AMOUNT = Integer.parseInt(l2jmega.getProperty("TradePvPAmount", "50"));
		GLOBAL_CHAT_WITH_PVP = Boolean.valueOf(l2jmega.getProperty("GlobalChatWithPvP", "false"));
		GLOBAL_PVP_AMOUNT = Integer.parseInt(l2jmega.getProperty("GlobalPvPAmount", "500"));
		CUSTOM_GLOBAL_CHAT_TIME = Integer.parseInt(l2jmega.getProperty("interval_ChatGlobal", "18000"));
		CUSTOM_HERO_CHAT_TIME = Integer.parseInt(l2jmega.getProperty("interval_ChatHero", "18000"));
		
		TALK_CHAT_ALL_CONFIG = Boolean.valueOf(l2jmega.getProperty("ChatAll_Protection", "false"));
		TALK_CHAT_ALL_TIME = Integer.parseInt(l2jmega.getProperty("Talk_ChatAll_Time", "15"));
		
		WYVERN_PVPZONE = Boolean.parseBoolean(l2jmega.getProperty("WyverbPvPZone", "False"));
		PARTY_PVPZONE = Boolean.parseBoolean(l2jmega.getProperty("DisablePartyPvPZone", "False"));
		PARTY_FLAGZONE = Boolean.parseBoolean(l2jmega.getProperty("DisablePartyFlagZone", "False"));
		OPEN_URL_ENABLE = l2jmega.getProperty("OpenUrlEnable", true);
		OPEN_URL_SITE = l2jmega.getProperty("OpenUrlSite", "");
		
		WYVERN_PROTECTION = Boolean.parseBoolean(l2jmega.getProperty("WyvernProtectionEnabled", "False"));
		ID_RESTRICT = l2jmega.getProperty("WyvernItemID", "10");
		LISTID_RESTRICT = new ArrayList<>();
		for (String classId : ID_RESTRICT.split(","))
		{
			LISTID_RESTRICT.add(Integer.parseInt(classId));
		}
		
		TITLE_MONSTER_SOLO = l2jmega.getProperty("Title_MonsterFarm", "");
		
		CUSTOM_DROP = Boolean.parseBoolean(l2jmega.getProperty("CustomDrop", "False"));
		
		CUSTOM_DROP_ITEM = l2jmega.getProperty("CustomDropItens", "");
		CUSTOM_DROP_LIST = new ArrayList<>();
		for (String itemId : CUSTOM_DROP_ITEM.split(","))
		{
			CUSTOM_DROP_LIST.add(Integer.parseInt(itemId));
		}
		
		CUSTOM_MONSTER = l2jmega.getProperty("List_Monster", "0");
		LIST_NPC_CUSTOM_MONSTER = new ArrayList<>();
		for (String listid : CUSTOM_MONSTER.split(","))
		{
			LIST_NPC_CUSTOM_MONSTER.add(Integer.parseInt(listid));
		}
		
		String[] a = l2jmega.getProperty("DropList_Monster", "57,0").split(";");
		CUSTOM_MONSTER_DROP.clear();
		for (String reward : a)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
				_log.warning("DropList_Monster ERROR");
			else
			{
				try
				{
					CUSTOM_MONSTER_DROP.add(new int[]
						{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
						});
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("DropList_Monster ERROR");
				}
			}
		}
		
		DROP_PROTECTED_TIME = Integer.parseInt(l2jmega.getProperty("DropProtectedTime", "15"));
		
		DISABLE_GRADE_PENALTY = Boolean.parseBoolean(l2jmega.getProperty("DisableGradePenalty", "False"));
		DISABLE_WEIGHT_PENALTY = Boolean.parseBoolean(l2jmega.getProperty("DisableWeightPenalty", "False"));
		
		RESET_DAILY_ENABLED = l2jmega.getProperty("ResetDailyEnabled", false);
		RESET_DAILY_TIME = l2jmega.getProperty("ResetDailyStartTime", "20:00").split(",");
		
		RESTART_BY_TIME_OF_DAY = Boolean.parseBoolean(l2jmega.getProperty("EnableRestartSystem", "false"));
		RESTART_SECONDS = Integer.parseInt(l2jmega.getProperty("RestartSeconds", "360"));
		RESTART_INTERVAL_BY_TIME_OF_DAY = l2jmega.getProperty("RestartByTimeOfDay", "20:00").split(",");
		
		COLOR_WITH_ITEM = Boolean.parseBoolean(l2jmega.getProperty("ColorWithItem", "true"));
		COLOR_ITEM_ID = Integer.parseInt(l2jmega.getProperty("ColorItemID", "9325"));
		COLOR_NAME_ITEM_AMOUNT = Integer.parseInt(l2jmega.getProperty("ColorNameItemAmount", "100"));
		COLOR_TITLE_ITEM_AMOUNT = Integer.parseInt(l2jmega.getProperty("ColorTitleItemAmount", "100"));
		
		BANKING_SYSTEM_ENABLED = Boolean.parseBoolean(l2jmega.getProperty("BankingEnabled", "false"));
		BANKING_SYSTEM_GOLDBAR_ID = Integer.parseInt(l2jmega.getProperty("BankingGoldbar_Id", "1"));
		BANKING_SYSTEM_GOLDBARS = Integer.parseInt(l2jmega.getProperty("BankingGoldbarCount", "1"));
		BANKING_SYSTEM_ADENA = Integer.parseInt(l2jmega.getProperty("BankingAdenaCount", "500000000"));
		
		OFFLINE_TRADE_ENABLE = Boolean.parseBoolean(l2jmega.getProperty("OfflineTradeEnable", "false"));
		OFFLINE_CRAFT_ENABLE = Boolean.parseBoolean(l2jmega.getProperty("OfflineCraftEnable", "false"));
		OFFLINE_SET_NAME_COLOR = Boolean.parseBoolean(l2jmega.getProperty("OfflineSetNameColor", "false"));
		OFFLINE_NAME_COLOR = Integer.decode("0x" + l2jmega.getProperty("OfflineNameColor", "808080"));
		
	}
	
	
	/**
	 * Loads Physics settings.
	 */
	private static final void loadBalanceSkills()
	{
		final ExProperties balanceSkills = initProperties(Config.BALANCE_SKILLS_FILE);
		
		
		for (String id : balanceSkills.getProperty("Skill_List_Olly", "0").split(","))
		{
			SKILL_LIST_SUCCESS_IN_OLY.add(Integer.parseInt(id));
		}
		
		for (String id : balanceSkills.getProperty("Skill_List", "0").split(","))
		{
			SKILL_LIST_SUCCESS.add(Integer.parseInt(id));
		}
		SKILL_MAX_CHANCE = Boolean.parseBoolean(balanceSkills.getProperty("Enable_Max_chance_skills", "true"));
		SKILLS_MAX_CHANCE_SUCCESS_IN_OLYMPIAD = balanceSkills.getProperty("SkillsMaxChanceSuccessInOlympiad", 40);
		SKILLS_MIN_CHANCE_SUCCESS_IN_OLYMPIAD = balanceSkills.getProperty("SkillsMinChanceSuccessInOlympiad", 40);
		SKILLS_MAX_CHANCE_SUCCESS = balanceSkills.getProperty("SkillsMaxChanceSuccess", 40);
		SKILLS_MIN_CHANCE_SUCCESS = balanceSkills.getProperty("SkillsMinChanceSuccess", 40);
		CUSTOM_CHANCE_FOR_ALL_SKILL = Boolean.parseBoolean(balanceSkills.getProperty("Enable_Max_chance_for_all_skills", "true"));
		SKILLS_MAX_CHANCE = balanceSkills.getProperty("MaxChance", 90);
		SKILLS_MIN_CHANCE = balanceSkills.getProperty("MinChance", 20);
		
		ENABLE_CUSTOM_CHANCE_SKILL = Boolean.parseBoolean(balanceSkills.getProperty("Enable_chance_skills", "true"));
		SURRENDER_TO_FIRE = Float.parseFloat(balanceSkills.getProperty("Surrender_to_fire", "1.0"));
		VORTEX_TO_FIRE = Float.parseFloat(balanceSkills.getProperty("Vortex_to_fire", "1.0"));	
		SURRENDER_TO_WIND = Float.parseFloat(balanceSkills.getProperty("Surrender_to_wind", "1.0"));
		WIND_VORTEX = Float.parseFloat(balanceSkills.getProperty("Vortex_to_wind", "1.0"));	
		CURSE_GLOOM = Float.parseFloat(balanceSkills.getProperty("Curse_of_Gloom", "1.0"));
		DARK_VORTEX = Float.parseFloat(balanceSkills.getProperty("Dark_vortex", "1.0"));
		SURRENDER_TO_WATER = Float.parseFloat(balanceSkills.getProperty("Surrender_to_water", "1.0"));
		ICE_VORTEX = Float.parseFloat(balanceSkills.getProperty("Ice_vortex", "1.0"));
		LIGHT_VORTEX = Float.parseFloat(balanceSkills.getProperty("Light_vortex", "1.0"));
		SILENCE = Float.parseFloat(balanceSkills.getProperty("Silence", "1.0"));
		SLEEP = Float.parseFloat(balanceSkills.getProperty("Sleep", "1.0"));
		CURSE_FEAR = Float.parseFloat(balanceSkills.getProperty("Curse_fear", "1.0"));
		ANCHOR = Float.parseFloat(balanceSkills.getProperty("Anchor", "1.0"));
		CURSE_OF_DOOM = Float.parseFloat(balanceSkills.getProperty("Curse_of_Doom", "1.0"));
		CURSE_OF_ABYSS = Float.parseFloat(balanceSkills.getProperty("Curse_of_Abyss", "1.0"));
		CANCELLATION = Float.parseFloat(balanceSkills.getProperty("Cancellation", "1.0"));
		MASS_SLOW = Float.parseFloat(balanceSkills.getProperty("Mass_slow", "1.0"));
		MASS_FEAR = Float.parseFloat(balanceSkills.getProperty("Mass_fear", "1.0"));
		MASS_GLOOM = Float.parseFloat(balanceSkills.getProperty("Mass_gloom", "1.0"));
		SLEEPING_CLOUD = Float.parseFloat(balanceSkills.getProperty("Sleeping_cloud", "1.0"));
		HEROIC_GRANDEUR = Float.parseFloat(balanceSkills.getProperty("Heroic_grandeur", "1.0"));
		HEROIC_DREAD = Float.parseFloat(balanceSkills.getProperty("Heroic_dread", "1.0"));
		STUNNING_SHOT = Float.parseFloat(balanceSkills.getProperty("Stunning_shot", "1.0"));	
		HEX = Float.parseFloat(balanceSkills.getProperty("Hex", "1.0"));
		SHOCK_STOMP = Float.parseFloat(balanceSkills.getProperty("Shock_stomp", "1.0"));
		THUNDER_STORM = Float.parseFloat(balanceSkills.getProperty("Thunder_storm", "1.0"));
		SHIELD_STUN = Float.parseFloat(balanceSkills.getProperty("Shield_stun", "1.0"));
		SHIELD_SLAM = Float.parseFloat(balanceSkills.getProperty("Shield_slam", "1.0"));
		SHACKLE = Float.parseFloat(balanceSkills.getProperty("Shackle", "1.0"));
		MASS_SHACKLING = Float.parseFloat(balanceSkills.getProperty("Mass_shackling", "1.0"));
		ARREST = Float.parseFloat(balanceSkills.getProperty("Arrest", "1.0"));
		BLUFF = Float.parseFloat(balanceSkills.getProperty("Bluff", "1.0"));
		SWITCH = Float.parseFloat(balanceSkills.getProperty("Switch", "1.0"));
		STUNNING_FIST = Float.parseFloat(balanceSkills.getProperty("Stunning_fist", "1.0"));
		FEAR_OVER = Float.parseFloat(balanceSkills.getProperty("Fear_over", "1.0"));
		SEAL_OF_SILENCE = Float.parseFloat(balanceSkills.getProperty("Seal_of_Silence", "1.0"));
		SEAL_OF_SUSPENSION = Float.parseFloat(balanceSkills.getProperty("Seal_of_Suspension", "1.0"));
		STUN_ATTACK = Float.parseFloat(balanceSkills.getProperty("Stun_attack", "1.0"));
		ARMOR_CRUSH = Float.parseFloat(balanceSkills.getProperty("Armor_crush", "1.0"));
		SLOW = Float.parseFloat(balanceSkills.getProperty("Slow", "1.0"));
		SEAL_OF_DESPAIR = Float.parseFloat(balanceSkills.getProperty("Seal_of_Despair", "1.0"));
		DREAMING_SPIRIT = Float.parseFloat(balanceSkills.getProperty("Dreaming_spirit", "1.0"));
		SEAL_OF_BINDING = Float.parseFloat(balanceSkills.getProperty("Seal_of_Binding", "1.0"));
		MASS_WARRIOR_BANE = Float.parseFloat(balanceSkills.getProperty("Mass_Warrior_Bane", "1.0"));
		MASS_MAGE_BANE = Float.parseFloat(balanceSkills.getProperty("Mass_Mage_Bane", "1.0"));
		SHIELD_BASH = Float.parseFloat(balanceSkills.getProperty("Shield_Bash", "1.0"));
		SHOCK_BLAST = Float.parseFloat(balanceSkills.getProperty("Shock_Blast", "1.0"));
		
	}
	
	/**
	 * Loads Physics settings.
	 */
	private static final void loadPhysics()
	{
		final ExProperties PHYSICSSetting = initProperties(Config.PHYSICS_FILE);
		
		BLOW_ATTACK_FRONT = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackFront", "50"));
		BLOW_ATTACK_SIDE = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackSide", "60"));
		BLOW_ATTACK_BEHIND = Integer.parseInt(PHYSICSSetting.getProperty("BlowAttackBehind", "70"));
		BACKSTAB_ATTACK_FRONT = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackFront", "0"));
		BACKSTAB_ATTACK_SIDE = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackSide", "0"));
		BACKSTAB_ATTACK_BEHIND = Integer.parseInt(PHYSICSSetting.getProperty("BackstabAttackBehind", "70"));
		
		ANTI_SS_BUG_1 = Integer.parseInt(PHYSICSSetting.getProperty("Delay", "2700"));
		ANTI_SS_BUG_2 = Integer.parseInt(PHYSICSSetting.getProperty("DelayBow", "1500"));
		ANTI_SS_BUG_3 = Integer.parseInt(PHYSICSSetting.getProperty("DelayNextAttack", "470000"));
		
		ADD_MATKSPEED = Integer.parseInt(PHYSICSSetting.getProperty("Add_MPAtkSpeed", "0"));
		ADD_ATKSPEED = Integer.parseInt(PHYSICSSetting.getProperty("Add_PAtkSpeed", "0"));
		
		ADD_HP = Integer.parseInt(PHYSICSSetting.getProperty("Add_HP", "0"));
		ADD_MP = Integer.parseInt(PHYSICSSetting.getProperty("Add_MP", "0"));
		ADD_CP = Integer.parseInt(PHYSICSSetting.getProperty("Add_CP", "0"));
		
		MAX_PATK_SPEED = Integer.parseInt(PHYSICSSetting.getProperty("MaxPAtkSpeed", "1500"));
		MAX_PATK_SPEED_GHOST = Integer.parseInt(PHYSICSSetting.getProperty("MaxPAtkSpeedGhost", "1500"));
		MAX_PATK_SPEED_MOONL = Integer.parseInt(PHYSICSSetting.getProperty("MaxPAtkSpeedMoonl", "1500"));
		
		MAX_MATK_SPEED = Integer.parseInt(PHYSICSSetting.getProperty("MaxMAtkSpeed", "1999"));
		
		RANDOM_DAMAGE = Boolean.parseBoolean(PHYSICSSetting.getProperty("RandomDamage", "true"));
		
		ENABLE_CUSTOM_CRIT_POWER = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_CriticalPower", "true"));
		MAGIC_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("MagicCriticalPower", "3.0"));
		FIGHTER_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("FighterCriticalPower", "1.0"));
		GHOSTSENTINEL_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("GhostSentinelCriticalPower", "1.0"));
		
		ENABLE_CUSTOM_ALL_DAMAGE = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_AllDamages", "true"));
		ALT_MAGES_MAGICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltMDamageMages", "1.00"));
		ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltPDamageFighters", "1.00"));
		
		ENABLE_CUSTOM_CRIT = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_CriticalChance", "true"));
		MAX_PCRIT_RATE = Integer.parseInt(PHYSICSSetting.getProperty("MaxPCritRate", "500"));
		PCRIT_RATE_ArcherHuman = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherHuman", "300"));
		PCRIT_RATE_ArcherElfo = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherElfo", "300"));
		PCRIT_RATE_ArcherDarkElfo = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherDarkElfo", "300"));
		
		MAX_MCRIT_RATE = Integer.parseInt(PHYSICSSetting.getProperty("MaxMCritRate", "300"));
		MCRIT_RATE_Archmage = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Archmage", "300"));
		MCRIT_RATE_Soultaker = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Soultaker", "300"));
		MCRIT_RATE_Mystic_Muse = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Mystic_Muse", "300"));
		MCRIT_RATE_Storm_Screamer = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Storm_Screamer", "300"));
		MCRIT_RATE_Dominator = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Dominator", "300"));
		
		ENABLE_CUSTOM_DAMAGE_MONSTER = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_Damage", "true"));
		
		ALT_FIGHTER_TO_MONSTER = Float.parseFloat(PHYSICSSetting.getProperty("FighterToMonster", "1.00"));
		ALT_MAGE_TO_MONSTER = Float.parseFloat(PHYSICSSetting.getProperty("MageToMonster", "1.00"));
		
		MONSTER_CUSTOM_DANO = PHYSICSSetting.getProperty("MonsterDanoList");
		LIST_MONSTER_CUSTOM_DANO_IDS = new ArrayList<>();
		for (String id : MONSTER_CUSTOM_DANO.trim().split(","))
		{
			LIST_MONSTER_CUSTOM_DANO_IDS.add(Integer.parseInt(id.trim()));
		}
		
		ALT_FIGHTER_TO_MONSTER_2 = Float.parseFloat(PHYSICSSetting.getProperty("FighterToMonster_2", "1.00"));
		ALT_MAGE_TO_MONSTER_2 = Float.parseFloat(PHYSICSSetting.getProperty("MageToMonster_2", "1.00"));
		
		RAID_CUSTOM_DANO = PHYSICSSetting.getProperty("RaidDanoList");
		LIST_RAID_CUSTOM_DANO_IDS = new ArrayList<>();
		for (String id : RAID_CUSTOM_DANO.trim().split(","))
		{
			LIST_RAID_CUSTOM_DANO_IDS.add(Integer.parseInt(id.trim()));
		}
		
		ALT_FIGHTER_TO_RAIDBOSS = Float.parseFloat(PHYSICSSetting.getProperty("FighterToRaidBoss", "1.00"));
		ALT_MAGE_TO_RAIDBOSS = Float.parseFloat(PHYSICSSetting.getProperty("MageToRaidBoss", "1.00"));
		
		ALT_FIGHTER_TO_BOSS_2 = Float.parseFloat(PHYSICSSetting.getProperty("FighterToBoss_2", "1.00"));
		ALT_MAGE_TO_BOSS_2 = Float.parseFloat(PHYSICSSetting.getProperty("MageToBoss_2", "1.00"));
		
		ALT_FIGHTER_TO_GRANDBOSS = Float.parseFloat(PHYSICSSetting.getProperty("FighterToGrandBoss", "1.00"));
		ALT_MAGE_TO_GRANDBOSS = Float.parseFloat(PHYSICSSetting.getProperty("MageToGrandBoss", "1.00"));
		
		CUSTOM_BOSS_TASK = PHYSICSSetting.getProperty("Custom_Boss_Time", "00");
		CUSTOM_BOSS_TASK_LIST = new ArrayList<>();
		for (String type : CUSTOM_BOSS_TASK.split(","))
		{
			CUSTOM_BOSS_TASK_LIST.add(type);
		}
		CUSTOM_BOSS_TASK = null;
		ALT_FIGHTER_TO_BOSS_NIGHT = Float.parseFloat(PHYSICSSetting.getProperty("FighterToBoss_Night", "1.00"));
		ALT_MAGE_TO_BOSS_NIGHT = Float.parseFloat(PHYSICSSetting.getProperty("MageToBoss_Night", "1.00"));
		
	}
	
	
	/**
	 * Loads Physics settings.
	 */
	private static final void loadPvpEvent()
	{
		final ExProperties pvpevent = initProperties(Config.PVP_EVENT_FILE);
		
		
		PVP_EVENT_ENABLED = pvpevent.getProperty("PvPEventEnabled", false);
		PVP_EVENT_INTERVAL = pvpevent.getProperty("PvPZEventInterval", "20:00").split(",");
		PVP_EVENT_RUNNING_TIME = pvpevent.getProperty("PvPZEventRunningTime", 120);
		PVP_EVENT_REWARDS = parseItemsList(pvpevent.getProperty("PvPEventWinnerReward", "6651,50"));
		ALLOW_SPECIAL_PVP_REWARD = pvpevent.getProperty("SpecialPvpRewardEnabled", false);
		PVP_SPECIAL_ITEMS_REWARD = new ArrayList<>();
		String[] pvpSpeReward = pvpevent.getProperty("SpecialPvpItemsReward", "57,100000").split(";");
		for (String reward : pvpSpeReward) {
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2) {
				_log.warning("SpecialPvpItemsReward: invalid config property -> PvpItemsReward \""+ reward + "\"");
			} else {
				try {
					PVP_SPECIAL_ITEMS_REWARD.add(new int[] { Integer.parseInt(rewardSplit[0]), 
						Integer.parseInt(rewardSplit[1]) });
				} catch (NumberFormatException nfe) {
					if (!reward.isEmpty())
						_log.warning("SpecialPvpItemsReward: invalid config property -> PvpItemsReward \""+ reward + "\""); 
				} 
			} 
		}
		
		SCREN_MSG_PVP = pvpevent.getProperty("SummonToPvPEnabled", false);
		pvp_locx = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocx", "1"));
		pvp_locy = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocy", "1"));
		pvp_locz = Integer.parseInt(pvpevent.getProperty("SummonToPvPLocz", "1"));
	}
	
	
	/**
	 * Loads Physics settings.
	 */
	private static final void loadOlympiadPhysics()
	{
		final ExProperties PHYSICSSetting = initProperties(Config.OLYMPIAD_PHYSICS_FILE);
		
		OLY_ENABLE_CUSTOM_CRIT_POWER = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_CriticalPower", "true"));
		OLY_MAGIC_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("MagicCriticalPower", "3.0"));
		OLY_FIGHTER_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("FighterCriticalPower", "1.0"));
		OLY_GHOSTSENTINEL_CRITICAL_POWER = Float.parseFloat(PHYSICSSetting.getProperty("GhostSentinelCriticalPower", "1.0"));
		
		OLY_ENABLE_CUSTOM_ALL_DAMAGE = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_AllDamages", "true"));
		OLY_ALT_MAGES_MAGICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltMDamageMages", "1.00"));
		OLY_ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltPDamageFighters", "1.00"));
		
		OLY_ENABLE_CUSTOM_CRIT = Boolean.parseBoolean(PHYSICSSetting.getProperty("EnableCustom_CriticalChance", "true"));
		OLY_MAX_PCRIT_RATE = Integer.parseInt(PHYSICSSetting.getProperty("MaxPCritRate", "500"));
		OLY_PCRIT_RATE_ArcherHuman = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherHuman", "300"));
		OLY_PCRIT_RATE_ArcherElfo = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherElfo", "300"));
		OLY_PCRIT_RATE_ArcherDarkElfo = Integer.parseInt(PHYSICSSetting.getProperty("PCritRate_ArcherDarkElfo", "300"));
		
		OLY_MAX_MCRIT_RATE = Integer.parseInt(PHYSICSSetting.getProperty("MaxMCritRate", "300"));
		OLY_MCRIT_RATE_Archmage = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Archmage", "300"));
		OLY_MCRIT_RATE_Soultaker = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Soultaker", "300"));
		OLY_MCRIT_RATE_Mystic_Muse = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Mystic_Muse", "300"));
		OLY_MCRIT_RATE_Storm_Screamer = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Storm_Screamer", "300"));
		OLY_MCRIT_RATE_Dominator = Integer.parseInt(PHYSICSSetting.getProperty("MCritRate_Dominator", "300"));
		
		for (String id : PHYSICSSetting.getProperty("Oly_Skill_list_config", "0").split(","))
		{
			OLYMPIAD_SKILL_LIST_DANO.add(Integer.parseInt(id));
		}
		OLYMPIAD_SKILL_DANO = Integer.parseInt(PHYSICSSetting.getProperty("Oly_Skill_dano", "2"));
		
		ALT_PETS_PHYSICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltPDamagePets", "1.00"));
		ALT_PETS_MAGICAL_DAMAGE_MULTI = Float.parseFloat(PHYSICSSetting.getProperty("AltMDamagePets", "1.00"));
	}
	
	
	/**
	 * Loads TvT settings.
	 */
	private static final void loadTvT()
	{
		final ExProperties tvt = initProperties(Config.TVT_FILE);
		
		TVT_EVENT_ENABLED = Boolean.parseBoolean(tvt.getProperty("TVTEventEnabled", "false"));
		
		TVT_SKILL_PROTECT = Boolean.parseBoolean(tvt.getProperty("TvTSkillProtect", "false"));
		for (String id : tvt.getProperty("TvTDisableSkillList", "0").split(","))
		{
			TVT_SKILL_LIST.add(Integer.parseInt(id));
		}
		DEBUG_TVT = tvt.getProperty("DebugTvT", true);
		TVT_EVEN_TEAMS = tvt.getProperty("TvTEvenTeams", "BALANCE");
		TVT_ALLOW_INTERFERENCE = Boolean.parseBoolean(tvt.getProperty("TvTAllowInterference", "False"));
		TVT_ALLOW_POTIONS = Boolean.parseBoolean(tvt.getProperty("TvTAllowPotions", "False"));
		TVT_ALLOW_SUMMON = Boolean.parseBoolean(tvt.getProperty("TvTAllowSummon", "False"));
		TVT_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(tvt.getProperty("TvTOnStartRemoveAllEffects", "True"));
		TVT_ON_START_UNSUMMON_PET = Boolean.parseBoolean(tvt.getProperty("TvTOnStartUnsummonPet", "True"));
		TVT_REVIVE_RECOVERY = Boolean.parseBoolean(tvt.getProperty("TvTReviveRecovery", "False"));
		TVT_ANNOUNCE_TEAM_STATS = Boolean.parseBoolean(tvt.getProperty("TvTAnnounceTeamStats", "False"));
		TVT_ANNOUNCE_REWARD = Boolean.parseBoolean(tvt.getProperty("TvTAnnounceReward", "False"));
		TVT_ANNOUNCE_LVL = Boolean.parseBoolean(tvt.getProperty("TvTAnnounceLevel", "False"));
		TVT_PRICE_NO_KILLS = Boolean.parseBoolean(tvt.getProperty("TvTPriceNoKills", "False"));
		TVT_JOIN_CURSED = Boolean.parseBoolean(tvt.getProperty("TvTJoinWithCursedWeapon", "True"));
		TVT_COMMAND = Boolean.parseBoolean(tvt.getProperty("TvTCommand", "True"));
		TVT_REVIVE_DELAY = Long.parseLong(tvt.getProperty("TvTReviveDelay", "20000"));
		if (TVT_REVIVE_DELAY < 1000)
			TVT_REVIVE_DELAY = 1000; // can't be set less then 1 second
		TVT_OPEN_FORT_DOORS = Boolean.parseBoolean(tvt.getProperty("TvTOpenFortDoors", "False"));
		TVT_CLOSE_FORT_DOORS = Boolean.parseBoolean(tvt.getProperty("TvTCloseFortDoors", "False"));
		TVT_OPEN_ADEN_COLOSSEUM_DOORS = Boolean.parseBoolean(tvt.getProperty("TvTOpenAdenColosseumDoors", "False"));
		TVT_CLOSE_ADEN_COLOSSEUM_DOORS = Boolean.parseBoolean(tvt.getProperty("TvTCloseAdenColosseumDoors", "False"));
		TVT_TOP_KILLER_REWARD = Integer.parseInt(tvt.getProperty("TvTTopKillerRewardId", "5575"));
		TVT_TOP_KILLER_QTY = Integer.parseInt(tvt.getProperty("TvTTopKillerRewardQty", "2000000"));
		TVT_AURA = Boolean.parseBoolean(tvt.getProperty("TvTAura", "False"));
		TVT_STATS_LOGGER = Boolean.parseBoolean(tvt.getProperty("TvTStatsLogger", "true"));
		Allow_Same_HWID_On_tvt = Boolean.parseBoolean(tvt.getProperty("SameHWIDOnTvT", "true"));
		
		SCREN_MSG = Boolean.parseBoolean(tvt.getProperty("TvTScreenMsg", "false"));
		
		TVT_OBSERVER_X = Integer.parseInt(tvt.getProperty("ObserverLocx", "83400"));
		TVT_OBSERVER_Y = Integer.parseInt(tvt.getProperty("ObserverLocy", "-16296"));
		TVT_OBSERVER_Z = Integer.parseInt(tvt.getProperty("ObserverLocz", "-1888"));
	}
	
	/**
	 * Loads CTF settings.
	 */
	private static final void loadCTF()
	{
		final ExProperties CTFSettings = initProperties(Config.CTF_FILE);
		
		CTF_EVENT_ENABLED = Boolean.parseBoolean(CTFSettings.getProperty("CTFEventEnabled", "false"));
		
		CTF_EVEN_TEAMS = CTFSettings.getProperty("CTFEvenTeams", "BALANCE");
		CTF_ALLOW_INTERFERENCE = Boolean.parseBoolean(CTFSettings.getProperty("CTFAllowInterference", "False"));
		CTF_ALLOW_POTIONS = Boolean.parseBoolean(CTFSettings.getProperty("CTFAllowPotions", "False"));
		CTF_ALLOW_SUMMON = Boolean.parseBoolean(CTFSettings.getProperty("CTFAllowSummon", "False"));
		CTF_ON_START_REMOVE_ALL_EFFECTS = Boolean.parseBoolean(CTFSettings.getProperty("CTFOnStartRemoveAllEffects", "True"));
		CTF_ON_START_UNSUMMON_PET = Boolean.parseBoolean(CTFSettings.getProperty("CTFOnStartUnsummonPet", "True"));
		CTF_ANNOUNCE_TEAM_STATS = Boolean.parseBoolean(CTFSettings.getProperty("CTFAnnounceTeamStats", "False"));
		CTF_ANNOUNCE_REWARD = Boolean.parseBoolean(CTFSettings.getProperty("CTFAnnounceReward", "False"));
		CTF_JOIN_CURSED = Boolean.parseBoolean(CTFSettings.getProperty("CTFJoinWithCursedWeapon", "True"));
		CTF_REVIVE_RECOVERY = Boolean.parseBoolean(CTFSettings.getProperty("CTFReviveRecovery", "False"));
		CTF_COMMAND = Boolean.parseBoolean(CTFSettings.getProperty("CTFCommand", "True"));
		CTF_REVIVE_DELAY = Long.parseLong(CTFSettings.getProperty("CTFReviveDelay", "20000"));
		if (CTF_REVIVE_DELAY < 1000)
			CTF_REVIVE_DELAY = 1000; // can't be set less then 1 second
		
		CTF_AURA = Boolean.parseBoolean(CTFSettings.getProperty("CTFAura", "True"));
		
		CTF_STATS_LOGGER = Boolean.parseBoolean(CTFSettings.getProperty("CTFStatsLogger", "true"));
		
		CTF_SPAWN_OFFSET = Integer.parseInt(CTFSettings.getProperty("CTFSpawnOffset", "100"));
		
		CTF_REMOVE_BUFFS_ON_DIE = Boolean.parseBoolean(CTFSettings.getProperty("CTFRemoveBuffsOnPlayerDie", "false"));
		
		Allow_Same_HWID_On_ctf = Boolean.parseBoolean(CTFSettings.getProperty("SameHWIDOnCTF", "true"));
		CTF_ANNOUNCE_LVL = Boolean.parseBoolean(CTFSettings.getProperty("CTFAnnounceLevel", "True"));
		
		CTF_REWARD_TIE = Integer.parseInt(CTFSettings.getProperty("CTFRewardTie_Id", "57"));
		CTF_REWARD_TIE_AMOUNT = Integer.parseInt(CTFSettings.getProperty("CTFRewardTie_Amount", "100"));
		
		for (String id : CTFSettings.getProperty("CTFDisableSkillList", "0").split(","))
		{
			CTF_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		CTF_OBSERVER_X = Integer.parseInt(CTFSettings.getProperty("ObserverLocx", "83400"));
		CTF_OBSERVER_Y = Integer.parseInt(CTFSettings.getProperty("ObserverLocy", "-16296"));
		CTF_OBSERVER_Z = Integer.parseInt(CTFSettings.getProperty("ObserverLocz", "-1888"));
		
	}
	
	/**
	 * Loads Tournament settings.
	 */
	private static final void load_Tournament()
	{
		final ExProperties tournament = initProperties(Config.TOURNAMENT_FILE);
		
		TOURNAMENT_EVENT_START = tournament.getProperty("TournamentStartOn", false);
		TOURNAMENT_EVENT_TIME = tournament.getProperty("TournamentAutoEvent", false);
		TOURNAMENT_EVENT_ANNOUNCE = tournament.getProperty("TournamenAnnounce", false);
		TOURNAMENT_EVENT_SUMMON = tournament.getProperty("TournamentSummon", false);
		
		TOUR_GRADE_A_1X1 = tournament.getProperty("Tour_gradeS_1x1", false);
		TOUR_GRADE_A_2X2 = tournament.getProperty("Tour_gradeS_2x2", false);
		TOUR_GRADE_A_5X5 = tournament.getProperty("Tour_gradeS_5x5", false);
		TOUR_GRADE_A_9X9 = tournament.getProperty("Tour_gradeS_9x9", false);
		ALT_TOUR_ENCHANT_LIMIT = Integer.parseInt(tournament.getProperty("Tour_MaxEnchant", "-1"));
		
		TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY = tournament.getProperty("TournamentStartTime", "20:00").split(",");
		
		TOURNAMENT_TIME = Integer.parseInt(tournament.getProperty("TournamentEventTime", "1"));
		
		TOUR_AURA_TEAM1 = Integer.parseInt(tournament.getProperty("Team_1_aura", "1"));
		TOUR_AURA_TEAM2 = Integer.parseInt(tournament.getProperty("Team_2_aura", "2"));
		
		TITLE_COLOR_TEAM1 = tournament.getProperty("TitleColorTeam_1", "FFFFFF");
		TITLE_COLOR_TEAM2 = tournament.getProperty("TitleColorTeam_2", "FFFFFF");
		
		MSG_TEAM1 = tournament.getProperty("TitleTeam_1", "Team [1]");
		MSG_TEAM2 = tournament.getProperty("TitleTeam_2", "Team [2]");
		
		Allow_Same_HWID_On_Tournament = Boolean.parseBoolean(tournament.getProperty("Allow_Same_HWID_On_Tournament", "true"));
		
		ARENA_NPC = Integer.parseInt(tournament.getProperty("NPCRegister", "1"));
		
		NPC_locx = Integer.parseInt(tournament.getProperty("Locx", "1"));
		NPC_locy = Integer.parseInt(tournament.getProperty("Locy", "1"));
		NPC_locz = Integer.parseInt(tournament.getProperty("Locz", "1"));
		NPC_Heading = Integer.parseInt(tournament.getProperty("Heading", "1"));
		
		NPC_locx2 = Integer.parseInt(tournament.getProperty("Locx2", "1"));
		NPC_locy2 = Integer.parseInt(tournament.getProperty("Locy2", "1"));
		NPC_locz2 = Integer.parseInt(tournament.getProperty("Locz2", "1"));
		NPC_Heading2 = Integer.parseInt(tournament.getProperty("Heading2", "1"));
		
		Tournament_locx = Integer.parseInt(tournament.getProperty("TournamentLocx", "1"));
		Tournament_locy = Integer.parseInt(tournament.getProperty("TournamentLocy", "1"));
		Tournament_locz = Integer.parseInt(tournament.getProperty("TournamentLocz", "1"));
		
		ALLOW_1X1_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow1x1Register", "true"));
		ALLOW_2X2_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow2x2Register", "true"));
		ALLOW_5X5_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow5x5Register", "true"));
		ALLOW_9X9_REGISTER = Boolean.parseBoolean(tournament.getProperty("Allow9x9Register", "true"));
		
		ALLOW_5X5_LOSTBUFF = Boolean.parseBoolean(tournament.getProperty("Allow5x5LostBuff", "false"));
		
		ARENA_MESSAGE_ENABLED = Boolean.parseBoolean(tournament.getProperty("ScreenArenaMessageEnable", "false"));
		ARENA_MESSAGE_TEXT = tournament.getProperty("ScreenArenaMessageText", "Welcome to L2J server!");
		ARENA_MESSAGE_TIME = Integer.parseInt(tournament.getProperty("ScreenArenaMessageTime", "10")) * 1000;
		
		String[] arenaLocs2 = tournament.getProperty("ArenasLoc1x1", "").split(";");
		String[] locSplit2 = null;
		ARENA_EVENT_COUNT_1X1 = arenaLocs2.length;
		ARENA_EVENT_LOCS_1X1 = new int[ARENA_EVENT_COUNT_1X1][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_1X1; i++)
		{
			locSplit2 = arenaLocs2[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_1X1[i][j] = Integer.parseInt(locSplit2[j].trim());
			}
		}
		
		String[] arenaLocs = tournament.getProperty("ArenasLoc", "").split(";");
		String[] locSplit = null;
		ARENA_EVENT_COUNT = arenaLocs.length;
		ARENA_EVENT_LOCS = new int[ARENA_EVENT_COUNT][3];
		for (int i = 0; i < ARENA_EVENT_COUNT; i++)
		{
			locSplit = arenaLocs[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS[i][j] = Integer.parseInt(locSplit[j].trim());
			}
		}
		
		String[] arenaLocs5x5 = tournament.getProperty("Arenas5x5Loc", "").split(";");
		String[] locSplit5x5 = null;
		ARENA_EVENT_COUNT_5X5 = arenaLocs5x5.length;
		ARENA_EVENT_LOCS_5X5 = new int[ARENA_EVENT_COUNT_5X5][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_5X5; i++)
		{
			locSplit5x5 = arenaLocs5x5[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_5X5[i][j] = Integer.parseInt(locSplit5x5[j].trim());
			}
		}
		
		String[] arenaLocs9x9 = tournament.getProperty("Arenas9x9Loc", "").split(";");
		String[] locSplit9x9 = null;
		ARENA_EVENT_COUNT_9X9 = arenaLocs9x9.length;
		ARENA_EVENT_LOCS_9X9 = new int[ARENA_EVENT_COUNT_9X9][3];
		for (int i = 0; i < ARENA_EVENT_COUNT_9X9; i++)
		{
			locSplit9x9 = arenaLocs9x9[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				ARENA_EVENT_LOCS_9X9[i][j] = Integer.parseInt(locSplit9x9[j].trim());
			}
		}
		duelist_COUNT_5X5 = tournament.getProperty("duelist_amount_5x5", 1);
		dreadnought_COUNT_5X5 = tournament.getProperty("dreadnought_amount_5x5", 1);
		tanker_COUNT_5X5 = tournament.getProperty("tanker_amount_5x5", 1);
		dagger_COUNT_5X5 = tournament.getProperty("dagger_amount_5x5", 1);
		archer_COUNT_5X5 = tournament.getProperty("archer_amount_5x5", 1);
		bs_COUNT_5X5 = tournament.getProperty("bs_amount_5x5", 1);
		archmage_COUNT_5X5 = tournament.getProperty("archmage_amount_5x5", 1);
		soultaker_COUNT_5X5 = tournament.getProperty("soultaker_amount_5x5", 1);
		mysticMuse_COUNT_5X5 = tournament.getProperty("mysticMuse_amount_5x5", 1);
		stormScreamer_COUNT_5X5 = tournament.getProperty("stormScreamer_amount_5x5", 1);
		titan_COUNT_5X5 = tournament.getProperty("titan_amount_5x5", 1);
		dominator_COUNT_5X5 = tournament.getProperty("dominator_amount_5x5", 1);
		
		duelist_COUNT_9X9 = tournament.getProperty("duelist_amount_9x9", 1);
		dreadnought_COUNT_9X9 = tournament.getProperty("dreadnought_amount_9x9", 1);
		tanker_COUNT_9X9 = tournament.getProperty("tanker_amount_9x9", 1);
		dagger_COUNT_9X9 = tournament.getProperty("dagger_amount_9x9", 1);
		archer_COUNT_9X9 = tournament.getProperty("archer_amount_9x9", 1);
		bs_COUNT_9X9 = tournament.getProperty("bs_amount_9x9", 1);
		archmage_COUNT_9X9 = tournament.getProperty("archmage_amount_9x9", 1);
		soultaker_COUNT_9X9 = tournament.getProperty("soultaker_amount_9x9", 1);
		mysticMuse_COUNT_9X9 = tournament.getProperty("mysticMuse_amount_9x9", 1);
		stormScreamer_COUNT_9X9 = tournament.getProperty("stormScreamer_amount_9x9", 1);
		titan_COUNT_9X9 = tournament.getProperty("titan_amount_9x9", 1);
		dominator_COUNT_9X9 = tournament.getProperty("dominator_amount_9x9", 1);
		
		ARENA_PVP_AMOUNT = tournament.getProperty("ArenaPvpJoin", 10);
		
		ARENA_REWARD_ID_1X1 = tournament.getProperty("ArenaRewardId1x1", 57);
		ARENA_WIN_REWARD_COUNT_1X1 = tournament.getProperty("ArenaWinRewardCount1x1", 1);
		ARENA_LOST_REWARD_COUNT_1X1 = tournament.getProperty("ArenaLostRewardCount1x1", 1);
		
		ARENA_REWARD_ID = tournament.getProperty("ArenaRewardId", 57);
		ARENA_WIN_REWARD_COUNT = tournament.getProperty("ArenaWinRewardCount", 1);
		ARENA_LOST_REWARD_COUNT = tournament.getProperty("ArenaLostRewardCount", 1);
		
		ARENA_WIN_REWARD_COUNT_5X5 = tournament.getProperty("ArenaWinRewardCount5x5", 1);
		ARENA_LOST_REWARD_COUNT_5X5 = tournament.getProperty("ArenaLostRewardCount5x5", 1);
		
		ARENA_WIN_REWARD_COUNT_9X9 = tournament.getProperty("ArenaWinRewardCount9x9", 1);
		ARENA_LOST_REWARD_COUNT_9X9 = tournament.getProperty("ArenaLostRewardCount9x9", 1);
		
		ARENA_CHECK_INTERVAL_1X1 = tournament.getProperty("ArenaBattleCheckInterval1x1", 15) * 1000;
		ARENA_CALL_INTERVAL_1X1 = tournament.getProperty("ArenaBattleCallInterval1x1", 60);
		
		ARENA_WAIT_INTERVAL_1X1 = tournament.getProperty("ArenaBattleWaitInterval1x1", 20);
		
		
		ARENA_CHECK_INTERVAL = tournament.getProperty("ArenaBattleCheckInterval", 15) * 1000;
		ARENA_CALL_INTERVAL = tournament.getProperty("ArenaBattleCallInterval", 60);
		
		ARENA_WAIT_INTERVAL = tournament.getProperty("ArenaBattleWaitInterval", 20);
		ARENA_WAIT_INTERVAL_5X5 = tournament.getProperty("ArenaBattleWaitInterval5x5", 45);
		ARENA_WAIT_INTERVAL_9X9 = tournament.getProperty("ArenaBattleWaitInterval9x9", 45);
		
		TOURNAMENT_ID_RESTRICT = tournament.getProperty("ItemsRestriction");
		
		TOURNAMENT_LISTID_RESTRICT = new ArrayList<>();
		for (String id : TOURNAMENT_ID_RESTRICT.split(","))
			TOURNAMENT_LISTID_RESTRICT.add(Integer.parseInt(id));
		
		ARENA_SKILL_PROTECT = Boolean.parseBoolean(tournament.getProperty("ArenaSkillProtect", "false"));
		
		for (String id : tournament.getProperty("ArenaDisableSkillList", "0").split(","))
		{
			ARENA_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("DisableSkillList", "0").split(","))
		{
			ARENA_DISABLE_SKILL_LIST_PERM.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("ArenaDisableSkillList_noStart", "0").split(","))
		{
			ARENA_DISABLE_SKILL_LIST.add(Integer.parseInt(id));
		}
		
		for (String id : tournament.getProperty("ArenaStopSkillList", "0").split(","))
		{
			ARENA_STOP_SKILL_LIST.add(Integer.parseInt(id));
		}
		PARTY_SEARCH_5X5_TIME = tournament.getProperty("PartySearch_5x5_CheckTime", 10);
		PARTY_SEARCH_9X9_TIME = tournament.getProperty("PartySearch_9x9_CheckTime", 10);
		
		ARENA_PROTECT = Boolean.parseBoolean(tournament.getProperty("ArenaProtect", "true"));
		
	}
	
	/**
	 * Loads Party Zone settings.
	 */
	private static final void load_PartyZone()
	{
		final ExProperties partyzone = initProperties(Config.PARTY_ZONE_FILE);
		
		
		PARTY_ZONE_REWARDS = parseReward(partyzone, "PartyZoneReward_normal");
		PARTY_ZONE_REWARDS2 = parseReward(partyzone, "PartyZoneReward_event_time");
		RADIUS_TO_PARTYZONE = Integer.parseInt(partyzone.getProperty("RadiusToPartyZone", "1000"));
		
		START_PARTY = Boolean.parseBoolean(partyzone.getProperty("StartSpawnPartyZone", "false"));
		START_AUTO_PARTY = Boolean.parseBoolean(partyzone.getProperty("StartAutoPartyZone", "false"));
		PARTYZONE_TIME = Integer.parseInt(partyzone.getProperty("PartyZoneEventTime", "1"));
		
		PARTYZONE_INTERVAL_BY_TIME_OF_DAY = partyzone.getProperty("PartyZoneStartTime", "20:00").split(",");
		
		PARTY_FARM_MONSTER_DALAY = Integer.parseInt(partyzone.getProperty("MonsterDelay", "10"));
		
		PARTY_FARM_MESSAGE_TEXT = partyzone.getProperty("ScreenPartyFarmMessageText", "Welcome to L2J server!");
		PARTY_FARM_MESSAGE_TIME = Integer.parseInt(partyzone.getProperty("ScreenPartyFarmMessageTime", "10")) * 1000;
		
		OPEN_DOORS_PARTY_FARM = Boolean.parseBoolean(partyzone.getProperty("ActiveOpenDoors", "false"));
		String open_dors = partyzone.getProperty("OpenDoors", "24190001,24190002,24190003,24190004");
		String[] open_dors_split = open_dors.split(",");
		for (String s : open_dors_split)
		{
			int i = Integer.parseInt(s);
			PARTY_OPEN_DOORS.add(i);
		}
		
		CLOSE_DOORS_PARTY_FARM = Boolean.parseBoolean(partyzone.getProperty("ActiveCloseDoors", "false"));
		String close_dors = partyzone.getProperty("CloseDoors", "24190001,24190002,24190003,24190004");
		String[] close_dors_split = close_dors.split(",");
		for (String s : close_dors_split)
		{
			int i = Integer.parseInt(s);
			PARTY_CLOSE_DOORS.add(i);
		}
		
		String[] monsterLocs2 = partyzone.getProperty("MonsterLoc", "").split(";");
		String[] locSplit3 = null;
		
		monsterId = Integer.parseInt(partyzone.getProperty("MonsterId", "1"));
		
		MONSTER_LOCS_COUNT = monsterLocs2.length;
		MONSTER_LOCS = new int[MONSTER_LOCS_COUNT][3];
		for (int i = 0; i < MONSTER_LOCS_COUNT; i++)
		{
			locSplit3 = monsterLocs2[i].split(",");
			for (int j = 0; j < 3; j++)
			{
				MONSTER_LOCS[i][j] = Integer.parseInt(locSplit3[j].trim());
			}
		}
		
	}
	
	
	private static List<RewardHolder> parseReward(ExProperties propertie, String configName)
	{
		List<RewardHolder> auxReturn = new ArrayList<>();
		
		String aux = propertie.getProperty(configName).trim();
		for (String randomReward : aux.split(";"))
		{
			final String[] infos = randomReward.split(",");
			if (infos.length > 2)
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]), Integer.valueOf(infos[3])));
			else
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2])));
		}
		return auxReturn;
	}
	
	/**
	 * Loads Phantom settings.
	 */
	private static final void load_Phantom()
	{
		final ExProperties Phanton = initProperties(PHANTOM_FILE);
		WALK_PHANTOM_TOWN = Boolean.parseBoolean(Phanton.getProperty("Town_PhantomWalkRandom", "False"));
		WALK_PHANTOM_TOWN2 = Boolean.parseBoolean(Phanton.getProperty("Town_PhantomWalkNPCs", "False"));
		ALLOW_PHANTOM_PLAYERS = Boolean.parseBoolean(Phanton.getProperty("AllowPhantom", "False"));
		ALLOW_PHANTOM_PLAYERS_FARM = Boolean.parseBoolean(Phanton.getProperty("AllowPhantomFarm", "False"));
		ALLOW_PHANTOM_PLAYERS_MASS_PVP = Boolean.parseBoolean(Phanton.getProperty("AllowPhantom_Mass_PvP", "False"));
		ALLOW_PHANTOM_PLAYERS_PVPEVENT = Boolean.parseBoolean(Phanton.getProperty("AllowPhantomPvPEvent", "False"));
		ALLOW_PHANTOM_PLAYERS_TVT = Boolean.parseBoolean(Phanton.getProperty("AllowPhantomTvT", "False"));
		ALLOW_PHANTOM_PLAYERS_EFFECT_SHOT = Boolean.parseBoolean(Phanton.getProperty("Effect_CP_And_Shots", "False"));
		
		PHANTOM_CHANCE_SIT = Integer.parseInt(Phanton.getProperty("Phantom_Chance_sitDown", "10"));
		
		PHANTOM_DISCONNETC_DELAY = TimeUnit.MINUTES.toMillis(Integer.parseInt(Phanton.getProperty("pvp_disconect", "15")));
		
		DISCONNETC_DELAY = TimeUnit.MINUTES.toMillis(Integer.parseInt(Phanton.getProperty("town_disconect", "15")));
		
		FARM_DISCONNETC_DELAY = TimeUnit.MINUTES.toMillis(Integer.parseInt(Phanton.getProperty("farm_disconect", "15")));
		
		PHANTOM_PLAYERS_SOULSHOT_ANIM = Boolean.parseBoolean(Phanton.getProperty("PhantomSoulshotAnimation", "True"));
		PHANTOM_PLAYERS_ARGUMENT_ANIM = Boolean.parseBoolean(Phanton.getProperty("PhantomArgumentAnimation", "True"));
		
		String[] arrayOfString1 = Phanton.getProperty("FakeEnchant", "0,14").split(",");
		PHANTOM_PLAYERS_ENCHANT_MIN = Integer.parseInt(arrayOfString1[0]);
		PHANTOM_PLAYERS_ENCHANT_MAX = Integer.parseInt(arrayOfString1[1]);
		
		PHANTOM_SPEED = Phanton.getProperty("RunSpeed", 100);
		PHANTOM_ATK_SPEED = Phanton.getProperty("Atack_RunSpeed", 100);
		
		NAME_COLOR = Phanton.getProperty("NameColor", "FFFFFF");
		TITLE_COLOR = Phanton.getProperty("TitleColor", "FFFFFF");
		
		PHANTOM_NAME_CLOLORS = Phanton.getProperty("FakeNameColors", "FFFFFF");
		PHANTOM_PLAYERS_NAME_CLOLORS = new ArrayList<>();
		for (String type : PHANTOM_NAME_CLOLORS.split(","))
		{
			PHANTOM_PLAYERS_NAME_CLOLORS.add(type);
		}
		PHANTOM_NAME_CLOLORS = null;
		
		PHANTOM_TITLE_CLOLORS = Phanton.getProperty("FakeTitleColors", "FFFFFF");
		PHANTOM_PLAYERS_TITLE_CLOLORS = new ArrayList<>();
		for (String type : PHANTOM_TITLE_CLOLORS.split(","))
		{
			PHANTOM_PLAYERS_TITLE_CLOLORS.add(type);
		}
		PHANTOM_TITLE_CLOLORS = null;
		
		PHANTOM_TITLE_PHANTOM = Boolean.parseBoolean(Phanton.getProperty("Town_title", "false"));
		
		PHANTOM_TITLE_PHANTOM_ATK = Boolean.parseBoolean(Phanton.getProperty("PvP_title", "false"));
		
		PHANTOM_TITLE_CONFIG = Boolean.parseBoolean(Phanton.getProperty("FakeTitleFixo", "false"));
		
		PHANTOM_TITLE_MSG = Phanton.getProperty("FakeTitle", "Lineage 2");
		PHANTOM_TITLE = new ArrayList<>();
		for (String type : PHANTOM_TITLE_MSG.split(","))
		{
			PHANTOM_TITLE.add(type);
		}
		PHANTOM_TITLE_MSG = null;
		
		ALLOW_PHANTOM_FACE = Boolean.parseBoolean(Phanton.getProperty("PhantomFace", "True"));
		
		PHANTOM_FACE = Phanton.getProperty("PhantomFaceList", "");
		LIST_PHANTOM_FACE = new ArrayList<>();
		for (String itemId : PHANTOM_FACE.split(","))
		{
			LIST_PHANTOM_FACE.add(Integer.parseInt(itemId));
		}
		ALLOW_PHANTOM_HAIR = Boolean.parseBoolean(Phanton.getProperty("PhantomHair", "True"));
		
		PHANTOM_HAIR = Phanton.getProperty("PhantomHairList", "0");
		LIST_PHANTOM_HAIR = new ArrayList<>();
		for (String itemId : PHANTOM_HAIR.split(","))
		{
			LIST_PHANTOM_HAIR.add(Integer.parseInt(itemId));
		}
		
		ALLOW_PHANTOM_CREST = Boolean.parseBoolean(Phanton.getProperty("Town_Crests", "true"));
		PHANTOM_PLAYERS_CREST = Phanton.getProperty("Town_Crests_Chance", 50);
		
		ALLOW_PHANTOM_CREST_ATK = Boolean.parseBoolean(Phanton.getProperty("PvP_Crests", "true"));
		PHANTOM_PLAYERS_CREST_ATK = Phanton.getProperty("PvP_Crests_Chance", 50);
		
		CLAN_ID = Phanton.getProperty("ClanIDList", "");
		LIST_CLAN_ID = new ArrayList<>();
		for (String itemId : CLAN_ID.split(","))
		{
			LIST_CLAN_ID.add(Integer.parseInt(itemId));
		}
		
		PHANTOM_RANDOM_WALK = Phanton.getProperty("town_Walk", 50);
		PHANTOM_MAGE_RANDOM_WALK = Phanton.getProperty("MageRandon_Walk", 50);
		PHANTOM_MAGE_INTERVAL_WALK = Phanton.getProperty("MageInterval_Walk", 50);
		PHANTOM_MAGE_INTERVAL_TARGET = Phanton.getProperty("MageInterval_Target", 50);
		PHANTOM_MAGE_INTERVAL_CHECK_TARGET = Phanton.getProperty("MageInterval_CheckTarget", 50);
		
		PHANTOM_MAGE_RANGE = Phanton.getProperty("MageRange", 50);
		PHANTOM_SURRENDER_INTERVAL = Phanton.getProperty("Interval_Surrender", 50);
		
		FARM_RANGE = Phanton.getProperty("Distance_attack_farm", 50);
		POWER_PHANTOM = Phanton.getProperty("PowerSkill_Phantom", 50);
		PDEF_PHANTOM = Phanton.getProperty("Pdef_Phantom", 10);
		MDEF_PHANTOM = Phanton.getProperty("Mdef_Phantom", 10);
		ATKSPEED_PHANTOM = Phanton.getProperty("AtkSpeed_Archers", 10);
		POWER_PHANTOM_ARCHER = Float.parseFloat(Phanton.getProperty("Power_Archer", "1.0"));
		PHANTOM_ARCHER_CRITICO_CHANCE = Phanton.getProperty("Archer_Crit", 50);
		CHANCE_MOVE_ARCHER = Phanton.getProperty("Move_Archer", 50);
		PDEF_PHANTOM_ARCHER = Phanton.getProperty("Pdef_PhantomArcher", 10);
		MDEF_PHANTOM_ARCHER = Phanton.getProperty("Mdef_PhantomArcher", 10);
		
		ALLOW_PHANTOM_RETAIL_ARMOR = Boolean.parseBoolean(Phanton.getProperty("AllowPhantom_Retail_Armor", "False"));
		
		PHANTOM_ARCHMAGE_PERCENTAGE = Phanton.getProperty("ArchMage_Crit", 50);
		PHANTOM_ARCHMAGE_DANO_INTERVAL = Phanton.getProperty("ArchMage_Interval_Dano", 50);
		PHANTOM_ARCHMAGE_EFFECT = Phanton.getProperty("ArchMage_Effect_Interval", 50);
		
		PHANTOM_SPELLSINGER_PERCENTAGE = Phanton.getProperty("MysticMuse_Crit", 50);
		PHANTOM_SPELLSINGER_DANO_INTERVAL = Phanton.getProperty("MysticMuse_Interval_Dano", 50);
		PHANTOM_SPELLSINGER_EFFECT = Phanton.getProperty("MysticMuse_Effect_Interval", 50);
		
		PHANTOM_SPELLHOLLER_PERCENTAGE = Phanton.getProperty("StormScream_Crit", 50);
		PHANTOM_SPELLHOLLER_DANO_INTERVAL = Phanton.getProperty("StormScream_Interval_Dano", 50);
		PHANTOM_SPELLHOLLER_EFFECT = Phanton.getProperty("StormScream_Effect_Interval", 50);
		
		PHANTOM_CHANCE_MALARIA = Phanton.getProperty("Phantom_Chance_MalariaEffect", 60);
		PHANTOM_CHANCE_TITLE = Phanton.getProperty("Phantom_Chance_Title", 60);
		
		COUNT_TOWN = Phanton.getProperty("town_count", 0);
		COUNT_FARM_ARCHMAGE = Phanton.getProperty("farm_archmage_count", 0);
		COUNT_FARM_MYSTICMUSE = Phanton.getProperty("farm_mysticmuse_count", 0);
		COUNT_FARM_STORMSCREAM = Phanton.getProperty("farm_stormscream_count", 0);
		COUNT_PVP_ARCHMAGE = Phanton.getProperty("pvp_archmage_count", 0);
		COUNT_PVP_MYSTICMUSE = Phanton.getProperty("pvp_mysticmuse_count", 0);
		COUNT_PVP_STORMSCREAM = Phanton.getProperty("pvp_stormscream_count", 0);
		COUNT_TVT_ARCHMAGE = Phanton.getProperty("tvt_archmage_count", 0);
		COUNT_TVT_MYSTICMUSE = Phanton.getProperty("tvt_mysticmuse_count", 0);
		COUNT_TVT_STORMSCREAM = Phanton.getProperty("tvt_stormscream_count", 0);
		
	}	
	
	/**
	 * Loads PvpZone settings.
	 */
	private static final void load_PvpZone()
	{
		final ExProperties votezone = initProperties(Config.PVPZONE_SYSTEM);
		
		VOTEZONE_INIT = Integer.parseInt(votezone.getProperty("ZoneId", "1"));
		VOTE_PVPZONE_ENABLED = votezone.getProperty("VotePvpZone", false);
		ENABLED_PVPZONE_KETRA = votezone.getProperty("PvpZone_Ketra", false);
		ENABLED_PVPZONE_ILHA = votezone.getProperty("PvpZone_Primeval", false);
		ENABLED_PVPZONE_HEINE = votezone.getProperty("PvpZone_Heine", false);
		ENABLED_PVPZONE_IRISLAKE = votezone.getProperty("PvpZone_IrisLake", false);
		ENABLED_PVPZONE_ALLIGATOR = votezone.getProperty("PvpZone_Alligator", false);
		ENABLED_PVPZONE_IMPERIAL = votezone.getProperty("PvpZone_Imperial", false);
		ENABLED_PVPZONE_WHISPERS = votezone.getProperty("PvpZone_Field_of_Whispers", false);
		VOTE_PVPZONE_MIN_VOTE = Integer.parseInt(votezone.getProperty("Vote", "0"));
		VOTEZONE_RANDOM_RANGE = Integer.parseInt(votezone.getProperty("RandomRange", "0"));
		VOTE_PVPZONE_INTERVAL_BY_TIME_OF_DAY = votezone.getProperty("VotePvpZoneStartTime", "20:00").split(",");
		VOTE_PVPZONE_TIME = Integer.parseInt(votezone.getProperty("VotePvpTime", "1"));
		
		PVPZONE_INIT = Integer.parseInt(votezone.getProperty("ZoneId", "1"));
		
		ZONE_1X = Integer.parseInt(votezone.getProperty("Zone1x", "128520"));
		ZONE_1Y = Integer.parseInt(votezone.getProperty("Zone1y", "-73208"));
		ZONE_1Z = Integer.parseInt(votezone.getProperty("Zone1z", "-3439"));
		
		ZONE_2X = Integer.parseInt(votezone.getProperty("Zone2x", "10488"));
		ZONE_2Y = Integer.parseInt(votezone.getProperty("Zone2y", "-24728"));
		ZONE_2Z = Integer.parseInt(votezone.getProperty("Zone2z", "-3651"));
		
		ZONE_3X = Integer.parseInt(votezone.getProperty("Zone3x", "111336"));
		ZONE_3Y = Integer.parseInt(votezone.getProperty("Zone3y", "219048"));
		ZONE_3Z = Integer.parseInt(votezone.getProperty("Zone3z", "-3543"));
		
		ZONE_4X = Integer.parseInt(votezone.getProperty("Zone4x", "53161"));
		ZONE_4Y = Integer.parseInt(votezone.getProperty("Zone4y", "83221"));
		ZONE_4Z = Integer.parseInt(votezone.getProperty("Zone4z", "-3467"));
		
		ZONE_5X = Integer.parseInt(votezone.getProperty("Zone5x", "115864"));
		ZONE_5Y = Integer.parseInt(votezone.getProperty("Zone5y", "193132"));
		ZONE_5Z = Integer.parseInt(votezone.getProperty("Zone5z", "-3592"));
		
		ZONE_6X = Integer.parseInt(votezone.getProperty("Zone6x", "115864"));
		ZONE_6Y = Integer.parseInt(votezone.getProperty("Zone6y", "193132"));
		ZONE_6Z = Integer.parseInt(votezone.getProperty("Zone6z", "-3592"));
		
		ZONE_7X = Integer.parseInt(votezone.getProperty("Zone7x", "115864"));
		ZONE_7Y = Integer.parseInt(votezone.getProperty("Zone7y", "193132"));
		ZONE_7Z = Integer.parseInt(votezone.getProperty("Zone7z", "-3592"));
		
		ZONE_8X = Integer.parseInt(votezone.getProperty("Zone8x", "115864"));
		ZONE_8Y = Integer.parseInt(votezone.getProperty("Zone8y", "193132"));
		ZONE_8Z = Integer.parseInt(votezone.getProperty("Zone8z", "-3592"));
		
	}
	
	
	/**
	 * Load loadProtect settings.
	 */
	private static final void loadProtect()
	{
		final ExProperties protect = initProperties(PROTECT_FILE);
		
		
		//Sistema de nao equipar muito rapido Weapon
		USER_ITEM_TIME_WEAPON = protect.getProperty("UserItemWeaponTime", 4200);
		
		ANTIZERG_RES = protect.getProperty("AntiZerg_resurrect", false);
		
		BOSSZONE_HWID_PROTECT = protect.getProperty("BossZoneHwidProtect", false);
		MAX_BOX_IN_BOSSZONE = protect.getProperty("MaxPlayersPerHwid_inBossZone", 1);
		
		FLAGZONE_HWID_PROTECT = protect.getProperty("FlagZoneHwidProtect", false);
		MAX_BOX_IN_FLAGZONE = protect.getProperty("MaxPlayersPerHwid_inFlagZone", 1);
		
		SOLOZONE_HWID_PROTECT = protect.getProperty("SoloZoneHwidProtect", false);
		MAX_BOX_IN_SOLOZONE = protect.getProperty("MaxPlayersPerHwid_inSoloZone", 1);
		
		ANTZERG_CHECK_PARTY_INVITE = protect.getProperty("CheckPartyInvite", false);
		ALLOW_HEALER_COUNT = protect.getProperty("AllowHealerCount", false);
		MAX_HEALER_PARTY = protect.getProperty("MaxHealerInParty", 2);
		
		BOTS_PREVENTION = protect.getProperty("EnableBotsPrevention", false);
		KILLS_COUNTER = protect.getProperty("KillsCounter", 60);
		KILLS_COUNTER_EXTRA = protect.getProperty("KillsCounterExtra", 5);
		
		EXTREME_CAPTCHA = protect.getProperty("ExtremeCaptcha", 60);
		
		PVP_BOTS_PREVENTION = protect.getProperty("EnablePvPBotsPrevention", false);
		PVP_KILLS_COUNTER = protect.getProperty("Pvp_KillsCounter", 60);
		PVP_KILLS_COUNTER_EXTRA = protect.getProperty("Pvp_KillsCounterExtra", 5);
		
		VALIDATION_TIME = protect.getProperty("ValidationTime", 60);
		
		NO_DELETE = protect.getProperty("NoDeleteItens", "");
		LIST_NO_DELETE_ITEM = new ArrayList<>();
		for (String itemId : NO_DELETE.split(","))
		{
			LIST_NO_DELETE_ITEM.add(Integer.parseInt(itemId));
		}
		
		NO_SELL = protect.getProperty("NoSellItens", "");
		LIST_NO_SELL_ITEM = new ArrayList<>();
		for (String itemId : NO_SELL.split(","))
		{
			LIST_NO_SELL_ITEM.add(Integer.parseInt(itemId));
		}
		
		NO_DEPOSITE = protect.getProperty("NoDepositeItens", "");
		LIST_NO_DEPOSITE_ITEM = new ArrayList<>();
		for (String itemId : NO_DEPOSITE.split(","))
		{
			LIST_NO_DEPOSITE_ITEM.add(Integer.parseInt(itemId));
		}
		
		NO_DROP = protect.getProperty("NoDropItens", "");
		LIST_NO_DROP_ITEM = new ArrayList<>();
		for (String itemId : NO_DROP.split(","))
		{
			LIST_NO_DROP_ITEM.add(Integer.parseInt(itemId));
		}
		
		NO_TRADE = protect.getProperty("NoTradeItens", "");
		LIST_NO_TRADE_ITEM = new ArrayList<>();
		for (String itemId : NO_TRADE.split(","))
		{
			LIST_NO_TRADE_ITEM.add(Integer.parseInt(itemId));
		}
		
		ENCHANT_PROTECT = protect.getProperty("Enchant", 23);
		
		ALLOW_HEAVY_USE_LIGHT = Boolean.parseBoolean(protect.getProperty("AllowHeavyUseLight", "False"));
		NOTALLOWCLASSE = protect.getProperty("NotAllowedUseLight", "");
		NOTALLOWEDUSELIGHT = new ArrayList<>();
		for (String classId : NOTALLOWCLASSE.split(","))
		{
			NOTALLOWEDUSELIGHT.add(Integer.parseInt(classId));
		}
		
		REMOVE_WEAPON = Boolean.parseBoolean(protect.getProperty("RemoveWeapon", "False"));
		REMOVE_CHEST = Boolean.parseBoolean(protect.getProperty("RemoveChest", "False"));
		REMOVE_LEG = Boolean.parseBoolean(protect.getProperty("RemoveLeg", "False"));
		
		ALLOW_LIGHT_USE_HEAVY = Boolean.parseBoolean(protect.getProperty("AllowLightUseHeavy", "False"));
		NOTALLOWCLASS = protect.getProperty("NotAllowedUseHeavy", "");
		NOTALLOWEDUSEHEAVY = new ArrayList<>();
		for (String classId : NOTALLOWCLASS.split(","))
		{
			NOTALLOWEDUSEHEAVY.add(Integer.parseInt(classId));
		}
		
		ALT_DISABLE_BOW_CLASSES_OLY = Boolean.parseBoolean(protect.getProperty("AltDisableBow_Oly", "False"));
		ALT_DISABLE_BOW_CLASSES = Boolean.parseBoolean(protect.getProperty("AltDisableBow", "False"));
		DISABLE_BOW_CLASSES_STRING = protect.getProperty("DisableBowForClasses", "");
		DISABLE_BOW_CLASSES = new ArrayList<>();
		for (String class_id : DISABLE_BOW_CLASSES_STRING.split(","))
		{
			if (!class_id.equals(""))
				DISABLE_BOW_CLASSES.add(Integer.parseInt(class_id));
		}
		
		WARN_ITEM_ENABLED = Boolean.parseBoolean(protect.getProperty("ProtectItemEnabled", "False"));
		
		WARN_LIST_ITEM = Boolean.parseBoolean(protect.getProperty("CheckItem", "False"));
		
		if (Config.WARN_ITEM_ENABLED)
		{
			String[] propertySplit1 = protect.getProperty("ProtectItem", "57,0").split(";");
			PROTECT_ITEMS.clear();
			for (String reward : propertySplit1)
			{
				String[] rewardSplit = reward.split(",");
				if (rewardSplit.length != 2)
					_log.warning("ProtectItem[Config.load()]: invalid config property -> ProtectItem \"" + reward + "\"");
				else
				{
					try
					{
						PROTECT_ITEMS.add(new int[]
							{
							Integer.parseInt(rewardSplit[0]),
							Integer.parseInt(rewardSplit[1])
							});
					}
					catch (NumberFormatException nfe)
					{
						if (!reward.isEmpty())
							_log.warning("ProtectItem[Config.load()]: invalid config property -> ProtectItem \"" + reward + "\"");
					}
				}
			}
		}
		
		WARN_ENCHANT_ITEM_ENABLED = Boolean.parseBoolean(protect.getProperty("CheckEnchant", "False"));
		WARN_ENCHANT_LEVEL = protect.getProperty("EnchantLevel", 0);
		
		ALLOW_DUALBOX = Boolean.parseBoolean(protect.getProperty("AllowDualBox", "True"));
		ALLOWED_BOXES = Integer.parseInt(protect.getProperty("AllowedBoxes", "1"));
		ALLOW_DUALBOX_OLY = Boolean.parseBoolean(protect.getProperty("AllowDualBoxInOly", "True"));
		
		MULTIBOX_PROTECTION_ENABLED = protect.getProperty("MultiboxProtectionEnabled", false);
		MULTIBOX_PROTECTION_CLIENTS_PER_PC = protect.getProperty("ClientsPerPc", 2);
		
		MAGE_ID_RESTRICT = protect.getProperty("MageItem");
		
		MAGE_LISTID_RESTRICT = new ArrayList<>();
		for (String id : MAGE_ID_RESTRICT.split(","))
			MAGE_LISTID_RESTRICT.add(Integer.parseInt(id));
		
		FIGHTER_ID_RESTRICT = protect.getProperty("FighterItem");
		
		FIGHTER_LISTID_RESTRICT = new ArrayList<>();
		for (String id : FIGHTER_ID_RESTRICT.split(","))
			FIGHTER_LISTID_RESTRICT.add(Integer.parseInt(id));
		
	}
	
	/**
	 * load PvpKing
	 */
	private static final void loadPvpKing()
	{
		final ExProperties pvpking = initProperties(PVPKING_FILE);
		CKM_ENABLED = pvpking.getProperty("CKMEnabled", false);
		CKM_CYCLE_LENGTH = pvpking.getProperty("CKMCycleLength", 86400000);
		CKM_PVP_NPC_TITLE = pvpking.getProperty("CKMPvPNpcTitle", "%kills% PvPs in the last 24h");
		CKM_PVP_NPC_TITLE_COLOR = Integer.decode("0x" + pvpking.getProperty("CKMPvPNpcTitleColor", "00CCFF"));
		CKM_PVP_NPC_NAME_COLOR = Integer.decode("0x" + pvpking.getProperty("CKMPvPNpcNameColor", "FFFFFF"));
		CKM_PK_NPC_TITLE = pvpking.getProperty("CKMPKNpcTitle", "%kills% PKs in the last 24h");
		CKM_PK_NPC_TITLE_COLOR = Integer.decode("0x" + pvpking.getProperty("CKMPKNpcTitleColor", "00CCFF"));
		CKM_PK_NPC_NAME_COLOR = Integer.decode("0x" + pvpking.getProperty("CKMPKNpcNameColor", "FFFFFF"));
		CKM_PLAYER_REWARDS = parseItemsList(pvpking.getProperty("CKMReward", "6651,50"));
	}
	
	/**
	 * load class duelist
	 */
	private static final void loadDuelist()
	{
		final ExProperties dmg = initProperties(Duelist_FILE);
		Duelist_vs_DreadNought = Float.parseFloat(dmg.getProperty("Duelist_vs_DreadNought", "1.00"));
		Duelist_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Duelist_vs_Phoenix_Knight", "1.00"));
		Duelist_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Duelist_vs_Hell_Knight", "1.00"));
		Duelist_vs_Sagittarius = Float.parseFloat(dmg.getProperty("Duelist_vs_Sagittarius", "1.00"));
		Duelist_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Duelist_vs_Eva_Templar", "1.00"));
		Duelist_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Duelist_vs_Sword_Muse", "1.00"));
		Duelist_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Duelist_vs_Moonlight_Sentinel", "1.00"));
		Duelist_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Duelist_vs_Shillien_Templar", "1.00"));
		Duelist_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Duelist_vs_Spectral_Dancer", "1.00"));
		Duelist_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Duelist_vs_Ghost_Sentinel", "1.00"));
		Duelist_vs_Titan = Float.parseFloat(dmg.getProperty("Duelist_vs_Titan", "1.00"));
		Duelist_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Duelist_vs_Grand_Khauatari", "1.00"));
		Duelist_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Duelist_vs_Fortune_Seeker", "1.00"));
		Duelist_vs_Maestro = Float.parseFloat(dmg.getProperty("Duelist_vs_Maestro", "1.00"));
		Duelist_vs_Adventurer = Float.parseFloat(dmg.getProperty("Duelist_vs_Adventurer", "1.00"));
		Duelist_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Duelist_vs_Wind_Rider", "1.00"));
		Duelist_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Duelist_vs_Ghost_Hunter", "1.00"));
		Duelist_vs_Archmage = Float.parseFloat(dmg.getProperty("Duelist_vs_Archmage", "1.00"));
		Duelist_vs_Soultaker = Float.parseFloat(dmg.getProperty("Duelist_vs_Soultaker", "1.00"));
		Duelist_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Duelist_vs_Arcana_Lord", "1.00"));
		Duelist_vs_Cardinal = Float.parseFloat(dmg.getProperty("Duelist_vs_Cardinal", "1.00"));
		Duelist_vs_Hierophant = Float.parseFloat(dmg.getProperty("Duelist_vs_Hierophant", "1.00"));
		Duelist_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Duelist_vs_Mystic_Muse", "1.00"));
		Duelist_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Duelist_vs_Elemental_Master", "1.00"));
		Duelist_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Duelist_vs_Eva_s_Saint", "1.00"));
		Duelist_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Duelist_vs_Storm_Screamer", "1.00"));
		Duelist_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Duelist_vs_Spectral_Master", "1.00"));
		Duelist_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Duelist_vs_Shillien_Saint", "1.00"));
		Duelist_vs_Dominator = Float.parseFloat(dmg.getProperty("Duelist_vs_Dominator", "1.00"));
		Duelist_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Duelist_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class DreadNought
	 */
	private static final void loadDreadNought()
	{
		final ExProperties dmg = initProperties(DreadNought_FILE);
		DreadNought_vs_Duelist = Float.parseFloat(dmg.getProperty("DreadNought_vs_Duelist", "1.00"));
		DreadNought_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("DreadNought_vs_Phoenix_Knight", "1.00"));
		DreadNought_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("DreadNought_vs_Hell_Knight", "1.00"));
		DreadNought_vs_Sagittarius = Float.parseFloat(dmg.getProperty("DreadNought_vs_Sagittarius", "1.00"));
		DreadNought_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("DreadNought_vs_Eva_Templar", "1.00"));
		DreadNought_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("DreadNought_vs_Sword_Muse", "1.00"));
		DreadNought_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("DreadNought_vs_Moonlight_Sentinel", "1.00"));
		DreadNought_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("DreadNought_vs_Shillien_Templar", "1.00"));
		DreadNought_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("DreadNought_vs_Spectral_Dancer", "1.00"));
		DreadNought_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("DreadNought_vs_Ghost_Sentinel", "1.00"));
		DreadNought_vs_Titan = Float.parseFloat(dmg.getProperty("DreadNought_vs_Titan", "1.00"));
		DreadNought_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("DreadNought_vs_Grand_Khauatari", "1.00"));
		DreadNought_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("DreadNought_vs_Fortune_Seeker", "1.00"));
		DreadNought_vs_Maestro = Float.parseFloat(dmg.getProperty("DreadNought_vs_Maestro", "1.00"));
		DreadNought_vs_Adventurer = Float.parseFloat(dmg.getProperty("DreadNought_vs_Adventurer", "1.00"));
		DreadNought_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("DreadNought_vs_Wind_Rider", "1.00"));
		DreadNought_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("DreadNought_vs_Ghost_Hunter", "1.00"));
		DreadNought_vs_Archmage = Float.parseFloat(dmg.getProperty("DreadNought_vs_Archmage", "1.00"));
		DreadNought_vs_Soultaker = Float.parseFloat(dmg.getProperty("DreadNought_vs_Soultaker", "1.00"));
		DreadNought_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("DreadNought_vs_Arcana_Lord", "1.00"));
		DreadNought_vs_Cardinal = Float.parseFloat(dmg.getProperty("DreadNought_vs_Cardinal", "1.00"));
		DreadNought_vs_Hierophant = Float.parseFloat(dmg.getProperty("DreadNought_vs_Hierophant", "1.00"));
		DreadNought_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("DreadNought_vs_Mystic_Muse", "1.00"));
		DreadNought_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("DreadNought_vs_Elemental_Master", "1.00"));
		DreadNought_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("DreadNought_vs_Eva_s_Saint", "1.00"));
		DreadNought_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("DreadNought_vs_Storm_Screamer", "1.00"));
		DreadNought_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("DreadNought_vs_Spectral_Master", "1.00"));
		DreadNought_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("DreadNought_vs_Shillien_Saint", "1.00"));
		DreadNought_vs_Dominator = Float.parseFloat(dmg.getProperty("DreadNought_vs_Dominator", "1.00"));
		DreadNought_vs_Doomcryer = Float.parseFloat(dmg.getProperty("DreadNought_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Phoenix_Knight
	 */
	private static final void loadPhoenix_Knight()
	{
		final ExProperties dmg = initProperties(Phoenix_Knight_FILE);
		Phoenix_Knight_vs_Duelist = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Duelist", "1.00"));
		Phoenix_Knight_vs_DreadNought = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_DreadNought", "1.00"));
		Phoenix_Knight_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Hell_Knight", "1.00"));
		Phoenix_Knight_vs_Sagittarius = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Sagittarius", "1.00"));
		Phoenix_Knight_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Eva_Templar", "1.00"));
		Phoenix_Knight_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Sword_Muse", "1.00"));
		Phoenix_Knight_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Moonlight_Sentinel", "1.00"));
		Phoenix_Knight_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Shillien_Templar", "1.00"));
		Phoenix_Knight_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Spectral_Dancer", "1.00"));
		Phoenix_Knight_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Ghost_Sentinel", "1.00"));
		Phoenix_Knight_vs_Titan = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Titan", "1.00"));
		Phoenix_Knight_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Grand_Khauatari", "1.00"));
		Phoenix_Knight_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Fortune_Seeker", "1.00"));
		Phoenix_Knight_vs_Maestro = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Maestro", "1.00"));
		Phoenix_Knight_vs_Adventurer = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Adventurer", "1.00"));
		Phoenix_Knight_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Wind_Rider", "1.00"));
		Phoenix_Knight_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Ghost_Hunter", "1.00"));
		Phoenix_Knight_vs_Archmage = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Archmage", "1.00"));
		Phoenix_Knight_vs_Soultaker = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Soultaker", "1.00"));
		Phoenix_Knight_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Arcana_Lord", "1.00"));
		Phoenix_Knight_vs_Cardinal = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Cardinal", "1.00"));
		Phoenix_Knight_vs_Hierophant = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Hierophant", "1.00"));
		Phoenix_Knight_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Mystic_Muse", "1.00"));
		Phoenix_Knight_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Elemental_Master", "1.00"));
		Phoenix_Knight_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Eva_s_Saint", "1.00"));
		Phoenix_Knight_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Storm_Screamer", "1.00"));
		Phoenix_Knight_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Spectral_Master", "1.00"));
		Phoenix_Knight_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Shillien_Saint", "1.00"));
		Phoenix_Knight_vs_Dominator = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Dominator", "1.00"));
		Phoenix_Knight_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Phoenix_Knight_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Hell_Knight
	 */
	private static final void loadHell_Knight()
	{
		final ExProperties dmg = initProperties(Hell_Knight_FILE);
		Hell_Knight_vs_Duelist = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Duelist", "1.00"));
		Hell_Knight_vs_DreadNought = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_DreadNought", "1.00"));
		Hell_Knight_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Phoenix_Knight", "1.00"));
		Hell_Knight_vs_Sagittarius = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Sagittarius", "1.00"));
		Hell_Knight_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Eva_Templar", "1.00"));
		Hell_Knight_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Sword_Muse", "1.00"));
		Hell_Knight_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Moonlight_Sentinel", "1.00"));
		Hell_Knight_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Shillien_Templar", "1.00"));
		Hell_Knight_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Spectral_Dancer", "1.00"));
		Hell_Knight_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Ghost_Sentinel", "1.00"));
		Hell_Knight_vs_Titan = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Titan", "1.00"));
		Hell_Knight_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Grand_Khauatari", "1.00"));
		Hell_Knight_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Fortune_Seeker", "1.00"));
		Hell_Knight_vs_Maestro = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Maestro", "1.00"));
		Hell_Knight_vs_Adventurer = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Adventurer", "1.00"));
		Hell_Knight_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Wind_Rider", "1.00"));
		Hell_Knight_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Ghost_Hunter", "1.00"));
		Hell_Knight_vs_Archmage = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Archmage", "1.00"));
		Hell_Knight_vs_Soultaker = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Soultaker", "1.00"));
		Hell_Knight_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Arcana_Lord", "1.00"));
		Hell_Knight_vs_Cardinal = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Cardinal", "1.00"));
		Hell_Knight_vs_Hierophant = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Hierophant", "1.00"));
		Hell_Knight_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Mystic_Muse", "1.00"));
		Hell_Knight_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Elemental_Master", "1.00"));
		Hell_Knight_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Eva_s_Saint", "1.00"));
		Hell_Knight_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Storm_Screamer", "1.00"));
		Hell_Knight_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Spectral_Master", "1.00"));
		Hell_Knight_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Shillien_Saint", "1.00"));
		Hell_Knight_vs_Dominator = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Dominator", "1.00"));
		Hell_Knight_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Hell_Knight_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Sagittarius
	 */
	private static final void loadSagittarius()
	{
		final ExProperties dmg = initProperties(Sagittarius_FILE);
		Sagittarius_vs_Duelist = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Duelist", "1.00"));
		Sagittarius_vs_DreadNought = Float.parseFloat(dmg.getProperty("Sagittarius_vs_DreadNought", "1.00"));
		Sagittarius_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Phoenix_Knight", "1.00"));
		Sagittarius_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Hell_Knight", "1.00"));
		Sagittarius_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Eva_Templar", "1.00"));
		Sagittarius_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Sword_Muse", "1.00"));
		Sagittarius_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Moonlight_Sentinel", "1.00"));
		Sagittarius_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Shillien_Templar", "1.00"));
		Sagittarius_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Spectral_Dancer", "1.00"));
		Sagittarius_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Ghost_Sentinel", "1.00"));
		Sagittarius_vs_Titan = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Titan", "1.00"));
		Sagittarius_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Grand_Khauatari", "1.00"));
		Sagittarius_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Fortune_Seeker", "1.00"));
		Sagittarius_vs_Maestro = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Maestro", "1.00"));
		Sagittarius_vs_Adventurer = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Adventurer", "1.00"));
		Sagittarius_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Wind_Rider", "1.00"));
		Sagittarius_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Ghost_Hunter", "1.00"));
		Sagittarius_vs_Archmage = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Archmage", "1.00"));
		Sagittarius_vs_Soultaker = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Soultaker", "1.00"));
		Sagittarius_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Arcana_Lord", "1.00"));
		Sagittarius_vs_Cardinal = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Cardinal", "1.00"));
		Sagittarius_vs_Hierophant = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Hierophant", "1.00"));
		Sagittarius_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Mystic_Muse", "1.00"));
		Sagittarius_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Elemental_Master", "1.00"));
		Sagittarius_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Eva_s_Saint", "1.00"));
		Sagittarius_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Storm_Screamer", "1.00"));
		Sagittarius_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Spectral_Master", "1.00"));
		Sagittarius_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Shillien_Saint", "1.00"));
		Sagittarius_vs_Dominator = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Dominator", "1.00"));
		Sagittarius_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Sagittarius_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Eva_Templar
	 */
	private static final void loadEva_Templar()
	{
		final ExProperties dmg = initProperties(Eva_Templar_FILE);
		Eva_Templar_vs_Duelist = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Duelist", "1.00"));
		Eva_Templar_vs_DreadNought = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_DreadNought", "1.00"));
		Eva_Templar_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Phoenix_Knight", "1.00"));
		Eva_Templar_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Hell_Knight", "1.00"));
		Eva_Templar_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Sagitarius", "1.00"));
		Eva_Templar_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Sword_Muse", "1.00"));
		Eva_Templar_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Moonlight_Sentinel", "1.00"));
		Eva_Templar_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Shillien_Templar", "1.00"));
		Eva_Templar_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Spectral_Dancer", "1.00"));
		Eva_Templar_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Ghost_Sentinel", "1.00"));
		Eva_Templar_vs_Titan = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Titan", "1.00"));
		Eva_Templar_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Grand_Khauatari", "1.00"));
		Eva_Templar_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Fortune_Seeker", "1.00"));
		Eva_Templar_vs_Maestro = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Maestro", "1.00"));
		Eva_Templar_vs_Adventurer = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Adventurer", "1.00"));
		Eva_Templar_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Wind_Rider", "1.00"));
		Eva_Templar_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Ghost_Hunter", "1.00"));
		Eva_Templar_vs_Archmage = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Archmage", "1.00"));
		Eva_Templar_vs_Soultaker = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Soultaker", "1.00"));
		Eva_Templar_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Arcana_Lord", "1.00"));
		Eva_Templar_vs_Cardinal = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Cardinal", "1.00"));
		Eva_Templar_vs_Hierophant = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Hierophant", "1.00"));
		Eva_Templar_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Mystic_Muse", "1.00"));
		Eva_Templar_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Elemental_Master", "1.00"));
		Eva_Templar_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Eva_s_Saint", "1.00"));
		Eva_Templar_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Storm_Screamer", "1.00"));
		Eva_Templar_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Spectral_Master", "1.00"));
		Eva_Templar_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Shillien_Saint", "1.00"));
		Eva_Templar_vs_Dominator = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Dominator", "1.00"));
		Eva_Templar_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Eva_Templar_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Sword_Muse
	 */
	private static final void loadSword_Muse()
	{
		final ExProperties dmg = initProperties(Sword_Muse_FILE);
		Sword_Muse_vs_Duelist = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Duelist", "1.00"));
		Sword_Muse_vs_DreadNought = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_DreadNought", "1.00"));
		Sword_Muse_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Phoenix_Knight", "1.00"));
		Sword_Muse_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Hell_Knight", "1.00"));
		Sword_Muse_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Sagitarius", "1.00"));
		Sword_Muse_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Eva_Templar", "1.00"));
		Sword_Muse_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Moonlight_Sentinel", "1.00"));
		Sword_Muse_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Shillien_Templar", "1.00"));
		Sword_Muse_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Spectral_Dancer", "1.00"));
		Sword_Muse_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Ghost_Sentinel", "1.00"));
		Sword_Muse_vs_Titan = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Titan", "1.00"));
		Sword_Muse_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Grand_Khauatari", "1.00"));
		Sword_Muse_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Fortune_Seeker", "1.00"));
		Sword_Muse_vs_Maestro = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Maestro", "1.00"));
		Sword_Muse_vs_Adventurer = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Adventurer", "1.00"));
		Sword_Muse_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Wind_Rider", "1.00"));
		Sword_Muse_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Ghost_Hunter", "1.00"));
		Sword_Muse_vs_Archmage = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Archmage", "1.00"));
		Sword_Muse_vs_Soultaker = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Soultaker", "1.00"));
		Sword_Muse_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Arcana_Lord", "1.00"));
		Sword_Muse_vs_Cardinal = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Cardinal", "1.00"));
		Sword_Muse_vs_Hierophant = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Hierophant", "1.00"));
		Sword_Muse_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Mystic_Muse", "1.00"));
		Sword_Muse_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Elemental_Master", "1.00"));
		Sword_Muse_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Eva_s_Saint", "1.00"));
		Sword_Muse_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Storm_Screamer", "1.00"));
		Sword_Muse_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Spectral_Master", "1.00"));
		Sword_Muse_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Shillien_Saint", "1.00"));
		Sword_Muse_vs_Dominator = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Dominator", "1.00"));
		Sword_Muse_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Sword_Muse_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Moonlight_Sentinel
	 */
	private static final void loadMoonlight_Sentinel()
	{
		final ExProperties dmg = initProperties(Moonlight_Sentinel_FILE);
		Moonlight_Sentinel_vs_Duelist = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Duelist", "1.00"));
		Moonlight_Sentinel_vs_DreadNought = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_DreadNought", "1.00"));
		Moonlight_Sentinel_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Phoenix_Knight", "1.00"));
		Moonlight_Sentinel_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Hell_Knight", "1.00"));
		Moonlight_Sentinel_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Sagitarius", "1.00"));
		Moonlight_Sentinel_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Eva_Templar", "1.00"));
		Moonlight_Sentinel_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Sword_Muse", "1.00"));
		Moonlight_Sentinel_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Shillien_Templar", "1.00"));
		Moonlight_Sentinel_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Spectral_Dancer", "1.00"));
		Moonlight_Sentinel_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Ghost_Sentinel", "1.00"));
		Moonlight_Sentinel_vs_Titan = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Titan", "1.00"));
		Moonlight_Sentinel_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Grand_Khauatari", "1.00"));
		Moonlight_Sentinel_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Fortune_Seeker", "1.00"));
		Moonlight_Sentinel_vs_Maestro = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Maestro", "1.00"));
		Moonlight_Sentinel_vs_Adventurer = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Adventurer", "1.00"));
		Moonlight_Sentinel_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Wind_Rider", "1.00"));
		Moonlight_Sentinel_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Ghost_Hunter", "1.00"));
		Moonlight_Sentinel_vs_Archmage = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Archmage", "1.00"));
		Moonlight_Sentinel_vs_Soultaker = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Soultaker", "1.00"));
		Moonlight_Sentinel_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Arcana_Lord", "1.00"));
		Moonlight_Sentinel_vs_Cardinal = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Cardinal", "1.00"));
		Moonlight_Sentinel_vs_Hierophant = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Hierophant", "1.00"));
		Moonlight_Sentinel_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Mystic_Muse", "1.00"));
		Moonlight_Sentinel_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Elemental_Master", "1.00"));
		Moonlight_Sentinel_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Eva_s_Saint", "1.00"));
		Moonlight_Sentinel_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Storm_Screamer", "1.00"));
		Moonlight_Sentinel_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Spectral_Master", "1.00"));
		Moonlight_Sentinel_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Shillien_Saint", "1.00"));
		Moonlight_Sentinel_vs_Dominator = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Dominator", "1.00"));
		Moonlight_Sentinel_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Moonlight_Sentinel_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Shillien_Templar
	 */
	private static final void loadShillien_Templar()
	{
		final ExProperties dmg = initProperties(Shillien_Templar_FILE);
		Shillien_Templar_vs_Duelist = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Duelist", "1.00"));
		Shillien_Templar_vs_DreadNought = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_DreadNought", "1.00"));
		Shillien_Templar_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Phoenix_Knight", "1.00"));
		Shillien_Templar_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Hell_Knight", "1.00"));
		Shillien_Templar_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Sagitarius", "1.00"));
		Shillien_Templar_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Eva_Templar", "1.00"));
		Shillien_Templar_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Sword_Muse", "1.00"));
		Shillien_Templar_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Moonlight_Sentinel", "1.00"));
		Shillien_Templar_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Spectral_Dancer", "1.00"));
		Shillien_Templar_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Ghost_Sentinel", "1.00"));
		Shillien_Templar_vs_Titan = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Titan", "1.00"));
		Shillien_Templar_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Grand_Khauatari", "1.00"));
		Shillien_Templar_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Fortune_Seeker", "1.00"));
		Shillien_Templar_vs_Maestro = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Maestro", "1.00"));
		Shillien_Templar_vs_Adventurer = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Adventurer", "1.00"));
		Shillien_Templar_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Wind_Rider", "1.00"));
		Shillien_Templar_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Ghost_Hunter", "1.00"));
		Shillien_Templar_vs_Archmage = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Archmage", "1.00"));
		Shillien_Templar_vs_Soultaker = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Soultaker", "1.00"));
		Shillien_Templar_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Arcana_Lord", "1.00"));
		Shillien_Templar_vs_Cardinal = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Cardinal", "1.00"));
		Shillien_Templar_vs_Hierophant = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Hierophant", "1.00"));
		Shillien_Templar_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Mystic_Muse", "1.00"));
		Shillien_Templar_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Elemental_Master", "1.00"));
		Shillien_Templar_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Eva_s_Saint", "1.00"));
		Shillien_Templar_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Storm_Screamer", "1.00"));
		Shillien_Templar_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Spectral_Master", "1.00"));
		Shillien_Templar_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Shillien_Saint", "1.00"));
		Shillien_Templar_vs_Dominator = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Dominator", "1.00"));
		Shillien_Templar_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Shillien_Templar_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Spectral_Dancer
	 */
	private static final void loadSpectral_Dancer()
	{
		final ExProperties dmg = initProperties(Spectral_Dancer_FILE);
		Spectral_Dancer_vs_Duelist = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Duelist", "1.00"));
		Spectral_Dancer_vs_DreadNought = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_DreadNought", "1.00"));
		Spectral_Dancer_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Phoenix_Knight", "1.00"));
		Spectral_Dancer_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Hell_Knight", "1.00"));
		Spectral_Dancer_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Sagitarius", "1.00"));
		Spectral_Dancer_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Eva_Templar", "1.00"));
		Spectral_Dancer_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Sword_Muse", "1.00"));
		Spectral_Dancer_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Moonlight_Sentinel", "1.00"));
		Spectral_Dancer_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Shillien_Templar", "1.00"));
		Spectral_Dancer_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Ghost_Sentinel", "1.00"));
		Spectral_Dancer_vs_Titan = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Titan", "1.00"));
		Spectral_Dancer_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Grand_Khauatari", "1.00"));
		Spectral_Dancer_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Fortune_Seeker", "1.00"));
		Spectral_Dancer_vs_Maestro = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Maestro", "1.00"));
		Spectral_Dancer_vs_Adventurer = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Adventurer", "1.00"));
		Spectral_Dancer_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Wind_Rider", "1.00"));
		Spectral_Dancer_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Ghost_Hunter", "1.00"));
		Spectral_Dancer_vs_Archmage = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Archmage", "1.00"));
		Spectral_Dancer_vs_Soultaker = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Soultaker", "1.00"));
		Spectral_Dancer_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Arcana_Lord", "1.00"));
		Spectral_Dancer_vs_Cardinal = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Cardinal", "1.00"));
		Spectral_Dancer_vs_Hierophant = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Hierophant", "1.00"));
		Spectral_Dancer_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Mystic_Muse", "1.00"));
		Spectral_Dancer_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Elemental_Master", "1.00"));
		Spectral_Dancer_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Eva_s_Saint", "1.00"));
		Spectral_Dancer_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Storm_Screamer", "1.00"));
		Spectral_Dancer_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Spectral_Master", "1.00"));
		Spectral_Dancer_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Shillien_Saint", "1.00"));
		Spectral_Dancer_vs_Dominator = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Dominator", "1.00"));
		Spectral_Dancer_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Spectral_Dancer_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Ghost_Sentinel
	 */
	private static final void loadGhost_Sentinel()
	{
		final ExProperties dmg = initProperties(Ghost_Sentinel_FILE);
		Ghost_Sentinel_vs_Duelist = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Duelist", "1.00"));
		Ghost_Sentinel_vs_DreadNought = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_DreadNought", "1.00"));
		Ghost_Sentinel_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Phoenix_Knight", "1.00"));
		Ghost_Sentinel_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Hell_Knight", "1.00"));
		Ghost_Sentinel_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Sagitarius", "1.00"));
		Ghost_Sentinel_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Eva_Templar", "1.00"));
		Ghost_Sentinel_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Sword_Muse", "1.00"));
		Ghost_Sentinel_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Moonlight_Sentinel", "1.00"));
		Ghost_Sentinel_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Shillien_Templar", "1.00"));
		Ghost_Sentinel_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Spectral_Dancer", "1.00"));
		Ghost_Sentinel_vs_Titan = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Titan", "1.00"));
		Ghost_Sentinel_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Grand_Khauatari", "1.00"));
		Ghost_Sentinel_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Fortune_Seeker", "1.00"));
		Ghost_Sentinel_vs_Maestro = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Maestro", "1.00"));
		Ghost_Sentinel_vs_Adventurer = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Adventurer", "1.00"));
		Ghost_Sentinel_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Wind_Rider", "1.00"));
		Ghost_Sentinel_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Ghost_Hunter", "1.00"));
		Ghost_Sentinel_vs_Archmage = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Archmage", "1.00"));
		Ghost_Sentinel_vs_Soultaker = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Soultaker", "1.00"));
		Ghost_Sentinel_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Arcana_Lord", "1.00"));
		Ghost_Sentinel_vs_Cardinal = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Cardinal", "1.00"));
		Ghost_Sentinel_vs_Hierophant = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Hierophant", "1.00"));
		Ghost_Sentinel_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Mystic_Muse", "1.00"));
		Ghost_Sentinel_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Elemental_Master", "1.00"));
		Ghost_Sentinel_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Eva_s_Saint", "1.00"));
		Ghost_Sentinel_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Storm_Screamer", "1.00"));
		Ghost_Sentinel_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Spectral_Master", "1.00"));
		Ghost_Sentinel_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Shillien_Saint", "1.00"));
		Ghost_Sentinel_vs_Dominator = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Dominator", "1.00"));
		Ghost_Sentinel_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Ghost_Sentinel_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Titan
	 */
	private static final void loadTitan()
	{
		final ExProperties dmg = initProperties(Titan_FILE);
		Titan_vs_Duelist = Float.parseFloat(dmg.getProperty("Titan_vs_Duelist", "1.00"));
		Titan_vs_DreadNought = Float.parseFloat(dmg.getProperty("Titan_vs_DreadNought", "1.00"));
		Titan_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Titan_vs_Phoenix_Knight", "1.00"));
		Titan_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Titan_vs_Hell_Knight", "1.00"));
		Titan_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Titan_vs_Sagitarius", "1.00"));
		Titan_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Titan_vs_Eva_Templar", "1.00"));
		Titan_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Titan_vs_Sword_Muse", "1.00"));
		Titan_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Titan_vs_Moonlight_Sentinel", "1.00"));
		Titan_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Titan_vs_Shillien_Templar", "1.00"));
		Titan_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Titan_vs_Spectral_Dancer", "1.00"));
		Titan_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Titan_vs_Ghost_Sentinel", "1.00"));
		Titan_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Titan_vs_Grand_Khauatari", "1.00"));
		Titan_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Titan_vs_Fortune_Seeker", "1.00"));
		Titan_vs_Maestro = Float.parseFloat(dmg.getProperty("Titan_vs_Maestro", "1.00"));
		Titan_vs_Adventurer = Float.parseFloat(dmg.getProperty("Titan_vs_Adventurer", "1.00"));
		Titan_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Titan_vs_Wind_Rider", "1.00"));
		Titan_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Titan_vs_Ghost_Hunter", "1.00"));
		Titan_vs_Archmage = Float.parseFloat(dmg.getProperty("Titan_vs_Archmage", "1.00"));
		Titan_vs_Soultaker = Float.parseFloat(dmg.getProperty("Titan_vs_Soultaker", "1.00"));
		Titan_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Titan_vs_Arcana_Lord", "1.00"));
		Titan_vs_Cardinal = Float.parseFloat(dmg.getProperty("Titan_vs_Cardinal", "1.00"));
		Titan_vs_Hierophant = Float.parseFloat(dmg.getProperty("Titan_vs_Hierophant", "1.00"));
		Titan_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Titan_vs_Mystic_Muse", "1.00"));
		Titan_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Titan_vs_Elemental_Master", "1.00"));
		Titan_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Titan_vs_Eva_s_Saint", "1.00"));
		Titan_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Titan_vs_Storm_Screamer", "1.00"));
		Titan_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Titan_vs_Spectral_Master", "1.00"));
		Titan_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Titan_vs_Shillien_Saint", "1.00"));
		Titan_vs_Dominator = Float.parseFloat(dmg.getProperty("Titan_vs_Dominator", "1.00"));
		Titan_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Titan_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Grand_Khauatari
	 */
	private static final void loadGrand_Khauatari()
	{
		final ExProperties dmg = initProperties(Grand_Khauatari_FILE);
		Grand_Khauatari_vs_Duelist = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Duelist", "1.00"));
		Grand_Khauatari_vs_DreadNought = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_DreadNought", "1.00"));
		Grand_Khauatari_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Phoenix_Knight", "1.00"));
		Grand_Khauatari_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Hell_Knight", "1.00"));
		Grand_Khauatari_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Sagitarius", "1.00"));
		Grand_Khauatari_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Eva_Templar", "1.00"));
		Grand_Khauatari_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Sword_Muse", "1.00"));
		Grand_Khauatari_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Moonlight_Sentinel", "1.00"));
		Grand_Khauatari_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Shillien_Templar", "1.00"));
		Grand_Khauatari_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Spectral_Dancer", "1.00"));
		Grand_Khauatari_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Ghost_Sentinel", "1.00"));
		Grand_Khauatari_vs_Titan = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Titan", "1.00"));
		Grand_Khauatari_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Fortune_Seeker", "1.00"));
		Grand_Khauatari_vs_Maestro = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Maestro", "1.00"));
		Grand_Khauatari_vs_Adventurer = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Adventurer", "1.00"));
		Grand_Khauatari_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Wind_Rider", "1.00"));
		Grand_Khauatari_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Ghost_Hunter", "1.00"));
		Grand_Khauatari_vs_Archmage = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Archmage", "1.00"));
		Grand_Khauatari_vs_Soultaker = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Soultaker", "1.00"));
		Grand_Khauatari_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Arcana_Lord", "1.00"));
		Grand_Khauatari_vs_Cardinal = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Cardinal", "1.00"));
		Grand_Khauatari_vs_Hierophant = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Hierophant", "1.00"));
		Grand_Khauatari_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Mystic_Muse", "1.00"));
		Grand_Khauatari_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Elemental_Master", "1.00"));
		Grand_Khauatari_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Eva_s_Saint", "1.00"));
		Grand_Khauatari_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Storm_Screamer", "1.00"));
		Grand_Khauatari_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Spectral_Master", "1.00"));
		Grand_Khauatari_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Shillien_Saint", "1.00"));
		Grand_Khauatari_vs_Dominator = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Dominator", "1.00"));
		Grand_Khauatari_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Grand_Khauatari_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Fortune_Seeker
	 */
	private static final void loadFortune_Seeker()
	{
		final ExProperties dmg = initProperties(Fortune_Seeker_FILE);
		Fortune_Seeker_vs_Duelist = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Duelist", "1.00"));
		Fortune_Seeker_vs_DreadNought = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_DreadNought", "1.00"));
		Fortune_Seeker_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Phoenix_Knight", "1.00"));
		Fortune_Seeker_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Hell_Knight", "1.00"));
		Fortune_Seeker_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Sagitarius", "1.00"));
		Fortune_Seeker_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Eva_Templar", "1.00"));
		Fortune_Seeker_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Sword_Muse", "1.00"));
		Fortune_Seeker_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Moonlight_Sentinel", "1.00"));
		Fortune_Seeker_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Shillien_Templar", "1.00"));
		Fortune_Seeker_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Spectral_Dancer", "1.00"));
		Fortune_Seeker_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Ghost_Sentinel", "1.00"));
		Fortune_Seeker_vs_Titan = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Titan", "1.00"));
		Fortune_Seeker_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Grand_Khauatari", "1.00"));
		Fortune_Seeker_vs_Maestro = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Maestro", "1.00"));
		Fortune_Seeker_vs_Adventurer = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Adventurer", "1.00"));
		Fortune_Seeker_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Wind_Rider", "1.00"));
		Fortune_Seeker_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Ghost_Hunter", "1.00"));
		Fortune_Seeker_vs_Archmage = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Archmage", "1.00"));
		Fortune_Seeker_vs_Soultaker = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Soultaker", "1.00"));
		Fortune_Seeker_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Arcana_Lord", "1.00"));
		Fortune_Seeker_vs_Cardinal = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Cardinal", "1.00"));
		Fortune_Seeker_vs_Hierophant = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Hierophant", "1.00"));
		Fortune_Seeker_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Mystic_Muse", "1.00"));
		Fortune_Seeker_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Elemental_Master", "1.00"));
		Fortune_Seeker_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Eva_s_Saint", "1.00"));
		Fortune_Seeker_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Storm_Screamer", "1.00"));
		Fortune_Seeker_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Spectral_Master", "1.00"));
		Fortune_Seeker_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Shillien_Saint", "1.00"));
		Fortune_Seeker_vs_Dominator = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Dominator", "1.00"));
		Fortune_Seeker_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Fortune_Seeker_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Maestro
	 */
	private static final void loadMaestro()
	{
		final ExProperties dmg = initProperties(Maestro_FILE);
		Maestro_vs_Duelist = Float.parseFloat(dmg.getProperty("Maestro_vs_Duelist", "1.00"));
		Maestro_vs_DreadNought = Float.parseFloat(dmg.getProperty("Maestro_vs_DreadNought", "1.00"));
		Maestro_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Maestro_vs_Phoenix_Knight", "1.00"));
		Maestro_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Maestro_vs_Hell_Knight", "1.00"));
		Maestro_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Maestro_vs_Sagitarius", "1.00"));
		Maestro_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Maestro_vs_Eva_Templar", "1.00"));
		Maestro_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Maestro_vs_Sword_Muse", "1.00"));
		Maestro_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Maestro_vs_Moonlight_Sentinel", "1.00"));
		Maestro_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Maestro_vs_Shillien_Templar", "1.00"));
		Maestro_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Maestro_vs_Spectral_Dancer", "1.00"));
		Maestro_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Maestro_vs_Ghost_Sentinel", "1.00"));
		Maestro_vs_Titan = Float.parseFloat(dmg.getProperty("Maestro_vs_Titan", "1.00"));
		Maestro_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Maestro_vs_Grand_Khauatari", "1.00"));
		Maestro_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Maestro_vs_Fortune_Seeker", "1.00"));
		Maestro_vs_Adventurer = Float.parseFloat(dmg.getProperty("Maestro_vs_Adventurer", "1.00"));
		Maestro_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Maestro_vs_Wind_Rider", "1.00"));
		Maestro_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Maestro_vs_Ghost_Hunter", "1.00"));
		Maestro_vs_Archmage = Float.parseFloat(dmg.getProperty("Maestro_vs_Archmage", "1.00"));
		Maestro_vs_Soultaker = Float.parseFloat(dmg.getProperty("Maestro_vs_Soultaker", "1.00"));
		Maestro_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Maestro_vs_Arcana_Lord", "1.00"));
		Maestro_vs_Cardinal = Float.parseFloat(dmg.getProperty("Maestro_vs_Cardinal", "1.00"));
		Maestro_vs_Hierophant = Float.parseFloat(dmg.getProperty("Maestro_vs_Hierophant", "1.00"));
		Maestro_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Maestro_vs_Mystic_Muse", "1.00"));
		Maestro_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Maestro_vs_Elemental_Master", "1.00"));
		Maestro_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Maestro_vs_Eva_s_Saint", "1.00"));
		Maestro_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Maestro_vs_Storm_Screamer", "1.00"));
		Maestro_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Maestro_vs_Spectral_Master", "1.00"));
		Maestro_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Maestro_vs_Shillien_Saint", "1.00"));
		Maestro_vs_Dominator = Float.parseFloat(dmg.getProperty("Maestro_vs_Dominator", "1.00"));
		Maestro_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Maestro_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Adventurer
	 */
	private static final void loadAdventurer()
	{
		final ExProperties dmg = initProperties(Adventurer_FILE);
		Adventurer_vs_Duelist = Float.parseFloat(dmg.getProperty("Adventurer_vs_Duelist", "1.00"));
		Adventurer_vs_DreadNought = Float.parseFloat(dmg.getProperty("Adventurer_vs_DreadNought", "1.00"));
		Adventurer_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Adventurer_vs_Phoenix_Knight", "1.00"));
		Adventurer_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Adventurer_vs_Hell_Knight", "1.00"));
		Adventurer_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Adventurer_vs_Sagitarius", "1.00"));
		Adventurer_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Adventurer_vs_Eva_Templar", "1.00"));
		Adventurer_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Adventurer_vs_Sword_Muse", "1.00"));
		Adventurer_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Adventurer_vs_Moonlight_Sentinel", "1.00"));
		Adventurer_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Adventurer_vs_Shillien_Templar", "1.00"));
		Adventurer_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Adventurer_vs_Spectral_Dancer", "1.00"));
		Adventurer_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Adventurer_vs_Ghost_Sentinel", "1.00"));
		Adventurer_vs_Titan = Float.parseFloat(dmg.getProperty("Adventurer_vs_Titan", "1.00"));
		Adventurer_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Adventurer_vs_Grand_Khauatari", "1.00"));
		Adventurer_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Adventurer_vs_Fortune_Seeker", "1.00"));
		Adventurer_vs_Maestro = Float.parseFloat(dmg.getProperty("Adventurer_vs_Maestro", "1.00"));
		Adventurer_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Adventurer_vs_Wind_Rider", "1.00"));
		Adventurer_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Adventurer_vs_Ghost_Hunter", "1.00"));
		Adventurer_vs_Archmage = Float.parseFloat(dmg.getProperty("Adventurer_vs_Archmage", "1.00"));
		Adventurer_vs_Soultaker = Float.parseFloat(dmg.getProperty("Adventurer_vs_Soultaker", "1.00"));
		Adventurer_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Adventurer_vs_Arcana_Lord", "1.00"));
		Adventurer_vs_Cardinal = Float.parseFloat(dmg.getProperty("Adventurer_vs_Cardinal", "1.00"));
		Adventurer_vs_Hierophant = Float.parseFloat(dmg.getProperty("Adventurer_vs_Hierophant", "1.00"));
		Adventurer_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Adventurer_vs_Mystic_Muse", "1.00"));
		Adventurer_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Adventurer_vs_Elemental_Master", "1.00"));
		Adventurer_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Adventurer_vs_Eva_s_Saint", "1.00"));
		Adventurer_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Adventurer_vs_Storm_Screamer", "1.00"));
		Adventurer_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Adventurer_vs_Spectral_Master", "1.00"));
		Adventurer_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Adventurer_vs_Shillien_Saint", "1.00"));
		Adventurer_vs_Dominator = Float.parseFloat(dmg.getProperty("Adventurer_vs_Dominator", "1.00"));
		Adventurer_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Adventurer_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Wind_Rider
	 */
	private static final void loadWind_Rider()
	{
		final ExProperties dmg = initProperties(Wind_Rider_FILE);
		Wind_Rider_vs_Duelist = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Duelist", "1.00"));
		Wind_Rider_vs_DreadNought = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_DreadNought", "1.00"));
		Wind_Rider_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Phoenix_Knight", "1.00"));
		Wind_Rider_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Hell_Knight", "1.00"));
		Wind_Rider_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Sagitarius", "1.00"));
		Wind_Rider_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Eva_Templar", "1.00"));
		Wind_Rider_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Sword_Muse", "1.00"));
		Wind_Rider_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Moonlight_Sentinel", "1.00"));
		Wind_Rider_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Shillien_Templar", "1.00"));
		Wind_Rider_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Spectral_Dancer", "1.00"));
		Wind_Rider_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Ghost_Sentinel", "1.00"));
		Wind_Rider_vs_Titan = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Titan", "1.00"));
		Wind_Rider_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Grand_Khauatari", "1.00"));
		Wind_Rider_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Fortune_Seeker", "1.00"));
		Wind_Rider_vs_Maestro = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Maestro", "1.00"));
		Wind_Rider_vs_Adventurer = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Adventurer", "1.00"));
		Wind_Rider_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Ghost_Hunter", "1.00"));
		Wind_Rider_vs_Archmage = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Archmage", "1.00"));
		Wind_Rider_vs_Soultaker = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Soultaker", "1.00"));
		Wind_Rider_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Arcana_Lord", "1.00"));
		Wind_Rider_vs_Cardinal = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Cardinal", "1.00"));
		Wind_Rider_vs_Hierophant = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Hierophant", "1.00"));
		Wind_Rider_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Mystic_Muse", "1.00"));
		Wind_Rider_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Elemental_Master", "1.00"));
		Wind_Rider_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Eva_s_Saint", "1.00"));
		Wind_Rider_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Storm_Screamer", "1.00"));
		Wind_Rider_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Spectral_Master", "1.00"));
		Wind_Rider_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Shillien_Saint", "1.00"));
		Wind_Rider_vs_Dominator = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Dominator", "1.00"));
		Wind_Rider_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Wind_Rider_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Ghost_Hunter
	 */
	private static final void loadGhost_Hunter()
	{
		final ExProperties dmg = initProperties(Ghost_Hunter_FILE);
		Ghost_Hunter_vs_Duelist = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Duelist", "1.00"));
		Ghost_Hunter_vs_DreadNought = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_DreadNought", "1.00"));
		Ghost_Hunter_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Phoenix_Knight", "1.00"));
		Ghost_Hunter_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Hell_Knight", "1.00"));
		Ghost_Hunter_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Sagitarius", "1.00"));
		Ghost_Hunter_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Eva_Templar", "1.00"));
		Ghost_Hunter_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Sword_Muse", "1.00"));
		Ghost_Hunter_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Moonlight_Sentinel", "1.00"));
		Ghost_Hunter_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Shillien_Templar", "1.00"));
		Ghost_Hunter_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Spectral_Dancer", "1.00"));
		Ghost_Hunter_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Ghost_Sentinel", "1.00"));
		Ghost_Hunter_vs_Titan = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Titan", "1.00"));
		Ghost_Hunter_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Grand_Khauatari", "1.00"));
		Ghost_Hunter_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Fortune_Seeker", "1.00"));
		Ghost_Hunter_vs_Maestro = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Maestro", "1.00"));
		Ghost_Hunter_vs_Adventurer = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Adventurer", "1.00"));
		Ghost_Hunter_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Wind_Rider", "1.00"));
		Ghost_Hunter_vs_Archmage = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Archmage", "1.00"));
		Ghost_Hunter_vs_Soultaker = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Soultaker", "1.00"));
		Ghost_Hunter_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Arcana_Lord", "1.00"));
		Ghost_Hunter_vs_Cardinal = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Cardinal", "1.00"));
		Ghost_Hunter_vs_Hierophant = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Hierophant", "1.00"));
		Ghost_Hunter_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Mystic_Muse", "1.00"));
		Ghost_Hunter_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Elemental_Master", "1.00"));
		Ghost_Hunter_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Eva_s_Saint", "1.00"));
		Ghost_Hunter_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Storm_Screamer", "1.00"));
		Ghost_Hunter_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Spectral_Master", "1.00"));
		Ghost_Hunter_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Shillien_Saint", "1.00"));
		Ghost_Hunter_vs_Dominator = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Dominator", "1.00"));
		Ghost_Hunter_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Ghost_Hunter_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Archmage
	 */
	private static final void loadArchmage()
	{
		final ExProperties dmg = initProperties(Archmage_FILE);
		Archmage_vs_Duelist = Float.parseFloat(dmg.getProperty("Archmage_vs_Duelist", "1.00"));
		Archmage_vs_DreadNought = Float.parseFloat(dmg.getProperty("Archmage_vs_DreadNought", "1.00"));
		Archmage_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Archmage_vs_Phoenix_Knight", "1.00"));
		Archmage_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Archmage_vs_Hell_Knight", "1.00"));
		Archmage_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Archmage_vs_Sagitarius", "1.00"));
		Archmage_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Archmage_vs_Eva_Templar", "1.00"));
		Archmage_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Archmage_vs_Sword_Muse", "1.00"));
		Archmage_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Archmage_vs_Moonlight_Sentinel", "1.00"));
		Archmage_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Archmage_vs_Shillien_Templar", "1.00"));
		Archmage_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Archmage_vs_Spectral_Dancer", "1.00"));
		Archmage_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Archmage_vs_Ghost_Sentinel", "1.00"));
		Archmage_vs_Titan = Float.parseFloat(dmg.getProperty("Archmage_vs_Titan", "1.00"));
		Archmage_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Archmage_vs_Grand_Khauatari", "1.00"));
		Archmage_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Archmage_vs_Fortune_Seeker", "1.00"));
		Archmage_vs_Maestro = Float.parseFloat(dmg.getProperty("Archmage_vs_Maestro", "1.00"));
		Archmage_vs_Adventurer = Float.parseFloat(dmg.getProperty("Archmage_vs_Adventurer", "1.00"));
		Archmage_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Archmage_vs_Wind_Rider", "1.00"));
		Archmage_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Archmage_vs_Ghost_Hunter", "1.00"));
		Archmage_vs_Soultaker = Float.parseFloat(dmg.getProperty("Archmage_vs_Soultaker", "1.00"));
		Archmage_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Archmage_vs_Arcana_Lord", "1.00"));
		Archmage_vs_Cardinal = Float.parseFloat(dmg.getProperty("Archmage_vs_Cardinal", "1.00"));
		Archmage_vs_Hierophant = Float.parseFloat(dmg.getProperty("Archmage_vs_Hierophant", "1.00"));
		Archmage_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Archmage_vs_Mystic_Muse", "1.00"));
		Archmage_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Archmage_vs_Elemental_Master", "1.00"));
		Archmage_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Archmage_vs_Eva_s_Saint", "1.00"));
		Archmage_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Archmage_vs_Storm_Screamer", "1.00"));
		Archmage_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Archmage_vs_Spectral_Master", "1.00"));
		Archmage_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Archmage_vs_Shillien_Saint", "1.00"));
		Archmage_vs_Dominator = Float.parseFloat(dmg.getProperty("Archmage_vs_Dominator", "1.00"));
		Archmage_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Archmage_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Soultaker
	 */
	private static final void loadSoultaker()
	{
		final ExProperties dmg = initProperties(Soultaker_FILE);
		Soultaker_vs_Duelist = Float.parseFloat(dmg.getProperty("Soultaker_vs_Duelist", "1.00"));
		Soultaker_vs_DreadNought = Float.parseFloat(dmg.getProperty("Soultaker_vs_DreadNought", "1.00"));
		Soultaker_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Soultaker_vs_Phoenix_Knight", "1.00"));
		Soultaker_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Soultaker_vs_Hell_Knight", "1.00"));
		Soultaker_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Soultaker_vs_Sagitarius", "1.00"));
		Soultaker_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Soultaker_vs_Eva_Templar", "1.00"));
		Soultaker_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Soultaker_vs_Sword_Muse", "1.00"));
		Soultaker_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Soultaker_vs_Moonlight_Sentinel", "1.00"));
		Soultaker_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Soultaker_vs_Shillien_Templar", "1.00"));
		Soultaker_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Soultaker_vs_Spectral_Dancer", "1.00"));
		Soultaker_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Soultaker_vs_Ghost_Sentinel", "1.00"));
		Soultaker_vs_Titan = Float.parseFloat(dmg.getProperty("Soultaker_vs_Titan", "1.00"));
		Soultaker_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Soultaker_vs_Grand_Khauatari", "1.00"));
		Soultaker_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Soultaker_vs_Fortune_Seeker", "1.00"));
		Soultaker_vs_Maestro = Float.parseFloat(dmg.getProperty("Soultaker_vs_Maestro", "1.00"));
		Soultaker_vs_Adventurer = Float.parseFloat(dmg.getProperty("Soultaker_vs_Adventurer", "1.00"));
		Soultaker_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Soultaker_vs_Wind_Rider", "1.00"));
		Soultaker_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Soultaker_vs_Ghost_Hunter", "1.00"));
		Soultaker_vs_Archmage = Float.parseFloat(dmg.getProperty("Soultaker_vs_Archmage", "1.00"));
		Soultaker_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Soultaker_vs_Arcana_Lord", "1.00"));
		Soultaker_vs_Cardinal = Float.parseFloat(dmg.getProperty("Soultaker_vs_Cardinal", "1.00"));
		Soultaker_vs_Hierophant = Float.parseFloat(dmg.getProperty("Soultaker_vs_Hierophant", "1.00"));
		Soultaker_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Soultaker_vs_Mystic_Muse", "1.00"));
		Soultaker_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Soultaker_vs_Elemental_Master", "1.00"));
		Soultaker_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Soultaker_vs_Eva_s_Saint", "1.00"));
		Soultaker_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Soultaker_vs_Storm_Screamer", "1.00"));
		Soultaker_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Soultaker_vs_Spectral_Master", "1.00"));
		Soultaker_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Soultaker_vs_Shillien_Saint", "1.00"));
		Soultaker_vs_Dominator = Float.parseFloat(dmg.getProperty("Soultaker_vs_Dominator", "1.00"));
		Soultaker_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Soultaker_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Arcana_Lord
	 */
	private static final void loadArcana_Lord()
	{
		final ExProperties dmg = initProperties(Arcana_Lord_FILE);
		Arcana_Lord_vs_Duelist = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Duelist", "1.00"));
		Arcana_Lord_vs_DreadNought = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_DreadNought", "1.00"));
		Arcana_Lord_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Phoenix_Knight", "1.00"));
		Arcana_Lord_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Hell_Knight", "1.00"));
		Arcana_Lord_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Sagitarius", "1.00"));
		Arcana_Lord_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Eva_Templar", "1.00"));
		Arcana_Lord_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Sword_Muse", "1.00"));
		Arcana_Lord_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Moonlight_Sentinel", "1.00"));
		Arcana_Lord_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Shillien_Templar", "1.00"));
		Arcana_Lord_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Spectral_Dancer", "1.00"));
		Arcana_Lord_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Ghost_Sentinel", "1.00"));
		Arcana_Lord_vs_Titan = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Titan", "1.00"));
		Arcana_Lord_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Grand_Khauatari", "1.00"));
		Arcana_Lord_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Fortune_Seeker", "1.00"));
		Arcana_Lord_vs_Maestro = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Maestro", "1.00"));
		Arcana_Lord_vs_Adventurer = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Adventurer", "1.00"));
		Arcana_Lord_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Wind_Rider", "1.00"));
		Arcana_Lord_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Ghost_Hunter", "1.00"));
		Arcana_Lord_vs_Archmage = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Archmage", "1.00"));
		Arcana_Lord_vs_Soultaker = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Soultaker", "1.00"));
		Arcana_Lord_vs_Cardinal = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Cardinal", "1.00"));
		Arcana_Lord_vs_Hierophant = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Hierophant", "1.00"));
		Arcana_Lord_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Mystic_Muse", "1.00"));
		Arcana_Lord_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Elemental_Master", "1.00"));
		Arcana_Lord_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Eva_s_Saint", "1.00"));
		Arcana_Lord_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Storm_Screamer", "1.00"));
		Arcana_Lord_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Spectral_Master", "1.00"));
		Arcana_Lord_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Shillien_Saint", "1.00"));
		Arcana_Lord_vs_Dominator = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Dominator", "1.00"));
		Arcana_Lord_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Arcana_Lord_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Cardinal
	 */
	private static final void loadCardinal()
	{
		final ExProperties dmg = initProperties(Cardinal_FILE);
		Cardinal_vs_Duelist = Float.parseFloat(dmg.getProperty("Cardinal_vs_Duelist", "1.00"));
		Cardinal_vs_DreadNought = Float.parseFloat(dmg.getProperty("Cardinal_vs_DreadNought", "1.00"));
		Cardinal_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Cardinal_vs_Phoenix_Knight", "1.00"));
		Cardinal_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Cardinal_vs_Hell_Knight", "1.00"));
		Cardinal_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Cardinal_vs_Sagitarius", "1.00"));
		Cardinal_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Cardinal_vs_Eva_Templar", "1.00"));
		Cardinal_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Cardinal_vs_Sword_Muse", "1.00"));
		Cardinal_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Cardinal_vs_Moonlight_Sentinel", "1.00"));
		Cardinal_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Cardinal_vs_Shillien_Templar", "1.00"));
		Cardinal_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Cardinal_vs_Spectral_Dancer", "1.00"));
		Cardinal_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Cardinal_vs_Ghost_Sentinel", "1.00"));
		Cardinal_vs_Titan = Float.parseFloat(dmg.getProperty("Cardinal_vs_Titan", "1.00"));
		Cardinal_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Cardinal_vs_Grand_Khauatari", "1.00"));
		Cardinal_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Cardinal_vs_Fortune_Seeker", "1.00"));
		Cardinal_vs_Maestro = Float.parseFloat(dmg.getProperty("Cardinal_vs_Maestro", "1.00"));
		Cardinal_vs_Adventurer = Float.parseFloat(dmg.getProperty("Cardinal_vs_Adventurer", "1.00"));
		Cardinal_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Cardinal_vs_Wind_Rider", "1.00"));
		Cardinal_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Cardinal_vs_Ghost_Hunter", "1.00"));
		Cardinal_vs_Archmage = Float.parseFloat(dmg.getProperty("Cardinal_vs_Archmage", "1.00"));
		Cardinal_vs_Soultaker = Float.parseFloat(dmg.getProperty("Cardinal_vs_Soultaker", "1.00"));
		Cardinal_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Cardinal_vs_Arcana_Lord", "1.00"));
		Cardinal_vs_Hierophant = Float.parseFloat(dmg.getProperty("Cardinal_vs_Hierophant", "1.00"));
		Cardinal_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Cardinal_vs_Mystic_Muse", "1.00"));
		Cardinal_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Cardinal_vs_Elemental_Master", "1.00"));
		Cardinal_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Cardinal_vs_Eva_s_Saint", "1.00"));
		Cardinal_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Cardinal_vs_Storm_Screamer", "1.00"));
		Cardinal_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Cardinal_vs_Spectral_Master", "1.00"));
		Cardinal_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Cardinal_vs_Shillien_Saint", "1.00"));
		Cardinal_vs_Dominator = Float.parseFloat(dmg.getProperty("Cardinal_vs_Dominator", "1.00"));
		Cardinal_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Cardinal_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Hierophant
	 */
	private static final void loadHierophant()
	{
		final ExProperties dmg = initProperties(Hierophant_FILE);
		Hierophant_vs_Duelist = Float.parseFloat(dmg.getProperty("Hierophant_vs_Duelist", "1.00"));
		Hierophant_vs_DreadNought = Float.parseFloat(dmg.getProperty("Hierophant_vs_DreadNought", "1.00"));
		Hierophant_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Hierophant_vs_Phoenix_Knight", "1.00"));
		Hierophant_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Hierophant_vs_Hell_Knight", "1.00"));
		Hierophant_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Hierophant_vs_Sagitarius", "1.00"));
		Hierophant_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Hierophant_vs_Eva_Templar", "1.00"));
		Hierophant_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Hierophant_vs_Sword_Muse", "1.00"));
		Hierophant_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Hierophant_vs_Moonlight_Sentinel", "1.00"));
		Hierophant_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Hierophant_vs_Shillien_Templar", "1.00"));
		Hierophant_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Hierophant_vs_Spectral_Dancer", "1.00"));
		Hierophant_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Hierophant_vs_Ghost_Sentinel", "1.00"));
		Hierophant_vs_Titan = Float.parseFloat(dmg.getProperty("Hierophant_vs_Titan", "1.00"));
		Hierophant_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Hierophant_vs_Grand_Khauatari", "1.00"));
		Hierophant_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Hierophant_vs_Fortune_Seeker", "1.00"));
		Hierophant_vs_Maestro = Float.parseFloat(dmg.getProperty("Hierophant_vs_Maestro", "1.00"));
		Hierophant_vs_Adventurer = Float.parseFloat(dmg.getProperty("Hierophant_vs_Adventurer", "1.00"));
		Hierophant_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Hierophant_vs_Wind_Rider", "1.00"));
		Hierophant_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Hierophant_vs_Ghost_Hunter", "1.00"));
		Hierophant_vs_Archmage = Float.parseFloat(dmg.getProperty("Hierophant_vs_Archmage", "1.00"));
		Hierophant_vs_Soultaker = Float.parseFloat(dmg.getProperty("Hierophant_vs_Soultaker", "1.00"));
		Hierophant_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Hierophant_vs_Arcana_Lord", "1.00"));
		Hierophant_vs_Cardinal = Float.parseFloat(dmg.getProperty("Hierophant_vs_Cardinal", "1.00"));
		Hierophant_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Hierophant_vs_Mystic_Muse", "1.00"));
		Hierophant_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Hierophant_vs_Elemental_Master", "1.00"));
		Hierophant_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Hierophant_vs_Eva_s_Saint", "1.00"));
		Hierophant_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Hierophant_vs_Storm_Screamer", "1.00"));
		Hierophant_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Hierophant_vs_Spectral_Master", "1.00"));
		Hierophant_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Hierophant_vs_Shillien_Saint", "1.00"));
		Hierophant_vs_Dominator = Float.parseFloat(dmg.getProperty("Hierophant_vs_Dominator", "1.00"));
		Hierophant_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Hierophant_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Mystic_Muse
	 */
	private static final void loadMystic_Muse()
	{
		final ExProperties dmg = initProperties(Mystic_Muse_FILE);
		Mystic_Muse_vs_Duelist = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Duelist", "1.00"));
		Mystic_Muse_vs_DreadNought = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_DreadNought", "1.00"));
		Mystic_Muse_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Phoenix_Knight", "1.00"));
		Mystic_Muse_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Hell_Knight", "1.00"));
		Mystic_Muse_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Sagitarius", "1.00"));
		Mystic_Muse_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Eva_Templar", "1.00"));
		Mystic_Muse_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Sword_Muse", "1.00"));
		Mystic_Muse_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Moonlight_Sentinel", "1.00"));
		Mystic_Muse_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Shillien_Templar", "1.00"));
		Mystic_Muse_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Spectral_Dancer", "1.00"));
		Mystic_Muse_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Ghost_Sentinel", "1.00"));
		Mystic_Muse_vs_Titan = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Titan", "1.00"));
		Mystic_Muse_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Grand_Khauatari", "1.00"));
		Mystic_Muse_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Fortune_Seeker", "1.00"));
		Mystic_Muse_vs_Maestro = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Maestro", "1.00"));
		Mystic_Muse_vs_Adventurer = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Adventurer", "1.00"));
		Mystic_Muse_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Wind_Rider", "1.00"));
		Mystic_Muse_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Ghost_Hunter", "1.00"));
		Mystic_Muse_vs_Archmage = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Archmage", "1.00"));
		Mystic_Muse_vs_Soultaker = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Soultaker", "1.00"));
		Mystic_Muse_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Arcana_Lord", "1.00"));
		Mystic_Muse_vs_Cardinal = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Cardinal", "1.00"));
		Mystic_Muse_vs_Hierophant = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Hierophant", "1.00"));
		Mystic_Muse_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Elemental_Master", "1.00"));
		Mystic_Muse_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Eva_s_Saint", "1.00"));
		Mystic_Muse_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Storm_Screamer", "1.00"));
		Mystic_Muse_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Spectral_Master", "1.00"));
		Mystic_Muse_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Shillien_Saint", "1.00"));
		Mystic_Muse_vs_Dominator = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Dominator", "1.00"));
		Mystic_Muse_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Mystic_Muse_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Elemental_Master
	 */
	private static final void loadElemental_Master()
	{
		final ExProperties dmg = initProperties(Elemental_Master_FILE);
		Elemental_Master_vs_Duelist = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Duelist", "1.00"));
		Elemental_Master_vs_DreadNought = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_DreadNought", "1.00"));
		Elemental_Master_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Phoenix_Knight", "1.00"));
		Elemental_Master_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Hell_Knight", "1.00"));
		Elemental_Master_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Sagitarius", "1.00"));
		Elemental_Master_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Eva_Templar", "1.00"));
		Elemental_Master_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Sword_Muse", "1.00"));
		Elemental_Master_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Moonlight_Sentinel", "1.00"));
		Elemental_Master_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Shillien_Templar", "1.00"));
		Elemental_Master_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Spectral_Dancer", "1.00"));
		Elemental_Master_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Ghost_Sentinel", "1.00"));
		Elemental_Master_vs_Titan = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Titan", "1.00"));
		Elemental_Master_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Grand_Khauatari", "1.00"));
		Elemental_Master_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Fortune_Seeker", "1.00"));
		Elemental_Master_vs_Maestro = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Maestro", "1.00"));
		Elemental_Master_vs_Adventurer = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Adventurer", "1.00"));
		Elemental_Master_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Wind_Rider", "1.00"));
		Elemental_Master_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Ghost_Hunter", "1.00"));
		Elemental_Master_vs_Archmage = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Archmage", "1.00"));
		Elemental_Master_vs_Soultaker = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Soultaker", "1.00"));
		Elemental_Master_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Arcana_Lord", "1.00"));
		Elemental_Master_vs_Cardinal = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Cardinal", "1.00"));
		Elemental_Master_vs_Hierophant = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Hierophant", "1.00"));
		Elemental_Master_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Mystic_Muse", "1.00"));
		Elemental_Master_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Eva_s_Saint", "1.00"));
		Elemental_Master_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Storm_Screamer", "1.00"));
		Elemental_Master_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Spectral_Master", "1.00"));
		Elemental_Master_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Shillien_Saint", "1.00"));
		Elemental_Master_vs_Dominator = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Dominator", "1.00"));
		Elemental_Master_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Elemental_Master_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Eva_s_Saint
	 */
	private static final void loadEva_s_Saint()
	{
		final ExProperties dmg = initProperties(Eva_s_Saint_FILE);
		Eva_s_Saint_vs_Duelist = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Duelist", "1.00"));
		Eva_s_Saint_vs_DreadNought = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_DreadNought", "1.00"));
		Eva_s_Saint_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Phoenix_Knight", "1.00"));
		Eva_s_Saint_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Hell_Knight", "1.00"));
		Eva_s_Saint_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Sagitarius", "1.00"));
		Eva_s_Saint_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Eva_Templar", "1.00"));
		Eva_s_Saint_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Sword_Muse", "1.00"));
		Eva_s_Saint_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Moonlight_Sentinel", "1.00"));
		Eva_s_Saint_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Shillien_Templar", "1.00"));
		Eva_s_Saint_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Spectral_Dancer", "1.00"));
		Eva_s_Saint_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Ghost_Sentinel", "1.00"));
		Eva_s_Saint_vs_Titan = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Titan", "1.00"));
		Eva_s_Saint_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Grand_Khauatari", "1.00"));
		Eva_s_Saint_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Fortune_Seeker", "1.00"));
		Eva_s_Saint_vs_Maestro = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Maestro", "1.00"));
		Eva_s_Saint_vs_Adventurer = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Adventurer", "1.00"));
		Eva_s_Saint_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Wind_Rider", "1.00"));
		Eva_s_Saint_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Ghost_Hunter", "1.00"));
		Eva_s_Saint_vs_Archmage = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Archmage", "1.00"));
		Eva_s_Saint_vs_Soultaker = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Soultaker", "1.00"));
		Eva_s_Saint_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Arcana_Lord", "1.00"));
		Eva_s_Saint_vs_Cardinal = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Cardinal", "1.00"));
		Eva_s_Saint_vs_Hierophant = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Hierophant", "1.00"));
		Eva_s_Saint_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Mystic_Muse", "1.00"));
		Eva_s_Saint_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Elemental_Master", "1.00"));
		Eva_s_Saint_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Storm_Screamer", "1.00"));
		Eva_s_Saint_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Spectral_Master", "1.00"));
		Eva_s_Saint_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Shillien_Saint", "1.00"));
		Eva_s_Saint_vs_Dominator = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Dominator", "1.00"));
		Eva_s_Saint_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Eva_s_Saint_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Storm_Screamer
	 */
	private static final void loadStorm_Screamer()
	{
		final ExProperties dmg = initProperties(Storm_Screamer_FILE);
		Storm_Screamer_vs_Duelist = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Duelist", "1.00"));
		Storm_Screamer_vs_DreadNought = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_DreadNought", "1.00"));
		Storm_Screamer_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Phoenix_Knight", "1.00"));
		Storm_Screamer_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Hell_Knight", "1.00"));
		Storm_Screamer_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Sagitarius", "1.00"));
		Storm_Screamer_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Eva_Templar", "1.00"));
		Storm_Screamer_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Sword_Muse", "1.00"));
		Storm_Screamer_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Moonlight_Sentinel", "1.00"));
		Storm_Screamer_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Shillien_Templar", "1.00"));
		Storm_Screamer_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Spectral_Dancer", "1.00"));
		Storm_Screamer_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Ghost_Sentinel", "1.00"));
		Storm_Screamer_vs_Titan = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Titan", "1.00"));
		Storm_Screamer_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Grand_Khauatari", "1.00"));
		Storm_Screamer_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Fortune_Seeker", "1.00"));
		Storm_Screamer_vs_Maestro = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Maestro", "1.00"));
		Storm_Screamer_vs_Adventurer = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Adventurer", "1.00"));
		Storm_Screamer_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Wind_Rider", "1.00"));
		Storm_Screamer_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Ghost_Hunter", "1.00"));
		Storm_Screamer_vs_Archmage = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Archmage", "1.00"));
		Storm_Screamer_vs_Soultaker = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Soultaker", "1.00"));
		Storm_Screamer_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Arcana_Lord", "1.00"));
		Storm_Screamer_vs_Cardinal = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Cardinal", "1.00"));
		Storm_Screamer_vs_Hierophant = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Hierophant", "1.00"));
		Storm_Screamer_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Mystic_Muse", "1.00"));
		Storm_Screamer_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Elemental_Master", "1.00"));
		Storm_Screamer_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Eva_s_Saint", "1.00"));
		Storm_Screamer_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Spectral_Master", "1.00"));
		Storm_Screamer_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Shillien_Saint", "1.00"));
		Storm_Screamer_vs_Dominator = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Dominator", "1.00"));
		Storm_Screamer_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Storm_Screamer_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Spectral_Master
	 */
	private static final void loadSpectral_Master()
	{
		final ExProperties dmg = initProperties(Spectral_Master_FILE);
		Spectral_Master_vs_Duelist = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Duelist", "1.00"));
		Spectral_Master_vs_DreadNought = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_DreadNought", "1.00"));
		Spectral_Master_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Phoenix_Knight", "1.00"));
		Spectral_Master_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Hell_Knight", "1.00"));
		Spectral_Master_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Sagitarius", "1.00"));
		Spectral_Master_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Eva_Templar", "1.00"));
		Spectral_Master_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Sword_Muse", "1.00"));
		Spectral_Master_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Moonlight_Sentinel", "1.00"));
		Spectral_Master_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Shillien_Templar", "1.00"));
		Spectral_Master_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Spectral_Dancer", "1.00"));
		Spectral_Master_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Ghost_Sentinel", "1.00"));
		Spectral_Master_vs_Titan = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Titan", "1.00"));
		Spectral_Master_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Grand_Khauatari", "1.00"));
		Spectral_Master_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Fortune_Seeker", "1.00"));
		Spectral_Master_vs_Maestro = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Maestro", "1.00"));
		Spectral_Master_vs_Adventurer = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Adventurer", "1.00"));
		Spectral_Master_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Wind_Rider", "1.00"));
		Spectral_Master_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Ghost_Hunter", "1.00"));
		Spectral_Master_vs_Archmage = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Archmage", "1.00"));
		Spectral_Master_vs_Soultaker = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Soultaker", "1.00"));
		Spectral_Master_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Arcana_Lord", "1.00"));
		Spectral_Master_vs_Cardinal = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Cardinal", "1.00"));
		Spectral_Master_vs_Hierophant = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Hierophant", "1.00"));
		Spectral_Master_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Mystic_Muse", "1.00"));
		Spectral_Master_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Elemental_Master", "1.00"));
		Spectral_Master_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Eva_s_Saint", "1.00"));
		Spectral_Master_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Storm_Screamer", "1.00"));
		Spectral_Master_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Shillien_Saint", "1.00"));
		Spectral_Master_vs_Dominator = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Dominator", "1.00"));
		Spectral_Master_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Spectral_Master_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Shillien_Saint
	 */
	private static final void loadShillien_Saint()
	{
		final ExProperties dmg = initProperties(Shillien_Saint_FILE);
		Shillien_Saint_vs_Duelist = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Duelist", "1.00"));
		Shillien_Saint_vs_DreadNought = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_DreadNought", "1.00"));
		Shillien_Saint_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Phoenix_Knight", "1.00"));
		Shillien_Saint_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Hell_Knight", "1.00"));
		Shillien_Saint_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Sagitarius", "1.00"));
		Shillien_Saint_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Eva_Templar", "1.00"));
		Shillien_Saint_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Sword_Muse", "1.00"));
		Shillien_Saint_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Moonlight_Sentinel", "1.00"));
		Shillien_Saint_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Shillien_Templar", "1.00"));
		Shillien_Saint_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Spectral_Dancer", "1.00"));
		Shillien_Saint_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Ghost_Sentinel", "1.00"));
		Shillien_Saint_vs_Titan = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Titan", "1.00"));
		Shillien_Saint_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Grand_Khauatari", "1.00"));
		Shillien_Saint_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Fortune_Seeker", "1.00"));
		Shillien_Saint_vs_Maestro = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Maestro", "1.00"));
		Shillien_Saint_vs_Adventurer = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Adventurer", "1.00"));
		Shillien_Saint_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Wind_Rider", "1.00"));
		Shillien_Saint_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Ghost_Hunter", "1.00"));
		Shillien_Saint_vs_Archmage = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Archmage", "1.00"));
		Shillien_Saint_vs_Soultaker = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Soultaker", "1.00"));
		Shillien_Saint_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Arcana_Lord", "1.00"));
		Shillien_Saint_vs_Cardinal = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Cardinal", "1.00"));
		Shillien_Saint_vs_Hierophant = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Hierophant", "1.00"));
		Shillien_Saint_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Mystic_Muse", "1.00"));
		Shillien_Saint_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Elemental_Master", "1.00"));
		Shillien_Saint_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Eva_s_Saint", "1.00"));
		Shillien_Saint_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Storm_Screamer", "1.00"));
		Shillien_Saint_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Spectral_Master", "1.00"));
		Shillien_Saint_vs_Dominator = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Dominator", "1.00"));
		Shillien_Saint_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Shillien_Saint_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Dominator
	 */
	private static final void loadDominator()
	{
		final ExProperties dmg = initProperties(Dominator_FILE);
		Dominator_vs_Duelist = Float.parseFloat(dmg.getProperty("Dominator_vs_Duelist", "1.00"));
		Dominator_vs_DreadNought = Float.parseFloat(dmg.getProperty("Dominator_vs_DreadNought", "1.00"));
		Dominator_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Dominator_vs_Phoenix_Knight", "1.00"));
		Dominator_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Dominator_vs_Hell_Knight", "1.00"));
		Dominator_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Dominator_vs_Sagitarius", "1.00"));
		Dominator_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Dominator_vs_Eva_Templar", "1.00"));
		Dominator_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Dominator_vs_Sword_Muse", "1.00"));
		Dominator_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Dominator_vs_Moonlight_Sentinel", "1.00"));
		Dominator_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Dominator_vs_Shillien_Templar", "1.00"));
		Dominator_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Dominator_vs_Spectral_Dancer", "1.00"));
		Dominator_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Dominator_vs_Ghost_Sentinel", "1.00"));
		Dominator_vs_Titan = Float.parseFloat(dmg.getProperty("Dominator_vs_Titan", "1.00"));
		Dominator_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Dominator_vs_Grand_Khauatari", "1.00"));
		Dominator_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Dominator_vs_Fortune_Seeker", "1.00"));
		Dominator_vs_Maestro = Float.parseFloat(dmg.getProperty("Dominator_vs_Maestro", "1.00"));
		Dominator_vs_Adventurer = Float.parseFloat(dmg.getProperty("Dominator_vs_Adventurer", "1.00"));
		Dominator_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Dominator_vs_Wind_Rider", "1.00"));
		Dominator_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Dominator_vs_Ghost_Hunter", "1.00"));
		Dominator_vs_Archmage = Float.parseFloat(dmg.getProperty("Dominator_vs_Archmage", "1.00"));
		Dominator_vs_Soultaker = Float.parseFloat(dmg.getProperty("Dominator_vs_Soultaker", "1.00"));
		Dominator_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Dominator_vs_Arcana_Lord", "1.00"));
		Dominator_vs_Cardinal = Float.parseFloat(dmg.getProperty("Dominator_vs_Cardinal", "1.00"));
		Dominator_vs_Hierophant = Float.parseFloat(dmg.getProperty("Dominator_vs_Hierophant", "1.00"));
		Dominator_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Dominator_vs_Mystic_Muse", "1.00"));
		Dominator_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Dominator_vs_Elemental_Master", "1.00"));
		Dominator_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Dominator_vs_Eva_s_Saint", "1.00"));
		Dominator_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Dominator_vs_Storm_Screamer", "1.00"));
		Dominator_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Dominator_vs_Spectral_Master", "1.00"));
		Dominator_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Dominator_vs_Shillien_Saint", "1.00"));
		Dominator_vs_Doomcryer = Float.parseFloat(dmg.getProperty("Dominator_vs_Doomcryer", "1.00"));
	}
	
	/**
	 * load class Doomcryer
	 */
	private static final void loadDoomcryer()
	{
		final ExProperties dmg = initProperties(Doomcryer_FILE);
		Doomcryer_vs_Duelist = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Duelist", "1.00"));
		Doomcryer_vs_DreadNought = Float.parseFloat(dmg.getProperty("Doomcryer_vs_DreadNought", "1.00"));
		Doomcryer_vs_Phoenix_Knight = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Phoenix_Knight", "1.00"));
		Doomcryer_vs_Hell_Knight = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Hell_Knight", "1.00"));
		Doomcryer_vs_Sagitarius = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Sagitarius", "1.00"));
		Doomcryer_vs_Eva_Templar = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Eva_Templar", "1.00"));
		Doomcryer_vs_Sword_Muse = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Sword_Muse", "1.00"));
		Doomcryer_vs_Moonlight_Sentinel = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Moonlight_Sentinel", "1.00"));
		Doomcryer_vs_Shillien_Templar = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Shillien_Templar", "1.00"));
		Doomcryer_vs_Spectral_Dancer = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Spectral_Dancer", "1.00"));
		Doomcryer_vs_Ghost_Sentinel = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Ghost_Sentinel", "1.00"));
		Doomcryer_vs_Titan = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Titan", "1.00"));
		Doomcryer_vs_Grand_Khauatari = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Grand_Khauatari", "1.00"));
		Doomcryer_vs_Fortune_Seeker = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Fortune_Seeker", "1.00"));
		Doomcryer_vs_Maestro = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Maestro", "1.00"));
		Doomcryer_vs_Adventurer = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Adventurer", "1.00"));
		Doomcryer_vs_Wind_Rider = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Wind_Rider", "1.00"));
		Doomcryer_vs_Ghost_Hunter = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Ghost_Hunter", "1.00"));
		Doomcryer_vs_Archmage = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Archmage", "1.00"));
		Doomcryer_vs_Soultaker = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Soultaker", "1.00"));
		Doomcryer_vs_Arcana_Lord = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Arcana_Lord", "1.00"));
		Doomcryer_vs_Cardinal = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Cardinal", "1.00"));
		Doomcryer_vs_Hierophant = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Hierophant", "1.00"));
		Doomcryer_vs_Mystic_Muse = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Mystic_Muse", "1.00"));
		Doomcryer_vs_Elemental_Master = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Elemental_Master", "1.00"));
		Doomcryer_vs_Eva_s_Saint = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Eva_s_Saint", "1.00"));
		Doomcryer_vs_Storm_Screamer = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Storm_Screamer", "1.00"));
		Doomcryer_vs_Spectral_Master = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Spectral_Master", "1.00"));
		Doomcryer_vs_Shillien_Saint = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Shillien_Saint", "1.00"));
		Doomcryer_vs_Dominator = Float.parseFloat(dmg.getProperty("Doomcryer_vs_Dominator", "1.00"));
	}
	
	/**
	 * load mission
	 */
	private static final void loadmission()
	{
		final ExProperties Mission = initProperties(MISSION_FILE);
		
		PC_1x1 = Integer.parseInt(Mission.getProperty("Pc_Ammount_1x1", "1"));
		PC_PVP = Integer.parseInt(Mission.getProperty("Pc_Ammount_PvP", "1"));
		
		ACTIVE_MISSION_PVP = Boolean.parseBoolean(Mission.getProperty("ActivePvPMission", "false"));
		MISSION_PVP_CONT = Integer.parseInt(Mission.getProperty("PvPCont", "1"));
		MISSION_PVP_REWARD_ID = Integer.parseInt(Mission.getProperty("PvPReward", "6392"));
		MISSION_PVP_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("PvPRewardAmmount", "1"));
		
		ID_RAID = Integer.parseInt(Mission.getProperty("RaidID", "0"));
		PC_PARTYZONE = Integer.parseInt(Mission.getProperty("Pc_Ammount_PartyZone", "1"));
		
		PC_MOBS = Integer.parseInt(Mission.getProperty("Pc_Ammount_Mobs", "1"));
		PC_2x2 = Integer.parseInt(Mission.getProperty("Pc_Ammount_2x2", "1"));
		PC_5x5 = Integer.parseInt(Mission.getProperty("Pc_Ammount_5x5", "1"));
		PC_9x9 = Integer.parseInt(Mission.getProperty("Pc_Ammount_9x9", "1"));
		PC_RAID_IND = Integer.parseInt(Mission.getProperty("Pc_Ammount_Raid", "1"));
		PC_TVT = Integer.parseInt(Mission.getProperty("Pc_Ammount_TvT", "1"));
		
		
		REWARD_PCPOINT = Boolean.parseBoolean(Mission.getProperty("Reward_PcBang", "false"));
		
		ACTIVE_MISSION = Boolean.parseBoolean(Mission.getProperty("ActiveMission", "false"));
		ACTIVE_MISSION_TVT = Boolean.parseBoolean(Mission.getProperty("ActiveTvTMission", "false"));
		MISSION_TVT_CONT = Integer.parseInt(Mission.getProperty("TvTCont", "1"));
		MISSION_TVT_REWARD_ID = Integer.parseInt(Mission.getProperty("TvTReward", "6392"));
		MISSION_TVT_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("TvTRewardAmmount", "1"));
		
		ACTIVE_MISSION_RAID = Boolean.parseBoolean(Mission.getProperty("ActiveRaidMission", "false"));
		MISSION_RAID_CONT = Integer.parseInt(Mission.getProperty("RaidCont", "1"));
		MISSION_RAID_REWARD_ID = Integer.parseInt(Mission.getProperty("RaidReward", "6392"));
		MISSION_RAID_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("RaidRewardAmmount", "1"));
		
		ACTIVE_MISSION_MOB = Boolean.parseBoolean(Mission.getProperty("ActiveMobMission", "false"));
		MISSION_MOB_CONT = Integer.parseInt(Mission.getProperty("MobCont", "1"));
		
		ACTIVE_MISSION_PARTY_MOB = Boolean.parseBoolean(Mission.getProperty("ActivePartyMobMission", "false"));
		MISSION_PARTY_MOB_CONT = Integer.parseInt(Mission.getProperty("PartyMobCont", "1"));
		MISSION_PARTY_MOB_REWARD_ID = Integer.parseInt(Mission.getProperty("PartyMobReward", "6392"));
		MISSION_PARTY_MOB_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("PartyMobRewardAmmount", "1"));
		
		MISSION_LIST_MOBS = Mission.getProperty("ListMobs", "0");
		MISSION_LIST_MONSTER = new ArrayList<>();
		for (String id : MISSION_LIST_MOBS.split(","))
			MISSION_LIST_MONSTER.add(Integer.valueOf(Integer.parseInt(id))); 
		MISSION_MOB_REWARD_ID = Integer.parseInt(Mission.getProperty("MobReward", "6392"));
		MISSION_MOB_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("MobRewardAmmount", "1"));
		
		ACTIVE_MISSION_1X1 = Boolean.parseBoolean(Mission.getProperty("Active1x1Mission", "false"));
		MISSION_1X1_CONT = Integer.parseInt(Mission.getProperty("1x1Cont", "1"));
		MISSION_1X1_REWARD_ID = Integer.parseInt(Mission.getProperty("1x1Reward", "6392"));
		MISSION_1X1_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("1x1RewardAmmount", "1"));
		
		ACTIVE_MISSION_2X2 = Boolean.parseBoolean(Mission.getProperty("Active2x2Mission", "false"));
		MISSION_2X2_CONT = Integer.parseInt(Mission.getProperty("2x2Cont", "1"));
		MISSION_2X2_REWARD_ID = Integer.parseInt(Mission.getProperty("2x2Reward", "6392"));
		MISSION_2X2_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("2x2RewardAmmount", "1"));
		
		ACTIVE_MISSION_5X5 = Boolean.parseBoolean(Mission.getProperty("Active5x5Mission", "false"));
		MISSION_5X5_CONT = Integer.parseInt(Mission.getProperty("5x5Cont", "1"));
		MISSION_5X5_REWARD_ID = Integer.parseInt(Mission.getProperty("5x5Reward", "6392"));
		MISSION_5X5_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("5x5RewardAmmount", "1"));
		
		ACTIVE_MISSION_9X9 = Boolean.parseBoolean(Mission.getProperty("Active9x9Mission", "false"));
		MISSION_9X9_CONT = Integer.parseInt(Mission.getProperty("9x9Cont", "1"));
		MISSION_9X9_REWARD_ID = Integer.parseInt(Mission.getProperty("9x9Reward", "6392"));
		MISSION_9X9_REWARD_AMOUNT = Integer.parseInt(Mission.getProperty("9x9RewardAmmount", "1"));
		
		CLEAR_MISSION_INTERVAL_BY_TIME_OF_DAY = Mission.getProperty("ClearMissionStartTime", "20:00").split(",");
		
	}
	
	/**
	 * Load PC BANG.
	 */
	private static final void loadpcbp()
	{
		final ExProperties pcbpSettings = initProperties(Config.PC_BANG_FILE);
		PCB_ENABLE = Boolean.parseBoolean(pcbpSettings.getProperty("PcBangPointEnable", "true"));
		PCB_MIN_LEVEL = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMinLevel", "20"));
		PCB_POINT_MIN = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMinCount", "20"));
		PCB_POINT_MAX = Integer.parseInt(pcbpSettings.getProperty("PcBangPointMaxCount", "1000000"));
		
		if (PCB_POINT_MAX < 1)
		{
			PCB_POINT_MAX = Integer.MAX_VALUE;
		}
		
		PCB_CHANCE_DUAL_POINT = Integer.parseInt(pcbpSettings.getProperty("PcBangPointDualChance", "20"));
		PCB_INTERVAL = Integer.parseInt(pcbpSettings.getProperty("PcBangPointTimeStamp", "900"));
	}
	
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final ExProperties hexid = initProperties(HEXID_FILE);
		SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
		HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			Properties hexSetting = new Properties();
			File file = new File(filename);
			file.createNewFile();
			
			OutputStream out = new FileOutputStream(file);
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			hexSetting.store(out, "the hexID to auth into login");
			out.close();
		}
		catch (Exception e)
		{
			_log.warning("Config: Failed to save hex ID to \"" + filename + "\" file.");
			e.printStackTrace();
		}
	}
	
	protected static final ArrayList<Calendar> ParseDates(String datesString, int randMax) {
		ArrayList<Calendar> tempList = new ArrayList<>();
		String[] dates = datesString.split(";");
		Calendar today = Calendar.getInstance();
		for (String dateItem : dates) {
			String[] tokens = dateItem.split(",");
			Calendar cal = Calendar.getInstance();
			cal.set(7, GetDayFromString(tokens[0]));
			String[] timeTokens = tokens[1].split(":");
			cal.set(11, Integer.parseInt(timeTokens[0]));
			cal.set(12, Integer.parseInt(timeTokens[1]));
			cal.set(13, 0);
			cal.add(12, (new Random()).nextInt(randMax));
			if (cal.getTimeInMillis() - today.getTimeInMillis() < 0L)
				cal.add(5, 7); 
			tempList.add(cal);
		} 
		return tempList;
	}
	
	public static int GetDayFromString(String day) {
		day = day.toLowerCase();
		int dayVal = 2;
		switch (day) {
			case "monday":
				dayVal = 2;
				break;
			case "tuesday":
				dayVal = 3;
				break;
			case "wednesday":
				dayVal = 4;
				break;
			case "thursday":
				dayVal = 5;
				break;
			case "friday":
				dayVal = 6;
				break;
			case "saturday":
				dayVal = 7;
				break;
			case "sunday":
				dayVal = 1;
				break;
		} 
		return dayVal;
	}
	
	public static Calendar FindNext(ArrayList<Calendar> times) {
		Calendar today = Calendar.getInstance();
		ArrayList<Calendar> tempTimes = new ArrayList<>();
		for (Calendar time : times) {
			if (time.getTimeInMillis() - today.getTimeInMillis() < 0L)
				time.add(5, 7); 
			tempTimes.add(time);
		} 
		if (tempTimes.size() == 0)
			return null; 
		Calendar nextTime = tempTimes.get(0);
		for (Calendar t : tempTimes) {
			if (t.getTimeInMillis() - today.getTimeInMillis() < nextTime.getTimeInMillis() - today.getTimeInMillis())
				nextTime = t; 
		} 
		return nextTime;
	}
	
	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, rates, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(NETWORK_FILE);
		
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		PORT_GAME = server.getProperty("GameserverPort", 7777);
		
		EXTERNAL_HOSTNAME = server.getProperty("ExternalHostname", "*");
		INTERNAL_HOSTNAME = server.getProperty("InternalHostname", "*");
		
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHost", "127.0.0.1");
		
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);
		
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		
		MIN_PROTOCOL_REVISION = server.getProperty("MinProtocolRevision", 730);
		MAX_PROTOCOL_REVISION = server.getProperty("MaxProtocolRevision", 746);
		if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
			throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in gameserver.properties.");
		
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	private static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGIN_CONFIGURATION_FILE);
		
		EXTERNAL_HOSTNAME = server.getProperty("ExternalHostname", "*");
		INTERNAL_HOSTNAME = server.getProperty("InternalHostname", "*");
		
		LOGIN_BIND_ADDRESS = server.getProperty("LoginserverHostname", "*");
		PORT_LOGIN = server.getProperty("LoginserverPort", 2106);
		
		GAME_SERVER_LOGIN_HOST = server.getProperty("LoginHostname", "*");
		GAME_SERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
		
		SHOW_LICENCE = server.getProperty("ShowLicence", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mysql://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);
		
		LOG_LOGIN_CONTROLLER = server.getProperty("LogLoginController", false);
		
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
		
		DEBUG = server.getProperty("Debug", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		IP_PROTECTION_CNFG = server.getProperty("IpProtection_Config", false);
		IP_PROTECTION = server.getProperty("IpProtection", "*");
		BRUT_PROTECTION_ENABLED = Boolean.parseBoolean(server.getProperty("BrutProtection", "true"));
		
		ANTI_LOGIN_FLOOD = server.getProperty("Anti_login_flood", false);
		LOGIN_FLOOD_BLOCK_TIME = server.getProperty("Login_Flood_Block_Time", 600);
		
	}
	
	public static final void loadGameServer()
	{
		
		_log.info("Loading gameserver configuration files.");
		
		// boss settings
		loadBoss();
		
		// clans settings
		loadClans();
		
		// enchant settings
		loadEnchant();
		
		// events settings
		loadEvents();
		
		// geoengine settings
		loadGeoengine();
		
		// gm settings
		loadGmFile();
		
		// NPCs/monsters settings
		loadNpcs();
		
		// Options settings
		loadOptions();
		
		// Party settings
		loadParty();
		
		// players settings
		loadPlayers();
		
		// pvp settings
		loadPvP();
		
		// rates settings
		loadRates();
		
		// siege settings
		loadSieges();
		
		// skills settings
		loadSkills();
		
		// L2jMega settings
		loadL2JMega();
		
		// Physics settings
		loadPhysics();
		loadOlympiadPhysics();
		loadBalanceSkills();
		
		// TvT Settings
		loadTvT();
		
		// CTF Settings
		loadCTF();
		
		// PvP Settings
		loadPvpEvent();
		
		// Tournament Settings
		load_Tournament();
		
		// PartyZone Settings
		load_PartyZone();
		
		// Phantom Settings
		load_Phantom();
		
		// PvpZone Settings
		load_PvpZone();
		
		// Protect Settings
		loadProtect();
		
		loadpcbp();
		
		loadmission();
		
		loadPvpKing();
		
		loadDuelist();
		loadDreadNought();
		loadPhoenix_Knight();
		loadHell_Knight();
		loadSagittarius();
		loadEva_Templar();
		loadSword_Muse();
		loadMoonlight_Sentinel();
		loadShillien_Templar();
		loadSpectral_Dancer();
		loadGhost_Sentinel();
		loadTitan();
		loadGrand_Khauatari();
		loadFortune_Seeker();
		loadMaestro();
		loadAdventurer();
		loadWind_Rider();
		loadGhost_Hunter();
		loadArchmage();
		loadSoultaker();
		loadArcana_Lord();
		loadCardinal();
		loadHierophant();
		loadMystic_Muse();
		loadElemental_Master();
		loadEva_s_Saint();
		loadStorm_Screamer();
		loadSpectral_Master();
		loadShillien_Saint();
		loadDominator();
		loadDoomcryer();
		
		// hexID
		loadHexID();
		
		// server settings
		loadServer();
		
	}
	
	public static final void loadLoginServer()
	{
		_log.info("Loading loginserver configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		_log.info("Loading account manager configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		_log.info("Loading gameserver registration configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGeodataConverter()
	{
		_log.info("Loading geodata converter configuration files.");
		
		// geoengine settings
		loadGeoengine();
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		public ClassMasterSettings(String configLine)
		{
			_allowedClassChange = new HashMap<>(3);
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				// Get allowed class change.
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				// Parse items needed for class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				// Feed the map, and clean the list.
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				// Parse gifts after class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
}