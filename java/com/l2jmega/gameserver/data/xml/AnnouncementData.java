package com.l2jmega.gameserver.data.xml;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jmega.commons.data.xml.XMLDocument;
import com.l2jmega.commons.lang.StringUtil;

import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.CreatureSay;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.util.Broadcast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class loads and stores {@link Announcement}s, the key being dynamically generated on loading.<br>
 * As the storage is a XML, the whole XML needs to be regenerated on Announcement addition/deletion.
 */
public class AnnouncementData extends XMLDocument
{
	private static final String HEADER = "<?xml version='1.0' encoding='utf-8'?> \n<!-- \n@param String message - the message to be announced \n@param Boolean critical - type of announcement (true = critical,false = normal) \n@param Boolean auto - when the announcement will be displayed (true = auto,false = on player login) \n@param Integer initial_delay - time delay for the first announce (used only if auto=true;value in seconds) \n@param Integer delay - time delay for the announces following the first announce (used only if auto=true;value in seconds) \n@param Integer limit - limit of announces (used only if auto=true, 0 = unlimited) \n--> \n";
	
	private final Map<Integer, Announcement> _announcements = new ConcurrentHashMap<>();
	
	protected AnnouncementData()
	{
		load();
	}
	
	@Override
	protected void load()
	{
		loadDocument("./data/xml/announcements.xml");
		LOGGER.info("Loaded {} announcements.", _announcements.size());
	}
	
	@Override
	protected void parseDocument(Document doc, File file)
	{
		// First element is never read.
		final Node n = doc.getFirstChild();
		
		// Used as dynamic id.
		int id = 0;
		
		for (Node o = n.getFirstChild(); o != null; o = o.getNextSibling())
		{
			if (!"announcement".equalsIgnoreCase(o.getNodeName()))
				continue;
			
			final String message = o.getAttributes().getNamedItem("message").getNodeValue();
			if (message == null || message.isEmpty())
			{
				LOGGER.warn("The message is empty on an announcement. Ignoring it.");
				continue;
			}
			
			int value = Integer.valueOf(o.getAttributes().getNamedItem("value").getNodeValue());
			
			boolean auto = Boolean.valueOf(o.getAttributes().getNamedItem("auto").getNodeValue());
			
			if (auto)
			{
				int initialDelay = Integer.valueOf(o.getAttributes().getNamedItem("initial_delay").getNodeValue());
				int delay = Integer.valueOf(o.getAttributes().getNamedItem("delay").getNodeValue());
				
				int limit = Integer.valueOf(o.getAttributes().getNamedItem("limit").getNodeValue());
				if (limit < 0)
					limit = 0;
				
				_announcements.put(id, new Announcement(message, value, auto, initialDelay, delay, limit));
			}
			else
				_announcements.put(id, new Announcement(message, value));
			
			id++;
		}
	}
	
	public void reload()
	{
		// Clean first tasks from automatic announcements.
		for (Announcement announce : _announcements.values())
			announce.stopTask();
		
		load();
	}
	
	/**
	 * Send stored {@link Announcement}s from _announcements Map to a specific {@link Player}.
	 * @param player : The Player to send infos.
	 * @param autoOrNot : If true, sends only automatic announcements, otherwise send classic ones.
	 */
	public void showAnnouncements(Player player, boolean autoOrNot)
	{
		for (Announcement announce : _announcements.values())
		{
			if (autoOrNot)
				announce.reloadTask();
			else
			{
				if (announce.isAuto())
					continue;
				
				if (announce.isCustomValue() == 0)
					player.sendPacket(new CreatureSay(0, Say2.ANNOUNCEMENT, "", announce.getMessage()));
				else if (announce.isCustomValue() == 1)
					player.sendPacket(new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", announce.getMessage()));
				else if (announce.isCustomValue() == 2)
					player.sendPacket(new CreatureSay(0, Say2.SHOUT, "", announce.getMessage()));
				else if (announce.isCustomValue() == 3)
					player.sendPacket(new CreatureSay(0, Say2.TRADE, "", announce.getMessage()));
				else if (announce.isCustomValue() == 4)
					player.sendPacket(new CreatureSay(0, Say2.HERO_VOICE, "", announce.getMessage()));
				else if (announce.isCustomValue() == 5)
					player.sendPacket(new CreatureSay(0, Say2.TELL, "", announce.getMessage()));
				else if (announce.isCustomValue() == 6)
					player.sendPacket(new CreatureSay(0, Say2.PARTY, "", announce.getMessage()));
				else if (announce.isCustomValue() == 7)
					player.sendPacket(new CreatureSay(0, Say2.CLAN, "", announce.getMessage()));
				else if (announce.isCustomValue() == 8)
					player.sendPacket(new CreatureSay(0, Say2.ALLIANCE, "", announce.getMessage()));
				
			}
		}
	}
	
	/**
	 * Use {@link Broadcast}.announceToOnlinePlayers(String, Boolean) in order to send announcement, wrapped into a ioobe try/catch.
	 * @param command : The command to handle.
	 * @param lengthToTrim : The length to trim, in order to send only the message without the command.
	 * @param value
	 */
	public void handleAnnounce(String command, int lengthToTrim, int value)
	{
		try
		{
			Broadcast.announceToOnlinePlayers(command.substring(lengthToTrim), value);
		}
		catch (StringIndexOutOfBoundsException e)
		{
		}
	}
	
	/**
	 * Send a static HTM with dynamic announcements content took from _announcements Map to a {@link Player}.
	 * @param player : The Player to send the {@link NpcHtmlMessage} packet.
	 */
	public void listAnnouncements(Player player)
	{
		final StringBuilder sb = new StringBuilder("<br>");
		if (_announcements.isEmpty())
			sb.append("<tr><td>The XML file doesn't contain any content.</td></tr>");
		else
		{
			for (Map.Entry<Integer, Announcement> entry : _announcements.entrySet())
			{
				final int index = entry.getKey();
				final Announcement announce = entry.getValue();
				
				StringUtil.append(sb, "<tr><td width=240>#", index, " - ", announce.getMessage(), "</td><td></td></tr><tr><td>value: ", announce.isCustomValue(), " | Auto: ", announce.isAuto(), "</td><td><button value=\"Delete\" action=\"bypass -h admin_announce del ", index, "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr>");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(HtmCache.getInstance().getHtmForce("data/html/admin/announce_list.htm"));
		html.replace("%announces%", sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Add an {@link Announcement} but only if the message isn't empty or null. Regenerate the XML.
	 * @param message : The String to announce.
	 * @param value
	 * @param auto : Is it using a specific task or not.
	 * @param initialDelay : Initial delay of the task, used only if auto is setted to True.
	 * @param delay : Delay of the task, used only if auto is setted to True.
	 * @param limit : Maximum amount of loops the task will do before ending.
	 * @return true if the announcement has been successfully added, false otherwise.
	 */
	public boolean addAnnouncement(String message, int value, boolean auto, int initialDelay, int delay, int limit)
	{
		// Empty or null message.
		if (message == null || message.isEmpty())
			return false;
		
		// Register announcement.
		if (auto)
			_announcements.put(_announcements.size(), new Announcement(message, value, auto, initialDelay, delay, limit));
		else
			_announcements.put(_announcements.size(), new Announcement(message, value));
		
		// Regenerate the XML.
		regenerateXML();
		return true;
	}
	
	/**
	 * End the task linked to an {@link Announcement} and delete it.
	 * @param index : The Map index to remove.
	 */
	public void delAnnouncement(int index)
	{
		// Stop the current task, if any.
		_announcements.remove(index).stopTask();
		
		// Regenerate the XML.
		regenerateXML();
	}
	
	/**
	 * This method allows to refresh the XML with infos took from _announcements Map.
	 */
	private void regenerateXML()
	{
		final StringBuilder sb = new StringBuilder(HEADER);
		
		sb.append("<list> \n");
		
		for (Announcement announce : _announcements.values())
			StringUtil.append(sb, "<announcement message=\"", announce.getMessage(), "\" value=\"", announce.isCustomValue(), "\" auto=\"", announce.isAuto(), "\" initial_delay=\"", announce.getInitialDelay(), "\" delay=\"", announce.getDelay(), "\" limit=\"", announce.getLimit(), "\" /> \n");
		
		sb.append("</list>");
		
		try (FileWriter fw = new FileWriter(new File("./data/xml/announcements.xml")))
		{
			fw.write(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static AnnouncementData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AnnouncementData INSTANCE = new AnnouncementData();
	}
}