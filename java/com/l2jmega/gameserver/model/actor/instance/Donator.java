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
package com.l2jmega.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.lang.StringUtil;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminEditChar;
import com.l2jmega.gameserver.instancemanager.HeroManager;
import com.l2jmega.gameserver.instancemanager.VipManager;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.olympiad.Olympiad;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.HennaInfo;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.templates.StatsSet;

/**
 * @author Baggos
 */
public class Donator extends Folk
{
	public Donator(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player._vip_days = 0;
		player._hero_days = 0;
		player._class_id = 0;
		player._sex_id = 0;
		player._change_Name = "";
		player.sendPacket(ActionFailed.STATIC_PACKET);
		if (Config.DONATESYSTEM){
		String filename = "data/html/mods/donate/index.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		}else{
			String filename = "data/html/mods/donate/disabled.htm";
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(filename);
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
	}
	
	public void Buy_Vip(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/vip.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		
		if (player._vip_days == 0)
			html.replace("%coin%", "0");
		else if (player._vip_days == 30)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_30_DAYS_PRICE + "</font>");
		else if (player._vip_days == 60)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_60_DAYS_PRICE + "</font>");
		else if (player._vip_days == 90)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_90_DAYS_PRICE + "</font>");
		else if (player._vip_days >= 360)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.VIP_ETERNAL_PRICE + "</font>");
		
		if (player._vip_days == 0)
			html.replace("%vip_days%", "0");
		else if (player._vip_days == 30)
			html.replace("%vip_days%", "<font color=\"00FF00\">30</font> Days");
		else if (player._vip_days == 60)
			html.replace("%vip_days%", "<font color=\"00FF00\">60</font> Days");
		else if (player._vip_days == 90)
			html.replace("%vip_days%", "<font color=\"00FF00\">90</font> Days");
		else if (player._vip_days >= 360)
			html.replace("%vip_days%", "<font color=\"00FF00\">Eternal</font>");
		
		player.sendPacket(html);
	}
	
	public void Buy_Hero(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/hero.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		
		if (player._hero_days == 0)
			html.replace("%coin%", "0");
		else if (player._hero_days == 30)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_30_DAYS_PRICE + "</font>");
		else if (player._hero_days == 60)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_60_DAYS_PRICE + "</font>");
		else if (player._hero_days == 90)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_90_DAYS_PRICE + "</font>");
		else if (player._hero_days >= 360)
			html.replace("%coin%", "<font color=\"00FF00\">" + Config.HERO_ETERNAL_PRICE + "</font>");
		
		if (player._hero_days == 0)
			html.replace("%hero_days%", "0");
		else if (player._hero_days == 30)
			html.replace("%hero_days%", "<font color=\"00FF00\">30</font> Days");
		else if (player._hero_days == 60)
			html.replace("%hero_days%", "<font color=\"00FF00\">60</font> Days");
		else if (player._hero_days == 90)
			html.replace("%hero_days%", "<font color=\"00FF00\">90</font> Days");
		else if (player._hero_days >= 360)
			html.replace("%hero_days%", "<font color=\"00FF00\">Eternal</font>");
		
		player.sendPacket(html);
	}
	
	public void class_select(Player player)
	{
		player._class_id = 0;
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/class_select.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	public void class_finish(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/class_finish.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_CLASS_PRICE + "</font>");
		
		if (player._class_id == 1)
			html.replace("%class_name%", "Duelist");
		else if (player._class_id == 2)
			html.replace("%class_name%", "Dreadnought");
		else if (player._class_id == 3)
			html.replace("%class_name%", "Phoenix Knight");
		else if (player._class_id == 4)
			html.replace("%class_name%", "Hell Knight");
		else if (player._class_id == 5)
			html.replace("%class_name%", "Sagittarius");
		else if (player._class_id == 6)
			html.replace("%class_name%", "Adventurer");
		else if (player._class_id == 7)
			html.replace("%class_name%", "Archmage");
		else if (player._class_id == 8)
			html.replace("%class_name%", "Soultaker");
		else if (player._class_id == 9)
			html.replace("%class_name%", "Arcana Lord");
		else if (player._class_id == 10)
			html.replace("%class_name%", "Cardinal");
		else if (player._class_id == 11)
			html.replace("%class_name%", "Hierophant");
		else if (player._class_id == 12)
			html.replace("%class_name%", "Eva's Templar");
		else if (player._class_id == 13)
			html.replace("%class_name%", "Sword Muse");
		else if (player._class_id == 14)
			html.replace("%class_name%", "Wind Rider");
		else if (player._class_id == 15)
			html.replace("%class_name%", "Moonlight Sentinel");
		else if (player._class_id == 16)
			html.replace("%class_name%", "Mystic Muse");
		else if (player._class_id == 17)
			html.replace("%class_name%", "Elemental Master");
		else if (player._class_id == 18)
			html.replace("%class_name%", "Eva's Saint");
		else if (player._class_id == 19)
			html.replace("%class_name%", "Shillien Templar");
		else if (player._class_id == 20)
			html.replace("%class_name%", "Spectral Dancer");
		else if (player._class_id == 21)
			html.replace("%class_name%", "Ghost Hunter");
		else if (player._class_id == 22)
			html.replace("%class_name%", "Ghost Sentinel");
		else if (player._class_id == 23)
			html.replace("%class_name%", "Storm Screamer");
		else if (player._class_id == 24)
			html.replace("%class_name%", "Spectral Master");
		else if (player._class_id == 25)
			html.replace("%class_name%", "Shillien Saint");
		else if (player._class_id == 26)
			html.replace("%class_name%", "Titan");
		else if (player._class_id == 27)
			html.replace("%class_name%", "Grand Khavatari");
		else if (player._class_id == 28)
			html.replace("%class_name%", "Dominator");
		else if (player._class_id == 29)
			html.replace("%class_name%", "Doomcryer");
		else if (player._class_id == 30)
			html.replace("%class_name%", "Fortune Seeker");
		else if (player._class_id == 31)
			html.replace("%class_name%", "Maestro");
		
		if (player._class_id >= 1 && player._class_id <= 6)
			html.replace("%race%", "Human Fighter");
		else if (player._class_id >= 7 && player._class_id <= 11)
			html.replace("%race%", "Human Mystic");
		else if (player._class_id >= 12 && player._class_id <= 15)
			html.replace("%race%", "Elf Fighter");
		else if (player._class_id >= 16 && player._class_id <= 18)
			html.replace("%race%", "Elf Mystic");
		else if (player._class_id >= 19 && player._class_id <= 22)
			html.replace("%race%", "Dark Elf Fighter");
		else if (player._class_id >= 23 && player._class_id <= 25)
			html.replace("%race%", "Dark Elf Mystic");
		else if (player._class_id >= 26 && player._class_id <= 27)
			html.replace("%race%", "Orc Fighter");
		else if (player._class_id >= 28 && player._class_id <= 29)
			html.replace("%race%", "Orc Mystic");
		else if (player._class_id >= 30 && player._class_id <= 31)
			html.replace("%race%", "Dwarf Fighter");
		
		player.sendPacket(html);
	}
	
	public void name_select(Player player)
	{
		player._change_Name = "";
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/name_select.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%name%", player.getName());
		player.sendPacket(html);
	}
	
	public void name_finish(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/name_finish.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%old_name%", player.getName());
		html.replace("%new_name%", player._change_Name);
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_NAME_PRICE + "</font>");
		player.sendPacket(html);
	}
	
	public void sex_select(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/sex.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		
		if (player.getAppearance().getSex() == Sex.MALE)
			html.replace("%old_sex%", "Male / Masculino");
		else
			html.replace("%old_sex%", "Female / Feminino");
		
		if (player._sex_id == 1)
			html.replace("%new_sex%", "Female / Feminino");
		else
			html.replace("%new_sex%", "Male / Masculino");
		
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_SEX_PRICE + "</font>");
		player.sendPacket(html);
	}
	
	public void Incorrect_item(Player player)
	{
		player._vip_days = 0;
		player._hero_days = 0;
		player._class_id = 0;
		player._sex_id = 0;
		player._change_Name = "";
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/incorrect.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("sex"))
		{
			Sex male = Sex.MALE;
			Sex female = Sex.FEMALE;
			
			if (player.getAppearance().getSex() == male)
				player._sex_id = 1;
			else if (player.getAppearance().getSex() == female)
				player._sex_id = 2;
			
			sex_select(player);
			
		}
		else if (command.startsWith("setsex"))
		{
			if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_NAME_PRICE, null, true))
			{
				Sex male = Sex.MALE;
				Sex female = Sex.FEMALE;
				
				if (player.getAppearance().getSex() == male)
				{
					player.getAppearance().setSex(female);
					player.sendPacket(new ExShowScreenMessage("Congratulations. Your Sex has been changed.", 6000, 0x02, true));
					player.broadcastUserInfo();
					player.decayMe();
					player.spawnMe();
				}
				else if (player.getAppearance().getSex() == female)
				{
					player.getAppearance().setSex(male);
					player.sendPacket(new ExShowScreenMessage("Congratulations. Your Sex has been changed.", 6000, 0x02, true));
					player.broadcastUserInfo();
					player.decayMe();
					player.spawnMe();
				}
				
				ThreadPool.schedule(new Runnable()
				{
					
					@Override
					public void run()
					{
						player.logout(true);
					}
				}, 3000);
			}
			else
				Incorrect_item(player);
			
		}
		else if (command.startsWith("name_select"))
			name_select(player);
		else if (command.startsWith("name"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String name = st.nextToken();
				
				player._change_Name = name;
				
				if (player._change_Name.length() > 16)
				{
					player.sendMessage("The chosen name cannot exceed 16 characters in length.");
					showChatWindow(player);
					return;
				}
				else if (player._change_Name.length() < 3)
				{
					player.sendMessage("Your name can not be mention that 3 characters in length.");
					showChatWindow(player);
					return;
				}
				else if (!StringUtil.isValidPlayerName(player._change_Name))
				{
					player.sendMessage("The new name doesn't fit with the regex pattern.");
					showChatWindow(player);
					return;
				}
				else if (PlayerNameTable.getInstance().getPlayerObjectId(player._change_Name) > 0)
				{
					player.sendMessage("The chosen name already exists.");
					showChatWindow(player);
					return;
				}
				
				name_finish(player);
			}
			catch (Exception e)
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setname"))
		{
			if (player._change_Name.length() > 16)
			{
				player.sendMessage("The chosen name cannot exceed 16 characters in length.");
				showChatWindow(player);
				return;
			}
			else if (player._change_Name.length() < 3)
			{
				player.sendMessage("Your name can not be mention that 3 characters in length.");
				showChatWindow(player);
				return;
			}
			else if (!StringUtil.isValidPlayerName(player._change_Name))
			{
				player.sendMessage("The new name doesn't fit with the regex pattern.");
				showChatWindow(player);
				return;
			}
			else if (PlayerNameTable.getInstance().getPlayerObjectId(player._change_Name) > 0)
			{
				player.sendMessage("The chosen name already exists.");
				showChatWindow(player);
				return;
			}
			
			if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_NAME_PRICE, null, true))
			{
				player.setName(player._change_Name);
				PlayerNameTable.getInstance().updatePlayerData(player, false);
				player.sendPacket(new ExShowScreenMessage("Congratulations. Your name has been changed.", 6000, 0x02, true));
				player.broadcastUserInfo();
				player.store();
				player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			}
			else
				Incorrect_item(player);
			
		}
		else if (command.startsWith("class_select"))
			class_select(player);
		else if (command.startsWith("class"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				player._class_id = Integer.parseInt(type);
				class_finish(player);
			}
			catch (Exception e)
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setclass"))
		{
			if (player.getBaseClass() != player.getClassId().getId())
			{
				player.sendMessage("You is not with its base class.");
				player.sendPacket(new ExShowScreenMessage("You is not with its base class.", 6000, 0x02, true));
				return;
			}
			else if (player.isInOlympiadMode())
			{
				player.sendMessage("This Item Cannot Be Used On Olympiad Games.");
				return;
			}
			else if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_CLASS_PRICE, null, true))
			{
				String nameclasse = player.getTemplate().getClassName();
				if (player._class_id == 1)
				{
					if (player.getClassId().getId() == 88)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(88);
					
					if (!player.isSubClassActive())
						player.setBaseClass(88);
					
					Finish(player);
				}
				else if (player._class_id == 2)
				{
					if (player.getClassId().getId() == 89)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(89);
					
					if (!player.isSubClassActive())
						player.setBaseClass(89);
					
					Finish(player);
				}
				else if (player._class_id == 3)
				{
					if (player.getClassId().getId() == 90)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(90);
					
					if (!player.isSubClassActive())
						player.setBaseClass(90);
					
					Finish(player);
				}
				else if (player._class_id == 4)
				{
					if (player.getClassId().getId() == 91)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(91);
					
					if (!player.isSubClassActive())
						player.setBaseClass(91);
					
					Finish(player);
				}
				else if (player._class_id == 5)
				{
					if (player.getClassId().getId() == 92)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(92);
					
					if (!player.isSubClassActive())
						player.setBaseClass(92);
					
					Finish(player);
				}
				else if (player._class_id == 6)
				{
					if (player.getClassId().getId() == 93)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(93);
					
					if (!player.isSubClassActive())
						player.setBaseClass(93);
					
					Finish(player);
				}
				else if (player._class_id == 7)
				{
					if (player.getClassId().getId() == 94)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(94);
					
					if (!player.isSubClassActive())
						player.setBaseClass(94);
					
					Finish(player);
				}
				else if (player._class_id == 8)
				{
					if (player.getClassId().getId() == 95)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(95);
					
					if (!player.isSubClassActive())
						player.setBaseClass(95);
					
					Finish(player);
				}
				else if (player._class_id == 9)
				{
					if (player.getClassId().getId() == 96)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(96);
					
					if (!player.isSubClassActive())
						player.setBaseClass(96);
					
					Finish(player);
				}
				else if (player._class_id == 10)
				{
					if (player.getClassId().getId() == 97)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(97);
					
					if (!player.isSubClassActive())
						player.setBaseClass(97);
					
					Finish(player);
				}
				else if (player._class_id == 11)
				{
					if (player.getClassId().getId() == 98)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(98);
					
					if (!player.isSubClassActive())
						player.setBaseClass(98);
					
					Finish(player);
				}
				else if (player._class_id == 12)
				{
					if (player.getClassId().getId() == 99)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(99);
					
					if (!player.isSubClassActive())
						player.setBaseClass(99);
					
					Finish(player);
				}
				else if (player._class_id == 13)
				{
					if (player.getClassId().getId() == 100)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(100);
					
					if (!player.isSubClassActive())
						player.setBaseClass(100);
					
					Finish(player);
				}
				else if (player._class_id == 14)
				{
					if (player.getClassId().getId() == 101)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(101);
					
					if (!player.isSubClassActive())
						player.setBaseClass(101);
					
					Finish(player);
				}
				else if (player._class_id == 15)
				{
					if (player.getClassId().getId() == 102)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(102);
					
					if (!player.isSubClassActive())
						player.setBaseClass(102);
					
					Finish(player);
				}
				else if (player._class_id == 16)
				{
					if (player.getClassId().getId() == 103)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(103);
					if (!player.isSubClassActive())
						player.setBaseClass(103);
					
					Finish(player);
				}
				else if (player._class_id == 17)
				{
					if (player.getClassId().getId() == 104)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(104);
					
					if (!player.isSubClassActive())
						player.setBaseClass(104);
					
					Finish(player);
				}
				else if (player._class_id == 18)
				{
					if (player.getClassId().getId() == 105)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(105);
					
					if (!player.isSubClassActive())
						player.setBaseClass(105);
					
					Finish(player);
				}
				else if (player._class_id == 19)
				{
					if (player.getClassId().getId() == 106)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(106);
					
					if (!player.isSubClassActive())
						player.setBaseClass(106);
					
					Finish(player);
				}
				else if (player._class_id == 20)
				{
					if (player.getClassId().getId() == 107)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(107);
					
					if (!player.isSubClassActive())
						player.setBaseClass(107);
					
					Finish(player);
				}
				else if (player._class_id == 21)
				{
					if (player.getClassId().getId() == 108)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(108);
					
					if (!player.isSubClassActive())
						player.setBaseClass(108);
					
					Finish(player);
				}
				else if (player._class_id == 22)
				{
					if (player.getClassId().getId() == 109)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(109);
					
					if (!player.isSubClassActive())
						player.setBaseClass(109);
					
					Finish(player);
				}
				else if (player._class_id == 23)
				{
					if (player.getClassId().getId() == 110)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(110);
					
					if (!player.isSubClassActive())
						player.setBaseClass(110);
					
					Finish(player);
				}
				else if (player._class_id == 24)
				{
					if (player.getClassId().getId() == 111)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(111);
					
					if (!player.isSubClassActive())
						player.setBaseClass(111);
					
					Finish(player);
				}
				else if (player._class_id == 25)
				{
					if (player.getClassId().getId() == 112)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(112);
					
					if (!player.isSubClassActive())
						player.setBaseClass(112);
					
					Finish(player);
				}
				else if (player._class_id == 26)
				{
					if (player.getClassId().getId() == 113)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(113);
					
					if (!player.isSubClassActive())
						player.setBaseClass(113);
					
					Finish(player);
				}
				else if (player._class_id == 27)
				{
					if (player.getClassId().getId() == 114)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(114);
					
					if (!player.isSubClassActive())
						player.setBaseClass(114);
					
					Finish(player);
				}
				else if (player._class_id == 28)
				{
					if (player.getClassId().getId() == 115)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(115);
					
					if (!player.isSubClassActive())
						player.setBaseClass(115);
					
					Finish(player);
				}
				else if (player._class_id == 29)
				{
					if (player.getClassId().getId() == 116)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(116);
					
					if (!player.isSubClassActive())
						player.setBaseClass(116);
					
					Finish(player);
				}
				else if (player._class_id == 30)
				{
					if (player.getClassId().getId() == 117)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(117);
					
					if (!player.isSubClassActive())
						player.setBaseClass(117);
					
					Finish(player);
				}
				else if (player._class_id == 31)
				{
					if (player.getClassId().getId() == 118)
					{
						player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return;
					}
					
					for (L2Skill skill : player.getSkills().values())
						player.removeSkill(skill);
					
					player.setClassId(118);
					
					if (!player.isSubClassActive())
						player.setBaseClass(118);
					
					Finish(player);
				}
				else
					player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
				
			}
			else
				Incorrect_item(player);
			
		}
		else if (command.startsWith("vip"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "30_Days":
						player._vip_days = 30;
						Buy_Vip(player);
						break;
					case "60_Days":
						player._vip_days = 60;
						Buy_Vip(player);
						break;
					case "90_Days":
						player._vip_days = 90;
						Buy_Vip(player);
						break;
					case "Eternal":
						player._vip_days = 999;
						Buy_Vip(player);
						break;
					default:
						player.sendMessage("Please select another value!");
						showChatWindow(player);
						break;
				
				}
			}
			catch (Exception e)
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setvip"))
		{
			if (player._vip_days == 30)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_30_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._vip_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().updateVip(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._vip_days >= 30)
						{
							while (player._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._vip_days -= 30;
							}
						}
						
						if (player._vip_days < 30 && player._vip_days > 0)
						{
							while (player._vip_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._vip_days == 60)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_60_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._vip_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().updateVip(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._vip_days >= 30)
						{
							while (player._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._vip_days -= 30;
							}
						}
						
						if (player._vip_days < 30 && player._vip_days > 0)
						{
							while (player._vip_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._vip_days == 90)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_90_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._vip_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().updateVip(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._vip_days >= 30)
						{
							while (player._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._vip_days -= 30;
							}
						}
						
						if (player._vip_days < 30 && player._vip_days > 0)
						{
							while (player._vip_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._vip_days == 999)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_ETERNAL_PRICE, null, true))
				{
					
					if (VipManager.getInstance().hasVipPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._vip_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().updateVip(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._vip_days >= 30)
						{
							while (player._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._vip_days -= 30;
							}
						}
						
						if (player._vip_days < 30 && player._vip_days > 0)
						{
							while (player._vip_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(player.getObjectId(), end_day);
					}
					
					player.sendPacket(new ExShowScreenMessage("Congratulations! You have activated Eternal VIP.", 10000));
					player.sendMessage("Congratulations! You have activated Eternal VIP.");
				}
				else
					Incorrect_item(player);
				
			}
			else
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
			
		}
		if (command.startsWith("hero"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "30_Days":
						player._hero_days = 30;
						Buy_Hero(player);
						break;
					case "60_Days":
						player._hero_days = 60;
						Buy_Hero(player);
						break;
					case "90_Days":
						player._hero_days = 90;
						Buy_Hero(player);
						break;
					case "Eternal":
						player._hero_days = 999;
						Buy_Hero(player);
						break;
					default:
						player.sendMessage("Please select another value!");
						showChatWindow(player);
						break;
				
				}
			}
			catch (Exception e)
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("sethero"))
		{
			if (player._hero_days == 30)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_30_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._hero_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().updateHero(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._hero_days >= 30)
						{
							while (player._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._hero_days -= 30;
							}
						}
						
						if (player._hero_days < 30 && player._hero_days > 0)
						{
							while (player._hero_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._hero_days == 60)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_60_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._hero_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().updateHero(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._hero_days >= 30)
						{
							while (player._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._hero_days -= 30;
							}
						}
						
						if (player._hero_days < 30 && player._hero_days > 0)
						{
							while (player._hero_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._hero_days == 90)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_90_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._hero_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().updateHero(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._hero_days >= 30)
						{
							while (player._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._hero_days -= 30;
							}
						}
						
						if (player._hero_days < 30 && player._hero_days > 0)
						{
							while (player._hero_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(player.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						player.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						player.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(player);
				
			}
			else if (player._hero_days == 999)
			{
				if (player.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_ETERNAL_PRICE, null, true))
				{
					
					if (HeroManager.getInstance().hasHeroPrivileges(player.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(player.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + player._hero_days + 1;
						
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (_daysleft >= 30)
						{
							while (_daysleft >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								_daysleft -= 30;
							}
						}
						
						if (_daysleft < 30 && _daysleft > 0)
						{
							while (_daysleft > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								_daysleft--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().updateHero(player.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (player._hero_days >= 30)
						{
							while (player._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								player._hero_days -= 30;
							}
						}
						
						if (player._hero_days < 30 && player._hero_days > 0)
						{
							while (player._hero_days > 0)
							{
								if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
									calendar.roll(Calendar.MONTH, true);
								if (calendar.get(Calendar.DATE) == 30)
								{
									if (calendar.get(Calendar.MONTH) == 11)
										calendar.roll(Calendar.YEAR, true);
									calendar.roll(Calendar.MONTH, true);
									
								}
								calendar.roll(Calendar.DATE, true);
								player._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(player.getObjectId(), end_day);
					}
					
					player.sendPacket(new ExShowScreenMessage("Congratulations! You have activated Eternal Hero.", 10000));
					player.sendMessage("Congratulations! You have activated Eternal Hero.");
				}
				else
					Incorrect_item(player);
				
			}
			else
			{
				player.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
			
		}
		else if (command.startsWith("back_home"))
			showChatWindow(player);
		
	}
	
	public static void Finish(Player activeChar)
	{
		String newclass = activeChar.getTemplate().getClassName();
		
		activeChar.sendMessage(activeChar.getName() + " is now a " + newclass + ".");
		activeChar.sendPacket(new ExShowScreenMessage("Congratulations. You is now a " + newclass + ".", 6000, 0x02, true));
		
		activeChar.refreshOverloaded();
		activeChar.store();
		activeChar.sendPacket(new HennaInfo(activeChar));
		activeChar.sendSkillList();
		activeChar.broadcastUserInfo();
		
		activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
		
		if (activeChar.isNoble())
		{
			StatsSet playerStat = Olympiad.getNobleStats(activeChar.getObjectId());
			if (!(playerStat == null))
			{
				AdminEditChar.updateClasse(activeChar);
				AdminEditChar.DeleteHero(activeChar);
				activeChar.sendMessage("You now has " + Olympiad.getInstance().getNoblePoints(activeChar.getObjectId()) + " Olympiad points.");
			}
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// Remove all henna info stored for this sub-class.
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, activeChar.getObjectId());
			statement.setInt(2, 0);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Class Item: " + e);
		}
		
		ThreadPool.schedule(new Runnable()
		{
			
			@Override
			public void run()
			{
				activeChar.logout(true);
			}
		}, 3000);
	}
	
}
