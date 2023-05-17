package com.l2jmega.gameserver.skills.funcs;

import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.skills.Formulas;
import com.l2jmega.gameserver.skills.Stats;
import com.l2jmega.gameserver.skills.basefuncs.Func;

public class FuncMAtkCritical extends Func
{
	static final FuncMAtkCritical _fac_instance = new FuncMAtkCritical();
	
	public static Func getInstance()
	{
		return _fac_instance;
	}
	
	private FuncMAtkCritical()
	{
		super(Stats.MCRITICAL_RATE, 0x30, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final Creature player = env.getCharacter();
		if (player instanceof Player)
		{
			if (player.getActiveWeaponInstance() != null)
				env.mulValue(Formulas.WIT_BONUS[player.getWIT()]);
		}
		else
			env.mulValue(Formulas.WIT_BONUS[player.getWIT()]);
	}
}