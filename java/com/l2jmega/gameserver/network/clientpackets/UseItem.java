package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.handler.ItemHandler;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.instance.Pet;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.model.item.type.ActionType;
import com.l2jmega.gameserver.model.item.type.ArmorType;
import com.l2jmega.gameserver.model.item.type.CrystalType;
import com.l2jmega.gameserver.model.item.type.EtcItemType;
import com.l2jmega.gameserver.model.item.type.WeaponType;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.PetItemList;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.scripting.Quest;
import com.l2jmega.gameserver.scripting.QuestState;
import com.l2jmega.gameserver.templates.skills.L2SkillType;

import com.l2jmega.commons.concurrent.ThreadPool;

public final class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;
	
	public static class WeaponEquipTask implements Runnable
	{
		ItemInstance _item;
		Player _activeChar;
		
		public WeaponEquipTask(ItemInstance it, Player character)
		{
			_item = it;
			_activeChar = character;
		}
		
		@Override
		public void run()
		{
			_activeChar.useEquippableItem(_item, false);
		}
	}
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
	
		
		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMessageId.ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE);
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
			return;
		}
		
		final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if (item == null)
			return;
		
		//Condisao que somente weapon vai te delay para trocar
		if (!FloodProtectors.performAction(activeChar.getClient(), Action.USER_ITEM) && item.isWeapon())
		{
			activeChar.sendMessage("Weapon Tem um delay, Anti Cheat");
			return;
		}
		
		if (activeChar.isGM())
			activeChar.sendMessage(item.getItem().getName() + " , ID: " + item.getItem().getItemId());
		
		if (item.getItem().getType2() == Item.TYPE2_QUEST)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		if (activeChar.isAlikeDead() || activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAfraid())
			return;
		
		if (!Config.KARMA_PLAYER_CAN_TELEPORT && activeChar.getKarma() > 0)
		{
			final IntIntHolder[] sHolders = item.getItem().getSkills();
			if (sHolders != null)
			{
				for (IntIntHolder sHolder : sHolders)
				{
					final L2Skill skill = sHolder.getSkill();
					if (skill != null && (skill.getSkillType() == L2SkillType.TELEPORT || skill.getSkillType() == L2SkillType.RECALL))
						return;
				}
			}
		}
		
		if (Config.NOTALLOWEDUSELIGHT.contains(activeChar.getClassId().getId()) && !activeChar.isGM() && !activeChar.isInOlympiadMode())
		{
			if (item.getItemType() == ArmorType.LIGHT)
			{
				activeChar.sendMessage("this class can not use set light!");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		else if (Config.NOTALLOWEDUSEHEAVY.contains(activeChar.getClassId().getId()) && !activeChar.isGM() && !activeChar.isInOlympiadMode())
		{
			if (item.getItemType() == ArmorType.HEAVY)
			{
				activeChar.sendMessage("this class can not use set heavy!");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if (Config.ALT_DISABLE_BOW_CLASSES && !activeChar.isGM() && !activeChar.isInOlympiadMode())
		{
			if (item.getItem() instanceof Weapon && ((Weapon) item.getItem()).getItemType() == WeaponType.BOW)
			{
				if (Config.DISABLE_BOW_CLASSES.contains(activeChar.getClassId().getId()))
				{
					activeChar.sendMessage("You are not allowed to equip this item.");
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		if (Config.ALT_DISABLE_BOW_CLASSES_OLY && !activeChar.isGM() && activeChar.isInOlympiadMode())
		{
			if (item.getItem() instanceof Weapon && ((Weapon) item.getItem()).getItemType() == WeaponType.BOW)
			{
				if (Config.DISABLE_BOW_CLASSES.contains(activeChar.getClassId().getId()))
				{
					activeChar.sendMessage("You are not allowed to equip this item.");
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		if (activeChar.isFishing() && item.getItem().getDefaultAction() != ActionType.fishingshot)
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			if (castle.getSiege().isInProgress() && Config.LISTID_RESTRICT.contains(item.getItemId()))
			{
				activeChar.sendMessage("Aguarde o fim da siege para utilizar esse item.");
				return;
			}
		}
		
		if (Config.WYVERN_PROTECTION && activeChar.isInsideZone(ZoneId.PEACE) && Config.LISTID_RESTRICT.contains(item.getItemId()))
		{
			activeChar.sendMessage("You can not use this item here within the city.");
			return;
		}
		
		if (activeChar.isInsideZone(ZoneId.BOSS) && (item.getItemId() == 4422 || item.getItemId() == 4423 || item.getItemId() == 4424 || item.getItemId() == 8663))
		{
			activeChar.sendMessage("The use of mounts is prohibited in Zone Boss.");
			return;
		}
		
		if (!Config.WYVERN_PVPZONE && activeChar.isInsideZone(ZoneId.CUSTOM) && Config.LISTID_RESTRICT.contains(item.getItemId()))
		{
			activeChar.sendMessage("You can not use this item here within the pvpzone.");
			return;
		}
		
		if ((item.getItemId() == 8663 || item.getItemId() == 4422 || item.getItemId() == 4423 || item.getItemId() == 4424) && ((activeChar._inEventTvT && (TvT.is_teleport() || TvT.is_started())) || (activeChar._inEventCTF && (CTF.is_teleport() || CTF.is_started())) || activeChar.isArenaProtection()))
			return;
		
		if ((item.getItemId() == 1538 || item.getItemId() == 3958 || item.getItemId() == 5858 || item.getItemId() == 5859 || item.getItemId() == 9156) && ((activeChar._inEventTvT && TvT.is_started()) || (activeChar._inEventCTF && CTF.is_started()) || activeChar.isArenaProtection() || activeChar.isArenaProtection() || activeChar.isInsideZone(ZoneId.TOURNAMENT)))
		{
			activeChar.sendMessage("You can not use this item in Combat/Event mode..");
			return;
		}
		
		if (activeChar.isInArenaEvent() || activeChar.isArenaProtection())
		{
			if (Config.TOURNAMENT_LISTID_RESTRICT.contains(item.getItemId()))
			{
				activeChar.sendMessage("You can not use this item during Tournament Event.");
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if (Config.FIGHTER_LISTID_RESTRICT.contains(item.getItemId()) && activeChar.isMageClass())
		{
			activeChar.sendMessage("Only fighter can equip this item.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (Config.MAGE_LISTID_RESTRICT.contains(item.getItemId()) && !activeChar.isMageClass())
		{
			activeChar.sendMessage("Only Mage can equip this item.");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		/*
		 * The player can't use pet items if no pet is currently summoned. If a pet is summoned and player uses the item directly, it will be used by the pet.
		 */
		if (item.isPetItem())
		{
			// If no pet, cancels the use
			if (!activeChar.hasPet())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_EQUIP_PET_ITEM);
				return;
			}
			
			final Pet pet = ((Pet) activeChar.getPet());
			
			if (!pet.canWear(item.getItem()))
			{
				activeChar.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
				return;
			}
			
			if (pet.isDead())
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET);
				return;
			}
			
			if (!pet.getInventory().validateCapacity(item))
			{
				activeChar.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return;
			}
			
			if (!pet.getInventory().validateWeight(item, 1))
			{
				activeChar.sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return;
			}
			
			activeChar.transferItem("Transfer", _objectId, 1, pet.getInventory(), pet);
			
			// Equip it, removing first the previous item.
			if (item.isEquipped())
			{
				pet.getInventory().unEquipItemInSlot(item.getLocationSlot());
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(item));
			}
			else
			{
				pet.getInventory().equipPetItem(item);
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PUT_ON_S1).addItemName(item));
			}
			
			activeChar.sendPacket(new PetItemList(pet));
			pet.updateAndBroadcastStatus(1);
			return;
		}
		
		if (!item.isEquipped())
		{
			if (!item.getItem().checkCondition(activeChar, activeChar, true))
				return;
		}
		
		if (!Config.OLLY_GRADE_A && item.getItem().getCrystalType() == CrystalType.S && (activeChar.isInOlympiadMode() || activeChar.isOlympiadProtection() || OlympiadManager.getInstance().isRegistered(activeChar)))
		{
			activeChar.sendMessage("[Olympiad]: Items Grade S cannot be used in Olympiad Event");
			return;
		}
		
	      if (item.getEnchantLevel() > Config.ALT_OLY_ENCHANT_LIMIT && (activeChar.isInOlympiadMode() || activeChar.isOlympiadProtection() || OlympiadManager.getInstance().isRegistered(activeChar)))
	      {
	         activeChar.sendMessage("Equipment with enchant level above +" + Config.ALT_OLY_ENCHANT_LIMIT + " can not be used while registered in olympiad.");
	         return;
	      }
	      
			if ((activeChar.isArena1x1() && !Config.TOUR_GRADE_A_1X1) || (activeChar.isArena2x2() && !Config.TOUR_GRADE_A_2X2) || (activeChar.isArena5x5() && !Config.TOUR_GRADE_A_5X5) || (activeChar.isArena9x9() && !Config.TOUR_GRADE_A_9X9) && item.getItem().getCrystalType() == CrystalType.S && !(item.getItem().getCrystalType() == CrystalType.NONE))
			{
				activeChar.sendMessage("Tournament: Items Grade S cannot be used in Tournament Event");
				return;
			}
			
		      if (item.getEnchantLevel() > Config.ALT_TOUR_ENCHANT_LIMIT && (activeChar.isArena1x1() && !Config.TOUR_GRADE_A_1X1) || (activeChar.isArena2x2() && !Config.TOUR_GRADE_A_2X2) || (activeChar.isArena5x5() && !Config.TOUR_GRADE_A_5X5) || (activeChar.isArena9x9() && !Config.TOUR_GRADE_A_9X9))
		      {
		         activeChar.sendMessage("Equipment with enchant level above +" + Config.ALT_TOUR_ENCHANT_LIMIT + " can not be used while registered in Tournament.");
		         return;
		      }
		
		if (item.isEquipable())
		{
			if (activeChar.isCastingNow() || activeChar.isCastingSimultaneouslyNow() || (activeChar._inEventCTF && activeChar._haveFlagCTF && (item.getItem().getBodyPart() == Item.SLOT_LR_HAND || item.getItem().getBodyPart() == Item.SLOT_L_HAND || item.getItem().getBodyPart() == Item.SLOT_R_HAND)))
			{
				if (activeChar._inEventCTF && activeChar._haveFlagCTF)
					activeChar.sendMessage("This item can not be equipped when you have the flag.");
				else
					activeChar.sendPacket(SystemMessageId.CANNOT_USE_ITEM_WHILE_USING_MAGIC);
				
				return;
			}
			
			switch (item.getItem().getBodyPart())
			{
				case Item.SLOT_LR_HAND:
				case Item.SLOT_L_HAND:
				case Item.SLOT_R_HAND:
				{
					if (activeChar.isMounted())
					{
						activeChar.sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
						return;
					}
					
					// Don't allow weapon/shield equipment if a cursed weapon is equipped
					if (activeChar.isCursedWeaponEquipped())
						return;
					
					break;
				}
			}
			
			if (activeChar.isCursedWeaponEquipped() && item.getItemId() == 6408) // Don't allow to put formal wear
				return;
			
			if (activeChar.isAttackingNow()){
				ThreadPool.schedule(() -> {
					final ItemInstance itemToTest = activeChar.getInventory().getItemByObjectId(_objectId);
					if(itemToTest == null)
						return;

					activeChar.useEquippableItem(itemToTest, false);
				}, activeChar.getAttackEndTime() - System.currentTimeMillis());
			}else{
				activeChar.useEquippableItem(item, true);
			}
		}
		else
		{
			if (activeChar.isCastingNow() && !(item.isPotion() || item.isElixir()))
				return;
			
			if (activeChar.getAttackType() == WeaponType.FISHINGROD && item.getItem().getItemType() == EtcItemType.LURE)
			{
				activeChar.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
				activeChar.broadcastUserInfo();
				
				sendPacket(new ItemList(activeChar, false));
				return;
			}
			
			final IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getEtcItem());
			if (handler != null)
				handler.useItem(activeChar, item, _ctrlPressed);
			
			for (Quest quest : item.getQuestEvents())
			{
				QuestState state = activeChar.getQuestState(quest.getName());
				if (state == null || !state.isStarted())
					continue;
				
				quest.notifyItemUse(item, activeChar, activeChar.getTarget());
			}
		}
	}
}