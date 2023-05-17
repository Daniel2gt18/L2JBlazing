package com.l2jmega.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.logging.CLogger;
import com.l2jmega.commons.math.MathUtil;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.communitybbs.BB.Forum;
import com.l2jmega.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.cache.CrestCache;
import com.l2jmega.gameserver.data.cache.CrestCache.CrestType;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.TimeStamp;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.entity.Siege;
import com.l2jmega.gameserver.model.entity.Siege.SiegeSide;
import com.l2jmega.gameserver.model.itemcontainer.ClanWarehouse;
import com.l2jmega.gameserver.model.itemcontainer.ItemContainer;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.network.serverpackets.PledgeReceiveSubPledgeCreated;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListAll;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import com.l2jmega.gameserver.network.serverpackets.PledgeSkillList;
import com.l2jmega.gameserver.network.serverpackets.PledgeSkillListAdd;
import com.l2jmega.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.network.serverpackets.UserInfo;

public class Clan
{
	private static final CLogger LOGGER = new CLogger(Clan.class.getName());
	
	private static final String LOAD_MEMBERS = "SELECT char_name,level,classid,obj_Id,title,power_grade,subpledge,apprentice,sponsor,sex,race FROM characters WHERE clanid=?";
	private static final String LOAD_SUBPLEDGES = "SELECT sub_pledge_id,name,leader_id FROM clan_subpledges WHERE clan_id=?";
	private static final String LOAD_PRIVILEDGES = "SELECT privs,rank FROM clan_privs WHERE clan_id=?";
	private static final String LOAD_SKILLS = "SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?";
	
	private static final String REMOVE_MEMBER = "UPDATE characters SET clanid=0, title=?, clan_join_expiry_time=?, clan_create_expiry_time=?, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0 WHERE obj_Id=?";
	private static final String REMOVE_APPRENTICE = "UPDATE characters SET apprentice=0 WHERE apprentice=?";
	private static final String REMOVE_SPONSOR = "UPDATE characters SET sponsor=0 WHERE sponsor=?";
	
	private static final String UPDATE_CLAN = "UPDATE clan_data SET leader_id=?,ally_id=?,ally_name=?,reputation_score=?,ally_penalty_expiry_time=?,ally_penalty_type=?,char_penalty_expiry_time=?,dissolving_expiry_time=?,clan_name=? WHERE clan_id=?";
	private static final String STORE_CLAN = "INSERT INTO clan_data (clan_id,clan_name,clan_level,hasCastle,ally_id,ally_name,leader_id,crest_id,crest_large_id,ally_crest_id) values (?,?,?,?,?,?,?,?,?,?)";
	
	private static final String UPDATE_NOTICE = "UPDATE clan_data SET enabled=?,notice=? WHERE clan_id=?";
	private static final String UPDATE_INTRODUCTION = "UPDATE clan_data SET introduction=? WHERE clan_id=?";
	
	private static final String ADD_OR_UPDATE_SKILL = "INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?) ON DUPLICATE KEY UPDATE skill_level=VALUES(skill_level)";
	
	private static final String INSERT_SUBPLEDGE = "INSERT INTO clan_subpledges (clan_id,sub_pledge_id,name,leader_id) values (?,?,?,?)";
	private static final String UPDATE_SUBPLEDGE = "UPDATE clan_subpledges SET leader_id=?, name=? WHERE clan_id=? AND sub_pledge_id=?";
	
	private static final String ADD_OR_UPDATE_PRIVILEDGE = "INSERT INTO clan_privs (clan_id,rank,privs) VALUES (?,?,?) ON DUPLICATE KEY UPDATE privs=VALUES(privs)";
	
	private static final String UPDATE_CRP = "UPDATE clan_data SET reputation_score=? WHERE clan_id=?";
	private static final String UPDATE_AUCTION = "UPDATE clan_data SET auction_bid_at=? WHERE clan_id=?";
	private static final String UPDATE_CLAN_LEVEL = "UPDATE clan_data SET clan_level = ? WHERE clan_id = ?";
	private static final String UPDATE_CLAN_CREST = "UPDATE clan_data SET crest_id = ? WHERE clan_id = ?";
	private static final String UPDATE_LARGE_CREST = "UPDATE clan_data SET crest_large_id = ? WHERE clan_id = ?";
	
	// Ally Penalty Types
	public static final int PENALTY_TYPE_CLAN_LEAVED = 1;
	public static final int PENALTY_TYPE_CLAN_DISMISSED = 2;
	public static final int PENALTY_TYPE_DISMISS_CLAN = 3;
	public static final int PENALTY_TYPE_DISSOLVE_ALLY = 4;
	
	// Clan Privileges
	public static final int CP_NOTHING = 0;
	public static final int CP_CL_JOIN_CLAN = 2;
	public static final int CP_CL_GIVE_TITLE = 4;
	public static final int CP_CL_VIEW_WAREHOUSE = 8;
	public static final int CP_CL_MANAGE_RANKS = 16;
	public static final int CP_CL_PLEDGE_WAR = 32;
	public static final int CP_CL_DISMISS = 64;
	public static final int CP_CL_REGISTER_CREST = 128;
	public static final int CP_CL_MASTER_RIGHTS = 256;
	public static final int CP_CL_MANAGE_LEVELS = 512;
	public static final int CP_CH_OPEN_DOOR = 1024;
	public static final int CP_CH_OTHER_RIGHTS = 2048;
	public static final int CP_CH_AUCTION = 4096;
	public static final int CP_CH_DISMISS = 8192;
	public static final int CP_CH_SET_FUNCTIONS = 16384;
	public static final int CP_CS_OPEN_DOOR = 32768;
	public static final int CP_CS_MANOR_ADMIN = 65536;
	public static final int CP_CS_MANAGE_SIEGE = 131072;
	public static final int CP_CS_USE_FUNCTIONS = 262144;
	public static final int CP_CS_DISMISS = 524288;
	public static final int CP_CS_TAXES = 1048576;
	public static final int CP_CS_MERCENARIES = 2097152;
	public static final int CP_CS_SET_FUNCTIONS = 4194304;
	public static final int CP_ALL = 8388606;
	
	// Sub-unit types
	public static final int SUBUNIT_ACADEMY = -1;
	public static final int SUBUNIT_ROYAL1 = 100;
	public static final int SUBUNIT_ROYAL2 = 200;
	public static final int SUBUNIT_KNIGHT1 = 1001;
	public static final int SUBUNIT_KNIGHT2 = 1002;
	public static final int SUBUNIT_KNIGHT3 = 2001;
	public static final int SUBUNIT_KNIGHT4 = 2002;
	
	private static final int MAX_NOTICE_LENGTH = 8192;
	private static final int MAX_INTRODUCTION_LENGTH = 300;
	
	private final Map<Integer, ClanMember> _members = new HashMap<>();
	private final Map<Integer, Long> _warPenaltyExpiryTime = new HashMap<>();
	private final Map<Integer, L2Skill> _skills = new HashMap<>();
	private final Map<Integer, Integer> _priviledges = new HashMap<>();
	private final Map<Integer, SubPledge> _subPledges = new HashMap<>();
	
	private final List<Integer> _atWarWith = new ArrayList<>();
	private final List<Integer> _atWarAttackers = new ArrayList<>();
	
	private final ItemContainer _warehouse = new ClanWarehouse(this);
	
	private String _name;
	private int _clanId;
	private ClanMember _leader;
	private String _allyName;
	private int _allyId;
	private int _level;
	private int _castleId;
	private int _hideoutId;
	private int _crestId;
	private int _crestLargeId;
	private int _allyCrestId;
	private int _auctionBiddedAt;
	private long _allyPenaltyExpiryTime;
	private int _allyPenaltyType;
	private long _charPenaltyExpiryTime;
	private long _dissolvingExpiryTime;
	
	private Forum _forum;
	
	private int _reputationScore;
	
	private int _clanBossScore;  
	private int _clan5x5Score; 
	private int _clan9x9Score;
	private int _rank;
	
	private String _notice;
	private boolean _noticeEnabled;
	
	private String _introduction;
	
	private int _siegeKills;
	private int _siegeDeaths;
	
	private Npc _flag;
	
	/**
	 * Called if a clan is referenced only by id. In this case all other data needs to be fetched from db
	 * @param clanId A valid clan Id to create and restore
	 * @param leaderId clan leader Id
	 */
	public Clan(int clanId, int leaderId)
	{
		_clanId = clanId;
		
		for (int i = 1; i < 10; i++)
			_priviledges.put(i, CP_NOTHING);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// Load members.
			PreparedStatement ps = con.prepareStatement(LOAD_MEMBERS);
			ps.setInt(1, _clanId);
			
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				ClanMember member = new ClanMember(this, rs);
				if (member.getObjectId() == leaderId)
					setLeader(member);
				else
					_members.put(member.getObjectId(), member);
				
				member.setApprenticeAndSponsor(rs.getInt("apprentice"), rs.getInt("sponsor"));
			}
			
		      restoreClanArena();
		      restoreClan5x5();
		      restoreClan9x9();
			
			rs.close();
			ps.close();
			// Load subpledges.
			ps = con.prepareStatement(LOAD_SUBPLEDGES);
			ps.setInt(1, _clanId);
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				int id = rs.getInt("sub_pledge_id");
				_subPledges.put(id, new SubPledge(id, rs.getString("name"), rs.getInt("leader_id")));
			}
			
			rs.close();
			ps.close();
			
			// Load priviledges.
			ps = con.prepareStatement(LOAD_PRIVILEDGES);
			ps.setInt(1, _clanId);
			
			rs = ps.executeQuery();
			while (rs.next())
				_priviledges.put(rs.getInt("rank"), rs.getInt("privs"));
			
			rs.close();
			ps.close();
			
			// Load clan skills.
			ps = con.prepareStatement(LOAD_SKILLS);
			ps.setInt(1, _clanId);
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				int id = rs.getInt("skill_id");
				int level = rs.getInt("skill_level");
				
				final L2Skill skill = SkillTable.getInstance().getInfo(id, level);
				if (skill == null)
					continue;
				
				_skills.put(id, skill);
			}
			
			rs.close();
			ps.close();
			
		}
		catch (Exception e)
		{
			LOGGER.error("Error while restoring clan.", e);
		}
		
		if (_crestId != 0 && CrestCache.getInstance().getCrest(CrestType.PLEDGE, _crestId) == null)
		{
			LOGGER.warn("Removing non-existent crest for clan {}, crestId: {}.", toString(), _crestId);
			changeClanCrest(0);
		}
		
		if (_crestLargeId != 0 && CrestCache.getInstance().getCrest(CrestType.PLEDGE_LARGE, _crestLargeId) == null)
		{
			LOGGER.warn("Removing non-existent large crest for clan {}, crestLargeId: {}.", toString(), _crestLargeId);
			changeLargeCrest(0);
		}
		
		if (_allyCrestId != 0 && CrestCache.getInstance().getCrest(CrestType.ALLY, _allyCrestId) == null)
		{
			LOGGER.warn("Removing non-existent ally crest for clan {}, allyCrestId: {}.", toString(), _allyCrestId);
			changeAllyCrest(0, true);
		}
		
		_warehouse.restore();
	}
	
	/**
	 * Called only if a new clan is created
	 * @param clanId A valid clan Id to create
	 * @param clanName A valid clan name
	 */
	public Clan(int clanId, String clanName)
	{
		_clanId = clanId;
		_name = clanName;
		
		for (int i = 1; i < 10; i++)
			_priviledges.put(i, CP_NOTHING);
		
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}
	
	/**
	 * @return the clanId.
	 */
	public int getClanId()
	{
		return _clanId;
	}
	
	/**
	 * @param clanId The clanId to set.
	 */
	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}
	
	public ClanMember getLeader()
	{
		return _leader;
	}
	
	/**
	 * @return the leaderId.
	 */
	public int getLeaderId()
	{
		return _leader.getObjectId();
	}
	
	/**
	 * @return L2ClanMember of clan leader.
	 */
	public String getLeaderName()
	{
		return (_leader == null) ? "" : _leader.getName();
	}
	
	/**
	 * @param leader The leader to set.
	 */
	public void setLeader(ClanMember leader)
	{
		_leader = leader;
		_members.put(leader.getObjectId(), leader);
	}
	
	public void setNewLeader(ClanMember member)
	{
		if (!_leader.isOnline())
			return;
		
		if (member == null || !member.isOnline())
			return;
		
		Player exLeader = _leader.getPlayerInstance();
		exLeader.removeSiegeSkills();
		exLeader.setClan(this);
		exLeader.setClanPrivileges(Clan.CP_NOTHING);
		exLeader.broadcastUserInfo();
		
		setLeader(member);
		updateClanInDB();
		
		exLeader.setPledgeClass(ClanMember.calculatePledgeClass(exLeader));
		exLeader.broadcastUserInfo();
		
		Player newLeader = member.getPlayerInstance();
		newLeader.setClan(this);
		newLeader.setPledgeClass(ClanMember.calculatePledgeClass(newLeader));
		newLeader.setClanPrivileges(Clan.CP_ALL);
		if (_level >= Config.MINIMUM_CLAN_LEVEL)
		{
			newLeader.addSiegeSkills();
			
			// Transfering siege skills TimeStamps from old leader to new leader to prevent unlimited headquarters
			if (!exLeader.getReuseTimeStamp().isEmpty())
			{
				for (L2Skill sk : SkillTable.getInstance().getSiegeSkills(newLeader.isNoble()))
				{
					if (exLeader.getReuseTimeStamp().containsKey(sk.getReuseHashCode()))
					{
						TimeStamp t = exLeader.getReuseTimeStamp().get(sk.getReuseHashCode());
						newLeader.addTimeStamp(sk, t.getReuse(), t.getStamp());
					}
				}
				newLeader.sendPacket(new SkillCoolTime(newLeader));
			}
		}
		newLeader.broadcastUserInfo();
		
		broadcastClanStatus();
		broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_S1).addCharName(newLeader));
	}
	
	public String getAllyName()
	{
		return _allyName;
	}
	
	public void setAllyName(String allyName)
	{
		_allyName = allyName;
	}
	
	public String getClanName()
	{
		return _name;
	}
	
	public void setClanName(String Name)
	{
		_name = Name;
	}
	
	public int getAllyId()
	{
		return _allyId;
	}
	
	public void setAllyId(int allyId)
	{
		_allyId = allyId;
	}
	
	public int getAllyCrestId()
	{
		return _allyCrestId;
	}
	
	public void setAllyCrestId(int allyCrestId)
	{
		_allyCrestId = allyCrestId;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @param level 
	 */
	public void setLevel(int level)
	{
		_level = level;
		
		if (Config.ENABLE_COMMUNITY_BOARD && _level >= 2 && _forum == null)
		{
			final Forum forum = ForumsBBSManager.getInstance().getForumByName("ClanRoot");
			if (forum != null)
			{
				_forum = forum.getChildByName(_name);
				if (_forum == null)
					_forum = ForumsBBSManager.getInstance().createNewForum(_name, forum, Forum.CLAN, Forum.CLANMEMBERONLY, _clanId);
			}
		}
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	public boolean hasCastle()
	{
		return _castleId > 0;
	}
	
	public void setCastle(int castle)
	{
		_castleId = castle;
	}
	
	public int getHideoutId()
	{
		return _hideoutId;
	}
	
	public boolean hasHideout()
	{
		return _hideoutId > 0;
	}
	
	public void setHideout(int hideout)
	{
		_hideoutId = hideout;
	}
	
	public int getCrestId()
	{
		return _crestId;
	}
	
	public void setCrestId(int crestId)
	{
		_crestId = crestId;
	}
	
	public int getCrestLargeId()
	{
		return _crestLargeId;
	}
	
	public void setCrestLargeId(int crestLargeId)
	{
		_crestLargeId = crestLargeId;
	}
	
	public int getSiegeKills()
	{
		return _siegeKills;
	}
	
	public int getSiegeDeaths()
	{
		return _siegeDeaths;
	}
	
	public void setSiegeKills(int value)
	{
		_siegeKills = value;
	}
	
	public void setSiegeDeaths(int value)
	{
		_siegeDeaths = value;
	}
	
	public Npc getFlag()
	{
		return _flag;
	}
	
	public void setFlag(Npc flag)
	{
		// If we set flag as null and clan flag is currently existing, delete it.
		if (flag == null && _flag != null)
			_flag.deleteMe();
		
		_flag = flag;
	}
	
	public long getAllyPenaltyExpiryTime()
	{
		return _allyPenaltyExpiryTime;
	}
	
	public int getAllyPenaltyType()
	{
		return _allyPenaltyType;
	}
	
	public void setAllyPenaltyExpiryTime(long expiryTime, int penaltyType)
	{
		_allyPenaltyExpiryTime = expiryTime;
		_allyPenaltyType = penaltyType;
	}
	
	public long getCharPenaltyExpiryTime()
	{
		return _charPenaltyExpiryTime;
	}
	
	public void setCharPenaltyExpiryTime(long time)
	{
		_charPenaltyExpiryTime = time;
	}
	
	public long getDissolvingExpiryTime()
	{
		return _dissolvingExpiryTime;
	}
	
	public void setDissolvingExpiryTime(long time)
	{
		_dissolvingExpiryTime = time;
	}
	
	/**
	 * @param objectId 
	 * @return the name.
	 */
	public ClanMember getClanMember(int objectId)
	{
		return _members.get(objectId);
	}
	
	/**
	 * @param name : The name to set.
	 * @return 
	 */
	public ClanMember getClanMember(String name)
	{
		return _members.values().stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}
	
	/**
	 * @param objectId : The member objectId.
	 * @return true if the {@link ClanMember} objectId given as parameter is in the _members list.
	 */
	public boolean isMember(int objectId)
	{
		return _members.containsKey(objectId);
	}
	
	public void addClanMember(Player player)
	{
		final ClanMember member = new ClanMember(this, player);
		_members.put(member.getObjectId(), member);
		member.setPlayerInstance(player);
		player.setClan(this);
		player.setPledgeClass(ClanMember.calculatePledgeClass(player));
		
		// Update siege flag, if needed.
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			final Siege siege = castle.getSiege();
			if (!siege.isInProgress())
				continue;
			
			if (siege.checkSide(this, SiegeSide.ATTACKER))
				player.setSiegeState((byte) 1);
			else if (siege.checkSides(this, SiegeSide.DEFENDER, SiegeSide.OWNER))
				player.setSiegeState((byte) 2);
		}
		
		player.sendPacket(new PledgeShowMemberListUpdate(player));
		player.sendPacket(new UserInfo(player));
	}
	
	public void updateClanMember(Player player)
	{
		final ClanMember member = new ClanMember(player.getClan(), player);
		if (player.isClanLeader())
			setLeader(member);
		
		_members.put(member.getObjectId(), member);
	}
	
	/**
	 * @param objectId : the object Id of the member that will be removed.
	 * @param clanJoinExpiryTime : time penalty to join a clan.
	 */
	public void removeClanMember(int objectId, long clanJoinExpiryTime)
	{
		final ClanMember exMember = _members.remove(objectId);
		if (exMember == null)
			return;
		
		// Sub-unit leader withdraws, position becomes vacant and leader should appoint new via NPC.
		final int subPledgeId = getLeaderSubPledge(objectId);
		if (subPledgeId != 0)
		{
			final SubPledge pledge = getSubPledge(subPledgeId);
			if (pledge != null)
			{
				pledge.setLeaderId(0);
				
				updateSubPledgeInDB(pledge);
			}
		}
		
		if (exMember.getApprentice() != 0)
		{
			final ClanMember apprentice = getClanMember(exMember.getApprentice());
			if (apprentice != null)
			{
				if (apprentice.getPlayerInstance() != null)
					apprentice.getPlayerInstance().setSponsor(0);
				else
					apprentice.setApprenticeAndSponsor(0, 0);
				
				apprentice.saveApprenticeAndSponsor(0, 0);
			}
		}
		
		if (exMember.getSponsor() != 0)
		{
			final ClanMember sponsor = getClanMember(exMember.getSponsor());
			if (sponsor != null)
			{
				if (sponsor.getPlayerInstance() != null)
					sponsor.getPlayerInstance().setApprentice(0);
				else
					sponsor.setApprenticeAndSponsor(0, 0);
				
				sponsor.saveApprenticeAndSponsor(0, 0);
			}
		}
		exMember.saveApprenticeAndSponsor(0, 0);
		
		// Unequip castle related items.
		if (hasCastle())
			CastleManager.getInstance().getCastleById(_castleId).checkItemsForMember(exMember);
		
		if (exMember.isOnline())
		{
			Player player = exMember.getPlayerInstance();
			
			// Clean title only for non nobles.
			if (!player.isNoble())
				player.setTitle("");
			
			// Setup active warehouse to null.
			if (player.getActiveWarehouse() != null)
				player.setActiveWarehouse(null);
			
			player.setApprentice(0);
			player.setSponsor(0);
			player.setSiegeState((byte) 0);
			
			if (player.isClanLeader())
			{
				player.removeSiegeSkills();
				player.setClanCreateExpiryTime(System.currentTimeMillis() + Config.ALT_CLAN_CREATE_DAYS * 86400000L);
			}
			
			// Remove clan skills.
			for (L2Skill skill : _skills.values())
				player.removeSkill(skill, false);
			
			player.sendSkillList();
			player.setClan(null);
			
			// players leaving from clan academy have no penalty
			if (exMember.getPledgeType() != -1)
				player.setClanJoinExpiryTime(clanJoinExpiryTime);
			
			player.setPledgeClass(ClanMember.calculatePledgeClass(player));
			player.broadcastUserInfo();
			
			// disable clan tab
			player.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement ps = con.prepareStatement(REMOVE_MEMBER);
				ps.setString(1, "");
				ps.setLong(2, clanJoinExpiryTime);
				ps.setLong(3, _leader.getObjectId() == objectId ? System.currentTimeMillis() + Config.ALT_CLAN_CREATE_DAYS * 86400000L : 0);
				ps.setInt(4, exMember.getObjectId());
				ps.executeUpdate();
				ps.close();
				
				ps = con.prepareStatement(REMOVE_APPRENTICE);
				ps.setInt(1, exMember.getObjectId());
				ps.executeUpdate();
				ps.close();
				
				ps = con.prepareStatement(REMOVE_SPONSOR);
				ps.setInt(1, exMember.getObjectId());
				ps.executeUpdate();
				ps.close();
			}
			catch (Exception e)
			{
				LOGGER.error("Error while removing clan member.", e);
			}
		}
	}
	
	public ClanMember[] getMembers()
	{
		return _members.values().toArray(new ClanMember[_members.size()]);
	}
	
	public int getMembersCount()
	{
		return _members.size();
	}
	
	public int getSubPledgeMembersCount(int pledgeType)
	{
		return (int) _members.values().stream().filter(m -> m.getPledgeType() == pledgeType).count();
	}
	
	public String getSubPledgeLeaderName(int pledgeType)
	{
		if (pledgeType == 0)
			return _leader.getName();
		
		SubPledge subPledge = _subPledges.get(pledgeType);
		int leaderId = subPledge.getLeaderId();
		
		if (subPledge.getId() == Clan.SUBUNIT_ACADEMY || leaderId == 0)
			return "";
		
		if (!_members.containsKey(leaderId))
		{
			LOGGER.warn("SubPledge leader {} is missing from clan: {}.", leaderId, toString());
			return "";
		}
		
		return _members.get(leaderId).getName();
	}
	
	/**
	 * @param pledgeType the Id of the pledge type.
	 * @return the maximum number of members allowed for a given {@code pledgeType}.
	 */
	public int getMaxNrOfMembers(int pledgeType)
	{
		if (Config.ALLOW_CLAN_FULL)
			return Config.ALT_MAX_NUM_OF_MEMBERS_IN_CLAN;
		
		switch (pledgeType)
		{
			case 0:
				switch (_level)
				{
					case 0:
						return 10;
						
					case 1:
						return 15;
						
					case 2:
						return 20;
						
					case 3:
						return 30;
						
					default:
						return 40;
				}
				
			case -1:
			case 100:
			case 200:
				return 20;
				
			case 1001:
			case 1002:
			case 2001:
			case 2002:
				return 10;
		}
		
		return 0;
	}
	
	public Player[] getOnlineMembers()
	{
		List<Player> list = new ArrayList<>();
		for (ClanMember temp : _members.values())
		{
			if (temp != null && temp.isOnline())
				list.add(temp.getPlayerInstance());
		}
		return list.toArray(new Player[list.size()]);
	}
	
	/**
	 * @return the online clan member count.
	 */
	public int getOnlineMembersCount()
	{
		return (int) _members.values().stream().filter(m -> m.isOnline()).count();
	}
	
	public void updateClanInDB()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_CLAN))
		{
			ps.setInt(1, _leader.getObjectId());
			ps.setInt(2, _allyId);
			ps.setString(3, _allyName);
			ps.setInt(4, _reputationScore);
			ps.setLong(5, _allyPenaltyExpiryTime);
			ps.setInt(6, _allyPenaltyType);
			ps.setLong(7, _charPenaltyExpiryTime);
			ps.setLong(8, _dissolvingExpiryTime);
			ps.setString(9, _name);
			ps.setInt(10, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating clan.", e);
		}
	}
	
	public void store()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(STORE_CLAN))
		{
			ps.setInt(1, _clanId);
			ps.setString(2, _name);
			ps.setInt(3, _level);
			ps.setInt(4, _castleId);
			ps.setInt(5, _allyId);
			ps.setString(6, _allyName);
			ps.setInt(7, _leader.getObjectId());
			ps.setInt(8, _crestId);
			ps.setInt(9, _crestLargeId);
			ps.setInt(10, _allyCrestId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while storing clan.", e);
		}
	}
	
	private void storeNotice(String notice, boolean enabled)
	{
		if (notice == null)
			notice = "";
		
		if (notice.length() > MAX_NOTICE_LENGTH)
			notice = notice.substring(0, MAX_NOTICE_LENGTH - 1);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_NOTICE))
		{
			ps.setString(1, (enabled) ? "true" : "false");
			ps.setString(2, notice);
			ps.setInt(3, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while storing notice.", e);
		}
		
		_notice = notice;
		_noticeEnabled = enabled;
	}
	
	public void setNoticeEnabledAndStore(boolean enabled)
	{
		storeNotice(_notice, enabled);
	}
	
	public void setNoticeAndStore(String notice)
	{
		storeNotice(notice, _noticeEnabled);
	}
	
	public boolean isNoticeEnabled()
	{
		return _noticeEnabled;
	}
	
	public void setNoticeEnabled(boolean enabled)
	{
		_noticeEnabled = enabled;
	}
	
	public String getNotice()
	{
		return (_notice == null) ? "" : _notice;
	}
	
	public void setNotice(String notice)
	{
		_notice = notice;
	}
	
	public String getIntroduction()
	{
		return (_introduction == null) ? "" : _introduction;
	}
	
	public void setIntroduction(String intro, boolean saveOnDb)
	{
		if (saveOnDb)
		{
			if (intro == null)
				intro = "";
			
			if (intro.length() > MAX_INTRODUCTION_LENGTH)
				intro = intro.substring(0, MAX_INTRODUCTION_LENGTH - 1);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_INTRODUCTION))
			{
				ps.setString(1, intro);
				ps.setInt(2, _clanId);
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.error("Error while storing introduction.", e);
			}
		}
		
		_introduction = intro;
	}
	
	/**
	 * @return an array with all clan skills that clan knows.
	 */
	public final L2Skill[] getClanSkills()
	{
		return _skills.values().toArray(new L2Skill[_skills.values().size()]);
	}
	
	/**
	 * Add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
	 * @param newSkill The skill to add
	 */
	public void addNewSkill(L2Skill newSkill)
	{
		if (newSkill == null)
			return;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_SKILL))
		{
			ps.setInt(1, _clanId);
			ps.setInt(2, newSkill.getId());
			ps.setInt(3, newSkill.getLevel());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while storing a clan skill.", e);
			return;
		}
		
		// Replace or add the skill.
		_skills.put(newSkill.getId(), newSkill);
		
		final PledgeSkillListAdd pledgeListAdd = new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel());
		final PledgeSkillList pledgeList = new PledgeSkillList(this);
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_SKILL_S1_ADDED).addSkillName(newSkill.getId());
		
		for (Player temp : getOnlineMembers())
		{
			if (newSkill.getMinPledgeClass() <= temp.getPledgeClass())
			{
				temp.addSkill(newSkill, false);
				temp.sendPacket(pledgeListAdd);
				temp.sendSkillList();
			}
			temp.sendPacket(pledgeList);
			temp.sendPacket(sm);
		}
	}
	
	public void addSkillEffects(Player player)
	{
		if (player == null || _reputationScore <= 0 || player.isInOlympiadMode())
			return;
		
		for (L2Skill skill : _skills.values())
		{
			if (skill.getMinPledgeClass() <= player.getPledgeClass())
				player.addSkill(skill, false);
		}
	}
	
	public void broadcastToOnlineAllyMembers(L2GameServerPacket packet)
	{
		for (Clan clan : ClanTable.getInstance().getClanAllies(_allyId))
			clan.broadcastToOnlineMembers(packet);
	}
	
	public void broadcastToOnlineMembers(L2GameServerPacket... packets)
	{
		for (ClanMember member : _members.values())
		{
			if (member != null && member.isOnline())
			{
				for (L2GameServerPacket packet : packets)
					member.getPlayerInstance().sendPacket(packet);
			}
		}
	}
	
	public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player)
	{
		for (ClanMember member : _members.values())
		{
			if (member != null && member.isOnline() && member.getPlayerInstance() != player)
				member.getPlayerInstance().sendPacket(packet);
		}
	}
	
	@Override
	public String toString()
	{
		return _name + "[" + _clanId + "]";
	}
	
	public ItemContainer getWarehouse()
	{
		return _warehouse;
	}
	
	public boolean isAtWarWith(int id)
	{
		return _atWarWith.contains(id);
	}
	
	public boolean isAtWarAttacker(int id)
	{
		return _atWarAttackers.contains(id);
	}
	
	public void setEnemyClan(int clanId)
	{
		_atWarWith.add(clanId);
	}
	
	public void setAttackerClan(int clanId)
	{
		_atWarAttackers.add(clanId);
	}
	
	public void deleteEnemyClan(int clanId)
	{
		_atWarWith.remove(Integer.valueOf(clanId));
	}
	
	public void deleteAttackerClan(int clanId)
	{
		_atWarAttackers.remove(Integer.valueOf(clanId));
	}
	
	public void addWarPenaltyTime(int clanId, long expiryTime)
	{
		_warPenaltyExpiryTime.put(clanId, expiryTime);
	}
	
	public boolean hasWarPenaltyWith(int clanId)
	{
		if (!_warPenaltyExpiryTime.containsKey(clanId))
			return false;
		
		return _warPenaltyExpiryTime.get(clanId) > System.currentTimeMillis();
	}
	
	public Map<Integer, Long> getWarPenalty()
	{
		return _warPenaltyExpiryTime;
	}
	
	public boolean isAtWar()
	{
		return !_atWarWith.isEmpty();
	}
	
	public List<Integer> getWarList()
	{
		return _atWarWith;
	}
	
	public List<Integer> getAttackerList()
	{
		return _atWarAttackers;
	}
	
	public void broadcastClanStatus()
	{
		for (Player member : getOnlineMembers())
		{
			member.sendPacket(PledgeShowMemberListDeleteAll.STATIC_PACKET);
			member.sendPacket(new PledgeShowMemberListAll(this, 0));
			
			for (SubPledge sp : getAllSubPledges())
				member.sendPacket(new PledgeShowMemberListAll(this, sp.getId()));
			
			member.sendPacket(new UserInfo(member));
		}
	}
	
	public boolean isSubPledgeLeader(int objectId)
	{
		for (SubPledge sp : getAllSubPledges())
		{
			if (sp.getLeaderId() == objectId)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieve subPledge by type
	 * @param pledgeType
	 * @return the subpledge object.
	 */
	public final SubPledge getSubPledge(int pledgeType)
	{
		return _subPledges.get(pledgeType);
	}
	
	/**
	 * Retrieve subPledge by name
	 * @param pledgeName
	 * @return the subpledge object.
	 */
	public final SubPledge getSubPledge(String pledgeName)
	{
		return _subPledges.values().stream().filter(s -> s.getName().equalsIgnoreCase(pledgeName)).findFirst().orElse(null);
	}
	
	/**
	 * Retrieve all subPledges.
	 * @return an array containing all subpledge objects.
	 */
	public final SubPledge[] getAllSubPledges()
	{
		return _subPledges.values().toArray(new SubPledge[_subPledges.values().size()]);
	}
	
	public SubPledge createSubPledge(Player player, int type, int leaderId, String subPledgeName)
	{
		final int pledgeType = getAvailablePledgeTypes(type);
		if (pledgeType == 0)
		{
			player.sendPacket((type == Clan.SUBUNIT_ACADEMY) ? SystemMessageId.CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY : SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT);
			return null;
		}
		
		if (_leader.getObjectId() == leaderId)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT);
			return null;
		}
		
		// Check regarding clan reputation points need : Royal Guard 5000 points, Order of Knights 10000 points.
		if (pledgeType != -1 && ((_reputationScore < 5000 && pledgeType < Clan.SUBUNIT_KNIGHT1) || (_reputationScore < 10000 && pledgeType > Clan.SUBUNIT_ROYAL2)))
		{
			player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return null;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_SUBPLEDGE))
		{
			ps.setInt(1, _clanId);
			ps.setInt(2, pledgeType);
			ps.setString(3, subPledgeName);
			ps.setInt(4, (pledgeType != -1) ? leaderId : 0);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error creating subpledge.", e);
			return null;
		}
		
		final SubPledge subPledge = new SubPledge(pledgeType, subPledgeName, leaderId);
		_subPledges.put(pledgeType, subPledge);
		
		if (pledgeType != -1)
		{
			if (pledgeType < Clan.SUBUNIT_KNIGHT1) // royal
				takeReputationScore(5000);
			else if (pledgeType > Clan.SUBUNIT_ROYAL2) // knight
				takeReputationScore(10000);
		}
		
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(this), new PledgeReceiveSubPledgeCreated(subPledge, this));
		
		return subPledge;
	}
	
	public int getAvailablePledgeTypes(int pledgeType)
	{
		if (_subPledges.get(pledgeType) != null)
		{
			switch (pledgeType)
			{
				case SUBUNIT_ACADEMY:
					return 0;
					
				case SUBUNIT_ROYAL1:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
					break;
				
				case SUBUNIT_ROYAL2:
					return 0;
					
				case SUBUNIT_KNIGHT1:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
					break;
				
				case SUBUNIT_KNIGHT2:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
					break;
				
				case SUBUNIT_KNIGHT3:
					pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
					break;
				
				case SUBUNIT_KNIGHT4:
					return 0;
			}
		}
		return pledgeType;
	}
	
	public void updateSubPledgeInDB(SubPledge pledge)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_SUBPLEDGE))
		{
			ps.setInt(1, pledge.getLeaderId());
			ps.setString(2, pledge.getName());
			ps.setInt(3, _clanId);
			ps.setInt(4, pledge.getId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error updating subpledge.", e);
		}
	}
	
	public final Map<Integer, Integer> getPriviledges()
	{
		return _priviledges;
	}
	
	public int getPriviledgesByRank(int rank)
	{
		return _priviledges.getOrDefault(rank, CP_NOTHING);
	}
	
	/**
	 * Update ranks priviledges.
	 * @param rank : The rank to edit.
	 * @param privs : The priviledges to be set.
	 */
	public void setPriviledgesForRank(int rank, int privs)
	{
		// Avoid to bother with invalid ranks.
		if (!_priviledges.containsKey(rank))
			return;
		
		// Replace the priviledges.
		_priviledges.put(rank, privs);
		
		// Refresh online members priviledges.
		for (Player member : getOnlineMembers())
		{
			if (member.getPowerGrade() == rank)
				member.setClanPrivileges(privs);
		}
		
		broadcastClanStatus();
		
		// Update database.
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_PRIVILEDGE))
		{
			ps.setInt(1, _clanId);
			ps.setInt(2, rank);
			ps.setInt(3, privs);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while storing rank.", e);
		}
	}
	
	public int getLeaderSubPledge(int leaderId)
	{
		int id = 0;
		for (SubPledge sp : _subPledges.values())
		{
			if (sp.getLeaderId() == 0)
				continue;
			
			if (sp.getLeaderId() == leaderId)
				id = sp.getId();
		}
		return id;
	}
	
	public int getReputationScore()
	{
		return _reputationScore;
	}
	
	  private void restoreClanArena() {
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("SELECT boss_win FROM clan_data WHERE clan_id=?");
		      statement.setInt(1, this._clanId);
		      ResultSet rset = statement.executeQuery();
		      while (rset.next()) {
		        int points = rset.getInt("boss_win");
		        this._clanBossScore = points;
		      } 
		      rset.close();
		      statement.close();
		    } catch (Exception e) {
		    	LOGGER.warn("Could not restore boss_win: " + e);
		    } 
		  }
		  
		  public synchronized void addclanBossScore(int value) {
		    setclanBossScore(this._clanBossScore + value);
		  }
		  
		  public synchronized void takeclanBossScore(int value) {
		    setclanBossScore(this._clanBossScore - value);
		  }
		  
		  public void setclanBossScore(int value) {
		    this._clanBossScore = Math.min(100000000, Math.max(-100000000, value));
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET boss_win=? WHERE clan_id=?");
		      statement.setInt(1, this._clanBossScore);
		      statement.setInt(2, this._clanId);
		      statement.execute();
		      statement.close();
		    } catch (Exception e) {
		    	LOGGER.warn(Level.WARNING, "Exception on setReputationScore(): " + e.getMessage(), e);
		    } 
		  }
		  
		  public int getclanBossScore() {
		    return this._clanBossScore;
		  }
		  
		  private void restoreClan5x5() {
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("SELECT 5x5_win FROM clan_data WHERE clan_id=?");
		      statement.setInt(1, this._clanId);
		      ResultSet rset = statement.executeQuery();
		      while (rset.next()) {
		        int points = rset.getInt("5x5_win");
		        this._clan5x5Score = points;
		      } 
		      rset.close();
		      statement.close();
		    } catch (Exception e) {
		      LOGGER.warn("Could not restore _clan5x5Score: " + e);
		    } 
		  }
		  
		  public synchronized void addclan5x5Score(int value) {
		    setclan5x5Score(this._clan5x5Score + value);
		  }
		  
		  public synchronized void takeclan5x5Score(int value) {
		    setclan5x5Score(this._clan5x5Score - value);
		  }
		  
		  public void setclan5x5Score(int value) {
		    this._clan5x5Score = Math.min(100000000, Math.max(-100000000, value));
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET 5x5_win=? WHERE clan_id=?");
		      statement.setInt(1, this._clan5x5Score);
		      statement.setInt(2, this._clanId);
		      statement.execute();
		      statement.close();
		    } catch (Exception e) {
		    	LOGGER.warn(Level.WARNING, "Exception on _clan5x5Score(): " + e.getMessage(), e);
		    } 
		  }
		  
		  public int getclan5x5Score() {
		    return this._clan5x5Score;
		  }
		  
		  private void restoreClan9x9() {
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("SELECT 9x9_win FROM clan_data WHERE clan_id=?");
		      statement.setInt(1, this._clanId);
		      ResultSet rset = statement.executeQuery();
		      while (rset.next()) {
		        int points = rset.getInt("9x9_win");
		        this._clan9x9Score = points;
		      } 
		      rset.close();
		      statement.close();
		    } catch (Exception e) {
		    	LOGGER.warn("Could not restore _clan9x9Score: " + e);
		    } 
		  }
		  
		  public synchronized void addclan9x9Score(int value) {
		    setclan9x9Score(this._clan9x9Score + value);
		  }
		  
		  public synchronized void takeclan9x9Score(int value) {
		    setclan9x9Score(this._clanBossScore - value);
		  }
		  
		  public void setclan9x9Score(int value) {
		    this._clan9x9Score = Math.min(100000000, Math.max(-100000000, value));
		    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
		      PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET 9x9_win=? WHERE clan_id=?");
		      statement.setInt(1, this._clan9x9Score);
		      statement.setInt(2, this._clanId);
		      statement.execute();
		      statement.close();
		    } catch (Exception e) {
		    	LOGGER.warn(Level.WARNING, "Exception on _clan9x9Score(): " + e.getMessage(), e);
		    } 
		  }
		  
		  public int getclan9x9Score() {
		    return this._clan9x9Score;
		  }
	
	/**
	 * Add the value to the total amount of the clan's reputation score.<br>
	 * <b>This method updates the database.</b>
	 * @param value : The value to add to current amount.
	 */
	public synchronized void addReputationScore(int value)
	{
		setReputationScore(_reputationScore + value);
	}
	
	/**
	 * Removes the value to the total amount of the clan's reputation score.<br>
	 * <b>This method updates the database.</b>
	 * @param value : The value to remove to current amount.
	 */
	public synchronized void takeReputationScore(int value)
	{
		setReputationScore(_reputationScore - value);
	}
	
	/**
	 * Launch behaviors following how big or low is the actual reputation.<br>
	 * <b>This method DOESN'T update the database.</b>
	 * @param value : The total amount to set to _reputationScore.
	 */
	private void setReputationScore(int value)
	{
		// Don't add any CRPs to clans with low level.
		if (_level < 5)
			return;
		
		// That check is used to see if it needs a refresh.
		final boolean needRefresh = (_reputationScore > 0 && value <= 0) || (value > 0 && _reputationScore <= 0);
		
		// Store the online members (used in 2 positions, can't merge)
		final Player[] members = getOnlineMembers();
		
		_reputationScore = MathUtil.limit(value, -100000000, 100000000);
		
		// Refresh clan windows of all clan members, and reward/remove skills.
		if (needRefresh)
		{
			final L2Skill[] skills = getClanSkills();
			
			if (_reputationScore <= 0)
			{
				for (Player member : members)
				{
					member.sendPacket(SystemMessageId.REPUTATION_POINTS_0_OR_LOWER_CLAN_SKILLS_DEACTIVATED);
					
					for (L2Skill sk : skills)
						member.removeSkill(sk, false);
					
					member.sendSkillList();
				}
			}
			else
			{
				for (Player member : members)
				{
					member.sendPacket(SystemMessageId.CLAN_SKILLS_WILL_BE_ACTIVATED_SINCE_REPUTATION_IS_0_OR_HIGHER);
					
					for (L2Skill sk : skills)
					{
						if (sk.getMinPledgeClass() <= member.getPledgeClass())
							member.addSkill(sk, false);
					}
					
					member.sendSkillList();
				}
			}
		}
		
		// Points reputation update for all.
		final PledgeShowInfoUpdate infoRefresh = new PledgeShowInfoUpdate(this);
		for (Player member : members)
			member.sendPacket(infoRefresh);
		
		// Save the amount on the database.
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_CRP))
		{
			ps.setInt(1, _reputationScore);
			ps.setInt(2, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating clan reputation points.", e);
		}
	}
	
	public int getRank()
	{
		return _rank;
	}
	
	public void setRank(int rank)
	{
		_rank = rank;
	}
	
	public int getAuctionBiddedAt()
	{
		return _auctionBiddedAt;
	}
	
	public void setAuctionBiddedAt(int id)
	{
		_auctionBiddedAt = id;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION))
		{
			ps.setInt(1, id);
			ps.setInt(2, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating clan auction.", e);
		}
	}
	
	/**
	 * Checks if activeChar and target meet various conditions to join a clan
	 * @param activeChar
	 * @param target
	 * @param pledgeType
	 * @return
	 */
	public boolean checkClanJoinCondition(Player activeChar, Player target, int pledgeType)
	{
		if (activeChar == null)
			return false;
		
		if ((activeChar.getClanPrivileges() & Clan.CP_CL_JOIN_CLAN) != Clan.CP_CL_JOIN_CLAN)
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return false;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (_charPenaltyExpiryTime > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER);
			return false;
		}
		
		if (target.getClanId() != 0)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_WORKING_WITH_ANOTHER_CLAN).addCharName(target));
			return false;
		}
		
		if ((target.getClanJoinExpiryTime() - 100 * 1000) > System.currentTimeMillis())
		{
			activeChar.sendPacket(new ExShowScreenMessage(target.getName() + " it's with clan penalty..", 8000));
			activeChar.sendMessage(target.getName() + " will be able to join a clan again in " + (target.getClanJoinExpiryTime() - System.currentTimeMillis()) / 60000 + " minute(s) ..");
			return false;
		}
		
		if ((target.getLevel() > 40 || target.getClassId().level() >= 2) && pledgeType == -1)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DOESNOT_MEET_REQUIREMENTS_TO_JOIN_ACADEMY).addCharName(target));
			activeChar.sendPacket(SystemMessageId.ACADEMY_REQUIREMENTS);
			return false;
		}
		
		if (getSubPledgeMembersCount(pledgeType) >= getMaxNrOfMembers(pledgeType))
		{
			if (pledgeType == 0)
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_IS_FULL).addCharName(target));
			else
				activeChar.sendPacket(SystemMessageId.SUBCLAN_IS_FULL);
			return false;
		}
		return true;
	}
	
	/**
	 * @param player : The player alliance launching the invitation.
	 * @param target : The target clan to recruit.
	 * @return true if target's clan meet conditions to join player's alliance.
	 */
	public static boolean checkAllyJoinCondition(Player player, Player target)
	{
		if (player == null)
			return false;
		
		if (player.getAllyId() == 0 || !player.isClanLeader() || player.getClanId() != player.getAllyId())
		{
			player.sendPacket(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return false;
		}
		
		final Clan leaderClan = player.getClan();
		if (leaderClan.getAllyPenaltyType() == PENALTY_TYPE_DISMISS_CLAN && leaderClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.CANT_INVITE_CLAN_WITHIN_1_DAY);
			return false;
		}
		
		if (target == null)
		{
			player.sendPacket(SystemMessageId.SELECT_USER_TO_INVITE);
			return false;
		}
		
		if (player.getObjectId() == target.getObjectId())
		{
			player.sendPacket(SystemMessageId.CANNOT_INVITE_YOURSELF);
			return false;
		}
		
		if (target.getClan() == null)
		{
			player.sendPacket(SystemMessageId.TARGET_MUST_BE_IN_CLAN);
			return false;
		}
		
		if (!target.isClanLeader())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER).addCharName(target));
			return false;
		}
		
		final Clan targetClan = target.getClan();
		if (target.getAllyId() != 0)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE).addString(targetClan.getName()).addString(targetClan.getAllyName()));
			return false;
		}
		
		if (targetClan.getAllyPenaltyExpiryTime() > System.currentTimeMillis())
		{
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_LEAVED)
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANT_ENTER_ALLIANCE_WITHIN_1_DAY).addString(targetClan.getName()));
				return false;
			}
			
			if (targetClan.getAllyPenaltyType() == PENALTY_TYPE_CLAN_DISMISSED)
			{
				player.sendPacket(SystemMessageId.CANT_ENTER_ALLIANCE_WITHIN_1_DAY);
				return false;
			}
		}
		
		// Check if clans are registered as opponents on the same siege.
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (castle.getSiege().isOnOppositeSide(leaderClan, targetClan))
			{
				player.sendPacket(SystemMessageId.OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE);
				return false;
			}
		}
		
		if (leaderClan.isAtWarWith(targetClan.getClanId()))
		{
			player.sendPacket(SystemMessageId.MAY_NOT_ALLY_CLAN_BATTLE);
			return false;
		}
		
		if (ClanTable.getInstance().getClanAllies(player.getAllyId()).size() >= Config.ALT_MAX_NUM_OF_CLANS_IN_ALLY)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_LIMIT);
			return false;
		}
		
		return true;
	}
	
	public void createAlly(Player player, String allyName)
	{
		if (player == null)
			return;
		
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
			return;
		}
		
		if (_allyId != 0)
		{
			player.sendPacket(SystemMessageId.ALREADY_JOINED_ALLIANCE);
			return;
		}
		
		if (_level < 5)
		{
			player.sendPacket(SystemMessageId.TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
			return;
		}
		
		if (_allyPenaltyType == Clan.PENALTY_TYPE_DISSOLVE_ALLY && _allyPenaltyExpiryTime > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION);
			return;
		}
		
		if (_dissolvingExpiryTime > System.currentTimeMillis())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING);
			return;
		}
		
		if (!StringUtil.isAlphaNumeric(allyName))
		{
			player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
			return;
		}
		
		if (allyName.length() > 16 || allyName.length() < 2)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_LENGTH);
			return;
		}
		
		if (ClanTable.getInstance().isAllyExists(allyName))
		{
			player.sendPacket(SystemMessageId.ALLIANCE_ALREADY_EXISTS);
			return;
		}
		
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (castle.getSiege().isInProgress() && castle.getSiege().checkSides(this))
			{
				player.sendPacket(SystemMessageId.NO_ALLY_CREATION_WHILE_SIEGE);
				return;
			}
		}
		
		_allyId = _clanId;
		_allyName = allyName;
		setAllyPenaltyExpiryTime(0, 0);
		updateClanInDB();
		
		player.sendPacket(new UserInfo(player));
	}
	
	public void dissolveAlly(Player player)
	{
		if (_allyId == 0)
		{
			player.sendPacket(SystemMessageId.NO_CURRENT_ALLIANCES);
			return;
		}
		
		if (!player.isClanLeader() || _clanId != _allyId)
		{
			player.sendPacket(SystemMessageId.FEATURE_ONLY_FOR_ALLIANCE_LEADER);
			return;
		}
		
		// For every clan in alliance, check if it is currently registered on siege.
		for (Clan clan : ClanTable.getInstance().getClanAllies(_allyId))
		{
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				if (castle.getSiege().isInProgress() && castle.getSiege().checkSides(clan))
				{
					player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE);
					return;
				}
			}
		}
		
		broadcastToOnlineAllyMembers(SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_DISOLVED));
		
		long currentTime = System.currentTimeMillis();
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getAllyId() == _allyId && clan.getClanId() != _clanId)
			{
				clan.setAllyId(0);
				clan.setAllyName(null);
				clan.setAllyPenaltyExpiryTime(0, 0);
				clan.updateClanInDB();
			}
		}
		
		_allyId = 0;
		_allyName = null;
		changeAllyCrest(0, false);
		setAllyPenaltyExpiryTime(currentTime + Config.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED * 86400000L, Clan.PENALTY_TYPE_DISSOLVE_ALLY);
		updateClanInDB();
		
		// The clan leader should take the XP penalty of a full death.
		player.deathPenalty(false, false, false);
	}
	
	public boolean levelUpClan(Player player)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return false;
		}
		
		if (System.currentTimeMillis() < _dissolvingExpiryTime)
		{
			player.sendPacket(SystemMessageId.CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS);
			return false;
		}
		
		boolean increaseClanLevel = false;
		
		switch (_level)
		{
			case 0: // upgrade to 1
				if (player.getSp() >= 30000 && player.reduceAdena("ClanLvl", 650000, player.getTarget(), true))
				{
					player.removeExpAndSp(0, 30000);
					increaseClanLevel = true;
				}
				break;
			
			case 1: // upgrade to 2
				if (player.getSp() >= 150000 && player.reduceAdena("ClanLvl", 2500000, player.getTarget(), true))
				{
					player.removeExpAndSp(0, 150000);
					increaseClanLevel = true;
				}
				break;
			
			case 2:// upgrade to 3
				if (player.getSp() >= 500000 && player.destroyItemByItemId("ClanLvl", 1419, 1, player.getTarget(), true))
				{
					player.removeExpAndSp(0, 500000);
					increaseClanLevel = true;
				}
				break;
			
			case 3: // upgrade to 4
				if (player.getSp() >= 1400000 && player.destroyItemByItemId("ClanLvl", 3874, 1, player.getTarget(), true))
				{
					player.removeExpAndSp(0, 1400000);
					increaseClanLevel = true;
				}
				break;
			
			case 4: // upgrade to 5
				if (player.getSp() >= 3500000 && player.destroyItemByItemId("ClanLvl", 3870, 1, player.getTarget(), true))
				{
					player.removeExpAndSp(0, 3500000);
					increaseClanLevel = true;
				}
				break;
			
			case 5:

				if (_reputationScore >= 10000 && getMembersCount() >= Config.MIN_MEMBERS_LV6)
				{
					takeReputationScore(10000);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(10000));
					increaseClanLevel = true;
				}
				break;
			
			case 6:
				
				if (_reputationScore >= 20000 && getMembersCount() >= Config.MIN_MEMBERS_LV7)
				{
					takeReputationScore(20000);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(20000));
					increaseClanLevel = true;
				}
				break;
			
			case 7:

				if (_reputationScore >= 40000 && getMembersCount() >= Config.MIN_MEMBERS_LV8)
				{
					takeReputationScore(40000);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DEDUCTED_FROM_CLAN_REP).addNumber(40000));
					increaseClanLevel = true;
				}
				break;
		}
		
		if (!increaseClanLevel)
		{
			player.sendPacket(SystemMessageId.FAILED_TO_INCREASE_CLAN_LEVEL);
			return false;
		}
		
		player.sendPacket(new ItemList(player, false));
		
		changeLevel(_level + 1);
		return true;
	}
	
	public void changeLevel(int level)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_LEVEL))
		{
			ps.setInt(1, level);
			ps.setInt(2, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating clan level.", e);
		}
		
		setLevel(level);
		
		if (_leader.isOnline())
		{
			Player leader = _leader.getPlayerInstance();
			if (3 < level)
				leader.addSiegeSkills();
			else if (4 > level)
				leader.removeSiegeSkills();
			
			if (4 < level)
				leader.sendPacket(SystemMessageId.CLAN_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
		}
		
		broadcastToOnlineMembers(new PledgeShowInfoUpdate(this), SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEVEL_INCREASED));
	}
	
	/**
	 * Change the clan crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeClanCrest(int crestId)
	{
		// Delete previous crest if existing.
		if (_crestId != 0)
			CrestCache.getInstance().removeCrest(CrestType.PLEDGE, _crestId);
		
		_crestId = crestId;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_CLAN_CREST))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating clan crest.", e);
		}
		
		for (Player member : getOnlineMembers())
			member.broadcastUserInfo();
	}
	
	/**
	 * Change the ally crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 * @param onlyThisClan Do it for the ally aswell.
	 */
	public void changeAllyCrest(int crestId, boolean onlyThisClan)
	{
		String query = "UPDATE clan_data SET ally_crest_id = ? WHERE clan_id = ?";
		int allyId = _clanId;
		if (!onlyThisClan)
		{
			// Delete previous crest if existing.
			if (_allyCrestId != 0)
				CrestCache.getInstance().removeCrest(CrestType.ALLY, _allyCrestId);
			
			query = "UPDATE clan_data SET ally_crest_id = ? WHERE ally_id = ?";
			allyId = _allyId;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, allyId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating ally crest.", e);
		}
		
		if (onlyThisClan)
		{
			_allyCrestId = crestId;
			for (Player member : getOnlineMembers())
				member.broadcastUserInfo();
		}
		else
		{
			for (Clan clan : ClanTable.getInstance().getClans())
			{
				if (clan.getAllyId() == _allyId)
				{
					clan.setAllyCrestId(crestId);
					for (Player member : clan.getOnlineMembers())
						member.broadcastUserInfo();
				}
			}
		}
	}
	
	/**
	 * Change the large crest. If crest id is 0, crest is removed. New crest id is saved to database.
	 * @param crestId if 0, crest is removed, else new crest id is set and saved to database
	 */
	public void changeLargeCrest(int crestId)
	{
		// Delete previous crest if existing.
		if (_crestLargeId != 0)
			CrestCache.getInstance().removeCrest(CrestType.PLEDGE_LARGE, _crestLargeId);
		
		_crestLargeId = crestId;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_LARGE_CREST))
		{
			ps.setInt(1, crestId);
			ps.setInt(2, _clanId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while updating large crest.", e);
		}
		
		for (Player member : getOnlineMembers())
			member.broadcastUserInfo();
	}
		
	/**
	 * Verify if the clan is registered to any siege.
	 * @return true if the clan is registered or owner of a castle
	 */
	public boolean isRegisteredOnSiege()
	{
		for (Castle castle : CastleManager.getInstance().getCastles())
			if (castle.getSiege().checkSides(this))
				return true;
		
		return false;
	}

}