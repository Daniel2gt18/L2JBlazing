package com.l2jmega.gameserver.handler.voicedcommandhandlers;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IVoicedCommandHandler;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.Dice;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.util.Broadcast;
import com.l2jmega.util.Rnd;

/**
 * @author MeGaPacK
 *
 */
public class VoicedLuckyDice implements IVoicedCommandHandler
{
    
	private static String[] VOICED_COMMANDS =
	{
		"luckydice",
		"play1",
		"TvTEvent",
		"SilverCoin",
		"TicketDonate"
	};
	
	//Item Id
    public static int _itemId;
	private String itemname= "SYS";
    //Ammount Item
    public static int jackpot;
    public static int jackpot2;
    public static int jackpot3;
    
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
      int ammount = 0;
      
		if (command.equals("luckydice"))
		{
	        showChatWindow(player);
		}
		else if (command.startsWith("play1"))
      {
        StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        try
        {
          String type = st.nextToken();

          if(type.startsWith("TvTEvent"))
          {
        	  _itemId = 9516;
          }
          else if(type.startsWith("SilverCoin"))
          {
        	  _itemId = 9510;
          }
          else if(type.startsWith("TicketDonate"))
          {
        	  _itemId = 10000;
          }
          ammount = Integer.parseInt(st.nextToken());
        }
        catch (NoSuchElementException nse)
        {
        }
        if (ammount >0)
        {
            Dice(player, ammount);
        }
        else return false;
        showChatWindow(player);
      }
	return false;
    }
    
    public void showChatWindow(Player player)
    {
		NpcHtmlMessage msg = new NpcHtmlMessage(1);
		msg.setFile("data/html/mods/lucky_dice.htm");
		msg.replace("%luckydice%", casinoWindow(player));
        player.sendPacket(msg);
    }

    private static String casinoWindow(Player player)
    {
    	StringBuilder tb = new StringBuilder();
        tb.append("<html><title>Lucky Dice</title><body><center>");
        tb.append("<table width=300>");
        tb.append("<tr>");
        tb.append("<td align=\"center\"><font color=\"FFFFFF\">EN:Hello "+player.getName()+", Shall we place bets?<font></td></tr>");        
        tb.append("<tr><td align=\"center\"><font color=\"FFFFFF\">EN:You got need to Roll Number: 6-6 or 6-5 or 5-5 or 3-3</font></td></tr>");
        tb.append("<tr><td align=\"center\"><font color=\"FFFFFF\">EN:But if you are Lucky and you will win the Jackpot</font></td></tr>");
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">EN:Jackpot: TvT Event: "+jackpot+"</font></td></tr>");	
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">EN:Jackpot: Silver Coin: "+jackpot2+"</font></td></tr><br>"); 
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">EN:Jackpot: Ticket Donate: "+jackpot3+"</font></td></tr>"); 
        tb.append("<tr></tr>");
        tb.append("</table>");
        tb.append("<table width=300>");
        tb.append("<tr>");
        tb.append("<td align=\"center\"><font color=\"FFFFFF\">PT:Ola "+player.getName()+", Vamos fazer umas apostas?<font></td></tr>");        
        tb.append("<tr><td align=\"center\"><font color=\"FFFFFF\">PT:voce precisa acertar os numeros: 6-6, 6-5, 5-5 ou 3-3</font></td></tr>");
        tb.append("<tr><td align=\"center\"><font color=\"FFFFFF\">PT:Se voce tiver sorte, Vai ganhar o Item Acumulado</font></td></tr>");
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">PT:Item Acumulado: TvT Event: "+jackpot+"</font></td></tr>");	
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">PT:Item Acumulado: Silver Coin: "+jackpot2+"</font></td></tr>"); 
        tb.append("<tr><td align=\"center\"><font color=\"0F92FF\">PT:Item Acumulado: Ticket Donate: "+jackpot3+"</font></td></tr>"); 
        tb.append("<tr></tr>");
        tb.append("</table>");
        tb.append("<center><img src=\"L2UI.SquareGray\" width=\"300\" height=\"1\"><br>");
        tb.append("<table width=290>");
        tb.append("<tr>");
        tb.append("<td align=\"center\"><font color=\"0F92FF\">Select a Currency To Bet:</font></td>");
        tb.append("<td><combobox width=120 height=21 var=\"cb\"list=TvTEvent;SilverCoin;TicketDonate></td>");
        tb.append("</tr>");
        tb.append("<tr></tr>");
        tb.append("<tr></tr>");
        tb.append("<tr>");
        tb.append("<td align=center><font color=\"0F92FF\">Type an Amount:</font></td>");
        tb.append("<td><edit var=\"qbox\" width=120 height=15></td>");
        tb.append("</table><br>");
        tb.append("<center><img src=\"L2UI.SquareGray\" width=\"280\" height=\"1\"></center><br><br>");
        tb.append("<center><button value=\"Try Your Luck!\" action=\"bypass voiced_play1 $cb $qbox\" width=134 height=21 back=\"L2UI_ch3.BigButton3_over\" fore=\"L2UI_ch3.BigButton3\"></center>");
        tb.append("</body></html>");
        return tb.toString();
    }
    
    public void Dice(Player activeChar,int _ammount)
    {
		if(activeChar.getInventory().getInventoryItemCount(_itemId, 0) >= _ammount)
		{
				int number = rollDice();
				int number2 = rollDice2();
				if(number == 6 && number2 == 6 || number == 6 && number2 == 5 || number == 5 && number2 == 5 || number == 3 && number2 == 3)
				{
			        ExShowScreenMessage screen = new ExShowScreenMessage("Congratulations "+activeChar.getName()+" won You got "+number+"-"+number2+" Good!", 6000);
			        String name = activeChar.getName();
			        activeChar.sendMessage("Congratulations "+name+" won You got "+number+"-"+number2+" Good!");
			        activeChar.sendPacket(screen);
					activeChar.addItem("Consume", _itemId, _ammount, activeChar,true);
					/*if(_itemId == 9516)
					{
				   	  jackpot += _ammount;
					}
					else if(_itemId == 9510)
					{
					  jackpot2 += _ammount;
					}*/
					showChatWindow(activeChar);
					switch(_itemId)
					{
					    case 9516:
						itemname = "TvT Event";
						break;
					    case 9510:
						itemname = "Silver Coin";
						break;
					    case 10000:
						itemname = "Ticket Donate";
						break;
					}
					
					switch(Rnd.get(1,20))
					{
					    case 1:
						break;
					    case 2:
						break;
					    case 3:
						break;
					    case 4:
						break;
					    case 5:
						    if(_itemId == 9516)
						    {
						     activeChar.addItem("Consume", _itemId, jackpot, activeChar,true);
						     activeChar.broadcastUserInfo();
								MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
								activeChar.sendPacket(MSU);
						     Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot +" Jackpot of "+ itemname +"!");
						     jackpot = 0;
						    }
						    else if(_itemId == 9510)
						    {
						    activeChar.addItem("Consume", _itemId, jackpot2, activeChar,true);
						    activeChar.broadcastUserInfo();
							MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
							activeChar.sendPacket(MSU);
						    Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot2 +" Jackpot of "+ itemname +"!");
						    jackpot2 = 0;
						    }
						    else if(_itemId == 10000)
						    {
						    activeChar.addItem("Consume", _itemId, jackpot3, activeChar,true);
						    activeChar.broadcastUserInfo();
							MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
							activeChar.sendPacket(MSU);
						    Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot3 +" Jackpot of "+ itemname +"!");
						    jackpot3 = 0;
						    }
						break;
					    case 6:
						break;
					    case 7:
						break;
					    case 8:
						break;
					    case 9:
						break;
					    case 10:
						break;
					    case 11:
					    if(_itemId == 9516)
					    {
					     activeChar.addItem("Consume", _itemId, jackpot, activeChar,true);
					     activeChar.broadcastUserInfo();
							MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
							activeChar.sendPacket(MSU);
					     Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot +" Jackpot of "+ itemname +"!");
					     jackpot = 0;
					    }
					    else if(_itemId == 9510)
					    {
					    activeChar.addItem("Consume", _itemId, jackpot2, activeChar,true);
					    activeChar.broadcastUserInfo();
						MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
						activeChar.sendPacket(MSU);
					    Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot2 +" Jackpot of "+ itemname +"!");
					    jackpot2 = 0;
					    }
					    else if(_itemId == 10000)
					    {
					    activeChar.addItem("Consume", _itemId, jackpot3, activeChar,true);
					    activeChar.broadcastUserInfo();
						MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2025, 1, 1, 0);
						activeChar.sendPacket(MSU);
					    Announcement.Announce("Congratulations "+ activeChar.getName() +" he won "+ jackpot3 +" Jackpot of "+ itemname +"!");
					    jackpot3 = 0;
					    }
						break;
					    case 12:
						break;
					    case 13:
						break;
					    case 14:
						break;
					    case 16:
						break;
					    case 17:
						break;
					    case 18:
						break;
					    case 19:
						break;
					    case 20:
						break;
					}
					activeChar.broadcastUserInfo();
				}
				else
				{
			        ExShowScreenMessage screen = new ExShowScreenMessage(""+activeChar.getName()+" ops lost again play can be lucky!", 6000);
			        String name = activeChar.getName();
			        activeChar.sendMessage(name+" ops lost again Play can be Lucky!");
			        activeChar.sendPacket(screen);
					activeChar.destroyItemByItemId("Consume", _itemId, _ammount, activeChar, true);
					if(_itemId == 9516)
					{
				   	  jackpot += _ammount;
					}
					else if(_itemId == 9510)
					{
					   jackpot2 += _ammount;
					}
					else if(_itemId == 10000)
					{
					   jackpot3 += _ammount;
					}
					activeChar.broadcastUserInfo();
					showChatWindow(activeChar);
				}
				Broadcast.toSelfAndKnownPlayers(activeChar, new Dice(activeChar.getObjectId(), 4625, number, activeChar.getX() - 30, activeChar.getY() - 30, activeChar.getZ()));
				Broadcast.toSelfAndKnownPlayers(activeChar, new Dice(activeChar.getObjectId(), 4626, number2, activeChar.getX() - 22, activeChar.getY() - 22, activeChar.getZ()));
			}
			else
			{
				switch(_itemId)
				{
				    case 9516:
					itemname = "TvT Event";
					break;
				    case 9510:
					itemname = "Silver Coin";
					break;
				    case 10000:
					itemname = "Ticket Donate";
					break;
				}
				activeChar.sendMessage("You do not have enough "+itemname+".");
			}
	}
    
	public static int rollDice2()
	{
		return Rnd.get(1, 7);
	}
	
	public static int rollDice()
	{
		return Rnd.get(1, 7);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}

}
