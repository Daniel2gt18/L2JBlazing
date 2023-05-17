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

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.StatusUpdate;
import com.l2jmega.util.CloseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

/**
 * @author MeGaPacK
 */
public class AdminAiox implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
        "admin_setaio",
		"admin_remove_aio"
	
	};
	
	private final static Logger _log = Logger.getLogger(AdminAiox.class.getName());
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 7)
			return false;
		
		final WorldObject target = activeChar.getTarget();
		if (target == null || !(target instanceof Player))
		{
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		if (command.startsWith("admin_setaio"))
		{
		      if (target instanceof Player) {
		        Player targetPlayer = (Player)target;
		        boolean newHero = !targetPlayer.isAioEterno();
		        if (newHero) {
		          if (AioManager.getInstance().hasAioPrivileges(targetPlayer.getObjectId()))
		            removeAio(activeChar, targetPlayer); 
		          targetPlayer.setAio(true);
		          targetPlayer.setAioEterno(true);
		          targetPlayer.getStat().addExp(targetPlayer.getStat().getExpForLevel(81));
		          targetPlayer.sendPacket(new CreatureSay(0, 17, "[Aio System]", "Voce se tornou um AIO Buffer ETERNO."));
		          updateDatabase(targetPlayer, true);
		          
		          if (Config.ALLOW_AIO_NCOLOR)
		            targetPlayer.getAppearance().setNameColor(Config.AIO_NCOLOR); 
		          if (Config.ALLOW_AIO_TCOLOR)
		            targetPlayer.getAppearance().setTitleColor(Config.AIO_TCOLOR); 
		          targetPlayer.rewardAioSkills();
		          if (Config.ALLOW_AIOX_SET_ITEM)
		            giveAioItems(targetPlayer); 
		          if (Config.ALLOW_AIO_ITEM) {
		            targetPlayer.addItem("", Config.AIO_ITEMID, 1, targetPlayer, true);
		            targetPlayer.getInventory().equipItem(targetPlayer.getInventory().getItemByItemId(Config.AIO_ITEMID));
		          } 
		          targetPlayer.sendPacket(new InventoryUpdate());
		          targetPlayer.sendPacket(new ItemList(targetPlayer, true));
		          targetPlayer.sendPacket(new StatusUpdate(targetPlayer));
		          if (Config.CHANGE_AIO_NAME)
		            nameChanger("[AIO]", targetPlayer); 
		          targetPlayer.broadcastUserInfo();
		          targetPlayer.sendSkillList();
		        } else {
		          targetPlayer.setAio(false);
		          targetPlayer.setAioEterno(false);
		          targetPlayer.sendMessage("[Aio System]: Seu AIO ETERNO foi removido.");
		          updateDatabase(targetPlayer, false);
		          targetPlayer.lostAioSkills();
		          targetPlayer.pvpColor();
		          targetPlayer.pkColor();
		          if (Config.ALLOW_AIO_ITEM) {
		            targetPlayer.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, targetPlayer, null);
		            targetPlayer.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, targetPlayer, null);
		          } 
		          targetPlayer.sendPacket(new InventoryUpdate());
		          targetPlayer.sendPacket(new ItemList(targetPlayer, true));
		          targetPlayer.sendPacket(new StatusUpdate(targetPlayer));
		          targetPlayer.getAppearance().setNameColor(16777215);
		          targetPlayer.getAppearance().setTitleColor(16777215);
		          if (Config.CHANGE_AIO_NAME)
		            nameaio("[AIO]", targetPlayer); 
		          targetPlayer.sendSkillList();
		          targetPlayer.broadcastUserInfo();
		        } 
		        targetPlayer = null;
		      } else {
		        activeChar.sendMessage("[AIO System]: Impossible to set a non Player Target as AIO.");
		        _log.info("[Aio System]: GM: " + activeChar.getName() + " is trying to set a non Player Target as AIO.");
		        return false;
		      } 
		    }
		
		else if (command.equalsIgnoreCase("admin_remove_Aio"))
			removeAio(activeChar, (Player) target);
		
		return true;
	}
	
	public static void removeAio(Player activeChar, Player targetChar)
	{
		if (!AioManager.getInstance().hasAioPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have Aio privileges.");
			return;
		}
		
		AioManager.getInstance().removeAio(targetChar.getObjectId());
		activeChar.sendMessage("You have removed Aio privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your Aio privileges were removed by the admin.", 10000));
		targetChar.setAio(false);
		
	}
	
	  public static void nameaio(String ReplaceName, Player player) {
		    if (ReplaceName == null || ReplaceName == "" || player == null)
		      return; 
		    String nameaio = player.getName();
		    if (nameaio.length() > 0)
		      nameaio = nameaio.substring(5, nameaio.length()); 
		    player.setName(nameaio);
		    player.broadcastUserInfo();
		    if (player.getClan() != null)
		      player.getClan().broadcastClanStatus(); 
		  }
		  
		  public static void nameChanger(String ReplaceName, Player player) {
		    if (ReplaceName == null || ReplaceName == "" || player == null)
		      return; 
		    String BitchName = player.getName();
		    String DaBitchRenamed = ReplaceName + BitchName;
		    player.setName(DaBitchRenamed);
		    player.broadcastUserInfo();
		    if (player.getClan() != null)
		      player.getClan().broadcastClanStatus(); 
		  }
		  
		  public static void giveAioItems(Player activeChar) {
		    for (int[] reward : Config.AIO_CHAR_ITENS) {
		      ItemInstance PhewPew1 = activeChar.getInventory().addItem("Aiox Items", reward[0], reward[1], activeChar, null);
		      activeChar.getInventory().equipItemAndRecord(PhewPew1);
		    } 
		  }
	
			/**
			 * @param player
			 * @param newVip
			 */
			public static void updateDatabase(Player player, boolean newVip)
			{
				// prevents any NPE.
				if (player == null)
					return;
				
				Connection con = null;
				try
				{
					// Database Connection
					// --------------------------------
					con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement stmt = con.prepareStatement(newVip ? INSERT_DATA : DEL_DATA);
					
					// if it is a new donator insert proper data
					// --------------------------------------------
					if (newVip)
					{
						
						stmt.setInt(1, player.getObjectId());
						stmt.setString(2, player.getName());
						stmt.setInt(3, 1);
						stmt.execute();
						stmt.close();
						stmt = null;
					}
					else
					// deletes from database
					{
						stmt.setInt(1, player.getObjectId());
						stmt.execute();
						stmt.close();
						stmt = null;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					CloseUtil.close(con);
				}
			}

			// Updates That Will be Executed by MySQL
			// ----------------------------------------
			static String INSERT_DATA = "REPLACE INTO characters_aio_eterno (obj_Id, char_name, aio) VALUES (?,?,?)";
			static String DEL_DATA = "UPDATE characters_aio_eterno SET aio = 0 WHERE obj_Id=?";
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}