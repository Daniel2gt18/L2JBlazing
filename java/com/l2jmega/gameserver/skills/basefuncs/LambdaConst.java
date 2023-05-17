package com.l2jmega.gameserver.skills.basefuncs;

import com.l2jmega.gameserver.skills.Env;

/**
 * @author mkizub
 */
public final class LambdaConst extends Lambda
{
	private final double _value;
	
	public LambdaConst(double value)
	{
		_value = value;
	}
	
	@Override
	public double calc(Env env)
	{
		return _value;
	}
}