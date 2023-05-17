package com.l2jmega.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.FriendList;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public final class RequestFriendDel extends L2GameClientPacket
{
	private String _name;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		int id = PlayerNameTable.getInstance().getPlayerObjectId(_name);
		
		if (id == -1 || !activeChar.getFriendList().contains(id))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_NOT_ON_YOUR_FRIENDS_LIST).addString(_name));
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE (char_id = ? AND friend_id = ?) OR (char_id = ? AND friend_id = ?)");
			statement.setInt(1, activeChar.getObjectId());
			statement.setInt(2, id);
			statement.setInt(3, id);
			statement.setInt(4, activeChar.getObjectId());
			statement.execute();
			statement.close();
			
			// Player deleted from your friendlist
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST).addString(_name));
			
			activeChar.getFriendList().remove(Integer.valueOf(id));
			activeChar.sendPacket(new FriendList(activeChar)); // update friendList *heavy method*
			
			Player player = World.getInstance().getPlayer(_name);
			if (player != null)
			{
				player.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
				player.sendPacket(new FriendList(player)); // update friendList *heavy method*
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete friend objectid: ", e);
		}
	}
}