package com.l2jmega.gameserver.handler.itemhandlers.custom;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ItemList;

public class InfinityStone implements IItemHandler
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
        if (activeChar.getInventory().getInventoryItemCount(6611, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6612, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6613, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6614, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6615, 1) >= 1 || activeChar.getInventory().getInventoryItemCount(6616, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6617, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6618, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6619, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6620, -1) >= 1 || activeChar.getInventory().getInventoryItemCount(6621, -1) >= 1) {
            activeChar.sendMessage("Your weapon infinity has been destructed.");
            activeChar.getInventory().destroyItemByItemId("Destroy", 6611, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6612, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6613, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6614, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6615, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6616, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6617, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6618, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6619, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6620, 1, activeChar, null);
            activeChar.getInventory().destroyItemByItemId("Destroy", 6621, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6611, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6612, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6613, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6614, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6615, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6616, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6617, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6618, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6619, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6620, 1, activeChar, null);
            activeChar.getWarehouse().destroyItemByItemId("Destroy", 6621, 1, activeChar, null);
            playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
            activeChar.getInventory().updateDatabase();
            activeChar.sendPacket(new ItemList(activeChar, true));
        }
        else {
            activeChar.sendMessage("You do not have a infinity weapon.");
        }
    }
}
