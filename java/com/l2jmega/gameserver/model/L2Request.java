package com.l2jmega.gameserver.model;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.L2GameClientPacket;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * This class manages requests (transactions) between two players.
 * @author kriau
 */
public class L2Request
{
	private static final int REQUEST_TIMEOUT = 15; // in secs
	
	protected Player _player;
	protected Player _partner;
	
	protected L2GameClientPacket _requestPacket;
	
	public L2Request(Player player)
	{
		_player = player;
	}
	
	protected void clear()
	{
		_partner = null;
		_requestPacket = null;
	}
	
	/**
	 * Set the partner of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 * @param partner
	 */
	private synchronized void setPartner(Player partner)
	{
		_partner = partner;
	}
	
	/**
	 * @return the partner of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 */
	public Player getPartner()
	{
		return _partner;
	}
	
	/**
	 * Set the packet incomed from requestor.
	 * @param packet
	 */
	private synchronized void setRequestPacket(L2GameClientPacket packet)
	{
		_requestPacket = packet;
	}
	
	/**
	 * @return the packet originally incomed from requestor.
	 */
	public L2GameClientPacket getRequestPacket()
	{
		return _requestPacket;
	}
	
	/**
	 * Checks if request can be made and in success case puts both PC on request state.
	 * @param partner The partner.
	 * @param packet The packet sent.
	 * @return true if request has succeeded.
	 */
	public synchronized boolean setRequest(Player partner, L2GameClientPacket packet)
	{
		if (partner == null)
		{
			_player.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return false;
		}
		
		if (partner.getRequest().isProcessingRequest())
		{
			_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(partner));
			return false;
		}
		
		if (isProcessingRequest())
		{
			_player.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return false;
		}
		
		_partner = partner;
		_requestPacket = packet;
		setOnRequestTimer(true);
		_partner.getRequest().setPartner(_player);
		_partner.getRequest().setRequestPacket(packet);
		_partner.getRequest().setOnRequestTimer(false);
		return true;
	}
	
	private void setOnRequestTimer(boolean isRequestor)
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				clear();
			}
		}, REQUEST_TIMEOUT * 1000);
		
	}
	
	/**
	 * Clears PC request state. Should be called after answer packet receive.
	 */
	public void onRequestResponse()
	{
		if (_partner != null)
			_partner.getRequest().clear();
		
		clear();
	}
	
	/**
	 * @return True if a transaction is in progress.
	 */
	public boolean isProcessingRequest()
	{
		return _partner != null;
	}
}