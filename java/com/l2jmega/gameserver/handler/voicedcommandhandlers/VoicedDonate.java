package com.l2jmega.gameserver.handler.voicedcommandhandlers;

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
import com.l2jmega.gameserver.data.xml.MultisellData;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminEditChar;
import com.l2jmega.gameserver.instancemanager.HeroManager;
import com.l2jmega.gameserver.instancemanager.VipManager;
import com.l2jmega.gameserver.model.L2Augmentation;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.olympiad.Olympiad;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.HennaInfo;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.OpenUrl;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.templates.StatsSet;

public class VoicedDonate implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"multisell",
		"exc_multisell",
		"donate",
		"sex_",
		"setsex_",
		"name_select",
		"name_",
		"setname_",
		"class_select",
		"class_",
		"setclass_",
		"vip_",
		"setvip_",
		"noble_",
		"setnoble_",
		"hero_",
		"sethero_",
		"back_home",
		"add_int",
		"add_str",
		"add_men",
		"add_con",
		"arg_status",
		"arg_skills",
		"add_arg",
		"donate_url"
	
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("donate"))
		{
			if (Config.DONATESYSTEM)
				showMainHtml(activeChar);
			else
				Disabled(activeChar);
		}
		else if (command.startsWith("noble_"))
		{
			showNobleHtml(activeChar);
		}
		else if (command.startsWith("setnoble_"))
		{
			
			if (activeChar.isNoble())
			{
				activeChar.sendMessage("SYS: Voce ja esta com status Noble.");
				return false;
			}
			
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_NOBLE_PRICE, null, true))
				{
					activeChar.setNoble(true, true);
				}
				else
					Incorrect_item(activeChar);
		}
	    if (command.startsWith("donate_url")) {
	        activeChar.sendPacket(new OpenUrl("" + Config.DONATE_URL + ""));
	     }
		if (command.startsWith("arg_status"))
		{
			if (Config.DONATESYSTEM)
				showMainStatusHtml(activeChar);
			else
				Disabled(activeChar);
		}
		if (command.startsWith("arg_skills"))
		{
			if (Config.DONATESYSTEM)
				showMainSkillsHtml(activeChar);
			else
				Disabled(activeChar);
		}
	    else if (command.startsWith("add_int"))
	    {	    	
	        add(activeChar, 1);
	    }
	    else if (command.startsWith("add_str"))
	    {
	        add(activeChar, 2);	      
	    }
	    else if (command.startsWith("add_men"))
	    {
	        add(activeChar, 3);
	    }
	    else if (command.startsWith("add_con"))
	    {
	        add(activeChar, 4);
	    }
		else if (command.startsWith("add_arg"))
		{

				StringTokenizer st1 = new StringTokenizer(command);
				command = st1.nextToken();
				
				final int id = Integer.parseInt(st1.nextToken());
				
				int count = 1;
				
				if (st1.hasMoreTokens())
				{
					count = Integer.parseInt(st1.nextToken());
				}
				
				addargument(activeChar, id, count);
	
		}
		else if (command.startsWith("multisell"))
		{
			try
			{
				activeChar.setIsUsingCMultisell(true);
				MultisellData.getInstance().separateAndSend(command.substring(9).trim(), activeChar, null, false);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("This list does not exist.");
			}
		}
		else if (command.startsWith("exc_multisell"))
		{
			try
			{
				activeChar.setIsUsingCMultisell(true);
				MultisellData.getInstance().separateAndSend(command.substring(13).trim(), activeChar, null, true);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("This list does not exist.");
			}
		}
		else if (command.startsWith("sex_"))
		{
			Sex male = Sex.MALE;
			Sex female = Sex.FEMALE;
			
			if (activeChar.getAppearance().getSex() == male)
				activeChar._sex_id = 1;
			else if (activeChar.getAppearance().getSex() == female)
				activeChar._sex_id = 2;
			
			sex_select(activeChar);
			
		}
		else if (command.startsWith("setsex_"))
		{
			if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_SEX_PRICE, null, true))
			{
				Sex male = Sex.MALE;
				Sex female = Sex.FEMALE;
				
				if (activeChar.getAppearance().getSex() == male)
				{
					activeChar.getAppearance().setSex(female);
					activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your Sex has been changed.", 6000, 0x02, true));
					activeChar.broadcastUserInfo();
					activeChar.decayMe();
					activeChar.spawnMe();
				}
				else if (activeChar.getAppearance().getSex() == female)
				{
					activeChar.getAppearance().setSex(male);
					activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your Sex has been changed.", 6000, 0x02, true));
					activeChar.broadcastUserInfo();
					activeChar.decayMe();
					activeChar.spawnMe();
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
			else
				Incorrect_item(activeChar);
			
		}
		else if (command.startsWith("name_select"))
			name_select(activeChar);
		else if (command.startsWith("name_"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String name = st.nextToken();
				
				activeChar._change_Name = name;
				
				if (activeChar._change_Name.length() > 16)
				{
					activeChar.sendMessage("The chosen name cannot exceed 16 characters in length.");
					showMainHtml(activeChar);
					return false;
				}
				else if (activeChar._change_Name.length() < 3)
				{
					activeChar.sendMessage("Your name can not be mention that 3 characters in length.");
					showMainHtml(activeChar);
					return false;
				}
				else if (!StringUtil.isValidPlayerName(activeChar._change_Name))
				{
					activeChar.sendMessage("The new name doesn't fit with the regex pattern.");
					showMainHtml(activeChar);
					return false;
				}
				else if (PlayerNameTable.getInstance().getPlayerObjectId(activeChar._change_Name) > 0)
				{
					activeChar.sendMessage("The chosen name already exists.");
					showMainHtml(activeChar);
					return false;
				}
				
				name_finish(activeChar);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setname_"))
		{
			if (activeChar._change_Name.length() > 16)
			{
				activeChar.sendMessage("The chosen name cannot exceed 16 characters in length.");
				showMainHtml(activeChar);
				return false;
			}
			else if (activeChar._change_Name.length() < 3)
			{
				activeChar.sendMessage("Your name can not be mention that 3 characters in length.");
				showMainHtml(activeChar);
				return false;
			}
			else if (!StringUtil.isValidPlayerName(activeChar._change_Name))
			{
				activeChar.sendMessage("The new name doesn't fit with the regex pattern.");
				showMainHtml(activeChar);
				return false;
			}
			else if (PlayerNameTable.getInstance().getPlayerObjectId(activeChar._change_Name) > 0)
			{
				activeChar.sendMessage("The chosen name already exists.");
				showMainHtml(activeChar);
				return false;
			}
			
			if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_NAME_PRICE, null, true))
			{
				activeChar.setName(activeChar._change_Name);
				PlayerNameTable.getInstance().updatePlayerData(activeChar, false);
				activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your name has been changed.", 6000, 0x02, true));
				activeChar.broadcastUserInfo();
				activeChar.store();
				activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
			}
			else
				Incorrect_item(activeChar);
			
		}
		else if (command.startsWith("class_select"))
			class_select(activeChar);
		else if (command.startsWith("class_"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				activeChar._class_id = Integer.parseInt(type);
				class_finish(activeChar);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setclass_"))
		{
			activeChar.abortAttack();
			activeChar.abortCast();
			
			if (activeChar.isInCombat())
			{
				activeChar.sendMessage("You can not be in combat mode..");			
				return false;
			}

			if (activeChar.getBaseClass() != activeChar.getClassId().getId())
			{
				activeChar.sendMessage("You is not with its base class.");
				activeChar.sendPacket(new ExShowScreenMessage("You is not with its base class.", 6000, 0x02, true));
				return false;
			}
			else if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This Item Cannot Be Used On Olympiad Games.");
				return false;
			}
			else if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.DONATE_CLASS_PRICE, null, true))
			{
				String nameclasse = activeChar.getTemplate().getClassName();
				if (activeChar._class_id == 1)
				{
					if (activeChar.getClassId().getId() == 88)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(88);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(88);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 2)
				{
					if (activeChar.getClassId().getId() == 89)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(89);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(89);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 3)
				{
					if (activeChar.getClassId().getId() == 90)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(90);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(90);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 4)
				{
					if (activeChar.getClassId().getId() == 91)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(91);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(91);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 5)
				{
					if (activeChar.getClassId().getId() == 92)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(92);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(92);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 6)
				{
					if (activeChar.getClassId().getId() == 93)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(93);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(93);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 7)
				{
					if (activeChar.getClassId().getId() == 94)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(94);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(94);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 8)
				{
					if (activeChar.getClassId().getId() == 95)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(95);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(95);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 9)
				{
					if (activeChar.getClassId().getId() == 96)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(96);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(96);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 10)
				{
					if (activeChar.getClassId().getId() == 97)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(97);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(97);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 11)
				{
					if (activeChar.getClassId().getId() == 98)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(98);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(98);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 12)
				{
					if (activeChar.getClassId().getId() == 99)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(99);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(99);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 13)
				{
					if (activeChar.getClassId().getId() == 100)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(100);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(100);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 14)
				{
					if (activeChar.getClassId().getId() == 101)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(101);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(101);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 15)
				{
					if (activeChar.getClassId().getId() == 102)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(102);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(102);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 16)
				{
					if (activeChar.getClassId().getId() == 103)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(103);
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(103);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 17)
				{
					if (activeChar.getClassId().getId() == 104)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(104);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(104);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 18)
				{
					if (activeChar.getClassId().getId() == 105)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(105);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(105);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 19)
				{
					if (activeChar.getClassId().getId() == 106)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(106);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(106);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 20)
				{
					if (activeChar.getClassId().getId() == 107)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(107);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(107);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 21)
				{
					if (activeChar.getClassId().getId() == 108)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(108);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(108);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 22)
				{
					if (activeChar.getClassId().getId() == 109)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(109);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(109);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 23)
				{
					if (activeChar.getClassId().getId() == 110)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(110);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(110);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 24)
				{
					if (activeChar.getClassId().getId() == 111)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(111);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(111);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 25)
				{
					if (activeChar.getClassId().getId() == 112)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(112);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(112);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 26)
				{
					if (activeChar.getClassId().getId() == 113)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(113);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(113);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 27)
				{
					if (activeChar.getClassId().getId() == 114)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(114);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(114);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 28)
				{
					if (activeChar.getClassId().getId() == 115)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(115);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(115);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 29)
				{
					if (activeChar.getClassId().getId() == 116)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(116);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(116);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 30)
				{
					if (activeChar.getClassId().getId() == 117)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(117);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(117);
					
					Finish(activeChar);
				}
				else if (activeChar._class_id == 31)
				{
					if (activeChar.getClassId().getId() == 118)
					{
						activeChar.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
						return false;
					}
					
					for (L2Skill skill : activeChar.getSkills().values())
						activeChar.removeSkill(skill);
					
					activeChar.setClassId(118);
					
					if (!activeChar.isSubClassActive())
						activeChar.setBaseClass(118);
					
					Finish(activeChar);
				}
				else
					activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
				
			}
			else
				Incorrect_item(activeChar);
			
		}
		else if (command.startsWith("vip_"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "30_Days":
						activeChar._vip_days = 30;
						Buy_Vip(activeChar);
						break;
					case "60_Days":
						activeChar._vip_days = 60;
						Buy_Vip(activeChar);
						break;
					case "90_Days":
						activeChar._vip_days = 90;
						Buy_Vip(activeChar);
						break;
					case "Eternal":
						activeChar._vip_days = 999;
						Buy_Vip(activeChar);
						break;
					default:
						activeChar.sendMessage("Please select another value!");
						showMainHtml(activeChar);
						break;
				
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("setvip_"))
		{
			if (activeChar._vip_days == 30)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_30_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._vip_days + 1;
						
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
						VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._vip_days >= 30)
						{
							while (activeChar._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._vip_days -= 30;
							}
						}
						
						if (activeChar._vip_days < 30 && activeChar._vip_days > 0)
						{
							while (activeChar._vip_days > 0)
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
								activeChar._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._vip_days == 60)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_60_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._vip_days + 1;
						
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
						VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._vip_days >= 30)
						{
							while (activeChar._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._vip_days -= 30;
							}
						}
						
						if (activeChar._vip_days < 30 && activeChar._vip_days > 0)
						{
							while (activeChar._vip_days > 0)
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
								activeChar._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._vip_days == 90)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_90_DAYS_PRICE, null, true))
				{
					if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._vip_days + 1;
						
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
						VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._vip_days >= 30)
						{
							while (activeChar._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._vip_days -= 30;
							}
						}
						
						if (activeChar._vip_days < 30 && activeChar._vip_days > 0)
						{
							while (activeChar._vip_days > 0)
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
								activeChar._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your vip privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._vip_days == 999)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.VIP_ETERNAL_PRICE, null, true))
				{
					
					if (VipManager.getInstance().hasVipPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = VipManager.getInstance().getVipDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._vip_days + 1;
						
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
						VipManager.getInstance().updateVip(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._vip_days >= 30)
						{
							while (activeChar._vip_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._vip_days -= 30;
							}
						}
						
						if (activeChar._vip_days < 30 && activeChar._vip_days > 0)
						{
							while (activeChar._vip_days > 0)
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
								activeChar._vip_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						VipManager.getInstance().addVip(activeChar.getObjectId(), end_day);
					}
					
					activeChar.sendPacket(new ExShowScreenMessage("Congratulations! You have activated Eternal VIP.", 10000));
					activeChar.sendMessage("Congratulations! You have activated Eternal VIP.");
				}
				else
					Incorrect_item(activeChar);
				
			}
			else
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
			
		}
		else if (command.startsWith("hero_"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try
			{
				String type = st.nextToken();
				switch (type)
				{
					case "30_Days":
						activeChar._hero_days = 30;
						Buy_Hero(activeChar);
						break;
					case "60_Days":
						activeChar._hero_days = 60;
						Buy_Hero(activeChar);
						break;
					case "90_Days":
						activeChar._hero_days = 90;
						Buy_Hero(activeChar);
						break;
					case "Eternal":
						activeChar._hero_days = 999;
						Buy_Hero(activeChar);
						break;
					default:
						activeChar.sendMessage("Please select another value!");
						showMainHtml(activeChar);
						break;
				
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
		}
		else if (command.startsWith("sethero_"))
		{
			if (activeChar._hero_days == 30)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_30_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._hero_days + 1;
						
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
						HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._hero_days >= 30)
						{
							while (activeChar._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._hero_days -= 30;
							}
						}
						
						if (activeChar._hero_days < 30 && activeChar._hero_days > 0)
						{
							while (activeChar._hero_days > 0)
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
								activeChar._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._hero_days == 60)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_60_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._hero_days + 1;
						
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
						HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._hero_days >= 30)
						{
							while (activeChar._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._hero_days -= 30;
							}
						}
						
						if (activeChar._hero_days < 30 && activeChar._hero_days > 0)
						{
							while (activeChar._hero_days > 0)
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
								activeChar._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._hero_days == 90)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_90_DAYS_PRICE, null, true))
				{
					if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._hero_days + 1;
						
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
						HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._hero_days >= 30)
						{
							while (activeChar._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._hero_days -= 30;
							}
						}
						
						if (activeChar._hero_days < 30 && activeChar._hero_days > 0)
						{
							while (activeChar._hero_days > 0)
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
								activeChar._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day);
					}
					
					long _daysleft;
					final long now = Calendar.getInstance().getTimeInMillis();
					long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
					final long endDay = duration;
					_daysleft = ((endDay - now) / 86400000);
					if (_daysleft < 270)
					{
						activeChar.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
						activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
					}
				}
				else
					Incorrect_item(activeChar);
				
			}
			else if (activeChar._hero_days == 999)
			{
				if (activeChar.destroyItemByItemId("Donate Coin", Config.DONATE_COIN_ID, Config.HERO_ETERNAL_PRICE, null, true))
				{
					
					if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId()))
					{
						long _daysleft;
						final long now = Calendar.getInstance().getTimeInMillis();
						long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
						final long endDay = duration;
						
						_daysleft = ((endDay - now) / 86400000) + activeChar._hero_days + 1;
						
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
						HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day);
					}
					else
					{
						long end_day;
						final Calendar calendar = Calendar.getInstance();
						if (activeChar._hero_days >= 30)
						{
							while (activeChar._hero_days >= 30)
							{
								if (calendar.get(Calendar.MONTH) == 11)
									calendar.roll(Calendar.YEAR, true);
								calendar.roll(Calendar.MONTH, true);
								activeChar._hero_days -= 30;
							}
						}
						
						if (activeChar._hero_days < 30 && activeChar._hero_days > 0)
						{
							while (activeChar._hero_days > 0)
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
								activeChar._hero_days--;
							}
						}
						
						end_day = calendar.getTimeInMillis();
						HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day);
					}
					
					activeChar.sendPacket(new ExShowScreenMessage("Congratulations! You have activated Eternal Hero.", 10000));
					activeChar.sendMessage("Congratulations! You have activated Eternal Hero.");
				}
				else
					Incorrect_item(activeChar);
				
			}
			else
			{
				activeChar.sendMessage("ERROR , CONTATE O ADMINISTRADOR");
			}
			
		}
		else if (command.startsWith("back_home"))
			showMainHtml(activeChar);
		
		return true;
	}
	
	@SuppressWarnings("null")
	public static void addargument(Player activeChar, int id, int lvl)
	{
	    ItemInstance rhand = activeChar.getInventory().getPaperdollItem(7);
		
	    if (rhand == null)
	    {
	      activeChar.sendMessage("SYS: Voce nao esta equipado com a arma.");
	      return;
	    }
		
		if (rhand.isAugmented())
		{
			activeChar.sendMessage("SYS: Esta arma ja esta Argumentada.");
			return;
		}
		
	    if (rhand != null)
	    {
		   if (activeChar.getInventory().getInventoryItemCount(Config.DONATE_ITEM_SKILL, 0) >= Config.DONATE_ITEM_COUNT_SKILL)
			{
				activeChar.destroyItemByItemId("Consume", Config.DONATE_ITEM_SKILL, Config.DONATE_ITEM_COUNT_SKILL, activeChar, true);
	    	
	      rhand.setAugmentation(new L2Augmentation(596378492, id, lvl));
	      ItemInstance[] unequipped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(rhand.getItem().getBodyPart());
	      InventoryUpdate iu = new InventoryUpdate();
	      for (ItemInstance element : unequipped) {
	        iu.addModifiedItem(element);
	      }
	      activeChar.sendPacket(iu);
		  } else {
				 activeChar.sendMessage("Voce nao tem tickets donate suficiente.");
			}
	    }
		
		// Apply augmentation bonuses on equip
		if (rhand.isAugmented())
			rhand.getAugmentation().applyBonus(activeChar);
		
		activeChar.sendPacket(new ItemList(activeChar, true));
		activeChar.broadcastUserInfo();
	}

	@SuppressWarnings("null")
	public static void add(Player activeChar, int id)
	  {
	    if (id == 1)
	    {
	      id = 1071062100;
	    }
	    else if (id == 2)
	    {
	      id = 1070927410;
	    }
	    else if (id == 3)
	    {
	      id = 1071123988;
	    }
	    else if (id == 4)
	    {
	      id = 1070996530;
	    }
	    else
	    {
	      activeChar.sendMessage("Invalido.");
	      return;
	    }
	    ItemInstance rhand = activeChar.getInventory().getPaperdollItem(7);
	    
	    if (rhand == null)
	    {
	      activeChar.sendMessage("SYS: Voce nao esta equipado com a arma.");
	      return;
	    }
  
	    if (rhand.isAugmented())
	    {
	      activeChar.sendMessage("SYS: Esta arma ja esta Argumentada.");
	      return;
	    }
	    if (rhand != null)
	    {
		   if (activeChar.getInventory().getInventoryItemCount(Config.DONATE_ITEM, 0) >= Config.DONATE_ITEM_COUNT)
			{
				activeChar.destroyItemByItemId("Consume", Config.DONATE_ITEM, Config.DONATE_ITEM_COUNT, activeChar, true);
	    	
	      rhand.setAugmentation(new L2Augmentation(id, 0, 0));
	      ItemInstance[] unequipped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(rhand.getItem().getBodyPart());
	      InventoryUpdate iu = new InventoryUpdate();
	      for (ItemInstance element : unequipped) {
	        iu.addModifiedItem(element);
	      }
	      activeChar.sendPacket(iu);
		  } else {
				 activeChar.sendMessage("Voce nao tem tickets donate suficiente.");
			}
	    }
	    if (rhand.isAugmented()) {
	      rhand.getAugmentation().applyBonus(activeChar);
	    }
	    activeChar.sendPacket(new ItemList(activeChar, true));
	    activeChar.broadcastCharInfo();
	  }
	
	static void Disabled(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/donate/disabled.htm");
		activeChar.sendPacket(html);
	}
	
	public static void showMainStatusHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/donate/arg_status.htm");
		activeChar.sendPacket(html);
	}
	
	public static void showMainSkillsHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/donate/augmentweapon.htm");
		activeChar.sendPacket(html);
	}
	
	public static void showMainHtml(Player activeChar)
	{
		activeChar._class_id = 0;
		activeChar._sex_id = 0;
		activeChar._change_Name = "";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/donate/index.htm");
		activeChar.sendPacket(html);
	}
	
	public static void showNobleHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/donate/noble.htm");
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_NOBLE_PRICE + "</font>");
		activeChar.sendPacket(html);
	}
	
	public void class_select(Player activeChar)
	{
		activeChar._class_id = 0;
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/class_select.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	public void class_finish(Player activeChar)
	{
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/class_finish.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_CLASS_PRICE + "</font>");
		
		if (activeChar._class_id == 1)
			html.replace("%class_name%", "Duelist");
		else if (activeChar._class_id == 2)
			html.replace("%class_name%", "Dreadnought");
		else if (activeChar._class_id == 3)
			html.replace("%class_name%", "Phoenix Knight");
		else if (activeChar._class_id == 4)
			html.replace("%class_name%", "Hell Knight");
		else if (activeChar._class_id == 5)
			html.replace("%class_name%", "Sagittarius");
		else if (activeChar._class_id == 6)
			html.replace("%class_name%", "Adventurer");
		else if (activeChar._class_id == 7)
			html.replace("%class_name%", "Archmage");
		else if (activeChar._class_id == 8)
			html.replace("%class_name%", "Soultaker");
		else if (activeChar._class_id == 9)
			html.replace("%class_name%", "Arcana Lord");
		else if (activeChar._class_id == 10)
			html.replace("%class_name%", "Cardinal");
		else if (activeChar._class_id == 11)
			html.replace("%class_name%", "Hierophant");
		else if (activeChar._class_id == 12)
			html.replace("%class_name%", "Eva's Templar");
		else if (activeChar._class_id == 13)
			html.replace("%class_name%", "Sword Muse");
		else if (activeChar._class_id == 14)
			html.replace("%class_name%", "Wind Rider");
		else if (activeChar._class_id == 15)
			html.replace("%class_name%", "Moonlight Sentinel");
		else if (activeChar._class_id == 16)
			html.replace("%class_name%", "Mystic Muse");
		else if (activeChar._class_id == 17)
			html.replace("%class_name%", "Elemental Master");
		else if (activeChar._class_id == 18)
			html.replace("%class_name%", "Eva's Saint");
		else if (activeChar._class_id == 19)
			html.replace("%class_name%", "Shillien Templar");
		else if (activeChar._class_id == 20)
			html.replace("%class_name%", "Spectral Dancer");
		else if (activeChar._class_id == 21)
			html.replace("%class_name%", "Ghost Hunter");
		else if (activeChar._class_id == 22)
			html.replace("%class_name%", "Ghost Sentinel");
		else if (activeChar._class_id == 23)
			html.replace("%class_name%", "Storm Screamer");
		else if (activeChar._class_id == 24)
			html.replace("%class_name%", "Spectral Master");
		else if (activeChar._class_id == 25)
			html.replace("%class_name%", "Shillien Saint");
		else if (activeChar._class_id == 26)
			html.replace("%class_name%", "Titan");
		else if (activeChar._class_id == 27)
			html.replace("%class_name%", "Grand Khavatari");
		else if (activeChar._class_id == 28)
			html.replace("%class_name%", "Dominator");
		else if (activeChar._class_id == 29)
			html.replace("%class_name%", "Doomcryer");
		else if (activeChar._class_id == 30)
			html.replace("%class_name%", "Fortune Seeker");
		else if (activeChar._class_id == 31)
			html.replace("%class_name%", "Maestro");
		
		if (activeChar._class_id >= 1 && activeChar._class_id <= 6)
			html.replace("%race%", "Human Fighter");
		else if (activeChar._class_id >= 7 && activeChar._class_id <= 11)
			html.replace("%race%", "Human Mystic");
		else if (activeChar._class_id >= 12 && activeChar._class_id <= 15)
			html.replace("%race%", "Elf Fighter");
		else if (activeChar._class_id >= 16 && activeChar._class_id <= 18)
			html.replace("%race%", "Elf Mystic");
		else if (activeChar._class_id >= 19 && activeChar._class_id <= 22)
			html.replace("%race%", "Dark Elf Fighter");
		else if (activeChar._class_id >= 23 && activeChar._class_id <= 25)
			html.replace("%race%", "Dark Elf Mystic");
		else if (activeChar._class_id >= 26 && activeChar._class_id <= 27)
			html.replace("%race%", "Orc Fighter");
		else if (activeChar._class_id >= 28 && activeChar._class_id <= 29)
			html.replace("%race%", "Orc Mystic");
		else if (activeChar._class_id >= 30 && activeChar._class_id <= 31)
			html.replace("%race%", "Dwarf Fighter");
		
		activeChar.sendPacket(html);
	}
	
	public void name_select(Player activeChar)
	{
		activeChar._change_Name = "";
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/name_select.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		html.replace("%name%", activeChar.getName());
		activeChar.sendPacket(html);
	}
	
	public void name_finish(Player activeChar)
	{
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/name_finish.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		html.replace("%old_name%", activeChar.getName());
		html.replace("%new_name%", activeChar._change_Name);
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_NAME_PRICE + "</font>");
		activeChar.sendPacket(html);
	}
	
	public void sex_select(Player activeChar)
	{
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/sex.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		
		if (activeChar.getAppearance().getSex() == Sex.MALE)
			html.replace("%old_sex%", "Male / Masculino");
		else
			html.replace("%old_sex%", "Female / Feminino");
		
		if (activeChar._sex_id == 1)
			html.replace("%new_sex%", "Female / Feminino");
		else
			html.replace("%new_sex%", "Male / Masculino");
		
		html.replace("%coin%", "<font color=\"00FF00\">" + Config.DONATE_SEX_PRICE + "</font>");
		activeChar.sendPacket(html);
	}
	
	public void Incorrect_item(Player activeChar)
	{
		activeChar._vip_days = 0;
		activeChar._hero_days = 0;
		activeChar._class_id = 0;
		activeChar._sex_id = 0;
		activeChar._change_Name = "";
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/incorrect.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		activeChar.sendPacket(html);
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
		activeChar.removeItens();
		activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
		
		if (activeChar.isNoble())
		{
			StatsSet playerStat = Olympiad.getNobleStats(activeChar.getObjectId());
			if (playerStat != null)
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement statement;
					statement = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?");
					statement.setInt(1, activeChar.getObjectId());
					statement.execute();
					statement.close();
				}
				catch (Exception e)
				{
					System.out.println("Class Item: " + e);
				}
				
				Olympiad.removeNobleStats(activeChar.getObjectId());
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
			System.out.println("Class Item: " + e);
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
	
	public void Buy_Vip(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/donate/vip.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		
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
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		
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
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
