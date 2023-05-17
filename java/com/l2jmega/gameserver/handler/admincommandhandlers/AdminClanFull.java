/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.gameserver.handler.admincommandhandlers;

import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author Bluur
 *
 */
public class AdminClanFull implements IAdminCommandHandler
{
    private static final String[] commands = {"admin_clanfull"};

    private static final int reputation = 30000000;
    private static final byte level = 8;
      
    @Override
    public boolean useAdminCommand(String command, Player activeChar)
    {	
    	WorldObject target = activeChar.getTarget();
        Player player = null;
        
        if (target != null && target instanceof Player)
            player = (Player) target;
        else
            return false;
            
        if (player.isClanLeader())
        {
             player.getClan().changeLevel(level);    
             player.getClan().addReputationScore(reputation);
           
             for (int i = 370; i <= 391; i++){
                 player.getClan().addNewSkill(SkillTable.getInstance().getInfo(i, SkillTable.getInstance().getMaxLevel(i)));            
             }
         
             player.getClan().updateClanInDB();
             player.sendSkillList();
             player.sendPacket(new ExShowScreenMessage("Seu Clan esta Full! Aproveite!", 8000));        
             activeChar.sendMessage("[Clan Full]: Foi adicionado com sucesso!");
        }
        else
            activeChar.sendMessage("O target precisa ser Lider de clan.");
          
        return true;
    }

    @Override
    public String[] getAdminCommandList()
    {
        return commands;
    }
}
