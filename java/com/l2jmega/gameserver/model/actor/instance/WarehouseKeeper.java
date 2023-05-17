package com.l2jmega.gameserver.model.actor.instance;

import java.util.Map;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.itemcontainer.PcFreight;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.EnchantResult;
import com.l2jmega.gameserver.network.serverpackets.PackageToList;
import com.l2jmega.gameserver.network.serverpackets.WarehouseDepositList;
import com.l2jmega.gameserver.network.serverpackets.WarehouseWithdrawList;

public class WarehouseKeeper extends Folk
{
	public WarehouseKeeper(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean isWarehouse()
	{
		return true;
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/warehouse/" + filename + ".htm";
	}
	
	private static void showRetrieveWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
		
		player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.PRIVATE));
	}
	
	private static void showDepositWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getWarehouse());
		player.tempInventoryDisable();
		
		player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.PRIVATE));
	}
	
	private static void showDepositWindowClan(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		if (player.getClan() != null)
		{
			if (player.getClan().getLevel() == 0)
				player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
			else
			{
				player.setActiveWarehouse(player.getClan().getWarehouse());
				player.tempInventoryDisable();
				player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.CLAN));
			}
		}
	}
	
	private static void showWithdrawWindowClan(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		if ((player.getClanPrivileges() & Clan.CP_CL_VIEW_WAREHOUSE) != Clan.CP_CL_VIEW_WAREHOUSE)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
			return;
		}
		
		if (player.getClan().getLevel() == 0)
			player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
		else
		{
			player.setActiveWarehouse(player.getClan().getWarehouse());
			player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.CLAN));
		}
	}
	
	private void showWithdrawWindowFreight(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		PcFreight freight = player.getFreight();
		
		if (freight != null)
		{
			if (freight.getSize() > 0)
			{
				if (Config.ALT_GAME_FREIGHTS)
					freight.setActiveLocation(0);
				else
					freight.setActiveLocation(getRegion().hashCode());
				
				player.setActiveWarehouse(freight);
				player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.FREIGHT));
			}
			else
				player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
		}
	}
	
	private static void showDepositWindowFreight(Player player)
	{
		// No other chars in the account of this player
		if (player.getAccountChars().isEmpty())
			player.sendPacket(SystemMessageId.CHARACTER_DOES_NOT_EXIST);
		// One or more chars other than this player for this account
		else
		{
			Map<Integer, String> chars = player.getAccountChars();
			
			if (chars.size() < 1)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			player.sendPacket(new PackageToList(chars));
		}
	}
	
	private void showDepositWindowFreight(Player player, int obj_Id)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		PcFreight freight = player.getDepositedFreight(obj_Id);
		
		if (Config.ALT_GAME_FREIGHTS)
			freight.setActiveLocation(0);
		else
			freight.setActiveLocation(getRegion().hashCode());
		
		player.setActiveWarehouse(freight);
		player.tempInventoryDisable();
		player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.FREIGHT));
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
		}
		
		if (command.startsWith("WithdrawP"))
			showRetrieveWindow(player);
		else if (command.equals("DepositP"))
			showDepositWindow(player);
		else if (command.equals("WithdrawC"))
			showWithdrawWindowClan(player);
		else if (command.equals("DepositC"))
			showDepositWindowClan(player);
		else if (command.startsWith("WithdrawF"))
		{
			if (Config.ALLOW_FREIGHT)
				showWithdrawWindowFreight(player);
		}
		else if (command.startsWith("DepositF"))
		{
			if (Config.ALLOW_FREIGHT)
				showDepositWindowFreight(player);
		}
		else if (command.startsWith("FreightChar"))
		{
			if (Config.ALLOW_FREIGHT)
			{
				int startOfId = command.lastIndexOf("_") + 1;
				String id = command.substring(startOfId);
				showDepositWindowFreight(player, Integer.parseInt(id));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}