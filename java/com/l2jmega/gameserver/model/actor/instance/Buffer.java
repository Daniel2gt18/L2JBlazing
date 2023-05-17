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
package com.l2jmega.gameserver.model.actor.instance;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.network.serverpackets.ValidateLocation;

import java.util.StringTokenizer;

import com.l2jmega.commons.random.Rnd;

/**
 * @author KDerD64
 */
public final class Buffer extends Folk
{
	public Buffer(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		int buffid = 0;
		int bufflevel = 1;
		String nextWindow = null;
		if (st.countTokens() == 3)
		{
			buffid = Integer.valueOf(st.nextToken());
			bufflevel = Integer.valueOf(st.nextToken());
			nextWindow = st.nextToken();
		}
		else if (st.countTokens() == 1)
			buffid = Integer.valueOf(st.nextToken());
		
		if (actualCommand.equalsIgnoreCase("getbuff"))
		{
			if (buffid != 0)
			{
				showChatWindow(player, nextWindow);
				 player.broadcastPacket(new MagicSkillUse(this, player, buffid, bufflevel, 5, 0));
				SkillTable.getInstance().getInfo(buffid, bufflevel).getEffects(this, player);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT).addSkillName(buffid));
			}
		}
		else if (actualCommand.equalsIgnoreCase("restore"))
		{
			showMessageWindow(player);
			
			if ((player._inEventTvT && TvT.is_started() || player._inEventCTF && CTF.is_started()) && !player.isGM())
			{
				player.sendMessage("You can not do that.");
				return;
			}
			
			if (!player.isInsideZone(ZoneId.PEACE) && !player.isGM())
			{
				player.sendMessage("You can not do that.");
				return;
			}
			
			 player.broadcastPacket(new MagicSkillUse(this, player, 1218, 33, 100, 0));
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
		else if (actualCommand.equalsIgnoreCase("cancel"))
		{
			showMessageWindow(player);
			player.stopAllEffects();
		}
		else if (actualCommand.equalsIgnoreCase("fighter"))
		{
			showMessageWindow(player);
			for (Integer skillid : Config.FIGHTER_BUFF_LIST)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(skillid, SkillTable.getInstance().getMaxLevel(skillid));
				if (skill != null)
					skill.getEffects(player, player);
			}
			
		}
		else if (actualCommand.equalsIgnoreCase("mage"))
		{
			showMessageWindow(player);
			
			for (Integer skillid : Config.MAGE_BUFF_LIST)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(skillid, SkillTable.getInstance().getMaxLevel(skillid));
				if (skill != null)
					skill.getEffects(player, player);
			}
			
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void onAction(Player player)
	{
		if (this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			player.sendPacket(new ValidateLocation(this));
		}
		else if (isInsideRadius(player, INTERACTION_DISTANCE, false, false))
		{
			SocialAction sa = new SocialAction(this, Rnd.get(8));
			broadcastPacket(sa);
			player.setCurrentFolkNPC(this);
			showMessageWindow(player);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			player.getAI().setIntention(CtrlIntention.INTERACT, this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	private void showMessageWindow(Player player)
	{
		String filename = "data/html/mods/custom_buffer/" + getNpcId() + ".htm";
		
		filename = getHtmlPath(getNpcId(), 0);
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;
		
		return "data/html/mods/custom_buffer/" + pom + ".htm";
	}
}