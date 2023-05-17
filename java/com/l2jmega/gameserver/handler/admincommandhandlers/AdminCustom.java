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

import com.l2jmega.Config;
import com.l2jmega.events.ArenaTask;
import com.l2jmega.events.PartyZoneTask;
import com.l2jmega.gameserver.NewZoneVote;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import com.l2jmega.gameserver.instancemanager.VIPINFO;
import com.l2jmega.gameserver.instancemanager.VipManager;
import com.l2jmega.gameserver.instancemanager.VoteZoneCommands;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.AutoAttackStart;
import com.l2jmega.gameserver.network.serverpackets.AutoAttackStop;
import com.l2jmega.gameserver.network.serverpackets.OpenUrl;
import com.l2jmega.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmega.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmega.gameserver.taskmanager.PvpFlagTaskManager;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;
import com.l2jmega.commons.util.StringUtil;

import phantom.Phantom_Archers;
import phantom.Phantom_Attack;
import phantom.Phantom_Farm;
import phantom.Phantom_Town;
import phantom.Phantom_TvT;



/**
 * @author MeGaPacK
 */
public class AdminCustom implements IAdminCommandHandler
{
	
	public static final AdminCustom getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_massflag",
		"admin_tournament",
		"admin_partyzone",
		"admin_spawn_phantom",
		"admin_spawn_phantomtown",
		"admin_spawn_phantomfarm",
		"admin_spawn_phantomtvt",
		"admin_attack",
		"admin_stop",
		"admin_masspvp",
		"admin_votezone",
		"admin_takecontrol",
		"admin_releasecontrol",
		"admin_spawn_clan",
		"admin_takecontrol",
		"admin_releasecontrol",
		"admin_spawn_archer",
		"admin_walk"
		
	};
	
	protected static final Logger _log = Logger.getLogger(AdminCustom.class.getName());
	
	public static boolean _arena_manual = false;
	public static boolean _partytask_manual = false;
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_attack"))
		{
			Player ppl = activeChar.getTarget().getActingPlayer();
			ppl.broadcastPacket(new AutoAttackStart(activeChar.getTarget().getObjectId()));
			ppl.getActingPlayer().getAI().clientStartAutoAttack();
		}	
		else if (command.startsWith("admin_stop"))
		{
			Player ppl = activeChar.getTarget().getActingPlayer();
			ppl.broadcastPacket(new AutoAttackStop(activeChar.getTarget().getObjectId()));
			ppl.getActingPlayer().getAI().clientStopAutoAttack();
		}
		else if (command.equals("admin_massflag"))
		{
			for (Player pl : activeChar.getKnownTypeInRadius(Player.class, 6000))
			{
				PvpFlagTaskManager.getInstance().add(pl, 60000);
			}
		}
		else if (command.equals("admin_walk"))
		{
            if(activeChar.isPhantom()){

    				ThreadPool.schedule(new PhantomWalk2(activeChar), Rnd.get(5200, 48540));
    			
            }

		}
		else if (command.equals("admin_tournament"))
		{
			if (ArenaTask._started)
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Tournament: Event Finished.");
				_log.info("----------------------------------------------------------------------------");
				ArenaTask._aborted = true;
				finishEventArena();
				_arena_manual = true;
				
				activeChar.sendMessage("SYS: Voce Finalizou Tournament");
			}
			else
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info("[Tournament]: Event Started.");
				_log.info("----------------------------------------------------------------------------");
				initEventArena();
				_arena_manual = true;
				activeChar.sendMessage("SYS: Voce ativou Tournament");
			}
		}
		else if (command.equals("admin_partyzone"))
		{
			if (PartyZoneTask._started)
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info(""+Config.NAME_EVENT+": Event Finished.");
				_log.info("----------------------------------------------------------------------------");
				PartyZoneTask._aborted = true;
				finishPartyZone();
				_partytask_manual = true;
				
				activeChar.sendMessage("SYS: Voce Finalizou "+Config.NAME_EVENT+"");
			}
			else
			{
				_log.info("----------------------------------------------------------------------------");
				_log.info(""+Config.NAME_EVENT+": Event Started.");
				_log.info("----------------------------------------------------------------------------");
				initPartyZone();
				_partytask_manual = true;
				activeChar.sendMessage("SYS: Voce ativou "+Config.NAME_EVENT+"");
			}
		}
		else if (command.equals("admin_votezone")) {
			Announcement.Announce("VoteZone: Vote for new pvp Zone");
			Announcement.Announce("VoteZone: Voting ends in " + Config.VOTE_PVPZONE_TIME + " minute(s)!");
			VoteZone();
			activeChar.sendMessage("SYS: Voce utilizou o comando admin_votezone..");
		} 
		else if (command.equals("admin_spawn_phantomtown"))
		{
			AdminAdmin.showMainPage(activeChar, "main_menu.htm");
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_Town.init();					
					_log.info("[Phantom Town]: Starded!");
					activeChar.sendMessage("SYS: Voce Ativou phanton town...");
				}
				
			}, 1);
		}
		else if (command.equals("admin_spawn_phantomtvt"))
		{
			AdminAdmin.showMainPage(activeChar, "main_menu.htm");
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_TvT.init();					
					_log.info("[Phantom TvT]: Starded!");
					activeChar.sendMessage("SYS: Voce Ativou phanton TvT...");
				}
				
			}, 1);
		}
		else if (command.equals("admin_spawn_phantomfarm"))
		{
			AdminAdmin.showMainPage(activeChar, "main_menu.htm");
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_Farm.init();
					_log.info("[Phantom Farm]: Starded!");
					activeChar.sendMessage("SYS: Voce Ativou phanton farm...");
				}
				
			}, 1);
		}
		else if (command.equals("admin_spawn_phantom"))
		{
			AdminAdmin.showMainPage(activeChar, "main_menu.htm");
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_Attack.init();
					_log.info("[Phantom PvP]: Starded!");
					activeChar.sendMessage("SYS: Voce Ativou phanton PvP...");
				}
				
			}, 1);	    
		}
		else if (command.equals("admin_spawn_archer"))
		{
			AdminAdmin.showMainPage(activeChar, "main_menu.htm");
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					Phantom_Archers.init();
					_log.info("[Phantom Archers]: Starded!");
					activeChar.sendMessage("SYS: Voce Ativou phanton Archers...");
				}
				
			}, 1);	    
		}
		return true;
	}
	
	
	private static void initEventArena()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				com.l2jmega.events.ArenaTask.SpawnEvent();
			}
			
		}, 1);
	}
	
	private static void finishEventArena()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				com.l2jmega.events.ArenaTask.finishEvent();
			}
			
		}, 1);
	}
	
	private static void initPartyZone()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				com.l2jmega.events.PartyZoneTask.SpawnEvent();
			}
		}, 1);
	}
	
	private static void finishPartyZone()
	{
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				com.l2jmega.events.PartyZoneTask.finishEvent();
			}
		}, 1);
	}
	
	public static final void showHtml(Player player) {
		String msg = HtmCache.getInstance().getHtm("data/html/mods/free_vip.htm");
		StringBuilder menu = new StringBuilder(100);
		StringUtil.append(menu, new String[] { "<a action=\"link BO", "1", "\">", "VIP FREE", "</a><br>" });
		msg = msg.replaceAll("%menu%", menu.toString());
		player.sendPacket(new TutorialShowHtml(msg));
	}
	
	public static final void onVIPLink(Player player, String request) {
		if (request == null || !request.startsWith("BO"))
			return; 
		try {
			int val = Integer.parseInt(request.substring(2));
			if (val == 1 && !VipManager.getInstance().hasVipPrivileges(player.getObjectId())) {
				VipManager.getInstance().addVip(player.getObjectId(), System.currentTimeMillis() + 86400000L);
				long duration = VipManager.getInstance().getVipDuration(player.getObjectId());
				player.sendChatMessage(0, 0, "SYS", "Your vip privileges ends at " + (new SimpleDateFormat("dd MMM, HH:mm")).format(new Date(duration)));
				ThreadPool.schedule(new VIPINFO(player), 5000L);
			} else {
				player.sendMessage("SYS: VOCE NAO PODE FAZER ISSO !");
			} 
		} catch (NumberFormatException numberFormatException) {}
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static final void showNewsHtml(Player player) {
		String msg = HtmCache.getInstance().getHtm("data/html/mods/newsHtml.htm");
		player.sendPacket(new TutorialShowHtml(msg));
	}
	
	public static final void NewsLink5(Player player, String request) {
		if (request.startsWith("info")){
			VoicedMenu.showInfoHtml(player);
		}
	}
	
	public static final void NewsLink(Player player, String request) {
		if (request.startsWith("news")){
			player.sendPacket(new OpenUrl(""+ Config.NEWS_1 +""));
		}
	}
	public static final void NewsLink2(Player player, String request) {
		if (request.startsWith("news2")){
			player.sendPacket(new OpenUrl(""+ Config.NEWS_2 +""));
		}			    
	}
	public static final void NewsLink3(Player player, String request) {
		
		if (request.startsWith("news3")) {
			player.sendPacket(new OpenUrl("" + Config.NEWS_3 + ""));
		}
		
	}
	public static final void NewsLink4(Player player, String request) {
		
		if (request.startsWith("news4")) {
			player.sendPacket(new OpenUrl("" + Config.NEWS_4 + ""));
		}
		
	}
	
	
	public static void VoteZone() {
		ThreadPool.schedule(new Runnable() {
			@Override
			public void run() {
				NewZoneVote._started = true;
				NewZoneVote._vote = true;
				for (Player player : World.getInstance().getPlayers()) {
					if (player != null && player.isOnline() && !player.isInObserverMode())
						VoteZoneCommands.ShowHtml(player); 
				} 
				NewZoneVote.waiter((Config.VOTE_PVPZONE_TIME * 60 * 1000));
				AdminCustom._log.info("[VoteZone]: Starded!");
			}
		},  1L);
	}
	
	static class PhantomWalk2 implements Runnable
	{
		Player _phantom;
		
		public PhantomWalk2(Player paramPlayer)
		{
			_phantom = paramPlayer;
		}
		
		@Override
		public void run()
		{
			if (!_phantom.isDead())
			{
				if (_phantom.isSpawnProtected())
					_phantom.setSpawnProtection(false);
				
				_phantom.walkToNpc();
				
				//startWalk2(_phantom);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static class SingletonHolder
	{
		protected static final AdminCustom _instance = new AdminCustom();
	}
	
}
