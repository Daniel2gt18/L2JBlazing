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
package com.l2jmega.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author rapfersan92
 */
public class PkColorTable
{
	private static final Logger _log = Logger.getLogger(PkColorTable.class.getName());
	
	private static List<PkColor> _pkColors;
	
	public static PkColorTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PkColorTable _instance = new PkColorTable();
	}
	
	protected PkColorTable()
	{
		_pkColors = new ArrayList<>();
		load();
	}
	
	public void reload()
	{
		_pkColors.clear();
		load();
	}
	
	private void load()
	{
		try
		{
			File f = new File("./data/xml/pk_color.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equalsIgnoreCase("template"))
				{
					StatsSet pvpColorData = new StatsSet();
					NamedNodeMap attrs = d.getAttributes();
					pvpColorData.set("pkAmount", Integer.valueOf(attrs.getNamedItem("pkAmount").getNodeValue()));
					pvpColorData.set("titleColor", Integer.decode(attrs.getNamedItem("titleColor").getNodeValue()));
					_pkColors.add(new PkColor(pvpColorData));
				}
			}
		}
		catch (Exception e)
		{
			_log.warning("Exception: pkColorTable load: " + e);
		}
		
		_log.info("pkColorTable: Loaded " + _pkColors.size() + " template(s).");
	}
	
	public List<PkColor> getPkColorTable()
	{
		return _pkColors;
	}
	
	public class PkColor
	{
		private int _pkAmount;
		private int _titleColor;
		
		public PkColor(StatsSet set)
		{
			_pkAmount = set.getInteger("pkAmount");
			_titleColor = set.getInteger("titleColor");
		}
		
		public int getPkAmount()
		{
			return _pkAmount;
		}
		
		public int getTitleColor()
		{
			return _titleColor;
		}
	}
}
