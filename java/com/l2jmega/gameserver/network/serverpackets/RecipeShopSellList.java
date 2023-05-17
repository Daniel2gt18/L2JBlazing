package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.gameserver.model.L2ManufactureItem;
import com.l2jmega.gameserver.model.L2ManufactureList;
import com.l2jmega.gameserver.model.actor.instance.Player;

public class RecipeShopSellList extends L2GameServerPacket
{
	private final Player _buyer, _manufacturer;
	
	public RecipeShopSellList(Player buyer, Player manufacturer)
	{
		_buyer = buyer;
		_manufacturer = manufacturer;
	}
	
	@Override
	protected final void writeImpl()
	{
		final L2ManufactureList createList = _manufacturer.getCreateList();
		if (createList != null)
		{
			writeC(0xd9);
			writeD(_manufacturer.getObjectId());
			writeD((int) _manufacturer.getCurrentMp());// Creator's MP
			writeD(_manufacturer.getMaxMp());// Creator's MP
			writeD(_buyer.getAdena());// Buyer Adena
			writeD(createList.size());
			
			for (L2ManufactureItem item : createList.getList())
			{
				writeD(item.getRecipeId());
				writeD(0x00); // unknown
				writeD(item.getCost());
			}
		}
	}
}