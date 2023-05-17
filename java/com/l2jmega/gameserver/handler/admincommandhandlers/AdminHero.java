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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.instancemanager.HeroManager;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.util.CloseUtil;

/**
 * @author rapfersan92
 */
public class AdminHero implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_remove_hero",
		"admin_sethero",
		"admin_masshero",
	
	};
	
	private final static Logger _log = Logger.getLogger(AdminHero.class.getName());
	
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
		if (command.startsWith("admin_sethero"))
		{
			if (target instanceof Player)
			{
				Player targetPlayer = (Player) target;
				boolean newHero = !targetPlayer.isHero();
				
				if (newHero)
				{
					if (HeroManager.getInstance().hasHeroPrivileges(targetPlayer.getObjectId()))
					{
						removeHero(activeChar, targetPlayer);
					}
					
					targetPlayer.setHero(true);
					targetPlayer.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "[Hero System]", "Voce se tornou um HERO ETERNO."));
					updateDatabase(targetPlayer, true);
					targetPlayer.broadcastUserInfo();
					if (activeChar.isSubClassActive())
					{
						for (L2Skill s : SkillTable.getHeroSkills())
							activeChar.addSkill(s, false); // Dont Save Hero skills to database
					}
					targetPlayer.sendSkillList();
					
				}
				else
				{
					targetPlayer.setHero(false);
					targetPlayer.sendMessage("[Hero System]: Seu HERO ETERNO foi removido.");
					targetPlayer.getInventory().destroyItemByItemId("", 6842, 1, targetPlayer, null);
					
					updateDatabase(targetPlayer, false);
					
					for (L2Skill s : SkillTable.getHeroSkills())
						targetPlayer.removeSkill(s); // Just Remove skills from nonHero characters
					
					targetPlayer.sendSkillList();
					targetPlayer.broadcastUserInfo();
				}
				
				targetPlayer = null;
			}
			else
			{
				activeChar.sendMessage("[Hero System]: Impossible to set a non Player Target as hero.");
				_log.info("[Hero System]: GM: " + activeChar.getName() + " is trying to set a non Player Target as hero.");
				
				return false;
			}
			
		}
		else if (command.startsWith("admin_masshero"))
		{
			for (Player player : World.getInstance().getPlayers())
			{
				if (player != null)
				{
					if (!player.isHero() && !player.isInOlympiadMode())
					{
						player.setHero(true);
						player.sendMessage("[Hero System]: Admin is rewarding all online players with Hero Status.");
						player.broadcastUserInfo();
					}
					player = null;
				}
			}
			Announcement.Announce("GM: Todos os Jogadores online receberam Hero ate que reloguem. Aproveitem..!");
		}
		
		else if (command.equalsIgnoreCase("admin_remove_hero"))
			removeHero(activeChar, (Player) target);
		
		return true;
	}
	
	public static void removeHero(Player activeChar, Player targetChar)
	{
		if (!HeroManager.getInstance().hasHeroPrivileges(targetChar.getObjectId()))
		{
			activeChar.sendMessage("Your target does not have hero privileges.");
			return;
		}
		
		HeroManager.getInstance().removeHero(targetChar.getObjectId());
		activeChar.sendMessage("You have removed hero privileges from " + targetChar.getName() + ".");
		targetChar.sendPacket(new ExShowScreenMessage("Your hero privileges were removed by the admin.", 10000));
		targetChar.setHero(false);
		targetChar.destroyItem("HeroEnd", 6842, 1, null, false);
		
	}
	
	/**
	 * @param player
	 * @param newHero
	 */
	public static void updateDatabase(Player player, boolean newHero)
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
			PreparedStatement stmt = con.prepareStatement(newHero ? INSERT_DATA : DEL_DATA);
			
			// if it is a new donator insert proper data
			// --------------------------------------------
			if (newHero)
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
	static String INSERT_DATA = "REPLACE INTO characters_hero_eterno (obj_Id, char_name, hero) VALUES (?,?,?)";
	static String DEL_DATA = "UPDATE characters_hero_eterno SET hero = 0 WHERE obj_Id=?";
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}