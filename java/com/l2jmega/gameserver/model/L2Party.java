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
package com.l2jmega.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.math.MathUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.instancemanager.DuelManager;
import com.l2jmega.gameserver.instancemanager.SevenSignsFestival;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Servitor;
import com.l2jmega.gameserver.model.entity.DimensionalRift;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoom;
import com.l2jmega.gameserver.model.partymatching.PartyMatchRoomList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExCloseMPCC;
import com.l2jmega.gameserver.network.serverpackets.ExOpenMPCC;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.network.serverpackets.PartyMemberPosition;
import com.l2jmega.gameserver.network.serverpackets.PartySmallWindowAdd;
import com.l2jmega.gameserver.network.serverpackets.PartySmallWindowAll;
import com.l2jmega.gameserver.network.serverpackets.PartySmallWindowDelete;
import com.l2jmega.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * @author nuocnam
 */
public class L2Party
{
	private static final double[] BONUS_EXP_SP =
	{
		1,
		1.30,
		1.39,
		1.50,
		1.54,
		1.58,
		1.63,
		1.67,
		1.71
	};
	private static final int PARTY_POSITION_BROADCAST = 12000;
	
	public static final int ITEM_LOOTER = 0;
	public static final int ITEM_RANDOM = 1;
	public static final int ITEM_RANDOM_SPOIL = 2;
	public static final int ITEM_ORDER = 3;
	public static final int ITEM_ORDER_SPOIL = 4;
	
	private final List<Player> _members = new CopyOnWriteArrayList<>();
	
	private boolean _pendingInvitation;
	private long _pendingInviteTimeout;
	private int _partyLvl;
	private final int _itemDistribution;
	private int _itemLastLoot;
	
	private L2CommandChannel _commandChannel;
	private DimensionalRift _dr;
	
	private Future<?> _positionBroadcastTask;
	protected PartyMemberPosition _positionPacket;
	
	private boolean _disbanding = false;
	
	/**
	 * The message type send to the party members.
	 */
	public enum MessageType
	{
		Expelled,
		Left,
		None,
		Disconnected
	}
	
	/**
	 * constructor ensures party has always one member - leader
	 * @param leader
	 * @param itemDistribution
	 */
	public L2Party(Player leader, int itemDistribution)
	{
		_members.add(leader);
		
		_partyLvl = leader.getLevel();
		_itemDistribution = itemDistribution;
	}
	
	/**
	 * returns number of party members
	 * @return
	 */
	public int getMemberCount()
	{
		return _members.size();
	}
	
	/**
	 * Check if another player can start invitation process
	 * @return boolean if party waits for invitation respond
	 */
	public boolean getPendingInvitation()
	{
		return _pendingInvitation;
	}
	
	/**
	 * set invitation process flag and store time for expiration happens when: player join party or player decline to join
	 * @param val
	 */
	public void setPendingInvitation(boolean val)
	{
		_pendingInvitation = val;
		_pendingInviteTimeout = System.currentTimeMillis() + Player.REQUEST_TIMEOUT * 1000;
	}
	
	/**
	 * Check if player invitation is expired
	 * @return boolean if time is expired
	 * @see com.l2jmega.gameserver.model.actor.instance.Player#isRequestExpired()
	 */
	public boolean isInvitationRequestExpired()
	{
		return _pendingInviteTimeout <= System.currentTimeMillis();
	}
	
	/**
	 * returns all party members
	 * @return
	 */
	public final List<Player> getPartyMembers()
	{
		return _members;
	}
	
	/**
	 * get random member from party
	 * @param ItemId
	 * @param target
	 * @return
	 */
	private Player getRandomMember(int ItemId, Creature target)
	{
		List<Player> availableMembers = new ArrayList<>();
		for (Player member : _members)
		{
			if (member != null && member.getInventory().validateCapacityByItemId(ItemId) && MathUtil.checkIfInRange(Config.ALT_PARTY_RANGE2, target, member, true))
				availableMembers.add(member);
		}
		
		if (!availableMembers.isEmpty())
			return Rnd.get(availableMembers);
		
		return null;
	}
	
	/**
	 * get next item looter
	 * @param ItemId
	 * @param target
	 * @return
	 */
	private Player getNextLooter(int ItemId, Creature target)
	{
		for (int i = 0; i < getMemberCount(); i++)
		{
			if (++_itemLastLoot >= getMemberCount())
				_itemLastLoot = 0;
			
			Player member = _members.get(_itemLastLoot);
			if (member != null && member.getInventory().validateCapacityByItemId(ItemId) && MathUtil.checkIfInRange(Config.ALT_PARTY_RANGE2, target, member, true))
				return member;
		}
		
		return null;
	}
	
	/**
	 * get next item looter
	 * @param player
	 * @param ItemId
	 * @param spoil
	 * @param target
	 * @return
	 */
	private Player getActualLooter(Player player, int ItemId, boolean spoil, Creature target)
	{
		Player looter = player;
		
		switch (_itemDistribution)
		{
			case ITEM_RANDOM:
				if (!spoil)
					looter = getRandomMember(ItemId, target);
				break;
			
			case ITEM_RANDOM_SPOIL:
				looter = getRandomMember(ItemId, target);
				break;
			
			case ITEM_ORDER:
				if (!spoil)
					looter = getNextLooter(ItemId, target);
				break;
			
			case ITEM_ORDER_SPOIL:
				looter = getNextLooter(ItemId, target);
				break;
		}
		
		if (looter == null)
			looter = player;
		
		return looter;
	}
	
	/**
	 * @param player The player to make checks on.
	 * @return true if player is party leader.
	 */
	public boolean isLeader(Player player)
	{
		return (getLeader().equals(player));
	}
	
	/**
	 * @return the Object ID for the party leader to be used as a unique identifier of this party
	 */
	public int getPartyLeaderOID()
	{
		return getLeader().getObjectId();
	}
	
	/**
	 * Broadcasts packet to every party member.
	 * @param packet The packet to broadcast.
	 */
	public void broadcastToPartyMembers(L2GameServerPacket packet)
	{
		for (Player member : _members)
		{
			if (member != null)
				member.sendPacket(packet);
		}
	}
	
	public void broadcastToPartyMembersNewLeader()
	{
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BECOME_A_PARTY_LEADER).addCharName(getLeader());
		for (Player member : _members)
		{
			if (member != null)
			{
				member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
				member.sendPacket(new PartySmallWindowAll(member, this));
				member.broadcastUserInfo();
				member.sendPacket(sm);
			}
		}
	}
	
	public void broadcastCSToPartyMembers(CreatureSay msg, Player broadcaster)
	{
		for (Player member : _members)
		{
			if (member != null && !BlockList.isBlocked(member, broadcaster))
				member.sendPacket(msg);
		}
	}
	
	/**
	 * Send a packet to all other Player of the Party.
	 * @param player
	 * @param msg
	 */
	public void broadcastToPartyMembers(Player player, L2GameServerPacket msg)
	{
		for (Player member : _members)
		{
			if (member != null && !member.equals(player))
				member.sendPacket(msg);
		}
	}
	
	/**
	 * adds new member to party
	 * @param player
	 */
	public void addPartyMember(Player player)
	{
		if (_members.contains(player))
			return;
		
		// Send new member party window for all members
		player.sendPacket(new PartySmallWindowAll(player, this));
		broadcastToPartyMembers(new PartySmallWindowAdd(player, this));
		
		// Send messages
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_JOINED_S1_PARTY).addCharName(getLeader()));
		broadcastToPartyMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_PARTY).addCharName(player));
		
		// Add player to party, adjust party level
		_members.add(player);
		if (player.getLevel() > _partyLvl)
			_partyLvl = player.getLevel();
		
		// Update partySpelled
		for (Player member : _members)
		{
			if (member != null)
			{
				member.updateEffectIcons(true); // update party icons only
				member.broadcastUserInfo();
			}
		}
		
		// if (isInDimensionalRift())
		// _dr.partyMemberInvited();
		
		// open the CCInformationwindow
		if (isInCommandChannel())
			player.sendPacket(ExOpenMPCC.STATIC_PACKET);
		
		// activate position task
		if (_positionBroadcastTask == null)
			_positionBroadcastTask = ThreadPool.scheduleAtFixedRate(new PositionBroadcast(), PARTY_POSITION_BROADCAST / 2, PARTY_POSITION_BROADCAST);
	}
	
	public void addCustomPartyMember(Player player)
	{
		if (_members.contains(player))
			return;
		
		// Send new member party window for all members
		player.sendPacket(new PartySmallWindowAll(player, this));
		broadcastToPartyMembers(new PartySmallWindowAdd(player, this));
		
		// Send messages
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_JOINED_S1_PARTY).addCharName(getLeader()));
		broadcastToPartyMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_PARTY).addCharName(player));
		
		// Add player to party, adjust party level
		_members.add(player);
		if (player.getLevel() > _partyLvl)
			_partyLvl = player.getLevel();
		
		// Update partySpelled
		for (Player member : _members)
		{
			if (member != null)
			{
				member.updateEffectIcons(true); // update party icons only
				member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
				member.sendPacket(new PartySmallWindowAll(member, this));
				member.broadcastUserInfo();
			}
		}
		
		// if (isInDimensionalRift())
		// _dr.partyMemberInvited();
		
		// open the CCInformationwindow
		if (isInCommandChannel())
			player.sendPacket(ExOpenMPCC.STATIC_PACKET);
		
		// activate position task
		if (_positionBroadcastTask == null)
			_positionBroadcastTask = ThreadPool.scheduleAtFixedRate(new PositionBroadcast(), PARTY_POSITION_BROADCAST / 2, PARTY_POSITION_BROADCAST);
	}
	
	/**
	 * Removes a party member using its name.
	 * @param name player the player to be removed from the party.
	 * @param type the message type {@link MessageType}.
	 */
	public void removePartyMember(String name, MessageType type)
	{
		removePartyMember(getPlayerByName(name), type);
	}
	
	/**
	 * Removes a party member instance.
	 * @param player the player to be removed from the party.
	 * @param type the message type {@link MessageType}.
	 */
	public void removePartyMember(Player player, MessageType type)
	{
		if (!_members.contains(player))
			return;
		
		final boolean isLeader = isLeader(player);
		if (!_disbanding)
		{
			if (_members.size() == 2 || (isLeader && !Config.ALT_LEAVE_PARTY_LEADER && type != MessageType.Disconnected))
			{
				_disbanding = true;
				
				for (Player member : _members)
				{
					member.sendPacket(SystemMessageId.PARTY_DISPERSED);
					removePartyMember(member, MessageType.None);
				}
				return;
			}
		}
		
		_members.remove(player);
		recalculatePartyLevel();
		
		if (player.isFestivalParticipant())
			SevenSignsFestival.getInstance().updateParticipants(player, this);
		
		if (player.isInDuel())
			DuelManager.getInstance().onRemoveFromParty(player);
		
		if (player.getFusionSkill() != null)
			player.abortCast();
		
		for (Creature character : player.getKnownType(Creature.class))
			if (character.getFusionSkill() != null && character.getFusionSkill().getTarget() == player)
				character.abortCast();
		
		if (type == MessageType.Expelled)
		{
			player.sendPacket(SystemMessageId.HAVE_BEEN_EXPELLED_FROM_PARTY);
			broadcastToPartyMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_EXPELLED_FROM_PARTY).addCharName(player));
		}
		else if (type == MessageType.Left || type == MessageType.Disconnected)
		{
			player.sendPacket(SystemMessageId.YOU_LEFT_PARTY);
			broadcastToPartyMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_LEFT_PARTY).addCharName(player));
		}
		
		player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
		player.setParty(null);
		
		broadcastToPartyMembers(new PartySmallWindowDelete(player));
		
		// if (isInDimensionalRift())
		// _dr.partyMemberExited(player);
		
		// Close the CCInfoWindow
		if (isInCommandChannel())
			player.sendPacket(ExCloseMPCC.STATIC_PACKET);
		
		if (isLeader && _members.size() > 1 && (Config.ALT_LEAVE_PARTY_LEADER || type == MessageType.Disconnected))
			broadcastToPartyMembersNewLeader();
		else if (_members.size() == 1)
		{
			if (isInCommandChannel())
			{
				// delete the whole command channel when the party who opened the channel is disbanded
				if (_commandChannel.getChannelLeader().equals(getLeader()))
					_commandChannel.disbandChannel();
				else
					_commandChannel.removeParty(this);
			}
			
			if (getLeader() != null)
			{
				getLeader().setParty(null);
				if (getLeader().isInDuel())
					DuelManager.getInstance().onRemoveFromParty(getLeader());
			}
			
			if (_positionBroadcastTask != null)
			{
				_positionBroadcastTask.cancel(false);
				_positionBroadcastTask = null;
			}
			_members.clear();
		}
	}
	
	/**
	 * Change party leader (used for string arguments)
	 * @param name
	 */
	public void changePartyLeader(String name)
	{
		Player player = getPlayerByName(name);
		
		if (player != null && !player.isInDuel())
		{
			if (_members.contains(player))
			{
				if (isLeader(player))
					player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF);
				else
				{
					// Swap party members
					Player temp = getLeader();
					int p1 = _members.indexOf(player);
					
					_members.set(0, player);
					_members.set(p1, temp);
					
					broadcastToPartyMembersNewLeader();
					
					if (isInCommandChannel() && temp.equals(_commandChannel.getChannelLeader()))
					{
						_commandChannel.setChannelLeader(getLeader());
						_commandChannel.broadcastToChannelMembers(SystemMessage.getSystemMessage(SystemMessageId.COMMAND_CHANNEL_LEADER_NOW_S1).addCharName(_commandChannel.getChannelLeader()));
					}
					
					if (player.isInPartyMatchRoom())
					{
						PartyMatchRoom room = PartyMatchRoomList.getInstance().getPlayerRoom(player);
						room.changeLeader(player);
					}
				}
			}
			else
				player.sendPacket(SystemMessageId.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
		}
	}
	
	/**
	 * finds a player in the party by name
	 * @param name
	 * @return
	 */
	private Player getPlayerByName(String name)
	{
		for (Player member : _members)
		{
			if (member.getName().equalsIgnoreCase(name))
				return member;
		}
		return null;
	}
	
	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 */
	public void distributeItem(Player player, ItemInstance item)
	{
		if (item.getItemId() == 57)
		{
			distributeAdena(player, item.getCount(), player);
			ItemTable.getInstance().destroyItem("Party", item, player, null);
			return;
		}
		
		Player target = getActualLooter(player, item.getItemId(), false, player);
		target.addItem("Party", item, player, true);
		
		// Send messages to other party members about reward
		if (item.getCount() > 1)
			broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2).addCharName(target).addItemName(item).addItemNumber(item.getCount()));
		else if (item.getEnchantLevel() > 0)
			broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2_S3).addCharName(target).addNumber(item.getEnchantLevel()).addItemName(item));
		else
			broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2).addCharName(target).addItemName(item));
	}
	
	/**
	 * distribute item(s) to party members
	 * @param player
	 * @param item
	 * @param spoil
	 * @param target
	 */
	public void distributeItem(Player player, IntIntHolder item, boolean spoil, Attackable target)
	{
		if (item == null)
			return;
		
		if (item.getId() == 57)
		{
			distributeAdena(player, item.getValue(), target);
			return;
		}
		
		if (Config.CUSTOM_DROP_LIST.contains(item.getId()))
		{
			distribute(player, item.getValue(), target, item.getId());
			return;
		}
		
		Player looter = getActualLooter(player, item.getId(), spoil, target);
		looter.addItem(spoil ? "Sweep" : "Party", item.getId(), item.getValue(), player, true);
		
		// Send messages to other party members about reward
		SystemMessage msg;
		if (item.getValue() > 1)
		{
			msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.S1_SWEEPED_UP_S3_S2) : SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2);
			msg.addCharName(looter);
			msg.addItemName(item.getId());
			msg.addItemNumber(item.getValue());
		}
		else
		{
			msg = spoil ? SystemMessage.getSystemMessage(SystemMessageId.S1_SWEEPED_UP_S2) : SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2);
			msg.addCharName(looter);
			msg.addItemName(item.getId());
		}
		broadcastToPartyMembers(looter, msg);
	}
	
	/**
	 * Distribute adena to party members, according distance.
	 * @param player The player who picked.
	 * @param adena Amount of adenas.
	 * @param target Target used for distance checks.
	 */
	public void distributeAdena(Player player, int adena, Creature target)
	{
		List<Player> toReward = new ArrayList<>(_members.size());
		for (Player member : _members)
		{
			if (!MathUtil.checkIfInRange(Config.ALT_PARTY_RANGE2, target, member, true) || member.getAdena() == Integer.MAX_VALUE)
				continue;
			
			toReward.add(member);
		}
		
		// Avoid divisions by 0.
		if (toReward.isEmpty())
			return;
		
		final int count = adena / toReward.size();
		for (Player member : toReward)
			member.addAdena("Party", count, player, true);
	}
	
	/**
	 * Distribute Experience and SP rewards to Player Party members in the known area of the last attacker.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Get the Player owner of the L2SummonInstance (if necessary)</li> <li>Calculate the Experience and SP reward distribution rate</li> <li>Add Experience and SP to the Player</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT><BR>
	 * <BR>
	 * Exception are L2PetInstances that leech from the owner's XP; they get the exp indirectly, via the owner's exp gain<BR>
	 * @param xpReward The Experience reward to distribute
	 * @param spReward The SP reward to distribute
	 * @param rewardedMembers The list of Player to reward
	 * @param topLvl
	 * @param rewards The list of players and summons
	 */
	public void distributeXpAndSp(long xpReward, int spReward, List<Player> rewardedMembers, int topLvl, Map<Creature, RewardInfo> rewards)
	{
		final List<Player> validMembers = getValidMembers(rewardedMembers, topLvl);
		
		xpReward *= getExpBonus(validMembers.size());
		spReward *= getSpBonus(validMembers.size());
		
		int sqLevelSum = 0;
		for (Player member : validMembers)
			sqLevelSum += member.getLevel() * member.getLevel();
		
		// Go through the Players and L2PetInstances (not L2SummonInstances) that must be rewarded
		for (Player member : rewardedMembers)
		{
			if (member.isDead())
				continue;
			
			// Calculate and add the EXP and SP reward to the member
			if (validMembers.contains(member))
			{
				// The servitor penalty
				final float penalty = member.hasServitor() ? ((Servitor) member.getPet()).getExpPenalty() : 0;
				
				final double sqLevel = member.getLevel() * member.getLevel();
				final double preCalculation = (sqLevel / sqLevelSum) * (1 - penalty);
				
				final long xp = Math.round(xpReward * preCalculation);
				
				// Set new karma.
				member.updateKarmaLoss(xp);
				
				// Add the XP/SP points to the requested party member
				member.addExpAndSp(xp, (int)(spReward * preCalculation));
			}
			else
				member.addExpAndSp(0, 0);
		}
	}
	
	/**
	 * refresh party level
	 */
	public void recalculatePartyLevel()
	{
		int newLevel = 0;
		for (Player member : _members)
		{
			if (member == null)
			{
				_members.remove(member);
				continue;
			}
			
			if (member.getLevel() > newLevel)
				newLevel = member.getLevel();
		}
		_partyLvl = newLevel;
	}
	
	private static List<Player> getValidMembers(List<Player> members, int topLvl)
	{
		final List<Player> validMembers = new ArrayList<>();
		
		// Fixed LevelDiff cutoff point
		if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("level"))
		{
			for (Player member : members)
			{
				if (topLvl - member.getLevel() <= Config.PARTY_XP_CUTOFF_LEVEL)
					validMembers.add(member);
			}
		}
		// Fixed MinPercentage cutoff point
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("percentage"))
		{
			int sqLevelSum = 0;
			for (Player member : members)
				sqLevelSum += (member.getLevel() * member.getLevel());
			
			for (Player member : members)
			{
				int sqLevel = member.getLevel() * member.getLevel();
				if (sqLevel * 100 >= sqLevelSum * Config.PARTY_XP_CUTOFF_PERCENT)
					validMembers.add(member);
			}
		}
		// Automatic cutoff method
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("auto"))
		{
			int sqLevelSum = 0;
			for (Player member : members)
				sqLevelSum += (member.getLevel() * member.getLevel());
			
			int i = members.size() - 1;
			if (i < 1)
				return members;
			
			if (i >= BONUS_EXP_SP.length)
				i = BONUS_EXP_SP.length - 1;
			
			for (Player member : members)
			{
				int sqLevel = member.getLevel() * member.getLevel();
				if (sqLevel >= sqLevelSum * (1 - 1 / (1 + BONUS_EXP_SP[i] - BONUS_EXP_SP[i - 1])))
					validMembers.add(member);
			}
		}
		return validMembers;
	}
	
	private static double getBaseExpSpBonus(int membersCount)
	{
		int i = membersCount - 1;
		if (i < 1)
			return 1;
		
		if (i >= BONUS_EXP_SP.length)
			i = BONUS_EXP_SP.length - 1;
		
		return BONUS_EXP_SP[i];
	}
	
	private static double getExpBonus(int membersCount)
	{
		// Not a valid party
		if (membersCount < 2)
			return getBaseExpSpBonus(membersCount);
		
		return getBaseExpSpBonus(membersCount) * Config.RATE_PARTY_XP;
	}
	
	private static double getSpBonus(int membersCount)
	{
		// Not a valid party
		if (membersCount < 2)
			return getBaseExpSpBonus(membersCount);
		
		return getBaseExpSpBonus(membersCount) * Config.RATE_PARTY_SP;
	}
	
	public int getLevel()
	{
		return _partyLvl;
	}
	
	public int getLootDistribution()
	{
		return _itemDistribution;
	}
	
	public boolean isInCommandChannel()
	{
		return _commandChannel != null;
	}
	
	public L2CommandChannel getCommandChannel()
	{
		return _commandChannel;
	}
	
	public void setCommandChannel(L2CommandChannel channel)
	{
		_commandChannel = channel;
	}
	
	public boolean isInDimensionalRift()
	{
		return _dr != null;
	}
	
	public void setDimensionalRift(DimensionalRift dr)
	{
		_dr = dr;
	}
	
	public DimensionalRift getDimensionalRift()
	{
		return _dr;
	}
	
	public Player getLeader()
	{
		try
		{
			return _members.get(0);
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}
	
	protected class PositionBroadcast implements Runnable
	{
		@Override
		public void run()
		{
			if (_positionPacket == null)
				_positionPacket = new PartyMemberPosition(L2Party.this);
			else
				_positionPacket.reuse(L2Party.this);
			
			broadcastToPartyMembers(_positionPacket);
		}
	}
	
	public boolean wipedOut()
	{
		for (Player member : _members)
		{
			if (!member.isDead())
				return false;
		}
		return true;
	}
	
	public void distribute(Player player, int item, Creature target, int itemId)
	{
		List<Player> toReward = new ArrayList<>(_members.size());
		for (Player member : _members)
		{
			if (!MathUtil.checkIfInRange(Config.ALT_PARTY_RANGE, target, member, true))
				continue;
			toReward.add(member);
		}
		
		// Avoid divisions by 0.
		if (toReward.isEmpty())
			return;
		
		final int count = item / toReward.size();
		for (Player member : toReward)
		{
			msg(member, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2).addCharName(member).addItemName(itemId).addItemNumber(count));
			member.addItem("Party", itemId, count, player, false);
		}
	}
	
	public void msg(Player player, L2GameServerPacket msg)
	{
		for (Player member : _members)
		{
			if (member != null)
				member.sendPacket(msg);
		}
	}
	
}