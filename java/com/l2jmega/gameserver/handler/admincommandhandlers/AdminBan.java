package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.LoginServerThread;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>ban_acc [account_name] = changes account access level to -100 and logs him off. If no account is specified target's account is used.</li>
 * <li>ban_char [char_name] = changes a characters access level to -100 and logs him off. If no character is specified target is used.</li>
 * <li>ban_chat [char_name] [duration] = chat bans a character for the specified duration. If no name is specified the target is chat banned indefinitely.</li>
 * <li>unban_acc [account_name] = changes account access level to 0.</li>
 * <li>unban_char [char_name] = changes specified characters access level to 0.</li>
 * <li>unban_chat [char_name] = lifts chat ban from specified player. If no player name is specified current target is used.</li>
 * <li>jail [char_name] [penalty_time] = jails character. Time specified in minutes. For ever if no time is specified.</li>
 * <li>unjail [char_name] = Unjails player, teleport him to Floran.</li>
 * </ul>
 */
public class AdminBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ban", // returns ban commands
		"admin_ban_acc",
		"admin_ban_char",
		"admin_ban_chat",
		
		"admin_unban", // returns unban commands
		"admin_unban_acc",
		"admin_unban_char",
		"admin_unban_chat",
		
		"admin_jail",
		"admin_unjail"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int duration = -1;
		Player targetPlayer = null;
		
		// One parameter, player name
		if (st.hasMoreTokens())
		{
			player = st.nextToken();
			targetPlayer = World.getInstance().getPlayer(player);
			
			// Second parameter, duration
			if (st.hasMoreTokens())
			{
				try
				{
					duration = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("Invalid number format used: " + nfe);
					return false;
				}
			}
		}
		else
		{
			// If there is no name, select target
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
				targetPlayer = (Player) activeChar.getTarget();
		}
		
		// Can't ban yourself
		if (targetPlayer != null && targetPlayer.equals(activeChar))
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
			return false;
		}
		
		if (command.startsWith("admin_ban ") || command.equalsIgnoreCase("admin_ban"))
		{
			activeChar.sendMessage("Available ban commands: //ban_acc, //ban_char, //ban_chat");
			return false;
		}
		else if (command.startsWith("admin_ban_acc"))
		{
			if (activeChar.getAccessLevel().getLevel() < 7)
				return false;

			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_acc <account_name> (if none, target char's account gets banned).");
				return false;
			}
			else if (targetPlayer == null)
			{
				LoginServerThread.getInstance().sendAccessLevel(player, -100);
				activeChar.sendMessage("Ban request sent for account " + player + ".");
			}
			else
			{
				targetPlayer.setPunishLevel(Player.PunishLevel.ACC, 0);
				activeChar.sendMessage(targetPlayer.getAccountName() + " account is now banned.");
			}
		}
		else if (command.startsWith("admin_ban_char"))
		{
			if (activeChar.getAccessLevel().getLevel() < 7)
				return false;

			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_char <char_name> (if none, target char is banned)");
				return false;
			}
			
			return changeCharAccessLevel(targetPlayer, player, activeChar, -100);
		}
		else if (command.startsWith("admin_ban_chat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_chat <char_name> [penalty_minutes]");
				return false;
			}
			
			if (targetPlayer != null)
			{
				if (targetPlayer.getPunishLevel().value() > 0)
				{
					activeChar.sendMessage(targetPlayer.getName() + " is already jailed or banned.");
					return false;
				}
				
				String banLengthStr = "";
				targetPlayer.setPunishLevel(Player.PunishLevel.CHAT, duration);
				
				if (duration > 0)
					banLengthStr = " for " + duration + " minutes";
				
				activeChar.sendMessage(targetPlayer.getName() + " is now chat banned" + banLengthStr + ".");
			}
			else
				banChatOfflinePlayer(activeChar, player, duration, true);
		}
		else if (command.startsWith("admin_unban ") || command.equalsIgnoreCase("admin_unban"))
		{
			activeChar.sendMessage("Available unban commands: //unban_acc, //unban_char, //unban_chat");
			return false;
		}
		else if (command.startsWith("admin_unban_acc"))
		{
			if (targetPlayer != null)
			{
				activeChar.sendMessage(targetPlayer.getName() + " is currently online so mustn't be banned.");
				return false;
			}
			else if (!player.equals(""))
			{
				LoginServerThread.getInstance().sendAccessLevel(player, 0);
				activeChar.sendMessage("Unban request sent for account " + player + ".");
			}
			else
			{
				activeChar.sendMessage("Usage: //unban_acc <account_name>");
				return false;
			}
		}
		else if (command.startsWith("admin_unban_char"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unban_char <char_name>");
				return false;
			}
			
			if (targetPlayer != null)
			{
				activeChar.sendMessage(targetPlayer.getName() + " is currently online so mustn't be banned.");
				return false;
			}
			
			return changeCharAccessLevel(null, player, activeChar, 0);
		}
		else if (command.startsWith("admin_unban_chat"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unban_chat <char_name>");
				return false;
			}
			
			if (targetPlayer != null)
			{
				if (targetPlayer.isChatBanned())
				{
					targetPlayer.setPunishLevel(Player.PunishLevel.NONE, 0);
					activeChar.sendMessage(targetPlayer.getName() + "'s chat ban has been lifted.");
				}
				else
					activeChar.sendMessage(targetPlayer.getName() + " isn't currently chat banned.");
			}
			else
				banChatOfflinePlayer(activeChar, player, 0, false);
		}
		else if (command.startsWith("admin_jail"))
		{
			if (activeChar.getAccessLevel().getLevel() < 7)
				return false;

			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //jail <charname> [penalty_minutes] (if no name is given, selected target is jailed forever).");
				return false;
			}
			
			if (targetPlayer != null)
			{
				targetPlayer.setPunishLevel(Player.PunishLevel.JAIL, duration);
				activeChar.sendMessage(targetPlayer.getName() + " have been jailed for " + (duration > 0 ? duration + " minutes." : "ever !"));
			}
			else
				jailOfflinePlayer(activeChar, player, duration);
		}
		else if (command.startsWith("admin_unjail"))
		{
			if (targetPlayer == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //unjail <charname> (If no name is given target is used).");
				return false;
			}
			else if (targetPlayer != null)
			{
				targetPlayer.setPunishLevel(Player.PunishLevel.NONE, 0);
				activeChar.sendMessage(targetPlayer.getName() + " have been unjailed.");
			}
			else
				unjailOfflinePlayer(activeChar, player);
		}
		return true;
	}
	
	private static void banChatOfflinePlayer(Player activeChar, String name, int delay, boolean ban)
	{
		int level = 0;
		long value = 0;
		
		if (ban)
		{
			level = Player.PunishLevel.CHAT.value();
			value = (delay > 0 ? delay * 60000L : 60000);
		}
		else
		{
			level = Player.PunishLevel.NONE.value();
			value = 0;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, level);
			statement.setLong(2, value);
			statement.setString(3, name);
			
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			
			if (count == 0)
				activeChar.sendMessage("Character isn't found.");
			else if (ban)
				activeChar.sendMessage(name + " is chat banned for " + (delay > 0 ? delay + " minutes." : "ever !"));
			else
				activeChar.sendMessage(name + "'s chat ban have been lifted.");
		}
		catch (SQLException se)
		{
			activeChar.sendMessage("SQLException while chat-banning player");
			if (Config.DEBUG)
				se.printStackTrace();
		}
	}
	
	private static void jailOfflinePlayer(Player activeChar, String name, int delay)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, -114356);
			statement.setInt(2, -249645);
			statement.setInt(3, -2984);
			statement.setInt(4, Player.PunishLevel.JAIL.value());
			statement.setLong(5, (delay > 0 ? delay * 60000L : 0));
			statement.setString(6, name);
			
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			
			if (count == 0)
				activeChar.sendMessage("Character not found!");
			else
				activeChar.sendMessage(name + " have been jailed for " + (delay > 0 ? delay + " minutes." : "ever!"));
		}
		catch (SQLException se)
		{
			activeChar.sendMessage("SQLException while jailing player");
			if (Config.DEBUG)
				se.printStackTrace();
		}
	}
	
	private static void unjailOfflinePlayer(Player activeChar, String name)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, punish_level=?, punish_timer=? WHERE char_name=?");
			statement.setInt(1, 17836);
			statement.setInt(2, 170178);
			statement.setInt(3, -3507);
			statement.setInt(4, 0);
			statement.setLong(5, 0);
			statement.setString(6, name);
			statement.execute();
			int count = statement.getUpdateCount();
			statement.close();
			if (count == 0)
				activeChar.sendMessage("Character isn't found.");
			else
				activeChar.sendMessage(name + " have been unjailed.");
		}
		catch (SQLException se)
		{
			activeChar.sendMessage("SQLException while jailing player");
			if (Config.DEBUG)
				se.printStackTrace();
		}
	}
	
	private static boolean changeCharAccessLevel(Player targetPlayer, String player, Player activeChar, int lvl)
	{
		if (targetPlayer != null)
		{
			targetPlayer.setAccessLevel(lvl);
			targetPlayer.logout();
			activeChar.sendMessage(targetPlayer.getName() + " has been banned.");
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?");
				statement.setInt(1, lvl);
				statement.setString(2, player);
				statement.execute();
				int count = statement.getUpdateCount();
				statement.close();
				
				if (count == 0)
				{
					activeChar.sendMessage("Character not found or access level unaltered.");
					return false;
				}
				
				activeChar.sendMessage(player + " now has an access level of " + lvl + ".");
			}
			catch (SQLException se)
			{
				activeChar.sendMessage("SQLException while changing character's access level");
				if (Config.DEBUG)
					se.printStackTrace();
				
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}