package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.L2ManufactureItem;
import com.l2jmega.gameserver.model.L2ManufactureList;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.StoreType;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.RecipeShopMsg;

public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private int _count;
	private int[] _items;
	
	@Override
	protected void readImpl()
	{
		_count = readD();
		if (_count < 0 || _count * 8 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
			_count = 0;
		
		_items = new int[_count * 2];
		for (int x = 0; x < _count; x++)
		{
			int recipeID = readD();
			_items[x * 2 + 0] = recipeID;
			int cost = readD();
			_items[x * 2 + 1] = cost;
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (player.isInDuel())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			player.sendPacket(SystemMessageId.NO_PRIVATE_WORKSHOP_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (_count == 0)
			player.forceStandUp();
		else
		{
			L2ManufactureList createList = new L2ManufactureList();
			
			for (int x = 0; x < _count; x++)
			{
				int recipeID = _items[x * 2 + 0];
				int cost = _items[x * 2 + 1];
				createList.add(new L2ManufactureItem(recipeID, cost));
			}
			createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
			player.setCreateList(createList);
			
			player.setStoreType(StoreType.MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}
}