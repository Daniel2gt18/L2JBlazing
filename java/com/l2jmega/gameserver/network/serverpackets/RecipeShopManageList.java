package com.l2jmega.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.Iterator;

import com.l2jmega.gameserver.model.L2ManufactureItem;
import com.l2jmega.gameserver.model.L2ManufactureList;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.RecipeList;

/**
 * dd d(dd) d(ddd)
 */
public class RecipeShopManageList extends L2GameServerPacket
{
	private final Player _seller;
	private final boolean _isDwarven;
	private Collection<RecipeList> _recipes;
	
	public RecipeShopManageList(Player seller, boolean isDwarven)
	{
		_seller = seller;
		_isDwarven = isDwarven;
		
		if (_isDwarven && seller.hasDwarvenCraft())
			_recipes = seller.getDwarvenRecipeBook();
		else
			_recipes = seller.getCommonRecipeBook();
		
		// clean previous recipes
		if (seller.getCreateList() != null)
		{
			final Iterator<L2ManufactureItem> it = seller.getCreateList().getList().iterator();
			while (it.hasNext())
			{
				L2ManufactureItem item = it.next();
				if (item.isDwarven() != _isDwarven || !seller.hasRecipeList(item.getRecipeId()))
					it.remove();
			}
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xd8);
		writeD(_seller.getObjectId());
		writeD(_seller.getAdena());
		writeD(_isDwarven ? 0x00 : 0x01);
		
		if (_recipes == null)
			writeD(0);
		else
		{
			writeD(_recipes.size());// number of items in recipe book
			
			int i = 0;
			for (RecipeList recipe : _recipes)
			{
				writeD(recipe.getId());
				writeD(++i);
			}
		}
		
		if (_seller.getCreateList() == null)
			writeD(0);
		else
		{
			L2ManufactureList list = _seller.getCreateList();
			writeD(list.size());
			
			for (L2ManufactureItem item : list.getList())
			{
				writeD(item.getRecipeId());
				writeD(0x00);
				writeD(item.getCost());
			}
		}
	}
}