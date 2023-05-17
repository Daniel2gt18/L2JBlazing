package com.l2jmega.loginserver.network.gameserverpackets;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import com.l2jmega.loginserver.network.clientpackets.ClientBasePacket;

public class BlowFishKey extends ClientBasePacket
{
	byte[] _key;
	protected static final Logger _log;

	public BlowFishKey(final byte[] decrypt, final RSAPrivateKey privateKey)
	{
		super(decrypt);
		final int size = readD();
		final byte[] tempKey = readB(size);
		try
		{
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(2, privateKey);
			byte[] tempDecryptKey;
			int i;
			int len;
			for (tempDecryptKey = rsaCipher.doFinal(tempKey), i = 0, len = tempDecryptKey.length; i < len && tempDecryptKey[i] == 0; ++i)
			{
			}
			System.arraycopy(tempDecryptKey, i, _key = new byte[len - i], 0, len - i);
		}
		catch (GeneralSecurityException e)
		{
			BlowFishKey._log.severe("Error While decrypting blowfish key (RSA)");
			e.printStackTrace();
		}
	}

	public byte[] getKey()
	{
		return _key;
	}

	static
	{
		_log = Logger.getLogger(BlowFishKey.class.getName());
	}
}
