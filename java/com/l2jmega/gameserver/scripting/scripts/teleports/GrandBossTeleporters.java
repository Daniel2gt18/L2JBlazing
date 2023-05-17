package com.l2jmega.gameserver.scripting.scripts.teleports;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.DoorTable;
import com.l2jmega.gameserver.instancemanager.GrandBossManager;
import com.l2jmega.gameserver.instancemanager.ZoneManager;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.type.L2BossZone;
import com.l2jmega.gameserver.scripting.Quest;
import com.l2jmega.gameserver.scripting.QuestState;
import com.l2jmega.gameserver.scripting.ScriptManager;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Antharas;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Baium;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Sailren;
import com.l2jmega.gameserver.scripting.scripts.ai.individual.Valakas;

/**
 * This script leads behavior of multiple bosses teleporters.
 * <ul>
 * <li>13001, Heart of Warding : Teleport into Lair of Antharas</li>
 * <li>29055, Teleportation Cubic : Teleport out of Baium zone</li>
 * <li>31859, Teleportation Cubic : Teleport out of Lair of Antharas</li>
 * <li>31384, Gatekeeper of Fire Dragon : Opening some doors</li>
 * <li>31385, Heart of Volcano : Teleport into Lair of Valakas</li>
 * <li>31540, Watcher of Valakas Klein : Teleport into Hall of Flames</li>
 * <li>31686, Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano</li>
 * <li>31687, Gatekeeper of Fire Dragon : Opens doors to Heart of Volcano</li>
 * <li>31759, Teleportation Cubic : Teleport out of Lair of Valakas</li>
 * <li>31862, Angelic Vortex : Baium Teleport (3 different HTMs according of situation)</li>
 * <li>32107, Teleportation Cubic : Teleport out of Sailren Nest</li>
 * <li>32109, Shilen's Stone Statue : Teleport to Sailren Nest</li>
 * </ul>
 * @author Plim, original python script by Emperorc
 */
public class GrandBossTeleporters extends Quest
{
	private static final String qn = "GrandBossTeleporters";
	
	private static final Location BAIUM_IN = new Location(113100, 14500, 10077);
	private static final Location[] BAIUM_OUT =
	{
		new Location(83400, 147943, -3404),
	};
	
	private static final Location SAILREN_IN = new Location(27333, -6835, -1970);
	private static final Location[] SAILREN_OUT =
	{
		new Location(83400, 147943, -3404),
	};
	
	private static int _valakasPlayersCount = 0;
	
	public GrandBossTeleporters()
	{
		super(-1, "teleports");
		
		addFirstTalkId(29055, 31862);
		addStartNpc(13001, 29055, 31859, 31384, 31385, 31540, 31686, 31687, 31759, 31862, 32107, 32109);
		addTalkId(13001, 29055, 31859, 31384, 31385, 31540, 31686, 31687, 31759, 31862, 32107, 32109);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		st.setState(STATE_STARTED);
		
		if (event.equalsIgnoreCase("baium"))
		{
			// Player is mounted on a wyvern, cancel it.
			if (player.isFlying())
				htmltext = "31862-05.htm";
			// Player hasn't blooded fabric, cancel it.
			else if (!st.hasQuestItems(Config.QUEST_BAIUM))
				htmltext = "31862-03.htm";
			// All is ok, take the item and teleport the player inside.
			else
			{			
				if (player.getMountType() != 0)
					player.dismount();

				st.takeItems(Config.QUEST_BAIUM, 1);
				
				// allow entry for the player for the next 30 secs.
				ZoneManager.getInstance().getZoneById(110002, L2BossZone.class).allowPlayerEntry(player, 30);
				player.teleToLocation(BAIUM_IN, 0);
			}
		}
		else if (event.equalsIgnoreCase("baium_story"))
			htmltext = "31862-02.htm";
		else if (event.equalsIgnoreCase("baium_exit"))
			player.teleToLocation(Rnd.get(BAIUM_OUT), 100);
		else if (event.equalsIgnoreCase("31540"))
		{
			if (st.hasQuestItems(Config.QUEST_VALAKAS))
			{
				st.takeItems(Config.QUEST_VALAKAS, 1);
				player.teleToLocation(189768, -105592, -779, 0);
				st.set("allowEnter", "1");
			}
			else
				htmltext = "31540-06.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(qn);
		if (st == null)
			st = newQuestState(player);
		
		st.setState(STATE_STARTED);
		
		switch (npc.getNpcId())
		{
			case 29055:
				htmltext = "29055-01.htm";
				break;
			
			case 31862:
				final int status = GrandBossManager.getInstance().getBossStatus(29020);
				if (status == Baium.AWAKE)
					htmltext = "31862-01.htm";
				else if (status == Baium.DEAD)
					htmltext = "31862-04.htm";
				else
					htmltext = "31862-00.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return null;
		
		st.setState(STATE_STARTED);
		
		int status;
		switch (npc.getNpcId())
		{
			case 13001:
				status = GrandBossManager.getInstance().getBossStatus(Antharas.ANTHARAS);
				if (status == Antharas.FIGHTING)
					htmltext = "13001-02.htm";
				else if (status == Antharas.DEAD)
					htmltext = "13001-01.htm";
				else if (status == Antharas.DORMANT || status == Antharas.WAITING)
				{
					if (st.hasQuestItems(Config.QUEST_ANTHARAS))
					{
						if (player.getMountType() != 0)
							player.dismount();

						st.takeItems(Config.QUEST_ANTHARAS, 1);
						ZoneManager.getInstance().getZoneById(110001, L2BossZone.class).allowPlayerEntry(player, 30);
						
						player.teleToLocation(175300 + Rnd.get(-350, 350), 115180 + Rnd.get(-1000, 1000), -7709, 0);
						
						if (status == Antharas.DORMANT)
						{
							GrandBossManager.getInstance().setBossStatus(Antharas.ANTHARAS, Antharas.WAITING);
							ScriptManager.getInstance().getQuest("Antharas").startQuestTimer("beginning", Config.WAIT_TIME_ANTHARAS, null, null, false);
						}
					}
					else
						htmltext = "13001-03.htm";
				}
				break;
			
			case 31859:
				player.teleToLocation(79800 + Rnd.get(600), 151200 + Rnd.get(1100), -3534, 0);
				break;
			
			case 31385:
				status = GrandBossManager.getInstance().getBossStatus(Valakas.VALAKAS);
				if (status == 0 || status == 1)
				{
					if (_valakasPlayersCount >= 500)
						htmltext = "31385-03.htm";
					else if (st.getInt("allowEnter") == 1)
					{
						st.unset("allowEnter");
						ZoneManager.getInstance().getZoneById(110010, L2BossZone.class).allowPlayerEntry(player, 30);
						
						if (player.getMountType() != 0)
							player.dismount();
					
						player.teleToLocation(204328, -111874, 70, 300);
						
						_valakasPlayersCount++;
						
						if (status == Valakas.DORMANT)
						{
							GrandBossManager.getInstance().setBossStatus(Valakas.VALAKAS, Valakas.WAITING);
							ScriptManager.getInstance().getQuest("Valakas").startQuestTimer("beginning", Config.WAIT_TIME_VALAKAS, null, null, false);
						}
					}
					else
						htmltext = "31385-04.htm";
				}
				else if (status == 2)
					htmltext = "31385-02.htm";
				else
					htmltext = "31385-01.htm";
				break;
			
			case 31384:
				DoorTable.getInstance().getDoor(24210004).openMe();
				break;
			
			case 31686:
				DoorTable.getInstance().getDoor(24210006).openMe();
				break;
			
			case 31687:
				DoorTable.getInstance().getDoor(24210005).openMe();
				break;
			
			case 31540:
				if (_valakasPlayersCount < 50)
					htmltext = "31540-01.htm";
				else if (_valakasPlayersCount < 100)
					htmltext = "31540-02.htm";
				else if (_valakasPlayersCount < 150)
					htmltext = "31540-03.htm";
				else if (_valakasPlayersCount < 500)
					htmltext = "31540-04.htm";
				else
					htmltext = "31540-05.htm";
				break;
			
			case 31759:
				player.teleToLocation(83400, 147943, -3404, 0);
				break;
			
			case 32107:
				player.teleToLocation(Rnd.get(SAILREN_OUT), 100);
				break;
			
			case 32109:
				status = GrandBossManager.getInstance().getBossStatus(Sailren.SAILREN);
				if (status == Sailren.FIGHTING)
					htmltext = "32109-01.htm";
				else if (status == Sailren.DEAD)
					htmltext = "32109-02.htm";
				else if (status == Sailren.DORMANT || status == Sailren.WAITING)
				{
					if (st.hasQuestItems(Config.QUEST_SAILREN))
					{
						if (player.getMountType() != 0)
							player.dismount();

						st.takeItems(Config.QUEST_SAILREN, 1);
						ZoneManager.getInstance().getZoneById(110015, L2BossZone.class).allowPlayerEntry(player, 30);
	                    player.teleToLocation(SAILREN_IN, 100);
						
						if (status == Sailren.DORMANT)
						{
							GrandBossManager.getInstance().setBossStatus(Sailren.SAILREN, Sailren.WAITING);
							ScriptManager.getInstance().getQuest("Sailren").startQuestTimer("beginning", Config.WAIT_TIME_SAILREN, null, null, false);
						}
					}
					else
						htmltext = "32109-03.htm";
					
		
					break;
				}
		}
		
		return htmltext;
	}
	
	public static void waiter(long interval)
	{
		long startWaiterTime = System.currentTimeMillis();
		int seconds = (int) (interval / 1000);
		
		while (startWaiterTime + interval > System.currentTimeMillis() && GrandBossManager._announce)
		{
			seconds--; // Here because we don't want to see two time announce at the same time
			
			switch (seconds)
			{
				case 3600: // 1 hour left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in " + seconds / 60 / 60 + " hour(s)!");
					break;
				case 1799: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in 30 minute(s) !");
					break;
				case 599: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in 10 minute(s) !");
					break;
				case 299: // 10 minutes left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in 5 minute(s) !");
					break;
				
				case 1500: // 25 minutes left
				case 1200: // 20 minutes left
				case 900: // 15 minutes left
				case 540: // 9 minutes left
				case 480: // 8 minutes left
				case 420: // 7 minutes left
				case 360: // 6 minutes left
				case 240: // 4 minutes left
				case 180: // 3 minutes left
				case 120: // 2 minutes left
				case 60: // 1 minute left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in " + seconds / 60 + " minute(s) !");
					break;
				case 30: // 30 seconds left
				case 15: // 15 seconds left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in " + seconds + " second(s) !");
					break;
				
				case 6: // 3 seconds left
				case 5: // 3 seconds left
				case 4: // 3 seconds left
				case 3: // 2 seconds left
				case 2: // 1 seconds left
					GrandBossManager.AnnounceGrandBoss("Spawn Antharas in " + (seconds - 1) + " second(s) !");
					break;
				
				case 1: // 1 seconds left
				{
					if (GrandBossManager._announce)
						GrandBossManager.AnnounceGrandBoss("Antharas Is alive, teleport to boss closed !");
					GrandBossManager._announce = false;
				}
					break;
			}
			
			long startOneSecondWaiterStartTime = System.currentTimeMillis();
			
			// Only the try catch with Thread.sleep(1000) give bad countdown on high wait times
			while (startOneSecondWaiterStartTime + 1000 > System.currentTimeMillis())
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException ie)
				{
				}
			}
		}
	}
}