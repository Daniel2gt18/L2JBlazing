package com.l2jmega.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;

/**
 * @author MeGaPacK
 */
public final class ObserverZones extends Folk
{
	public ObserverZones(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("observe"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			final int x = Integer.parseInt(st.nextToken());
			final int y = Integer.parseInt(st.nextToken());
			final int z = Integer.parseInt(st.nextToken());
			
			if (player.destroyItemByItemId("zone", Config.ITEM_OBSERVER, Config.ITEM_BUY_QUANT_OBSERVER, player, true))
			{
			player.setZoneObserver(true);
			player.enterZoneObserverMode(x, y, z);
			}
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
		
		return "data/html/mods/observer/" + filename + ".htm";
	}
}