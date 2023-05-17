package com.l2jmega.gameserver.model.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.L2Party.MessageType;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.type.L2OlympiadStadiumZone;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ExOlympiadMode;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.util.CloseUtil;

/**
 * @author godson, GodKratos, Pere, DS
 */
public abstract class AbstractOlympiadGame
{
	protected static final Logger _log = Logger.getLogger(AbstractOlympiadGame.class.getName());
	
	protected static final String POINTS = "olympiad_points";
	protected static final String COMP_DONE = "competitions_done";
	protected static final String COMP_WON = "competitions_won";
	protected static final String COMP_LOST = "competitions_lost";
	protected static final String COMP_DRAWN = "competitions_drawn";
	
	protected long _startTime = 0;
	protected boolean _aborted = false;
	protected final int _stadiumID;
	
	protected AbstractOlympiadGame(int id)
	{
		_stadiumID = id;
	}
	
	public final boolean isAborted()
	{
		return _aborted;
	}
	
	public final int getStadiumId()
	{
		return _stadiumID;
	}
	
	protected boolean makeCompetitionStart()
	{
		_startTime = System.currentTimeMillis();
		return !_aborted;
	}
	private static final String OLYMPIAD_POINTS_WIN = "SELECT olympiad_points, competitions_done, competitions_won FROM olympiad_nobles WHERE char_Id=?";
	
	private static final String OLYMPIAD_POINTS_LOST = "SELECT olympiad_points, competitions_done, competitions_lost FROM olympiad_nobles WHERE char_Id=?";

	static String OLYMPIAD_UPDATE_WIN = "UPDATE olympiad_nobles SET olympiad_points=?,competitions_done=?,competitions_won=?  WHERE char_Id=?";
	static String OLYMPIAD_UPDATE_LOST = "UPDATE olympiad_nobles SET olympiad_points=?,competitions_done=?,competitions_lost=?  WHERE char_Id=?";
	
	@SuppressWarnings("resource")
	protected final void addPointsToParticipant(Participant par, int points)
	{
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_GAINED_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
	
		int oldpoints = 0;
		int done = 0;
		int win = 0;
	
		Connection con = null;		
		try
		{

			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(OLYMPIAD_POINTS_WIN);
			statement.setInt(1, par.objectId);

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				oldpoints = rset.getInt("olympiad_points");
				done = rset.getInt("competitions_done");
				win = rset.getInt("competitions_won");
			}
			rset.close();
			statement.close();
			statement = null;
			rset = null;

		}
		catch (Exception e)
		{
			_log.warning("[FASE 1]: ERRO AO ADICIONAR PONTOS: " + e);
		}
		finally
		{
			CloseUtil.close(con);
		}

		try
		{
			// Database Connection
			//--------------------------------
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(OLYMPIAD_UPDATE_WIN);
		
			int newpoints = oldpoints + points;
			int newdone = done + 1;
			int newwin = win + 1;

			stmt.setInt(1, newpoints);
			stmt.setInt(2, newdone);
			stmt.setInt(3, newwin);
			stmt.setInt(4, par.objectId);
			stmt.execute();
			stmt.close();
			stmt = null;
		}
		catch(Exception e)
		{
			_log.warning("[FASE 2]: ERRO AO ADICIONAR PONTOS: " + e);
		}
		finally
		{
			CloseUtil.close(con);
		}
	}
	
	@SuppressWarnings("resource")
	protected final void removePointsFromParticipant(Participant par, int points)
	{
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_LOST_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
		int oldpoints = 0;
		int done = 0;
		int lost = 0;
	
		Connection con = null;		
		try
		{

			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(OLYMPIAD_POINTS_LOST);
			statement.setInt(1, par.objectId);

			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				oldpoints = rset.getInt("olympiad_points");
				done = rset.getInt("competitions_done");
				lost = rset.getInt("competitions_lost");
			}
			rset.close();
			statement.close();
			statement = null;
			rset = null;

		}
		catch (Exception e)
		{
			_log.warning("[FASE 1]: ERRO AO REMOVER PONTOS: " + e);
		}
		finally
		{
			CloseUtil.close(con);
		}

		try
		{
			// Database Connection
			//--------------------------------
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(OLYMPIAD_UPDATE_LOST);
		
			int newpoints = oldpoints - points;
			int newdone = done + 1;
			int newlost = lost + 1;

			stmt.setInt(1, newpoints);
			stmt.setInt(2, newdone);
			stmt.setInt(3, newlost);
			stmt.setInt(4, par.objectId);
			stmt.execute();
			stmt.close();
			stmt = null;
		}
		catch(Exception e)
		{
			_log.warning("[FASE 2]: ERRO AO REMOVER PONTOS: " + e);
		}
		finally
		{
			CloseUtil.close(con);
		}
	}
	
	/**
	 * Return null if player passed all checks or broadcast the reason to opponent.
	 * @param player to check.
	 * @return null or reason.
	 */
	protected static SystemMessage checkDefaulted(Player player)
	{
		if (player == null || !player.isOnline())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		if (player.getClient() == null || player.getClient().isDetached())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		// safety precautions
		if (player.isInObserverMode())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_PARTICIPATE_OLYMPIAD_WHILE_DEAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isSubClassActive())
		{
			player.sendPacket(SystemMessageId.SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_JOIN_OLYMPIAD_POSSESSING_S1).addItemName(player.getCursedWeaponEquippedId()));
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.getInventoryLimit() * 0.8 <= player.getInventory().getSize())
		{
			player.sendPacket(SystemMessageId.SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		return null;
	}
	
	protected static final boolean portPlayerToArena(Participant par, Location loc, int id)
	{
		final Player player = par.player;
		if (player == null || !player.isOnline())
			return false;
		
		try
		{
			player.getSavedLocation().set(player.getX(), player.getY(), player.getZ());
			
			player.setTarget(null);
			
			player.setOlympiadGameId(id);
			player.getAppearance().setInvisible();
			player.setOlympiadMode(true);
			player.setOlympiadStart(false);
			player.setOlympiadSide(par.side);
			player.teleToLocation(loc, 0);
			player.sendPacket(new ExOlympiadMode(par.side));
			
			// Remove Buffs
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			for (L2Effect effect : player.getAllEffects())
			{
				if (effect != null)
					player.stopSkillEffects(effect.getSkill().getId());
			}
			
			// Remove Clan Skills
			if (player.getClan() != null)
			{
				for (L2Skill skill : player.getClan().getClanSkills())
					player.removeSkill(skill, false);
			}
			
			// Remove Summon's Buffs
			final Summon summon = player.getPet();
			if (summon != null)
			{
				summon.stopAllEffectsExceptThoseThatLastThroughDeath();
				
				for (L2Effect effect : summon.getAllEffects())
				{
					if (effect != null)
						summon.stopSkillEffects(effect.getSkill().getId());
				}
				
				summon.abortAttack();
				summon.abortCast();
			}
			
			// stop any cubic that has been given by other player.
			player.stopCubicsByOthers();
			
			player.sendSkillList();
			
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	protected static final void removals(Player player)
	{
		try
		{
			if (player == null)
				return;
			
			// Remove Buffs
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			for (L2Effect effect : player.getAllEffects())
			{
				if (effect != null)
					player.stopSkillEffects(effect.getSkill().getId());
			}
			
			// Remove Clan Skills
			if (player.getClan() != null)
			{
				for (L2Skill skill : player.getClan().getClanSkills())
					player.removeSkill(skill, false);
			}
			
			// Abort casting if player casting
			player.abortAttack();
			player.abortCast();
			
			// Remove Hero Skills
			if (player.isHero())
			{
				for (L2Skill skill : SkillTable.getHeroSkills())
					player.removeSkill(skill, false);
			}
			
			// Heal Player fully
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			
			// Remove Summon's Buffs
			final Summon summon = player.getPet();
			if (summon != null)
			{
				summon.stopAllEffectsExceptThoseThatLastThroughDeath();
				summon.abortAttack();
				summon.abortCast();
			}
			
			// stop any cubic that has been given by other player.
			player.stopCubicsByOthers();
			
			// Remove player from his party
			
			final L2Party party = player.getParty();
			if (party != null)
				party.removePartyMember(player, MessageType.Expelled);
			
			player.checkItemRestriction();
			
			// Remove shot automation
			player.disableAutoShotsAll();
			
			// Discharge any active shots
			ItemInstance item = player.getActiveWeaponInstance();
			if (item != null)
				item.unChargeAllShots();
			
			player.sendSkillList();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	/**
	 * Buff the player. WW2 for fighter/mage + haste 1 if fighter.
	 * @param player : the happy benefactor.
	 */
	protected static final void buffPlayer(Player player)
	{
		SystemMessage sm;
		if (!player.isMageClass() || player.getClassId().getId() == 116)
		{
			for (int[] buff : Config.CUSTOM_BUFFS_F) // Custom buffs for fighters
			{
				L2Skill skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
				skill.getEffects(player, player);
				sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
				sm.addSkillName(buff[0]);
				player.sendPacket(sm);
			}
		}
		else
		{
			for (int[] buff : Config.CUSTOM_BUFFS_M) // Custom buffs for mystics
			{
				L2Skill skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
				skill.getEffects(player, player);
				sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
				sm.addSkillName(buff[0]);
				player.sendPacket(sm);
			}
		}
		
		// Remove Clan Skills
		if (player.getClan() != null)
		{
			for (L2Skill skill : player.getClan().getClanSkills())
				player.removeSkill(skill, false);
		}
		
	}
	
	/**
	 * Heal the player.
	 * @param player : the happy benefactor.
	 */
	protected static final void healPlayer(Player player)
	{
		player.setCurrentCp(player.getMaxCp());
		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
	}
	
	protected static final void enableSkills(Player player)
	{
		if (Config.OLY_SKILL_PROTECT)
		{
			for (L2Skill protection : player.getSkills().values())
			{
				if (Config.OLY_SKILL_LIST.contains(protection.getId()))
				{
					player.enableSkill(protection);
					player.setSkillProtectOlympiadMode(false);
					player.sendMessage("Olympiad: His skill can be used normally");
				}
			}
		}
	}
	
	protected static final void disableSkills(Player player)
	{
		if (Config.OLY_SKILL_PROTECT)
		{
			for (L2Skill protection : player.getSkills().values())
			{
				if (Config.OLY_SKILL_LIST.contains(protection.getId()))
				{
					player.disableSkill(protection, 0);
					player.setSkillProtectOlympiadMode(true);
				}
			}
		}
		
	}
	
	protected static final void cleanEffects(Player player)
	{
		try
		{
			// prevent players kill each other
			player.setOlympiadStart(false);
			player.setTarget(null);
			player.abortAttack();
			player.abortCast();
			player.getAI().setIntention(CtrlIntention.IDLE);
			
			if (player.isDead())
				player.setIsDead(false);
			
			final Summon summon = player.getPet();
			if (summon != null && !summon.isDead())
			{
				summon.setTarget(null);
				summon.abortAttack();
				summon.abortCast();
				summon.getAI().setIntention(CtrlIntention.IDLE);
			}
			
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp());
			player.setCurrentMp(player.getMaxMp());
			player.getStatus().startHpMpRegeneration();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected static final void playerStatusBack(Player player)
	{
		try
		{
			if (!player.isGM())
				player.getAppearance().setVisible();
			
			player.setOlympiadMode(false);
			player.setOlympiadStart(false);
			player.setOlympiadSide(-1);
			player.setOlympiadGameId(-1);
			player.sendPacket(new ExOlympiadMode(0));
			
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.clearCharges();
			
			final Summon summon = player.getPet();
			if (summon != null && !summon.isDead())
				summon.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			// Add Clan Skills
			if (player.getClan() != null)
			{
				player.getClan().addSkillEffects(player);
				
				// heal again after adding clan skills
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp());
				player.setCurrentMp(player.getMaxMp());
			}
			
			// Add Hero Skills
			if (player.isHero())
			{
				for (L2Skill skill : SkillTable.getHeroSkills())
					player.addSkill(skill, false);
			}
			
			if (Config.ALT_DISABLE_BOW_CLASSES)
				player.removeBow();
			
			player._reuseTimeStamps.clear();
			player.getDisabledSkills().clear();
			player.sendPacket(new SkillCoolTime(player));
			
			player.sendSkillList();
			player.broadcastUserInfo();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected static final void portPlayerBack(Player player)
	{
		if (player == null)
			return;
		
		Location loc = player.getSavedLocation();
		if (loc.equals(Location.DUMMY_LOC))
			return;
		
		player.teleToLocation(loc, 0);
		player.getSavedLocation().clean();
		player.setOlympiadProtection(false);
	}
	
	public static final void rewardParticipant(Player player, int[][] reward)
	{
		if (player == null || !player.isOnline() || reward == null)
			return;
		
		try
		{
			final InventoryUpdate iu = new InventoryUpdate();
			for (int[] it : reward)
			{
				if (it == null || it.length != 2)
					continue;
				
				final ItemInstance item = player.getInventory().addItem("Olympiad", it[0], it[1], player, null);
				if (item == null)
					continue;
				
				iu.addModifiedItem(item);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(it[0]).addNumber(it[1]));
			}
			player.sendPacket(iu);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	public abstract CompetitionType getType();
	
	public abstract String[] getPlayerNames();
	
	public abstract String[] getPlayerClass();
	
	public abstract boolean containsParticipant(int playerId);
	
	public abstract void sendOlympiadInfo(Creature player);
	
	public abstract void broadcastOlympiadInfo(L2OlympiadStadiumZone stadium);
	
	protected abstract void broadcastPacket(L2GameServerPacket packet);
	
	protected abstract boolean checkDefaulted();
	
	protected abstract void removals();
	
	protected abstract void buffPlayers();
	
	protected abstract void healPlayers();
	
	protected abstract void enableSkills();
	
	protected abstract void disableSkills();
	
	protected abstract boolean portPlayersToArena(List<Location> spawns);
	
	protected abstract void cleanEffects();
	
	protected abstract void portPlayersBack();
	
	protected abstract void playersStatusBack();
	
	protected abstract void clearPlayers();
	
	protected abstract void handleDisconnect(Player player);
	
	protected abstract void resetDamage();
	
	protected abstract void addDamage(Player player, int damage);
	
	protected abstract boolean checkBattleStatus();
	
	protected abstract boolean haveWinner();
	
	protected abstract void validateWinner(L2OlympiadStadiumZone stadium);
	
	protected abstract int getDivider();
	
	protected abstract int[][] getReward();
}