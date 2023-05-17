/*
 * L2jFrozen Project - www.l2jfrozen.com 
 * 
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
import java.util.Vector;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.manager.CTFEventManager;
import com.l2jmega.events.manager.EventTask;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Radar;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.RadarControl;
import com.l2jmega.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.util.CloseUtil;

import phantom.PhantomOld;

/**
 * The Class CTF.
 */
public class CTF implements EventTask
{
	/** The Constant LOGGER. */
	protected static final Logger LOGGER = Logger.getLogger(CTF.class.getName());
	
	/** The _joining location name. */
	public static String _eventName = new String();
	
	protected static String _eventDesc = new String();
	
	protected static String _joiningLocationName = new String();
	
	/** The _npc spawn. */
	private static L2Spawn _npcSpawn;
	
	/** The _in progress. */
	protected static boolean _joining = false, _teleport = false, _started = false, _aborted = false, _sitForced = false, _inProgress = false;
	
	/** The _max players. */
	protected static int _npcId = 0, _npcX = 0, _npcY = 0, _npcZ = 0, _npcHeading = 0, _rewardId = 0, _rewardAmount = 0, _minlvl = 0, _maxlvl = 0, _joinTime = 0, _eventTime = 0, _minPlayers = 0, _maxPlayers = 0;
	
	/** The _interval between matches. */
	protected static long _intervalBetweenMatches = 0;
	
	/** The start event time. */
	private String startEventTime;
	
	/** The _team event. */
	protected static boolean _teamEvent = true; // TODO to be integrated
	
	/** The _players. */
	public static Vector<Player> _players = new Vector<>();
	
	/** The _top team. */
	private static String _topTeam = new String();
	
	/** The _players shuffle. */
	public static Vector<Player> _playersShuffle = new Vector<>();
	
	/** The _save player teams. */
	public static Vector<String> _teams = new Vector<>(), _savePlayers = new Vector<>(), _savePlayerTeams = new Vector<>();
	
	/** The _teams z. */
	public static Vector<Integer> _teamPlayersCount = new Vector<>(), _teamColors = new Vector<>(), _teamsX = new Vector<>(), _teamsY = new Vector<>(), _teamsZ = new Vector<>();
	
	/** The _team points count. */
	public static Vector<Integer> _teamPointsCount = new Vector<>();
	
	/** The _top score. */
	public static int _topScore = 0;
	
	/** The _event offset. */
	public static int _eventCenterX = 0, _eventCenterY = 0, _eventCenterZ = 0, _eventOffset = 0;
	
	/** The _ fla g_ i n_ han d_ ite m_ id. */
	private static int _FlagNPC = 35062, _FLAG_IN_HAND_ITEM_ID = 6718;
	
	/** The _flags z. */
	public static Vector<Integer> _flagIds = new Vector<>(), _flagsX = new Vector<>(), _flagsY = new Vector<>(), _flagsZ = new Vector<>();
	
	/** The _throne spawns. */
	public static Vector<L2Spawn> _flagSpawns = new Vector<>(), _throneSpawns = new Vector<>();
	
	/** The _flags taken. */
	public static Vector<Boolean> _flagsTaken = new Vector<>();
	
	/**
	 * Instantiates a new cTF.
	 */
	private CTF()
	{
	}
	
	/**
	 * Gets the new instance.
	 * @return the new instance
	 */
	public static CTF getNewInstance()
	{
		return new CTF();
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
	public static boolean set_eventName(final String _eventName)
	{
		if (!is_inProgress())
		{
			CTF._eventName = _eventName;
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
	public static boolean set_eventDesc(final String _eventDesc)
	{
		if (!is_inProgress())
		{
			CTF._eventDesc = _eventDesc;
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
	public static boolean set_joiningLocationName(final String _joiningLocationName)
	{
		if (!is_inProgress())
		{
			CTF._joiningLocationName = _joiningLocationName;
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
	public static boolean set_npcId(final int _npcId)
	{
		if (!is_inProgress())
		{
			CTF._npcId = _npcId;
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
		final Location npc_loc = new Location(_npcX, _npcY, _npcZ);
		
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
	public static boolean set_rewardId(final int _rewardId)
	{
		if (!is_inProgress())
		{
			CTF._rewardId = _rewardId;
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
	public static boolean set_rewardAmount(final int _rewardAmount)
	{
		if (!is_inProgress())
		{
			CTF._rewardAmount = _rewardAmount;
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
	public static boolean set_minlvl(final int _minlvl)
	{
		if (!is_inProgress())
		{
			CTF._minlvl = _minlvl;
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
	public static boolean set_maxlvl(final int _maxlvl)
	{
		if (!is_inProgress())
		{
			CTF._maxlvl = _maxlvl;
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
	public static boolean set_joinTime(final int _joinTime)
	{
		if (!is_inProgress())
		{
			CTF._joinTime = _joinTime;
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
	public static boolean set_eventTime(final int _eventTime)
	{
		if (!is_inProgress())
		{
			CTF._eventTime = _eventTime;
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
	public static boolean set_minPlayers(final int _minPlayers)
	{
		if (!is_inProgress())
		{
			CTF._minPlayers = _minPlayers;
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
	public static boolean set_maxPlayers(final int _maxPlayers)
	{
		if (!is_inProgress())
		{
			CTF._maxPlayers = _maxPlayers;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the _interval between matchs.
	 * @return the _intervalBetweenMatches
	 */
	public static long get_intervalBetweenMatches()
	{
		return _intervalBetweenMatches;
	}
	
	/**
	 * Set_interval between matchs.
	 * @param _intervalBetweenMatches the _intervalBetweenMatches to set
	 * @return true, if successful
	 */
	public static boolean set_intervalBetweenMatches(final long _intervalBetweenMatches)
	{
		if (!is_inProgress())
		{
			CTF._intervalBetweenMatches = _intervalBetweenMatches;
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
	public boolean setStartEventTime(final String startEventTime)
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
	public static boolean checkMaxLevel(final int maxlvl)
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
	public static boolean checkMinLevel(final int minlvl)
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
	public static boolean checkMinPlayers(final int players)
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
	public static boolean checkMaxPlayers(final int players)
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
		
		try
		{
			if (_flagsX.contains(0) || _flagsY.contains(0) || _flagsZ.contains(0) || _flagIds.contains(0))
				return false;
			if (_flagsX.size() < _teams.size() || _flagsY.size() < _teams.size() || _flagsZ.size() < _teams.size() || _flagIds.size() < _teams.size())
				return false;
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sets the npc pos.
	 * @param activeChar the new npc pos
	 */
	public static void setNpcPos(final Player activeChar)
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
			_npcSpawn.getNpc()._isEventMobCTF = true;
			_npcSpawn.getNpc().setTitle(_eventName);
			_npcSpawn.getNpc().isAggressive();
			_npcSpawn.getNpc().decayMe();
			_npcSpawn.getNpc().spawnMe(_npcSpawn.getNpc().getX(), _npcSpawn.getNpc().getY(), _npcSpawn.getNpc().getZ());
			_npcSpawn.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn.getNpc(), _npcSpawn.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			LOGGER.info("Engine[spawnEventNpc(exception: " + e.getMessage());
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
			LOGGER.info(_eventName + "Engine[startJoin]: startJoinOk() = false");
			return false;
		}
		
		_inProgress = true;
		_joining = true;
		spawnEventNpc();
		Announcement.AnnounceEvents(_eventName + ": " + _eventDesc + "!");
		
		Announcement.AnnounceEvents(_eventName + ": Joinable in " + _joiningLocationName + ".");
		
		if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
			Announcement.AnnounceEvents(_eventName + ": Remove All buffs !");
		
		if (Config.CTF_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
			Announcement.AnnounceEvents(_eventName + ": Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
		
		if (Config.CTF_ANNOUNCE_LVL)
			Announcement.AnnounceEvents(_eventName + ": Min Level: " + _minlvl);
		
		if (Config.CTF_COMMAND)
			Announcement.AnnounceEvents(_eventName + ": Commands .ctfjoin .ctfleave");
		
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
		
		if (_teamEvent)
		{
			
			if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE") && checkMinPlayers(_playersShuffle.size()))
			{
				shuffleTeams();
			}
			else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE") && !checkMinPlayers(_playersShuffle.size()))
			{
				Announcement.AnnounceEvents(_eventName + ": Not enough players . Min Requested : " + _minPlayers + ", Participating : " + _playersShuffle.size());
				CTFEventManager.getInstance().StartCalculationOfNextCtfEventTime();
				CTFEventManager.getInstance().getNextTime();
				if (Config.CTF_STATS_LOGGER)
					LOGGER.info(_eventName + ":Not enough players . Min Requested : " + _minPlayers + ", Participating : " + _playersShuffle.size());
				
				return false;
			}
			
		}
		else
		{
			
			if (!checkMinPlayers(_players.size()))
			{
				Announcement.AnnounceEvents(_eventName + ": Not enough players . Min Requested : " + _minPlayers + ", Participating : " + _players.size());
				CTFEventManager.getInstance().StartCalculationOfNextCtfEventTime();
				for (Player player : World.getInstance().getPlayers())
				{
					if (player != null && player.isOnline())
					{
						CreatureSay cs = new CreatureSay(player.getObjectId(), Config.ANNOUNCE_ID_EVENT, "CTF", ("Next CTF Event: " + CTFEventManager.getInstance().getNextTime()) + " (GMT-3)."); // 8D
						player.sendPacket(cs);
					}
				}
				if (Config.CTF_STATS_LOGGER)
					LOGGER.info(_eventName + ":Not enough players . Min Requested : " + _minPlayers + ", Participating : " + _players.size());
				return false;
			}
			
		}
		
		_joining = false;
		Announcement.AnnounceEvents(_eventName + ": Teleport to team spot in 20 seconds!");
		
		setUserData();
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				sit();
				afterTeleportOperations();
				
				synchronized (_players)
				{
					for (final Player player : World.getInstance().getPlayers())
					{
						if (player != null && player.isOnline() && player._inEventCTF)
						{
							if (Config.CTF_ON_START_UNSUMMON_PET)
							{
								// Remove Summon's buffs
								if (player.getPet() != null)
								{
									final Summon summon = player.getPet();
									for (final L2Effect e : summon.getAllEffects())
										if (e != null)
											e.exit(true);
									
									if (summon instanceof Pet)
										summon.unSummon(player);
								}
							}
							
							if (player.isDead())
								player.doRevive();
							
							final int offset = Config.CTF_SPAWN_OFFSET;
							player.teleToLocation(_teamsX.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(-offset, offset), _teamsY.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(-offset, offset), _teamsZ.get(_teams.indexOf(player._teamNameCTF)), 0);
							
							if (player.isPhantom())
								player.setLastCords(player.getX(), player.getY(), player.getZ());
							
							if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
								player.stopAllEffects();
							
							player.add_buff_event();
							
							// Remove player from his party
							if (player.getParty() != null)
							{
								final L2Party party = player.getParty();
								party.removePartyMember(player, null);
							}
						}
					}
				}
				
			}
		}, 20000);
		_teleport = true;
		return true;
	}
	
	/**
	 * After teleport operations.
	 */
	protected static void afterTeleportOperations()
	{
		spawnAllFlags();
	}
	
	/**
	 * Start event.
	 * @return true, if successful
	 */
	public static boolean startEvent()
	{
		if (!startEventOk())
		{
			if (Config.DEBUG)
				LOGGER.info(_eventName + " Engine[startEvent()]: startEventOk() = false");
			return false;
		}
		
	    if (Config.TVT_CLOSE_FORT_DOORS)
	        closeFortDoors(); 
	      if (Config.TVT_CLOSE_ADEN_COLOSSEUM_DOORS)
	        closeAdenColosseumDoors(); 
		
		_teleport = false;
		
		Announcement.AnnounceEvents(_eventName + ": Started. Go Capture the Flags!");
		_started = true;
		
		sit();
		
		for (Player player : _players)
		{
			if (player != null && player.isOnline())
			{
				if (player.getKarma() > 0)
					player.setKarma(0);
				
				if (Config.SCREN_MSG)
					player.sendPacket(new ExShowScreenMessage("Capture the Flags!!", 3 * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
				
				if (player.isDead())
					player.doRevive();
				
				player.getStatus().setCurrentHp(player.getMaxHp());
				player.getStatus().setCurrentMp(player.getMaxMp());
				player.getStatus().setCurrentCp(player.getMaxCp());
				
				if (player.getMountType() != 0)
					player.dismount();
			}
		}
		
		afterStartOperations();
		
		return true;
	}
	
	/**
	 * After start operations.
	 */
	private static void afterStartOperations()
	{
		
		synchronized (_players)
		{
			for (final Player player : _players)
				if (player != null)
				{
					player._teamNameHaveFlagCTF = null;
					player._haveFlagCTF = false;
				}
			
		}
		
	}
	
	/**
	 * Finish event.
	 */
	public static void finishEvent()
	{
		if (!finishEventOk())
		{
			LOGGER.info(_eventName + " Engine[finishEvent]: finishEventOk() = false");
			return;
		}
		
		_started = false;
		_aborted = false;
		unspawnEventNpc();
		
		afterFinishOperations();
		
		if (_teamEvent)
		{
			processTopTeam();
			
			if (_topScore != 0)
			{
				
				playKneelAnimation(_topTeam);
				
				if (Config.CTF_ANNOUNCE_TEAM_STATS)
				{
					Announcement.AnnounceEvents(_eventName + " Team Statistics:");
					for (final String team : _teams)
					{
						final int _flags_ = teamPointsCount(team);
						Announcement.AnnounceEvents(_eventName + ": Team: " + team + " - Flags taken: " + _flags_);
					}
				}
				
				if (_topTeam != null)
				{
					Announcement.AnnounceEvents(_eventName + ": Team " + _topTeam + " wins the match, with " + _topScore + " flags taken!");
				}
				else
				{
					Announcement.AnnounceEvents(_eventName + ": The event finished with a TIE: " + _topScore + " flags taken by each team!");
				}
				
				rewardTeam(_topTeam);
				
				if (Config.CTF_STATS_LOGGER)
				{
					
					LOGGER.info("**** " + _eventName + " ****");
					LOGGER.info(_eventName + " Team Statistics:");
					for (final String team : _teams)
					{
						final int _flags_ = teamPointsCount(team);
						LOGGER.info("Team: " + team + " - Flags taken: " + _flags_);
					}
					
					LOGGER.info(_eventName + ": Team " + _topTeam + " wins the match, with " + _topScore + " flags taken!");
					
				}
				
			}
			else
			{
				
				Announcement.AnnounceEvents(_eventName + ": The event finished with a TIE: No team wins the match(nobody took flags)!");
				
				if (Config.CTF_STATS_LOGGER)
					LOGGER.info(_eventName + ": No team win the match(nobody took flags).");
				
				rewardTeam(_topTeam);
				
			}
			
		}
		else
		{
			processTopPlayer();
		}
		
		teleportFinish();
	}
	
	/**
	 * After finish operations.
	 */
	private static void afterFinishOperations()
	{
		unspawnAllFlags();
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
			cleanCTF();
			_joining = false;
			_inProgress = false;
			Announcement.AnnounceEvents(_eventName + ": Match aborted!");
			return;
		}
		_joining = false;
		_teleport = false;
		_started = false;
		_aborted = true;
		unspawnEventNpc();
		
		afterFinish();
		
		Announcement.AnnounceEvents(_eventName + ": Match aborted!");
		teleportFinish();
	}
	
	/**
	 * After finish.
	 */
	private static void afterFinish()
	{
		
		unspawnAllFlags();
		
	}
	
	/**
	 * Teleport finish.
	 */
	public static void teleportFinish()
	{
		
		CTFEventManager.getInstance().StartCalculationOfNextCtfEventTime();
		CTFEventManager.getInstance().getNextTime();
		sit();
		Announcement.AnnounceEvents(_eventName + ": Teleport back to participation NPC in 20 seconds!");
		for (final Player player : _players)
		{
			if (player != null && player.isOnline())
			{
				if (player.isDead())
					player.doRevive();
				
				player._reuseTimeStamps.clear();
				player.getDisabledSkills().clear();
				player.sendPacket(new SkillCoolTime(player));
				
				// Remove player from his party
				if (player.getParty() != null)
				{
					final L2Party party = player.getParty();
					party.removePartyMember(player, null);
				}
				
				if (player._haveFlagCTF)
					removeFlagFromPlayer(player);	

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
					
					for (final Player player : _players)
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
									
									final PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE char_name=?");
									statement.setInt(1, player.getLastX());
									statement.setInt(2, player.getLastY());
									statement.setInt(3, player.getLastZ());
									statement.setString(4, player.getName());
									statement.execute();
									statement.close();
								}
								catch (final Exception e)
								{
									LOGGER.info(e.getMessage());
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
				cleanCTF();
			}
		}, 20000);
	}
	
	protected static class AutoEventTask implements Runnable
	{
		
		@Override
		public void run()
		{
			LOGGER.info("Starting " + _eventName + "!");
			LOGGER.info("Matchs Are Restarted At Every: " + getIntervalBetweenMatchs() + " Minutes.");
			if (checkAutoEventStartJoinOk() && startJoin() && !_aborted)
			{
				if (_joinTime > 0)
					waiter(_joinTime * 60 * 1000); // minutes for join event
				else if (_joinTime <= 0)
				{
					LOGGER.info(_eventName + ": join time <=0 aborting event.");
					abortEvent();
					return;
				}
				if (startTeleport() && !_aborted)
				{
					waiter(30 * 1000); // 30 sec wait time untill start fight after teleported
					if (startEvent() && !_aborted)
					{
						LOGGER.info(_eventName + ": waiting.....minutes for event time " + _eventTime);
						
						waiter(_eventTime * 60 * 1000); // minutes for event time
						finishEvent();
						
						LOGGER.info(_eventName + ": waiting... delay for final messages ");
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
	protected static void waiter(final long interval)
	{
		final long startWaiterTime = System.currentTimeMillis();
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
							Announcement.AnnounceEvents(_eventName + ": Joinable in " + _joiningLocationName + "!");
							
							if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
								Announcement.AnnounceEvents(_eventName + ": Remove All buffs !");
							
							if (Config.CTF_ANNOUNCE_LVL)
								Announcement.AnnounceEvents(_eventName + ": Min Level: " + _minlvl);
							
							if (Config.CTF_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
								Announcement.AnnounceEvents(_eventName + ": Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
							
							if (Config.CTF_COMMAND)
								Announcement.AnnounceEvents(_eventName + ": Commands .ctfjoin .ctfleave");
							
							Announcement.AnnounceEvents(_eventName + ": " + seconds / 60 / 60 + " hour(s) till registration close!");
						}
						else if (_started)
							Announcement.AnnounceEvents(_eventName + ": " + seconds / 60 / 60 + " hour(s) till event finish!");
						
						break;
					case 1800: // 30 minutes left
					case 900: // 15 minutes left
					case 420: // 7 minutes left
					case 300: // 5 minutes left
					case 240: // 4 minutes left
					case 180: // 3 minutes left
					case 120: // 2 minutes left
					case 60: // 1 minute left
						// removeOfflinePlayers();
						
						if (_joining)
						{
							Announcement.AnnounceEvents(_eventName + ": Joinable in " + _joiningLocationName + "!");
							
							if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
								Announcement.AnnounceEvents(_eventName + ": Remove All buffs !");
							
							if (Config.CTF_ANNOUNCE_LVL)
								Announcement.AnnounceEvents(_eventName + ": Min Level: " + _minlvl);
							
							if (Config.CTF_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
								Announcement.AnnounceEvents(_eventName + ": Reward: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
							
							if (Config.CTF_COMMAND)
								Announcement.AnnounceEvents(_eventName + ": Commands .ctfjoin .ctfleave");
							
							Announcement.AnnounceEvents(_eventName + ": " + seconds / 60 + " minute(s) till registration close!");
						}
						else if (_started)
						{
							if (Config.CTF_ANNOUNCE_REWARD && ItemTable.getInstance().getTemplate(_rewardId) != null)
								Announcement.AnnounceEvents(_eventName + ": Reward winner: " + get_rewardAmount() + " " + ItemTable.getInstance().getTemplate(get_rewardId()).getName());
							
							if (ItemTable.getInstance().getTemplate(Config.CTF_REWARD_TIE) != null)
								Announcement.AnnounceEvents(_eventName + ": Reward for tie: " + Config.CTF_REWARD_TIE_AMOUNT + " " + ItemTable.getInstance().getTemplate(Config.CTF_REWARD_TIE).getName());
							
							Announcement.AnnounceEvents(_eventName + ": " + seconds / 60 + " minute(s) till event finish!");
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
							Announcement.AnnounceEvents(_eventName + ": " + seconds + " second(s) till registration close!");
						else if (_teleport)
						{
							Announcement.AnnounceEvents(_eventName + ": " + seconds + " seconds(s) till start fight!");
							
							if (Config.SCREN_MSG)
							{
								for (Player player : _players)
									player.sendPacket(new ExShowScreenMessage(seconds + " seconds(s) till start fight!", 5000));
							}
							
						}
						else if (_started)
							Announcement.AnnounceEvents(_eventName + ": " + seconds + " second(s) till event finish!");
						
						break;
				}
			}
			
			final long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (final InterruptedException ie)
				{
					ie.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Sit.
	 */
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
				for (final Player player : _playersShuffle)
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
			LOGGER.info("removeOfflinePlayers() =  " + e);
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
		
		if (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE"))
		{
			if (_teamPlayersCount.contains(0))
				return false;
		}
		else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
		{
			final Vector<Player> playersShuffleTemp = new Vector<>();
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
	private static boolean addPlayerOk(final String teamName, final Player eventPlayer)
	{
		if (!Config.Allow_Same_HWID_On_ctf && !eventPlayer.isGM() && !eventPlayer.isPhantom())
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
		else if (checkShufflePlayers(eventPlayer) || eventPlayer._inEventCTF)
		{
			eventPlayer.sendMessage("You already participated in the event!");
			return false;
		}
		else if (eventPlayer._inEventTvT)
		{
			eventPlayer.sendMessage("You already participated in another event!");
			return false;
		}
		else if (OlympiadManager.getInstance().isRegisteredInComp(eventPlayer) || eventPlayer.isInOlympiadMode() || eventPlayer.getOlympiadGameId() > 0)
		{
			eventPlayer.sendMessage("You can't register while you are in olympiad!");
			return false;
		}
		
		synchronized (_players)
		{
			for (final Player player : _players)
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
		
		if (CTF._savePlayers.contains(eventPlayer.getName()))
		{
			eventPlayer.sendMessage("You already participated in another event!");
			return false;
		}
		
		if (Config.CTF_EVEN_TEAMS.equals("NO"))
			return true;
		
		else if (Config.CTF_EVEN_TEAMS.equals("BALANCE"))
		{
			boolean allTeamsEqual = true;
			int countBefore = -1;
			
			for (final int playersCount : _teamPlayersCount)
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
			
			for (final int teamPlayerCount : _teamPlayersCount)
			{
				if (teamPlayerCount < countBefore)
					countBefore = teamPlayerCount;
			}
			
			final Vector<String> joinableTeams = new Vector<>();
			
			for (final String team : _teams)
			{
				if (teamPlayersCount(team) == countBefore)
					joinableTeams.add(team);
			}
			
			if (joinableTeams.contains(teamName))
				return true;
		}
		else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
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
			for (final Player player : _players)
			{
				player._originalNameColorCTF = player.getAppearance().getNameColor();
				player._originalKarmaCTF = player.getKarma();
				player._originalTitleCTF = player.getTitle();
				player.setLastCords(player.getX(), player.getY(), player.getZ());
				
				player.getAppearance().setNameColor(_teamColors.get(_teams.indexOf(player._teamNameCTF)));
				player.setKarma(0);
				
				if (Config.CTF_AURA)
				{
					if (_teams.size() >= 2)
						player.setTeam(_teams.indexOf(player._teamNameCTF) + 1);
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
				
				if (Config.CTF_ON_START_UNSUMMON_PET)
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
		LOGGER.info("");
		LOGGER.info("");
		
		if (!_joining && !_teleport && !_started)
		{
			LOGGER.info("<<---------------------------------->>");
			LOGGER.info(">> " + _eventName + " Engine infos dump (INACTIVE) <<");
			LOGGER.info("<<--^----^^-----^----^^------^^----->>");
		}
		else if (_joining && !_teleport && !_started)
		{
			LOGGER.info("<<--------------------------------->>");
			LOGGER.info(">> " + _eventName + " Engine infos dump (JOINING) <<");
			LOGGER.info("<<--^----^^-----^----^^------^----->>");
		}
		else if (!_joining && _teleport && !_started)
		{
			LOGGER.info("<<---------------------------------->>");
			LOGGER.info(">> " + _eventName + " Engine infos dump (TELEPORT) <<");
			LOGGER.info("<<--^----^^-----^----^^------^^----->>");
		}
		else if (!_joining && !_teleport && _started)
		{
			LOGGER.info("<<--------------------------------->>");
			LOGGER.info(">> " + _eventName + " Engine infos dump (STARTED) <<");
			LOGGER.info("<<--^----^^-----^----^^------^----->>");
		}
		
		LOGGER.info("Name: " + _eventName);
		LOGGER.info("Desc: " + _eventDesc);
		LOGGER.info("Join location: " + _joiningLocationName);
		LOGGER.info("Min lvl: " + _minlvl);
		LOGGER.info("Max lvl: " + _maxlvl);
		LOGGER.info("");
		LOGGER.info("##########################");
		LOGGER.info("# _teams(Vector<String>) #");
		LOGGER.info("##########################");
		
		for (final String team : _teams)
			LOGGER.info(team + " Flags Taken :" + _teamPointsCount.get(_teams.indexOf(team)));
		
		if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
		{
			LOGGER.info("");
			LOGGER.info("#########################################");
			LOGGER.info("# _playersShuffle(Vector<Player>) #");
			LOGGER.info("#########################################");
			
			for (final Player player : _playersShuffle)
			{
				if (player != null)
					LOGGER.info("Name: " + player.getName());
			}
		}
		
		LOGGER.info("");
		LOGGER.info("##################################");
		LOGGER.info("# _players(Vector<Player>) #");
		LOGGER.info("##################################");
		
		synchronized (_players)
		{
			for (final Player player : _players)
			{
				if (player != null)
					LOGGER.info("Name: " + player.getName() + "   Team: " + player._teamNameCTF + "  Flags :" + player._countCTFflags);
			}
			
		}
		
		LOGGER.info("");
		LOGGER.info("#####################################################################");
		LOGGER.info("# _savePlayers(Vector<String>) and _savePlayerTeams(Vector<String>) #");
		LOGGER.info("#####################################################################");
		
		for (final String player : _savePlayers)
			LOGGER.info("Name: " + player + "	Team: " + _savePlayerTeams.get(_savePlayers.indexOf(player)));
		
		LOGGER.info("");
		LOGGER.info("");
		
		dumpLocalEventInfo();
		
	}
	
	/**
	 * Dump local event info.
	 */
	private static void dumpLocalEventInfo()
	{
		LOGGER.info("**********==CTF==************");
		LOGGER.info("CTF._teamPointsCount:" + _teamPointsCount.toString());
		LOGGER.info("CTF._flagIds:" + _flagIds.toString());
		LOGGER.info("CTF._flagSpawns:" + _flagSpawns.toString());
		LOGGER.info("CTF._throneSpawns:" + _throneSpawns.toString());
		LOGGER.info("CTF._flagsTaken:" + _flagsTaken.toString());
		LOGGER.info("CTF._flagsX:" + _flagsX.toString());
		LOGGER.info("CTF._flagsY:" + _flagsY.toString());
		LOGGER.info("CTF._flagsZ:" + _flagsZ.toString());
		LOGGER.info("************EOF**************\n");
		LOGGER.info("");
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
		_savePlayers = new Vector<>();
		synchronized (_players)
		{
			_players = new Vector<>();
		}
		
		_topTeam = new String();
		_teams = new Vector<>();
		_savePlayerTeams = new Vector<>();
		_playersShuffle = new Vector<>();
		_teamPlayersCount = new Vector<>();
		_teamPointsCount = new Vector<>();
		_teamColors = new Vector<>();
		_teamsX = new Vector<>();
		_teamsY = new Vector<>();
		_teamsZ = new Vector<>();
		
		_throneSpawns = new Vector<>();
		_flagSpawns = new Vector<>();
		_flagsTaken = new Vector<>();
		_flagIds = new Vector<>();
		_flagsX = new Vector<>();
		_flagsY = new Vector<>();
		_flagsZ = new Vector<>();
		
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
		_topScore = 0;
		_minlvl = 0;
		_maxlvl = 0;
		_joinTime = 0;
		_eventTime = 0;
		_minPlayers = 0;
		_maxPlayers = 0;
		_intervalBetweenMatches = 0;
		
		java.sql.Connection con = null;
		try
		{
			PreparedStatement statement;
			ResultSet rs;
			
			con = L2DatabaseFactory.getInstance().getConnection();
			
			statement = con.prepareStatement("Select * from ctf");
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
				_intervalBetweenMatches = rs.getLong("delayForNextEvent");
			}
			
			statement.close();
			
			int index = -1;
			if (teams > 0)
				index = 0;
			while (index < teams && index > -1)
			{
				statement = con.prepareStatement("Select * from ctf_teams where teamId = ?");
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
					
					_flagsX.add(0);
					_flagsY.add(0);
					_flagsZ.add(0);
					_flagsX.set(index, rs.getInt("flagX"));
					_flagsY.set(index, rs.getInt("flagY"));
					_flagsZ.set(index, rs.getInt("flagZ"));
					_flagSpawns.add(null);
					_flagIds.add(_FlagNPC);
					_flagsTaken.add(false);
					
				}
				index++;
				statement.close();
			}
		}
		catch (final Exception e)
		{
			LOGGER.info("Exception: loadData(): " + e.getMessage());
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
			
			statement = con.prepareStatement("Delete from ctf");
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("INSERT INTO ctf (eventName, eventDesc, joiningLocation, minlvl, maxlvl, npcId, npcX, npcY, npcZ, npcHeading, rewardId, rewardAmount, teamsCount, joinTime, eventTime, minPlayers, maxPlayers,delayForNextEvent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
			statement.setLong(18, _intervalBetweenMatches);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("Delete from ctf_teams");
			statement.execute();
			statement.close();
			
			for (final String teamName : _teams)
			{
				final int index = _teams.indexOf(teamName);
				
				if (index == -1)
					return;
				statement = con.prepareStatement("INSERT INTO ctf_teams (teamId ,teamName, teamX, teamY, teamZ, teamColor, flagX, flagY, flagZ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
				statement.setInt(1, index);
				statement.setString(2, teamName);
				statement.setInt(3, _teamsX.get(index));
				statement.setInt(4, _teamsY.get(index));
				statement.setInt(5, _teamsZ.get(index));
				statement.setInt(6, _teamColors.get(index));
				
				statement.setInt(7, _flagsX.get(index));
				statement.setInt(8, _flagsY.get(index));
				statement.setInt(9, _flagsZ.get(index));
				
				statement.execute();
				statement.close();
			}
		}
		catch (final Exception e)
		{
			LOGGER.info("Exception: saveData(): " + e.getMessage());
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
			
			StringBuilder replyMSG = new StringBuilder("<html><title>CTF</title><body>");
			replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
			replyMSG.append("<center><font color=\"LEVEL\">Current event:</font></center><br1>");
			replyMSG.append("<center>Name: &nbsp;<font color=\"1E90FF\">" + _eventName + "</font></center><br1>");
			replyMSG.append("<center>Description:&nbsp;<font color=\"1E90FF\">" + _eventDesc + "</font></center><br>");
			
			if (!_started && !_joining)
			{
				replyMSG.append("<center>Wait till the admin/gm start the participation.</center>");
				if (!eventPlayer._inEventCTF)
				{
					replyMSG.append("<br><br>");
					replyMSG.append("<center><button value=\"Watch event\" action=\"bypass -h npc_" + objectId + "_ctf_watch\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
					replyMSG.append("<br>");
				}
			}
			else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE") && !checkMaxPlayers(_playersShuffle.size()))
			{
				if (!_started)
				{
					replyMSG.append("Currently participated: <font color=\"1E90FF\">" + _playersShuffle.size() + ".</font><br>");
					replyMSG.append("Max players: <font color=\"1E90FF\">" + _maxPlayers + "</font><br>");
					replyMSG.append("<font color=\"1E90FF\">You can't participate to this event.</font><br>");
				}
			}
			else if (eventPlayer.isCursedWeaponEquipped() && !Config.CTF_JOIN_CURSED)
			{
				replyMSG.append("<font color=\"1E90FF\">You can't participate to this event with a cursed Weapon.</font><br>");
			}
			else if (!_started && _joining && eventPlayer.getLevel() >= _minlvl && eventPlayer.getLevel() <= _maxlvl)
			{
				synchronized (_players)
				{
					if (_players.contains(eventPlayer) || _playersShuffle.contains(eventPlayer) || checkShufflePlayers(eventPlayer))
					{
						if (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE"))
							replyMSG.append("You participated already in team <font color=\"1E90FF\">" + eventPlayer._teamNameCTF + "</font><br><br>");
						else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
							replyMSG.append("<center><font color=\"LEVEL\">You participated already!</font></center><br>");
						
						replyMSG.append("<center>Joined Players: <font color=\"1E90FF\">" + _playersShuffle.size() + "</font></center><br>");
						
						replyMSG.append("<center><button value=\"Remove\" action=\"bypass -h npc_" + objectId + "_ctf_player_leave\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
						replyMSG.append("<center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br1>");
					}
					else
					{
						replyMSG.append("<center><font color=\"LEVEL\">You want to participate in the event?</font></center><br>");
						replyMSG.append("<center><td width=\"200\">Min. level: <font color=\"1E90FF\">" + _minlvl + "</font></center></td><br>");
						replyMSG.append("<center><td width=\"200\">Max. level: <font color=\"1E90FF\">" + _maxlvl + "</font></center></td><br>");
						replyMSG.append("<center><font color=\"LEVEL\">Teams: </font></center>");
						
						if (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE"))
						{
							replyMSG.append("<center><table border=\"0\">");
							
							for (String team : _teams)
							{
								replyMSG.append("<tr><td width=\"100\"><font color=\"1E90FF\">" + team + "</font>&nbsp;(" + teamPlayersCount(team) + " joined.)</td>");
								replyMSG.append("<center><td width=\"60\"><button value=\"Join\" action=\"bypass -h npc_" + objectId + "_ctf_player_join " + team + "\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center></td></tr>");
							}
							replyMSG.append("</table></center>");
						}
						else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
						{
							replyMSG.append("<center>");
							
							for (String team : _teams)
								replyMSG.append("<tr><td width=\"100\"><font color=\"1E90FF\">" + team + "</font> &nbsp;</td>");
							
							replyMSG.append("</center><br>");
							
							replyMSG.append("<center><button value=\"Join Event\" action=\"bypass -h npc_" + objectId + "_ctf_player_join eventShuffle\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
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
				if (!eventPlayer._inEventCTF)
				{
					replyMSG.append("<br><br>");
					replyMSG.append("<center><button value=\"Watch event\" action=\"bypass -h npc_" + objectId + "_ctf_watch\" width=134 height=21 back=\"L2UI_ch3.bigbutton3_over\" fore=\"L2UI_ch3.bigbutton3\"></center>");
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
			LOGGER.info("Engine[showEventHtlm(" + eventPlayer.getName() + ", " + objectId + ")]: exception" + e.getMessage());
		}
	}
	
	/**
	 * Adds the player.
	 * @param player the player
	 * @param teamName the team name
	 */
	public static void addPlayer(final Player player, final String teamName)
	{
		if (!addPlayerOk(teamName, player))
			return;
		
		synchronized (_players)
		{
			if (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE"))
			{
				player._teamNameCTF = teamName;
				_players.add(player);
				setTeamPlayersCount(teamName, teamPlayersCount(teamName) + 1);
			}
			else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE"))
				_playersShuffle.add(player);
			
		}
		
		player._inEventCTF = true;
		player._countCTFflags = 0;
		player.sendMessage(_eventName + ": You successfully registered for the event.");
	}
	
	/**
	 * Removes the player.
	 * @param player the player
	 */
	public static void removePlayer(final Player player)
	{
		if (player._inEventCTF)
		{
			if (!_joining)
			{
				player.getAppearance().setNameColor(player._originalNameColorCTF);
				player.setTitle(player._originalTitleCTF);
				player.setKarma(player._originalKarmaCTF);
				if (Config.CTF_AURA)
				{
					if (_teams.size() >= 2)
						player.setTeam(0);// clear aura :P
				}
				player.broadcastUserInfo();
			}
			
			// after remove, all event data must be cleaned in player
			player._originalNameColorCTF = 0;
			player._originalTitleCTF = null;
			player._originalKarmaCTF = 0;
			player._teamNameCTF = new String();
			player._countCTFflags = 0;
			player._inEventCTF = false;
			
			synchronized (_players)
			{
				if ((Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE")) && _players.contains(player))
				{
					setTeamPlayersCount(player._teamNameCTF, teamPlayersCount(player._teamNameCTF) - 1);
					_players.remove(player);
				}
				else if (Config.CTF_EVEN_TEAMS.equals("SHUFFLE") && (!_playersShuffle.isEmpty() && _playersShuffle.contains(player)))
					_playersShuffle.remove(player);
				
			}
			
			player.sendMessage("Your participation in the CTF event has been removed.");
		}
	}
	
	/**
	 * Clean ctf.
	 */
	public static void cleanCTF()
	{
		synchronized (_players)
		{
			for (final Player player : _players)
			{
				if (player != null)
				{
					removePlayer(player);
					if (_savePlayers.contains(player.getName()))
						_savePlayers.remove(player.getName());
					player._inEventCTF = false;
				}
			}
		}
		
		if (_playersShuffle != null && !_playersShuffle.isEmpty())
		{
			for (final Player player : _playersShuffle)
			{
				if (player != null)
					player._inEventCTF = false;
			}
		}
		
		_topScore = 0;
		_topTeam = new String();
		synchronized (_players)
		{
			_players = new Vector<>();
		}
		
		_playersShuffle = new Vector<>();
		_savePlayers = new Vector<>();
		_savePlayerTeams = new Vector<>();
		
		_teamPointsCount = new Vector<>();
		_teamPlayersCount = new Vector<>();
		
		cleanLocalEventInfo();
		
		_inProgress = false;
		
		loadData();
	}
	
	/**
	 * Clean local event info.
	 */
	private static void cleanLocalEventInfo()
	{
		
		_flagSpawns = new Vector<>();
		_flagsTaken = new Vector<>();
		
	}
	
	/**
	 * Adds the disconnected player.
	 * @param player the player
	 */
	public static synchronized void addDisconnectedPlayer(final Player player)
	{
		if ((Config.CTF_EVEN_TEAMS.equals("SHUFFLE") && (_teleport || _started)) || (Config.CTF_EVEN_TEAMS.equals("NO") || Config.CTF_EVEN_TEAMS.equals("BALANCE") && (_teleport || _started)))
		{
			if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
			{
				player.stopAllEffects();
			}
			
			player._teamNameCTF = _savePlayerTeams.get(_savePlayers.indexOf(player.getName()));
			
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
						player._originalNameColorCTF = player.getAppearance().getNameColor();
						player._originalTitleCTF = player.getTitle();
						player._originalKarmaCTF = player.getKarma();
						player._inEventCTF = true;
						player._countCTFflags = p._countCTFflags;
						player.setLastCords(player.getX(), player.getY(), player.getZ());
						_players.remove(p); // removing old object id from vector
						_players.add(player); // adding new objectId to vector
						break;
					}
				}
			}
			
			player.getAppearance().setNameColor(_teamColors.get(_teams.indexOf(player._teamNameCTF)));
			player.setKarma(0);
			if (Config.CTF_AURA)
			{
				if (_teams.size() >= 2)
					player.setTeam(_teams.indexOf(player._teamNameCTF) + 1);
			}
			player.broadcastUserInfo();
			
			final int offset = Config.CTF_SPAWN_OFFSET;
			player.teleToLocation(_teamsX.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(offset), _teamsY.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(offset), _teamsZ.get(_teams.indexOf(player._teamNameCTF)), 50);
			
			if (Config.TVT_SKILL_PROTECT)
			{
				for (L2Effect effect : player.getAllEffects())
				{
					
					if (Config.TVT_SKILL_LIST.contains(effect.getSkill().getId()))
						player.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			afterAddDisconnectedPlayerOperations(player);
			
		}
	}
	
	/**
	 * After add disconnected player operations.
	 * @param player the player
	 */
	private static void afterAddDisconnectedPlayerOperations(final Player player)
	{
		player._teamNameHaveFlagCTF = null;
		player._haveFlagCTF = false;
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
				
				final int playerToAddIndex = Rnd.nextInt(_playersShuffle.size());
				Player player = null;
				player = _playersShuffle.get(playerToAddIndex);
				
				_players.add(player);
				_players.get(playersCount)._teamNameCTF = _teams.get(teamCount);
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
	public static void playKneelAnimation(final String teamName)
	{
		synchronized (_players)
		{
			for (final Player player : _players)
			{
				if (player != null && player.isOnline() && !player.isDead())
				{
					if (!player._teamNameCTF.equals(teamName))
					{
						player.broadcastPacket(new SocialAction(player.getObjectId(), 7));
					}
					else if (player._teamNameCTF.equals(teamName))
					{
						player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
					}
				}
			}
		}
		
	}
	
	/**
	 * Reward team.
	 * @param teamName the team name
	 */
	public static void rewardTeam(final String teamName)
	{
		synchronized (_players)
		{
			for (final Player player : _players)
			{
				if (player != null && (player.isOnline()) && (player._inEventCTF))
				{
					if (teamName != null && (player._teamNameCTF.equals(teamName)))
					{
						
						if (player.isVip())
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount * Config.RATE_DROP_VIP, player, true);
						else
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount, player, true);
						
						final NpcHtmlMessage nhm = new NpcHtmlMessage(5);
						final StringBuilder replyMSG = new StringBuilder("");
						
						replyMSG.append("<html><body>");
						replyMSG.append("<font color=\"FFFF00\">Your team wins the event. Look in your inventory for the reward.</font>");
						replyMSG.append("</body></html>");
						
						nhm.setHtml(replyMSG.toString());
						player.sendPacket(nhm);
						
						// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
						player.sendPacket(ActionFailed.STATIC_PACKET);
						
					}
					else if (teamName == null)
					{ // TIE
					
						if (player.isVip())
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount * Config.RATE_DROP_VIP, player, true);
						else
							player.addItem(_eventName + " Event: " + _eventName, _rewardId, _rewardAmount, player, true);
						
						final NpcHtmlMessage nhm = new NpcHtmlMessage(5);
						final StringBuilder replyMSG = new StringBuilder("");
						
						replyMSG.append("<html><body>");
						replyMSG.append("<font color=\"FFFF00\">Your team had a tie in the event. Look in your inventory for the reward.</font>");
						replyMSG.append("</body></html>");
						
						nhm.setHtml(replyMSG.toString());
						player.sendPacket(nhm);
						
						// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
						player.sendPacket(ActionFailed.STATIC_PACKET);
						
					}
				}
			}
		}
		
		/*
		 * for(Player player : _players) { if(player != null && (player.isOnline() != 0) && (player._inEventCTF == true) && (player._teamNameCTF.equals(teamName))) { player.addItem(_eventName+" Event: " + _eventName, _rewardId, _rewardAmount, player, true); NpcHtmlMessage nhm = new
		 * NpcHtmlMessage(5); TextBuilder replyMSG = new TextBuilder(""); replyMSG.append("<html><body>"); replyMSG.append("<font color=\"FFFF00\">Your team wins the event. Look in your inventory for the reward.</font>"); replyMSG.append("</body></html>"); nhm.setHtml(replyMSG.toString());
		 * player.sendPacket(nhm); // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet player.sendPacket( ActionFailed.STATIC_PACKET ); } }
		 */
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
		for (final String team : _teams)
		{
			if (teamPointsCount(team) == _topScore && _topScore > 0)
				_topTeam = null;
			
			if (teamPointsCount(team) > _topScore)
			{
				_topTeam = team;
				_topScore = teamPointsCount(team);
			}
		}
	}
	
	/**
	 * Adds the team.
	 * @param teamName the team name
	 */
	public static void addTeam(final String teamName)
	{
		if (is_inProgress())
		{
			LOGGER.info(_eventName + " Engine[addTeam(" + teamName + ")]: checkTeamOk() = false");
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
	private static void addTeamEventOperations(final String teamName)
	{
		
		addOrSet(_teams.indexOf(teamName), null, false, _FlagNPC, 0, 0, 0);
		
	}
	
	/**
	 * Removes the team.
	 * @param teamName the team name
	 */
	public static void removeTeam(final String teamName)
	{
		if (is_inProgress() || _teams.isEmpty())
		{
			LOGGER.info(_eventName + " Engine[removeTeam(" + teamName + ")]: checkTeamOk() = false");
			return;
		}
		
		if (teamPlayersCount(teamName) > 0)
		{
			LOGGER.info(_eventName + " Engine[removeTeam(" + teamName + ")]: teamPlayersCount(teamName) > 0");
			return;
		}
		
		final int index = _teams.indexOf(teamName);
		
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
	private static void removeTeamEventItems(final String teamName)
	{
		
		final int index = _teams.indexOf(teamName);
		
		_flagSpawns.remove(index);
		_flagsTaken.remove(index);
		_flagIds.remove(index);
		_flagsX.remove(index);
		_flagsY.remove(index);
		_flagsZ.remove(index);
		
	}
	
	/**
	 * Sets the team pos.
	 * @param teamName the team name
	 * @param activeChar the active char
	 */
	public static void setTeamPos(final String teamName, final Player activeChar)
	{
		final int index = _teams.indexOf(teamName);
		
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
	public static void setTeamPos(final String teamName, final int x, final int y, final int z)
	{
		final int index = _teams.indexOf(teamName);
		
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
	public static void setTeamColor(final String teamName, final int color)
	{
		if (is_inProgress())
			return;
		
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamColors.set(index, color);
	}
	
	/**
	 * Team players count.
	 * @param teamName the team name
	 * @return the int
	 */
	public static int teamPlayersCount(final String teamName)
	{
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return -1;
		
		return _teamPlayersCount.get(index);
	}
	
	/**
	 * Sets the team players count.
	 * @param teamName the team name
	 * @param teamPlayersCount the team players count
	 */
	public static void setTeamPlayersCount(final String teamName, final int teamPlayersCount)
	{
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamPlayersCount.set(index, teamPlayersCount);
	}
	
	/**
	 * Check shuffle players.
	 * @param eventPlayer the event player
	 * @return true, if successful
	 */
	public static boolean checkShufflePlayers(final Player eventPlayer)
	{
		try
		{
			for (final Player player : _playersShuffle)
			{
				if (player == null || !player.isOnline())
				{
					_playersShuffle.remove(player);
					eventPlayer._inEventCTF = false;
					continue;
				}
				else if (player.getObjectId() == eventPlayer.getObjectId())
				{
					eventPlayer._inEventCTF = true;
					eventPlayer._countCTFflags = 0;
					return true;
				}
				
				// This 1 is incase player got new objectid after DC or reconnect
				else if (player.getName().equals(eventPlayer.getName()))
				{
					_playersShuffle.remove(player);
					_playersShuffle.add(eventPlayer);
					eventPlayer._inEventCTF = true;
					eventPlayer._countCTFflags = 0;
					return true;
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * just an announcer to send termination messages.
	 */
	public static void sendFinalMessages()
	{
		if (!_started && !_aborted)
			Announcement.AnnounceEvents(_eventName + ": Thank you For Participating At, " + _eventName + " Event.");
	}
	
	/**
	 * returns the interval between each event.
	 * @return the interval between matches
	 */
	public static int getIntervalBetweenMatchs()
	{
		final long actualTime = System.currentTimeMillis();
		final long totalTime = actualTime + _intervalBetweenMatches;
		final long interval = totalTime - actualTime;
		final int seconds = (int) (interval / 1000);
		
		return seconds / 60;
	}
	
	@Override
	public void run()
	{
		LOGGER.info(_eventName + ": Event notification start");
		eventOnceStart();
	}
	
	@Override
	public String getEventIdentifier()
	{
		return _eventName;
	}
	
	@Override
	public String getEventStartTime()
	{
		return startEventTime;
	}
	
	/**
	 * Sets the event start time.
	 * @param newTime the new event start time
	 */
	public void setEventStartTime(final String newTime)
	{
		startEventTime = newTime;
	}
	
	/**
	 * On disconnect.
	 * @param player the player
	 */
	public static void onDisconnect(final Player player)
	{
		if (player._inEventCTF)
		{
			if (player._haveFlagCTF)
				player.removeCTFFlagOnDie();
			
			removePlayer(player);
			if (_started || _teleport)
				player.teleToLocation(player.getLastX(), player.getLastY(), player.getLastZ(), 50);
		}
	}
	
	/**
	 * Team points count.
	 * @param teamName the team name
	 * @return the int
	 */
	public static int teamPointsCount(final String teamName)
	{
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return -1;
		
		return _teamPointsCount.get(index);
	}
	
	/**
	 * Sets the team points count.
	 * @param teamName the team name
	 * @param teamPointCount the team point count
	 */
	public static void setTeamPointsCount(final String teamName, final int teamPointCount)
	{
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		
		_teamPointsCount.set(index, teamPointCount);
	}
	
	/**
	 * Gets the _event offset.
	 * @return the _eventOffset
	 */
	public static int get_eventOffset()
	{
		return _eventOffset;
	}
	
	/**
	 * Set_event offset.
	 * @param _eventOffset the _eventOffset to set
	 * @return true, if successful
	 */
	public static boolean set_eventOffset(final int _eventOffset)
	{
		if (!is_inProgress())
		{
			CTF._eventOffset = _eventOffset;
			return true;
		}
		return false;
	}
	
	/**
	 * Show flag html.
	 * @param eventPlayer the event player
	 * @param objectId the object id
	 * @param teamName the team name
	 */
	public static void showFlagHtml(final Player eventPlayer, final String objectId, final String teamName)
	{
		if (eventPlayer == null)
			return;
		
		try
		{
			final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			
			final StringBuilder replyMSG = new StringBuilder("<html><head><body><center>");
			replyMSG.append("CTF Flag<br><br>");
			replyMSG.append("<font color=\"00FF00\">" + teamName + "'s Flag</font><br1>");
			if (eventPlayer._teamNameCTF != null && eventPlayer._teamNameCTF.equals(teamName))
				replyMSG.append("<font color=\"LEVEL\">This is your Flag</font><br1>");
			else
				replyMSG.append("<font color=\"LEVEL\">Enemy Flag!</font><br1>");
			if (_started)
			{
				processInFlagRange(eventPlayer);
			}
			else
				replyMSG.append("CTF match is not in progress yet.<br>Wait for a GM to start the event<br>");
			replyMSG.append("</center></body></html>");
			adminReply.setHtml(replyMSG.toString());
			eventPlayer.sendPacket(adminReply);
		}
		catch (final Exception e)
		{
			LOGGER.info("CTF Engine[showEventHtlm(" + eventPlayer.getName() + ", " + objectId + ")]: exception: " + e.getStackTrace());
		}
	}
	
	/**
	 * Adds the flag to player.
	 * @param _player the _player
	 */
	public static void addFlagToPlayer(final Player _player)
	{
		// Remove items from the player hands (right, left, both)
		// This is NOT a BUG, I don't want them to see the icon they have 8D
		ItemInstance wpn = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (wpn != null)
		{
			ItemInstance[] unequipped = _player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance element : unequipped)
				iu.addModifiedItem(element);
			_player.sendPacket(iu);
		}
		
		// Add the flag in his hands
		_player.getInventory().equipItem(ItemTable.getInstance().createItem("", CTF._FLAG_IN_HAND_ITEM_ID, 1, _player, null));
		_player.sendPacket(new ItemList(_player, false)); // Get your weapon back now ...
		_player.broadcastPacket(new SocialAction(_player.getObjectId(), 16)); // Amazing glow
		_player._haveFlagCTF = true;
		_player.broadcastUserInfo();
		final CreatureSay cs = new CreatureSay(_player.getObjectId(), 15, ":", "You got it! Run back! ::"); // 8D
		_player.sendPacket(cs);
	}
	
	/**
	 * Removes the flag from player.
	 * @param player the player
	 */
	public static void removeFlagFromPlayer(final Player player)
	{
		final ItemInstance wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		player._haveFlagCTF = false;
		if (wpn != null)
		{
			final ItemInstance[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			player.getInventory().destroyItemByItemId("", CTF._FLAG_IN_HAND_ITEM_ID, 1, player, null);
			final InventoryUpdate iu = new InventoryUpdate();
			for (final ItemInstance element : unequiped)
				iu.addModifiedItem(element);
			player.sendPacket(iu);
			player.sendPacket(new ItemList(player, false)); // Get your weapon back now ...
			player.abortAttack();
			player.broadcastUserInfo();
		}
		else
		{
			player.getInventory().destroyItemByItemId("", CTF._FLAG_IN_HAND_ITEM_ID, 1, player, null);
			player.sendPacket(new ItemList(player, false)); // Get your weapon back now ...
			player.abortAttack();
			player.broadcastUserInfo();
		}
	}
	
	/**
	 * Sets the team flag.
	 * @param teamName the team name
	 * @param activeChar the active char
	 */
	public static void setTeamFlag(final String teamName, final Player activeChar)
	{
		final int index = _teams.indexOf(teamName);
		
		if (index == -1)
			return;
		addOrSet(_teams.indexOf(teamName), null, false, _FlagNPC, activeChar.getX(), activeChar.getY(), activeChar.getZ());
	}
	
	/**
	 * Spawn all flags.
	 */
	public static void spawnAllFlags()
	{
		while (_flagSpawns.size() < _teams.size())
			_flagSpawns.add(null);
		while (_throneSpawns.size() < _teams.size())
			_throneSpawns.add(null);
		for (final String team : _teams)
		{
			final int index = _teams.indexOf(team);
			final NpcTemplate tmpl = NpcTable.getInstance().getTemplate(_flagIds.get(index));
			final NpcTemplate throne = NpcTable.getInstance().getTemplate(32027);
			try
			{
				// Spawn throne
				_throneSpawns.set(index, new L2Spawn(throne));
				_throneSpawns.get(index).setLoc(_flagsX.get(index), _flagsY.get(index), _flagsZ.get(index) - 10, 0);
				_throneSpawns.get(index).setRespawnDelay(1);
				
				SpawnTable.getInstance().addNewSpawn(_throneSpawns.get(index), false);
				
				_throneSpawns.get(index).setRespawnState(true);
				_throneSpawns.get(index).doSpawn(false);
				_throneSpawns.get(index).getNpc().getStatus().setCurrentHp(99999);
				_throneSpawns.get(index).getNpc()._isCTF_throneSpawn = true;
				_throneSpawns.get(index).getNpc().setTitle(" ");
				_throneSpawns.get(index).getNpc().setName(" ");
				_throneSpawns.get(index).getNpc().decayMe();
				_throneSpawns.get(index).getNpc().spawnMe(_throneSpawns.get(index).getNpc().getX(), _throneSpawns.get(index).getNpc().getY(), _throneSpawns.get(index).getNpc().getZ());
				_throneSpawns.get(index).getNpc().broadcastPacket(new MagicSkillUse(_throneSpawns.get(index).getNpc(), _throneSpawns.get(index).getNpc(), 1036, 1, 5500, 1));
				
				// Spawn flag
				_flagSpawns.set(index, new L2Spawn(tmpl));
				_flagSpawns.get(index).setLoc(_flagsX.get(index), _flagsY.get(index), _flagsZ.get(index), 0);
				
				_flagSpawns.get(index).setRespawnDelay(1);
				SpawnTable.getInstance().addNewSpawn(_flagSpawns.get(index), false);
				
				_flagSpawns.get(index).setRespawnState(true);
				_flagSpawns.get(index).doSpawn(false);
				_flagSpawns.get(index).getNpc().getStatus().setCurrentHp(999999999);
				_flagSpawns.get(index).getNpc()._CTF_FlagTeamName = team;
				_flagSpawns.get(index).getNpc()._isCTF_Flag = true;
				_flagSpawns.get(index).getNpc().setTitle(team + "'s Flag");
				_flagSpawns.get(index).getNpc().decayMe();
				_flagSpawns.get(index).getNpc().spawnMe(_flagSpawns.get(index).getNpc().getX(), _flagSpawns.get(index).getNpc().getY(), _flagSpawns.get(index).getNpc().getZ());
				calculateOutSideOfCTF(); // Sets event boundaries so players don't run with the flag.
				
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Unspawn all flags.
	 */
	public static void unspawnAllFlags()
	{
		try
		{
			if (_throneSpawns == null || _flagSpawns == null || _teams == null)
				return;
			for (final String team : _teams)
			{
				final int index = _teams.indexOf(team);
				if (_throneSpawns.get(index) != null)
				{
					_throneSpawns.get(index).getNpc().deleteMe();
					_throneSpawns.get(index).setRespawnState(false);
					SpawnTable.getInstance().deleteSpawn(_throneSpawns.get(index), true);
				}
				if (_flagSpawns.get(index) != null)
				{
					_flagSpawns.get(index).getNpc().deleteMe();
					_flagSpawns.get(index).setRespawnState(false);
					SpawnTable.getInstance().deleteSpawn(_flagSpawns.get(index), true);
				}
			}
			_throneSpawns.removeAllElements();
		}
		catch (final Exception e)
		{
			LOGGER.info("CTF Engine[unspawnAllFlags()]: exception: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Unspawn flag.
	 * @param teamName the team name
	 */
	private static void unspawnFlag(final String teamName)
	{
		final int index = _teams.indexOf(teamName);
		_flagSpawns.get(index).getNpc().deleteMe();
		_flagSpawns.get(index).setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_flagSpawns.get(index), true);
	}
	
	/**
	 * Spawn flag.
	 * @param teamName the team name
	 */
	public static void spawnFlag(final String teamName)
	{
		final int index = _teams.indexOf(teamName);
		final NpcTemplate tmpl = NpcTable.getInstance().getTemplate(_flagIds.get(index));
		
		try
		{
			_flagSpawns.set(index, new L2Spawn(tmpl));
			_flagSpawns.get(index).setLoc(_flagsX.get(index), _flagsY.get(index), _flagsZ.get(index) - 10, 0);
			_flagSpawns.get(index).setRespawnDelay(1);
			
			SpawnTable.getInstance().addNewSpawn(_flagSpawns.get(index), false);
			
			_flagSpawns.get(index).setRespawnState(true);
			_flagSpawns.get(index).doSpawn(false);
			_flagSpawns.get(index).getNpc().getStatus().setCurrentHp(999999999);
			_flagSpawns.get(index).getNpc().setTitle(teamName + "'s Flag");
			_flagSpawns.get(index).getNpc()._CTF_FlagTeamName = teamName;
			_flagSpawns.get(index).getNpc()._isCTF_Flag = true;
			_flagSpawns.get(index).getNpc().decayMe();
			_flagSpawns.get(index).getNpc().spawnMe(_flagSpawns.get(index).getNpc().getX(), _flagSpawns.get(index).getNpc().getY(), _flagSpawns.get(index).getNpc().getZ());
		}
		catch (final Exception e)
		{
			LOGGER.info("CTF Engine[spawnFlag(" + teamName + ")]: exception: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * In range of flag.
	 * @param _player the _player
	 * @param flagIndex the flag index
	 * @param offset the offset
	 * @return true, if successful
	 */
	public static boolean InRangeOfFlag(final Player _player, final int flagIndex, final int offset)
	{
		if (_player.getX() > CTF._flagsX.get(flagIndex) - offset && _player.getX() < CTF._flagsX.get(flagIndex) + offset && _player.getY() > CTF._flagsY.get(flagIndex) - offset && _player.getY() < CTF._flagsY.get(flagIndex) + offset && _player.getZ() > CTF._flagsZ.get(flagIndex) - offset && _player.getZ() < CTF._flagsZ.get(flagIndex) + offset)
			return true;
		return false;
	}
	
	/**
	 * Process in flag range.
	 * @param _player the _player
	 */
	public static void processInFlagRange(final Player _player)
	{
		try
		{
			for (final String team : _teams)
			{
				if (team.equals(_player._teamNameCTF))
				{
					final int indexOwn = _teams.indexOf(_player._teamNameCTF);
					
					// If player is near his team flag holding the enemy flag
					if (InRangeOfFlag(_player, indexOwn, 150) && !_flagsTaken.get(indexOwn) && _player._haveFlagCTF)
					{
						final int indexEnemy = _teams.indexOf(_player._teamNameHaveFlagCTF);
						// Return enemy flag to place
						_flagsTaken.set(indexEnemy, false);
						
						// Remove the flag from this player
						_player.broadcastPacket(new SocialAction(_player.getObjectId(), 16)); // Amazing glow
						_player.broadcastUserInfo();
						_player.broadcastPacket(new SocialAction(_player.getObjectId(), 3)); // Victory
						_player.broadcastUserInfo();
						removeFlagFromPlayer(_player);
						_teamPointsCount.set(indexOwn, teamPointsCount(team) + 1);
						Announcement.AnnounceEvents(""+Config.NAME_CTF+" " + _player.getName() + " scores for " + _player._teamNameCTF + ".");
						
						for (Player player : _players)
						{
							player.sendPacket(new ExShowScreenMessage(_player.getName() + " scores for " + _player._teamNameCTF + " !", 3000));
						}
						
						Announce("**** Team Statistics ****");
						for (final String Statistics : _teams)
						{
							int _flag = teamPointsCount(Statistics);
							Announce("Team: " + Statistics + " - Flags taken: " + _flag);
						}
						
						ThreadPool.schedule(new Runnable()
						{
							@Override
							public void run()
							{
								Announcement.AnnounceEvents(""+Config.NAME_CTF+" The teams were repositioned at their bases..");
								
								for (final Player player : World.getInstance().getPlayers())
								{
									if (player != null && player.isOnline() && player._inEventCTF)
									{
										if (player.isDead())
											player.doRevive();
										
										final int offset = Config.CTF_SPAWN_OFFSET;
										player.teleToLocation(_teamsX.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(-offset, offset), _teamsY.get(_teams.indexOf(player._teamNameCTF)) + Rnd.get(-offset, offset), _teamsZ.get(_teams.indexOf(player._teamNameCTF)), 0);
										
										if (Config.CTF_ON_START_REMOVE_ALL_EFFECTS)
											player.stopAllEffects();
										
										player.add_buff_event();
										player._reuseTimeStamps.clear();
										player.getDisabledSkills().clear();
										player.sendPacket(new SkillCoolTime(player));
										
										player.stopMove(null);
										player.setIsInvul(true);
										player.setStopArena(true);
										
										if (player._haveFlagCTF)
											removeFlagFromPlayer(player);				
	
									}
								}
								
								spawnFlag(_player._teamNameHaveFlagCTF);
																
								stop_batte(20 * 1000);
								
								if (_started)
								{
									for (Player player : _players)
									{
										if (Config.SCREN_MSG)
											player.sendPacket(new ExShowScreenMessage("Capture the Flags!!", 3 * 1000, ExShowScreenMessage.SMPOS.MIDDLE_LEFT, false));
										player.setIsInvul(false);
										player.setStopArena(false);
									}
								}
								
							}
						}, 1500);
						
					}
				}
				else
				{
					final int indexEnemy = _teams.indexOf(team);
					// If the player is near a enemy flag
					if (InRangeOfFlag(_player, indexEnemy, 150) && !_flagsTaken.get(indexEnemy) && !_player._haveFlagCTF && !_player.isDead())
					{
						_flagsTaken.set(indexEnemy, true);
						unspawnFlag(team);
						_player._teamNameHaveFlagCTF = team;
						addFlagToPlayer(_player);
						_player.broadcastUserInfo();
						_player._haveFlagCTF = true;
						Announcement.AnnounceEvents(""+Config.NAME_CTF+" " + team + " flag taken by " + _player.getName() + "...");
						pointTeamTo(_player, team);
						break;
					}
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Point team to.
	 * @param hasFlag the has flag
	 * @param ourFlag the our flag
	 */
	public static void pointTeamTo(final Player hasFlag, final String ourFlag)
	{
		try
		{
			synchronized (_players)
			{
				for (final Player player : _players)
				{
					if (player != null && player.isOnline())
					{
						if (player._teamNameCTF.equals(ourFlag))
						{
							player.sendMessage(hasFlag.getName() + " took your flag!");
							if (player._haveFlagCTF)
							{
								player.sendMessage("You can not return the flag to headquarters, until your flag is returned to it's place.");
								player.sendPacket(new RadarControl(1, 1, player.getX(), player.getY(), player.getZ()));
							}
							else
							{
								player.sendPacket(new RadarControl(0, 1, hasFlag.getX(), hasFlag.getY(), hasFlag.getZ()));
								final L2Radar rdr = new L2Radar(player);
								final L2Radar.RadarOnPlayer radar = rdr.new RadarOnPlayer(hasFlag, player);
								ThreadPool.schedule(radar, 10000 + Rnd.get(30000));
							}
						}
					}
				}
			}
			
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Adds the or set.
	 * @param listSize the list size
	 * @param flagSpawn the flag spawn
	 * @param flagsTaken the flags taken
	 * @param flagId the flag id
	 * @param flagX the flag x
	 * @param flagY the flag y
	 * @param flagZ the flag z
	 */
	private static void addOrSet(final int listSize, final L2Spawn flagSpawn, final boolean flagsTaken, final int flagId, final int flagX, final int flagY, final int flagZ)
	{
		while (_flagsX.size() <= listSize)
		{
			_flagSpawns.add(null);
			_flagsTaken.add(false);
			_flagIds.add(_FlagNPC);
			_flagsX.add(0);
			_flagsY.add(0);
			_flagsZ.add(0);
		}
		_flagSpawns.set(listSize, flagSpawn);
		_flagsTaken.set(listSize, flagsTaken);
		_flagIds.set(listSize, flagId);
		_flagsX.set(listSize, flagX);
		_flagsY.set(listSize, flagY);
		_flagsZ.set(listSize, flagZ);
	}
	
	/**
	 * Used to calculate the event CTF area, so that players don't run off with the flag. Essential, since a player may take the flag just so other teams can't score points. This function is Only called upon ONE time on BEGINING OF EACH EVENT right after we spawn the flags.
	 */
	private static void calculateOutSideOfCTF()
	{
		if (_teams == null || _flagSpawns == null || _teamsX == null || _teamsY == null || _teamsZ == null)
			return;
		final int division = _teams.size() * 2;
		int pos = 0;
		final int[] locX = new int[division], locY = new int[division], locZ = new int[division];
		// Get all coordinates inorder to create a polygon:
		for (final L2Spawn flag : _flagSpawns)
		{
			if (flag == null)
				continue;
			
			locX[pos] = flag.getLocX();
			locY[pos] = flag.getLocY();
			locZ[pos] = flag.getLocZ();
			pos++;
			if (pos > division / 2)
				break;
		}
		for (int x = 0; x < _teams.size(); x++)
		{
			locX[pos] = _teamsX.get(x);
			locY[pos] = _teamsY.get(x);
			locZ[pos] = _teamsZ.get(x);
			pos++;
			if (pos > division)
				break;
		}
		// Find the polygon center, note that it's not the mathematical center of the polygon,
		// Rather than a point which centers all coordinates:
		int centerX = 0, centerY = 0, centerZ = 0;
		for (int x = 0; x < pos; x++)
		{
			centerX += (locX[x] / division);
			centerY += (locY[x] / division);
			centerZ += (locZ[x] / division);
		}
		// Now let's find the furthest distance from the "center" to the egg shaped sphere
		// Surrounding the polygon, size x1.5 (for maximum logical area to wander...):
		int maxX = 0, maxY = 0, maxZ = 0;
		for (int x = 0; x < pos; x++)
		{
			if (maxX < 2 * Math.abs(centerX - locX[x]))
				maxX = (2 * Math.abs(centerX - locX[x]));
			if (maxY < 2 * Math.abs(centerY - locY[x]))
				maxY = (2 * Math.abs(centerY - locY[x]));
			if (maxZ < 2 * Math.abs(centerZ - locZ[x]))
				maxZ = (2 * Math.abs(centerZ - locZ[x]));
		}
		
		// CenterX,centerY,centerZ are the coordinates of the "event center".
		// So let's save those coordinates to check on the players:
		_eventCenterX = centerX;
		_eventCenterY = centerY;
		_eventCenterZ = centerZ;
		_eventOffset = maxX;
		if (_eventOffset < maxY)
			_eventOffset = maxY;
		if (_eventOffset < maxZ)
			_eventOffset = maxZ;
	}
	
	/**
	 * Checks if is outside ctf area.
	 * @param _player the _player
	 * @return true, if is outside ctf area
	 */
	public static boolean isOutsideCTFArea(final Player _player)
	{
		if (_player == null || !_player.isOnline())
			return true;
		if (!(_player.getX() > _eventCenterX - _eventOffset && _player.getX() < _eventCenterX + _eventOffset && _player.getY() > _eventCenterY - _eventOffset && _player.getY() < _eventCenterY + _eventOffset && _player.getZ() > _eventCenterZ - _eventOffset && _player.getZ() < _eventCenterZ + _eventOffset))
			return true;
		return false;
	}
	
	/**
	 * Waiter.
	 * @param interval the interval
	 */
	protected static void stop_batte(final long interval)
	{
		final long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && _started)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 30: // 30 seconds left
				case 15: // 15 seconds left
				case 10: // 10 seconds left
				case 3: // 3 seconds left
				case 2: // 2 seconds left
				case 1: // 1 seconds left
				{
					if (_started)
					{
						for (Player player : _players)
						{
							player.sendPacket(new ExShowScreenMessage(seconds + " seconds(s) till start fight!", 5000));
							player.sendMessage(seconds + " seconds(s) till start fight!");
						}
					}
				}
					
					break;
			}
			
			final long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (final InterruptedException ie)
				{
					ie.printStackTrace();
				}
			}
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
		      LOGGER.warning("Error, " + ie.getMessage());
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
		      LOGGER.warning("Error, " + ie.getMessage());
		    } 
		  }
	
	public static void Announce(String text)
	{
		final CreatureSay a = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "", text); // 8D
		final CreatureSay b = new CreatureSay(0, Config.PVP_COLOR_ANNOUNCE, "", text); // 8D
		
		for (final Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline() && player._inEventCTF)
			{
				if (Config.PVP_COLOR_ANNOUNCE == 18 || Config.PVP_COLOR_ANNOUNCE == 10)
					player.sendPacket(a);
				else
					player.sendPacket(b);
			}
		}
	}
	
	public static void removeCTFFlagOnDie(final Player player)
	{
		CTF._flagsTaken.set(CTF._teams.indexOf(player._teamNameHaveFlagCTF), false);
		CTF.spawnFlag(player._teamNameHaveFlagCTF);
		removeFlagFromPlayer(player);
		player.broadcastUserInfo();
		player._haveFlagCTF = false;
	}
	
}