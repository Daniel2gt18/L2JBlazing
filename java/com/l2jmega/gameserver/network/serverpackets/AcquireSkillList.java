package com.l2jmega.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

public final class AcquireSkillList extends L2GameServerPacket
{
	public enum SkillType
	{
		Usual,
		Fishing,
		Clan
	}
	
	private List<Skill> _skills;
	private final SkillType _skillType;
	
	private static class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int spCost;
		public int requirements;
		
		public Skill(int pId, int pNextLevel, int pMaxLevel, int pSpCost, int pRequirements)
		{
			id = pId;
			nextLevel = pNextLevel;
			maxLevel = pMaxLevel;
			spCost = pSpCost;
			requirements = pRequirements;
		}
	}
	
	public AcquireSkillList(SkillType type)
	{
		_skillType = type;
	}
	
	public void addSkill(int id, int nextLevel, int maxLevel, int spCost, int requirements)
	{
		if (_skills == null)
			_skills = new ArrayList<>();
		
		_skills.add(new Skill(id, nextLevel, maxLevel, spCost, requirements));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x8a);
		writeD(_skillType.ordinal());
		writeD(_skills.size());
		
		for (Skill temp : _skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.spCost);
			writeD(temp.requirements);
		}
	}
}