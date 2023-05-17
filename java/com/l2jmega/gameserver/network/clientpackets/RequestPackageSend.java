package com.l2jmega.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.itemcontainer.ItemContainer;
import com.l2jmega.gameserver.model.itemcontainer.PcFreight;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.StatusUpdate;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.util.WharehouseLog;

public final class RequestPackageSend extends L2GameClientPacket
{
	private List<IntIntHolder> _items;
	private int _objectID;
	
	@Override
	protected void readImpl()
	{
		_objectID = readD();
		
		int count = readD();
		if (count < 0 || count > Config.MAX_ITEM_IN_PACKET)
			return;
		
		_items = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int id = readD();
			int cnt = readD();
			
			_items.add(new IntIntHolder(id, cnt));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null || _items.isEmpty() || !Config.ALLOW_FREIGHT)
			return;
		
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		// player attempts to send freight to the different account
		if (!player.getAccountChars().containsKey(_objectID))
			return;
		
		final PcFreight freight = player.getDepositedFreight(_objectID);
		player.setActiveWarehouse(freight);
		
		final ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
			return;
		
		final Npc manager = player.getCurrentFolkNPC();
		if ((manager == null || !player.isInsideRadius(manager, Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
			return;
		
		if (warehouse instanceof PcFreight && !player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.KARMA_PLAYER_CAN_USE_WH && player.getKarma() > 0)
			return;
		
		// Freight price from config or normal price per item slot (30)
		int fee = _items.size() * Config.ALT_GAME_FREIGHT_PRICE;
		int currentAdena = player.getAdena();
		int slots = 0;
		
		for (IntIntHolder i : _items)
		{
			int count = i.getValue();
			
			// Check validity of requested item
			ItemInstance item = player.checkItemManipulation(i.getId(), count);
			if (item == null)
			{
				i.setId(0);
				i.setValue(0);
				
				_log.warning("Error depositing a warehouse object for char " + player.getName() + " (validity check)");
				continue;
			}
			
			if (!item.isTradable() || item.isQuestItem())
				return;
			
			// Calculate needed adena and slots
			if (item.getItemId() == 57)
				currentAdena -= count;
			
			if (!item.isStackable())
				slots += count;
			else if (warehouse.getItemByItemId(item.getItemId()) == null)
				slots++;
		}
		
		// Item Max Limit Check
		if (!warehouse.validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
			return;
		}
		
		// Check if enough adena and charge the fee
		if (currentAdena < fee || !player.reduceAdena("Warehouse", fee, player.getCurrentFolkNPC(), false))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed to the transfer
		InventoryUpdate playerIU = new InventoryUpdate();
		for (IntIntHolder i : _items)
		{
			int objectId = i.getId();
			int count = i.getValue();
			
			// check for an invalid item
			if (objectId == 0 && count == 0)
				continue;
			
			ItemInstance oldItem = player.getInventory().getItemByObjectId(objectId);
			if (oldItem == null)
			{
				_log.warning("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
				continue;
			}
			
			if (oldItem.isHeroItem())
				continue;
			
			ItemInstance newItem = player.getInventory().transferItem("Warehouse", objectId, count, warehouse, player, player.getCurrentFolkNPC());
			if (newItem == null)
			{
				_log.warning("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
				continue;
			}
			
			WharehouseLog.Log(player.getName(), warehouse.getName(), oldItem, oldItem.getEnchantLevel(), oldItem.getCount(), objectId);			
		
			if (oldItem.getCount() > 0 && oldItem != newItem)
				playerIU.addModifiedItem(oldItem);
			else
				playerIU.addRemovedItem(oldItem);
		}
		
		// Send updated item list to the player
		player.sendPacket(playerIU);
		
		// Update current load status on player
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}
}