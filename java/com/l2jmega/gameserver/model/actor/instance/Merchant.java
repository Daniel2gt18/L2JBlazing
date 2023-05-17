package com.l2jmega.gameserver.model.actor.instance;

import java.util.List;
import java.util.StringTokenizer;

import com.l2jmega.Config;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.data.manager.BuyListManager;
import com.l2jmega.gameserver.data.xml.MultisellData;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.buylist.NpcBuyList;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.BuyList;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.SellList;
import com.l2jmega.gameserver.network.serverpackets.ShopPreviewList;

/**
 * Merchant type, it got buy/sell methods && bypasses.<br>
 * It is used as extends for classes such as Fisherman, CastleChamberlain, etc.
 */
public class Merchant extends Folk
{
	public Merchant(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/merchant/" + filename + ".htm";
	}
	
	private final void showWearWindow(Player player, int val)
	{
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(val);
		if (buyList == null || !buyList.isNpcAllowed(getNpcId()))
			return;
		
		player.tempInventoryDisable();
		player.sendPacket(new ShopPreviewList(buyList, player.getAdena(), player.getExpertiseIndex()));
	}
	
	protected final void showBuyWindow(Player player, int val)
	{
		final NpcBuyList buyList = BuyListManager.getInstance().getBuyList(val);
		if (buyList == null || !buyList.isNpcAllowed(getNpcId()))
			return;
		
		player.tempInventoryDisable();
		player.sendPacket(new BuyList(buyList, player.getAdena(), (getCastle() != null) ? getCastle().getTaxRate() : 0));
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("Buy"))
		{
			if (st.countTokens() < 1)
				return;
			
			showBuyWindow(player, Integer.parseInt(st.nextToken()));
		}
		else if (actualCommand.equalsIgnoreCase("Sell"))
		{
			// Retrieve sellable items.
			final List<ItemInstance> items = player.getInventory().getSellableItems();
			if (items.isEmpty())
			{
				final String content = HtmCache.getInstance().getHtm("data/html/" + ((this instanceof Fisherman) ? "fisherman" : "merchant") + "/" + getNpcId() + "-empty.htm");
				if (content != null)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setHtml(content);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					return;
				}
			}
			
			player.sendPacket(new SellList(player.getAdena(), items));
		}
		else if (actualCommand.equalsIgnoreCase("Wear") && Config.ALLOW_WEAR)
		{
			if (st.countTokens() < 1)
				return;
			
			showWearWindow(player, Integer.parseInt(st.nextToken()));
		}
		else if (actualCommand.equalsIgnoreCase("Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/"+player.getHtmlSave()+"");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);	
		
			MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, false);
		}
		else if (actualCommand.equalsIgnoreCase("Multisell_Shadow"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (player.getLevel() < 40)
				html.setFile("data/html/common/shadow_item-lowlevel.htm");
			else if (player.getLevel() < 46)
				html.setFile("data/html/common/shadow_item_mi_c.htm");
			else if (player.getLevel() < 52)
				html.setFile("data/html/common/shadow_item_hi_c.htm");
			else
				html.setFile("data/html/common/shadow_item_b.htm");
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("Exc_Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, true);
		}
		else if (actualCommand.equalsIgnoreCase("Newbie_Exc_Multisell"))
		{
			if (st.countTokens() < 1)
				return;
			
			if (player.isNewbie())
				MultisellData.getInstance().separateAndSend(st.nextToken(), player, this, true);
			else
				showChatWindow(player, "data/html/exchangelvlimit.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}