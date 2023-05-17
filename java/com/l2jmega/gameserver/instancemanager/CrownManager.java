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
package com.l2jmega.gameserver.instancemanager;

import java.util.logging.Logger;

import com.l2jmega.gameserver.data.sql.CrownTable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.model.pledge.ClanMember;

/**
 * @author Computador
 */
public class CrownManager
{
	protected static final Logger _log = Logger.getLogger(CrownManager.class.getName());
	private static CrownManager _instance;
	
	public static final CrownManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new CrownManager();
		}
		return _instance;
	}
	
	public CrownManager()
	{
		_log.info("CrownManager: initialized");
	}
	
	public void checkCrowns(Clan clan)
	{
		if (clan == null)
			return;
		
		for (ClanMember member : clan.getMembers())
		{
			if (member != null && member.isOnline() && member.getPlayerInstance() != null)
				checkCrowns(member.getPlayerInstance());
		}
	}
	
	public void checkCrowns(Player activeChar)
	{
		boolean isLeader = false;
		int crownId = -1;
		
		Clan activeCharClan = activeChar.getClan();
		ClanMember activeCharClanLeader;
		
		if (activeCharClan != null)
			activeCharClanLeader = activeChar.getClan().getLeader();
		else
			activeCharClanLeader = null;
		
		if (activeCharClan != null)
		{
			Castle activeCharCastle = CastleManager.getInstance().getCastleByOwner(activeCharClan);
			
			if (activeCharCastle != null)
				crownId = CrownTable.getCrownId(activeCharCastle.getCastleId());
			
			activeCharCastle = null;
			
			if (activeCharClanLeader != null && activeCharClanLeader.getObjectId() == activeChar.getObjectId())
				isLeader = true;
		}
		
		activeCharClan = null;
		activeCharClanLeader = null;
		
		if (crownId > 0)
		{
			if (isLeader && activeChar.getInventory().getItemByItemId(6841) == null)
			{
				activeChar.addItem("Crown", 6841, 1, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
			
			if (activeChar.getInventory().getItemByItemId(crownId) == null)
			{
				activeChar.addItem("Crown", crownId, 1, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
		}
		
		boolean alreadyFoundCirclet = false;
		boolean alreadyFoundCrown = false;
		
		for (ItemInstance item : activeChar.getInventory().getItems())
		{
			if (CrownTable.getCrownList().contains(item.getItemId()))
			{
				if (crownId > 0)
				{
					if (item.getItemId() == crownId)
					{
						if (!alreadyFoundCirclet)
						{
							alreadyFoundCirclet = true;
							continue;
						}
					}
					else if (item.getItemId() == 6841 && isLeader)
					{
						if (!alreadyFoundCrown)
						{
							alreadyFoundCrown = true;
							continue;
						}
					}
				}
				
				activeChar.destroyItem("Removing Crown", item, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
		}
	}
}
