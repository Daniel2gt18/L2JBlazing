package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.RecipeList;

/**
 * format dddd
 */
public class RecipeItemMakeInfo extends L2GameServerPacket
{
	private final int _id;
	private final Player _activeChar;
	private final int _status;
	
	public RecipeItemMakeInfo(int id, Player player, int status)
	{
		_id = id;
		_activeChar = player;
		_status = status;
	}
	
	public RecipeItemMakeInfo(int id, Player player)
	{
		_id = id;
		_activeChar = player;
		_status = -1;
	}
	
	@Override
	protected final void writeImpl()
	{
		RecipeList recipe = RecipeTable.getInstance().getRecipeList(_id);
		if (recipe != null)
		{
			writeC(0xD7);
			
			writeD(_id);
			writeD(recipe.isDwarvenRecipe() ? 0 : 1);
			writeD((int) _activeChar.getCurrentMp());
			writeD(_activeChar.getMaxMp());
			writeD(_status);
		}
	}
}