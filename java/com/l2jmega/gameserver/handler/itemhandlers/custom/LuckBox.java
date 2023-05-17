package com.l2jmega.gameserver.handler.itemhandlers.custom;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;

public class LuckBox
implements IItemHandler {
    @Override
    public void useItem(Playable playable, ItemInstance item, boolean forceUse) {
        if (!(playable instanceof Player)) {
            return;
        }
        
        
        Player activeChar = (Player)playable;
        
		if (activeChar.isOlympiadProtection())
		{
			activeChar.sendMessage("You can not do that.");
			return;
		}
         
	       switch (Rnd.get(0,11)) {
	            case 0: if(Rnd.get(100) < Config.CHANCE_ITEM_1){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_1, Config.ITEM_QUANT_1, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 1: if(Rnd.get(100) < Config.CHANCE_ITEM_2){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_2, Config.ITEM_QUANT_2, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 2: if(Rnd.get(100) < Config.CHANCE_ITEM_3){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_3, Config.ITEM_QUANT_3, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 3: if(Rnd.get(100) < Config.CHANCE_ITEM_4){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_4, Config.ITEM_QUANT_4, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 4: if(Rnd.get(100) < Config.CHANCE_ITEM_5){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_5, Config.ITEM_QUANT_5, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 5: if(Rnd.get(100) < Config.CHANCE_ITEM_6){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_6, Config.ITEM_QUANT_6, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 6: if(Rnd.get(100) < Config.CHANCE_ITEM_7){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_7, Config.ITEM_QUANT_7, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 7: if(Rnd.get(100) < Config.CHANCE_ITEM_8){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_8, Config.ITEM_QUANT_8, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }     
	            case 8: if(Rnd.get(100) < Config.CHANCE_ITEM_9){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_9, Config.ITEM_QUANT_9, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 9: if(Rnd.get(100) < Config.CHANCE_ITEM_10){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_10, Config.ITEM_QUANT_10, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 10: if(Rnd.get(100) < Config.CHANCE_ITEM_11){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.addItem("item", Config.ITEM_11, Config.ITEM_QUANT_11, activeChar, true);
	                MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
	                activeChar.broadcastPacket(MSU);
	                break;
	            }
	            case 11: if(Rnd.get(100) < 70){
	            	playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
	                activeChar.sendMessage("Ohh noo! Your lucky box is empty.");
	                break;
	            }
        }
    }
}