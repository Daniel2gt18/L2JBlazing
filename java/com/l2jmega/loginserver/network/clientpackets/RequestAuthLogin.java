package com.l2jmega.loginserver.network.clientpackets;

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import com.l2jmega.Config;
import com.l2jmega.loginserver.GameServerTable;
import com.l2jmega.loginserver.L2LoginClient;
import com.l2jmega.loginserver.LoginController;
import com.l2jmega.loginserver.network.serverpackets.AccountKicked;
import com.l2jmega.loginserver.network.serverpackets.LoginFail;
import com.l2jmega.loginserver.network.serverpackets.LoginOk;
import com.l2jmega.loginserver.network.serverpackets.ServerList;

public class RequestAuthLogin extends L2LoginClientPacket
{
	private static Logger _log;
	private final byte[] _raw;
	private String _user;
	private String _password;
	private int _ncotp;

	public RequestAuthLogin()
	{
		_raw = new byte[128];
	}

	public String getPassword()
	{
		return _password;
	}

	public String getUser()
	{
		return _user;
	}

	public int getOneTimePassword()
	{
		return _ncotp;
	}

	@Override
	public boolean readImpl()
	{
		if (super._buf.remaining() >= 128)
		{
			this.readB(_raw);
			return true;
		}
		return false;
	}

	@Override
	public void run()
	{
		byte[] decrypted = null;
		final L2LoginClient client = getClient();
		try
		{
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(2, getClient().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0, 128);
		}
		catch (GeneralSecurityException e)
		{
			RequestAuthLogin._log.log(Level.INFO, "", e);
			return;
		}
		try
		{
			_user = new String(decrypted, 94, 14).trim().toLowerCase();
			_password = new String(decrypted, 108, 16).trim();
			_ncotp = decrypted[124];
			_ncotp |= decrypted[125] << 8;
			_ncotp |= decrypted[126] << 16;
			_ncotp |= decrypted[127] << 24;
		}
		catch (Exception e2)
		{
			return;
		}
		final LoginController lc = LoginController.getInstance();
		final LoginController.AuthLoginResult result = lc.tryAuthLogin(_user, _password, client);
		switch (result)
		{
			case AUTH_SUCCESS:
			{
				client.setAccount(_user);
				client.setState(L2LoginClient.LoginClientState.AUTHED_LOGIN);
				client.setSessionKey(lc.assignSessionKeyToClient(_user, client));
				if (Config.SHOW_LICENCE)
				{
					client.sendPacket(new LoginOk(getClient().getSessionKey()));
					break;
				}
				getClient().sendPacket(new ServerList(getClient()));
				break;
			}
			case INVALID_PASSWORD:
			{
				client.close(LoginFail.LoginFailReason.REASON_USER_OR_PASS_WRONG);
				break;
			}
			case ACCOUNT_BANNED:
			{
				client.close(new AccountKicked(AccountKicked.AccountKickedReason.REASON_PERMANENTLY_BANNED));
				break;
			}
			case ALREADY_ON_LS:
			{
				final L2LoginClient oldClient;
				if ((oldClient = lc.getAuthedClient(_user)) != null)
				{
					lc.removeAuthedLoginClient(_user);
					oldClient.close(LoginFail.LoginFailReason.REASON_ACCOUNT_IN_USE);
					lc.removeAuthedLoginClient(_user);
				}
				client.close(LoginFail.LoginFailReason.REASON_ACCOUNT_IN_USE);
				break;
			}
			case ALREADY_ON_GS:
			{
				final GameServerTable.GameServerInfo gsi;
				if ((gsi = lc.getAccountOnGameServer(_user)) != null)
				{
					client.close(LoginFail.LoginFailReason.REASON_ACCOUNT_IN_USE);
					if (gsi.isAuthed())
						gsi.getGameServerThread().kickPlayer(_user);
					gsi.getGameServerThread().removeAcc(_user);
					break;
				}
				break;
			}
		}
	}

	static
	{
		RequestAuthLogin._log = Logger.getLogger(RequestAuthLogin.class.getName());
	}
}
