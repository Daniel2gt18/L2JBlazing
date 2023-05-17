package com.l2jmega.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.l2jmega.commons.lang.StringUtil;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;

public final class RequestChangePetName extends L2GameClientPacket
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
		
		if (_name.length() < 2 || _name.length() > 8)
		{
			activeChar.sendPacket(SystemMessageId.NAMING_PETNAME_UP_TO_8CHARS);
			return;
		}
		
		if (!StringUtil.isValidName(_name, "^[A-Za-z]{2,8}$"))
		{
			activeChar.sendPacket(SystemMessageId.NAMING_PETNAME_CONTAINS_INVALID_CHARS);
			return;
		}
		
		if (!activeChar.hasPet())
			return;
		
		final Pet pet = (Pet) activeChar.getPet();
		
		if (pet.getName() != null)
		{
			activeChar.sendPacket(SystemMessageId.NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET);
			return;
		}
		
		if (doesPetNameExist(_name))
		{
			activeChar.sendPacket(SystemMessageId.NAMING_ALREADY_IN_USE_BY_ANOTHER_PET);
			return;
		}
		
		pet.setName(_name);
		pet.sendPetInfosToOwner();
	}
	
	private static boolean doesPetNameExist(String name)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT name FROM pets WHERE name=?");
			statement.setString(1, name);
			
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.warning("could not check existing petname:" + e.getMessage());
		}
		return result;
	}
}