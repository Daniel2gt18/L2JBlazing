package com.l2jmega.loginserver.network.serverpackets;

public final class PlayFail extends L2LoginServerPacket
{
	private final PlayFailReason _reason;

	public PlayFail(final PlayFailReason reason)
	{
		_reason = reason;
	}

	@Override
	protected void write()
	{
		writeC(6);
		writeC(_reason.getCode());
	}

	public enum PlayFailReason
	{
		REASON_SYSTEM_ERROR(1),
		REASON_USER_OR_PASS_WRONG(2),
		REASON3(3),
		REASON4(4),
		REASON_TOO_MANY_PLAYERS(15);

		private final int _code;

		private PlayFailReason(final int code)
		{
			_code = code;
		}

		public final int getCode()
		{
			return _code;
		}
	}
}
