package com.l2jmega.gameserver.model;

import java.util.List;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.xml.AugmentationData;
import com.l2jmega.gameserver.data.xml.AugmentationData.AugStat;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jmega.gameserver.skills.Stats;
import com.l2jmega.gameserver.skills.basefuncs.FuncAdd;
import com.l2jmega.gameserver.skills.basefuncs.LambdaConst;

/**
 * Used to store an augmentation and its boni
 * @author durgus
 */
public final class L2Augmentation
{
	private int _effectsId = 0;
	private AugmentationStatBoni _boni = null;
	private L2Skill _skill = null;
	
	public L2Augmentation(int effects, L2Skill skill)
	{
		_effectsId = effects;
		_boni = new AugmentationStatBoni(_effectsId);
		_skill = skill;
	}
	
	public L2Augmentation(int effects, int skill, int skillLevel)
	{
		this(effects, skill != 0 ? SkillTable.getInstance().getInfo(skill, skillLevel) : null);
	}
	
	public static class AugmentationStatBoni
	{
		private final Stats _stats[];
		private final float _values[];
		private boolean _active;
		
		public AugmentationStatBoni(int augmentationId)
		{
			_active = false;
			List<AugStat> as = AugmentationData.getInstance().getAugStatsById(augmentationId);
			
			_stats = new Stats[as.size()];
			_values = new float[as.size()];
			
			int i = 0;
			for (AugStat aStat : as)
			{
				_stats[i] = aStat.getStat();
				_values[i] = aStat.getValue();
				i++;
			}
		}
		
		public void applyBonus(Player player)
		{
			// make sure the bonuses are not applied twice..
			if (_active)
				return;
			
			for (int i = 0; i < _stats.length; i++)
				((Creature) player).addStatFunc(new FuncAdd(_stats[i], 0x40, this, new LambdaConst(_values[i])));
			
			_active = true;
		}
		
		public void removeBonus(Player player)
		{
			// make sure the bonuses are not removed twice
			if (!_active)
				return;
			
			((Creature) player).removeStatsByOwner(this);
			
			_active = false;
		}
	}
	
	public int getAttributes()
	{
		return _effectsId;
	}
	
	/**
	 * Get the augmentation "id" used in serverpackets.
	 * @return augmentationId
	 */
	public int getAugmentationId()
	{
		return _effectsId;
	}
	
	public L2Skill getSkill()
	{
		return _skill;
	}
	
	/**
	 * Applies the bonuses to the player.
	 * @param player
	 */
	public void applyBonus(Player player)
	{
		boolean updateTimeStamp = false;
		_boni.applyBonus(player);
		
		// add the skill if any
		if (_skill != null)
		{
			player.addSkill(_skill);
			if (_skill.isActive())
			{
				if (player.getReuseTimeStamp().containsKey(_skill.getReuseHashCode()))
				{
					final long delay = player.getReuseTimeStamp().get(_skill.getReuseHashCode()).getRemaining();
					if (delay > 0)
					{
						player.disableSkill(_skill, delay);
						updateTimeStamp = true;
					}
				}
			}
			player.sendSkillList();
			if (updateTimeStamp)
				player.sendPacket(new SkillCoolTime(player));
		}
	}
	
	/**
	 * Removes the augmentation bonuses from the player.
	 * @param player
	 */
	public void removeBonus(Player player)
	{
		_boni.removeBonus(player);
		
		// remove the skill if any
		if (_skill != null)
		{
			if (_skill.isPassive())
				player.removeSkill(_skill, false, true);
			else
				player.removeSkill(_skill, false, false);
			
			player.sendSkillList();
		}
	}
}