package com.l2jmega.gameserver.model.actor.instance;

import java.util.List;
import java.util.StringTokenizer;

import com.l2jmega.Config;
import com.l2jmega.events.Arena1x1;
import com.l2jmega.events.Arena2x2;
import com.l2jmega.events.Arena5x5;
import com.l2jmega.events.Arena9x9;
import com.l2jmega.gameserver.data.CharTemplateTable;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.type.CrystalType;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class Tournament extends Folk
{
	public Tournament(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/tournament/index.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		
		if (Arena1x1.registered.size() == 0)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		else if (Arena1x1.registered.size() == 1)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
		else if (Arena1x1.registered.size() == 2)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
		else if (Arena1x1.registered.size() == 3)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
		else if (Arena1x1.registered.size() == 4)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
		else if (Arena1x1.registered.size() == 5)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
		else if (Arena1x1.registered.size() == 6)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
		else if (Arena1x1.registered.size() == 7)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
		else if (Arena1x1.registered.size() == 8)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
		else if (Arena1x1.registered.size() >= 9)
			html.replace("%1x1%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
		if (Arena2x2.registered.size() == 0)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		else if (Arena2x2.registered.size() == 1)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
		else if (Arena2x2.registered.size() == 2)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
		else if (Arena2x2.registered.size() == 3)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
		else if (Arena2x2.registered.size() == 4)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
		else if (Arena2x2.registered.size() == 5)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
		else if (Arena2x2.registered.size() == 6)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
		else if (Arena2x2.registered.size() == 7)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
		else if (Arena2x2.registered.size() == 8)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
		else if (Arena2x2.registered.size() >= 9)
			html.replace("%2x2%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
		if (Arena5x5.registered.size() == 0)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		else if (Arena5x5.registered.size() == 1)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
		else if (Arena5x5.registered.size() == 2)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
		else if (Arena5x5.registered.size() == 3)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
		else if (Arena5x5.registered.size() == 4)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
		else if (Arena5x5.registered.size() == 5)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
		else if (Arena5x5.registered.size() == 6)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
		else if (Arena5x5.registered.size() == 7)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
		else if (Arena5x5.registered.size() == 8)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
		else if (Arena5x5.registered.size() >= 9)
			html.replace("%5x5%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
		if (Arena9x9.registered.size() == 0)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_0_over\" fore=\"L2UI_CH3.calculate1_0\">");
		else if (Arena9x9.registered.size() == 1)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_1_over\" fore=\"L2UI_CH3.calculate1_1\">");
		else if (Arena9x9.registered.size() == 2)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_2_over\" fore=\"L2UI_CH3.calculate1_2\">");
		else if (Arena9x9.registered.size() == 3)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_3_over\" fore=\"L2UI_CH3.calculate1_3\">");
		else if (Arena9x9.registered.size() == 4)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_4_over\" fore=\"L2UI_CH3.calculate1_4\">");
		else if (Arena9x9.registered.size() == 5)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_5_over\" fore=\"L2UI_CH3.calculate1_5\">");
		else if (Arena9x9.registered.size() == 6)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_6_over\" fore=\"L2UI_CH3.calculate1_6\">");
		else if (Arena9x9.registered.size() == 7)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_7_over\" fore=\"L2UI_CH3.calculate1_7\">");
		else if (Arena9x9.registered.size() == 8)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_8_over\" fore=\"L2UI_CH3.calculate1_8\">");
		else if (Arena9x9.registered.size() >= 9)
			html.replace("%9x9%", "<button value=\"\" action=\"\" width=32 height=32 back=\"L2UI_CH3.calculate1_9_over\" fore=\"L2UI_CH3.calculate1_9\">");
		
		player.sendPacket(html);
	}
	
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("1x1"))
		{
			if (!Config.ALLOW_1X1_REGISTER)
			{
				player.sendMessage("[WARN]:The 1x1 is Temporarily unavailable.");
				return;
			}
			
			if (!Config.TOUR_GRADE_A_1X1)
			{
				ItemInstance item;
				for (int i = 1; i < 15; i++)
				{
					item = player.getInventory().getPaperdollItem(i);
					if (item == null)
						continue;
					
					if (item.getItem().getCrystalType() == CrystalType.S && !(item.getItem().getCrystalType() == CrystalType.NONE))
					{
						player.sendMessage("Tournament: Items (Grade-A)!");
						player.sendPacket(new ExShowScreenMessage("Tournament: Items (Grade-A)!", 5000));
						return;
					}
					
					if (item.getEnchantLevel() > Config.ALT_TOUR_ENCHANT_LIMIT)
					{
						player.sendMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".");
						player.sendPacket(new ExShowScreenMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".", 5000));
						return;
					}
				}
			}
			
			if (player._active_boxes > 1 && !Config.Allow_Same_HWID_On_Tournament)
			{
				final List<String> players_in_boxes = player.active_boxes_characters;
				
				if (players_in_boxes != null && players_in_boxes.size() > 1)
					for (final String character_name : players_in_boxes)
					{
						final Player ppl = World.getInstance().getPlayer(character_name);
						
						if (ppl != null && ppl.isArenaProtection())
						{
							player.sendMessage("You are already participating in Tournament with another char!");
							return;
						}
					}
			}
			
			if (player.isArena1x1() || player.isArena2x2() || player.isArena5x5() || player.isArena9x9() || player.isArenaProtection())
			{
				player.sendMessage("Tournament: You already registered!");
				return;
			}
			else if (player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT)
			{
				player.sendMessage("Tournament: Bishop not allowed in Tournament 1x1.");
				return;
			}
			else if (player.isCursedWeaponEquipped()|| player.isInStoreMode() || player.getKarma() > 0)
			{
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				return;
			}
			else if (player.isInParty())
			{
				player.sendMessage("Tournament: Leave your party.");
				player.sendPacket(new ExShowScreenMessage("Tournament: Leave your party.", 6 * 1000));
				return;
			}
			else if (OlympiadManager.getInstance().isRegistered(player))
			{
				player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				return;
			}
			else if (player._inEventTvT || player._inEventCTF)
			{
				player.sendMessage("Tournament: You already participated in another event!");
				return;
			}
			
			
			if (player.isAio() || player.isAioEterno() || AioManager.getInstance().hasAioPrivileges(player.getObjectId())) {
				player.sendMessage("Aio Buffer You can not attend the event!");
				return;
			}   
			
			
			if (Arena1x1.getInstance().register(player))
			{
				player.sendMessage("Tournament: Your participation has been approved.");
				player.sendPacket(new ExShowScreenMessage("Successfully registered - 1x1", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				player.setArenaProtection(true);
				player.setArena1x1(true);
				showChatWindow(player);
			}
			else
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			
		}
		if (command.startsWith("2x2"))
		{
			if (!Config.ALLOW_2X2_REGISTER)
			{
				player.sendMessage("[WARN]:The 2x2 is Temporarily unavailable.");
				return;
			}
			
			if (!Config.TOUR_GRADE_A_2X2)
			{
				ItemInstance item;
				for (int i = 1; i < 15; i++)
				{
					item = player.getInventory().getPaperdollItem(i);
					if (item == null)
						continue;
					
					if (item.getItem().getCrystalType() == CrystalType.S)
					{
						player.sendMessage("Tournament: Items (Grade-A)!");
						player.sendPacket(new ExShowScreenMessage("Tournament: Items (Grade-A)!", 5000));
						return;
					}
					
					if (item.getEnchantLevel() > Config.ALT_TOUR_ENCHANT_LIMIT)
					{
						player.sendMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".");
						player.sendPacket(new ExShowScreenMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".", 5000));
						return;
					}
				}
			}
			
			if (player._active_boxes > 1 && !Config.Allow_Same_HWID_On_Tournament)
			{
				final List<String> players_in_boxes = player.active_boxes_characters;
				
				if (players_in_boxes != null && players_in_boxes.size() > 1)
					for (final String character_name : players_in_boxes)
					{
						final Player ppl = World.getInstance().getPlayer(character_name);
						
						if (ppl != null && ppl.isArenaProtection())
						{
							player.sendMessage("You are already participating in Tournament with another char!");
							return;
						}
					}
			}
			
			if (player.isArena1x1() || player.isArena2x2() || player.isArena5x5() || player.isArena9x9() || player.isArenaProtection())
			{
				player.sendMessage("Tournament: You already registered!");
				return;
			}
			else if (!player.isInParty())
			{
				player.sendMessage("Tournament: You dont have a party.");
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Tournament: You are not the party leader!");
				return;
			}
			else if (player.getParty().getMemberCount() < 2)
			{
				player.sendMessage("Tournament: Your party does not have 2 members.");
				player.sendPacket(new ExShowScreenMessage("Your party does not have 2 members", 6 * 1000));
				return;
			}
			else if (player.getParty().getMemberCount() > 2)
			{
				player.sendMessage("Tournament: Your Party can not have more than 2 members.");
				player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 2 members", 6 * 1000));
				return;
			}
			
			Player assist = player.getParty().getPartyMembers().get(1);
			
			String className = CharTemplateTable.getInstance().getClassNameById(player.getClassId().getId());
			String assist_className = CharTemplateTable.getInstance().getClassNameById(assist.getClassId().getId());
			
			if ((player.getClassId() == ClassId.GLADIATOR || player.getClassId() == ClassId.DUELIST || player.getClassId() == ClassId.GRAND_KHAVATARI || player.getClassId() == ClassId.TYRANT) && (assist.getClassId() == ClassId.GLADIATOR || assist.getClassId() == ClassId.DUELIST || assist.getClassId() == ClassId.GRAND_KHAVATARI || assist.getClassId() == ClassId.TYRANT))
			{
				player.sendMessage("Tournament: Only 1 " + className + " / " + assist_className + " allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only 1 " + className + " / " + assist_className + " allowed per party.", 6 * 1000));
				return;
			}
			else if (assist.getClassId() == ClassId.SHILLIEN_ELDER || assist.getClassId() == ClassId.SHILLIEN_SAINT || assist.getClassId() == ClassId.BISHOP || assist.getClassId() == ClassId.CARDINAL || assist.getClassId() == ClassId.ELVEN_ELDER || assist.getClassId() == ClassId.EVAS_SAINT)
			{
				assist.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				player.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				return;
			}
			else if (player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT)
			{
				assist.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				player.sendMessage("Tournament: Bishop not allowed in Tournament 2x2.");
				return;
			}
			else if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0)
			{
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				return;
			}
			else if (player.getClassId() == assist.getClassId())
			{
				player.sendMessage("Tournament: Only 1 " + className + "'s allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only 1 " + className + "'s allowed per party.", 6 * 1000));
				return;
			}
			else if ((player.getClassId() == ClassId.HAWKEYE || player.getClassId() == ClassId.SAGGITARIUS || player.getClassId() == ClassId.MOONLIGHT_SENTINEL || player.getClassId() == ClassId.SILVER_RANGER || player.getClassId() == ClassId.GHOST_SENTINEL || player.getClassId() == ClassId.PHANTOM_RANGER) && (assist.getClassId() == ClassId.HAWKEYE || assist.getClassId() == ClassId.SAGGITARIUS || assist.getClassId() == ClassId.MOONLIGHT_SENTINEL || assist.getClassId() == ClassId.SILVER_RANGER || assist.getClassId() == ClassId.GHOST_SENTINEL || assist.getClassId() == ClassId.PHANTOM_RANGER))
			{
				player.sendMessage("Tournament: Only 1 Acher allowed per party.");
				player.sendPacket(new ExShowScreenMessage("OOnly 1 Acher allowed per party.", 6 * 1000));
				return;
			}
			else if ((player.getClassId() == ClassId.ADVENTURER || player.getClassId() == ClassId.TREASURE_HUNTER || player.getClassId() == ClassId.WIND_RIDER || player.getClassId() == ClassId.PLAINS_WALKER || player.getClassId() == ClassId.GHOST_HUNTER || player.getClassId() == ClassId.ABYSS_WALKER) && (assist.getClassId() == ClassId.ADVENTURER || assist.getClassId() == ClassId.TREASURE_HUNTER || assist.getClassId() == ClassId.WIND_RIDER || assist.getClassId() == ClassId.PLAINS_WALKER || assist.getClassId() == ClassId.GHOST_HUNTER || assist.getClassId() == ClassId.ABYSS_WALKER))
			{
				player.sendMessage("Tournament: Only 1 Dagger allowed per party.");
				player.sendPacket(new ExShowScreenMessage("OOnly 1 Dagger allowed per party.", 6 * 1000));
				return;
			}
			else if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist))
			{
				player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				return;
			}
			else if (player._inEventTvT || assist._inEventTvT || player._inEventCTF || assist._inEventCTF)
			{
				player.sendMessage("Tournament: You already participated in another event!");
				assist.sendMessage("Tournament: You already participated in another event!");
				return;
			}
			
			L2Party plparty = player.getParty();
			for (Player ppl : plparty.getPartyMembers()) {
				if (ppl != null)
					if (ppl.getParty() != null)
						if (ppl.isAio() || ppl.isAioEterno() || AioManager.getInstance().hasAioPrivileges(ppl.getObjectId())) {
							ppl.sendMessage("Aio Buffer You can not attend the event!");
							return;
						}   
			} 
			
			if (!Config.Allow_Same_HWID_On_Tournament)
			{
				String ip1 = player.getHWID();
				String ip2 = assist.getHWID();
				
				if (ip1.equals(ip2))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
			}
			
			if (Config.ARENA_SKILL_PROTECT)
			{
				for (L2Effect effect : player.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						player.stopSkillEffects(effect.getSkill().getId());
				}
				
				for (L2Effect effect : assist.getAllEffects())
				{
					if (Config.ARENA_STOP_SKILL_LIST.contains(effect.getSkill().getId()))
						assist.stopSkillEffects(effect.getSkill().getId());
				}
				
				for (L2Effect effect : player.getAllEffects())
				{
					if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
						player.stopSkillEffects(effect.getSkill().getId());
				}
				
				for (L2Effect effect : assist.getAllEffects())
				{
					if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
						assist.stopSkillEffects(effect.getSkill().getId());
				}
			}
			
			if (Arena2x2.getInstance().register(player, assist))
			{
				player.sendMessage("Tournament: Your participation has been approved.");
				assist.sendMessage("Tournament: Your participation has been approved.");
				player.sendPacket(new ExShowScreenMessage("Successfully registered - 2x2", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist.sendPacket(new ExShowScreenMessage("Successfully registered - 2x2", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				player.setArenaProtection(true);
				assist.setArenaProtection(true);
				player.setArena2x2(true);
				assist.setArena2x2(true);
				showChatWindow(player);
			}
			else
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			
		}
		else if (command.startsWith("5x5"))
		{
			
			if (!Config.ALLOW_5X5_REGISTER)
			{
				player.sendMessage("[WARN]:The 5x5 is Temporarily unavailable.");
				return;
			}
			
			if (!Config.TOUR_GRADE_A_5X5)
			{
				ItemInstance item;
				for (int i = 1; i < 15; i++)
				{
					item = player.getInventory().getPaperdollItem(i);
					if (item == null)
						continue;
					
					if (item.getItem().getCrystalType() == CrystalType.S)
					{
						player.sendMessage("Tournament: Items (Grade-A)!");
						player.sendPacket(new ExShowScreenMessage("Tournament: Items (Grade-A)!", 5000));
						return;
					}
					
					if (item.getEnchantLevel() > Config.ALT_TOUR_ENCHANT_LIMIT)
					{
						player.sendMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".");
						player.sendPacket(new ExShowScreenMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".", 5000));
						return;
					}
				}
			}
			
			if (player._active_boxes > 1 && !Config.Allow_Same_HWID_On_Tournament)
			{
				final List<String> players_in_boxes = player.active_boxes_characters;
				
				if (players_in_boxes != null && players_in_boxes.size() > 1)
					for (final String character_name : players_in_boxes)
					{
						final Player ppl = World.getInstance().getPlayer(character_name);
						
						if (ppl != null && ppl.isArenaProtection())
						{
							player.sendMessage("You are already participating in Tournament with another char!");
							return;
						}
					}
			}
			
			if (player.isArena1x1() || player.isArena2x2() || player.isArena5x5() || player.isArena9x9() || player.isArenaProtection())
			{
				player.sendMessage("Tournament: You already registered!");
				return;
			}
			else if (!player.isInParty())
			{
				player.sendMessage("Tournament: You dont have a party.");
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Tournament: You are not the party leader!");
				return;
			}
			else if (player.getParty().getMemberCount() < 5)
			{
				player.sendMessage("Tournament: Your party does not have 5 members.");
				player.sendPacket(new ExShowScreenMessage("Your party does not have 5 members", 6 * 1000));
				return;
			}
			else if (player.getParty().getMemberCount() > 5)
			{
				player.sendMessage("Tournament: Your Party can not have more than 5 members.");
				player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 5 members", 6 * 1000));
				return;
			}
			
			Player assist = player.getParty().getPartyMembers().get(1);
			Player assist2 = player.getParty().getPartyMembers().get(2);
			Player assist3 = player.getParty().getPartyMembers().get(3);
			Player assist4 = player.getParty().getPartyMembers().get(4);
			
			if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || assist2.isCursedWeaponEquipped() || assist3.isCursedWeaponEquipped() || assist4.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || assist2.isInStoreMode() || assist3.isInStoreMode()  || assist4.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0 || assist2.getKarma() > 0 || assist3.getKarma() > 0 || assist4.getKarma() > 0)
			{
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist2.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist3.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist4.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				return;
			}
			else if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist) || OlympiadManager.getInstance().isRegistered(assist2) || OlympiadManager.getInstance().isRegistered(assist3) || OlympiadManager.getInstance().isRegistered(assist4))
			{
				player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist2.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist3.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist4.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				return;
			}
			else if (player._inEventTvT || assist._inEventTvT || assist2._inEventTvT || assist3._inEventTvT || assist4._inEventTvT || player._inEventCTF || assist._inEventCTF || assist2._inEventCTF || assist3._inEventCTF || assist4._inEventCTF)
			{
				player.sendMessage("Tournament: You already participated in TvT event!");
				assist.sendMessage("Tournament: You already participated in TvT event!");
				assist2.sendMessage("Tournament: You already participated in TvT event!");
				assist3.sendMessage("Tournament: You already participated in TvT event!");
				assist4.sendMessage("Tournament: You already participated in TvT event!");
				return;
			}
			
			L2Party plparty = player.getParty();
			for (Player ppl : plparty.getPartyMembers()) {
				if (ppl != null)
					if (ppl.getParty() != null)
						if (ppl.isAio() || ppl.isAioEterno() || AioManager.getInstance().hasAioPrivileges(ppl.getObjectId())) {
							ppl.sendMessage("Aio Buffer You can not attend the event!");
							return;
						}   
			} 
			
			if (!Config.Allow_Same_HWID_On_Tournament)
			{
				String ip1 = player.getHWID();
				String ip2 = assist.getHWID();
				String ip3 = assist2.getHWID();
				String ip4 = assist3.getHWID();
				String ip5 = assist4.getHWID();
				
				if (ip1.equals(ip2) || ip1.equals(ip3) || ip1.equals(ip4) || ip1.equals(ip5))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					assist2.sendMessage("Tournament: Register only 1 player per Computer");
					assist3.sendMessage("Tournament: Register only 1 player per Computer");
					assist4.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
				else if (ip2.equals(ip1) || ip2.equals(ip3) || ip2.equals(ip4) || ip2.equals(ip5))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					assist2.sendMessage("Tournament: Register only 1 player per Computer");
					assist3.sendMessage("Tournament: Register only 1 player per Computer");
					assist4.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
				else if (ip3.equals(ip1) || ip3.equals(ip2) || ip3.equals(ip4) || ip3.equals(ip5))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					assist2.sendMessage("Tournament: Register only 1 player per Computer");
					assist3.sendMessage("Tournament: Register only 1 player per Computer");
					assist4.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
				else if (ip4.equals(ip1) || ip4.equals(ip2) || ip4.equals(ip3) || ip4.equals(ip5))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					assist2.sendMessage("Tournament: Register only 1 player per Computer");
					assist3.sendMessage("Tournament: Register only 1 player per Computer");
					assist4.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
				else if (ip5.equals(ip1) || ip5.equals(ip2) || ip5.equals(ip3) || ip5.equals(ip4))
				{
					player.sendMessage("Tournament: Register only 1 player per Computer");
					assist.sendMessage("Tournament: Register only 1 player per Computer");
					assist2.sendMessage("Tournament: Register only 1 player per Computer");
					assist3.sendMessage("Tournament: Register only 1 player per Computer");
					assist4.sendMessage("Tournament: Register only 1 player per Computer");
					return;
				}
			}
			
			for (L2Effect effect : player.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					player.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist2.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist2.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist3.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist3.stopSkillEffects(effect.getSkill().getId());
			}
			
			ClasseCheck(player);
			
			if (player.duelist_cont > Config.duelist_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.duelist_COUNT_5X5 + " Duelist's or " + Config.duelist_COUNT_5X5 + " Grand Khauatari's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.duelist_COUNT_5X5 + " Duelist's or " + Config.duelist_COUNT_5X5 + " Grand Khauatari'sallowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dreadnought_cont > Config.dreadnought_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.dreadnought_COUNT_5X5 + " Dread Nought's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dreadnought_COUNT_5X5 + " Dread Nought's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.tanker_cont > Config.tanker_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.tanker_COUNT_5X5 + " Tanker's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.tanker_COUNT_5X5 + " Tanker's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dagger_cont > Config.dagger_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.dagger_COUNT_5X5 + " Dagger's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dagger_COUNT_5X5 + " Dagger's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.archer_cont > Config.archer_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.archer_COUNT_5X5 + " Archer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.archer_COUNT_5X5 + " Archer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.bs_cont > Config.bs_COUNT_5X5)
			{
				if (Config.bs_COUNT_5X5 == 0)
				{
					player.sendMessage("Tournament: Bishop's not allowed in 5x5.");
					player.sendPacket(new ExShowScreenMessage("Tournament: Bishop's not allowed in 5x5.", 6 * 1000));
				}
				else
				{
					player.sendMessage("Tournament: Only " + Config.bs_COUNT_5X5 + " Bishop's allowed per party.");
					player.sendPacket(new ExShowScreenMessage("Only " + Config.bs_COUNT_5X5 + " Bishop's allowed per party.", 6 * 1000));
				}
				clean(player);
				return;
			}
			else if (player.archmage_cont > Config.archmage_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.archmage_COUNT_5X5 + " Archmage's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.archmage_COUNT_5X5 + " Archmage's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.soultaker_cont > Config.soultaker_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.soultaker_COUNT_5X5 + " Soultaker's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.soultaker_COUNT_5X5 + " Soultaker's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.mysticMuse_cont > Config.mysticMuse_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.mysticMuse_COUNT_5X5 + " Mystic Muse's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.mysticMuse_COUNT_5X5 + " Mystic Muse's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.stormScreamer_cont > Config.stormScreamer_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.stormScreamer_COUNT_5X5 + " Storm Screamer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.stormScreamer_COUNT_5X5 + " Storm Screamer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.titan_cont > Config.titan_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.titan_COUNT_5X5 + " Titan's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.titan_COUNT_5X5 + " Titan's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dominator_cont > Config.dominator_COUNT_5X5)
			{
				player.sendMessage("Tournament: Only " + Config.dominator_COUNT_5X5 + " Dominator's or " + Config.dominator_COUNT_5X5 + " Doomcryer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dominator_COUNT_5X5 + " Dominator's or " + Config.dominator_COUNT_5X5 + " Doomcryer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (Arena5x5.getInstance().register(player, assist, assist2, assist3, assist4) && (player.getParty().getPartyMembers().get(1) != null && player.getParty().getPartyMembers().get(2) != null && player.getParty().getPartyMembers().get(3) != null && player.getParty().getPartyMembers().get(4) != null))
			{
				player.sendMessage("Tournament: Your participation has been approved.");
				assist.sendMessage("Tournament: Your participation has been approved.");
				assist2.sendMessage("Tournament: Your participation has been approved.");
				assist3.sendMessage("Tournament: Your participation has been approved.");
				assist4.sendMessage("Tournament: Your participation has been approved.");
				
				player.sendPacket(new ExShowScreenMessage("Successfully registered - 5x5", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist.sendPacket(new ExShowScreenMessage("Successfully registered - 5x5", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist2.sendPacket(new ExShowScreenMessage("Successfully registered - 5x5", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist3.sendPacket(new ExShowScreenMessage("Successfully registered - 5x5", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist4.sendPacket(new ExShowScreenMessage("Successfully registered - 5x5", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				
				player.setArenaProtection(true);
				assist.setArenaProtection(true);
				assist2.setArenaProtection(true);
				assist3.setArenaProtection(true);
				assist4.setArenaProtection(true);
				
				player.setArena5x5(true);
				assist.setArena5x5(true);
				assist2.setArena5x5(true);
				assist3.setArena5x5(true);
				clean(player);
				showChatWindow(player);
			}
			else
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			
		}
		else if (command.startsWith("9x9"))
		{
			
			if (!Config.ALLOW_9X9_REGISTER)
			{
				player.sendMessage("[WARN]:The 9x9 is Temporarily unavailable.");
				return;
			}
			
			if (!Config.TOUR_GRADE_A_9X9)
			{
				ItemInstance item;
				for (int i = 1; i < 15; i++)
				{
					item = player.getInventory().getPaperdollItem(i);
					if (item == null)
						continue;
					
					if (item.getItem().getCrystalType() == CrystalType.S)
					{
						player.sendMessage("Tournament: Items (Grade-A)!");
						player.sendPacket(new ExShowScreenMessage("Tournament: Items (Grade-A)!", 5000));
						return;
					}
					
					if (item.getEnchantLevel() > Config.ALT_TOUR_ENCHANT_LIMIT)
					{
						player.sendMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".");
						player.sendPacket(new ExShowScreenMessage("Max enchant allowed in tournament +" + Config.ALT_TOUR_ENCHANT_LIMIT + ".", 5000));
						return;
					}
				}
			}
			
			if (player._active_boxes > 1 && !Config.Allow_Same_HWID_On_Tournament)
			{
				final List<String> players_in_boxes = player.active_boxes_characters;
				
				if (players_in_boxes != null && players_in_boxes.size() > 1)
					for (final String character_name : players_in_boxes)
					{
						final Player ppl = World.getInstance().getPlayer(character_name);
						
						if (ppl != null && ppl.isArenaProtection())
						{
							player.sendMessage("You are already participating in Tournament with another char!");
							return;
						}
					}
			}
			
			if (player.isArena1x1() || player.isArena2x2() || player.isArena5x5() || player.isArena9x9() || player.isArenaProtection())
			{
				player.sendMessage("Tournament: You already registered!");
				return;
			}
			else if (!player.isInParty())
			{
				player.sendMessage("Tournament: You dont have a party.");
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Tournament: You are not the party leader!");
				return;
			}
			else if (player.getParty().getMemberCount() < 9)
			{
				player.sendMessage("Tournament: Your party does not have 9 members.");
				player.sendPacket(new ExShowScreenMessage("Your party does not have 9 members", 6 * 1000));
				return;
			}
			else if (player.getParty().getMemberCount() > 9)
			{
				player.sendMessage("Tournament: Your Party can not have more than 9 members.");
				player.sendPacket(new ExShowScreenMessage("Your Party can not have more than 9 members", 6 * 1000));
				return;
			}
			
			Player assist = player.getParty().getPartyMembers().get(1);
			Player assist2 = player.getParty().getPartyMembers().get(2);
			Player assist3 = player.getParty().getPartyMembers().get(3);
			Player assist4 = player.getParty().getPartyMembers().get(4);
			Player assist5 = player.getParty().getPartyMembers().get(5);
			Player assist6 = player.getParty().getPartyMembers().get(6);
			Player assist7 = player.getParty().getPartyMembers().get(7);
			Player assist8 = player.getParty().getPartyMembers().get(8);
			
			if (player.isCursedWeaponEquipped() || assist.isCursedWeaponEquipped() || assist2.isCursedWeaponEquipped() || assist3.isCursedWeaponEquipped() || assist4.isCursedWeaponEquipped() || assist5.isCursedWeaponEquipped() || assist6.isCursedWeaponEquipped() || assist7.isCursedWeaponEquipped() || assist8.isCursedWeaponEquipped() || player.isInStoreMode() || assist.isInStoreMode() || assist2.isInStoreMode() || assist3.isInStoreMode() || assist4.isInStoreMode() || assist5.isInStoreMode() || assist6.isInStoreMode() || assist7.isInStoreMode() || assist8.isInStoreMode() || player.getKarma() > 0 || assist.getKarma() > 0 || assist2.getKarma() > 0 || assist3.getKarma() > 0 || assist4.getKarma() > 0 || assist5.getKarma() > 0 || assist6.getKarma() > 0 || assist7.getKarma() > 0 || assist8.getKarma() > 0)
			{
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist2.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist3.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist4.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist5.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist6.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist7.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				assist8.sendMessage("Tournament: You or your member does not have the necessary requirements.");
				return;
			}
			else if (OlympiadManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(assist) || OlympiadManager.getInstance().isRegistered(assist2) || OlympiadManager.getInstance().isRegistered(assist3) || OlympiadManager.getInstance().isRegistered(assist4) || OlympiadManager.getInstance().isRegistered(assist5) || OlympiadManager.getInstance().isRegistered(assist6) || OlympiadManager.getInstance().isRegistered(assist7) || OlympiadManager.getInstance().isRegistered(assist8))
			{
				player.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist2.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist3.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist4.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist5.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist6.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist7.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				assist8.sendMessage("Tournament: You or your member is registered in the Olympiad.");
				return;
			}
			else if (player._inEventTvT || assist._inEventTvT || assist2._inEventTvT || assist3._inEventTvT || assist4._inEventTvT || assist5._inEventTvT || assist6._inEventTvT || assist7._inEventTvT || assist8._inEventTvT || player._inEventCTF || assist._inEventCTF || assist2._inEventCTF || assist3._inEventCTF || assist4._inEventCTF || assist5._inEventCTF || assist6._inEventCTF || assist7._inEventCTF || assist8._inEventCTF)
			{
				player.sendMessage("Tournament: You already participated in TvT event!");
				assist.sendMessage("Tournament: You already participated in TvT event!");
				assist2.sendMessage("Tournament: You already participated in TvT event!");
				assist3.sendMessage("Tournament: You already participated in TvT event!");
				assist4.sendMessage("Tournament: You already participated in TvT event!");
				assist5.sendMessage("Tournament: You already participated in TvT event!");
				assist6.sendMessage("Tournament: You already participated in TvT event!");
				assist7.sendMessage("Tournament: You already participated in TvT event!");
				assist8.sendMessage("Tournament: You already participated in TvT event!");
				return;
			}
			
			L2Party plparty = player.getParty();
			for (Player ppl : plparty.getPartyMembers()) {
				if (ppl != null)
					if (ppl.getParty() != null)
						if (ppl.isAio() || ppl.isAioEterno() || AioManager.getInstance().hasAioPrivileges(ppl.getObjectId())) {
							ppl.sendMessage("Aio Buffer You can not attend the event!");
							return;
						}   
			} 
			
			if (!Config.Allow_Same_HWID_On_Tournament)
			{
				if (player.isInParty() && (player.getParty().getPartyMembers().get(1) != null && player.getParty().getPartyMembers().get(2) != null && player.getParty().getPartyMembers().get(3) != null && player.getParty().getPartyMembers().get(4) != null && player.getParty().getPartyMembers().get(5) != null && player.getParty().getPartyMembers().get(6) != null && player.getParty().getPartyMembers().get(7) != null && player.getParty().getPartyMembers().get(8) != null))
				{
					String ip1 = player.getHWID();
					String ip2 = assist.getHWID();
					String ip3 = assist2.getHWID();
					String ip4 = assist3.getHWID();
					String ip5 = assist4.getHWID();
					String ip6 = assist5.getHWID();
					String ip7 = assist6.getHWID();
					String ip8 = assist7.getHWID();
					String ip9 = assist8.getHWID();
					
					if (ip1.equals(ip2) || ip1.equals(ip3) || ip1.equals(ip4) || ip1.equals(ip5) || ip1.equals(ip6) || ip1.equals(ip7) || ip1.equals(ip8) || ip1.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip2.equals(ip1) || ip2.equals(ip3) || ip2.equals(ip4) || ip2.equals(ip5) || ip2.equals(ip6) || ip2.equals(ip7) || ip2.equals(ip8) || ip2.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip3.equals(ip1) || ip3.equals(ip2) || ip3.equals(ip4) || ip3.equals(ip5) || ip3.equals(ip6) || ip3.equals(ip7) || ip3.equals(ip8) || ip3.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip4.equals(ip1) || ip4.equals(ip2) || ip4.equals(ip3) || ip4.equals(ip5) || ip4.equals(ip6) || ip4.equals(ip7) || ip4.equals(ip8) || ip4.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip5.equals(ip1) || ip5.equals(ip2) || ip5.equals(ip3) || ip5.equals(ip4) || ip5.equals(ip6) || ip5.equals(ip7) || ip5.equals(ip8) || ip5.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip6.equals(ip1) || ip6.equals(ip2) || ip6.equals(ip3) || ip6.equals(ip4) || ip6.equals(ip5) || ip6.equals(ip7) || ip6.equals(ip8) || ip6.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip7.equals(ip1) || ip7.equals(ip2) || ip7.equals(ip3) || ip7.equals(ip4) || ip7.equals(ip5) || ip7.equals(ip6) || ip7.equals(ip8) || ip7.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip8.equals(ip1) || ip8.equals(ip2) || ip8.equals(ip3) || ip8.equals(ip4) || ip8.equals(ip5) || ip8.equals(ip6) || ip8.equals(ip7) || ip8.equals(ip9))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					else if (ip9.equals(ip1) || ip9.equals(ip2) || ip9.equals(ip3) || ip9.equals(ip4) || ip9.equals(ip5) || ip9.equals(ip6) || ip9.equals(ip7) || ip9.equals(ip8))
					{
						player.sendMessage("Tournament: Register only 1 player per Computer");
						assist.sendMessage("Tournament: Register only 1 player per Computer");
						assist2.sendMessage("Tournament: Register only 1 player per Computer");
						assist3.sendMessage("Tournament: Register only 1 player per Computer");
						assist4.sendMessage("Tournament: Register only 1 player per Computer");
						assist5.sendMessage("Tournament: Register only 1 player per Computer");
						assist6.sendMessage("Tournament: Register only 1 player per Computer");
						assist7.sendMessage("Tournament: Register only 1 player per Computer");
						assist8.sendMessage("Tournament: Register only 1 player per Computer");
						return;
					}
					
				}
			}
			
			for (L2Effect effect : player.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					player.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist2.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist2.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist3.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist3.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist4.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist4.stopSkillEffects(effect.getSkill().getId());
			}
			
			for (L2Effect effect : assist5.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist5.stopSkillEffects(effect.getSkill().getId());
			}
			for (L2Effect effect : assist6.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist6.stopSkillEffects(effect.getSkill().getId());
			}
			for (L2Effect effect : assist7.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist7.stopSkillEffects(effect.getSkill().getId());
			}
			for (L2Effect effect : assist8.getAllEffects())
			{
				if (Config.ARENA_DISABLE_SKILL_LIST_PERM.contains(effect.getSkill().getId()))
					assist8.stopSkillEffects(effect.getSkill().getId());
			}
			
			ClasseCheck(player);
			
			if (player.duelist_cont > Config.duelist_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.duelist_COUNT_9X9 + " Duelist's or " + Config.duelist_COUNT_9X9 + " Grand Khauatari's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.duelist_COUNT_9X9 + " Duelist's or " + Config.duelist_COUNT_9X9 + " Grand Khauatari's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dreadnought_cont > Config.dreadnought_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.dreadnought_COUNT_9X9 + " Dread Nought's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dreadnought_COUNT_9X9 + " Dread Nought's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.tanker_cont > Config.tanker_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.tanker_COUNT_9X9 + " Tanker's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.tanker_COUNT_9X9 + " Tanker's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dagger_cont > Config.dagger_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.dagger_COUNT_9X9 + " Dagger's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dagger_COUNT_9X9 + " Dagger's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.archer_cont > Config.archer_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.archer_COUNT_9X9 + " Archer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.archer_COUNT_9X9 + " Archer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.bs_cont > Config.bs_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.bs_COUNT_9X9 + " Bishop's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.bs_COUNT_9X9 + " Bishop's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.archmage_cont > Config.archmage_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.archmage_COUNT_9X9 + " Archmage's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.archmage_COUNT_9X9 + " Archmage's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.soultaker_cont > Config.soultaker_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.soultaker_COUNT_9X9 + " Soultaker's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.soultaker_COUNT_9X9 + " Soultaker's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.mysticMuse_cont > Config.mysticMuse_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.mysticMuse_COUNT_9X9 + " Mystic Muse's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.mysticMuse_COUNT_9X9 + " Mystic Muse's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.stormScreamer_cont > Config.stormScreamer_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.stormScreamer_COUNT_9X9 + " Storm Screamer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.stormScreamer_COUNT_9X9 + " Storm Screamer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.titan_cont > Config.titan_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.titan_COUNT_9X9 + " Titan's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.titan_COUNT_9X9 + " Titan's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (player.dominator_cont > Config.dominator_COUNT_9X9)
			{
				player.sendMessage("Tournament: Only " + Config.dominator_COUNT_9X9 + " Dominator's or " + Config.dominator_COUNT_9X9 + " Doomcryer's allowed per party.");
				player.sendPacket(new ExShowScreenMessage("Only " + Config.dominator_COUNT_9X9 + " Dominator's or " + Config.dominator_COUNT_9X9 + " Doomcryer's allowed per party.", 6 * 1000));
				clean(player);
				return;
			}
			else if (Arena9x9.getInstance().register(player, assist, assist2, assist3, assist4, assist5, assist6, assist7, assist8) && (player.getParty().getPartyMembers().get(1) != null && player.getParty().getPartyMembers().get(2) != null && player.getParty().getPartyMembers().get(3) != null && player.getParty().getPartyMembers().get(4) != null && player.getParty().getPartyMembers().get(5) != null && player.getParty().getPartyMembers().get(6) != null && player.getParty().getPartyMembers().get(7) != null && player.getParty().getPartyMembers().get(8) != null))
			{
				player.sendMessage("Tournament: Your participation has been approved.");
				assist.sendMessage("Tournament: Your participation has been approved.");
				assist2.sendMessage("Tournament: Your participation has been approved.");
				assist3.sendMessage("Tournament: Your participation has been approved.");
				assist4.sendMessage("Tournament: Your participation has been approved.");
				assist5.sendMessage("Tournament: Your participation has been approved.");
				assist6.sendMessage("Tournament: Your participation has been approved.");
				assist7.sendMessage("Tournament: Your participation has been approved.");
				assist8.sendMessage("Tournament: Your participation has been approved.");
				
				player.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist2.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist3.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist4.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist5.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist6.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist7.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				assist8.sendPacket(new ExShowScreenMessage("Successfully registered - 9x9", 4000,ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));
				
				player.setArenaProtection(true);
				assist.setArenaProtection(true);
				assist2.setArenaProtection(true);
				assist3.setArenaProtection(true);
				assist4.setArenaProtection(true);
				assist5.setArenaProtection(true);
				assist6.setArenaProtection(true);
				assist7.setArenaProtection(true);
				assist8.setArenaProtection(true);
				
				player.setArena9x9(true);
				assist.setArena9x9(true);
				assist2.setArena9x9(true);
				assist3.setArena9x9(true);
				assist4.setArena9x9(true);
				assist5.setArena9x9(true);
				assist6.setArena9x9(true);
				assist7.setArena9x9(true);
				assist8.setArena9x9(true);
				clean(player);
				showChatWindow(player);
				
			}
			else
				player.sendMessage("Tournament: You or your member does not have the necessary requirements.");
			
		}
		else if (command.startsWith("remove"))
		{
			if (player.isArena1x1()){
				Arena1x1.getInstance().remove(player);
			}
			else if (!player.isInParty())
			{
				player.sendMessage("Tournament: You dont have a party.");
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Tournament: You are not the party leader!");
				return;
			}
			if (player.isArena2x2())
				Arena2x2.getInstance().remove(player);
			if (player.isArena5x5())
				Arena5x5.getInstance().remove(player);
			if (player.isArena9x9())
				Arena9x9.getInstance().remove(player);
		}
		else if (command.startsWith("observe_list"))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			String filename = "data/html/mods/tournament/observer.htm";
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(filename);
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if (command.startsWith("back"))
			showChatWindow(player);
		
		else if (command.startsWith("tournament_observe"))
		{
			if (player._inEventTvT || player._inEventCTF)
			{
				player.sendMessage("Tournament: Remove your participation from the event!");
				return;
			}
			
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			final int x = Integer.parseInt(st.nextToken());
			final int y = Integer.parseInt(st.nextToken());
			final int z = Integer.parseInt(st.nextToken());
			// if ((x == -114413 && y == -213241 && z == -3331) || (x == -81748 && y == -245950 && z == -3331) || (x == -120324 && y == -225077 && z == -3331))
			// {
			player.setArenaObserv(true);
			player.enterTvTObserverMode(x, y, z);
			// }
		}	
		else
			super.onBypassFeedback(player, command);
	}
	
	public void ClasseCheck(Player activeChar)
	{
		
		L2Party plparty = activeChar.getParty();
		for (Player player : plparty.getPartyMembers())
		{/* Membros da party for */
			if (player != null)
			{
				if (player.getParty() != null)
				{
					if (player.getClassId() == ClassId.GLADIATOR || player.getClassId() == ClassId.DUELIST || player.getClassId() == ClassId.GRAND_KHAVATARI || player.getClassId() == ClassId.TYRANT)
						activeChar.duelist_cont = activeChar.duelist_cont + 1;
					
					if (player.getClassId() == ClassId.WARLORD || player.getClassId() == ClassId.DREADNOUGHT)
						activeChar.dreadnought_cont = activeChar.dreadnought_cont + 1;
					
					if (player.getClassId() == ClassId.PALADIN || player.getClassId() == ClassId.PHOENIX_KNIGHT || player.getClassId() == ClassId.DARK_AVENGER || player.getClassId() == ClassId.HELL_KNIGHT || player.getClassId() == ClassId.EVAS_TEMPLAR || player.getClassId() == ClassId.TEMPLE_KNIGHT || player.getClassId() == ClassId.SHILLIEN_KNIGHT || player.getClassId() == ClassId.SHILLIEN_TEMPLAR)
						activeChar.tanker_cont = activeChar.tanker_cont + 1;
					
					if (player.getClassId() == ClassId.ADVENTURER || player.getClassId() == ClassId.TREASURE_HUNTER || player.getClassId() == ClassId.WIND_RIDER || player.getClassId() == ClassId.PLAINS_WALKER || player.getClassId() == ClassId.GHOST_HUNTER || player.getClassId() == ClassId.ABYSS_WALKER)
						activeChar.dagger_cont = activeChar.dagger_cont + 1;
					
					if (player.getClassId() == ClassId.HAWKEYE || player.getClassId() == ClassId.SAGGITARIUS || player.getClassId() == ClassId.MOONLIGHT_SENTINEL || player.getClassId() == ClassId.SILVER_RANGER || player.getClassId() == ClassId.GHOST_SENTINEL || player.getClassId() == ClassId.PHANTOM_RANGER)
						activeChar.archer_cont = activeChar.archer_cont + 1;
					
					if (player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT)
						activeChar.bs_cont = activeChar.bs_cont + 1;
					
					if (player.getClassId() == ClassId.ARCHMAGE || player.getClassId() == ClassId.SORCERER)
						activeChar.archmage_cont = activeChar.archmage_cont + 1;
					
					if (player.getClassId() == ClassId.SOULTAKER || player.getClassId() == ClassId.NECROMANCER)
						activeChar.soultaker_cont = activeChar.soultaker_cont + 1;
					
					if (player.getClassId() == ClassId.MYSTIC_MUSE || player.getClassId() == ClassId.SPELLSINGER)
						activeChar.mysticMuse_cont = activeChar.mysticMuse_cont + 1;
					
					if (player.getClassId() == ClassId.STORM_SCREAMER || player.getClassId() == ClassId.SPELLHOWLER)
						activeChar.stormScreamer_cont = activeChar.stormScreamer_cont + 1;
					
					if (player.getClassId() == ClassId.TITAN || player.getClassId() == ClassId.DESTROYER)
						activeChar.titan_cont = activeChar.titan_cont + 1;
					
					if (player.getClassId() == ClassId.DOMINATOR || player.getClassId() == ClassId.OVERLORD || player.getClassId() == ClassId.DOOMCRYER || player.getClassId() == ClassId.WARCRYER)
						activeChar.dominator_cont = activeChar.dominator_cont + 1;
					
				}
			}
		}
		
	}
	
	public void clean(Player player)
	{
		player.duelist_cont = 0;
		player.dreadnought_cont = 0;
		player.tanker_cont = 0;
		player.dagger_cont = 0;
		player.archer_cont = 0;
		player.bs_cont = 0;
		player.archmage_cont = 0;
		player.soultaker_cont = 0;
		player.mysticMuse_cont = 0;
		player.stormScreamer_cont = 0;
		player.titan_cont = 0;
		player.dominator_cont = 0;
	}
	
}