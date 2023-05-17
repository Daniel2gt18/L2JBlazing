package com.l2jmega.loginserver;

import java.nio.ByteBuffer;

import com.l2jmega.commons.mmocore.IPacketHandler;
import com.l2jmega.commons.mmocore.ReceivablePacket;

import com.l2jmega.loginserver.network.clientpackets.AuthGameGuard;
import com.l2jmega.loginserver.network.clientpackets.RequestAuthLogin;
import com.l2jmega.loginserver.network.clientpackets.RequestServerList;
import com.l2jmega.loginserver.network.clientpackets.RequestServerLogin;

public final class L2LoginPacketHandler implements IPacketHandler<L2LoginClient>
{
	@Override
	public ReceivablePacket<L2LoginClient> handlePacket(final ByteBuffer buf, final L2LoginClient client)
	{
		final int opcode = buf.get() & 0xFF;
		ReceivablePacket<L2LoginClient> packet = null;
		final L2LoginClient.LoginClientState state = client.getState();
		switch (state)
		{
			case CONNECTED:
			{
				if (opcode == 7)
				{
					packet = new AuthGameGuard();
					break;
				}
				debugOpcode(opcode, state);
				break;
			}
			case AUTHED_GG:
			{
				if (opcode == 0)
				{
					packet = new RequestAuthLogin();
					break;
				}
				debugOpcode(opcode, state);
				break;
			}
			case AUTHED_LOGIN:
			{
				if (opcode == 5)
				{
					packet = new RequestServerList();
					break;
				}
				if (opcode == 2)
				{
					packet = new RequestServerLogin();
					break;
				}
				debugOpcode(opcode, state);
				break;
			}
		}
		return packet;
	}

	private static void debugOpcode(final int opcode, final L2LoginClient.LoginClientState state)
	{
		System.out.println("Unknown Opcode: " + opcode + " for state: " + state.name());
	}
}
