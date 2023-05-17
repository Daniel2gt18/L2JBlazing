package com.l2jmega.gameserver.network.serverpackets;

import java.util.Collection;

import com.l2jmega.gameserver.model.item.RecipeList;

/**
 * format d d(dd)
 */
public class RecipeBookItemList extends L2GameServerPacket
{
	private Collection<RecipeList> _recipes;
	private final boolean _isDwarvenCraft;
	private final int _maxMp;
	
	public RecipeBookItemList(boolean isDwarvenCraft, int maxMp)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_maxMp = maxMp;
	}
	
	public void addRecipes(Collection<RecipeList> recipeBook)
	{
		_recipes = recipeBook;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xD6);
		
		writeD(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
		writeD(_maxMp);
		
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
	}
}