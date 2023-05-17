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
package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedReport implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"report",
		"send_report"
	};
	private static String _type;
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.startsWith("report"))
		{
			mainHtml(activeChar);
		}
		else if (command.startsWith("send_report"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String msg = "";
			String type = null;
			type = st.nextToken();

		try
		{
			while (st.hasMoreTokens())
			{
			msg = msg + " " + st.nextToken();
			}
	
			if (msg.equals(""))
			{
				activeChar.sendMessage("Message box cannot be empty.");
				return false;
			}

			sendReport(activeChar, type, msg);
			
		}
		  catch (StringIndexOutOfBoundsException e){}
		}
		mainHtml(activeChar);
		
		return true;

	}
	
	private static void sendReport(Player player, String command, String msg)
	{
	String type = command;
	L2GameClient info = player.getClient().getConnection().getClient();
	
	if (type.equals("General"))
	_type = "General";
	if (type.equals("Fatal"))
	_type = "Fatal";
	if (type.equals("Misuse"))
	_type = "Misuse";
	if (type.equals("Balance"))
	_type = "Balance";
	if (type.equals("Other"))
	_type = "Other";

	try
	{
	String fname = "log/BugReports/" + player.getName() + ".txt";
	File file = new File(fname);
	
	boolean exist = file.createNewFile();
	if (!exist)
	{
	player.sendMessage("You have already sent a bug report, GMs must check it first.");
	player.sendPacket(new ExShowScreenMessage("You have already sent a bug report!", 4000, ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));

	return;
	}
	FileWriter fstream = new FileWriter(fname);
	BufferedWriter out = new BufferedWriter(fstream);
	out.write("Character Info: " + info + "\r\nBug Type: " + _type + "\r\nMessage: " + msg);
	player.sendMessage("Report sent. GMs will check it soon. Thanks...");
	player.sendPacket(new ExShowScreenMessage("Report sent successfully!", 4000, ExShowScreenMessage.SMPOS.MIDDLE_RIGHT, false));

	for (Player allgms : World.getAllGMs())
	allgms.sendPacket(new CreatureSay(0, Say2.SHOUT, "Bug Report Manager", player.getName() + " sent a bug report."));

	System.out.println("Character: " + player.getName() + " sent a bug report.");
	out.close();
	}
	catch (Exception e)
	{
	player.sendMessage("Something went wrong try again.");
	}
	}
	
	static
	{
	new File("log/BugReports/").mkdirs();
	}
	
	public static void mainHtml(Player activeChar)
	{	
		String htmFile = "data/html/mods/menu/report.htm";		
		NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setFile(htmFile);
		msg.replace("%player%", activeChar.getName());
		activeChar.sendPacket(msg);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}