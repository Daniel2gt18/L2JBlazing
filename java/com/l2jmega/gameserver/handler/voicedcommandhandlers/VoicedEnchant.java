package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.xml.ArmorSetData;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.ArmorSet;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Armor;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;

/**
 * This class handles following player commands: - enchant_armor
 */
public class VoicedEnchant implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"seteh", // 6
		"setec", // 10
		"seteg", // 9
		"setel", // 11
		"seteb", // 12
		"setew", // 7
		"setes", // 8
		"setle", // 1
		"setre", // 2
		"setlf", // 4
		"setrf", // 5
		"seten", // 3
		"setun", // 0
		"setba", // 13
		"enchant"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		
		if (command.equals("enchant"))
			mainHtml(activeChar);
		else
		{	
			if (command.startsWith("seteh"))
				setEnchant(activeChar,Inventory.PAPERDOLL_HEAD);
			else if (command.startsWith("setec"))
				setEnchant(activeChar,Inventory.PAPERDOLL_CHEST);
			else if (command.startsWith("seteg"))
				setEnchant(activeChar,Inventory.PAPERDOLL_GLOVES);
			else if (command.startsWith("seteb"))
				setEnchant(activeChar,Inventory.PAPERDOLL_FEET);
			else if (command.startsWith("setel"))
				setEnchant(activeChar,Inventory.PAPERDOLL_LEGS);
			else if (command.startsWith("setew"))
				setEnchant(activeChar,Inventory.PAPERDOLL_RHAND);
			else if (command.startsWith("setes"))
				setEnchant(activeChar,Inventory.PAPERDOLL_LHAND);
			else if (command.startsWith("setle"))
				setEnchant(activeChar,Inventory.PAPERDOLL_LEAR);
			else if (command.startsWith("setre"))
				setEnchant(activeChar,Inventory.PAPERDOLL_REAR);
			else if (command.startsWith("setlf"))
				setEnchant(activeChar,Inventory.PAPERDOLL_LFINGER);
			else if (command.startsWith("setrf"))
				setEnchant(activeChar,Inventory.PAPERDOLL_RFINGER);
			else if (command.startsWith("seten"))
				setEnchant(activeChar,Inventory.PAPERDOLL_NECK);
			else if (command.startsWith("setun"))
				setEnchant(activeChar,Inventory.PAPERDOLL_UNDER);
			else if (command.startsWith("setba"))
				setEnchant(activeChar,Inventory.PAPERDOLL_BACK);
			
			// show the enchant menu after an action
			mainHtml(activeChar);
		}
		
		return true;
	}
	
	private static void setEnchant(Player player, int armorType)
	{
		final ItemInstance item = player.getInventory().getPaperdollItem(armorType);
		
		if (item != null && item.getLocationSlot() == armorType)
		{
			if (item.getEnchantLevel() < Config.MIN_ENCHANT)
			{
				player.sendPacket(new CreatureSay(0, Say2.TELL, "SYS","Minimum Of Enchantment +"+ Config.MIN_ENCHANT +"."));
				return;
			}
			
			if (item.getEnchantLevel() >= Config.MAX_ENCHANT)
			{
				player.sendPacket(new CreatureSay(0, Say2.TELL, "SYS","Maximum Of Enchantment +"+ Config.MAX_ENCHANT +"."));
				return;
			}
			
			if (!player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_ENCHANT_PRICE, null, true))
			{	
				player.sendPacket(new CreatureSay(0, Say2.TELL, "SYS","You don't have enough ticket donate!"));
				return;
			}
			
			final Item it = item.getItem();
			final int oldEnchant = item.getEnchantLevel() + 1;
			
			item.setEnchantLevel(item.getEnchantLevel() + 1);
			item.updateDatabase();
			
			// If item is equipped, verify the skill obtention/drop (+4 duals, +6 armorset).
			if (item.isEquipped())
			{
				final int currentEnchant = item.getEnchantLevel() + 1;
				
				// Skill bestowed by +4 duals.
				if (it instanceof Weapon)
				{
					// Old enchant was >= 4 and new is lower : we drop the skill.
					if (oldEnchant >= 4 && currentEnchant < 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.removeSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
					// Old enchant was < 4 and new is 4 or more : we add the skill.
					else if (oldEnchant < 4 && currentEnchant >= 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.addSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
				}
				// Add skill bestowed by +6 armorset.
				else if (it instanceof Armor)
				{
					// Old enchant was >= 6 and new is lower : we drop the skill.
					if (oldEnchant >= 6 && currentEnchant < 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null)
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.removeSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
					// Old enchant was < 6 and new is 6 or more : we add the skill.
					else if (oldEnchant < 6 && currentEnchant >= 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.addSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
				}
			}
			
			player.sendPacket(new ItemList(player, false));
			player.broadcastUserInfo();
			SystemMessage sm;
			sm = new SystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
			sm.addNumber(item.getEnchantLevel());
			sm.addItemName(item.getItemId());
			player.sendPacket(sm);
		}
		else
		player.sendPacket(new CreatureSay(0, Say2.TELL, "SYS","You are not equipped."));
	}
	
	public static void mainHtml(Player activeChar)
	{	
		String htmFile = "data/html/mods/donate/enchant.htm";		
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		msg.replace("%player%", activeChar.getName());
		msg.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_ENCHANT_PRICE + "</font>");
		msg.replace("%min%", "<font color=\"00FF00\">" + Config.MIN_ENCHANT + "</font>");
		msg.replace("%max%", "<font color=\"00FF00\">" + Config.MAX_ENCHANT + "</font>");
		activeChar.sendPacket(msg);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}