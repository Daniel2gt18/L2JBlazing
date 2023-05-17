package com.l2jmega.gameserver.handler.admincommandhandlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.location.Location;

public class AdminLocPhantom implements IAdminCommandHandler
{
   
   private static final String[] ADMIN_COMMANDS =
   {
       "admin_save",
       "admin_reset",
       "admin_remove",
       "admin_end",
   };
   
   private static final List<Location> savedLocs = new ArrayList<>();
   private static final String fileName = "town_locs" + "%s" + ".ini";

   
   @Override
   public boolean useAdminCommand(String command, Player activeChar)
   {
       final StringTokenizer st = new StringTokenizer(command);
       st.nextToken();

        if (command.startsWith("admin_save"))
       {
               final Location loc = new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ());
               if (savedLocs.add(loc))
                   activeChar.sendMessage(loc + " saved..");
 
       }
       else if (command.startsWith("admin_reset"))
       {
           clear();
           activeChar.sendMessage("Reset completed.");
           
       }
       else if (command.startsWith("admin_remove"))
       {
           if (savedLocs.size() > 0)
               activeChar.sendMessage(savedLocs.remove(savedLocs.size() - 1) + " removed.");
           
       }
       else if (command.startsWith("admin_end"))
       {
           if (savedLocs.isEmpty())
           {
               activeChar.sendMessage("Empty locs..");
               return false;
           }
           store(activeChar);
       }
       return true;
   }
   
   private static void store(Player gm)
   {
       final String fName = String.format(fileName, "");
       String filePath = "gameserver";
       try (BufferedWriter writer = new BufferedWriter(new FileWriter(fName)))
       {
           for (Location loc : savedLocs)
               writer.write(String.format("%s,%s,%s\r\n", loc.getX(), loc.getY(), loc.getZ()));
       }
       catch (IOException e)
       {
           gm.sendMessage(String.format("Couldn't store coordinates in %s file.", fName));
           e.printStackTrace();
       }
       gm.sendMessage("Coordinates has been successfully stored at " + filePath);
       clear();
   }

   private static void clear()
   {
       savedLocs.clear();
   }
   
   @Override
   public String[] getAdminCommandList()
   {
       return ADMIN_COMMANDS;
   }
}