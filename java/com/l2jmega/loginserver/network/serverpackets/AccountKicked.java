package com.l2jmega.loginserver.network.serverpackets;

public final class AccountKicked extends L2LoginServerPacket
{
	private final AccountKickedReason _reason;

	public AccountKicked(final AccountKickedReason reason)
	{
		_reason = reason;
	}

	@Override
	protected void write()
	{
		writeC(2);
		writeD(_reason.getCode());
	}

	public enum AccountKickedReason
	{
		REASON_DATA_STEALER(1),
		REASON_GENERIC_VIOLATION(8),
		REASON_7_DAYS_SUSPENDED(16),
		REASON_PERMANENTLY_BANNED(32);

		private final int _code;

		private AccountKickedReason(final int code)
		{
			_code = code;
		}

		public final int getCode()
		{
			return _code;
		}
	}
}
