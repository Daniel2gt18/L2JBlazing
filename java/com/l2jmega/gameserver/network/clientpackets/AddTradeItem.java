package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.tradelist.TradeItem;
import com.l2jmega.gameserver.model.tradelist.TradeList;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.TradeItemUpdate;
import com.l2jmega.gameserver.network.serverpackets.TradeOtherAdd;
import com.l2jmega.gameserver.network.serverpackets.TradeOwnAdd;

public final class AddTradeItem extends L2GameClientPacket
{
	private int _tradeId;
	private int _objectId;
	private int _count;
	
	public AddTradeItem()
	{
	}
	
	@Override
	protected void readImpl()
	{
		_tradeId = readD();
		_objectId = readD();
		_count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		final TradeList trade = player.getActiveTradeList();
		if (trade == null)
		{
			_log.warning("Character: " + player.getName() + " requested item:" + _objectId + " add without active tradelist:" + _tradeId);
			return;
		}
		
		final Player partner = trade.getPartner();
		if (partner == null || World.getInstance().getPlayer(partner.getObjectId()) == null || partner.getActiveTradeList() == null)
		{
			// Trade partner not found, cancel trade
			if (partner != null)
				_log.warning(player.getName() + " requested invalid trade object: " + _objectId);
			
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			player.cancelActiveTrade();
			return;
		}
		
		if (trade.isConfirmed() || partner.getActiveTradeList().isConfirmed())
		{
			player.sendPacket(SystemMessageId.CANNOT_ADJUST_ITEMS_AFTER_TRADE_CONFIRMED);
			return;
		}
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			player.cancelActiveTrade();
			return;
		}
		
		if (player.validateItemManipulation(_objectId) == null)
		{
			player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
			return;
		}
		
		final TradeItem item = trade.addItem(_objectId, _count);
		if (item != null)
		{
			player.sendPacket(new TradeOwnAdd(item));
			player.sendPacket(new TradeItemUpdate(trade, player));
			trade.getPartner().sendPacket(new TradeOtherAdd(item));
		}
	}
}