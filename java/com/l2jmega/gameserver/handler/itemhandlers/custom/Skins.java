package com.l2jmega.gameserver.handler.itemhandlers.custom;

import com.l2jmega.gameserver.data.xml.DressMeData;
import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.DressMe;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

public class Skins implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final Player player = (Player) playable;
		
		if (!(playable instanceof Player))
			return;
		
		final DressMe dress = DressMeData.getInstance().getItemId(item.getItemId());
		if (dress == null)
			return;
		
		player.setDress(dress);
		player.broadcastPacket(new MagicSkillUse(player, player, 1036, 1, 4000, 0));
		player.broadcastUserInfo();
	}
}
