package com.l2jmega.gameserver.handler.itemhandlers.custom;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.UserInfo;

public class DeletePk implements IItemHandler
{
    @Override
    public void useItem(final Playable playable, final ItemInstance item, final boolean forceUse) {
        if (!(playable instanceof Player)) {
            return;
        }
        final Player activeChar = (Player)playable;
        if (activeChar.isAllSkillsDisabled()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("This item cannot be used on Olympiad Games.");
        }
        if (activeChar.getPkKills() == 0) {
            activeChar.sendMessage("You do not have PK's to be removed.");
        }
        else {
            activeChar.setPkKills(0);
            playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
            activeChar.sendMessage("Your PK's have been removed.");
            activeChar.sendPacket(new UserInfo(activeChar));
        }
    }
}
