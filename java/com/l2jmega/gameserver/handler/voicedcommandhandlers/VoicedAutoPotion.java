package com.l2jmega.gameserver.handler.voicedcommandhandlers;



import com.l2jmega.Config;
import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.handler.ItemHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author root
 * @date: 20/01/2021 at 23:15
 * Description : autocp / automp / autohp item use.
 */
public class VoicedAutoPotion implements IVoicedCommandHandler {
	
	static final Logger _log = Logger.getLogger(Config.class.getName());

    //*******Config Section*****************/
    //    *********************** Potion ItemID
    private static final int ID_HEAL_CP = 5592;
    private static final int ID_HEAL_MP = 728;
    private static final int ID_HEAL_HP = 1539;
    //*********************** USE FULL
    //Enable/Disable voicecoomand
    private static final boolean ACP_ON = true;
    //    Min lvl for use ACP
    private static final int ACP_MIN_LVL = 0;
    private static final int ACP_HP_LVL = 70;
    private static final int ACP_CP_LVL = 70;
    private static final int ACP_MP_LVL = 70;
    private static final int ACP_MILI_SECONDS_FOR_LOOP = 1000;
    // Automatic regen : Default ACP/MP/HP
    private static final boolean ACP_CP = true;
    private static final boolean ACP_MP = true;
    private static final boolean ACP_HP = true;
    static final HashMap<String, Thread> userAcpMap = new HashMap<>();

    private static String[] _voicedCommands = {
                    "acpon",
                    "acpoff"
    };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String params) {
        if (activeChar == null) {
            return false;
        }

        if (command.equals("acpon")) {
            if (!ACP_ON) {
                activeChar.sendMessage("The function is disabled on the server!");
                return false;
            }
			if (userAcpMap.containsKey(activeChar.toString())) {
			    activeChar.sendMessage("Already included!");
			} else {
			    activeChar.sendMessage("Auto Potion Activated!");
			    Thread t = new Thread(new AcpHealer(activeChar));
			    userAcpMap.put(activeChar.toString(), t);
			    t.start();
			    return true;
			}
        } else if (command.equals("acpoff")) {
            if (!userAcpMap.containsKey(activeChar.toString())) {
                activeChar.sendMessage("Was not included");
            } else {
                userAcpMap.remove(activeChar.toString()) //here we get thread and remove it from map
                        .interrupt(); //and interrupt it
                activeChar.sendMessage("Auto Potion Disabled!");
            }
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }

    private class AcpHealer implements Runnable {

        Player activeChar;

        public AcpHealer(Player activeChar) {
            this.activeChar = activeChar;
        }

        @Override
        public void run() {
            try {
                while (true) {
//                  Checking the level
                    if (activeChar.getLevel() >= ACP_MIN_LVL) {
//                            Checking if we have at least one can of something
                            ItemInstance cpBottle = activeChar.getInventory().getItemByItemId(ID_HEAL_CP);
                            ItemInstance hpBottle = activeChar.getInventory().getItemByItemId(ID_HEAL_HP);
                            ItemInstance mpBottle = activeChar.getInventory().getItemByItemId(ID_HEAL_MP);

                            if (hpBottle != null && hpBottle.getCount() > 0) {
//                               Checking our health
                                if ((activeChar.getStatus().getCurrentHp() / activeChar.getMaxHp()) * 100 < ACP_HP_LVL && ACP_HP) {
                                    IItemHandler handlerHP = ItemHandler.getInstance().getItemHandler(hpBottle.getEtcItem());
                                    if (handlerHP != null) {
                                        handlerHP.useItem(activeChar, hpBottle, true);
                                        //activeChar.sendMessage("ACP: Restored HP");
                                    }
                                }
//                               Checking our CP level
                                if (cpBottle != null && cpBottle.getCount() > 0) {
                                    if ((activeChar.getStatus().getCurrentCp() / activeChar.getMaxCp()) * 100 < ACP_CP_LVL && ACP_CP) {
                                        IItemHandler handlerCP = ItemHandler.getInstance().getItemHandler(cpBottle.getEtcItem());
                                        if (handlerCP != null) {
                                            handlerCP.useItem(activeChar, cpBottle, true);
                                          //  activeChar.sendMessage("ACP: Restored CP");
                                        }
                                    }
                                }
//                              Checking our MP level
                                if (mpBottle != null && mpBottle.getCount() > 0) {
                                    if ((activeChar.getStatus().getCurrentMp() / activeChar.getMaxMp()) * 100 < ACP_MP_LVL && ACP_MP) {
                                        IItemHandler handlerMP = ItemHandler.getInstance().getItemHandler(mpBottle.getEtcItem());
                                        if (handlerMP != null) {
                                            handlerMP.useItem(activeChar, mpBottle, true);
                                           // activeChar.sendMessage("ACP: Restored MP");
                                        }
                                    }
                                }
                            } else {
                                activeChar.sendMessage("[Auto Potion] Incorrect item count");
                                return;
                            }
                    } else {
                        activeChar.sendMessage("Available only " + ACP_MIN_LVL + " level!");
                        return;
                    }
                    Thread.sleep(ACP_MILI_SECONDS_FOR_LOOP);
                }
            } catch (InterruptedException e) {
                //nothing
            } catch (Exception e) {
            	_log.warning(e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                userAcpMap.remove(activeChar.toString());
            }
        }
    }
}