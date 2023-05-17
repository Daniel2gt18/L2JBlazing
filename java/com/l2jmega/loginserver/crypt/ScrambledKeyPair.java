package com.l2jmega.loginserver.crypt;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

public class ScrambledKeyPair
{
	private final KeyPair _pair;
	private final byte[] _scrambledModulus;

	public ScrambledKeyPair(final KeyPair pPair)
	{
		_pair = pPair;
		_scrambledModulus = scrambleModulus(((RSAPublicKey) _pair.getPublic()).getModulus());
	}

	public KeyPair getKeyPair()
	{
		return _pair;
	}

	public byte[] getScrambledModulus()
	{
		return _scrambledModulus;
	}

	private static final byte[] scrambleModulus(final BigInteger modulus)
	{
		byte[] scrambledMod = modulus.toByteArray();
		if (scrambledMod.length == 129 && scrambledMod[0] == 0)
		{
			final byte[] temp = new byte[128];
			System.arraycopy(scrambledMod, 1, temp, 0, 128);
			scrambledMod = temp;
		}
		for (int i = 0; i < 4; ++i)
		{
			final byte temp2 = scrambledMod[i];
			scrambledMod[i] = scrambledMod[77 + i];
			scrambledMod[77 + i] = temp2;
		}
		for (int i = 0; i < 64; ++i)
			scrambledMod[i] ^= scrambledMod[64 + i];
		for (int i = 0; i < 4; ++i)
			scrambledMod[13 + i] ^= scrambledMod[52 + i];
		for (int i = 0; i < 64; ++i)
			scrambledMod[64 + i] ^= scrambledMod[i];
		return scrambledMod;
	}
}
