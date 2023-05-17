package com.l2jmega.gameserver.network.gameserverpackets;

public class PlayerLogout extends GameServerBasePacket
{
	public PlayerLogout(String player)
	{
		writeC(0x03);
		writeS(player);
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}