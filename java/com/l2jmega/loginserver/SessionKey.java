package com.l2jmega.loginserver;

import com.l2jmega.Config;

public class SessionKey
{
	public int playOkID1;
	public int playOkID2;
	public int loginOkID1;
	public int loginOkID2;

	public SessionKey(final int loginOK1, final int loginOK2, final int playOK1, final int playOK2)
	{
		playOkID1 = playOK1;
		playOkID2 = playOK2;
		loginOkID1 = loginOK1;
		loginOkID2 = loginOK2;
	}

	@Override
	public String toString()
	{
		return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + loginOkID1 + " " + loginOkID2;
	}

	public boolean checkLoginPair(final int loginOk1, final int loginOk2)
	{
		return loginOkID1 == loginOk1 && loginOkID2 == loginOk2;
	}

	public boolean equals(final SessionKey key)
	{
		if (Config.SHOW_LICENCE)
			return playOkID1 == key.playOkID1 && loginOkID1 == key.loginOkID1 && playOkID2 == key.playOkID2 && loginOkID2 == key.loginOkID2;
		return playOkID1 == key.playOkID1 && playOkID2 == key.playOkID2;
	}
}
