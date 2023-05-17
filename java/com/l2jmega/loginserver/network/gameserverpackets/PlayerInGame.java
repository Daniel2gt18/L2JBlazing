package com.l2jmega.loginserver.network.gameserverpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class PlayerInGame extends ClientBasePacket
{
	private final List<String> _accounts;

	public PlayerInGame(final byte[] decrypt)
	{
		super(decrypt);
		_accounts = new ArrayList<>();
		for (int size = readH(), i = 0; i < size; ++i)
			_accounts.add(readS());
	}

	public List<String> getAccounts()
	{
		return _accounts;
	}
}
