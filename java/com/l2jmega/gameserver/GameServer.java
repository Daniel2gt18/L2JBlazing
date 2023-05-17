package com.l2jmega.gameserver;



import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.L2jMega;
import com.l2jmega.events.ArenaTask;
import com.l2jmega.events.PartyFarm;
import com.l2jmega.events.manager.CTFEventManager;
import com.l2jmega.events.manager.TvTEventManager;
import com.l2jmega.events.pvpevent.PvPEventManager;
import com.l2jmega.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.l2jmega.gameserver.data.BufferTable;
import com.l2jmega.gameserver.data.CharTemplateTable;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.IconTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.MapRegionTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.data.cache.CrestCache;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.data.manager.BuyListManager;
import com.l2jmega.gameserver.data.sql.BookmarkTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.data.sql.ServerMemoTable;
import com.l2jmega.gameserver.data.xml.AdminData;
import com.l2jmega.gameserver.data.xml.AnnouncementData;
import com.l2jmega.gameserver.data.xml.ArmorSetData;
import com.l2jmega.gameserver.data.xml.AugmentationData;
import com.l2jmega.gameserver.data.xml.DressMeData;
import com.l2jmega.gameserver.data.xml.FishData;
import com.l2jmega.gameserver.data.xml.HennaData;
import com.l2jmega.gameserver.data.xml.HerbDropData;
import com.l2jmega.gameserver.data.xml.MultisellData;
import com.l2jmega.gameserver.data.xml.NewbieBuffData;
import com.l2jmega.gameserver.data.xml.PkColorTable;
import com.l2jmega.gameserver.data.xml.PvpColorTable;
import com.l2jmega.gameserver.data.xml.SoulCrystalData;
import com.l2jmega.gameserver.data.xml.SpellbookData;
import com.l2jmega.gameserver.data.xml.StaticObjectData;
import com.l2jmega.gameserver.data.xml.SummonItemData;
import com.l2jmega.gameserver.data.xml.TeleportLocationData;
import com.l2jmega.gameserver.data.xml.WalkerRouteData;
import com.l2jmega.gameserver.geoengine.GeoEngine;
import com.l2jmega.gameserver.handler.AdminCommandHandler;
import com.l2jmega.gameserver.handler.ChatHandler;
import com.l2jmega.gameserver.handler.ItemHandler;
import com.l2jmega.gameserver.handler.SkillHandler;
import com.l2jmega.gameserver.handler.UserCommandHandler;
import com.l2jmega.gameserver.handler.VoicedCommandHandler;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.instancemanager.AuctionManager;
import com.l2jmega.gameserver.instancemanager.AutoSpawnManager;
import com.l2jmega.gameserver.instancemanager.BoatManager;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.CastleManorManager;
import com.l2jmega.gameserver.instancemanager.CharacterKillingManager;
import com.l2jmega.gameserver.instancemanager.ChatBanManager;
import com.l2jmega.gameserver.instancemanager.ChatGlobalManager;
import com.l2jmega.gameserver.instancemanager.ChatHeroManager;
import com.l2jmega.gameserver.instancemanager.CheckManager;
import com.l2jmega.gameserver.instancemanager.ClanHallManager;
import com.l2jmega.gameserver.instancemanager.CoupleManager;
import com.l2jmega.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jmega.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jmega.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jmega.gameserver.instancemanager.FishingChampionshipManager;
import com.l2jmega.gameserver.instancemanager.FourSepulchersManager;
import com.l2jmega.gameserver.instancemanager.GrandBossManager;
import com.l2jmega.gameserver.instancemanager.HeroManager;
import com.l2jmega.gameserver.instancemanager.IPManager;
import com.l2jmega.gameserver.instancemanager.MovieMakerManager;
import com.l2jmega.gameserver.instancemanager.OlyClassDamageManager;
import com.l2jmega.gameserver.instancemanager.PartyZoneManager;
import com.l2jmega.gameserver.instancemanager.PetitionManager;
import com.l2jmega.gameserver.instancemanager.RaidBossInfoManager;
import com.l2jmega.gameserver.instancemanager.RaidBossPointsManager;
import com.l2jmega.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jmega.gameserver.instancemanager.RaidZoneManager;
import com.l2jmega.gameserver.instancemanager.SevenSigns;
import com.l2jmega.gameserver.instancemanager.SevenSignsFestival;
import com.l2jmega.gameserver.instancemanager.SoloZoneManager;
import com.l2jmega.gameserver.instancemanager.VipManager;
import com.l2jmega.gameserver.instancemanager.VoteZone;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.instancemanager.games.MonsterRace;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.entity.Hero;
import com.l2jmega.gameserver.model.olympiad.Olympiad;
import com.l2jmega.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.model.partymatching.PartyMatchWaitingList;
import com.l2jmega.gameserver.model.vehicles.BoatGiranTalking;
import com.l2jmega.gameserver.model.vehicles.BoatGludinRune;
import com.l2jmega.gameserver.model.vehicles.BoatInnadrilTour;
import com.l2jmega.gameserver.model.vehicles.BoatRunePrimeval;
import com.l2jmega.gameserver.model.vehicles.BoatTalkingGludin;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GamePacketHandler;
import com.l2jmega.gameserver.scripting.ScriptManager;
import com.l2jmega.gameserver.taskmanager.AttackStanceTaskManager;
import com.l2jmega.gameserver.taskmanager.DecayTaskManager;
import com.l2jmega.gameserver.taskmanager.GameTimeTaskManager;
import com.l2jmega.gameserver.taskmanager.ItemsOnGroundTaskManager;
import com.l2jmega.gameserver.taskmanager.MovementTaskManager;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;
import com.l2jmega.gameserver.taskmanager.RandomAnimationTaskManager;
import com.l2jmega.gameserver.taskmanager.ShadowItemTaskManager;
import com.l2jmega.gameserver.taskmanager.WaterTaskManager;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;
import com.l2jmega.util.DeadLockDetector;
import com.l2jmega.util.IPv4Filter;

import hwid.Hwid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.LogManager;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.logging.CLogger;
import com.l2jmega.commons.mmocore.SelectorConfig;
import com.l2jmega.commons.mmocore.SelectorThread;
import com.l2jmega.commons.util.SysUtil;

import phantom.PhantomNameManager;
import phantom.PhantomTitleManager;
import phantom.Phantom_Farm;
import phantom.Phantom_Town;

public class GameServer
{
	private static final CLogger LOGGER = new CLogger(GameServer.class.getName());
	
	private final SelectorThread<L2GameClient> _selectorThread;
	
	private static GameServer _gameServer;
	
	public static void main(String[] args) throws Exception
	{
		_gameServer = new GameServer();
	}
	
	public GameServer() throws Exception
	{
		final long serverLoadStart = System.currentTimeMillis();
		
		// Create log folder
		new File("./log").mkdir();
		new File("./log/chat").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		new File("./log/gmaudit").mkdir();
		new File("./log/item").mkdir();
		new File("./data/crests").mkdirs();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File(Config.LOGGING)))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		StringUtil.printSection("Project 2022");
		L2jMega.info();
		StringUtil.printSection("Base aCis");

		// Initialize config
		Config.loadGameServer();
		
		// Factories
		XMLDocumentFactory.getInstance();
		L2DatabaseFactory.getInstance();
		
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("World");
		GameTimeController.getInstance();
		World.getInstance();
		MapRegionTable.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		
		StringUtil.printSection("Items");
		ItemTable.getInstance();
		SummonItemData.getInstance();
		HennaData.getInstance();
		BuyListManager.getInstance();
		MultisellData.getInstance();
		RecipeTable.getInstance();
		ArmorSetData.getInstance();
		FishData.getInstance();
		SpellbookData.getInstance();
		SoulCrystalData.getInstance();
		AugmentationData.getInstance();
		CursedWeaponsManager.getInstance();
		IconTable.getInstance();
		// Siege Reward Manager - Seth

		StringUtil.printSection("Admins");
		AdminData.getInstance();
		BookmarkTable.getInstance();
		MovieMakerManager.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		CharTemplateTable.getInstance();
		PlayerNameTable.getInstance();
		NewbieBuffData.getInstance();
		TeleportLocationData.getInstance();
		HtmCache.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		RaidBossPointsManager.getInstance();
		
		StringUtil.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			LOGGER.info("Community server is disabled.");
		
		StringUtil.printSection("Clans");
		CrestCache.getInstance();
		ClanTable.getInstance();
		AuctionManager.getInstance();
		ClanHallManager.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		MovementTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		CheckManager.getInstance();
		ChatBanManager.getInstance();
		CheckManager.getInstance();
		HeroManager.getInstance();
		VipManager.getInstance();
		AioManager.getInstance();
		ChatGlobalManager.getInstance();
		ChatHeroManager.getInstance();
		
		StringUtil.printSection("Castles");
		CastleManager.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		
		StringUtil.printSection("Manor Manager");
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		RaidBossInfoManager.getInstance();
		BufferTable.getInstance();
		HerbDropData.getInstance();
		NpcTable.getInstance();
		WalkerRouteData.getInstance();
		DoorTable.getInstance().spawn();
		StaticObjectData.getInstance();
		SpawnTable.getInstance();
		RaidBossSpawnManager.getInstance();
		GrandBossManager.getInstance();
		DayNightSpawnManager.getInstance();
		DimensionalRiftManager.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		Hero.getInstance();
		
		StringUtil.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance().init();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptManager.getInstance();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		
		StringUtil.printSection("Events");
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance();
		
		MonsterRace.getInstance();
		
		if (Config.PCB_ENABLE)
			ThreadPool.scheduleAtFixedRate(PcPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		
		if (Config.TVT_EVENT_ENABLED)
		{
			TvTEventManager.getInstance().startTvTEventRegistration();
			TvTEventManager.getInstance().StartCalculationOfNextEventTime();
			
			if (Config.TVT_EVENT_ENABLED)
				LOGGER.info("TVT Event is enabled.");
		}
		else
			LOGGER.info("TvT Event is disabled.");
		
		if (Config.CTF_EVENT_ENABLED)
		{
			CTFEventManager.getInstance().startCTFEventRegistration();
			CTFEventManager.getInstance().StartCalculationOfNextCtfEventTime();
			if (Config.CTF_EVENT_ENABLED)
				LOGGER.info("CTF Event is enabled.");
		}
		else
			LOGGER.info("CTF Event is disabled.");
		
		if (Config.START_AUTO_PARTY)
		{
			LOGGER.info(""+Config.NAME_EVENT+" Auto Event is enabled.");
			PartyFarmEvent.getInstance().StartCalculationOfNextEventTime();
		}
		
	    if (Config.ACTIVE_MISSION) {
	        MissionReset.getInstance().StartNextEventTime();
	      } else {
	    	LOGGER.info("Mission Reset: desativado...");
	      }
	    
		if (Config.TOURNAMENT_EVENT_TIME)
		{
			LOGGER.info("Tournament Event is enabled.");
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else if (Config.TOURNAMENT_EVENT_START)
		{
			LOGGER.info("Tournament Event is enabled.");
			ArenaTask.spawnNpc1();
			ArenaTask.spawnNpc2();
		}
		else
			LOGGER.info("Tournament Event is disabled");
		
		if (Config.CKM_ENABLED)
			CharacterKillingManager.getInstance().init();
		
		if (Config.START_PARTY)
		{
			LOGGER.info("Start Spawn "+Config.NAME_EVENT+": Enabled");
			ThreadPool.schedule(new SpawnMonsters(), 3000);
		}
		
		StringUtil.printSection("Vote Zone");
		
		VoteZone.getInstance();
		PvPEventManager.getInstance();
	    
	    if (Config.VOTE_PVPZONE_ENABLED) {
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
	          PreparedStatement statement = con.prepareStatement("TRUNCATE votezone_hwid");
	          statement.execute();
	          statement.close();
	        } catch (SQLException e) {
	        	LOGGER.warn("VoteZone --> " + e);
	        } 
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
	          PreparedStatement statement = con.prepareStatement("TRUNCATE ranking_votezone");
	          statement.execute();
	          statement.close();
	        } catch (SQLException e) {
	        	LOGGER.warn("VoteZone --> " + e);
	        } 
	        NewZoneVote.getInstance().StartNextEventTime();
	      } else {
	    	  LOGGER.info("VoteZone: desativado...");
	      } 
	    
		StringUtil.printSection("Others");
		
		if (Config.ENABLE_PVP_COLOR)
			PvpColorTable.getInstance();
		
		if (Config.ENABLE_PK_COLOR)
			PkColorTable.getInstance();
		
		if (Config.RESET_DAILY_ENABLED)
			ResetDaily.getInstance().StartReset();
		else
			LOGGER.info("Reset Daily: desativado.");
		
		if (Config.RESTART_BY_TIME_OF_DAY)
			Restart.getInstance().StartCalculationOfNextRestartTime();
		else
			LOGGER.info("Restart System: disabled...");
		
		StringUtil.printSection("Balance System");
		OlyClassDamageManager.loadConfig();
		BalanceLoad.LoadEm();
		
		StringUtil.printSection("Protection");
		PartyZoneManager.getInstance();
		RaidZoneManager.getInstance();
		SoloZoneManager.getInstance();
	    IPManager.getInstance();
		
		StringUtil.printSection("Phantom Players");
		PhantomNameManager.INSTANCE.initialise();
		PhantomTitleManager.INSTANCE.initialise();
	    
	    if (Config.ALLOW_PHANTOM_PLAYERS) {
	        Phantom_Town.init();
	      } else {
	    	LOGGER.info("Town Phantom: desativado...");
	      }
	    
	    if (Config.ALLOW_PHANTOM_PLAYERS_FARM) {
	        Phantom_Farm.init();
	      } else {
	    	LOGGER.info("Phantom Farm: desativado...");
	      }
		StringUtil.printSection("DressMe Manager");
	    DressMeData.getInstance();
	    
		StringUtil.printSection("Hwid Manager");
		Hwid.Init();
		
		StringUtil.printSection("Handlers");
		LOGGER.info("AutoSpawnHandler: Loaded {} handlers.", AutoSpawnManager.getInstance().size());
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} voiced command handlers.", +VoicedCommandHandler.getInstance().size());
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		System.gc();
		
		LOGGER.info("Gameserver has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
		LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);
		
		LOGGER.info("Server Loaded in " + (System.currentTimeMillis() - serverLoadStart) / 1000 + " seconds");
		
		ServerStatus.getInstance();
		
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final L2GamePacketHandler handler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket.", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static GameServer getInstance()
	{
		return _gameServer;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public class SpawnMonsters implements Runnable
	{
		@Override
		public void run()
		{
			PartyFarm.spawnMonsters();
		}
	}	
}