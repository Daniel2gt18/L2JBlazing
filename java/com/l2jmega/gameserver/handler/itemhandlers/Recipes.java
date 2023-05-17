package com.l2jmega.gameserver.handler.itemhandlers;

import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.StoreType;
import com.l2jmega.gameserver.model.item.RecipeList;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

public class Recipes implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player activeChar = (Player) playable;
		
		if (activeChar.isCrafting())
		{
			activeChar.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			return;
		}
		
		final RecipeList rp = RecipeTable.getInstance().getRecipeByItemId(item.getItemId());
		if (rp == null)
			return;
		
		if (activeChar.hasRecipeList(rp.getId()))
		{
			activeChar.sendPacket(SystemMessageId.RECIPE_ALREADY_REGISTERED);
			return;
		}
		
		if (rp.isDwarvenRecipe())
		{
			if (activeChar.hasDwarvenCraft())
			{
				if (activeChar.getStoreType() == StoreType.MANUFACTURE)
					activeChar.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
				else if (rp.getLevel() > activeChar.getDwarvenCraft())
					activeChar.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
				else if (activeChar.getDwarvenRecipeBook().size() >= activeChar.getDwarfRecipeLimit())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(activeChar.getDwarfRecipeLimit()));
				else
				{
					activeChar.registerDwarvenRecipeList(rp);
					activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				}
			}
			else
				activeChar.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
		}
		else
		{
			if (activeChar.hasCommonCraft())
			{
				if (activeChar.getStoreType() == StoreType.MANUFACTURE)
					activeChar.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
				else if (rp.getLevel() > activeChar.getCommonCraft())
					activeChar.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
				else if (activeChar.getCommonRecipeBook().size() >= activeChar.getCommonRecipeLimit())
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(activeChar.getCommonRecipeLimit()));
				else
				{
					activeChar.registerCommonRecipeList(rp);
					activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false);
					activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				}
			}
			else
				activeChar.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
		}
	}
}