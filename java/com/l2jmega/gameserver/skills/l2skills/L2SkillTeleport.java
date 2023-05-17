package com.l2jmega.gameserver.skills.l2skills;

import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.MapRegionTable;
import com.l2jmega.gameserver.data.MapRegionTable.TeleportType;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.ShotType;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class L2SkillTeleport extends L2Skill
{
	private final String _recallType;
	private final Location _loc;
	
	public L2SkillTeleport(StatsSet set)
	{
		super(set);
		
		_recallType = set.getString("recallType", "");
		String coords = set.getString("teleCoords", null);
		if (coords != null)
		{
			String[] valuesSplit = coords.split(",");
			_loc = new Location(Integer.parseInt(valuesSplit[0]), Integer.parseInt(valuesSplit[1]), Integer.parseInt(valuesSplit[2]));
		}
		else
			_loc = null;
	}
	
	@Override
	public void useSkill(Creature activeChar, WorldObject[] targets)
	{
		if (activeChar instanceof Player)
		{
			// Check invalid states.
			if (activeChar.isAfraid() || ((Player) activeChar).isInOlympiadMode())
				return;
		}
		
		boolean bsps = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			
			if (target instanceof Player)
			{
				Player targetChar = (Player) target;
				
				// Check invalid states.
				if (targetChar.isFestivalParticipant() || targetChar.isInJail() || targetChar.isInDuel())
					continue;
				
				if ((TvT.is_started() && targetChar._inEventTvT) || (CTF.is_started() && targetChar._inEventCTF) || targetChar.isArenaProtection())
				{
					targetChar.sendMessage("You can't use escape skill in Event.");
					continue;
				}
				
				if (targetChar != activeChar)
				{
					if (targetChar.isInOlympiadMode())
						continue;
				}
			}
			
			Location loc = null;
			if (getSkillType() == L2SkillType.TELEPORT)
			{
				if (_loc != null)
				{
					if (!(target instanceof Player) || !target.isFlying())
						loc = _loc;
				}
			}
			else
			{
				if (_recallType.equalsIgnoreCase("Castle"))
					loc = MapRegionTable.getInstance().getLocationToTeleport(target, TeleportType.CASTLE);
				else if (_recallType.equalsIgnoreCase("ClanHall"))
					loc = MapRegionTable.getInstance().getLocationToTeleport(target, TeleportType.CLAN_HALL);
				else
					loc = MapRegionTable.getInstance().getLocationToTeleport(target, TeleportType.TOWN);
			}
			
			if (loc != null)
			{
				if (target instanceof Player)
					((Player) target).setIsIn7sDungeon(false);
				
				target.teleToLocation(loc, 20);
			}
		}
		
		activeChar.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
	}
}