package com.l2jmega.gameserver.handler;

import java.util.logging.Logger;

import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;

/**
 * Mother class of all itemHandlers.
 */
public interface IItemHandler
{
	public static Logger _log = Logger.getLogger(IItemHandler.class.getName());
	
	/**
	 * Launch task associated to the item.
	 * @param playable L2Playable designating the player
	 * @param item ItemInstance designating the item to use
	 * @param forceUse ctrl hold on item use
	 */
	public void useItem(Playable playable, ItemInstance item, boolean forceUse);
}