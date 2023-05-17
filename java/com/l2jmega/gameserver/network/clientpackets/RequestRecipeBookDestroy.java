package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.StoreType;
import com.l2jmega.gameserver.model.item.RecipeList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.RecipeBookItemList;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public final class RequestRecipeBookDestroy extends L2GameClientPacket
{
	private int _recipeID;
	
	@Override
	protected void readImpl()
	{
		_recipeID = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (activeChar.getStoreType() == StoreType.MANUFACTURE)
		{
			activeChar.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			return;
		}
		
		final RecipeList rp = RecipeTable.getInstance().getRecipeList(_recipeID);
		if (rp == null)
			return;
		
		activeChar.unregisterRecipeList(_recipeID);
		activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_DELETED).addItemName(_recipeID));
		
		RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), activeChar.getMaxMp());
		if (rp.isDwarvenRecipe())
			response.addRecipes(activeChar.getDwarvenRecipeBook());
		else
			response.addRecipes(activeChar.getCommonRecipeBook());
		
		activeChar.sendPacket(response);
	}
}