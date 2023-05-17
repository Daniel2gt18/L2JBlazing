package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.commons.math.MathUtil;

import com.l2jmega.gameserver.data.RecipeTable;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.StoreType;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.SystemMessageId;

public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private int _id;
	private int _recipeId;
	@SuppressWarnings("unused")
	private int _unknow;
	
	@Override
	protected void readImpl()
	{
		_id = readD();
		_recipeId = readD();
		_unknow = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANUFACTURE))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_id);
		if (manufacturer == null)
			return;
		
		if (activeChar.isInStoreMode())
			return;
		
		if (manufacturer.getStoreType() != StoreType.MANUFACTURE)
			return;
		
		if (activeChar.isCrafting() || manufacturer.isCrafting())
			return;
		
		if (manufacturer.isInDuel() || activeChar.isInDuel())
		{
			activeChar.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		if (MathUtil.checkIfInRange(150, activeChar, manufacturer, true))
			RecipeTable.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
	}
}