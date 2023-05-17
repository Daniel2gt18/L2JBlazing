/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.events;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.manager.EventTask;
import com.l2jmega.events.manager.TvTEventManager;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmega.util.CloseUtil;

import phantom.PhantomOld;
import phantom.Phantom_TvT;

/**
 * The Class TvT.
 */
public class TvT implements EventTask
{
	
	/** The Constant _log. */
	protected static final Logger _log = Logger.getLogger(TvT.class.getName());
	
	/** The _joining location name. */
	public static String _eventName = new String();
	
	public static String _eventDesc = new String();
	
	protected static String _joiningLocationName = new String();
	
	/** The _npc spawn. */
	private static L2Spawn _npcSpawn;
	
	/** The _in progress. */
	
	public static boolean _joining = false;
	
	public static boolean _teleport = false;
	
	public static boolean _started = false;
	
	protected static boolean _aborted = false;
	
	protected static boolean _sitForced = false;
	
	protected static boolean _inProgress = false;
	
	/** The _max players. */
	protected static int _npcId = 0;
	public static int _npcX = 0;
	
	public static int _npcY = 0;
	
	public static int _npcZ = 0;
	protected static int _npcHeading = 0;
	
	public static int _rewardId = 0;
	
	public static int _rewardAmount = 0;
	
	public static int _minlvl = 0;
	
	public static int _maxlvl = 0;
	
	protected static int _joinTime = 0;
	
	protected static int _eventTime = 0;
	
	protected static int _minPlayers = 0;
	
	public static int _maxPlayers = 0;
	
	/** The _interval between matchs. */
	protected static long _intervalBetweenMatchs = 0;
	
	/** The start event time. */
	private String startEventTime;
	
	/** The _team event. */
	private static boolean _teamEvent = true; // TODO to be integrated
	
	/** The _players. */
	public static ArrayList<Player> _players = new ArrayList<>();
	public static ArrayList<Player> _players_afk = new ArrayList<>();
	
	/** The _top team. */
	private static String _topTeam = new String();
	
	/** The _players shuffle. */
	public static ArrayList<Player> _playersShuffle = new ArrayList<>();
	
	/** The _save player teams. */
	public static ArrayList<String> _teams = new ArrayList<>(), _savePlayers = new ArrayList<>(), _savePlayerTeams = new ArrayList<>();
	
	/** The _teams z. */
	public static ArrayList<Integer> _teamPlayersCount = new ArrayList<>(), _teamColors = new ArrayList<>(), _teamsX = new ArrayList<>(), _teamsY = new ArrayList<>(), _teamsZ = new ArrayList<>();
	
	/** The _team points count. */
	public static ArrayList<Integer> _teamPointsCount = new ArrayList<>();
	
	/** The _top kills. */
	public static int _topKills = 0;
	
	/**
	 * Instantiates a new tv t.
	 */
	private TvT()
	{
	}
	
	/**
	 * Gets the new instance.
	 * @return the new instance
	 */
	public static TvT getNewInstance()
	{
		return new TvT();
	}
	
	/**
	 * Gets the _event name.
	 * @return the _eventName
	 */
	public static String get_eventName()
	{
		return _eventName;
	}
	
	/**
	 * Set_event name.
	 * @param _eventName the _eventName to set
	 * @return true, if successful
	 */
	public static boolean set_eventName(String _eventName)
	{
		if (!is_inProgress())
		{
			TvT._eventName = _eventName;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _event desc.
	 * @return the _eventDesc
	 */
	public static String get_eventDesc()
	{
		return _eventDesc;
	}
	
	/**
	 * Set_event desc.
	 * @param _eventDesc the _eventDesc to set
	 * @return true, if successful
	 */
	public static boolean set_eventDesc(String _eventDesc)
	{
		if (!is_inProgress())
		{
			TvT._eventDesc = _eventDesc;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _joining location name.
	 * @return the _joiningLocationName
	 */
	public static String get_joiningLocationName()
	{
		return _joiningLocationName;
	}
	
	/**
	 * Set_joining location name.
	 * @param _joiningLocationName the _joiningLocationName to set
	 * @return true, if successful
	 */
	public static boolean set_joiningLocationName(String _joiningLocationName)
	{
		if (!is_inProgress())
		{
			TvT._joiningLocationName = _joiningLocationName;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _npc id.
	 * @return the _npcId
	 */
	public static int get_npcId()
	{
		return _npcId;
	}
	
	/**
	 * Set_npc id.
	 * @param _npcId the _npcId to set
	 * @return true, if successful
	 */
	public static boolean set_npcId(int _npcId)
	{
		if (!is_inProgress())
		{
			TvT._npcId = _npcId;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _npc location.
	 * @return the _npc location
	 */
	public static Location get_npcLocation()
	{
		Location npc_loc = new Location(_npcX, _npcY, _npcZ);
		return npc_loc;
	}
	
	/**
	 * Gets the _reward id.
	 * @return the _rewardId
	 */
	public static int get_rewardId()
	{
		return _rewardId;
	}
	
	/**
	 * Set_reward id.
	 * @param _rewardId the _rewardId to set
	 * @return true, if successful
	 */
	public static boolean set_rewardId(int _rewardId)
	{
		if (!is_inProgress())
		{
			TvT._rewardId = _rewardId;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _reward amount.
	 * @return the _rewardAmount
	 */
	public static int get_rewardAmount()
	{
		return _rewardAmount;
	}
	
	/**
	 * Set_reward amount.
	 * @param _rewardAmount the _rewardAmount to set
	 * @return true, if successful
	 */
	public static boolean set_rewardAmount(int _rewardAmount)
	{
		if (!is_inProgress())
		{
			TvT._rewardAmount = _rewardAmount;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _minlvl.
	 * @return the _minlvl
	 */
	public static int get_minlvl()
	{
		return _minlvl;
	}
	
	/**
	 * Set_minlvl.
	 * @param _minlvl the _minlvl to set
	 * @return true, if successful
	 */
	public static boolean set_minlvl(int _minlvl)
	{
		if (!is_inProgress())
		{
			TvT._minlvl = _minlvl;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _maxlvl.
	 * @return the _maxlvl
	 */
	public static int get_maxlvl()
	{
		return _maxlvl;
	}
	
	/**
	 * Set_maxlvl.
	 * @param _maxlvl the _maxlvl to set
	 * @return true, if successful
	 */
	public static boolean set_maxlvl(int _maxlvl)
	{
		if (!is_inProgress())
		{
			TvT._maxlvl = _maxlvl;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _join time.
	 * @return the _joinTime
	 */
	public static int get_joinTime()
	{
		return _joinTime;
	}
	
	/**
	 * Set_join time.
	 * @param _joinTime the _joinTime to set
	 * @return true, if successful
	 */
	public static boolean set_joinTime(int _joinTime)
	{
		if (!is_inProgress())
		{
			TvT._joinTime = _joinTime;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _event time.
	 * @return the _eventTime
	 */
	public static int get_eventTime()
	{
		return _eventTime;
	}
	
	/**
	 * Set_event time.
	 * @param _eventTime the _eventTime to set
	 * @return true, if successful
	 */
	public static boolean set_eventTime(int _eventTime)
	{
		if (!is_inProgress())
		{
			TvT._eventTime = _eventTime;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _min players.
	 * @return the _minPlayers
	 */
	public static int get_minPlayers()
	{
		return _minPlayers;
	}
	
	/**
	 * Set_min players.
	 * @param _minPlayers the _minPlayers to set
	 * @return true, if successful
	 */
	public static boolean set_minPlayers(int _minPlayers)
	{
		if (!is_inProgress())
		{
			TvT._minPlayers = _minPlayers;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _max players.
	 * @return the _maxPlayers
	 */
	public static int get_maxPlayers()
	{
		return _maxPlayers;
	}
	
	/**
	 * Set_max players.
	 * @param _maxPlayers the _maxPlayers to set
	 * @return true, if successful
	 */
	public static boolean set_maxPlayers(int _maxPlayers)
	{
		if (!is_inProgress())
		{
			TvT._maxPlayers = _maxPlayers;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _interval between matchs.
	 * @return the _intervalBetweenMatchs
	 */
	public static long get_intervalBetweenMatchs()
	{
		return _intervalBetweenMatchs;
	}
	
	/**
	 * Set_interval between matchs.
	 * @param _intervalBetweenMatchs the _intervalBetweenMatchs to set
	 * @return true, if successful
	 */
	public static boolean set_intervalBetweenMatchs(long _intervalBetweenMatchs)
	{
		if (!is_inProgress())
		{
			TvT._intervalBetweenMatchs = _intervalBetweenMatchs;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the start event time.
	 * @return the startEventTime
	 */
	public String getStartEventTime()
	{
		return startEventTime;
	}
	
	/**
	 * Sets the start event time.
	 * @param startEventTime the startEventTime to set
	 * @return true, if successful
	 */
	public boolean setStartEventTime(String startEventTime)
	{
		if (!is_inProgress())
		{
			this.startEventTime = startEventTime;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is _joining.
	 * @return the _joining
	 */
	public static boolean is_joining()
	{
		return _joining;
	}
	
	/**
	 * Checks if is _teleport.
	 * @return the _teleport
	 */
	public static boolean is_teleport()
	{
		return _teleport;
	}
	
	/**
	 * Checks if is _started.
	 * @return the _started
	 */
	public static boolean is_started()
	{
		return _started;
	}
	
	/**
	 * Checks if is _aborted.
	 * @return the _aborted
	 */
	public static boolean is_aborted()
	{
		return _aborted;
	}
	
	/**
	 * Checks if is _sit forced.
	 * @return the _sitForced
	 */
	public static boolean is_sitForced()
	{
		return _sitForced;
	}
	
	/**
	 * Checks if is _in progress.
	 * @return the _inProgress
	 */
	public static boolean is_inProgress()
	{
		return _inProgress;
	}
	
	/**
	 * Check max level.
	 * @param maxlvl the maxlvl
	 * @return true, if successful
	 */
	public static boolean checkMaxLevel(int maxlvl)
	{
		if (_minlvl >= maxlvl)
			return false;
		
		return true;
	}
	
	/**
	 * Check min level.
	 * @param minlvl the minlvl
	 * @return true, if successful
	 */
	public static boolean checkMinLevel(int minlvl)
	{
		if (_maxlvl <= minlvl)
			return false;
		
		return true;
	}
	
	/**
	 * returns true if participated players is higher or equal then minimum needed players.
	 * @param players the players
	 * @return true, if successful
	 */
	public static boolean checkMinPlayers(int players)
	{
		if (_minPlayers <= players)
			return true;
		
		return false;
	}
	
	/**
	 * returns true if max players is higher or equal then participated players.
	 * @param players the players
	 * @return true, if successful
	 */
	public static boolean checkMaxPlayers(int players)
	{
		if (_maxPlayers > players)
			return true;
		
		return false;
	}
	
	/**
	 * Check start join ok.
	 * @return true, if successful
	 */
	public static boolean checkStartJoinOk()
	{
		if (_started || _teleport || _joining || _eventName.equals("") || _joiningLocationName.equals("") || _eventDesc.equals("") || _npcId == 0 || _npcX == 0 || _npcY == 0 || _npcZ == 0 || _rewardId == 0 || _rewardAmount == 0)
			return false;
		
		if (_teamEvent)
		{
			if (!checkStartJoinTeamInfo())
				return false;
		}
		else
		{
			if (!checkStartJoinPlayerInfo())
				return false;
		}
		
		if (!checkOptionalEventStartJoinOk())
			return false;
		
		return true;
	}
	
	/**
	 * Check start join team info.
	 * @return true, if successful
	 */
	private static boolean checkStartJoinTeamInfo()
	{
		
		if (_teams.size() < 2 || _teamsX.contains(0) || _teamsY.contains(0) || _teamsZ.contains(0))
			return false;
		
		return true;
		
	}
	
	/**
	 * Check start join player info.
	 * @return true, if successful
	 */
	private static boolean checkStartJoinPlayerInfo()
	{
		
		// TODO be integrated
		return true;
		
	}
	
	/**
	 * Check auto event start join ok.
	 * @return true, if successful
	 */
	protected static boolean checkAutoEventStartJoinOk()
	{
		
		if (_joinTime == 0 || _eventTime == 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Check optional event start join ok.
	 * @return true, if successful
	 */
	private static boolean checkOptionalEventStartJoinOk()
	{
		
		// TODO be integrated
		return true;
		
	}
	
	/**
	 * Sets the npc pos.
	 * @param activeChar the new npc pos
	 */
	public static void setNpcPos(Player activeChar)
	{
		_npcX = activeChar.getX();
		_npcY = activeChar.getY();
		_npcZ = activeChar.getZ();
		_npcHeading = activeChar.getHeading();
	}
	
	private static void spawnEventNpc()
	{
		NpcTemplate tmpl = NpcTable.getInstance().getTemplate(_npcId);
		
		try
		{
			_npcSpawn = new L2Spawn(tmpl);
			_npcSpawn.setLoc(_npcX, _npcY, _npcZ, _npcHeading);
			_npcSpawn.setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
			
			_npcSpawn.setRespawnState(true);
			_npcSpawn.doSpawn(false);
			_npcSpawn.getNpc().getStatus().setCurrentHp(999999999);
			_npcSpawn.getNpc()._isEventMobTvT = true;
			_npcSpawn.getNpc().setTitle(_eventName);
			_npcSpawn.getNpc().isAggressive();
			_npcSpawn.getNpc().decayMe();
			_npcSpawn.getNpc().spawnMe(_npcSpawn.getNpc().getX(), _npcSpawn.getNpc().getY(), _npcSpawn.getNpc().getZ());
			_npcSpawn.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn.getNpc(), _npcSpawn.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			_log.info("Engine[spawnEventNpc(exception: " + e.getMessage());
		}
	}
	
	/**
	 * Unspawn event npc.
	 */
	private static void unspawnEventNpc()
	{
		if (_npcSpawn == null || _npcSpawn.getNpc() == null)
			return;
		
		_npcSpawn.getNpc().deleteMe();
		_npcSpawn.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn, true);
	}
	
	/**
	 * Start join.
	 * @return true, if successful
	 */
	public static boolean startJoin()
	{
		if (!checkStartJoinOk())
		{
			_log.info("Engine[startJoin]: startJoinOk() = false");
			return false;
		}
		
		_inProgress = true;
		_joining = true;
		spawnEventNpc();
		
		Announcement.TvTAnnounce(""+Config.NAME_TVT+" " + _eventDesc + "!");
		
		Announcement.AnnounceEvents(""+Config.NAME_TVT+" Joinable in " + _joiningLocationName);
		
		if (Config.TVT_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
		
		if (Config.TVT_ANNOUNCE_LVL)
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Min Level: " + _minlvl);
		
		if (Config.TVT_COMMAND)
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Commands .tvtjoin .tvtleave");
		
		if (Config.ALLOW_PHANTOM_PLAYERS_TVT)
		{
		Phantom_TvT.init();
		}
		
		return true;
	}
	
	/**
	 * Start teleport.
	 * @return true, if successful
	 */
	public static boolean startTeleport()
	{
		if (!_joining || _started || _teleport)
			return false;
		
		removeOfflinePlayers();
		
		if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE") && checkMinPlayers(_playersShuffle.size()))
		{
			shuffleTeams();
		}
		else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE") && !checkMinPlayers(_playersShuffle.size()))
		{
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Not enough players . Min : " + _minPlayers + ", Reg : " + _playersShuffle.size());
			TvTEventManager.getInstance().StartCalculationOfNextEventTime();
			
			TvTEventManager.getInstance().getNextTime();
			return false;
		}
		
		_joining = false;
		Announcement.AnnounceEvents(""+Config.NAME_TVT+" Teleport to team spot in 20 seconds!");
		
		setUserData();
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				for (final Player player : World.getInstance().getPlayers())
				{
					if (player != null && player.isOnline() && player._inEventTvT)
					{
						if ((player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.ELVEN_ELDER) && !player.isGM() && !player.isPhantom())
							player.logout(true);
						
						if (Config.TVT_ON_START_REMOVE_ALL_EFFECTS)
							player.stopAllEffects();
						
						if (player.isDead())
							player.doRevive();
						
						player.teleToLocation(_teamsX.get(_teams.indexOf(player._teamNameTvT)), _teamsY.get(_teams.indexOf(player._teamNameTvT)), _teamsZ.get(_teams.indexOf(player._teamNameTvT)), 200);
						
						if (player.isPhantom())
							player.setLastCords(player.getX(), player.getY(), player.getZ());
						
					}
					
				}
				
				sit();
				
			}
		}, 20000);
		_teleport = true;
		return true;
	}
	
	/**
	 * Start event.
	 * @return true, if successful
	 */
	public static boolean startEvent()
	{
		if (!startEventOk())
		{
			_log.info("Engine[startEvent()]: startEventOk() = false");
			return false;
		}
		
		_teleport = false;
		
	    if (Config.TVT_CLOSE_FORT_DOORS)
	        closeFortDoors(); 
	      if (Config.TVT_CLOSE_ADEN_COLOSSEUM_DOORS)
	        closeAdenColosseumDoors(); 
	      
		Announcement.AnnounceEvents(""+Config.NAME_TVT+" Started. Go to kill your enemies!");
		
		_started = true;
		
		sit();
		
		for (Player player : _players)
		{
			if (player != null && player.isOnline())
			{
				if (player.getKarma() > 0)
					player.setKarma(0);
				
				if (Config.SCREN_MSG)
					player.sendPacket(new ExShowScreenMessage("Started. Good Fight!", 6 * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				
				if (player.isDead())
					player.doRevive();
				
				player.getStatus().setCurrentHp(player.getMaxHp());
				player.getStatus().setCurrentMp(player.getMaxMp());
				player.getStatus().setCurrentCp(player.getMaxCp());
				
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
						CreatureSay cs = new CreatureSay(player.getObjectId(), 2, "TvT", "Anti AFK active!"); // 8D
						player.sendPacket(cs);
						player.setKickProtection(true);
						
						if (player.getMountType() != 0)
							player.dismount();
						
					}
				}, 5000);
				
				if (player.isPhantomMysticMuseTvT() || player.isPhantomStormScreamTvT() || player.isPhantomArchMageTvT())
				{
					ThreadPool.schedule(new Runnable()
					{
						
						@Override
						public void run()
						{
							if (!player.isDead())
								Phantom_TvT.doCastlist(player);
						}
					}, Rnd.get(2100, 5200));
				}
			}
		}
		
		// for (final Player player : World.getInstance().getPlayers())
		// {
		// if (player.isOnline() && !player.isInsideZone(ZoneId.TOURNAMENT) && !player.isInsideZone(ZoneId.ARENA_EVENT) && !player.isArenaProtection() && !player.isInObserverMode() && !player._inEventTvT && player.getKarma() == 0)
		// player.sendPacket(new TutorialShowHtml(HtmCache.getInstance().getHtmForce("data/html/mods/tvt.htm")));
		// }
		
		AntiAfkTvt.getInstance();
		
		return true;
	}
	
	/**
	 * Finish event.
	 */
	public static void finishEvent()
	{
		if (!finishEventOk())
		{
			_log.info("Engine[finishEvent]: finishEventOk() = false");
			return;
		}
		
		_started = false;
		_aborted = false;
		
		unspawnEventNpc();
		
		if (_teamEvent)
		{
			processTopTeam();
			synchronized (_players)
			{
				Player bestKiller = findBestKiller(_players);
				
				if (_topKills != 0)
				{
					
					playKneelAnimation(_topTeam);
					
					if (Config.TVT_ANNOUNCE_TEAM_STATS)
					{
						Announcement.AnnounceEvents(""+Config.NAME_TVT+" Team Statistics:");
						for (String team : _teams)
						{
							int _kills = teamKillsCount(team);
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" Team: " + team + " - Kills: " + _kills);
						}
						
						if (bestKiller != null)
						{
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" Top killer: " + bestKiller.getName() + " - Kills: " + bestKiller._countTvTkills);
							
							if (Config.SCREN_MSG)
							{
								for (Player player : World.getInstance().getPlayers())
								{
									if (player != null && !player.getFirstLog())
										player.sendPacket(new ExShowScreenMessage(""+Config.NAME_TVT+" Top killer: " + bestKiller.getName() + " - Kills: " + bestKiller._countTvTkills, 6000));
								}
							}
						}
					}
					
					if (_topTeam != null)
					{
						Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + _topTeam + "'s win the match! " + _topKills + " kills.");
					}
					else
					{
						Announcement.AnnounceEvents(""+Config.NAME_TVT+" The event finished with a TIE: " + _topKills + " kills by each team!");
					}
					
					rewardTeam(_topTeam, bestKiller);
					
					if (Config.TVT_STATS_LOGGER)
					{
						_log.info("**** " + _eventName + " ****");
						_log.info(_eventName + " Team Statistics:");
						for (String team : _teams)
						{
							int _kills = teamKillsCount(team);
							_log.info(_eventName + ": Team: " + team + " - Kills: " + _kills);
						}
						
						if (bestKiller != null)
						{
							_log.info(_eventName + ": Top killer: " + bestKiller.getName() + " - Kills: " + bestKiller._countTvTkills);
						}
						
						_log.info(_eventName + ": " + _topTeam + "'s win the match! " + _topKills + " kills.");
						
					}
					
				}
				else
				{
					Announcement.AnnounceEvents(""+Config.NAME_TVT+" The event finished with a TIE: No team wins the match(nobody killed)!");
					_log.info(_eventName + ": No team win the match(nobody killed).");
					rewardTeam(_topTeam, bestKiller);
				}
			}
		}
		else
		{
			processTopPlayer();
		}
		
		teleportFinish();
	}
	
	/**
	 * Abort event.
	 */
	public static void abortEvent()
	{
		if (!_joining && !_teleport && !_started)
			return;
		
		if (_joining && !_teleport && !_started)
		{
			unspawnEventNpc();
			cleanTvT();
			_joining = false;
			_inProgress = false;
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Match aborted!");
			return;
		}
		_joining = false;
		_teleport = false;
		_started = false;
		_aborted = true;
		unspawnEventNpc();
		Announcement.AnnounceEvents(""+Config.NAME_TVT+" Match aborted!");
		teleportFinish();
	}
	
	/**
	 * Teleport finish.
	 */
	public static void teleportFinish()
	{
		TvTEventManager.getInstance().StartCalculationOfNextEventTime();
		
		TvTEventManager.getInstance().getNextTime();
		
		sit();
		
		Announcement.AnnounceEvents(""+Config.NAME_TVT+" Teleport back in 20 seconds!");
		
		for (final Player player : _players)
		{
			if (player != null && player.isOnline())
			{
				if (player.isKickProtection())
					player.setKickProtection(false);
				
				if (player.isDead())
					player.doRevive();
				
				player._reuseTimeStamps.clear();
				player.getDisabledSkills().clear();
				player.sendPacket(new SkillCoolTime(player));
				
			}
		}
		
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized (_players)
				{
					
					sit();
					
					for (Player player : _players)
					{
						if (player != null)
						{
							if (player.isOnline())
							{
								if (player.isPhantom())
									player.teleToLocation(0, 0, 0, 50);
								else
									player.teleToLocation(player.getLastX(), player.getLastY(), player.getLastZ(), 50);
								
								if (player.isPhantom())
								{
									PhantomOld fakePlayer = (PhantomOld) player;
									fakePlayer.despawnPlayer();
								}
							}
							else
							{
								java.sql.Connection con = null;
								try
								{
									con = L2DatabaseFactory.getInstance().getConnection();
									
									PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE char_name=?");
									statement.setInt(1, player.getLastX());
									statement.setInt(2, player.getLastY());
									statement.setInt(3, player.getLastZ());
									statement.setString(4, player.getName());
									statement.execute();
									statement.close();
								}
								catch (Exception e)
								{
									_log.info("teleportFinish() =  " + e);
								}
								finally
								{
									CloseUtil.close(con);
									con = null;
								}
							}
						}
					}
				}
				
				cleanTvT();
			}
		}, 20000);
	}
	
	protected static class AutoEventTask implements Runnable
	{
		@Override
		public void run()
		{
			_log.info("Starting " + _eventName + "!");
			_log.info("Matchs Are Restarted At Every: " + getIntervalBetweenMatchs() + " Minutes.");
			if (checkAutoEventStartJoinOk() && startJoin() && !_aborted)
			{
				if (_joinTime > 0)
					waiter(_joinTime * 60 * 1000); // minutes for join event
				else if (_joinTime <= 0)
				{
					_log.info(_eventName + ": join time <=0 aborting event.");
					abortEvent();
					return;
				}
				if (startTeleport() && !_aborted)
				{
					waiter(30 * 1000); // 30 sec wait time until start fight after teleported
					if (startEvent() && !_aborted)
					{
						_log.log(Level.WARNING, _eventName + ": waiting.....minutes for event time " + _eventTime);
						
						waiter(_eventTime * 60 * 1000); // minutes for event time
						finishEvent();
						
						_log.info(_eventName + ": waiting... delay for final messages ");
						waiter(60000);// just a give a delay delay for final messages
						sendFinalMessages();
					}
				}
				else if (!_aborted)
				{
					abortEvent();
				}
			}
		}
		
	}
	
	/**
	 * Auto event.
	 */
	public static void autoEvent()
	{
		ThreadPool.execute(new AutoEventTask());
	}
	
	// start without restart
	/**
	 * Event once start.
	 */
	public static void eventOnceStart()
	{
		
		if (startJoin() && !_aborted)
		{
			if (_joinTime > 0)
				waiter(_joinTime * 60 * 1000); // minutes for join event
			else if (_joinTime <= 0)
			{
				abortEvent();
				return;
			}
			if (startTeleport() && !_aborted)
			{
				waiter(1 * 60 * 1000); // 1 min wait time untill start fight after teleported
				if (startEvent() && !_aborted)
				{
					waiter(_eventTime * 60 * 1000); // minutes for event time
					finishEvent();
				}
			}
			else if (!_aborted)
			{
				abortEvent();
			}
		}
		
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	protected static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && !_aborted)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			if (_joining || _started || _teleport)
			{
				switch (seconds)
				{
					case 3600: // 1 hour left
						removeOfflinePlayers();
						
						if (_joining)
						{
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" Joinable in " + _joiningLocationName + "!");
							
							if (Config.TVT_ANNOUNCE_LVL)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Min Level: " + _minlvl);
							
							if (Config.TVT_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
							
							if (Config.TVT_COMMAND)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Commands .tvtjoin .tvtleave");
							
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds / 60 / 60 + " hour(s) till registration close!");
						}
						else if (_started)
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds / 60 / 60 + " hour(s) till event finish!");
						
						break;
					case 1800: // 30 minutes left
					case 900: // 15 minutes left
					case 600: // 10 minutes left
					case 300: // 5 minutes left
					case 240: // 4 minutes left
					case 180: // 3 minutes left
					case 120: // 2 minutes left
					case 60: // 1 minute left
						// removeOfflinePlayers();
						
						if (_joining)
						{
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" Joinable in " + _joiningLocationName + "!");
							
							if (Config.TVT_ANNOUNCE_LVL)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Min Level: " + _minlvl);
							
							if (Config.TVT_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
							
							if (Config.TVT_COMMAND)
								Announcement.AnnounceEvents(""+Config.NAME_TVT+" Commands .tvtjoin .tvtleave");
							
							Announcement.TvTAnnounce(""+Config.NAME_TVT+" " + seconds / 60 + " minute(s) till registration close!");
						}
						else if (_started)
						{
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds / 60 + " minute(s) till event finish!");
							Announce("**** Team Statistics ****");
							for (String team : _teams)
							{
								int _kills = teamKillsCount(team);
								Announce("Team: " + team + " - Kills: " + _kills);
							}
						}
						break;
					case 30: // 30 seconds left
					case 15: // 15 seconds left
					case 10: // 10 seconds left
						removeOfflinePlayers();
					case 3: // 3 seconds left
					case 2: // 2 seconds left
					case 1: // 1 seconds left
						
						if (_joining)
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds + " second(s) till registration close!");
						else if (_teleport)
						{
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds + " seconds(s) till start fight!");
							if (Config.SCREN_MSG)
							{
								for (Player player : _players)
									player.sendPacket(new ExShowScreenMessage(seconds + " seconds(s) till start fight!", 5000));
							}
						}
						else if (_started)
							Announcement.AnnounceEvents(""+Config.NAME_TVT+" " + seconds + " second(s) till event finish!");
						
						break;
				}
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
	
	public static void sit()
	{
		if (_sitForced)
			_sitForced = false;
		else
			_sitForced = true;
		
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (_sitForced)
				{
					player.stopMove(null);
					player.abortAttack();
					player.abortCast();
					player.setTarget(null);
					player.setIsInvul(true);
					player.setStopArena(true);
				}
				else
				{
					player.setIsInvul(false);
					player.setStopArena(false);
				}
			}
		}
	}
	
	/**
	 * Removes the offline players.
	 */
	public static void removeOfflinePlayers()
	{
		try
		{
			if (_playersShuffle == null || _playersShuffle.isEmpty())
				return;
			else if (_playersShuffle.size() > 0)
			{
				
				for (Player player : _playersShuffle)
				{
					if (player == null)
						_playersShuffle.remove(player);
					else if (!player.isOnline() || player.isInJail())
						removePlayer(player);
					if (_playersShuffle.size() == 0 || _playersShuffle.isEmpty())
						break;
				}
			}
		}
		catch (Exception e)
		{
			_log.info("removeOfflinePlayers() =  " + e);
		}
	}
	
	/**
	 * Start event ok.
	 * @return true, if successful
	 */
	private static boolean startEventOk()
	{
		if (_joining || !_teleport || _started)
			return false;
		
		if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
		{
			if (_teamPlayersCount.contains(0))
				return false;
		}
		else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
		{
			ArrayList<Player> playersShuffleTemp = new ArrayList<>();
			int loopCount = 0;
			
			loopCount = _playersShuffle.size();
			
			for (int i = 0; i < loopCount; i++)
			{
				playersShuffleTemp.add(_playersShuffle.get(i));
			}
			
			_playersShuffle = playersShuffleTemp;
			playersShuffleTemp.clear();
		}
		return true;
	}
	
	/**
	 * Finish event ok.
	 * @return true, if successful
	 */
	private static boolean finishEventOk()
	{
		if (!_started)
			return false;
		
		return true;
	}
	
	/**
	 * Adds the player ok.
	 * @param teamName the team name
	 * @param eventPlayer the event player
	 * @return true, if successful
	 */
	private static boolean addPlayerOk(String teamName, Player eventPlayer)
	{
		if (!Config.Allow_Same_HWID_On_tvt && !eventPlayer.isGM() && !eventPlayer.isPhantom())
		{
			String hwid = eventPlayer.getHWID();
			if (_playersShuffle != null && !_playersShuffle.isEmpty())
			{
				for (Player p : _playersShuffle)
				{
					if (p != null && p.getHWID().equalsIgnoreCase(hwid))
					{
						eventPlayer.sendMessage("You have already joined the event with another character.");
						return false;
					}
				}
			}
		}
		
	    if (eventPlayer.isAio() || AioManager.getInstance().hasAioPrivileges(eventPlayer.getObjectId())) {
	        eventPlayer.sendMessage("SYS: AIO charactes are not allowed to participate in events :/");
	        return false;
	      } 
		
		if (!checkMaxPlayers(_playersShuffle.size()))
		{
			eventPlayer.sendMessage("Player limit exceeded, you cannot attend the event.");
			return false;
		}
		else if (eventPlayer.isInObserverMode())
		{
			eventPlayer.sendMessage("You can not do this in ObserverMode!");
			return false;
		}
		else if (checkShufflePlayers(eventPlayer) || eventPlayer._inEventTvT)
		{
			eventPlayer.sendMessage("You already participated in the event!");
			return false;
		}
		else if (eventPlayer._inEventCTF)
		{
			eventPlayer.sendMessage("You already participated in another event!");
			return false;
		}
		else if (OlympiadManager.getInstance().isRegisteredInComp(eventPlayer) || eventPlayer.isInOlympiadMode() || eventPlayer.getOlympiadGameId() > 0)
		{
			eventPlayer.sendMessage("You can't register while you are in olympiad!");
			return false;
		}
		else if ((eventPlayer.getClassId() == ClassId.BISHOP || eventPlayer.getClassId() == ClassId.CARDINAL || eventPlayer.getClassId() == ClassId.SHILLIEN_ELDER || eventPlayer.getClassId() == ClassId.SHILLIEN_SAINT || eventPlayer.getClassId() == ClassId.EVAS_SAINT || eventPlayer.getClassId() == ClassId.ELVEN_ELDER) && !eventPlayer.isGM())
		{
			eventPlayer.sendMessage("Classe nao permitida em eventos.");
			return false;
		}
		
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (player.getObjectId() == eventPlayer.getObjectId())
				{
					eventPlayer.sendMessage("You already participated in the event!");
					return false;
				}
				else if (player.getName().equalsIgnoreCase(eventPlayer.getName()))
				{
					eventPlayer.sendMessage("You already participated in the event!");
					return false;
				}
				
			}
			
			if (_players.contains(eventPlayer))
			{
				eventPlayer.sendMessage("You already participated in the event!");
				return false;
			}
		}
		
		if (Config.TVT_EVEN_TEAMS.equals("NO"))
			return true;
		else if (Config.TVT_EVEN_TEAMS.equals("BALANCE"))
		{
			boolean allTeamsEqual = true;
			int countBefore = -1;
			
			for (int playersCount : _teamPlayersCount)
			{
				if (countBefore == -1)
					countBefore = playersCount;
				
				if (countBefore != playersCount)
				{
					allTeamsEqual = false;
					break;
				}
				
				countBefore = playersCount;
			}
			
			if (allTeamsEqual)
				return true;
			
			countBefore = Integer.MAX_VALUE;
			
			for (int teamPlayerCount : _teamPlayersCount)
			{
				if (teamPlayerCount < countBefore)
					countBefore = teamPlayerCount;
			}
			
			ArrayList<String> joinableTeams = new ArrayList<>();
			
			for (String team : _teams)
			{
				if (teamPlayersCount(team) == countBefore)
					joinableTeams.add(team);
			}
			
			if (joinableTeams.contains(teamName))
				return true;
		}
		else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
			return true;
		
		eventPlayer.sendMessage("Too many players in team \"" + teamName + "\"");
		return false;
	}
	
	/**
	 * Sets the user data.
	 */
	public static void setUserData()
	{
		synchronized (_players)
		{
			for (Player player : _players)
			{
				player._originalNameColorTvT = player.getAppearance().getNameColor();
				player._originalKarmaTvT = player.getKarma();
				player._originalTitleTvT = player.getTitle();
				player.setLastCords(player.getX(), player.getY(), player.getZ());
				
				player.getAppearance().setNameColor(_teamColors.get(_teams.indexOf(player._teamNameTvT)));
				player.setKarma(0);
				player.setTitle("Kills: " + player._countTvTkills);
				if (Config.TVT_AURA)
				{
					if (_teams.size() >= 2)
						player.setTeam(_teams.indexOf(player._teamNameTvT) + 1);
				}
				
				if (player.isDead())
					player.doRevive();
				
				if (Config.TVT_SKILL_PROTECT)
				{
					for (L2Effect effect : player.getAllEffects())
					{
						if (Config.TVT_SKILL_LIST.contains(effect.getSkill().getId()))
							player.stopSkillEffects(effect.getSkill().getId());
					}
				}
				
				if (Config.TVT_ON_START_UNSUMMON_PET)
				{
					// Remove Summon's buffs
					if (player.getPet() != null)
					{
						Summon summon = player.getPet();
						
						if (summon != null)
							summon.unSummon(summon.getOwner());
						
						if (summon instanceof Pet)
							summon.unSummon(player);
					}
					
				}
				
				// Remove player from his party
				if (player.getParty() != null)
				{
					L2Party party = player.getParty();
					party.removePartyMember(player, null);
				}
				
				if (player.getMountType() != 0)
					player.dismount();
				
				player.broadcastUserInfo();
			}
		}
		
	}
	
	/**
	 * Dump data.
	 */
	public static void dumpData()
	{
		_log.info("");
		
		if (!_joining && !_teleport && !_started)
		{
			_log.info("<<---------------------------------->>");
			_log.info(">> " + _eventName + " Engine infos dump (INACTIVE) <<");
			_log.info("<<--^----^^-----^----^^------^^----->>");
		}
		else if (_joining && !_teleport && !_started)
		{
			_log.info("<<--------------------------------->>");
			_log.info(">> " + _eventName + " Engine infos dump (JOINING) <<");
			_log.info("<<--^----^^-----^----^^------^----->>");
		}
		else if (!_joining && _teleport && !_started)
		{
			_log.info("<<---------------------------------->>");
			_log.info(">> " + _eventName + " Engine infos dump (TELEPORT) <<");
			_log.info("<<--^----^^-----^----^^------^^----->>");
		}
		else if (!_joining && !_teleport && _started)
		{
			_log.info("<<--------------------------------->>");
			_log.info(">> " + _eventName + " Engine infos dump (STARTED) <<");
			_log.info("<<--^----^^-----^----^^------^----->>");
		}
		
		_log.info("Name: " + _eventName);
		_log.info("Desc: " + _eventDesc);
		_log.info("Join location: " + _joiningLocationName);
		_log.info("Min lvl: " + _minlvl);
		_log.info("Max lvl: " + _maxlvl);
		_log.info("");
		_log.info("##########################");
		_log.info("# _teams(ArrayList<String>) #");
		_log.info("##########################");
		
		for (String team : _teams)
			_log.info(team + " Kills Done :" + _teamPointsCount.get(_teams.indexOf(team)));
		
		if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
		{
			_log.info("");
			_log.info("#########################################");
			_log.info("# _playersShuffle(ArrayList<Player>) #");
			_log.info("#########################################");
			
			for (Player player : _playersShuffle)
			{
				if (player != null && player.isOnline())
					_log.info("Name: " + player.getName());
			}
		}
		
		_log.info("");
		_log.info("##################################");
		_log.info("# _players(ArrayList<Player>) #");
		_log.info("##################################");
		
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (player != null && player.isOnline())
					_log.info("Name: " + player.getName() + "   Team: " + player._teamNameTvT + "  Kills Done:" + player._countTvTkills);
			}
		}
		
		_log.info("");
		_log.info("#####################################################################");
		_log.info("# _savePlayers(ArrayList<String>) and _savePlayerTeams(ArrayList<String>) #");
		_log.info("#####################################################################");
		
		for (String player : _savePlayers)
			_log.info("Name: " + player + "	Team: " + _savePlayerTeams.get(_savePlayers.indexOf(player)));
		
		_log.info("");
		
		dumpLocalEventInfo();
		
	}
	
	/**
	 * Dump local event info.
	 */
	private static void dumpLocalEventInfo()
	{
		
	}
	
	/**
	 * Load data.
	 */
	@SuppressWarnings("resource")
	public static void loadData()
	{
		_eventName = new String();
		_eventDesc = new String();
		_joiningLocationName = new String();
		_savePlayers = new ArrayList<>();
		_players = new ArrayList<>();
		
		_topTeam = new String();
		_teams = new ArrayList<>();
		_savePlayerTeams = new ArrayList<>();
		_playersShuffle = new ArrayList<>();
		_teamPlayersCount = new ArrayList<>();
		_teamPointsCount = new ArrayList<>();
		_teamColors = new ArrayList<>();
		_teamsX = new ArrayList<>();
		_teamsY = new ArrayList<>();
		_teamsZ = new ArrayList<>();
		
		_joining = false;
		_teleport = false;
		_started = false;
		_sitForced = false;
		_aborted = false;
		_inProgress = false;
		
		_npcId = 0;
		_npcX = 0;
		_npcY = 0;
		_npcZ = 0;
		_npcHeading = 0;
		_rewardId = 0;
		_rewardAmount = 0;
		_topKills = 0;
		_minlvl = 0;
		_maxlvl = 0;
		_joinTime = 0;
		_eventTime = 0;
		_minPlayers = 0;
		_maxPlayers = 0;
		_intervalBetweenMatchs = 0;
		
		java.sql.Connection con = null;
		try
		{
			PreparedStatement statement;
			ResultSet rs;
			
			con = L2DatabaseFactory.getInstance().getConnection();
			
			statement = con.prepareStatement("Select * from tvt");
			rs = statement.executeQuery();
			
			int teams = 0;
			
			while (rs.next())
			{
				_eventName = rs.getString("eventName");
				_eventDesc = rs.getString("eventDesc");
				_joiningLocationName = rs.getString("joiningLocation");
				_minlvl = rs.getInt("minlvl");
				_maxlvl = rs.getInt("maxlvl");
				_npcId = rs.getInt("npcId");
				_npcX = rs.getInt("npcX");
				_npcY = rs.getInt("npcY");
				_npcZ = rs.getInt("npcZ");
				_npcHeading = rs.getInt("npcHeading");
				_rewardId = rs.getInt("rewardId");
				_rewardAmount = rs.getInt("rewardAmount");
				teams = rs.getInt("teamsCount");
				_joinTime = rs.getInt("joinTime");
				_eventTime = rs.getInt("eventTime");
				_minPlayers = rs.getInt("minPlayers");
				_maxPlayers = rs.getInt("maxPlayers");
				_intervalBetweenMatchs = rs.getLong("delayForNextEvent");
			}
			statement.close();
			
			int index = -1;
			if (teams > 0)
				index = 0;
			while (index < teams && index > -1)
			{
				statement = con.prepareStatement("Select * from tvt_teams where teamId = ?");
				statement.setInt(1, index);
				rs = statement.executeQuery();
				while (rs.next())
				{
					_teams.add(rs.getString("teamName"));
					_teamPlayersCount.add(0);
					_teamPointsCount.add(0);
					_teamColors.add(0);
					_teamsX.add(0);
					_teamsY.add(0);
					_teamsZ.add(0);
					_teamsX.set(index, rs.getInt("teamX"));
					_teamsY.set(index, rs.getInt("teamY"));
					_teamsZ.set(index, rs.getInt("teamZ"));
					_teamColors.set(index, rs.getInt("teamColor"));
					
				}
				index++;
				statement.close();
			}
		}
		catch (Exception e)
		{
			_log.info("Exception: loadData(): " + e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}
	
	/**
	 * Save data.
	 */
	public static void saveData()
	{
		java.sql.Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			
			statement = con.prepareStatement("Delete from tvt");
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("INSERT INTO tvt (eventName, eventDesc, joiningLocation, minlvl, maxlvl, npcId, npcX, npcY, npcZ, npcHeading, rewardId, rewardAmount, teamsCount, joinTime, eventTime, minPlayers, maxPlayers,delayForNextEvent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");
			statement.setString(1, _eventName);
			statement.setString(2, _eventDesc);
			statement.setString(3, _joiningLocationName);
			statement.setInt(4, _minlvl);
			statement.setInt(5, _maxlvl);
			statement.setInt(6, _npcId);
			statement.setInt(7, _npcX);
			statement.setInt(8, _npcY);
			statement.setInt(9, _npcZ);
			statement.setInt(10, _npcHeading);
			statement.setInt(11, _rewardId);
			statement.setInt(12, _rewardAmount);
			statement.setInt(13, _teams.size());
			statement.setInt(14, _joinTime);
			statement.setInt(15, _eventTime);
			statement.setInt(16, _minPlayers);
			statement.setInt(17, _maxPlayers);
			statement.setLong(18, _intervalBetweenMatchs);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("Delete from tvt_teams");
			statement.execute();
			statement.close();
			
			for (String teamName : _teams)
			{
				int index = _teams.indexOf(teamName);
				
				if (index == -1)
				{
					CloseUtil.close(con);
					con = null;
					return;
				}
				statement = con.prepareStatement("INSERT INTO tvt_teams (teamId ,teamName, teamX, teamY, teamZ, teamColor) VALUES (?, ?, ?, ?, ?, ?)");
				statement.setInt(1, index);
				statement.setString(2, teamName);
				statement.setInt(3, _teamsX.get(index));
				statement.setInt(4, _teamsY.get(index));
				statement.setInt(5, _teamsZ.get(index));
				statement.setInt(6, _teamColors.get(index));
				
				statement.execute();
				statement.close();
			}
		}
		catch (Exception e)
		{
			_log.info("Exception: saveData(): " + e);
		}
		finally
		{
			CloseUtil.close(con);
			con = null;
		}
	}
	
	/**
	 * Show event html.
	 * @param eventPlayer the event player
	 * @param objectId the object id
	 */
	public static void showEventHtml(Player eventPlayer, String objectId)
	{
		try
		{
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			
			StringBuilder replyMSG = new StringBuilder("<html><title>TvT</title><body>");
			replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
			replyMSG.append("<center><font color=\"LEVEL\">Current event:</font></center><br1>");
			replyMSG.append("<center>Name: &nbsp;<font color=\"1E90FF\">" + _eventName + "</font></center><br1>");
			replyMSG.append("<center>Description:&nbsp;<font color=\"1E90FF\">" + _eventDesc + "</font></center><br>");
			
			if (!_started && !_joining)
			{
				replyMSG.append("<center>Wait till the admin/gm start the participation.</center>");
				if (!eventPlayer._inEventTvT)
				{
					replyMSG.append("<br><br>");
					replyMSG.append("<center><button value=\"Watch event\" action=\"bypass -h npc_" + objectId + "_tvt_watch\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
					replyMSG.append("<br>");
				}
			}
			else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE") && !checkMaxPlayers(_playersShuffle.size()))
			{
				if (!_started)
				{
					replyMSG.append("Currently participated: <font color=\"1E90FF\">" + _playersShuffle.size() + ".</font><br>");
					replyMSG.append("Max players: <font color=\"1E90FF\">" + _maxPlayers + "</font><br>");
					replyMSG.append("<font color=\"1E90FF\">You can't participate to this event.</font><br>");
				}
			}
			else if (eventPlayer.isCursedWeaponEquipped() && !Config.TVT_JOIN_CURSED)
			{
				replyMSG.append("<font color=\"1E90FF\">You can't participate to this event with a cursed Weapon.</font><br>");
			}
			else if (!_started && _joining && eventPlayer.getLevel() >= _minlvl && eventPlayer.getLevel() <= _maxlvl)
			{
				synchronized (_players)
				{
					if (_players.contains(eventPlayer) || _playersShuffle.contains(eventPlayer) || checkShufflePlayers(eventPlayer))
					{
						if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
							replyMSG.append("You participated already in team <font color=\"1E90FF\">" + eventPlayer._teamNameTvT + "</font><br><br>");
						else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
							replyMSG.append("<center><font color=\"LEVEL\">You participated already!</font></center><br>");
						
						replyMSG.append("<center>Joined Players: <font color=\"1E90FF\">" + _playersShuffle.size() + "</font></center><br>");
						
						replyMSG.append("<center><button value=\"Remove\" action=\"bypass -h npc_" + objectId + "_tvt_player_leave\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
						replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
					}
					else
					{
						replyMSG.append("<center><font color=\"LEVEL\">You want to participate in the event?</font></center><br>");
						replyMSG.append("<center><td width=\"200\">Min. level: <font color=\"1E90FF\">" + _minlvl + "</font></center></td><br>");
						replyMSG.append("<center><td width=\"200\">Max. level: <font color=\"1E90FF\">" + _maxlvl + "</font></center></td><br>");
						replyMSG.append("<center><font color=\"LEVEL\">Teams: </font></center>");
						
						if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
						{
							replyMSG.append("<center><table border=\"0\">");
							
							for (String team : _teams)
							{
								replyMSG.append("<tr><td width=\"100\"><font color=\"1E90FF\">" + team + "</font>&nbsp;(" + teamPlayersCount(team) + " joined.)</td>");
								replyMSG.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_" + objectId + "_tvt_player_join " + team + "\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center></td></tr>");
							}
							replyMSG.append("</table></center>");
						}
						else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
						{
							replyMSG.append("<center>");
							
							for (String team : _teams)
								replyMSG.append("<tr><td width=\"100\"><font color=\"1E90FF\">" + team + "</font> &nbsp;</td>");
							
							replyMSG.append("</center><br>");
							
							replyMSG.append("<center><button value=\"Join Event\" action=\"bypass -h npc_" + objectId + "_tvt_player_join eventShuffle\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
							replyMSG.append("<center><font color=\"1E90FF\">Teams will be reandomly generated!</font></center><br>");
							replyMSG.append("<center>Joined Players: </font><font color=\"1E90FF\">" + _playersShuffle.size() + "</center></font><br>");
							
							replyMSG.append("<center>Reward: <font color=\"LEVEL\">" + _rewardAmount + " " + ItemTable.getInstance().getTemplate(_rewardId).getName() + "</font></center>");
							
							replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
							
						}
					}
				}
				
			}
			else if (_started && !_joining)
			{
				replyMSG.append("<center>" + _eventName + " match is in progress.</center>");
				if (!eventPlayer._inEventTvT)
				{
					replyMSG.append("<br><br>");
					replyMSG.append("<center><button value=\"Watch event\" action=\"bypass -h npc_" + objectId + "_tvt_watch\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
					replyMSG.append("<br>");
				}
			}
			else if (eventPlayer.getLevel() < _minlvl || eventPlayer.getLevel() > _maxlvl)
			{
				replyMSG.append("<center>Your level: <font color=\"1E90FF\">" + eventPlayer.getLevel() + "</font><br>");
				replyMSG.append("<center>Min. level: <font color=\"1E90FF\">" + _minlvl + "</font><br>");
				replyMSG.append("<center>Max. level: <font color=\"1E90FF\">" + _maxlvl + "</font><br><br>");
				replyMSG.append("<center><font color=\"1E90FF\">You can't participate to this event.</font><br>");
				replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
			}
			
			replyMSG.append("</body></html>");
			adminReply.setHtml(replyMSG.toString());
			eventPlayer.sendPacket(adminReply);
			
			// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
			eventPlayer.sendPacket(ActionFailed.STATIC_PACKET);
		}
		catch (Exception e)
		{
			_log.info("Engine[showEventHtlm(" + eventPlayer.getName() + ", " + objectId + ")]: exception" + e.getMessage());
		}
	}
	
	/**
	 * Adds the player.
	 * @param player the player
	 * @param teamName the team name
	 */
	public static void addPlayer(Player player, String teamName)
	{
		if (!addPlayerOk(teamName, player))
			return;
		
		synchronized (_players)
		{
			if (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE"))
			{
				player._teamNameTvT = teamName;
				_players.add(player);
				setTeamPlayersCount(teamName, teamPlayersCount(teamName) + 1);
			}
			else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE"))
				_playersShuffle.add(player);
		}
		
		player._inEventTvT = true;
		player._countTvTkills = 0;
		player.sendMessage("Your participation in the TvT event has been approved.");
	}
	
	/**
	 * Removes the player.
	 * @param player the player
	 */
	public static void removePlayer(Player player)
	{
		if (player._inEventTvT)
		{
			if (!_joining)
			{
				player.getAppearance().setNameColor(player._originalNameColorTvT);
				player.setTitle(player._originalTitleTvT);
				player.setKarma(player._originalKarmaTvT);
				if (Config.TVT_AURA)
				{
					if (_teams.size() >= 2)
						player.setTeam(0);// clear aura :P
				}
				player.broadcastUserInfo();
			}
			
			// after remove, all event data must be cleaned in player
			player._originalNameColorTvT = 0;
			player._originalTitleTvT = null;
			player._originalKarmaTvT = 0;
			player._teamNameTvT = new String();
			player._countTvTkills = 0;
			player._inEventTvT = false;
			
			synchronized (_players)
			{
				if ((Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE")) && _players.contains(player))
				{
					setTeamPlayersCount(player._teamNameTvT, teamPlayersCount(player._teamNameTvT) - 1);
					_players.remove(player);
				}
				else if (Config.TVT_EVEN_TEAMS.equals("SHUFFLE") && (!_playersShuffle.isEmpty() && _playersShuffle.contains(player)))
					_playersShuffle.remove(player);
				
			}
			
			player.sendMessage("Your participation in the TvT event has been removed.");
		}
	}
	
	/**
	 * Clean tv t.
	 */
	public static void cleanTvT()
	{
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (player != null && player.isOnline())
				{
					
					cleanEventPlayer(player);
					
					removePlayer(player);
					if (_savePlayers.contains(player.getName()))
						_savePlayers.remove(player.getName());
					player._inEventTvT = false;
				}
			}
		}
		
		if (_playersShuffle != null && !_playersShuffle.isEmpty())
		{
			for (Player player : _playersShuffle)
			{
				if (player != null && player.isOnline())
					player._inEventTvT = false;
			}
		}
		
		_topKills = 0;
		_topTeam = new String();
		_players = new ArrayList<>();
		_playersShuffle = new ArrayList<>();
		_savePlayers = new ArrayList<>();
		_savePlayerTeams = new ArrayList<>();
		
		_teamPointsCount = new ArrayList<>();
		_teamPlayersCount = new ArrayList<>();
		
		cleanLocalEventInfo();
		
		_inProgress = false;
		
		loadData();
	}
	
	/**
	 * Clean local event info.
	 */
	private static void cleanLocalEventInfo()
	{
		
		// nothing
	}
	
	/**
	 * Clean event player.
	 * @param player the player
	 */
	private static void cleanEventPlayer(Player player)
	{
		
		// nothing
		
	}
	
	/**
	 * Adds the disconnected player.
	 * @param player the player
	 */
	/**
	 * Adds the disconnected player.
	 * @param player the player
	 */
	public static synchronized void addDisconnectedPlayer(final Player player)
	{
		if ((Config.TVT_EVEN_TEAMS.equals("SHUFFLE") && (_teleport || _started)) || (Config.TVT_EVEN_TEAMS.equals("NO") || Config.TVT_EVEN_TEAMS.equals("BALANCE") && (_teleport || _started)))
		{
			if (Config.TVT_ON_START_REMOVE_ALL_EFFECTS)
			{
				player.stopAllEffects();
			}
			
			player._teamNameTvT = _savePlayerTeams.get(_savePlayers.indexOf(player.getName()));
			
			synchronized (_players)
			{
				for (final Player p : _players)
				{
					if (p == null)
					{
						continue;
					}
					// check by name incase player got new objectId
					else if (p.getName().equals(player.getName()))
					{
						player._originalNameColorTvT = player.getAppearance().getNameColor();
						player._originalTitleTvT = player.getTitle();
						player._originalKarmaTvT = player.getKarma();
						player._inEventTvT = true;
						player._countTvTkills = p._countTvTkills;
						player.setLastCords(player.getX(), player.getY(), player.getZ());
						_players.remove(p); // removing old object id from
						_players.add(player); // adding new objectId to vector
						break;
					}
				}
			}
			
			player.getAppearance().setNameColor(_teamColors.get(_teams.indexOf(player._teamNameTvT)));
			player.setKarma(0);
			if (Config.TVT_AURA)
			{
				if (_teams.size() >= 2)
					player.setTeam(_teams.indexOf(player._teamNameTvT) + 1);
			}
			
			player.broadcastUserInfo();
			
			player.teleToLocation(_teamsX.get(_teams.indexOf(player._teamNameTvT)), _teamsY.get(_teams.indexOf(player._teamNameTvT)), _teamsZ.get(_teams.indexOf(player._teamNameTvT)), 0);
			
			if (Config.TVT_SKILL_PROTECT)
			{
				for (L2Effect effect : player.getAllEffects())
				{
					
					if (Config.TVT_SKILL_LIST.contains(effect.getSkill().getId()))
						player.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
		}
	}
	
	/**
	 * Shuffle teams.
	 */
	public static void shuffleTeams()
	{
		int teamCount = 0, playersCount = 0;
		
		synchronized (_players)
		{
			for (;;)
			{
				if (_playersShuffle.isEmpty())
					break;
				
				int playerToAddIndex = Rnd.nextInt(_playersShuffle.size());
				Player player = null;
				player = _playersShuffle.get(playerToAddIndex);
				
				_players.add(player);
				_players.get(playersCount)._teamNameTvT = _teams.get(teamCount);
				_savePlayers.add(_players.get(playersCount).getName());
				_savePlayerTeams.add(_teams.get(teamCount));
				playersCount++;
				
				if (teamCount == _teams.size() - 1)
					teamCount = 0;
				else
					teamCount++;
				
				_playersShuffle.remove(playerToAddIndex);
			}
		}
		
	}
	
	// Show loosers and winners animations
	/**
	 * Play kneel animation.
	 * @param teamName the team name
	 */
	public static void playKneelAnimation(String teamName)
	{
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (player != null && player.isOnline() && !player.isDead())
				{
					if (!player._teamNameTvT.equals(teamName))
					{
						player.broadcastPacket(new SocialAction(player, 7));
					}
					else if (player._teamNameTvT.equals(teamName))
					{
						player.broadcastPacket(new SocialAction(player, 3));
					}
				}
			}
		}
		
	}
	
	static Calendar now = Calendar.getInstance();
	
	static int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
	
	/**
	 * Reward team.
	 * @param teamName the team name
	 * @param bestKiller
	 */
	public static void rewardTeam(final String teamName, final Player bestKiller)
	{
		synchronized (_players)
		{
			for (Player player : _players)
			{
				if (player != null && (player.isOnline()) && (player._inEventTvT) && (player._countTvTkills > 0 || Config.TVT_PRICE_NO_KILLS))
				{
					if ((bestKiller != null) && (bestKiller.equals(player)))
					{
						player.addItem("Event:", Config.TVT_TOP_KILLER_REWARD, Config.TVT_TOP_KILLER_QTY, player, true);
						
						if (player.isVip())
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount * Config.RATE_DROP_VIP, player, true);
						else
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount, player, true);
						
			            if (Config.ACTIVE_MISSION && !player.isPhantom()) {
			                if (!player.check_obj_mission(player.getObjectId()))
			                  player.updateMission(); 
			                if (!player.isTvTCompleted() && player.getTvTCont() < Config.MISSION_TVT_CONT)
			                  player.setTvTCont(player.getTvTCont() + 1); 
			              } 
					
					}
					else if (teamName != null && (player._teamNameTvT.equals(teamName)))
					{
						if (player.isVip())
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount * Config.RATE_DROP_VIP, player, true);
						else
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount, player, true);
						
						NpcHtmlMessage nhm = new NpcHtmlMessage(5);
						StringBuilder replyMSG = new StringBuilder("");
						replyMSG.append("<html><body>");
						replyMSG.append("<font color=\"FFFF00\">Your team wins the event. Look in your inventory for the reward.</font>");
						replyMSG.append("</body></html>");
						nhm.setHtml(replyMSG.toString());
						player.sendPacket(nhm);
			            if (Config.ACTIVE_MISSION) {
			                if (!player.check_obj_mission(player.getObjectId()))
			                  player.updateMission(); 
			                if (!player.isTvTCompleted() && player.getTvTCont() < Config.MISSION_TVT_CONT)
			                  player.setTvTCont(player.getTvTCont() + 1); 
			              } 
						// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
						player.sendPacket(ActionFailed.STATIC_PACKET);
						
					}
					else if (teamName == null)
					{
						int minus_reward = 0;
						if (_topKills != 0)
							minus_reward = _rewardAmount / 2;
						else
							// nobody killed
							minus_reward = _rewardAmount / 4;
						
						if (player.isVip())
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, minus_reward * Config.RATE_DROP_VIP, player, true);
						else
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, minus_reward, player, true);
						
						final NpcHtmlMessage nhm = new NpcHtmlMessage(5);
						final StringBuilder replyMSG = new StringBuilder("");
						
						replyMSG.append("<html><body>");
						replyMSG.append("<font color=\"FFFF00\">Your team had a tie in the event. Look in your inventory for the reward.</font>");
						replyMSG.append("</body></html>");
						
						nhm.setHtml(replyMSG.toString());
						player.sendPacket(nhm);
						
						// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
						player.sendPacket(ActionFailed.STATIC_PACKET);
					}
				}
			}
		}
		
	}
	
	/**
	 * Process top player.
	 */
	private static void processTopPlayer()
	{
		//
	}
	
	/**
	 * Process top team.
	 */
	private static void processTopTeam()
	{
		_topTeam = null;
		for (String team : _teams)
		{
			if (teamKillsCount(team) == _topKills && _topKills > 0)
				_topTeam = null;
			
			if (teamKillsCount(team) > _topKills)
			{
				_topTeam = team;
				_topKills = teamKillsCount(team);
			}
		}
	}
	
	/**
	 * Adds the team.
	 * @param teamName the team name
	 */
	public static void addTeam(String teamName)
	{
		if (is_inProgress())
		{
			if (Config.DEBUG_TVT)
				_log.info("Engine[addTeam(" + teamName + ")]: checkTeamOk() = false");
			return;
		}
		
		if (teamName.equals(" "))
			return;
		
		_teams.add(teamName);
		_teamPlayersCount.add(0);
		_teamPointsCount.add(0);
		_teamColors.add(0);
		_teamsX.add(0);
		_teamsY.add(0);
		_teamsZ.add(0);
		
		addTeamEventOperations(teamName);
		
	}
	
	/**
	 * Adds the team event operations.
	 * @param teamName the team name
	 */
	private static void addTeamEventOperations(String teamName)
	{
		
		// nothing
		
	}
	
	/**
	 * Removes the team.
	 * @param teamName the team name
	 */
	public static void removeTeam(String teamName)
	{
		if (is_inProgress() || _teams.isEmpty())
		{
			_log.info("Engine[removeTeam(" + teamName + ")]: checkTeamOk() = false");
			return;
		}
		
		if (teamPlayersCount(teamName) > 0)
		{
			_log.info("Engine[removeTeam(" + teamName + ")]: teamPlayersCount(teamName) > 0");
			return;
		}
		
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamsZ.remove(index);
		_teamsY.remove(index);
		_teamsX.remove(index);
		_teamColors.remove(index);
		_teamPointsCount.remove(index);
		_teamPlayersCount.remove(index);
		_teams.remove(index);
		
		removeTeamEventItems(teamName);
		
	}
	
	/**
	 * Removes the team event items.
	 * @param teamName the team name
	 */
	private static void removeTeamEventItems(String teamName)
	{
		
		_teams.indexOf(teamName);
		
		//
	}
	
	/**
	 * Sets the team pos.
	 * @param teamName the team name
	 * @param activeChar the active char
	 */
	public static void setTeamPos(String teamName, Player activeChar)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamsX.set(index, activeChar.getX());
		_teamsY.set(index, activeChar.getY());
		_teamsZ.set(index, activeChar.getZ());
	}
	
	/**
	 * Sets the team pos.
	 * @param teamName the team name
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public static void setTeamPos(String teamName, int x, int y, int z)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamsX.set(index, x);
		_teamsY.set(index, y);
		_teamsZ.set(index, z);
	}
	
	/**
	 * Sets the team color.
	 * @param teamName the team name
	 * @param color the color
	 */
	public static void setTeamColor(String teamName, int color)
	{
		if (is_inProgress())
			return;
		
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamColors.set(index, color);
	}
	
	/**
	 * Team players count.
	 * @param teamName the team name
	 * @return the int
	 */
	public static int teamPlayersCount(String teamName)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return -1;
		
		return _teamPlayersCount.get(index);
	}
	
	/**
	 * Sets the team players count.
	 * @param teamName the team name
	 * @param teamPlayersCount the team players count
	 */
	public static void setTeamPlayersCount(String teamName, int teamPlayersCount)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamPlayersCount.set(index, teamPlayersCount);
	}
	
	/**
	 * Check shuffle players.
	 * @param eventPlayer the event player
	 * @return true, if successful
	 */
	public static boolean checkShufflePlayers(Player eventPlayer)
	{
		try
		{
			synchronized (_players)
			{
				
				for (final Player player : _playersShuffle)
				{
					if (player == null || !player.isOnline())
					{
						_playersShuffle.remove(player);
						eventPlayer._inEventTvT = false;
						continue;
					}
					else if (player.getObjectId() == eventPlayer.getObjectId())
					{
						eventPlayer._inEventTvT = true;
						eventPlayer._countTvTkills = 0;
						return true;
					}
					
					// This 1 is incase player got new objectid after DC or reconnect
					else if (player.getName().equals(eventPlayer.getName()))
					{
						_playersShuffle.remove(player);
						_playersShuffle.add(eventPlayer);
						eventPlayer._inEventTvT = true;
						eventPlayer._countTvTkills = 0;
						return true;
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.info("checkShufflePlayers() =  " + e);
		}
		return false;
	}
	
	/**
	 * just an announcer to send termination messages.
	 */
	public static void sendFinalMessages()
	{
		if (!_started && !_aborted)
			Announcement.AnnounceEvents(""+Config.NAME_TVT+" Thank you For Participating At, " + _eventName + " Event.");
	}
	
	/**
	 * returns the interval between each event.
	 * @return the interval between matchs
	 */
	public static int getIntervalBetweenMatchs()
	{
		long actualTime = System.currentTimeMillis();
		long totalTime = actualTime + _intervalBetweenMatchs;
		long interval = totalTime - actualTime;
		int seconds = (int) (interval / 1000);
		
		return seconds / 60;
	}
	
	@Override
	public void run()
	{
		_log.info(_eventName + ": Event notification start");
		eventOnceStart();
	}
	
	@Override
	public String getEventIdentifier()
	{
		return _eventName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jfrozen.gameserver.model.entity.event.manager.EventTask#getEventStartTime()
	 */
	@Override
	public String getEventStartTime()
	{
		return startEventTime;
	}
	
	/**
	 * Sets the event start time.
	 * @param newTime the new event start time
	 */
	public void setEventStartTime(String newTime)
	{
		startEventTime = newTime;
	}
	
	/**
	 * On disconnect.
	 * @param player the player
	 */
	public static void onDisconnect(Player player)
	{
		if (player._inEventTvT)
		{
			if (player.isKickProtection())
			{
				if (TvT._savePlayers.contains(player.getName()))
					TvT._savePlayers.remove(player.getName());
			}
			
			removePlayer(player);
			
			if (_started || _teleport)
				player.teleToLocation(player.getLastX(), player.getLastY(), player.getLastZ(), 50);
		}
	}
	
	/**
	 * Team kills count.
	 * @param teamName the team name
	 * @return the int
	 */
	public static int teamKillsCount(String teamName)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return -1;
		
		return _teamPointsCount.get(index);
	}
	
	/**
	 * Sets the team kills count.
	 * @param teamName the team name
	 * @param teamKillsCount the team kills count
	 */
	public static void setTeamKillsCount(String teamName, int teamKillsCount)
	{
		int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamPointsCount.set(index, teamKillsCount);
	}
	
	/**
	 * Kick player from tvt.
	 * @param playerToKick the player to kick
	 */
	public static void kickPlayerFromTvt(Player playerToKick)
	{
		if (playerToKick == null)
			return;
		
		synchronized (_players)
		{
			if (_joining)
			{
				_playersShuffle.remove(playerToKick);
				_players.remove(playerToKick);
				playerToKick._inEventTvT = false;
				playerToKick._teamNameTvT = "";
				playerToKick._countTvTkills = 0;
			}
		}
		
		if (_started || _teleport)
		{
			_playersShuffle.remove(playerToKick);
			// playerToKick._inEventTvT = false;
			removePlayer(playerToKick);
			if (playerToKick.isOnline())
			{
				playerToKick.getAppearance().setNameColor(playerToKick._originalNameColorTvT);
				playerToKick.setKarma(playerToKick._originalKarmaTvT);
				playerToKick.setTitle(playerToKick._originalTitleTvT);
				playerToKick.broadcastUserInfo();
				playerToKick.sendMessage("You have been kicked from the TvT.");
				playerToKick.teleToLocation(playerToKick.getLastX(), playerToKick.getLastY(), playerToKick.getLastZ(), 50);
			}
		}
	}
	
	/**
	 * Find best killer.
	 * @param players the players
	 * @return the l2 pc instance
	 */
	public static Player findBestKiller(ArrayList<Player> players)
	{
		if (players == null)
		{
			return null;
		}
		Player bestKiller = null;
		for (Player player : players)
		{
			if ((bestKiller == null) || (bestKiller._countTvTkills < player._countTvTkills))
				bestKiller = player;
		}
		return bestKiller;
	}
	
	/**
	 * The Class TvTTeam.
	 */
	public static class TvTTeam
	{
		
		/** The kill count. */
		private int killCount = -1;
		
		/** The name. */
		private String name = null;
		
		/**
		 * Instantiates a new tv t team.
		 * @param name the name
		 * @param killCount the kill count
		 */
		TvTTeam(String name, int killCount)
		{
			this.killCount = killCount;
			this.name = name;
		}
		
		/**
		 * Gets the kill count.
		 * @return the kill count
		 */
		public int getKillCount()
		{
			return this.killCount;
		}
		
		/**
		 * Sets the kill count.
		 * @param killCount the new kill count
		 */
		public void setKillCount(int killCount)
		{
			this.killCount = killCount;
		}
		
		/**
		 * Gets the name.
		 * @return the name
		 */
		public String getName()
		{
			return this.name;
		}
		
		/**
		 * Sets the name.
		 * @param name the new name
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}
	
	  private static void closeFortDoors() {
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170004)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170005)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170002)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170003)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170006)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170007)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170008)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170009)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170010)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(23170011)).closeMe();
		    try {
		      Thread.sleep(20L);
		    } catch (InterruptedException ie) {
		      if (Config.DEBUG_TVT)
		        ie.printStackTrace(); 
		      _log.warning("Error, " + ie.getMessage());
		    } 
		  }
		  
		  private static void closeAdenColosseumDoors() {
		    DoorTable.getInstance().getDoor(Integer.valueOf(24190002)).closeMe();
		    DoorTable.getInstance().getDoor(Integer.valueOf(24190003)).closeMe();
		    try {
		      Thread.sleep(20L);
		    } catch (InterruptedException ie) {
		      if (Config.DEBUG_TVT)
		        ie.printStackTrace(); 
		      _log.warning("Error, " + ie.getMessage());
		    } 
		  }
	
	public static void Classes(String command, final Player activeChar)
	{
		if (command.startsWith("close"))
			activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		else if (command.startsWith("tvt_watch"))
		{
			activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			if ((TvT.is_teleport() || TvT.is_started()) && !activeChar.isInObserverMode() && !activeChar._inEventTvT && activeChar.getKarma() == 0)
			{
				activeChar.setEventObserver(true);
				activeChar.enterTvTObserverMode(Config.TVT_OBSERVER_X, Config.TVT_OBSERVER_Y, Config.TVT_OBSERVER_Z);
			}
		}
	}
	
	public static final void Link(Player player, String request)
	{
		Classes(request, player);
	}
	
	public static void Announce(String text)
	{
		final CreatureSay a = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "", text); // 8D
		final CreatureSay b = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "", text); // 8D
		
		for (final Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline() && player._inEventTvT)
			{
				if (Config.PVP_COLOR_ANNOUNCE == 18 || Config.PVP_COLOR_ANNOUNCE == 10)
					player.sendPacket(a);
				else
					player.sendPacket(b);
			}
		}
		
	}
}