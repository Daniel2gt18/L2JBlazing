package com.l2jmega.gameserver.skills.funcs;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.skills.Stats;
import com.l2jmega.gameserver.skills.basefuncs.Func;

public class FuncHennaWIT extends Func
{
	static final FuncHennaWIT _fh_instance = new FuncHennaWIT();
	
	public static Func getInstance()
	{
		return _fh_instance;
	}
	
	private FuncHennaWIT()
	{
		super(Stats.STAT_WIT, 0x10, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final Player player = env.getPlayer();
		if (player != null)
			env.addValue(player.getHennaStatWIT());
	}
}