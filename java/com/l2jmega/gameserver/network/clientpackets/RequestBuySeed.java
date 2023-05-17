package com.l2jmega.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.CastleManorManager;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.ManorManagerNpc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.manor.SeedProduction;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public class RequestBuySeed extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 8;
	
	private int _manorId;
	private List<IntIntHolder> _items;
	
	@Override
	protected void readImpl()
	{
		_manorId = readD();
		
		final int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			final int itemId = readD();
			final int cnt = readD();
			
			if (cnt < 1 || itemId < 1)
			{
				_items = null;
				return;
			}
			
			_items.add(new IntIntHolder(itemId, cnt));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANOR))
			return;
		
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (_items == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		if (manor.isUnderMaintenance())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Castle castle = CastleManager.getInstance().getCastleById(_manorId);
		if (castle == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Npc manager = player.getCurrentFolkNPC();
		if (!(manager instanceof ManorManagerNpc) || !manager.canInteract(player) || manager.getCastle() != castle)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		long totalPrice = 0;
		int slots = 0;
		long weight = 0;
		
		final Map<Integer, SeedProduction> _productInfo = new HashMap<>();
		
		for (IntIntHolder ih : _items)
		{
			final SeedProduction sp = manor.getSeedProduct(_manorId, ih.getId(), false);
			if (sp == null || sp.getPrice() <= 0 || sp.getAmount() < ih.getValue() || ((Integer.MAX_VALUE / ih.getValue()) < sp.getPrice()))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// Calculate price
			totalPrice += (sp.getPrice() * ih.getValue());
			// Check for overflow
			if (totalPrice > Integer.MAX_VALUE || totalPrice < 0)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Item template = ItemTable.getInstance().getTemplate(ih.getId());
			weight += (long)ih.getValue() * template.getWeight();
	
			// Calculate slots
			if (!template.isStackable())
				slots += ih.getValue();
			else if (player.getInventory().getItemByItemId(ih.getId()) == null)
				slots++;
			
			_productInfo.put(ih.getId(), sp);
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SLOTS_FULL));
			return;
		}
		
		if (weight > Integer.MAX_VALUE || weight < 0 || !player.getInventory().validateWeight((int)weight))
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (player.getAdena() < totalPrice)
		{
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed the purchase
		for (IntIntHolder i : _items)
		{
			final SeedProduction sp = _productInfo.get(i.getId());
			final int price = sp.getPrice() * i.getValue();
			
			// Take Adena and decrease seed amount
			if (!sp.decreaseAmount(i.getValue()) || !player.reduceAdena("Buy", price, player, false))
			{
				// failed buy, reduce total price
				totalPrice -= price;
				continue;
			}
			
			// Add item to player's inventory
			player.addItem("Buy", i.getId(), i.getValue(), manager, true);
		}
		
		// Adding to treasury for Manor Castle
		if (totalPrice > 0)
		{
			castle.addToTreasuryNoTax(totalPrice);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED_ADENA).addItemNumber((int)totalPrice));
		}
	}
}