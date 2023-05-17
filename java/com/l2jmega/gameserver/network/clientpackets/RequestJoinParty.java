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
package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.model.BlockList;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.AskJoinParty;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * format cdd
 */
public final class RequestJoinParty extends L2GameClientPacket
{
	private String _name;
	private static int _itemDistribution;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_itemDistribution = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player requestor = getClient().getActiveChar();
		if (requestor == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_name);
		if (target == null)
		{
			requestor.sendPacket(SystemMessageId.FIRST_SELECT_USER_TO_INVITE_TO_PARTY);
			return;
		}

		if (TvT.is_started() && (target._inEventTvT || requestor._inEventTvT) && !requestor.isGM())
		{
			requestor.sendMessage("You or your target cannot Join Party during TvT Event.");
			return;
		}
		
		if (target.getClient().isDetached())
		{
			requestor.sendMessage("Player is in offline mode.");
			return;
		}
		
	    if (Config.ANTZERG_CHECK_PARTY_INVITE && target.getClan() != requestor.getClan())
	    {
	      if (target.isInsideZone(ZoneId.FLAG) && requestor.isInsideZone(ZoneId.FLAG)  && !requestor.isGM() && Config.PARTY_FLAGZONE)
	      {
	          requestor.sendMessage("ANTI-ZERG: You can not invite players who are not from your clan/ally to your party in the Party zone.");
	          return;
	      }
	      if (target.isInsideZone(ZoneId.RAID) || requestor.isInsideZone(ZoneId.RAID)) 
	      {	          
		      requestor.sendMessage("ANTI-ZERG: You can not invite players who are not from your clan/ally to your party in the Boss zone.");
		       return;
		   }
	      
	      if (target.isInsideZone(ZoneId.RAID_NO_FLAG) || requestor.isInsideZone(ZoneId.RAID_NO_FLAG)) 
	      {	          
		      requestor.sendMessage("ANTI-ZERG: You can not invite players who are not from your clan/ally to your party in the Boss zone.");
		       return;
		   }
	      
		  if (target.isInsideZone(ZoneId.CUSTOM) && requestor.isInsideZone(ZoneId.CUSTOM) && !requestor.isGM() && Config.PARTY_PVPZONE)
		  {
			  requestor.sendMessage("ANTI-ZERG: You can not invite players who are not from your clan/ally to your party in the pvp Zone.");
			  return;
		  }
	     }

	    if (Config.ALLOW_HEALER_COUNT && checkBsQuanty(requestor) && (target.getClassId() == ClassId.SHILLIEN_ELDER || target.getClassId() == ClassId.SHILLIEN_SAINT || target.getClassId() == ClassId.BISHOP || target.getClassId() == ClassId.CARDINAL || target.getClassId() == ClassId.ELVEN_ELDER || target.getClassId() == ClassId.EVAS_SAINT)) {
	        
	        requestor.sendMessage("Your party has exceeded the maximum number of healer classes.");
	        
	        return;
	      } 
	    
		if (target.isPartyInvProt())
		{
			requestor.sendMessage(target.getName() + " is in party refusal mode.");
			return;
		}
		
		if (BlockList.isBlocked(target, requestor))
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addCharName(target));
			return;
		}
		
		if (target.equals(requestor) || target.isCursedWeaponEquipped() || requestor.isCursedWeaponEquipped() || (target.getAppearance().getInvisible() && !target.isInsideZone(ZoneId.TOURNAMENT) && !target._inEventCTF && !target.isGM()))
		{
			requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}
		
		if (target.isInParty())
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_IN_PARTY).addCharName(target));
			return;
		}
		
		if (target.getClient().isDetached())
		{
			requestor.sendMessage("The player you tried to invite is in offline mode.");
			return;
		}
		
		if (target.isInJail() || requestor.isInJail())
		{
			requestor.sendMessage("The player you tried to invite is currently jailed.");
			return;
		}
		
		if (target.isInOlympiadMode() || requestor.isInOlympiadMode() || target.isOlympiadProtection() || requestor.isOlympiadProtection())
		{
			requestor.sendMessage(target.getName() + " is in Olympiad Mode!");
			return;
		}
		
		if (!requestor.isInParty())
			createNewParty(target, requestor);
		else
		{
			if (!requestor.getParty().isInDimensionalRift())
				addTargetToParty(target, requestor);
		}
	}
	
	  public static boolean checkBsQuanty(Player requestor) {
		    L2Party party = requestor.getParty();
		    if (party == null) {
		      return false;
		    }
		    int count = 0;
		    for (Player member : requestor.getParty().getPartyMembers()) {
		      
		      if (member != null)
		      {
		        if (member.getClassId() == ClassId.SHILLIEN_ELDER || member.getClassId() == ClassId.SHILLIEN_SAINT || member.getClassId() == ClassId.BISHOP || member.getClassId() == ClassId.CARDINAL || member.getClassId() == ClassId.ELVEN_ELDER || member.getClassId() == ClassId.EVAS_SAINT) {
		          count++;
		        }
		      }
		    } 
		    if (count >= Config.MAX_HEALER_PARTY) {
		      return true;
		    }
		    return false;
		  }
	  
	/**
	 * @param target
	 * @param requestor
	 */
	private static void addTargetToParty(Player target, Player requestor)
	{
		final L2Party party = requestor.getParty();
		if (party == null)
			return;
		
		if (!party.isLeader(requestor))
		{
			requestor.sendPacket(SystemMessageId.ONLY_LEADER_CAN_INVITE);
			return;
		}
		
		if (party.getMemberCount() >= 9)
		{
			requestor.sendPacket(SystemMessageId.PARTY_FULL);
			return;
		}
		
		if (party.getPendingInvitation() && !party.isInvitationRequestExpired())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (!target.isProcessingRequest())
		{
			requestor.onTransactionRequest(target);
			
			// in case a leader change has happened, use party's mode
			target.sendPacket(new AskJoinParty(requestor.getName(), party.getLootDistribution()));
			party.setPendingInvitation(true);
			
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY).addCharName(target));
			if (Config.DEBUG)
				_log.fine("Sent out a party invitation to " + target.getName());
		}
		else
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
			if (Config.DEBUG)
				_log.warning(requestor.getName() + " already received a party invitation");
		}
	}
	
	/**
	 * @param target
	 * @param requestor
	 */
	public static void createNewParty(Player target, Player requestor)
	{
		if (!target.isProcessingRequest())
		{
			requestor.setParty(new L2Party(requestor, _itemDistribution));
			
			requestor.onTransactionRequest(target);
			target.sendPacket(new AskJoinParty(requestor.getName(), _itemDistribution));
			requestor.getParty().setPendingInvitation(true);
			
			if (Config.DEBUG)
				_log.fine("Sent out a party invitation to " + target.getName());
			
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY).addCharName(target));
		}
		else
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			
			if (Config.DEBUG)
				_log.warning(requestor.getName() + " already received a party invitation");
		}
	}
	
	public static void createCustomParty(Player requestor)
	{
		requestor.setParty(new L2Party(requestor, _itemDistribution));
	}
	
}