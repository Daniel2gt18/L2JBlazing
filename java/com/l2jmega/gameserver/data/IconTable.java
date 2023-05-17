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
package com.l2jmega.gameserver.data;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Tayran.JavaDev
 */
public class IconTable
{
	private static final Logger _log = Logger.getLogger(IconTable.class.getName());

	public static Map<Integer, String> _icons = new ConcurrentHashMap<>();

	public static final IconTable getInstance()
	{
		return SingletonHolder._instance;
	}

	protected IconTable()
	{

		load();
	}

	private static void load()
	{
		try
		{
			File f = new File("./data/xml/icons.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);

			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("icon"))
				{
					NamedNodeMap attrs = d.getAttributes();

					int itemId = Integer.valueOf(attrs.getNamedItem("itemId").getNodeValue());
					String iconName = attrs.getNamedItem("iconName").getNodeValue();

					if (itemId == 0 && itemId < 0)
					{
						_log.log(Level.WARNING, "Icon Table: itemId=\"" + itemId + "\" is not item ID, Ignoring it!");
						continue;
					}                  
					_icons.put(itemId, iconName);
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Icon Table: Error loading from database: " + e.getMessage(), e);
		}

		_log.info("Icon Table: Loaded " + _icons.size() + " icons.");
	}

	public static String getIcon(int id)
	{
		if (_icons.get(id) == null)
			return "icon.NOIMAGE";

		return _icons.get(id);
	}

	private static class SingletonHolder
	{
		protected static final IconTable _instance = new IconTable();
	}
}