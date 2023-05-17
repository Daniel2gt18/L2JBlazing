package com.l2jmega.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.CastleManorManager;
import com.l2jmega.gameserver.instancemanager.SevenSigns;
import com.l2jmega.gameserver.instancemanager.SevenSigns.SealType;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Door;
import com.l2jmega.gameserver.model.actor.instance.HolyThing;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.item.MercenaryTicket;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.location.TowerSpawnLocation;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.model.pledge.ClanMember;
import com.l2jmega.gameserver.model.zone.type.L2CastleTeleportZone;
import com.l2jmega.gameserver.model.zone.type.L2CastleZone;
import com.l2jmega.gameserver.model.zone.type.L2SiegeZone;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public class Castle
{
	protected static final Logger _log = Logger.getLogger(Castle.class.getName());
	
	private static final String UPDATE_TREASURY = "UPDATE castle SET treasury = ? WHERE id = ?";
	private static final String UPDATE_ITEMS_LOC = "UPDATE items SET loc='INVENTORY' WHERE item_id IN (?, 6841) AND owner_id=? AND loc='PAPERDOLL'";
	
	private final int _castleId;
	private final String _name;
	
	private int _circletId;
	private int _ownerId;
	private Clan _formerOwner;
	
	private final List<Door> _doors = new ArrayList<>();
	private final List<MercenaryTicket> _tickets = new ArrayList<>(60);
	private final List<Integer> _artifacts = new ArrayList<>(1);
	private final List<Integer> _relatedNpcIds = new ArrayList<>();
	
	private final Set<ItemInstance> _droppedTickets = new ConcurrentSkipListSet<>();
	private final List<Npc> _siegeGuards = new ArrayList<>();
	
	private final List<TowerSpawnLocation> _controlTowers = new ArrayList<>();
	private final List<TowerSpawnLocation> _flameTowers = new ArrayList<>();
	
	private Siege _siege;
	private Calendar _siegeDate;
	private boolean _isTimeRegistrationOver = true;
	
	private int _taxPercent;
	private double _taxRate;
	private long _treasury;
	
	private L2SiegeZone _siegeZone;
	private L2CastleZone _castleZone;
	private L2CastleTeleportZone _teleZone;
	
	private int _leftCertificates;
	
	public Castle(int id, String name)
	{
		_castleId = id;
		_name = name;
		
		// Feed _siegeZone.
		for (L2SiegeZone zone : ZoneManager.getInstance().getAllZones(L2SiegeZone.class))
		{
			if (zone.getSiegeObjectId() == _castleId)
			{
				_siegeZone = zone;
				break;
			}
		}
		
		// Feed _castleZone.
		for (L2CastleZone zone : ZoneManager.getInstance().getAllZones(L2CastleZone.class))
		{
			if (zone.getCastleId() == _castleId)
			{
				_castleZone = zone;
				break;
			}
		}
		
		// Feed _teleZone.
		for (L2CastleTeleportZone zone : ZoneManager.getInstance().getAllZones(L2CastleTeleportZone.class))
		{
			if (zone.getCastleId() == _castleId)
			{
				_teleZone = zone;
				break;
			}
		}
	}
	
	public synchronized void engrave(Clan clan, WorldObject target)
	{
		if (!isGoodArtifact(target))
			return;
		
		setOwner(clan);
		
		// "Clan X engraved the ruler" message.
		getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_ENGRAVED_RULER).addString(clan.getName()), true);
	}
	
	/**
	 * Add amount to castle's treasury (warehouse).
	 * @param amount The amount to add.
	 */
	public void addToTreasury(int amount)
	{
		if (_ownerId <= 0)
			return;
		
		if (_name.equalsIgnoreCase("Schuttgart") || _name.equalsIgnoreCase("Goddard"))
		{
			Castle rune = CastleManager.getInstance().getCastleByName("rune");
			if (rune != null)
			{
				int runeTax = (int) (amount * rune._taxRate);
				if (rune._ownerId > 0)
					rune.addToTreasury(runeTax);
				amount -= runeTax;
			}
		}
		
		if (!_name.equalsIgnoreCase("aden") && !_name.equalsIgnoreCase("Rune") && !_name.equalsIgnoreCase("Schuttgart") && !_name.equalsIgnoreCase("Goddard")) // If current castle instance is not Aden, Rune, Goddard or Schuttgart.
		{
			Castle aden = CastleManager.getInstance().getCastleByName("aden");
			if (aden != null)
			{
				int adenTax = (int) (amount * aden._taxRate); // Find out what Aden gets from the current castle instance's income
				if (aden._ownerId > 0)
					aden.addToTreasury(adenTax); // Only bother to really add the tax to the treasury if not npc owned
					
				amount -= adenTax; // Subtract Aden's income from current castle instance's income
			}
		}
		
		addToTreasuryNoTax(amount);
	}
	
	/**
	 * Add amount to castle instance's treasury (warehouse), no tax paying.
	 * @param amount The amount of adenas to add to treasury.
	 * @return true if successful.
	 */
	public boolean addToTreasuryNoTax(long amount)
	{
		if (_ownerId <= 0)
			return false;
		
		if (amount < 0)
		{
			amount *= -1;
			if (_treasury < amount)
				return false;
			_treasury -= amount;
		}
		else
		{
			if (_treasury + amount > Integer.MAX_VALUE)
				_treasury = Integer.MAX_VALUE;
			else
				_treasury += amount;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_TREASURY))
		{
			ps.setLong(1, _treasury);
			ps.setInt(2, _castleId);
			ps.execute();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Failed to update treasury.", e);
		}
		return true;
	}
	
	/**
	 * Move non clan members off castle area and to nearest town.
	 */
	public void banishForeigners()
	{
		getCastleZone().banishForeigners(_ownerId);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return true if object is inside the zone
	 */
	public boolean checkIfInZone(int x, int y, int z)
	{
		return getSiegeZone().isInsideZone(x, y, z);
	}
	
	public L2SiegeZone getSiegeZone()
	{
		return _siegeZone;
	}
	
	public L2CastleZone getCastleZone()
	{
		return _castleZone;
	}
	
	public L2CastleTeleportZone getTeleZone()
	{
		return _teleZone;
	}
	
	public void oustAllPlayers()
	{
		getTeleZone().oustAllPlayers();
	}
	
	public int getLeftCertificates()
	{
		return _leftCertificates;
	}
	
	/**
	 * Set (and optionally save on database) left certificates count.
	 * @param leftCertificates : the count to save.
	 * @param save : true means we store it on database. Basically setted to false on server startup.
	 */
	public void setLeftCertificates(int leftCertificates, boolean save)
	{
		_leftCertificates = leftCertificates;
		
		if (save)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE castle SET certificates=? WHERE id=?"))
			{
				ps.setInt(1, leftCertificates);
				ps.setInt(2, _castleId);
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "setLeftCertificates: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Get the object distance to this castle zone.
	 * @param obj The WorldObject to make tests on.
	 * @return the distance between the WorldObject and the zone.
	 */
	public double getDistance(WorldObject obj)
	{
		return getSiegeZone().getDistanceToZone(obj);
	}
	
	public void closeDoor(Player activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, false);
	}
	
	public void openDoor(Player activeChar, int doorId)
	{
		openCloseDoor(activeChar, doorId, true);
	}
	
	public void openCloseDoor(Player activeChar, int doorId, boolean open)
	{
		if (activeChar.getClanId() != _ownerId)
			return;
		
		Door door = getDoor(doorId);
		if (door != null)
		{
			if (open)
				door.openMe();
			else
				door.closeMe();
		}
	}
	
	/**
	 * This method setup the castle owner.
	 * @param clan The clan who will own the castle.
	 */
	public void setOwner(Clan clan)
	{
		// Act only if castle owner is different of NPC, or if old owner is different of new owner.
		if (_ownerId > 0 && (clan == null || clan.getClanId() != _ownerId))
		{
			// Try to find clan instance of the old owner.
			Clan oldOwner = ClanTable.getInstance().getClan(_ownerId);
			if (oldOwner != null)
			{
				// Set the former owner.
				if (_formerOwner == null)
					_formerOwner = oldOwner;
				
				// Dismount the old leader if he was riding a wyvern.
				Player oldLeader = oldOwner.getLeader().getPlayerInstance();
				if (oldLeader != null)
				{
					if (oldLeader.getMountType() == 2)
						oldLeader.dismount();
				}
				
				// Unset castle flag for old owner clan.
				oldOwner.setCastle(0);
			}
		}
		
		// Update database.
		updateOwnerInDB(clan);
		
		// If siege is in progress, mid victory phase of siege.
		if (getSiege().isInProgress())
		{
			getSiege().midVictory();
			
			// "There is a new castle Lord" message when the castle change of hands. Message sent for both sides.
			getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.NEW_CASTLE_LORD), true);
		}
	}
	
	/**
	 * Remove the castle owner. This method is only used by admin command.
	 * @param clan The clan which is victim of the command.
	 **/
	public void removeOwner(Clan clan)
	{
		if (clan != null)
		{
			_formerOwner = clan;
			
			clan.setCastle(0);
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
			
			// Remove clan from siege registered clans (as owners are automatically added).
			getSiege().getRegisteredClans().remove(clan);
		}
		
		updateOwnerInDB(null);
		
		if (getSiege().isInProgress())
			getSiege().midVictory();
		else if (_formerOwner != null)
			checkItemsForClan(_formerOwner);
	}
	
	/**
	 * This method updates the castle tax rate.
	 * @param activeChar Sends informative messages to that character (success or fail).
	 * @param taxPercent The new tax rate to apply.
	 */
	public void setTaxPercent(Player activeChar, int taxPercent)
	{
		int maxTax;
		switch (SevenSigns.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				maxTax = 25;
				break;
			
			case DUSK:
				maxTax = 5;
				break;
			
			default:
				maxTax = 15;
		}
		
		if (taxPercent < 0 || taxPercent > maxTax)
		{
			activeChar.sendMessage("Tax value must be between 0 and " + maxTax + ".");
			return;
		}
		
		setTaxPercent(taxPercent, true);
		activeChar.sendMessage(_name + " castle tax changed to " + taxPercent + "%.");
	}
	
	public void setTaxPercent(int taxPercent, boolean save)
	{
		_taxPercent = taxPercent;
		_taxRate = _taxPercent / 100.0;
		
		if (save)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE castle SET taxPercent = ? WHERE id = ?");
				statement.setInt(1, taxPercent);
				statement.setInt(2, _castleId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	/**
	 * Respawn doors associated to that castle.
	 * @param isDoorWeak if true, spawn doors with 50% max HPs.
	 */
	public void spawnDoors(boolean isDoorWeak)
	{
		for (Door door : _doors)
		{
			if (door.isDead())
				door.doRevive();
			
			door.closeMe();
			door.setCurrentHp((isDoorWeak) ? door.getMaxHp() / 2 : door.getMaxHp());
			door.broadcastStatusUpdate();
		}
	}
	
	/**
	 * Close doors associated to that castle.
	 */
	public void closeDoors()
	{
		for (Door door : _doors)
			door.closeMe();
	}
	
	/**
	 * Upgrade door.
	 * @param doorId The doorId to affect.
	 * @param hp The hp ratio.
	 * @param db If set to true, save changes on database.
	 */
	public void upgradeDoor(int doorId, int hp, boolean db)
	{
		Door door = getDoor(doorId);
		if (door == null)
			return;
		
		door.getStat().setUpgradeHpRatio(hp);
		door.setCurrentHp(door.getMaxHp());
		
		if (db)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("REPLACE INTO castle_doorupgrade (doorId, hp, castleId) VALUES (?,?,?)");
				statement.setInt(1, doorId);
				statement.setInt(2, hp);
				statement.setInt(3, _castleId);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Exception: saveDoorUpgrade(int doorId, int hp): " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * This method loads castle door upgrade data from database.
	 */
	public void loadDoorUpgrade()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM castle_doorupgrade WHERE castleId=?");
			statement.setInt(1, _castleId);
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
				upgradeDoor(rs.getInt("doorId"), rs.getInt("hp"), false);
			
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: loadDoorUpgrade(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * This method is only used on siege midVictory.
	 */
	public void removeDoorUpgrade()
	{
		for (Door door : _doors)
			door.getStat().setUpgradeHpRatio(1);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM castle_doorupgrade WHERE castleId=?");
			statement.setInt(1, _castleId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: removeDoorUpgrade(): " + e.getMessage(), e);
		}
	}
	
	private void updateOwnerInDB(Clan clan)
	{
		if (clan != null)
			_ownerId = clan.getClanId(); // Update owner id property
		else
		{
			_ownerId = 0; // Remove owner
			CastleManorManager.getInstance().resetManorData(_castleId);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=?");
			statement.setInt(1, _castleId);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=?");
			statement.setInt(1, _castleId);
			statement.setInt(2, _ownerId);
			statement.execute();
			statement.close();
			
			// Announce to clan memebers
			if (clan != null)
			{
				// Set castle for new owner.
				clan.setCastle(_castleId);
				
				// Announce to clan members.
				clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan), new PlaySound(1, "Siege_Victory"));
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: updateOwnerInDB(L2Clan clan): " + e.getMessage(), e);
		}
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	public Door getDoor(int doorId)
	{
		for (Door door : _doors)
		{
			if (door.getDoorId() == doorId)
				return door;
		}
		return null;
	}
	
	public List<Door> getDoors()
	{
		return _doors;
	}
	
	public List<MercenaryTicket> getTickets()
	{
		return _tickets;
	}
	
	public MercenaryTicket getTicket(int itemId)
	{
		return _tickets.stream().filter(t -> t.getItemId() == itemId).findFirst().orElse(null);
	}
	
	public Set<ItemInstance> getDroppedTickets()
	{
		return _droppedTickets;
	}
	
	public void addDroppedTicket(ItemInstance item)
	{
		_droppedTickets.add(item);
	}
	
	public void removeDroppedTicket(ItemInstance item)
	{
		_droppedTickets.remove(item);
	}
	
	public int getDroppedTicketsCount(int itemId)
	{
		return (int) _droppedTickets.stream().filter(t -> t.getItemId() == itemId).count();
	}
	
	public boolean isTooCloseFromDroppedTicket(int x, int y, int z)
	{
		for (ItemInstance item : _droppedTickets)
		{
			double dx = x - item.getX();
			double dy = y - item.getY();
			double dz = z - item.getZ();
			
			if ((dx * dx + dy * dy + dz * dz) < 25 * 25)
				return true;
		}
		return false;
	}
	
	/**
	 * That method is used to spawn NPCs, being neutral guards or player-based mercenaries.
	 * <ul>
	 * <li>If castle got an owner, it spawns mercenaries following tickets. Otherwise it uses SpawnManager territory.</li>
	 * <li>It feeds the nearest Control Tower with the spawn. If tower is broken, associated spawns are removed.</li>
	 * </ul>
	 */
	public void spawnSiegeGuardsOrMercenaries()
	{
		if (_ownerId > 0)
		{
			for (ItemInstance item : _droppedTickets)
			{
				// Retrieve MercenaryTicket information.
				final MercenaryTicket ticket = getTicket(item.getItemId());
				if (ticket == null)
					continue;
				
				// Generate templates, feed them with ticket information.
				final NpcTemplate template = NpcTable.getInstance().getTemplate(ticket.getNpcId());
				if (template == null)
					continue;
				
				try
				{
					final L2Spawn spawn = new L2Spawn(template);
					spawn.setLoc(item.getPosition());
					spawn.setRespawnState(false);
					
					_siegeGuards.add(spawn.doSpawn(false));
				}
				catch (Exception e)
				{
					_log.warning("Could not spawn Npc " + ticket.getNpcId());
				}
				
				// Delete the ticket item.
				item.decayMe();
			}
			
			_droppedTickets.clear();
		}
		else
		{
			// TODO Territory based spawn
		}
	}
	
	/**
	 * Despawn neutral guards or player-based mercenaries.
	 */
	public void despawnSiegeGuardsOrMercenaries()
	{
		if (_ownerId > 0)
		{
			for (Npc npc : _siegeGuards)
				npc.doDie(npc);
			
			_siegeGuards.clear();
		}
		else
		{
			// TODO territory based despawn
		}
	}
	
	public List<TowerSpawnLocation> getControlTowers()
	{
		return _controlTowers;
	}
	
	public List<TowerSpawnLocation> getFlameTowers()
	{
		return _flameTowers;
	}
	
	public void loadTrapUpgrade()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM castle_trapupgrade WHERE castleId=?");
			statement.setInt(1, _castleId);
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
				_flameTowers.get(rs.getInt("towerIndex")).setUpgradeLevel(rs.getInt("level"));
			
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: loadTrapUpgrade(): " + e);
		}
	}
	
	public List<Integer> getRelatedNpcIds()
	{
		return _relatedNpcIds;
	}
	
	public void setRelatedNpcIds(String idsToSplit)
	{
		for (String splittedId : idsToSplit.split(";"))
			_relatedNpcIds.add(Integer.parseInt(splittedId));
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getCircletId()
	{
		return _circletId;
	}
	
	public void setCircletId(int circletId)
	{
		_circletId = circletId;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	public Siege getSiege()
	{
		return _siege;
	}
	
	public void setSiege(Siege siege)
	{
		_siege = siege;
	}
	
	public Calendar getSiegeDate()
	{
		return _siegeDate;
	}
	
	public void setSiegeDate(Calendar siegeDate)
	{
		_siegeDate = siegeDate;
	}
	
	public boolean isTimeRegistrationOver()
	{
		return _isTimeRegistrationOver;
	}
	
	public void setTimeRegistrationOver(boolean val)
	{
		_isTimeRegistrationOver = val;
	}
	
	public int getTaxPercent()
	{
		return _taxPercent;
	}
	
	public double getTaxRate()
	{
		return _taxRate;
	}
	
	public long getTreasury()
	{
		return _treasury;
	}
	
	public void setTreasury(long treasury)
	{
		_treasury = treasury;
	}
	
	/**
	 * Update clan reputation points over siege end, as following :
	 * <ul>
	 * <li>The former clan failed to defend the castle : 1000 points for new owner, -1000 for former clan.</li>
	 * <li>The former clan successfully defended the castle, ending in a draw : 500 points for former clan.</li>
	 * <li>No former clan, which means players successfully attacked over NPCs : 1000 points for new owner.</li>
	 * </ul>
	 */
	public void updateClansReputation()
	{
		final Clan owner = ClanTable.getInstance().getClan(getOwnerId());
		if (_formerOwner != null)
		{
			// Defenders fail
			if (_formerOwner != owner)
			{
				_formerOwner.takeReputationScore(1000);
				_formerOwner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAS_DEFEATED_IN_SIEGE_AND_LOST_S1_REPUTATION_POINTS).addNumber(1000));
				
				// Attackers succeed over defenders
				if (owner != null)
				{
					owner.addReputationScore(1000);
					owner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(1000));
				}
			}
			// Draw
			else
			{
				_formerOwner.addReputationScore(500);
				_formerOwner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(500));
			}
		}
		// Attackers win over NPCs
		else if (owner != null)
		{
			owner.addReputationScore(1000);
			owner.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(1000));
		}
	}
	
	public List<Integer> getArtifacts()
	{
		return _artifacts;
	}
	
	public void setArtifacts(String idsToSplit)
	{
		for (String idToSplit : idsToSplit.split(";"))
			_artifacts.add(Integer.parseInt(idToSplit));
	}
	
	public boolean isGoodArtifact(WorldObject object)
	{
		return object instanceof HolyThing && _artifacts.contains(((HolyThing) object).getNpcId());
	}
	
	/**
	 * @return the initial castle owner. Used to clean crowns && others castles related functions.
	 */
	public Clan getInitialCastleOwner()
	{
		return _formerOwner;
	}
	
	/**
	 * @param towerIndex : The index to check on.
	 * @return the trap upgrade level for a dedicated tower index.
	 */
	public int getTrapUpgradeLevel(int towerIndex)
	{
		final TowerSpawnLocation spawn = _flameTowers.get(towerIndex);
		return (spawn != null) ? spawn.getUpgradeLevel() : 0;
	}
	
	/**
	 * Save properties of a Flame Tower.
	 * @param towerIndex : The tower to affect.
	 * @param level : The new level of update.
	 * @param save : Should it be saved on database or not.
	 */
	public void setTrapUpgrade(int towerIndex, int level, boolean save)
	{
		if (save)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("REPLACE INTO castle_trapupgrade (castleId, towerIndex, level) values (?,?,?)");
				statement.setInt(1, _castleId);
				statement.setInt(2, towerIndex);
				statement.setInt(3, level);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Exception: setTrapUpgradeLevel(int towerIndex, int level, int castleId): " + e.getMessage(), e);
			}
		}
		
		final TowerSpawnLocation spawn = _flameTowers.get(towerIndex);
		if (spawn != null)
			spawn.setUpgradeLevel(level);
	}
	
	/**
	 * Delete all traps informations for a single castle.
	 */
	public void removeTrapUpgrade()
	{
		for (TowerSpawnLocation ts : _flameTowers)
			ts.setUpgradeLevel(0);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM castle_trapupgrade WHERE castleId=?");
			statement.setInt(1, _castleId);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception: removeTrapUpgrade(): " + e.getMessage(), e);
		}
	}
	
	public void checkItemsForMember(ClanMember member)
	{
		
		final Player player = member.getPlayerInstance();
		
		if (player != null)
			player.checkItemRestriction();
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_ITEMS_LOC))
			{
				ps.setInt(1, _circletId);
				ps.setInt(2, member.getObjectId());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Failed to check items for member.", e);
			}
		}
	}
	
	public void checkItemsForClan(Clan clan)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_ITEMS_LOC))
		{
			ps.setInt(1, _circletId);
			
			for (ClanMember member : clan.getMembers())
			{
				final Player player = member.getPlayerInstance();
				if (player != null)
					player.checkItemRestriction();
				else
				{
					ps.setInt(2, member.getObjectId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Failed to check items for clan.", e);
		}
	}
}