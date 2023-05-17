package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Fisherman;
import com.l2jmega.gameserver.model.actor.instance.MercenaryManagerNpc;
import com.l2jmega.gameserver.model.actor.instance.Merchant;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.holder.IntIntHolder;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.StatusUpdate;

public final class RequestSellItem extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 12; // length of the one item
	
	private int _listId;
	private IntIntHolder[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_listId = readD();
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new IntIntHolder[count];
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			int itemId = readD();
			int cnt = readD();
			
			if (objectId < 1 || itemId < 1 || cnt < 1)
			{
				_items = null;
				return;
			}
			
			_items[i] = new IntIntHolder(objectId, cnt);
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.isProcessingTransaction() || player.isInStoreMode())
        {
            player.sendPacket(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
            return;
        }
		
		final Npc merchant = (player.getTarget() instanceof Merchant || player.getTarget() instanceof MercenaryManagerNpc) ? (Npc) player.getTarget() : null;
		if (merchant == null || !merchant.canInteract(player))
			return;
		
		if (_listId > 1000000) // lease
		{
			if (merchant.getTemplate().getNpcId() != _listId - 1000000)
				return;
		}
		
		long totalPrice = 0;
		// Proceed the sell
		for (IntIntHolder i : _items)
		{
			ItemInstance item = player.checkItemManipulation(i.getId(), i.getValue());
			if (item == null || (!item.isSellable()))
				continue;
			
			if (item.getEnchantLevel() >= Config.ENCHANT_PROTECT)
				continue;
			
			int price = item.getReferencePrice() / 2;
			totalPrice += (long) price * i.getValue();
			// Check for overflow
			if ((Integer.MAX_VALUE / i.getValue()) < price || totalPrice > Integer.MAX_VALUE || totalPrice < 0)
				return;
			
			item = player.getInventory().destroyItem("Sell", i.getId(), i.getValue(), player, merchant);
		}
		
		player.addAdena("Sell", (int) totalPrice, merchant, false);
		
		// Send the htm, if existing.
		String htmlFolder = "";
		if (merchant instanceof Fisherman)
			htmlFolder = "fisherman";
		else if (merchant instanceof Merchant)
			htmlFolder = "merchant";
		
		if (!htmlFolder.isEmpty())
		{
			final String content = HtmCache.getInstance().getHtm("data/html/" + htmlFolder + "/" + merchant.getNpcId() + "-sold.htm");
			if (content != null)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(merchant.getObjectId());
				html.setHtml(content);
				html.replace("%objectId%", merchant.getObjectId());
				player.sendPacket(html);
			}
		}
		
		// Update current load as well
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		player.sendPacket(new ItemList(player, true));
	}
}