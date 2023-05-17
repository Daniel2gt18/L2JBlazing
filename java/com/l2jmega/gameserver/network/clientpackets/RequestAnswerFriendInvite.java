package com.l2jmega.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.FriendList;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * format cdd
 */
public final class RequestAnswerFriendInvite extends L2GameClientPacket
{
	private int _response;
	
	@Override
	protected void readImpl()
	{
		_response = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final Player requestor = player.getActiveRequester();
		if (requestor == null)
			return;
		
		if (_response == 1)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("INSERT INTO character_friends (char_id, friend_id) VALUES (?,?), (?,?)");
				statement.setInt(1, requestor.getObjectId());
				statement.setInt(2, player.getObjectId());
				statement.setInt(3, player.getObjectId());
				statement.setInt(4, requestor.getObjectId());
				statement.execute();
				statement.close();
				
				requestor.sendPacket(SystemMessageId.YOU_HAVE_SUCCEEDED_INVITING_FRIEND);
				
				// Player added to your friendlist
				requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED_TO_FRIENDS).addCharName(player));
				requestor.getFriendList().add(player.getObjectId());
				
				// has joined as friend.
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_AS_FRIEND).addCharName(requestor));
				player.getFriendList().add(requestor.getObjectId());
				
				// update friendLists *heavy method*
				requestor.sendPacket(new FriendList(requestor));
				player.sendPacket(new FriendList(player));
			}
			catch (Exception e)
			{
				_log.warning("could not add friend objectid: " + e);
			}
		}
		else
			requestor.sendPacket(SystemMessageId.FAILED_TO_INVITE_A_FRIEND);
		
		player.setActiveRequester(null);
		requestor.onTransactionResponse();
	}
}