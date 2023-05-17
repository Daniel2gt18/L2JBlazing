package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.events.PartyZoneTask;
import com.l2jmega.gameserver.model.MinionList;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.holder.RewardHolder;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * This class manages monsters.
 * <ul>
 * <li>L2MonsterInstance</li>
 * <li>L2RaidBossInstance</li>
 * <li>L2GrandBossInstance</li>
 * </ul>
 */
public class Monster extends Attackable
{
	private Monster _master;
	private MinionList _minionList;
	
	/**
	 * Constructor of L2MonsterInstance (use Creature and L2NpcInstance constructor).
	 * <ul>
	 * <li>Call the Creature constructor to set the _template of the L2MonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2MonsterInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @param template L2NpcTemplate to apply to the NPC
	 */
	public Monster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		// FIXME: to test to allow monsters hit others monsters
		if (attacker instanceof Monster)
			return false;
		
		return true;
	}
	
	@Override
	public boolean isAggressive()
	{
		return getTemplate().getAggroRange() > 0;
	}
	
	@Override
	public void onSpawn()
	{
		if (!isTeleporting())
		{
			if (_master != null)
			{
				setIsNoRndWalk(true);
				setIsRaidMinion(_master.isRaid());
				_master.getMinionList().onMinionSpawn(this);
			}
			// delete spawned minions before dynamic minions spawned by script
			else if (_minionList != null)
				getMinionList().deleteSpawnedMinions();
			
			startMaintenanceTask();
		}
		
		// dynamic script-based minions spawned here, after all preparations.
		super.onSpawn();
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		
		if (_minionList != null)
			getMinionList().onMasterTeleported();
	}
	
	/**
	 * Spawn minions.
	 */
	protected void startMaintenanceTask()
	{
		if (!getTemplate().getMinionData().isEmpty())
			getMinionList().spawnMinions();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_master != null)
			_master.getMinionList().onMinionDie(this, _master.getSpawn().getRespawnDelay() * 1000 / 2);
		
		if (killer != null)
		{
			final Player player = killer.getActingPlayer();
			if (player != null)
			{
				if (player.getCaptcha_ok() >= Config.EXTREME_CAPTCHA)
				{
					System.out.println("-------------------------------------------------------------------------------");
					System.out.println("Limite Captcha: " + player.getName() + " execute Captcha " + Config.EXTREME_CAPTCHA + " X");
					System.out.println("-------------------------------------------------------------------------------");
					for (Player p : World.getInstance().getPlayers())
					{
						String hwidz = p.getHWID();
						if (p.isOnline())
						{
							if (hwidz.equals(player.getHWID()))
								p.logout();
						}
					}
				}
				
				if (getNpcId() == Config.monsterId && PartyZoneTask.is_started())
				{
					PvpFlagTaskManager.getInstance().add(player, 60000);
				}
				
				if (getNpcId() == Config.monsterId && !PartyZoneTask.is_started())
				{
					for (RewardHolder reward : Config.PARTY_ZONE_REWARDS)
					{
						if (player.getParty() != null)
						{
							for (Player member : player.getParty().getPartyMembers())
							{
								if (member.isInsideRadius(getX(), getY(), getZ(), Config.RADIUS_TO_PARTYZONE, false, false))
								{
									if (Rnd.get(100) <= reward.getRewardChance())
									{
										if (member.isVip())
										{
											member.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()) * Config.RATE_DROP_VIP, member, true);
										}
										else
										{
											member.addItem("item", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()), member, true);
										}
									}
								}
								
							}
						}
						else if (Rnd.get(100) <= reward.getRewardChance())
						{
							if (player.isVip())
							{
								player.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()) * Config.RATE_DROP_VIP, player, true);
							}
							else
							{
								player.addItem("item", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()), player, true);
							}
						}
					}
				}else if (getNpcId() == Config.monsterId && PartyZoneTask.is_started())
				{
					for (RewardHolder reward : Config.PARTY_ZONE_REWARDS2)
					{
						if (player.getParty() != null)
						{
							for (Player member : player.getParty().getPartyMembers())
							{
								if (member.isInsideRadius(getX(), getY(), getZ(), Config.RADIUS_TO_PARTYZONE, false, false))
								{
									if (Rnd.get(100) <= reward.getRewardChance())
									{
										if (member.isVip())
										{
											member.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()) * Config.RATE_DROP_VIP, member, true);
										}
										else
										{
											member.addItem("item", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()), member, true);
										}
									}
								}
							}
						}
						else if (Rnd.get(100) <= reward.getRewardChance())
						{
							if (player.isVip())
							{
								player.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()) * Config.RATE_DROP_VIP, player, true);
							}
							else
							{
								player.addItem("item", reward.getRewardId(), Rnd.get(reward.getMin(), reward.getMax()), player, true);
							}
						}
					}
				}
				
				if (Config.MISSION_LIST_MONSTER.contains(Integer.valueOf(getTemplate().getNpcId())) && Config.ACTIVE_MISSION)
					if (player.getParty() == null) {
						if (!player.check_obj_mission(player.getObjectId()))
							player.updateMission(); 
						if (!player.isMobCompleted() && player.getMonsterKills() < Config.MISSION_MOB_CONT)
							player.setMonsterKills(player.getMonsterKills() + 1); 
					}
				
				if (getNpcId() == Config.monsterId && Config.ACTIVE_MISSION)
					if (player.getParty() != null) {
						for (Player member : player.getParty().getPartyMembers()) {
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false)) {
								if (!member.check_obj_mission(member.getObjectId()))
									member.updateMission(); 
								if ((!member.isPartyMobCompleted() && member.getPartyMonsterKills() < Config.MISSION_PARTY_MOB_CONT) || member.getHWID().equals(player.getHWID()))
									member.setPartyMonsterKills(member.getPartyMonsterKills() + 1); 
								continue;
							} 
							CreatureSay cs = new CreatureSay(member.getObjectId(), 2, "[Party Event]", "Voce esta muito longe de sua Party..");
							member.sendPacket(cs);
						} 
					} else if (player.getParty() == null) {
						if (!player.check_obj_mission(player.getObjectId()))
							player.updateMission(); 
						if (!player.isPartyMobCompleted() && player.getPartyMonsterKills() < Config.MISSION_PARTY_MOB_CONT)
							player.setPartyMonsterKills(player.getPartyMonsterKills() + 1); 
					}
				
			}
		}
		
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (_minionList != null)
			getMinionList().onMasterDie(true);
		else if (_master != null)
			_master.getMinionList().onMinionDie(this, 0);
		
		super.deleteMe();
	}
	
	@Override
	public Monster getLeader()
	{
		return _master;
	}
	
	public void setLeader(Monster leader)
	{
		_master = leader;
	}
	
	public boolean hasMinions()
	{
		return _minionList != null;
	}
	
	public MinionList getMinionList()
	{
		if (_minionList == null)
			_minionList = new MinionList(this);
		
		return _minionList;
	}
}