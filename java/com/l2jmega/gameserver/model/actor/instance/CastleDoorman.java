package com.l2jmega.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.pledge.Clan;

public class CastleDoorman extends Doorman
{
	public CastleDoorman(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}
	
	@Override
	protected void openDoors(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command.substring(10), ", ");
		st.nextToken();
		
		while (st.hasMoreTokens())
		{/*
			 * if (getConquerableHall() != null) getConquerableHall().openCloseDoor(Integer.parseInt(st.nextToken()), true); else
			 */
			getCastle().openDoor(player, Integer.parseInt(st.nextToken()));
		}
	}
	
	@Override
	protected final void closeDoors(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command.substring(11), ", ");
		st.nextToken();
		
		while (st.hasMoreTokens())
		{/*
			 * if (getConquerableHall() != null) getConquerableHall().openCloseDoor(Integer.parseInt(st.nextToken()), false); else
			 */
			getCastle().closeDoor(player, Integer.parseInt(st.nextToken()));
		}
	}
	
	@Override
	protected final boolean isOwnerClan(Player player)
	{
		if (player.getClan() != null)
		{/*
			 * if (getConquerableHall() != null) { // player should have privileges to open doors if (player.getClanId() == getConquerableHall().getOwnerId() && (player.getClanPrivileges() & L2Clan.CP_CS_OPEN_DOOR) == L2Clan.CP_CS_OPEN_DOOR) return true; } else
			 */
			if (getCastle() != null)
			{
				// player should have privileges to open doors
				if (player.getClanId() == getCastle().getOwnerId() && (player.getClanPrivileges() & Clan.CP_CS_OPEN_DOOR) == Clan.CP_CS_OPEN_DOOR)
					return true;
			}
		}
		return false;
	}
	
	@Override
	protected final boolean isUnderSiege()
	{/*
		 * SiegableHall hall = getConquerableHall(); if (hall != null) return hall.isInSiege();
		 */
		
		return getCastle().getSiegeZone().isActive();
	}
}