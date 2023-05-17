package com.l2jmega.gameserver.model.actor.instance;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import com.l2jmega.commons.math.MathUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.events.ArenaTask;
import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.data.xml.TeleportLocationData;
import com.l2jmega.gameserver.instancemanager.CastleManager;
import com.l2jmega.gameserver.instancemanager.VoteZone;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.entity.Siege;
import com.l2jmega.gameserver.model.location.TeleportLocation;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public final class Gatekeeper extends Folk
{
	private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	private static final int COND_OWNER = 2;
	private static final int COND_REGULAR = 3;
	static L2Spawn _Spawn_guardian;
	static L2Spawn _Spawn_guardian2;
	private static final String partyteleport = "data/html/teleporter/custom/";

	
	public Gatekeeper(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You can not do that!");
			return;
		}
		
		if (player.isArenaProtection())
		{
			if (!ArenaTask.is_started())
				player.setArenaProtection(false);
			else
				player.sendMessage("Remove your participation from the Tournament event!");
			return;
		}
		
		if (command.startsWith("barakiel"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 85320 + Rnd.get(100);
					int y = -85112 + Rnd.get(100);
					member.teleToLocation(x, y, -3568, 0);
				}
			}
		}
		
		if (command.startsWith("brakki"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 142872 + Rnd.get(100);
					int y = -82840 + Rnd.get(100);
					member.teleToLocation(x, y, -6480, 0);
				}
			}
		}
		
		if (command.startsWith("tayr"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 149544 + Rnd.get(100);
					int y = -81528 + Rnd.get(100);
					member.teleToLocation(x, y, -5624, 0);
				}
			}
		}
		
		if (command.startsWith("hekaton"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 150765 + Rnd.get(100);
					int y = -77842 + Rnd.get(100);
					member.teleToLocation(x, y, -4955, 0);
				}
			}
		}
		
		if (command.startsWith("shadith"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 114088 + Rnd.get(100);
					int y = -44888 + Rnd.get(100);
					member.teleToLocation(x, y, -2704, 0);
				}
			}
		}
		
		if (command.startsWith("horus"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 103160 + Rnd.get(100);
					int y = -37176 + Rnd.get(100);
					member.teleToLocation(x, y, -1392, 0);
				}
			}
		}
		
		if (command.startsWith("moss"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 110040 + Rnd.get(100);
					int y = -39640 + Rnd.get(100);
					member.teleToLocation(x, y, -1800, 0);
				}
			}
		}
		
		if (command.startsWith("baium"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 114280 + Rnd.get(100);
					int y = 17160 + Rnd.get(100);
					member.teleToLocation(x, y, 9000, 0);
				}
			}
		}
		
		if (command.startsWith("queen"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -21576 + Rnd.get(100);
					int y = 179768 + Rnd.get(100);
					member.teleToLocation(x, y, -5832, 0);
				}
			}
		}
		
		if (command.startsWith("antharas"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 152312 + Rnd.get(100);
					int y = 120344 + Rnd.get(100);
					member.teleToLocation(x, y, -3809, 0);
				}
			}
		}
		
		if (command.startsWith("valakas"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 181928 + Rnd.get(100);
					int y = -116088 + Rnd.get(100);
					member.teleToLocation(x, y, -3336, 0);
				}
			}
		}
		
		if (command.startsWith("zaken"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 56280 + Rnd.get(100);
					int y = 216664 + Rnd.get(100);
					member.teleToLocation(x, y, -3464, 0);
				}
			}
		}
		
		if (command.startsWith("core"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 18895 + Rnd.get(100);
					int y = 112955 + Rnd.get(100);
					member.teleToLocation(x, y, -6579, 0);
				}
			}
		}
		
		if (command.startsWith("orfen"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 50567 + Rnd.get(100);
					int y = 21665 + Rnd.get(100);
					member.teleToLocation(x, y, -5235, 0);
				}
			}
		}
		
		if (command.startsWith("sailren"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 21952 + Rnd.get(100);
					int y = -8669 + Rnd.get(100);
					member.teleToLocation(x, y, -2660, 0);
				}
			}
		}
		
		if (command.startsWith("frintezza"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = 187928 + Rnd.get(100);
					int y = -75192 + Rnd.get(100);
					member.teleToLocation(x, y, -2744, 0);
				}
			}
		}
		
		if (command.startsWith("abandoned_1"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -51365 + Rnd.get(100);
					int y = 135824 + Rnd.get(100);
					member.teleToLocation(x, y, -2928, 0);
				}
			}
		}
		
		if (command.startsWith("abandoned_2"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -46698 + Rnd.get(100);
					int y = 140823 + Rnd.get(100);
					member.teleToLocation(x, y, -2946, 0);
				}
			}
		}
		
		if (command.startsWith("abandoned_3"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -49769 + Rnd.get(100);
					int y = 146929 + Rnd.get(100);
					member.teleToLocation(x, y, -2800, 0);
				}
			}
		}
		
		if (command.startsWith("abandoned_4"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -54483 + Rnd.get(100);
					int y = 146560 + Rnd.get(100);
					member.teleToLocation(x, y, -2876, 0);
				}
			}
		}
		
		if (command.startsWith("abandoned_5"))
		{
			if (!player.isInParty())
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Party.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else if (!player.getParty().isLeader(player))
			{
				final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "No-Leader.htm");
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				html.setHtml(htmContent);
				html.replace("%objectId%", getObjectId());
				html.replace("%npcname%", getName());
				player.sendPacket(html);
				return;
			}
			else
			{
				final List<Player> party = player.getParty().getPartyMembers();
				
				// Check players conditions.
				for (Player member : party)
				{
					if (!MathUtil.checkIfInRange(700, player, member, true))
					{
						final String htmContent = HtmCache.getInstance().getHtm(partyteleport + "Far-Member.htm");
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						html.setHtml(htmContent);
						html.replace("%objectId%", getObjectId());
						html.replace("%npcname%", getName());
						player.sendPacket(html);
						return;
					}
				}
				
				// Teleport members.
				for (Player member : party)
				{
					int x = -56006 + Rnd.get(100);
					int y = 145002 + Rnd.get(100);
					member.teleToLocation(x, y, -2162, 0);
				}
			}
		}
		
		if (command.startsWith("goto"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (st.countTokens() <= 0)
				return;
			
			final int condition = validateCondition(player);
			if (condition == COND_REGULAR || condition == COND_OWNER)
			{
				if (player.isAlikeDead())
					return;
				
				final TeleportLocation list = TeleportLocationData.getInstance().getTeleportLocation(Integer.parseInt(st.nextToken()));
				if (list == null)
					return;
				
				final Siege siegeOnTeleportLocation = CastleManager.getInstance().getSiege(list.getX(), list.getY(), list.getZ());
				if (siegeOnTeleportLocation != null && siegeOnTeleportLocation.isInProgress())
				{
					player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
					return;
				}
				
				if (!Config.PLAYER_FLAG_CAN_USE_GK && !player.isGM() && player.getPvpFlag() > 0 && !player.isInsideZone(ZoneId.CUSTOM))
				{
					player.sendMessage("Don't run from PvP! You will be able to use the teleporter only after your flag is gone.");
					return;
				}
				
				if (!Config.KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0)
				{
					player.sendMessage("Go away, you're not welcome here.");
					return;
				}
				
				if (list.isNoble() && !player.isNoble())
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile("data/html/teleporter/nobleteleporter-no.htm");
					html.replace("%objectId%", getObjectId());
					html.replace("%npcname%", getName());
					player.sendPacket(html);
					return;
				}
				
				Calendar cal = Calendar.getInstance();
				int price = list.getPrice();
				
				if (!list.isNoble())
				{
					if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && cal.get(Calendar.HOUR_OF_DAY) <= 23 && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7))
						price /= 2;
				}
				
				if (player.destroyItemByItemId("Teleport ", (list.isNoble()) ? 6651 : 57, price, this, true))
					player.teleToLocation(list, 100);
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (command.startsWith("Chat"))
		{
			Calendar cal = Calendar.getInstance();
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			if (val == 1 && cal.get(Calendar.HOUR_OF_DAY) >= 20 && cal.get(Calendar.HOUR_OF_DAY) <= 23 && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7))
			{
				showHalfPriceHtml(player);
				return;
			}
			showChatWindow(player, val);
		}
		else if (command.startsWith("vote_zone"))
		{
			if (VoteZone.is_zone_1())
				player.teleToLocation(Config.ZONE_1X, Config.ZONE_1Y, Config.ZONE_1Z, 50);
			else if (VoteZone.is_zone_2())
				player.teleToLocation(Config.ZONE_2X, Config.ZONE_2Y, Config.ZONE_2Z, 50);
			else if (VoteZone.is_zone_3())
				player.teleToLocation(Config.ZONE_3X, Config.ZONE_3Y, Config.ZONE_3Z, 50);
			else if (VoteZone.is_zone_4())
				player.teleToLocation(Config.ZONE_4X, Config.ZONE_4Y, Config.ZONE_4Z, 50);
			else if (VoteZone.is_zone_5())
				player.teleToLocation(Config.ZONE_5X, Config.ZONE_5Y, Config.ZONE_5Z, 50);
			else if (VoteZone.is_zone_6())
				player.teleToLocation(Config.ZONE_6X, Config.ZONE_6Y, Config.ZONE_6Z, 50);
			else if (VoteZone.is_zone_7())
				player.teleToLocation(Config.ZONE_7X, Config.ZONE_7Y, Config.ZONE_7Z, 50);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/teleporter/" + filename + ".htm";
	}
	
	private void showHalfPriceHtml(Player player)
	{
		if (player == null)
			return;
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		String content = HtmCache.getInstance().getHtm("data/html/teleporter/half/" + getNpcId() + ".htm");
		if (content == null)
			content = HtmCache.getInstance().getHtmForce("data/html/teleporter/" + getNpcId() + "-1.htm");
		
		html.setHtml(content);
		html.replace("%objectId%", getObjectId());
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		
		int condition = validateCondition(player);
		if (condition == COND_REGULAR)
		{
			super.showChatWindow(player);
			return;
		}
		
		if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
			filename = "data/html/teleporter/castleteleporter-busy.htm";
		else if (condition == COND_OWNER)
			filename = getHtmlPath(getNpcId(), 0);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	  public static void unspawn_guardian()
	  {
	    if ((_Spawn_guardian == null) || (_Spawn_guardian.getNpc() == null)) {
	      return;
	    }
	    _Spawn_guardian.getNpc().deleteMe();
	    _Spawn_guardian.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_Spawn_guardian, true);
	  }
	  
	  public static void unspawn_guardian2()
	  {
	    if ((_Spawn_guardian2 == null) || (_Spawn_guardian2.getNpc() == null)) {
	      return;
	    }
	    _Spawn_guardian2.getNpc().deleteMe();
	    _Spawn_guardian2.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_Spawn_guardian2, true);
	  }
	
	private int validateCondition(Player player)
	{
		if (getCastle() != null)
		{
			if (getCastle().getSiege().isInProgress())
				return COND_BUSY_BECAUSE_OF_SIEGE;
			
			if (player.getClan() != null && getCastle().getOwnerId() == player.getClanId())
				return COND_OWNER;
		}
		
		return COND_REGULAR;
	}
	
}