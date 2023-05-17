package com.l2jmega.gameserver.handler.skillhandlers;

import com.l2jmega.commons.math.MathUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.geoengine.GeoEngine;
import com.l2jmega.gameserver.handler.ISkillHandler;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.model.item.type.WeaponType;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.L2ZoneType;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.model.zone.type.L2FishingZone;
import com.l2jmega.gameserver.model.zone.type.L2WaterZone;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

public class Fishing implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.FISHING
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		Player player = (Player) activeChar;
		
		if (player.isFishing())
		{
			if (player.getFishCombat() != null)
				player.getFishCombat().doDie(false);
			else
				player.endFishing(false);
			// Cancels fishing
			player.sendPacket(SystemMessageId.FISHING_ATTEMPT_CANCELLED);
			return;
		}
		
		// Fishing poles arent installed
		Weapon weaponItem = player.getActiveWeaponItem();
		if ((weaponItem == null || weaponItem.getItemType() != WeaponType.FISHINGROD))
		{
			player.sendPacket(SystemMessageId.FISHING_POLE_NOT_EQUIPPED);
			return;
		}
		
		// Baits arent equipped
		ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (lure == null)
		{
			player.sendPacket(SystemMessageId.BAIT_ON_HOOK_BEFORE_FISHING);
			return;
		}
		
		player.setLure(lure);
		ItemInstance lure2 = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		
		// Not enough baits
		if (lure2 == null || lure2.getCount() < 1)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_BAIT);
			return;
		}
		
		// You can't fish while you are on boat
		if (player.isInBoat())
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_ON_BOAT);
			return;
		}
		
		if (player.isCrafting() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_WHILE_USING_RECIPE_BOOK);
			return;
		}
		
		// You can't fish in water
		if (player.isInsideZone(ZoneId.WATER))
		{
			player.sendPacket(SystemMessageId.CANNOT_FISH_UNDER_WATER);
			return;
		}
		
		/*
		 * If fishing is enabled, decide where will the hook be cast...
		 */
		int rnd = Rnd.get(150) + 50;
		double angle = MathUtil.convertHeadingToDegree(player.getHeading());
		double radian = Math.toRadians(angle);
		double sin = Math.sin(radian);
		double cos = Math.cos(radian);
		int x = player.getX() + (int) (cos * rnd);
		int y = player.getY() + (int) (sin * rnd);
		int z = player.getZ() + 50;
		/*
		 * ...and if the spot is in a fishing zone. If it is, it will position the hook on the water surface. If not, you have to be GM to proceed past here... in that case, the hook will be positioned using the old Z lookup method.
		 */
		L2FishingZone aimingTo = null;
		L2WaterZone water = null;
		boolean canFish = false;
		for (L2ZoneType zone : ZoneManager.getInstance().getZones(x, y))
		{
			if (zone instanceof L2FishingZone)
			{
				aimingTo = (L2FishingZone) zone;
				continue;
			}
			
			if (zone instanceof L2WaterZone)
				water = (L2WaterZone) zone;
		}
		
		if (aimingTo != null)
		{
			// geodata enabled, checking if we can see end of the pole
			if (GeoEngine.getInstance().canSeeTarget(player, new Location(x, y, z)))
			{
				// finding z level for hook
				if (water != null)
				{
					// water zone exist
					if (GeoEngine.getInstance().getHeight(x, y, z) < water.getWaterZ())
					{
						// water Z is higher than geo Z
						z = water.getWaterZ() + 10;
						canFish = true;
					}
				}
				else
				{
					// no water zone, using fishing zone
					if (GeoEngine.getInstance().getHeight(x, y, z) < aimingTo.getWaterZ())
					{
						// fishing Z is higher than geo Z
						z = aimingTo.getWaterZ() + 10;
						canFish = true;
					}
				}
			}
		}
		
		if (!canFish)
		{
			// You can't fish here
			player.sendPacket(SystemMessageId.CANNOT_FISH_HERE);
			return;
		}
		
		// Has enough bait, consume 1 and update inventory. Start fishing follows.
		lure2 = player.getInventory().destroyItem("Consume", player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1, player, null);
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(lure2);
		player.sendPacket(iu);
		
		// If everything else checks out, actually cast the hook and start fishing... :P
		player.startFishing(new Location(x, y, z));
	}
	
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}