package phantom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.CharTemplateTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.geoengine.GeoEngine;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.model.L2Augmentation;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.appearance.PcAppearance;
import com.l2jmega.gameserver.model.actor.instance.Monster;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.PlayerTemplate;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.base.Experience;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GameClient.GameClientState;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

public class Phantom_Farm
{
	
	static final Logger _log = Logger.getLogger(Phantom_Farm.class.getName());

	static int _setsCount = 0;
	static ArrayList<L2Set> _sets = new ArrayList<>();
	static Phantom_Farm _instance;
	static int _locsCount = 0;
	static ArrayList<Location> _PhantomsTownLoc = new ArrayList<>();
	
	public static Phantom_Farm getInstance()
	{
		return _instance;
	}
	
	private void load()
	{
			parceArmors();
			cacheFantoms();
			parceFarmLocs();
	}
	
	public void reload()
	{
		parceArmors();
		parceFarmLocs();
	}
	
	public static void init()
	{
		_instance = new Phantom_Farm();
		_instance.load();
	}
	
	@SuppressWarnings("resource")
	private static void parceFarmLocs()
	{
		_PhantomsTownLoc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/phantom/farm_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_PhantomsTownLoc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_locsCount = _PhantomsTownLoc.size() - 1;
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
		
	static int getFaceEquipe()
	{
		return Config.LIST_PHANTOM_FACE.get(Rnd.get(Config.LIST_PHANTOM_FACE.size()));
	}
	
	static int getHairEquipe()
	{
		return Config.LIST_PHANTOM_HAIR.get(Rnd.get(Config.LIST_PHANTOM_HAIR.size()));
	}
	
	final static List<String> list_title = new ArrayList<>();
	
	
	static String getNameColor()
	{
		return Config.PHANTOM_PLAYERS_NAME_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_NAME_CLOLORS.size()));
	}
	
	static String getTitleColor()
	{
		return Config.PHANTOM_PLAYERS_TITLE_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_TITLE_CLOLORS.size()));
	}
	
	@SuppressWarnings("resource")
	private static void parceArmors()
	{
		if (!_sets.isEmpty())
		{
			_sets.clear();
		}
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/custom/phantom/mage_sets.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_sets.add(new L2Set(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2]), Integer.parseInt(arrayOfString[3]), Integer.parseInt(arrayOfString[4]), Integer.parseInt(arrayOfString[5]), Integer.parseInt(arrayOfString[6])));
				}
			}
			_setsCount = _sets.size();
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException6)
			{
			}
		}
	}
	
	private void cacheFantoms()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ThreadPool.schedule(new FantomTask(1), Rnd.get(5000, 15000));
			}
		}).start();
	}
	
	static L2Set getRandomSet()
	{
		return _sets.get(Rnd.get(_setsCount));
	}
	
	static class L2Set
	{
		public int _body;
		public int _gaiters;
		public int _gloves;
		public int _boots;
		public int _weapon;
		public int _custom;
		public int _grade;
		
		L2Set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
		{
			_body = paramInt1;
			_gaiters = paramInt2;
			_gloves = paramInt3;
			_boots = paramInt4;
			_weapon = paramInt5;
			_grade = paramInt6;
			_custom = paramInt7;
		}
	}
	
	public static ArrayList<Player> _add_phantom = new ArrayList<>();
	
	public static int getPhantomCount()
	{
		if (_add_phantom != null)
			return _add_phantom.size();
		
		return 0;
	}
	
	public static void removePhantom(Player spec)
	{
		if (_add_phantom != null && _add_phantom.contains(spec))
			_add_phantom.remove(spec);
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH");
	
	public class FantomTask implements Runnable
	{
		public int _task;
		
		public FantomTask(int paramInt)
		{
			_task = paramInt;
		}
		
		@Override
		public void run()
		{
			WhileLoop1.spawnfarm1();
			WhileLoop2.spawnfarm2();
			WhileLoop3.spawnfarm3();
		}
	}
	
	static class WhileLoop1
	{
		public static void spawnfarm1()
		{	
			int i = Config.COUNT_FARM_ARCHMAGE;
			
			while (i > 0)
			{
				spawnFarmArchmage();
				try
				{
					Thread.sleep(Rnd.get(2100, 5200));
				}
				catch (InterruptedException e)
				{
				}
				i--;
			}
		}
	}
	
	static class WhileLoop2
	{
		public static void spawnfarm2()
		{	
			int i = Config.COUNT_FARM_MYSTICMUSE;
			
			while (i > 0)
			{
				spawnFarmMysticmuse();
				try
				{
					Thread.sleep(Rnd.get(2100, 5200));
				}
				catch (InterruptedException e)
				{
				}
				i--;
			}
		}
	}
	
	static class WhileLoop3
	{
		public static void spawnfarm3()
		{	
			int i = Config.COUNT_FARM_STORMSCREAM;
			
			while (i > 0)
			{
				spawnFarmStormscream();
				try
				{
					Thread.sleep(Rnd.get(2100, 5200));
				}
				catch (InterruptedException e)
				{
				}
				i--;
			}
		}
	}
	
	public static PhantomOld createArchmagefarm()
	{
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		String playerName = PhantomNameManager.INSTANCE.getRandomAvailableName();
		
		int[] classes =
		{
			94,117,92
		};
		
		int classId = classes[Rnd.get(classes.length)];
		
		final PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId);
		PcAppearance app = getRandomAppearance(template.getRace());
		PhantomOld player = new PhantomOld(objectId, template, accountName, app);
		
		player.setName(playerName);
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		PlayerNameTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
		player.setBaseClass(player.getClassId());
		setLevel(player, 81);
		player.heal();
		
		return player;
	}
	
	public static PhantomOld createMysticmusefarm()
	{
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		String playerName = PhantomNameManager.INSTANCE.getRandomAvailableName();
		
		int[] classes =
		{
			103,117,92
		};
		
		int classId = classes[Rnd.get(classes.length)];
		
		final PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId);
		PcAppearance app = getRandomAppearance(template.getRace());
		PhantomOld player = new PhantomOld(objectId, template, accountName, app);
		
		player.setName(playerName);
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		PlayerNameTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
		player.setBaseClass(player.getClassId());
		setLevel(player, 81);
		player.heal();
		
		return player;
	}
	
	public static PhantomOld createStormscreamfarm()
	{
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		String playerName = PhantomNameManager.INSTANCE.getRandomAvailableName();
		
		int[] classes =
		{
			110,117,92,115,94
		};
		
		int classId = classes[Rnd.get(classes.length)];
		
		final PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(classId);
		PcAppearance app = getRandomAppearance(template.getRace());
		PhantomOld player = new PhantomOld(objectId, template, accountName, app);
		
		player.setName(playerName);
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		PlayerNameTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
		player.setBaseClass(player.getClassId());
		setLevel(player, 81);
		player.heal();
		
		return player;
	}
	
	public static PcAppearance getRandomAppearance(ClassRace race)
	{
		
		Sex randomSex = Rnd.get(1, 2) == 1 ? Sex.MALE : Sex.FEMALE;
		int hairStyle = Rnd.get(0, randomSex == Sex.MALE ? 4 : 6);
		int hairColor = Rnd.get(0, 3);
		int faceId = Rnd.get(0, 2);
		
		return new PcAppearance((byte) faceId, (byte) hairColor, (byte) hairStyle, randomSex);
	}
	
	public static void setLevel(PhantomOld player, int level)
	{
		if (level >= 1 && level <= Experience.MAX_LEVEL)
		{
			long pXp = player.getExp();
			long tXp = Experience.LEVEL[81];
			
			if (pXp > tXp)
				player.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				player.addExpAndSp(tXp - pXp, 0);
		}
	}
	
	public static PhantomOld spawnFarmArchmage()
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		PhantomOld activeChar = createArchmagefarm();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		World.getInstance().addPlayer(activeChar);
		activeChar.setIsPhantom(true);
		activeChar.setIsFarmArchMage(true);
		L2Set localL2Set = getRandomSet();
		ItemInstance localL2ItemInstance1 = ItemTable.getInstance().createDummyItem(localL2Set._body);
		ItemInstance localL2ItemInstance2 = ItemTable.getInstance().createDummyItem(localL2Set._gaiters);
		ItemInstance localL2ItemInstance3 = ItemTable.getInstance().createDummyItem(localL2Set._gloves);
		ItemInstance localL2ItemInstance4 = ItemTable.getInstance().createDummyItem(localL2Set._boots);
		ItemInstance localL2ItemInstance5 = ItemTable.getInstance().createDummyItem(localL2Set._weapon);
		ItemInstance localL2ItemInstance6 = null;
		
		ItemInstance WINGS = ItemTable.getInstance().createDummyItem(getFaceEquipe());
		ItemInstance HAIR = ItemTable.getInstance().createDummyItem(getHairEquipe());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance1);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance2);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance3);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance4);
		
		if (Config.ALLOW_PHANTOM_FACE)
			activeChar.getInventory().equipItemAndRecord(WINGS);
		
		if (Config.ALLOW_PHANTOM_HAIR)
			activeChar.getInventory().equipItemAndRecord(HAIR);
		
		if (localL2Set._custom > 0)
		{
			localL2ItemInstance6 = ItemTable.getInstance().createDummyItem(localL2Set._custom);
			activeChar.getInventory().equipItemAndRecord(localL2ItemInstance6);
		}
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		startAttack(activeChar);
		
		Location localLocation = getRandomLoc();
		activeChar.spawnMe(localLocation.getX() + Rnd.get(-80, 80), localLocation.getY() + Rnd.get(-80, 80), localLocation.getZ());
		
		activeChar.setLastCords(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance5);
		
		if (Rnd.get(100) < 30 && Config.PHANTOM_PLAYERS_ARGUMENT_ANIM)
		{
			localL2ItemInstance5.setAugmentation(new L2Augmentation(1067847165, 3250, 1));
		}
		
		localL2ItemInstance5.setEnchantLevel(Rnd.get(Config.PHANTOM_PLAYERS_ENCHANT_MIN, Config.PHANTOM_PLAYERS_ENCHANT_MAX));
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
		 if (Rnd.get(100) < Config.PHANTOM_PLAYERS_CREST_ATK && Config.ALLOW_PHANTOM_CREST_ATK)
		 {
		 activeChar.setClan(ClanTable.getInstance().getClan(getRandomClan()));
		 activeChar.getAppearance().setNameColor(Integer.decode("0x" + getNameColor()));
		 activeChar.getAppearance().setTitleColor(Integer.decode("0x" + getTitleColor()));
		 }
		 else
		 {
		activeChar.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
		activeChar.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
		 }
		
			String playerTitle = PhantomTitleManager.INSTANCE.getRandomAvailableTitle();
			
		if (Config.PHANTOM_TITLE_PHANTOM_ATK)
		{
			if(Config.PHANTOM_TITLE_CONFIG)
				activeChar.setTitle(getFixTitle());
			else if (Rnd.get(100) < Config.PHANTOM_CHANCE_TITLE)
				activeChar.setTitle(playerTitle);
		}
		
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
			skill.getEffects(activeChar, activeChar);
		}
		
		Disconect(activeChar);
		
		activeChar.onPlayerEnter();
		
		activeChar.heal();

		return activeChar;
	}
	
	public static PhantomOld spawnFarmMysticmuse()
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		PhantomOld activeChar = createMysticmusefarm();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		World.getInstance().addPlayer(activeChar);
		activeChar.setIsPhantom(true);
		activeChar.setIsFarmMysticMuse(true);
		L2Set localL2Set = getRandomSet();
		ItemInstance localL2ItemInstance1 = ItemTable.getInstance().createDummyItem(localL2Set._body);
		ItemInstance localL2ItemInstance2 = ItemTable.getInstance().createDummyItem(localL2Set._gaiters);
		ItemInstance localL2ItemInstance3 = ItemTable.getInstance().createDummyItem(localL2Set._gloves);
		ItemInstance localL2ItemInstance4 = ItemTable.getInstance().createDummyItem(localL2Set._boots);
		ItemInstance localL2ItemInstance5 = ItemTable.getInstance().createDummyItem(localL2Set._weapon);
		ItemInstance localL2ItemInstance6 = null;
		
		ItemInstance WINGS = ItemTable.getInstance().createDummyItem(getFaceEquipe());
		ItemInstance HAIR = ItemTable.getInstance().createDummyItem(getHairEquipe());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance1);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance2);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance3);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance4);
		
		if (Config.ALLOW_PHANTOM_FACE)
			activeChar.getInventory().equipItemAndRecord(WINGS);
		
		if (Config.ALLOW_PHANTOM_HAIR)
			activeChar.getInventory().equipItemAndRecord(HAIR);
		
		if (localL2Set._custom > 0)
		{
			localL2ItemInstance6 = ItemTable.getInstance().createDummyItem(localL2Set._custom);
			activeChar.getInventory().equipItemAndRecord(localL2ItemInstance6);
		}
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		startAttack(activeChar);
		
		Location localLocation = getRandomLoc();
		activeChar.spawnMe(localLocation.getX() + Rnd.get(-80, 80), localLocation.getY() + Rnd.get(-80, 80), localLocation.getZ());
		
		activeChar.setLastCords(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance5);
		
		if (Rnd.get(100) < 30 && Config.PHANTOM_PLAYERS_ARGUMENT_ANIM)
		{
			localL2ItemInstance5.setAugmentation(new L2Augmentation(1067847165, 3250, 1));
		}
		
		localL2ItemInstance5.setEnchantLevel(Rnd.get(Config.PHANTOM_PLAYERS_ENCHANT_MIN, Config.PHANTOM_PLAYERS_ENCHANT_MAX));
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
		 if (Rnd.get(100) < Config.PHANTOM_PLAYERS_CREST_ATK && Config.ALLOW_PHANTOM_CREST_ATK)
		 {
		 activeChar.setClan(ClanTable.getInstance().getClan(getRandomClan()));
		 activeChar.getAppearance().setNameColor(Integer.decode("0x" + getNameColor()));
		 activeChar.getAppearance().setTitleColor(Integer.decode("0x" + getTitleColor()));
		 }
		 else
		 {
		activeChar.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
		activeChar.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
		 }
		
			String playerTitle = PhantomTitleManager.INSTANCE.getRandomAvailableTitle();
			
		if (Config.PHANTOM_TITLE_PHANTOM_ATK)
		{
			if(Config.PHANTOM_TITLE_CONFIG)
				activeChar.setTitle(getFixTitle());
			else if (Rnd.get(100) < Config.PHANTOM_CHANCE_TITLE)
				activeChar.setTitle(playerTitle);
		}
		
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
			skill.getEffects(activeChar, activeChar);
		}
		
		Disconect(activeChar);
		
		activeChar.onPlayerEnter();
		
		activeChar.heal();

		return activeChar;
	}
	
	public static PhantomOld spawnFarmStormscream()
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		PhantomOld activeChar = createStormscreamfarm();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		World.getInstance().addPlayer(activeChar);
		activeChar.setIsPhantom(true);
		activeChar.setIsFarmStormScream(true);
		L2Set localL2Set = getRandomSet();
		ItemInstance localL2ItemInstance1 = ItemTable.getInstance().createDummyItem(localL2Set._body);
		ItemInstance localL2ItemInstance2 = ItemTable.getInstance().createDummyItem(localL2Set._gaiters);
		ItemInstance localL2ItemInstance3 = ItemTable.getInstance().createDummyItem(localL2Set._gloves);
		ItemInstance localL2ItemInstance4 = ItemTable.getInstance().createDummyItem(localL2Set._boots);
		ItemInstance localL2ItemInstance5 = ItemTable.getInstance().createDummyItem(localL2Set._weapon);
		ItemInstance localL2ItemInstance6 = null;
		
		ItemInstance WINGS = ItemTable.getInstance().createDummyItem(getFaceEquipe());
		ItemInstance HAIR = ItemTable.getInstance().createDummyItem(getHairEquipe());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance1);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance2);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance3);
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance4);
		
		if (Config.ALLOW_PHANTOM_FACE)
			activeChar.getInventory().equipItemAndRecord(WINGS);
		
		if (Config.ALLOW_PHANTOM_HAIR)
			activeChar.getInventory().equipItemAndRecord(HAIR);
		
		if (localL2Set._custom > 0)
		{
			localL2ItemInstance6 = ItemTable.getInstance().createDummyItem(localL2Set._custom);
			activeChar.getInventory().equipItemAndRecord(localL2ItemInstance6);
		}
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		startAttack(activeChar);
		
		Location localLocation = getRandomLoc();
		activeChar.spawnMe(localLocation.getX() + Rnd.get(-80, 80), localLocation.getY() + Rnd.get(-80, 80), localLocation.getZ());
		
		activeChar.setLastCords(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		activeChar.getInventory().equipItemAndRecord(localL2ItemInstance5);
		
		if (Rnd.get(100) < 30 && Config.PHANTOM_PLAYERS_ARGUMENT_ANIM)
		{
			localL2ItemInstance5.setAugmentation(new L2Augmentation(1067847165, 3250, 1));
		}
		
		localL2ItemInstance5.setEnchantLevel(Rnd.get(Config.PHANTOM_PLAYERS_ENCHANT_MIN, Config.PHANTOM_PLAYERS_ENCHANT_MAX));
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
		 if (Rnd.get(100) < Config.PHANTOM_PLAYERS_CREST_ATK && Config.ALLOW_PHANTOM_CREST_ATK)
		 {
		 activeChar.setClan(ClanTable.getInstance().getClan(getRandomClan()));
		 activeChar.getAppearance().setNameColor(Integer.decode("0x" + getNameColor()));
		 activeChar.getAppearance().setTitleColor(Integer.decode("0x" + getTitleColor()));
		 }
		 else
		 {
		activeChar.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
		activeChar.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
		 }
		
			String playerTitle = PhantomTitleManager.INSTANCE.getRandomAvailableTitle();
			
		if (Config.PHANTOM_TITLE_PHANTOM_ATK)
		{
			if(Config.PHANTOM_TITLE_CONFIG)
				activeChar.setTitle(getFixTitle());
			else if (Rnd.get(100) < Config.PHANTOM_CHANCE_TITLE)
				activeChar.setTitle(playerTitle);
		}
		
		activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
		
		if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
			skill.getEffects(activeChar, activeChar);
		}
		
		Disconect(activeChar);
		
		activeChar.onPlayerEnter();
		
		activeChar.heal();

		return activeChar;
	}
	

	
	  public static boolean doCastlist(final Player player)
	  {
	    if ((player.isDead())) {
	      return false;
	    }
	    List<Creature> targetList = new ArrayList<>();
		for (WorldObject obj : player.getKnownType(WorldObject.class)) {
			
	    	if (obj instanceof Monster && ((Monster) obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), Config.FARM_RANGE / 2, false, false))
				targetList.add((Monster) obj);
	    }
		

	    
	    if (targetList.size() == 0)
	    {
			for (WorldObject obj : player.getKnownType(WorldObject.class)) {
		    	if (obj instanceof Monster && ((Monster) obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 1000, false, false))
					targetList.add((Monster) obj);
		    }
	    }
	    
	    if (targetList.size() == 0)
	    {
			for (WorldObject obj : player.getKnownType(WorldObject.class)) {
		    	if (obj instanceof Monster && ((Monster) obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 2000, false, false))
					targetList.add((Monster) obj);
		    }
	    }
	    
	    if (targetList.size() == 0)
	    {
			for (WorldObject obj : player.getKnownType(WorldObject.class)) {
		    	if (obj instanceof Monster && ((Monster) obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 3000, false, false))
					targetList.add((Monster) obj);
		    }
	    }
	    
	    if (targetList.size() == 0)
	    {
			for (WorldObject obj : player.getKnownType(WorldObject.class)) {
		    	if (obj instanceof Monster && ((Monster) obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 6000, false, false))
					targetList.add((Monster) obj);
		    }
	    }
	
		if (targetList.size() == 0)
		{
			player.stopMove(null);
			player.setTarget(null);
			player.getAI().setIntention(CtrlIntention.FOLLOW, null);
			
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					if (!player.isDead())
					{
						if (Rnd.get(100) < 90)
						{
							player.rndWalkMonster();
							try
							{
								Thread.sleep(2000);
							}
							catch (InterruptedException e)
							{
							}
						}
						
					}
					doCastlist(player);
				}
			}, 1000);
			
			return false;
		}
	    
	    if (targetList.isEmpty()) {
	      return true;
	    }
	    int nextTargetIdx = Rnd.get(targetList.size());
	    
	    WorldObject target = targetList.get(nextTargetIdx);
	    
	    player.setTarget(target);
	    player.setRunning();
	    try
	    {
	      Thread.sleep(1000L);
	    }
	    catch (InterruptedException localInterruptedException) {}
		doCast(player, (Monster) target);
	    return true;
	  }
	
		static void Seguir(final Player player, final Monster target)
		{
			if (player.isDead() || player.isAttackP())
				return;
			
			player.getAI().setIntention(CtrlIntention.FOLLOW, target);
			
			try
			{
				Thread.sleep(1200);
			}
			catch (InterruptedException e)
			{
			}
			doCastlist(player);
			
		}
	
	static void doCast(final Player player, final Monster target)
	{
		if (!player.isDead() && player.getTarget() != null && !target.isDead() && !(target._inEventTvT && TvT.is_started()) && !(target._inEventCTF && CTF.is_started()) && (player.getZ() > (target.getZ() + 100)))
		{
			Seguir(player, target);
			return;
		}
		else if (!player.isDead() && player.getTarget() != null && !target.isDead() && !(target._inEventTvT && TvT.is_started()) && !(target._inEventCTF && CTF.is_started()) && !GeoEngine.getInstance().canSeeTarget(player, target))
		{
			Seguir(player, target);
			return;
		}
		else if (!player.isDead() && (target.isDead() || player.getTarget() == null || (target._inEventTvT && TvT.is_started()) || (target._inEventCTF && CTF.is_started())))
		{
			player.stopMove(null);
			player.setTarget(null);
			doCastlist(player);
			return;
		}
		
		if (player.isFarmArchMage())
		Mage_Surrender(player, target, 1083, 17, Config.PHANTOM_SURRENDER_INTERVAL, 25);
		else if (player.isFarmMysticMuse())
		Mage_Surrender(player, target, 1071, 14, Config.PHANTOM_SURRENDER_INTERVAL, 25);
		else if (player.isFarmStormScream())
		Mage_Surrender(player, target, 1074, 14, Config.PHANTOM_SURRENDER_INTERVAL, 25);
		
		if (!player.isDead() && player.isFarmArchMage()){
			Archmage_Attack_Target(player, target);
		}else if (!player.isDead() && player.isFarmMysticMuse()){
			Mysticmuse_Attack_Target(player, target);
		}else if (!player.isDead() && player.isFarmStormScream()){
			Stormscream_Attack_Target(player, target);
		}

	}
	
	
	static void Mage_Surrender(Player player, Monster target, int skill_id, int skill_level, int delay, int random)
	{
	    if (player.isSpawnProtected()) {
	        player.setSpawnProtection(false);
	      }
		if (!player.isAllSkillsDisabled() && !player.isDead())
		{
			checkRange(player, target);
			if (player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				if (!target.isInvul())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
					skill.getEffects(target, target);
				}
				player.stopMove(null);
				player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, 500, 0, false));
				player.broadcastPacket(new MagicSkillUse(player, player, 2154, 1, 0, 0));
				player.getActingPlayer().getAI().clientStartAutoAttack();
				
				player.broadcastUserInfo();
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
				}
				if (Rnd.get(100) < random)
				{
					player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, 500, 0, false));
					player.broadcastPacket(new MagicSkillUse(player, player, 2154, 1, 0, 0));
					try
					{
						Thread.sleep(delay);
					}
					catch (InterruptedException e)
					{
					}
				}
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
			}
		}
	}

	
	static void Archmage_Attack(Player player, Monster target, int skill_id, int skill_level, int delay)
	{
	    if (player.isSpawnProtected()) {
	        player.setSpawnProtection(false);
	      }
		if (!player.isAllSkillsDisabled() && !player.isDead())
		{
			checkRange(player, target);
			if (player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				player.stopMove(null);
				player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, Config.PHANTOM_ARCHMAGE_EFFECT, 0, false));
				player.broadcastPacket(new MagicSkillUse(player, player, 2154, 1, 0, 0));
				player.getActingPlayer().getAI().clientStartAutoAttack();

				player.broadcastUserInfo();
				
				double mDef = target.getMDef(player, null);
				double damage = 91 * Math.sqrt(Config.POWER_PHANTOM) / mDef * 1000;
				
				if (Rnd.get(100) < Config.PHANTOM_ARCHMAGE_PERCENTAGE)
					target.reduceCurrentHp(damage, player, null);
				else
					target.reduceCurrentHp(damage / 2, player, null);
				
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
				}
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
			}
		}
	}
	
	static void Archmage_Attack_Target(Player player, Monster target)
	{
		boolean doAtack = true;
		
		while (doAtack)
		{
			if (player.isAttackP())
				doAtack = false;
			else if (!target.isDead() && player.getTarget() != null && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = true;
				Archmage_Attack(player, target, 1230, 1, Config.PHANTOM_ARCHMAGE_DANO_INTERVAL);
			}
			else if (!player.isDead() && player.getTarget() != null && !target.isDead() && !(target._inEventTvT && TvT.is_started()) && !(target._inEventCTF && CTF.is_started()) && !GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = false;
				Seguir(player, target);
			}
			else if (!player.isDead() && (target.isDead() || player.getTarget() == null || (target._inEventTvT && TvT.is_started()) || (target._inEventCTF && CTF.is_started())))
			{
				doAtack = false;
				player.stopMove(null);
				player.setTarget(null);
				if (Rnd.get(100) < 60)
				{
					player.rndWalkMonster();
					try
					{
						Thread.sleep(2500);
					}
					catch (InterruptedException e)
					{
					}
				}
				doCastlist(player);
			}
			else
				doAtack = false;
		}
	}
	
	static void Mysticmuse_Attack(Player player, Monster target, int skill_id, int skill_level, int delay)
	{
		if (player.isSpawnProtected())
			player.setSpawnProtection(false);
		
		if (!player.isDead() && !player.isAllSkillsDisabled() && !player.isAttackP())
		{
			checkRange(player, target);
			if (!player.isDead() && player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target) && !(target._inEventTvT && (TvT.is_started() || TvT.is_teleport())) && !(target._inEventCTF && (CTF.is_started() || CTF.is_teleport())))
			{
				player.stopMove(null);
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
				player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, Config.PHANTOM_SPELLSINGER_EFFECT, 0, false));
				player.getActingPlayer().getAI().clientStartAutoAttack();
				
			//	if (player.getPvpFlag() == 0)
			//	{
			//		player.setPvpFlag(1);
			//		player.broadcastUserInfo();					
			//	}
				
				double mDef = target.getMDef(player, null);
				double damage = 91 * Math.sqrt(Config.POWER_PHANTOM) / mDef * 1000;
				
				if (Rnd.get(100) < Config.PHANTOM_SPELLSINGER_PERCENTAGE)
					target.reduceCurrentHp(damage, player, null);
				else
					target.reduceCurrentHp(damage / 2, player, null);
				
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
				}
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
			}
		}
	}
	
	static void Mysticmuse_Attack_Target(Player player, Monster target)
	{
		boolean doAtack = true;
		
		while (doAtack)
		{
			if (player.isAttackP())
				doAtack = false;
			else if (!target.isDead() && player.getTarget() != null && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = true;
				Mysticmuse_Attack(player, target, 1235, 1, Config.PHANTOM_SPELLSINGER_DANO_INTERVAL);
			}
			else if (!player.isDead() && player.getTarget() != null && !target.isDead() && !(target._inEventTvT && TvT.is_started()) && !(target._inEventCTF && CTF.is_started()) && !GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = false;
				Seguir(player, target);
			}
			else if (!player.isDead() && (target.isDead() || player.getTarget() == null || (target._inEventTvT && TvT.is_started()) || (target._inEventCTF && CTF.is_started())))
			{
				doAtack = false;
				player.stopMove(null);
				player.setTarget(null);
				
				if (Rnd.get(100) < 60)
				{
					player.rndWalkMonster();
					try
					{
						Thread.sleep(2500);
					}
					catch (InterruptedException e)
					{
					}
				}
				doCastlist(player);
			}
			else
				doAtack = false;
		}
	}
	
	static void Stormscream_Attack(Player player, Monster target, int skill_id, int skill_level, int delay)
	{
		if (player.isSpawnProtected())
			player.setSpawnProtection(false);
		
		if (!player.isDead() && !player.isAllSkillsDisabled() && !player.isAttackP())
		{
			checkRange(player, target);
			if (!player.isDead() && player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target) && !(target._inEventTvT && (TvT.is_started() || TvT.is_teleport())) && !(target._inEventCTF && (CTF.is_started() || CTF.is_teleport())))
			{
				player.stopMove(null);
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
				player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, Config.PHANTOM_SPELLHOLLER_EFFECT, 0, false));
				if (Config.ALLOW_PHANTOM_PLAYERS_EFFECT_SHOT){
				//player.broadcastPacket(new MagicSkillUse(player, player, 2166, 1, 0, 0));
				player.broadcastPacket(new MagicSkillUse(player, player, 2164, 1, 0, 0));
				}
				player.getActingPlayer().getAI().clientStartAutoAttack();
				
			//	if (player.getPvpFlag() == 0)
			//	{
			//		player.setPvpFlag(1);
			//		player.broadcastUserInfo();					
			//	}
				
				double mDef = target.getMDef(player, null);
				double damage = 91 * Math.sqrt(Config.POWER_PHANTOM) / mDef * 1000;
				
				if (Rnd.get(100) < Config.PHANTOM_SPELLHOLLER_PERCENTAGE)
					target.reduceCurrentHp(damage, player, null);
				else
					target.reduceCurrentHp(damage / 2, player, null);
				
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
				}
				player.getAI().setIntention(CtrlIntention.FOLLOW, null);
			}
		}
	}
	
	static void Stormscream_Attack_Target(Player player, Monster target)
	{
		boolean doAtack = true;
		
		while (doAtack)
		{
			if (player.isAttackP())
				doAtack = false;
			else if (!target.isDead() && player.getTarget() != null && GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = true;
				Stormscream_Attack(player, target, 1239, 1, Config.PHANTOM_SPELLHOLLER_DANO_INTERVAL);
			}
			else if (!player.isDead() && player.getTarget() != null && !target.isDead() && !(target._inEventTvT && TvT.is_started()) && !(target._inEventCTF && CTF.is_started()) && !GeoEngine.getInstance().canSeeTarget(player, target))
			{
				doAtack = false;
				Seguir(player, target);
			}
			else if (!player.isDead() && (target.isDead() || player.getTarget() == null || (target._inEventTvT && TvT.is_started()) || (target._inEventCTF && CTF.is_started())))
			{
				doAtack = false;
				player.stopMove(null);
				player.setTarget(null);
				if (Rnd.get(100) < 60)
				{
					player.rndWalkMonster();
					try
					{
						Thread.sleep(2500);
					}
					catch (InterruptedException e)
					{
					}
				}
				doCastlist(player);
			}
			else
				doAtack = false;
		}
	}
	
	
	static void checkRange(Player player, Monster target)
	{
		if (player.isDead())
			return;
		
		if (!player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && !player.isMovementDisabled())
		{
			player.abortCast();
			player.getAI().setIntention(CtrlIntention.FOLLOW, target);
			try
			{
				Thread.sleep(1200);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	public static int getRandomClan()
	{
		return Config.LIST_CLAN_ID.get(Rnd.get(Config.LIST_CLAN_ID.size()));
	}
	
	static String getFixTitle()
	{
		return Config.PHANTOM_TITLE.get(Rnd.get(Config.PHANTOM_TITLE.size()));
	}
	
	public static void Disconect(Player paramPlayer)
	{
		ThreadPool.schedule(new PhantomDelete(paramPlayer), Config.FARM_DISCONNETC_DELAY*1000);
	}
	
	static class PhantomDelete implements Runnable
	{
		Player _phantom;
		
		public PhantomDelete(Player paramPlayer)
		{
			_phantom = paramPlayer;
		}
		
		@Override
		public void run()
		{
			if (_phantom.isFarmArchMage() || _phantom.isFarmMysticMuse() || _phantom.isFarmStormScream())
			{
				PhantomOld fakePlayer = (PhantomOld) _phantom;
				fakePlayer.despawnPlayer();
			}
		}
	}
	public static void startAttack(Player paramPlayer)
	{
		ThreadPool.schedule(new PhantomAtack(paramPlayer), Rnd.get(2100, 5200));
	}
	
	static class PhantomAtack implements Runnable
	{
		Player _phantom;
		
		public PhantomAtack(Player paramPlayer)
		{
			_phantom = paramPlayer;
		}
		
		@Override
		public void run()
		{
			if (!_phantom.isDead())
				doCastlist(_phantom);
		}
	}

	
	@SuppressWarnings("null")
	public
	static Location getRandomLoc()
	{
		Location loc = null;
		if (loc == null)
			loc = _PhantomsTownLoc.get(Rnd.get(0, _locsCount));
		return loc;
	}
}
