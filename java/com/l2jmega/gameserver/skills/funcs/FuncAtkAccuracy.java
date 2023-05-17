package com.l2jmega.gameserver.skills.funcs;

import com.l2jmega.gameserver.model.actor.Summon;
import com.l2jmega.gameserver.skills.Env;
import com.l2jmega.gameserver.skills.Formulas;
import com.l2jmega.gameserver.skills.Stats;
import com.l2jmega.gameserver.skills.basefuncs.Func;

public class FuncAtkAccuracy extends Func
{
	static final FuncAtkAccuracy _faa_instance = new FuncAtkAccuracy();
	
	public static Func getInstance()
	{
		return _faa_instance;
	}
	
	private FuncAtkAccuracy()
	{
		super(Stats.ACCURACY_COMBAT, 0x10, null, null);
	}
	
	@Override
	public void calc(Env env)
	{
		final int level = env.getCharacter().getLevel();
		
		env.addValue(Formulas.BASE_EVASION_ACCURACY[env.getCharacter().getDEX()] + level);
		
		if (env.getCharacter() instanceof Summon)
			env.addValue((level < 60) ? 4 : 5);
	}
}