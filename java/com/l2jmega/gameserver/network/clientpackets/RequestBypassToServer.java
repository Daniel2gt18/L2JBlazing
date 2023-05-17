package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.BalancerEdit;
import com.l2jmega.gameserver.communitybbs.CommunityBoard;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.data.xml.AdminData;
import com.l2jmega.gameserver.handler.AdminCommandHandler;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.handler.VoicedCommandHandler;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminBalancer;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminEditChar;
import com.l2jmega.gameserver.instancemanager.VoteZone;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Gatekeeper;
import com.l2jmega.gameserver.model.actor.instance.OlympiadManagerNpc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.Sex;
import com.l2jmega.gameserver.model.entity.Hero;
import com.l2jmega.gameserver.model.olympiad.Olympiad;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.HennaInfo;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.util.ChangeAllyNameLog;
import com.l2jmega.gameserver.util.ChangeClanNameLog;
import com.l2jmega.gameserver.util.ChangeNameLog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.random.Rnd;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	
	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.SERVER_BYPASS) && !_command.startsWith("voiced_getbuff"))
			return;
		
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		
		if (_command.isEmpty())
		{
			_log.info(activeChar.getName() + " sent an empty requestBypass packet.");
			activeChar.logout();
			return;
		}
		
		try
		{
			if (_command.startsWith("admin_"))
			{
				String command = _command.split(" ")[0];
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				if (ach == null)
				{
					if (activeChar.isGM())
						activeChar.sendMessage("The command " + command.substring(6) + " doesn't exist.");
					
					_log.warning("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminData.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access rights to use this command.");
					_log.warning(activeChar.getName() + " tried to use admin command " + command + " without proper Access Level.");
					return;
				}
				
				if (Config.GMAUDIT)
					GMAUDIT_LOG.info(activeChar.getName() + " [" + activeChar.getObjectId() + "] used '" + _command + "' command on: " + ((activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "none"));
				
				ach.useAdminCommand(_command, activeChar);
			}
			else if (_command.startsWith("voiced_"))
			{
				String command = _command.split(" ")[0];
				
				IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getHandler(_command.substring(7));
				
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command.substring(7) + " does not exist!");
					_log.warning("No handler registered for command '" + _command + "'");
					return;
				}
				
				ach.useVoicedCommand(_command.substring(7), activeChar, null);
			}
			else if (_command.startsWith("player_help "))
			{
				playerHelp(activeChar, _command.substring(12));
			}
			else if (_command.startsWith("tele_tournament"))
			{
				if (activeChar.isOlympiadProtection())
				{
					activeChar.sendMessage("Are you participating in the Olympiad..");
					return;
				}
				
				for (Gatekeeper knownChar : activeChar.getKnownTypeInRadius(Gatekeeper.class, 300))
				{
					if (knownChar != null)
					{
						activeChar.teleToLocation(Config.Tournament_locx + Rnd.get(-100, 100), Config.Tournament_locy + Rnd.get(-100, 100), Config.Tournament_locz, 0);
						activeChar.setTournamentTeleport(true);
					}
				}
			}
			else if (_command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				activeChar.setIsUsingCMultisell(false);
				
				int endOfId = _command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
					id = _command.substring(4, endOfId);
				else
					id = _command.substring(4);
				
				try
				{
					final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
					if (_command.substring(endOfId + 1).startsWith("tvt_player_join "))
					{
						final String teamName = _command.substring(endOfId + 1).substring(16);
						
						if (TvT.is_joining())
							TvT.addPlayer(activeChar, teamName);
						else
							activeChar.sendMessage("The event is already started. You can not join now!");
					}
					else if (_command.substring(endOfId + 1).startsWith("tvt_player_leave"))
					{
						if (TvT.is_joining())
							TvT.removePlayer(activeChar);
						else
							activeChar.sendMessage("The event is already started. You can not leave now!");
					}
					else if (_command.substring(endOfId + 1).startsWith("tvt_watch"))
					{
						if (activeChar._inEventTvT)
							return;
						else if (TvT.is_teleport() || TvT.is_started())
						{
							activeChar.setEventObserver(true);
							activeChar.enterTvTObserverMode(Config.TVT_OBSERVER_X, Config.TVT_OBSERVER_Y, Config.TVT_OBSERVER_Z);
						}
						else
							activeChar.sendMessage("The event is Is offline.");
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_watch"))
					{
						if (activeChar._inEventCTF)
							return;
						else if (CTF.is_teleport() || CTF.is_started())
						{
							activeChar.setEventObserver(true);
							activeChar.enterTvTObserverMode(Config.CTF_OBSERVER_X, Config.CTF_OBSERVER_Y, Config.CTF_OBSERVER_Z);
						}
						else
							activeChar.sendMessage("The event is Is offline.");
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_player_join "))
					{
						final String teamName = _command.substring(endOfId + 1).substring(16);
						
						if (CTF.is_joining())
							CTF.addPlayer(activeChar, teamName);
						else
							activeChar.sendMessage("The event is already started. You can not join now!");
					}
					else if (_command.substring(endOfId + 1).startsWith("ctf_player_leave"))
					{
						if (CTF.is_joining())
							CTF.removePlayer(activeChar);
						else
							activeChar.sendMessage("The event is already started. You can not leave now!");
					}
					
					else if (object != null && object instanceof Npc && endOfId > 0 && ((Npc) object).canInteract(activeChar))
						((Npc) object).onBypassFeedback(activeChar, _command.substring(endOfId + 1));
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
				}
			}
			else if (_command.startsWith("droplist"))
			{
				StringTokenizer st = new StringTokenizer(_command, " ");
				st.nextToken();
				
				int npcId = Integer.parseInt(st.nextToken());
				int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				
				Npc.sendNpcDrop(activeChar, npcId, page);
			}
			else if (_command.startsWith("pvpzone"))
			{
				if (!activeChar.isGM())
					return;
				
				if (VoteZone.is_zone_1())
					activeChar.teleToLocation(Config.ZONE_1X + Rnd.get(-80, 80), Config.ZONE_1Y + Rnd.get(-80, 80), Config.ZONE_1Z, 0);
				else if (VoteZone.is_zone_2())
					activeChar.teleToLocation(Config.ZONE_2X + Rnd.get(-80, 80), Config.ZONE_2Y + Rnd.get(-80, 80), Config.ZONE_2Z, 0);
				else if (VoteZone.is_zone_3())
					activeChar.teleToLocation(Config.ZONE_3X + Rnd.get(-80, 80), Config.ZONE_3Y + Rnd.get(-80, 80), Config.ZONE_3Z, 0);
				else if (VoteZone.is_zone_4())
					activeChar.teleToLocation(Config.ZONE_4X + Rnd.get(-80, 80), Config.ZONE_4Y + Rnd.get(-80, 80), Config.ZONE_4Z, 0);
				else if (VoteZone.is_zone_5())
					activeChar.teleToLocation(Config.ZONE_5X + Rnd.get(-80, 80), Config.ZONE_5Y + Rnd.get(-80, 80), Config.ZONE_5Z, 0);
				else if (VoteZone.is_zone_6())
					activeChar.teleToLocation(Config.ZONE_6X + Rnd.get(-80, 80), Config.ZONE_6Y + Rnd.get(-80, 80), Config.ZONE_6Z, 0);
				else if (VoteZone.is_zone_7())
					activeChar.teleToLocation(Config.ZONE_7X + Rnd.get(-80, 80), Config.ZONE_7Y + Rnd.get(-80, 80), Config.ZONE_7Z, 0);
			}
			else if (_command.startsWith("tournament"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/tournament_zone.htm");
				html.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));
				activeChar.sendPacket(html);
			}	
			else if (_command.startsWith("name_change"))
			{
				final String clientInfo = activeChar.getClient().toString();
				final String ip = clientInfo.substring(clientInfo.indexOf(" - IP: ") + 7, clientInfo.lastIndexOf("]"));
				
				try
				{
					String name = _command.substring(12);
					
					if (name.length() > 16)
					{
						activeChar.sendMessage("The chosen name cannot exceed 16 characters in length.");
						return;
					}
					
					if (name.length() < 3)
					{
						activeChar.sendMessage("Your name can not be mention that 3 characters in length.");
						return;
					}
					
					if (!StringUtil.isValidPlayerName(name))
					{
						activeChar.sendMessage("The new name doesn't fit with the regex pattern.");
						return;
					}
					
					if (PlayerNameTable.getInstance().getPlayerObjectId(name) > 0)
					{
						activeChar.sendMessage("The chosen name already exists.");
						return;
					}
					
					if (activeChar.destroyItemByItemId("Name Change", activeChar.getNameChangeItemId(), 1, null, true))
					{
						ChangeNameLog.auditGMAction(activeChar.getObjectId(), activeChar.getName(), name, ip);
						
						for (Player gm : World.getAllGMs())
							gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "[Name]", activeChar.getName() + " mudou o nome para [" + name + "]"));
						
						activeChar.setName(name);
						PlayerNameTable.getInstance().updatePlayerData(activeChar, false);
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your name has been changed.", 6000, 0x02, true));
						activeChar.broadcastUserInfo();
						activeChar.store();
						activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Fill out the field correctly.");
				}
			}
			else if (_command.startsWith("_clan_name_"))
			{
				final String clientInfo = activeChar.getClient().toString();
				final String ip = clientInfo.substring(clientInfo.indexOf(" - IP: ") + 7, clientInfo.lastIndexOf("]"));
				
				try
				{
					
					String name = _command.substring(12);
					
					if (name.length() < 2 || name.length() > 16)
					{
						activeChar.sendPacket(SystemMessageId.CLAN_NAME_LENGTH_INCORRECT);
						return;
					}
					
					if (!StringUtil.isAlphaNumeric(name))
					{
						activeChar.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
						return;
					}
					
					if (ClanTable.getInstance().getClanByName(name) != null)
					{
						// clan name is already taken
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS).addString(name));
						return;
					}
					final Clan clanold = activeChar.getClan();
					
					if (activeChar.destroyItemByItemId("", activeChar.getClanNameChangeItemId(), 1, null, true))
					{
						ChangeClanNameLog.auditGMAction(activeChar.getObjectId(), clanold.getName(), name, ip);
						
						for (Player gm : World.getAllGMs())
							gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "[ClanName]", activeChar.getName() + " mudou o nome do clan para [" + name + "]"));
						
						for (Clan clan : ClanTable.getInstance().getClans())
						{
								clan.setClanName(name);
								clan.updateClanInDB();
							
						}
						activeChar.broadcastUserInfo();
						activeChar.store();
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your Clan name has been changed.", 6000, 0x02, true));
						activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Fill out the field correctly.");
				}
			}
			else if (_command.startsWith("_ally_name_"))
			{
				final String clientInfo = activeChar.getClient().toString();
				final String ip = clientInfo.substring(clientInfo.indexOf(" - IP: ") + 7, clientInfo.lastIndexOf("]"));
				
				try
				{
					
					String name = _command.substring(12);
					

					if (!StringUtil.isAlphaNumeric(name))
					{
						activeChar.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
						return;
					}
					
					if (name.length() > 16 || name.length() < 2)
					{
						activeChar.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME_LENGTH);
						return;
					}
					
					if (ClanTable.getInstance().isAllyExists(name))
					{
						activeChar.sendPacket(SystemMessageId.ALLIANCE_ALREADY_EXISTS);
						return;
					}
					final Clan clanold = activeChar.getClan();
					
					if (activeChar.destroyItemByItemId("", activeChar.getAllyNameChangeItemId(), 1, null, true))
					{
						ChangeAllyNameLog.auditGMAction(activeChar.getObjectId(), clanold.getAllyName(), name, ip);
						
						for (Player gm : World.getAllGMs())
							gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "[AllyName]", activeChar.getName() + " mudou o nome da Ally para [" + name + "]"));
						
						for (Clan clan : ClanTable.getInstance().getClans())
						{
								clan.setAllyName(name);
								clan.updateClanInDB();
							
						}
						activeChar.broadcastUserInfo();
						activeChar.store();
						activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your Ally name has been changed.", 6000, 0x02, true));
						activeChar.sendPacket(new PlaySound("ItemSound.quest_finish"));
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Fill out the field correctly.");
				}
			}
			else if (this._command.startsWith("classe_change")) {
				StringTokenizer st = new StringTokenizer(this._command);
				st.nextToken();
				String type = null;
				type = st.nextToken();
				try {
					if (activeChar.getBaseClass() != activeChar.getClassId().getId()) {
						activeChar.sendMessage("SYS: Voce precisa estar com sua Classe Base para usar este item.");
						activeChar.sendPacket(new ExShowScreenMessage("You is not with its base class.", 6000, 2, true));
						return;
					} 
					if (activeChar.isInOlympiadMode()) {
						activeChar.sendMessage("This Item Cannot Be Used On Olympiad Games.");
						return;
					} 
					ClassChangeCoin(activeChar, type);
				}
				catch (StringIndexOutOfBoundsException e)
				{
				}
			}
			else if (_command.startsWith("classe_index"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/Coin Custom/classes.htm");
				activeChar.sendPacket(html);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else if (_command.startsWith("change_sex"))
			{
				if (activeChar.destroyItemByItemId("Sex Change", activeChar.getSexChangeItemId(), 1, null, true))
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
					
					for (Player gm : World.getAllGMs())
						gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "SYS", activeChar.getName() + " acabou de trocar de Sexo."));
					
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							activeChar.logout();
						}
					}, 2000);
				}
				
			}
			// Navigate throught Manor windows
			else if (_command.startsWith("manor_menu_select?"))
			{
				WorldObject object = activeChar.getTarget();
				if (object instanceof Npc)
					((Npc) object).onBypassFeedback(activeChar, _command);
			}
			else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), _command);
			}
			else if (_command.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(_command))
					return;
				
				String[] str = _command.substring(6).trim().split(" ", 2);
				if (str.length == 1)
					activeChar.processQuestEvent(str[0], "");
				else
					activeChar.processQuestEvent(str[0], str[1]);
			}
			else if (_command.startsWith("bp_balance"))
			{
				String bp = _command.substring(11);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 1)
				{
					return;
				}
				
				int classId = Integer.parseInt(st.nextToken());
				
				AdminBalancer.sendBalanceWindow(classId, activeChar);
			}
			
			else if (_command.startsWith("bp_add"))
			{
				String bp = _command.substring(7);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 3)
				{
					return;
				}
				
				String stat = st.nextToken();
				int classId = Integer.parseInt(st.nextToken()),
					value = Integer.parseInt(st.nextToken());
				
				BalancerEdit.editStat(stat, classId, value, true);
				
				AdminBalancer.sendBalanceWindow(classId, activeChar);
			}
			
			else if (_command.startsWith("bp_rem"))
			{
				String bp = _command.substring(7);
				StringTokenizer st = new StringTokenizer(bp);
				
				if (st.countTokens() != 3)
				{
					return;
				}
				
				String stat = st.nextToken();
				int classId = Integer.parseInt(st.nextToken()),
					value = Integer.parseInt(st.nextToken());
				
				BalancerEdit.editStat(stat, classId, value, false);
				
				AdminBalancer.sendBalanceWindow(classId, activeChar);
			}
			else if (_command.startsWith("_match"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroFights(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("_diary"))
			{
				String params = _command.substring(_command.indexOf("?") + 1);
				StringTokenizer st = new StringTokenizer(params, "&");
				int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
				int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
				int heroid = Hero.getInstance().getHeroByClass(heroclass);
				if (heroid > 0)
					Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
			}
			else if (_command.startsWith("arenachange")) // change
			{
				final boolean isManager = activeChar.getCurrentFolkNPC() instanceof OlympiadManagerNpc;
				if (!isManager)
				{
					// Without npc, command can be used only in observer mode on arena
					if (!activeChar.isInObserverMode() || activeChar.isInOlympiadMode() || activeChar.getOlympiadGameId() < 0)
						return;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return;
				}
				
				final int arenaId = Integer.parseInt(_command.substring(12).trim());
				activeChar.enterOlympiadObserverMode(arenaId);
			}
			else if (_command.startsWith("tournament_observe"))
			{
				if (activeChar._inEventTvT || activeChar._inEventCTF)
				{
					activeChar.sendMessage("You already participated in the event tvt/ctf/pvp event!");
					return;
				}
				
				StringTokenizer st = new StringTokenizer(_command);
				st.nextToken();
				
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = Integer.parseInt(st.nextToken());
				// if ((x == -114413 && y == -213241 && z == -3331) || (x == -81748 && y == -245950 && z == -3331) || (x == -120324 && y == -225077 && z == -3331) && (activeChar.isInsideZone(ZoneId.TOURNAMENT) || activeChar.isInsideZone(ZoneId.ARENA_EVENT)))
				// {
				activeChar.setArenaObserv(true);
				activeChar.enterTvTObserverMode(x, y, z);
				// }
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Bad RequestBypassToServer: " + e, e);
		}
	}
	
	public static void Incorrect_item(Player activeChar)
	{
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/mods/Coin Custom/NoItem.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	
	private static void playerHelp(Player activeChar, String path)
	{
		if (path.indexOf("..") != -1)
			return;
		
		final StringTokenizer st = new StringTokenizer(path);
		final String[] cmd = st.nextToken().split("#");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/help/" + cmd[0]);
		if (cmd.length > 1)
			html.setItemId(Integer.parseInt(cmd[1]));
		html.disableValidation();
		activeChar.sendPacket(html);
	}
	
	private static void ClassChangeCoin(Player player, String command) {
		String nameclasse = player.getTemplate().getClassName();
		String type = command;
		if (type.equals("---SELECIONE---")) {
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/Coin Custom/classes.htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendMessage("Por favor, Selecione a Classe desejada para continuar.");
		} 
		if (type.equals("Duelist")) {
			if (player.getClassId().getId() == 88) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(88);
			if (!player.isSubClassActive())
				player.setBaseClass(88); 
			Finish(player);
		} 
		if (type.equals("DreadNought")) {
			if (player.getClassId().getId() == 89) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(89);
			if (!player.isSubClassActive())
				player.setBaseClass(89); 
			Finish(player);
		} 
		if (type.equals("Phoenix_Knight")) {
			if (player.getClassId().getId() == 90) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(90);
			if (!player.isSubClassActive())
				player.setBaseClass(90); 
			Finish(player);
		} 
		if (type.equals("Hell_Knight")) {
			if (player.getClassId().getId() == 91) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(91);
			if (!player.isSubClassActive())
				player.setBaseClass(91); 
			Finish(player);
		} 
		if (type.equals("Sagittarius")) {
			if (player.getClassId().getId() == 92) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(92);
			if (!player.isSubClassActive())
				player.setBaseClass(92); 
			Finish(player);
		} 
		if (type.equals("Adventurer")) {
			if (player.getClassId().getId() == 93) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(93);
			if (!player.isSubClassActive())
				player.setBaseClass(93); 
			Finish(player);
		} 
		if (type.equals("Archmage")) {
			if (player.getClassId().getId() == 94) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(94);
			if (!player.isSubClassActive())
				player.setBaseClass(94); 
			Finish(player);
		} 
		if (type.equals("Soultaker")) {
			if (player.getClassId().getId() == 95) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(95);
			if (!player.isSubClassActive())
				player.setBaseClass(95); 
			Finish(player);
		} 
		if (type.equals("Arcana_Lord")) {
			if (player.getClassId().getId() == 96) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(96);
			if (!player.isSubClassActive())
				player.setBaseClass(96); 
			Finish(player);
		} 
		if (type.equals("Cardinal")) {
			if (player.getClassId().getId() == 97) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(97);
			if (!player.isSubClassActive())
				player.setBaseClass(97); 
			Finish(player);
		} 
		if (type.equals("Hierophant")) {
			if (player.getClassId().getId() == 98) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(98);
			if (!player.isSubClassActive())
				player.setBaseClass(98); 
			Finish(player);
		} 
		if (type.equals("Eva_Templar")) {
			if (player.getClassId().getId() == 99) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(99);
			if (!player.isSubClassActive())
				player.setBaseClass(99); 
			Finish(player);
		} 
		if (type.equals("Sword_Muse")) {
			if (player.getClassId().getId() == 100) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(100);
			if (!player.isSubClassActive())
				player.setBaseClass(100); 
			Finish(player);
		} 
		if (type.equals("Wind_Rider")) {
			if (player.getClassId().getId() == 101) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(101);
			if (!player.isSubClassActive())
				player.setBaseClass(101); 
			Finish(player);
		} 
		if (type.equals("Moonli_Sentinel")) {
			if (player.getClassId().getId() == 102) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(102);
			if (!player.isSubClassActive())
				player.setBaseClass(102); 
			Finish(player);
		} 
		if (type.equals("Mystic_Muse")) {
			if (player.getClassId().getId() == 103) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(103);
			if (!player.isSubClassActive())
				player.setBaseClass(103); 
			Finish(player);
		} 
		if (type.equals("Elemental_Master")) {
			if (player.getClassId().getId() == 104) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(104);
			if (!player.isSubClassActive())
				player.setBaseClass(104); 
			Finish(player);
		} 
		if (type.equals("Eva_Saint")) {
			if (player.getClassId().getId() == 105) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(105);
			if (!player.isSubClassActive())
				player.setBaseClass(105); 
			Finish(player);
		} 
		if (type.equals("Shillien_Templar")) {
			if (player.getClassId().getId() == 106) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(106);
			if (!player.isSubClassActive())
				player.setBaseClass(106); 
			Finish(player);
		} 
		if (type.equals("Spectral_Dancer")) {
			if (player.getClassId().getId() == 107) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(107);
			if (!player.isSubClassActive())
				player.setBaseClass(107); 
			Finish(player);
		} 
		if (type.equals("Ghost_Hunter")) {
			if (player.getClassId().getId() == 108) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(108);
			if (!player.isSubClassActive())
				player.setBaseClass(108); 
			Finish(player);
		} 
		if (type.equals("Ghost_Sentinel")) {
			if (player.getClassId().getId() == 109) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(109);
			if (!player.isSubClassActive())
				player.setBaseClass(109); 
			Finish(player);
		} 
		if (type.equals("Storm_Screamer")) {
			if (player.getClassId().getId() == 110) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(110);
			if (!player.isSubClassActive())
				player.setBaseClass(110); 
			Finish(player);
		} 
		if (type.equals("Spectral_Master")) {
			if (player.getClassId().getId() == 111) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(111);
			if (!player.isSubClassActive())
				player.setBaseClass(111); 
			Finish(player);
		} 
		if (type.equals("Shillen_Saint")) {
			if (player.getClassId().getId() == 112) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(112);
			if (!player.isSubClassActive())
				player.setBaseClass(112); 
			Finish(player);
		} 
		if (type.equals("Titan")) {
			if (player.getClassId().getId() == 113) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(113);
			if (!player.isSubClassActive())
				player.setBaseClass(113); 
			Finish(player);
		} 
		if (type.equals("Grand_Khauatari")) {
			if (player.getClassId().getId() == 114) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(114);
			if (!player.isSubClassActive())
				player.setBaseClass(114); 
			Finish(player);
		} 
		if (type.equals("Dominator")) {
			if (player.getClassId().getId() == 115) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(115);
			if (!player.isSubClassActive())
				player.setBaseClass(115); 
			Finish(player);
		} 
		if (type.equals("Doomcryer")) {
			if (player.getClassId().getId() == 116) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(116);
			if (!player.isSubClassActive())
				player.setBaseClass(116); 
			Finish(player);
		} 
		if (type.equals("Fortune_Seeker")) {
			if (player.getClassId().getId() == 117) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(117);
			if (!player.isSubClassActive())
				player.setBaseClass(117); 
			Finish(player);
		} 
		if (type.equals("Maestro")) {
			if (player.getClassId().getId() == 118) {
				player.sendMessage("Desculpe, voce ja esta com a Classe " + nameclasse + ".");
				return;
			} 
			RemoverSkills(player);
			player.setClassId(118);
			if (!player.isSubClassActive())
				player.setBaseClass(118); 
			Finish(player);
		} 
	}
	
	private static void RemoverSkills(Player activeChar) {
		
		for (L2Skill s : activeChar.getSkills().values())
			activeChar.removeSkill(s);
		
		activeChar.destroyItemByItemId("Classe Change", activeChar.getClassChangeItemId(), 1, null, true);
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
		
		for (Player gm : World.getAllGMs())
			gm.sendPacket(new CreatureSay(0, Say2.SHOUT, "Chat Manager", activeChar.getName() + " acabou de trocar sua Classe Base."));
		
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
		
		activeChar.logout(true);
		
	}
}