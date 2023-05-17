package com.l2jmega.gameserver.model.itemcontainer;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance.ItemLocation;

public class PcWarehouse extends ItemContainer
{
	private final Player _owner;
	
	public PcWarehouse(Player owner)
	{
		_owner = owner;
	}
	
	@Override
	public String getName()
	{
		return "Warehouse";
	}
	
	@Override
	public Player getOwner()
	{
		return _owner;
	}
	
	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.WAREHOUSE;
	}
	
	@Override
	public boolean validateCapacity(int slots)
	{
		return _items.size() + slots <= _owner.getWareHouseLimit();
	}
}