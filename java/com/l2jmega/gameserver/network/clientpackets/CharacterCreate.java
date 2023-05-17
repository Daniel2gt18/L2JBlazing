package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.CharTemplateTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTreeTable;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.model.L2ShortCut;
import com.l2jmega.gameserver.model.L2SkillLearn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.template.PlayerTemplate;
import com.l2jmega.gameserver.model.base.ClassRace;
import com.l2jmega.gameserver.model.base.ClassType;
import com.l2jmega.gameserver.model.base.Experience;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.network.serverpackets.CharCreateFail;
import com.l2jmega.gameserver.network.serverpackets.CharCreateOk;
import com.l2jmega.gameserver.network.serverpackets.CharSelectInfo;
import com.l2jmega.gameserver.scripting.Quest;
import com.l2jmega.gameserver.scripting.ScriptManager;

@SuppressWarnings("unused")
public final class CharacterCreate extends L2GameClientPacket
{
	// cSdddddddddddd
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private int _int;
	private int _str;
	private int _con;
	private int _men;
	private int _dex;
	private int _wit;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		_int = readD();
		_str = readD();
		_con = readD();
		_men = readD();
		_dex = readD();
		_wit = readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!StringUtil.isValidPlayerName(_name))
		{
			sendPacket((_name.length() > 16) ? CharCreateFail.REASON_16_ENG_CHARS : CharCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (_face > 2 || _face < 0)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (_hairStyle < 0 || (_sex == 0 && _hairStyle > 4) || (_sex != 0 && _hairStyle > 6))
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (_hairColor > 3 || _hairColor < 0)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (PlayerNameTable.getInstance().getCharactersInAcc(getClient().getAccountName()) >= 7)
		{
			sendPacket(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
			return;
		}
		
		if (PlayerNameTable.getInstance().getPlayerObjectId(_name) > 0)
		{
			sendPacket(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
			return;
		}
		
		final PlayerTemplate template = CharTemplateTable.getInstance().getTemplate(_classId);
		if (template == null || template.getClassBaseLevel() > 1)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		final Player newChar = Player.create(IdFactory.getInstance().getNextId(), template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, Sex.values()[_sex]);
		if (newChar == null)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		newChar.setCurrentCp(0);
		newChar.setCurrentHp(newChar.getMaxHp());
		newChar.setCurrentMp(newChar.getMaxMp());
		
		// send acknowledgement
		sendPacket(CharCreateOk.STATIC_PACKET);
		
		World.getInstance().addObject(newChar);
		
		if (Config.SPAWN_CHAR)
		{
			int rnd;
			rnd = 1 + (int) (Math.random() * 4);
			if (rnd == 1)
				newChar.setXYZInvisible(Config.SPAWN_X_1 + Rnd.get(-150, 150), Config.SPAWN_Y_1 + Rnd.get(-150, 150), Config.SPAWN_Z_1);
			else if (rnd == 2)
				newChar.setXYZInvisible(Config.SPAWN_X_2 + Rnd.get(-150, 150), Config.SPAWN_Y_2 + Rnd.get(-150, 150), Config.SPAWN_Z_2);
			else if (rnd == 3)
				newChar.setXYZInvisible(Config.SPAWN_X_3 + Rnd.get(-150, 150), Config.SPAWN_Y_3 + Rnd.get(-150, 150), Config.SPAWN_Z_3);
			else if (rnd == 4)
				newChar.setXYZInvisible(Config.SPAWN_X_4 + Rnd.get(-150, 150), Config.SPAWN_Y_4 + Rnd.get(-150, 150), Config.SPAWN_Z_4);
			else
				newChar.setXYZInvisible(Config.SPAWN_X_1 + Rnd.get(-150, 150), Config.SPAWN_Y_1 + Rnd.get(-150, 150), Config.SPAWN_Z_1);
		}
		else
			newChar.getPosition().set(template.getSpawn());
		
		if (Config.CHAR_TITLE)
			newChar.setTitle(Config.ADD_CHAR_TITLE);
		else
			newChar.setTitle("");
		
		if (Config.ALLOW_CREATE_LVL)
			newChar.getStat().addExp(Experience.LEVEL[Config.CHAR_CREATE_LVL]);
		
		newChar.registerShortCut(new L2ShortCut(0, 0, 3, 2, -1, 1)); // attack shortcut
		newChar.registerShortCut(new L2ShortCut(3, 0, 3, 5, -1, 1)); // take shortcut
		newChar.registerShortCut(new L2ShortCut(10, 0, 3, 0, -1, 1)); // sit shortcut
		
		for (Item ia : template.getItems())
		{
			ItemInstance item = newChar.getInventory().addItem("Init", ia.getItemId(), 1, newChar, null);
			if (item.getItemId() == 5588) // tutorial book shortcut
				newChar.registerShortCut(new L2ShortCut(11, 0, 1, item.getObjectId(), -1, 1));
			
			if (item.isEquipable())
			{
				if (newChar.getActiveWeaponItem() == null || !(item.getItem().getType2() != Item.TYPE2_WEAPON))
					newChar.getInventory().equipItemAndRecord(item);
			}
		}
		
		for (L2SkillLearn skill : SkillTreeTable.getInstance().getAvailableSkills(newChar, newChar.getClassId()))
		{
			newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);
			if (skill.getId() == 1001 || skill.getId() == 1177)
				newChar.registerShortCut(new L2ShortCut(1, 0, 2, skill.getId(), 1, 1));
			
			if (skill.getId() == 1216)
				newChar.registerShortCut(new L2ShortCut(9, 0, 2, skill.getId(), 1, 1));
		}
		
		if (!Config.DISABLE_TUTORIAL)
		{
			if (newChar.getQuestState("Tutorial") == null)
			{
				Quest q = ScriptManager.getInstance().getQuest("Tutorial");
				if (q != null)
					q.newQuestState(newChar).setState(Quest.STATE_STARTED);
			}
		}
		
		if (Config.CUSTOM_STARTER_ITEMS_ENABLED)
		{
			if (newChar.isMageClass() || (newChar.getTemplate().getRace() == ClassRace.ORC && (newChar.getClassId().getType() == ClassType.MYSTIC || newChar.getClassId().getType() == ClassType.PRIEST)))
			{
				for (int[] reward : Config.STARTING_CUSTOM_ITEMS_M)
				{
					if (ItemTable.getInstance().createDummyItem(reward[0]).isStackable())
						newChar.getInventory().addItem("Starter Items Mage", reward[0], reward[1], newChar, null);
					else
						for (int i = 0; i < reward[1]; ++i)
							newChar.getInventory().addItem("Starter Items Mage", reward[0], 1, newChar, null);
				}
			}
			else
			{
				for (int[] reward : Config.STARTING_CUSTOM_ITEMS_F)
				{
					if (ItemTable.getInstance().createDummyItem(reward[0]).isStackable())
						newChar.getInventory().addItem("Starter Items Fighter", reward[0], reward[1], newChar, null);
					else
						for (int i = 0; i < reward[1]; ++i)
							newChar.getInventory().addItem("Starter Items Fighter", reward[0], 1, newChar, null);
				}
			}
		}
		
		newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
		newChar.setCurrentCp(newChar.getMaxCp());
		
		newChar.setOnlineStatus(true, false);
		newChar.deleteMe();
		
		final CharSelectInfo cl = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		getClient().getConnection().sendPacket(cl);
		getClient().setCharSelection(cl.getCharInfo());
	}
}