package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.Player.StoreType;
import com.l2jmega.gameserver.network.serverpackets.RecipeShopItemInfo;

public final class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _playerObjectId, _recipeId;
	
	@Override
	protected void readImpl()
	{
		_playerObjectId = readD();
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final Player shop = World.getInstance().getPlayer(_playerObjectId);
		if (shop == null || shop.getStoreType() != StoreType.MANUFACTURE)
			return;
		
		player.sendPacket(new RecipeShopItemInfo(shop, _recipeId));
	}
}