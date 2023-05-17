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
package com.l2jmega.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedAutoPotion;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedBanking;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedBossSpawn;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedCastles;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedClanNotice;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedColor;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedDonate;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedEnchant;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedEvent;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedLuckyDice;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedMission;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedPassword;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedPlayersCont;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedRanking;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedRepair;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedReport;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedStatus;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedTrySkin;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedVoteZone;

public class VoicedCommandHandler
{
	private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();
	
	public static VoicedCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected VoicedCommandHandler()
	{
		registerHandler(new VoicedAutoPotion());
		registerHandler(new VoicedPassword());
		registerHandler(new VoicedEnchant());
		
		registerHandler(new VoicedTrySkin());
		registerHandler(new VoicedRanking());
		registerHandler(new VoicedReport());
		registerHandler(new VoicedMenu());
		registerHandler(new VoicedBossSpawn());
		registerHandler(new VoicedCastles());
		registerHandler(new VoicedClanNotice());
		registerHandler(new VoicedColor());
		registerHandler(new VoicedEvent());
		registerHandler(new VoicedPlayersCont());
		registerHandler(new VoicedBanking());
		registerHandler(new VoicedDonate());
		registerHandler(new VoicedRepair());		
		registerHandler(new VoicedMission());
		registerHandler(new VoicedVoteZone());
		registerHandler(new VoicedLuckyDice());
		registerHandler(new VoicedStatus());	
	}
	
	public void registerHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (int i = 0; i < ids.length; i++)
			_datatable.put(ids[i].hashCode(), handler);
	}
	
	public IVoicedCommandHandler getHandler(String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		
		return _datatable.get(command.hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
	}
}
