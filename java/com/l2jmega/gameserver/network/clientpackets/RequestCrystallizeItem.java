package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.type.CrystalType;
import com.l2jmega.gameserver.model.itemcontainer.PcInventory;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public final class RequestCrystallizeItem extends L2GameClientPacket
{
	private int _objectId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_count <= 0)
			return;
		
		if (activeChar.isInStoreMode() || activeChar.isCrystallizing())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		int skillLevel = activeChar.getSkillLevel(L2Skill.SKILL_CRYSTALLIZE);
		if (skillLevel <= 0)
		{
			activeChar.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final PcInventory inventory = activeChar.getInventory();
		if (inventory != null)
		{
			final ItemInstance item = inventory.getItemByObjectId(_objectId);
			if (item == null)
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (item.isHeroItem())
				return;
			
			if (_count > item.getCount())
				_count = activeChar.getInventory().getItemByObjectId(_objectId).getCount();
		}
		
		ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);
		if (itemToRemove == null || itemToRemove.isShadowItem() /* || itemToRemove.isTimeLimitedItem() */)
			return;
		
		if (!itemToRemove.getItem().isCrystallizable() || (itemToRemove.getItem().getCrystalCount() <= 0) || (itemToRemove.getItem().getCrystalType() == CrystalType.NONE))
		{
			_log.warning(activeChar.getName() + " tried to crystallize " + itemToRemove.getItem().getItemId());
			return;
		}
		
		// Check if the char can crystallize items and return if false;
		boolean canCrystallize = true;
		
		switch (itemToRemove.getItem().getCrystalType())
		{
			case C:
				if (skillLevel <= 1)
					canCrystallize = false;
				break;
			
			case B:
				if (skillLevel <= 2)
					canCrystallize = false;
				break;
			
			case A:
				if (skillLevel <= 3)
					canCrystallize = false;
				break;
			
			case S:
				if (skillLevel <= 4)
					canCrystallize = false;
				break;
		}
		
		if (!canCrystallize)
		{
			activeChar.sendPacket(SystemMessageId.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.setCrystallizing(true);
		
		// unequip if needed
		if (itemToRemove.isEquipped())
		{
			ItemInstance[] unequipped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance item : unequipped)
				iu.addModifiedItem(item);
			
			activeChar.sendPacket(iu);
			
			SystemMessage msg;
			if (itemToRemove.getEnchantLevel() > 0)
			{
				msg = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
				msg.addNumber(itemToRemove.getEnchantLevel());
				msg.addItemName(itemToRemove.getItemId());
			}
			else
			{
				msg = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
				msg.addItemName(itemToRemove.getItemId());
			}
			activeChar.sendPacket(msg);
		}
		
		// remove from inventory
		ItemInstance removedItem = activeChar.getInventory().destroyItem("Crystalize", _objectId, _count, activeChar, null);
		
		InventoryUpdate iu = new InventoryUpdate();
		iu.addRemovedItem(removedItem);
		activeChar.sendPacket(iu);
		
		// add crystals
		int crystalId = itemToRemove.getItem().getCrystalItemId();
		int crystalAmount = itemToRemove.getCrystalCount();
		ItemInstance createditem = activeChar.getInventory().addItem("Crystalize", crystalId, crystalAmount, activeChar, activeChar);
		
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CRYSTALLIZED).addItemName(removedItem.getItemId()));
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(createditem.getItemId()).addItemNumber(crystalAmount));
		
		activeChar.broadcastUserInfo();
		World.getInstance().removeObject(removedItem);
		activeChar.setCrystallizing(false);
	}
}