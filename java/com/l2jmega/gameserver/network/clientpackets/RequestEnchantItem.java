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
package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.EnchantResult;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.StatusUpdate;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

import com.l2jmega.commons.random.Rnd;

public final class RequestEnchantItem extends L2GameClientPacket
{
	private static final int[] DONATOR_WEAPON_SCROLL =
	{
		Config.GOLD_WEAPON
	};
	
	private static final int[] DONATOR_ARMOR_SCROLL =
	{
		Config.GOLD_ARMOR
	};
	
	private static final int[] CRYSTAL_SCROLLS =
	{
		731,
		732,
		949,
		950,
		953,
		954,
		957,
		958,
		961,
		962
	};
	
	private static final int[] NORMAL_WEAPON_SCROLLS =
	{
		729,
		947,
		951,
		955,
		959
	};
	
	private static final int[] BLESSED_WEAPON_SCROLLS =
	{
		6569,
		6571,
		6573,
		6575,
		6577
	};
	
	private static final int[] CRYSTAL_WEAPON_SCROLLS =
	{
		731,
		949,
		953,
		957,
		961
	};
	
	private static final int[] NORMAL_ARMOR_SCROLLS =
	{
		730,
		948,
		952,
		956,
		960
	};
	
	private static final int[] BLESSED_ARMOR_SCROLLS =
	{
		6570,
		6572,
		6574,
		6576,
		6578
	};
	
	private static final int[] CRYSTAL_ARMOR_SCROLLS =
	{
		732,
		950,
		954,
		958,
		962
	};
	
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null || _objectId == 0)
			return;
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.cancelActiveTrade();
			activeChar.sendMessage("Your trade canceled");
			return;
		}
		
		// Fix enchant transactions
		if (activeChar.isProcessingTransaction())
		{
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			activeChar.setActiveEnchantItem(null);
			return;
		}
		if (!activeChar.isOnline())
		{
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (activeChar.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(activeChar))
		{
			activeChar.sendMessage("[Olympiad]: Voce nao pode fazer isso!!");
			return;
		}
		
		if (!activeChar.isOnline())
		{
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		final ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		ItemInstance scroll = activeChar.getActiveEnchantItem();
		activeChar.setActiveEnchantItem(null);
		
		if (item == null || scroll == null)
		{
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (!Config.ENCHANT_HERO_WEAPON && item.getItemId() >= 6611 && item.getItemId() <= 6621)
		{
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		/*
		 * if(!FloodProtector.getInstance().tryPerformAction(activeChar.getObjectId(), FloodProtector.PROTECTED_ENCHANT)) { activeChar.setActiveEnchantItem(null); activeChar.sendMessage("Enchant failed"); return; }
		 */
		
		final int itemType2 = item.getItem().getType2();
		boolean enchantItem = false;
		boolean blessedScroll = false;
		boolean crystalScroll = false;
		boolean donatorScroll = false;
		int crystalId = 0;
		
		/** pretty code ;D */
		switch (item.getItem().getCrystalType())
		{
			case A:
				crystalId = 1461;
				
				if (scroll.getItemId() == Config.GOLD_WEAPON && itemType2 == Item.TYPE2_WEAPON)
					enchantItem = true;
				else if (scroll.getItemId() == Config.GOLD_ARMOR && (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY))
					enchantItem = true;
				
				switch (scroll.getItemId())
				{
					case 729:
					case 731:
					case 6569:
						if (itemType2 == Item.TYPE2_WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 730:
					case 732:
					case 6570:
						if (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY)
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case B:
				crystalId = 1460;
				
				if (scroll.getItemId() == Config.GOLD_WEAPON && itemType2 == Item.TYPE2_WEAPON)
					enchantItem = true;
				else if (scroll.getItemId() == Config.GOLD_ARMOR && (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY))
					enchantItem = true;
				
				switch (scroll.getItemId())
				{
					case 947:
					case 949:
					case 6571:
						if (itemType2 == Item.TYPE2_WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 948:
					case 950:
					case 6572:
						if (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY)
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case C:
				crystalId = 1459;
				
				if (scroll.getItemId() == Config.GOLD_WEAPON && itemType2 == Item.TYPE2_WEAPON)
					enchantItem = true;
				else if (scroll.getItemId() == Config.GOLD_ARMOR && (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY))
					enchantItem = true;
				
				switch (scroll.getItemId())
				{
					case 951:
					case 953:
					case 6573:
						if (itemType2 == Item.TYPE2_WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 952:
					case 954:
					case 6574:
						if (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY)
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case D:
				crystalId = 1458;
				
				if (scroll.getItemId() == Config.GOLD_WEAPON && itemType2 == Item.TYPE2_WEAPON)
					enchantItem = true;
				else if (scroll.getItemId() == Config.GOLD_ARMOR && (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY))
					enchantItem = true;
				
				switch (scroll.getItemId())
				{
					case 955:
					case 957:
					case 6575:
						if (itemType2 == Item.TYPE2_WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 956:
					case 958:
					case 6576:
						if (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY)
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case S:
				crystalId = 1462;
				
				if (scroll.getItemId() == Config.GOLD_WEAPON && itemType2 == Item.TYPE2_WEAPON)
					enchantItem = true;
				else if (scroll.getItemId() == Config.GOLD_ARMOR && (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY))
					enchantItem = true;
				
				switch (scroll.getItemId())
				{
					case 959:
					case 961:
					case 6577:
						if (itemType2 == Item.TYPE2_WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 960:
					case 962:
					case 6578:
						if (itemType2 == Item.TYPE2_SHIELD_ARMOR || itemType2 == Item.TYPE2_ACCESSORY)
						{
							enchantItem = true;
						}
						break;
				}
				break;
		}
		
		if (!enchantItem)
		{
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			return;
		}
		
		// Get the scroll type - Yesod
		if (scroll.getItemId() >= 6569 && scroll.getItemId() <= 6578)
		{
			blessedScroll = true;
		}
		else if (scroll.getItemId() == Config.GOLD_WEAPON || scroll.getItemId() == Config.GOLD_ARMOR)
		{
			donatorScroll = true;
		}
		else
		{
			for (final int crystalscroll : CRYSTAL_SCROLLS)
				if (scroll.getItemId() == crystalscroll)
				{
					crystalScroll = true;
					break;
				}
		}
		
		// SystemMessage sm = new SystemMessage(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
		// activeChar.sendPacket(sm);
		
		SystemMessage sm;
		
		int chance = 0;
		int maxEnchantLevel = 0;
		int minEnchantLevel = 0;
		int nextEnchantLevel = item.getEnchantLevel() + 1;
		int EnchantLevel = item.getEnchantLevel();
		
		if (item.getItem().getType2() == Item.TYPE2_WEAPON)
		{
			if (blessedScroll)
			{
				
				for (final int blessedweaponscroll : BLESSED_WEAPON_SCROLLS)
				{
					if (scroll.getItemId() == blessedweaponscroll)
					{
						if (item.getEnchantLevel() >= Config.BLESS_WEAPON_ENCHANT_LEVEL.size())
						{
							chance = Config.BLESS_WEAPON_ENCHANT_LEVEL.get(Config.BLESS_WEAPON_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.BLESS_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.BLESSED_ENCHANT_WEAPON_MAX;
						break;
					}
				}
				
			}
			else if (crystalScroll)
			{
				
				for (final int crystalweaponscroll : CRYSTAL_WEAPON_SCROLLS)
				{
					if (scroll.getItemId() == crystalweaponscroll)
					{
						if (item.getEnchantLevel() >= Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.size())
						{
							chance = Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.get(Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.CRYSTAL_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
						maxEnchantLevel = Config.CRYSTAL_ENCHANT_WEAPON_MAX;
						break;
						
					}
				}
				
			}
			else if (donatorScroll)
			{
				
				for (final int donateweaponscroll : DONATOR_WEAPON_SCROLL)
				{
					if (scroll.getItemId() == donateweaponscroll)
					{
						if (item.getEnchantLevel() >= Config.DONATOR_WEAPON_ENCHANT_LEVEL.size())
						{
							chance = Config.DONATOR_WEAPON_ENCHANT_LEVEL.get(Config.DONATOR_WEAPON_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.DONATOR_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.DONATOR_ENCHANT_MIN;
						maxEnchantLevel = Config.DONATOR_ENCHANT_WEAPON_MAX;
						break;
						
					}
				}
				
			}
			else
			{ // normal scrolls
				
				for (final int normalweaponscroll : NORMAL_WEAPON_SCROLLS)
				{
					if (scroll.getItemId() == normalweaponscroll)
					{
						if (item.getEnchantLevel() >= Config.NORMAL_WEAPON_ENCHANT_LEVEL.size())
						{
							chance = Config.NORMAL_WEAPON_ENCHANT_LEVEL.get(Config.NORMAL_WEAPON_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.NORMAL_WEAPON_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.ENCHANT_WEAPON_MAX;
						break;
					}
				}
				
			}
			
		}
		else if (item.getItem().getType2() == Item.TYPE2_SHIELD_ARMOR)
		{
			if (blessedScroll)
			{
				
				for (final int blessedarmorscroll : BLESSED_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == blessedarmorscroll)
					{
						if (item.getEnchantLevel() >= Config.BLESS_ARMOR_ENCHANT_LEVEL.size())
						{
							chance = Config.BLESS_ARMOR_ENCHANT_LEVEL.get(Config.BLESS_ARMOR_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.BLESS_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.BLESSED_ENCHANT_ARMOR_MAX;
						break;
					}
				}
				
			}
			else if (crystalScroll)
			{
				
				for (final int crystalarmorscroll : CRYSTAL_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == crystalarmorscroll)
					{
						if (item.getEnchantLevel() >= Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.size())
						{
							chance = Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.get(Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.CRYSTAL_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
						maxEnchantLevel = Config.CRYSTAL_ENCHANT_ARMOR_MAX;
						break;
					}
				}
				
			}
			else if (donatorScroll)
			{
				
				for (final int donatorarmorscroll : DONATOR_ARMOR_SCROLL)
				{
					if (scroll.getItemId() == donatorarmorscroll)
					{
						if (item.getEnchantLevel() >= Config.DONATOR_ARMOR_ENCHANT_LEVEL.size())
						{
							chance = Config.DONATOR_ARMOR_ENCHANT_LEVEL.get(Config.DONATOR_ARMOR_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.DONATOR_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.DONATOR_ENCHANT_MIN;
						maxEnchantLevel = Config.DONATOR_ENCHANT_ARMOR_MAX;
						break;
					}
				}
				
			}
			else
			{ // normal scrolls
				
				for (final int normalarmorscroll : NORMAL_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == normalarmorscroll)
					{
						if (item.getEnchantLevel() >= Config.NORMAL_ARMOR_ENCHANT_LEVEL.size())
						{
							chance = Config.NORMAL_ARMOR_ENCHANT_LEVEL.get(Config.NORMAL_ARMOR_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.NORMAL_ARMOR_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.ENCHANT_ARMOR_MAX;
						break;
					}
				}
				
			}
			
		}
		else if (item.getItem().getType2() == Item.TYPE2_ACCESSORY)
		{
			if (blessedScroll)
			{
				
				for (final int blessedjewelscroll : BLESSED_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == blessedjewelscroll)
					{
						if (item.getEnchantLevel() >= Config.BLESS_JEWELRY_ENCHANT_LEVEL.size())
						{
							chance = Config.BLESS_JEWELRY_ENCHANT_LEVEL.get(Config.BLESS_JEWELRY_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.BLESS_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.BLESSED_ENCHANT_JEWELRY_MAX;
						break;
					}
				}
				
			}
			else if (crystalScroll)
			{
				
				for (final int crystaljewelscroll : CRYSTAL_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == crystaljewelscroll)
					{
						if (item.getEnchantLevel() >= Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.size())
						{
							chance = Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.get(Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.CRYSTAL_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.CRYSTAL_ENCHANT_MIN;
						maxEnchantLevel = Config.CRYSTAL_ENCHANT_JEWELRY_MAX;
						
						break;
					}
				}
				
			}
			else if (donatorScroll)
			{
				
				for (final int donatorjewelscroll : DONATOR_ARMOR_SCROLL)
				{
					if (scroll.getItemId() == donatorjewelscroll)
					{
						if (item.getEnchantLevel() >= Config.DONATOR_JEWELRY_ENCHANT_LEVEL.size())
						{
							chance = Config.DONATOR_JEWELRY_ENCHANT_LEVEL.get(Config.DONATOR_JEWELRY_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.DONATOR_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						minEnchantLevel = Config.DONATOR_ENCHANT_MIN;
						maxEnchantLevel = Config.DONATOR_ENCHANT_JEWELRY_MAX;
						break;
					}
				}
				
			}
			else
			{
				
				for (final int normaljewelscroll : NORMAL_ARMOR_SCROLLS)
				{
					if (scroll.getItemId() == normaljewelscroll)
					{
						if (item.getEnchantLevel() >= Config.NORMAL_JEWELRY_ENCHANT_LEVEL.size())
						{
							chance = Config.NORMAL_JEWELRY_ENCHANT_LEVEL.get(Config.NORMAL_JEWELRY_ENCHANT_LEVEL.size());
						}
						else
						{
							chance = Config.NORMAL_JEWELRY_ENCHANT_LEVEL.get(item.getEnchantLevel() + 1);
						}
						maxEnchantLevel = Config.ENCHANT_JEWELRY_MAX;
						break;
					}
				}
				
			}
			
		}
		
		if ((maxEnchantLevel != 0 && item.getEnchantLevel() >= maxEnchantLevel) || (item.getEnchantLevel()) < minEnchantLevel)
		{
			activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			return;
		}
		
		if (Config.SCROLL_STACKABLE)
		{
			scroll = activeChar.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, activeChar, item);
		}
		else
		{
			scroll = activeChar.getInventory().destroyItem("Enchant", scroll, activeChar, item);
		}
		
		if (scroll == null)
		{
			activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			return;
		}
		
		if (item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX || item.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR && item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX_FULL)
		{
			chance = 100;
		}
		
		int rndValue = Rnd.get(100);
		
		final Object aChance = item.fireEvent("calcEnchantChance", new Object[chance]);
		
		if (aChance != null)
		{
			chance = (Integer) aChance;
		}
		synchronized (item)
		{
		    if (Config.ENABLE_DWARF_ENCHANT_BONUS && activeChar.getRace() == ClassRace.DWARF)
		        if (activeChar.getLevel() >= Config.DWARF_ENCHANT_MIN_LEVEL)
		          rndValue -= Config.DWARF_ENCHANT_BONUS;
		    
			if (rndValue < chance)
			{
				if (item.getOwnerId() != activeChar.getObjectId())
				{
					activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
					return;
				}
				
				if (item.getLocation() != ItemInstance.ItemLocation.INVENTORY && item.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
				{
					activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
					return;
				}
				
				if (item.getEnchantLevel() == 0)
				{
					sm = new SystemMessage(SystemMessageId.S1_SUCCESSFULLY_ENCHANTED);
					sm.addItemName(item.getItemId());
					activeChar.sendPacket(sm);
					
					if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(nextEnchantLevel))
					{
						Announcement.announceToPlayers(activeChar.getName() + " Enchanted " + item.getItemName() + " +" + nextEnchantLevel + " ..");
						MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
						activeChar.sendPacket(MSU);
						activeChar.broadcastPacket(MSU);
					}
					
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getItemId());
					activeChar.sendPacket(sm);
					
					if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(nextEnchantLevel))
					{
						Announcement.announceToPlayers(activeChar.getName() + " Enchanted " + item.getItemName() + " +" + nextEnchantLevel + " ..");
						MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
						activeChar.sendPacket(MSU);
						activeChar.broadcastPacket(MSU);
					}
					
				}
				
				item.setEnchantLevel(item.getEnchantLevel() + 1);
				item.updateDatabase();
			}
			else
			{
				if (crystalScroll)
				{
					sm = SystemMessage.sendString("Failed in Crystal Enchant. The enchant value of the item become " + Config.CRYSTAL_ENCHANT_MIN);
					activeChar.sendPacket(sm);
					
					if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(EnchantLevel))
					{
						Announcement.AnnouncePM(activeChar.getName() + " Failed " + item.getItemName() + " +" + EnchantLevel + " ..");
					}
				}
				else if (blessedScroll)
				{
					sm = SystemMessage.sendString("Failed in Blessed Enchant. The enchant value of the item become " + Config.BREAK_ENCHANT);
					activeChar.sendPacket(sm);
					if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(EnchantLevel))
					{
						Announcement.AnnouncePM(activeChar.getName() + " Failed " + item.getItemName() + " +" + EnchantLevel + " ..");
					}
				}
				else if (donatorScroll)
				{
					if (Config.DONATOR_DECREASE_ENCHANT)
					{
						if (item.getEnchantLevel() <= Config.DONATOR_ENCHANT_MIN)
						{
							SystemMessage sm1 = SystemMessage.sendString("Failed Golden Enchant.");
							activeChar.sendPacket(sm1);
						}
						else
						{
							SystemMessage sm1 = SystemMessage.sendString("Failed Golden Enchant. His equipment had 1 reduced enchant.");
							activeChar.sendPacket(sm1);
						}
					}
					else
					{
						SystemMessage sm1 = SystemMessage.sendString("Failed Golden Enchant.");
						activeChar.sendPacket(sm1);
					}
				}
				else
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = new SystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_S2_EVAPORATED);
						sm.addNumber(item.getEnchantLevel());
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
						if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(EnchantLevel))
						{
							Announcement.AnnouncePM(activeChar.getName() + " Crystallized " + item.getItemName() + " +" + EnchantLevel + " ..");
	
						}
					}
					else
					{
						sm = new SystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_EVAPORATED);
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
						if (Config.ENABLE_ENCHANT_ANNOUNCE && Config.LIST_ENCHANT_ANNOUNCE_LEVEL.contains(EnchantLevel))
						{
							Announcement.AnnouncePM(activeChar.getName() + " Crystallized " + item.getItemName() + " +" + EnchantLevel + " ..");
						}
					}
				}
				
				if (!blessedScroll && !crystalScroll && !donatorScroll)
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
						sm.addNumber(item.getEnchantLevel());
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
					}
					else
					{
						sm = new SystemMessage(SystemMessageId.S1_DISARMED);
						sm.addItemName(item.getItemId());
						activeChar.sendPacket(sm);
					}
					
					if (item.isEquipped())
					{
						if (item.isAugmented())
						{
							item.getAugmentation().removeBonus(activeChar);
						}
						
						final ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getEquipSlot());
						
						final InventoryUpdate iu = new InventoryUpdate();
						for (final ItemInstance element : unequiped)
						{
							iu.addModifiedItem(element);
						}
						activeChar.sendPacket(iu);
						
						activeChar.broadcastUserInfo();
					}
					
					int count = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
					if (count < 1)
					{
						count = 1;
					}
					
					if (item.fireEvent("enchantFail", new Object[] {}) != null)
						return;
					final ItemInstance destroyItem = activeChar.getInventory().destroyItem("Enchant", item, activeChar, null);
					if (destroyItem == null)
						return;
					
					final ItemInstance crystals = activeChar.getInventory().addItem("Enchant", crystalId, count, activeChar, destroyItem);
					
					sm = new SystemMessage(SystemMessageId.EARNED_S2_S1_S);
					sm.addItemName(crystals.getItemId());
					sm.addNumber(count);
					activeChar.sendPacket(sm);
					
					activeChar.sendPacket(new ItemList(activeChar, true));
					
					final StatusUpdate su = new StatusUpdate(activeChar);
					su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
					activeChar.sendPacket(su);
					
					activeChar.broadcastUserInfo();
					
					final World world = World.getInstance();
					world.removeObject(destroyItem);
				}
				else
				{
					if (blessedScroll)
					{
						item.setEnchantLevel(Config.BREAK_ENCHANT);
						item.updateDatabase();
					}
					else if (crystalScroll)
					{
						item.setEnchantLevel(Config.CRYSTAL_ENCHANT_MIN);
						item.updateDatabase();
					}
					else if (donatorScroll)
					{
						if (Config.DONATOR_DECREASE_ENCHANT)
						{
							if (item.getEnchantLevel() <= Config.DONATOR_ENCHANT_MIN)
								item.setEnchantLevel(item.getEnchantLevel());
							else
								item.setEnchantLevel(item.getEnchantLevel() - 1);		
						}
						else if(Config.DONATOR_LOST_ENCHANT)
							item.setEnchantLevel(item.getEnchantLevel());
						else
							item.setEnchantLevel(Config.DONATOR_BREAK_ENCHANT);
						
						item.updateDatabase();
					}					
				}
			}
		}
		sm = null;
		
		StatusUpdate su = new StatusUpdate(activeChar);
		su.addAttribute(StatusUpdate.CUR_LOAD, activeChar.getCurrentLoad());
		activeChar.sendPacket(su);
		su = null;
		
		activeChar.sendPacket(new EnchantResult(item.getEnchantLevel())); // FIXME i'm really not sure about this...
		activeChar.sendPacket(new ItemList(activeChar, false)); // TODO update only the enchanted item
		activeChar.broadcastUserInfo();
		activeChar.setActiveEnchantItem(null);
	}
	
	@Override
	public String getType()
	{
		return "[C] 58 RequestEnchantItem";
	}
}
