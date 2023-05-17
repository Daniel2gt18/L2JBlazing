package com.l2jmega.gameserver.instancemanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.Duel;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;

public final class DuelManager
{
	private final Map<Integer, Duel> _duels = new ConcurrentHashMap<>();
	private final AtomicInteger _currentDuelId = new AtomicInteger();
	
	public static final DuelManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected DuelManager()
	{
		
	}
	
	public Duel getDuel(int duelId)
	{
		return _duels.get(duelId);
	}
	
	public void addDuel(Player playerA, Player playerB, int partyDuel)
	{
		if (playerA == null || playerB == null)
			return;
		
		final int duelId = _currentDuelId.incrementAndGet();
		_duels.put(duelId, new Duel(playerA, playerB, partyDuel, duelId));
	}
	
	public void removeDuel(int duelId)
	{
		_duels.remove(duelId);
	}
	
	public void doSurrender(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.doSurrender(player);
	}
	
	/**
	 * Updates player states.
	 * @param player - the dying player
	 */
	public void onPlayerDefeat(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.onPlayerDefeat(player);
	}
	
	/**
	 * Registers a buff which will be removed if the duel ends
	 * @param player
	 * @param buff
	 */
	public void onBuff(Player player, L2Effect buff)
	{
		if (player == null || !player.isInDuel() || buff == null)
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.onBuff(player, buff);
	}
	
	/**
	 * Removes player from duel.
	 * @param player - the removed player
	 */
	public void onRemoveFromParty(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.onRemoveFromParty(player);
	}
	
	/**
	 * Broadcasts a packet to the team opposing the given player.
	 * @param player
	 * @param packet
	 */
	public void broadcastToOppositeTeam(Player player, L2GameServerPacket packet)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel == null)
			return;
		
		if (duel.getPlayerA() == player)
			duel.broadcastToTeam2(packet);
		else if (duel.getPlayerB() == player)
			duel.broadcastToTeam1(packet);
		else if (duel.isPartyDuel())
		{
			if (duel.getPlayerA().getParty() != null && duel.getPlayerA().getParty().getPartyMembers().contains(player))
				duel.broadcastToTeam2(packet);
			else if (duel.getPlayerB().getParty() != null && duel.getPlayerB().getParty().getPartyMembers().contains(player))
				duel.broadcastToTeam1(packet);
		}
	}
	
	private static class SingletonHolder
	{
		protected static final DuelManager _instance = new DuelManager();
	}
}