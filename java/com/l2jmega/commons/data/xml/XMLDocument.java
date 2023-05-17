package com.l2jmega.commons.data.xml;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.xml.parsers.DocumentBuilderFactory;

import com.l2jmega.commons.logging.CLogger;

import com.l2jmega.gameserver.templates.StatsSet;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An XML document, relying on a static and single DocumentBuilderFactory.
 */
public abstract class XMLDocument
{
	protected static final CLogger LOGGER = new CLogger(XMLDocument.class.getName());
	
	private static final DocumentBuilderFactory BUILDER;
	static
	{
		BUILDER = DocumentBuilderFactory.newInstance();
		BUILDER.setValidating(false);
		BUILDER.setIgnoringComments(true);
	}
	
	abstract protected void load();
	
	abstract protected void parseDocument(Document doc, File f);
	
	public void loadDocument(String filePath)
	{
		loadDocument(new File(filePath));
	}
	
	/**
	 * Parse an entire directory or file if found.
	 * @param file
	 */
	public void loadDocument(File file)
	{
		if (!file.exists())
		{
			LOGGER.error("The following file or directory doesn't exist: {}.", file.getName());
			return;
		}
		
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
				loadDocument(f);
		}
		else if (file.isFile())
		{
			try
			{
				parseDocument(BUILDER.newDocumentBuilder().parse(file), file);
			}
			catch (Exception e)
			{
				LOGGER.error("Error loading XML file '{}'.", e, file.getName());
			}
		}
	}
	
	/**
	 * This method parses the content of a NamedNodeMap and feed the given StatsSet.
	 * @param attrs : The NamedNodeMap to parse.
	 * @param set : The StatsSet to feed.
	 */
	public static void parseAndFeed(NamedNodeMap attrs, StatsSet set)
	{
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node attr = attrs.item(i);
			set.set(attr.getNodeName(), attr.getNodeValue());
		}
	}
	
	
	public void forEach(Node node, Consumer<Node> action)
	{
		forEach(node, a -> true, action);
	}
	
	public void forEach(Node node, String nodeName, Consumer<Node> action)
	{
		forEach(node, innerNode ->
		{
			if (nodeName.contains("|"))
			{
				final String[] nodeNames = nodeName.split("\\|");
				for (String name : nodeNames)
				{
					if (!name.isEmpty() && name.equals(innerNode.getNodeName()))
					{
						return true;
					}
				}
				return false;
			}
			return nodeName.equals(innerNode.getNodeName());
		}, action);
	}
	
	public void forEach(Node node, Predicate<Node> filter, Consumer<Node> action)
	{
		final NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node targetNode = list.item(i);
			if (filter.test(targetNode))
			{
				action.accept(targetNode);
			}
		}
	}
	
	public  StatsSet parseAttributes(Node node)
	{
		final NamedNodeMap attrs = node.getAttributes();
		final StatsSet map = new StatsSet();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node att = attrs.item(i);
			map.put(att.getNodeName(), att.getNodeValue());
		}
		return map;
	}
}