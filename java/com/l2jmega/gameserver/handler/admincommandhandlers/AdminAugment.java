/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.L2Augmentation;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;

/**
 * Give / remove aio status to player Changes name color and title color if enabled Uses: setaio [<player_name>] [<time_duration in days>] If <player_name> is not specified, the current target player is used.
 * @author KhayrusS && SweeTs
 */
public class AdminAugment implements IAdminCommandHandler
{
	private static String[] _adminCommands =
	{
		"admin_add_arglist",
		"admin_add_arg",
		"admin_add_int",
		"admin_add_str",
		"admin_add_men",
		"admin_add_con",
	
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 7)
			return false;
		
		StringTokenizer st1 = new StringTokenizer(command);
		command = st1.nextToken();
		
		if ((command.startsWith("admin_add_arglist")))
			AdminHelpPage.showHelpPage(activeChar, "augments.htm");
		else if (command.equals("admin_add_arg"))
		{
			
			Player targetz = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
				targetz = (Player) activeChar.getTarget();
			
			try
			{
				final int id = Integer.parseInt(st1.nextToken());
				
				int count = 1;
				
				if (st1.hasMoreTokens())
				{
					count = Integer.parseInt(st1.nextToken());
				}
				
				addargument(activeChar, targetz, id, count);
				
				AdminHelpPage.showHelpPage(activeChar, "augmentweapon.htm");
				
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //add_arg [id] [lvl]");
				AdminHelpPage.showHelpPage(activeChar, "augmentweapon.htm");
				
			}
		}
		else if ((command.startsWith("admin_add_int")))
		{
			Player target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
			{
				target = (Player) activeChar.getTarget();
				add(activeChar, target, 1);
			}
			else
				activeChar.sendMessage("Target invalido");
			
		}
		else if ((command.startsWith("admin_add_str")))
		{
			Player target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
			{
				target = (Player) activeChar.getTarget();
				add(activeChar, target, 2);
			}
			else
				activeChar.sendMessage("Target invalido");
			
		}
		else if ((command.startsWith("admin_add_men")))
		{
			Player target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
			{
				target = (Player) activeChar.getTarget();
				add(activeChar, target, 3);
			}
			else
				activeChar.sendMessage("Target invalido");
			
		}
		else if ((command.startsWith("admin_add_con")))
		{
			Player target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
			{
				target = (Player) activeChar.getTarget();
				add(activeChar, target, 4);
			}
			else
				activeChar.sendMessage("Target invalido");
			
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return _adminCommands;
	}
	
	@SuppressWarnings(
	{
		"null"
	})
	public static void add(Player activeChar, Player target, int id)
	{
		if (id == 1)
			id = 1071062100;
		else if (id == 2)
			id = 1070927410;
		else if (id == 3)
			id = 1071123988;
		else if (id == 4)
			id = 1070996530;
		
		else
		{
			activeChar.sendMessage("Invalido.");
			return;
		}
		
		ItemInstance rhand = target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if (rhand.isAugmented())
		{
			activeChar.sendMessage("SYS: Esta arma ja esta Argumentada.");
			return;
		}
		
		if (rhand != null)
		{
			rhand.setAugmentation(new L2Augmentation(id, 0, 0));
			ItemInstance[] unequipped = target.getInventory().unEquipItemInBodySlotAndRecord(rhand.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance element : unequipped)
				iu.addModifiedItem(element);
			target.sendPacket(iu);
		}
		// Apply augmentation bonuses on equip
		if (rhand.isAugmented())
			rhand.getAugmentation().applyBonus(target);
		
		target.sendPacket(new ItemList(target, true));
		target.broadcastUserInfo();
		
	}
	
	@SuppressWarnings("null")
	public static void addargument(Player activeChar, Player target, int id, int lvl)
	{
		ItemInstance rhand = target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if (rhand.isAugmented())
		{
			activeChar.sendMessage("SYS: Esta arma ja esta Argumentada.");
			return;
		}
		
		if (rhand != null)
		{
			rhand.setAugmentation(new L2Augmentation(0, id, lvl));
			ItemInstance[] unequipped = target.getInventory().unEquipItemInBodySlotAndRecord(rhand.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance element : unequipped)
				iu.addModifiedItem(element);
			target.sendPacket(iu);
		}
		
		// Apply augmentation bonuses on equip
		if (rhand.isAugmented())
			rhand.getAugmentation().applyBonus(target);
		
		target.sendPacket(new ItemList(target, true));
		target.broadcastUserInfo();
		
		activeChar.sendMessage("SYS: Voce adicionou uma Skills Augment no Jogador " + target.getName());
	}
}