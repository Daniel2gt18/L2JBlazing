package com.l2jmega.events.pvpevent;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.model.Announcement;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PvPEvent
{
    private static final Logger _log;
    private PvPEventEngineState _state;
    
    public PvPEvent() {
        this._state = PvPEventEngineState.INACTIVE;
    }
    
    public boolean startPartyEvent() {
        this.setState(PvPEventEngineState.ACTIVE);
        return true;
    }
    
    public boolean endPartyEvent() {
        this.setState(PvPEventEngineState.INACTIVE);
        return true;
    }
    
    private void setState(final PvPEventEngineState state) {
        synchronized (state) {
            this._state = state;
        }
    }
    
    public boolean isActive() {
        synchronized (this._state) {
            return this._state == PvPEventEngineState.ACTIVE;
        }
    }
    
    public boolean isInactive() {
        synchronized (this._state) {
            return this._state == PvPEventEngineState.INACTIVE;
        }
    }
    
    public void rewardFinish() {
        if (getTopZonePvpCount() == 0) {
            Announcement.AnnounceEvents(""+Config.NAME_PVP+" is finished without winners!");
        }
        else {
            Announcement.AnnounceEvents(""+Config.NAME_PVP+" Winner is " + getTopZonePvpName() + " with " + getTopZonePvpCount() + " pvp's.");
            addReward(getTopZonePlayerReward());
        }
        cleanPvpEvent();
    }
   
    public static void cleanPvpEvent() {
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("UPDATE characters SET event_pvp=0");
            statement.executeUpdate();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static int getTopZonePlayerReward() {
        int id = 0;
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY event_pvp DESC LIMIT 1");
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                id = rset.getInt("obj_Id");
            }
            rset.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    static int getTopZonePvpCount() {
        int id = 0;
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("SELECT event_pvp FROM characters ORDER BY event_pvp DESC LIMIT 1");
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                id = rset.getInt("event_pvp");
            }
            rset.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    static String getTopZonePvpName() {
        String name = null;
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters ORDER BY event_pvp DESC LIMIT 1");
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                name = rset.getString("char_name");
            }
            rset.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
    
    public static void addEventPvp(final Player activeChar) {
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("UPDATE characters SET event_pvp=? WHERE obj_Id=?");
            statement.setInt(1, getEventPvp(activeChar) + 1);
            statement.setInt(2, activeChar.getObjectId());
            statement.executeUpdate();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getEventPvp(final Player activeChar) {
        int id = 0;
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            final PreparedStatement statement = con.prepareStatement("SELECT event_pvp FROM characters WHERE obj_Id=?");
            statement.setInt(1, activeChar.getObjectId());
            final ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                id = rset.getInt("event_pvp");
            }
            rset.close();
            statement.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    private static void addReward(final int objId) {
        final Player player = World.getInstance().getPlayer(objId);
        for (final int[] reward : Config.PVP_EVENT_REWARDS) {
            if (player != null && player.isOnline()) {
                final InventoryUpdate iu = new InventoryUpdate();
                player.addItem("Top Reward", reward[0], reward[1], player, true);
                player.sendPacket(new ExShowScreenMessage("Congratulations " + player.getName() + " you are the winner of PvP Event.", 3000, 2, true));
                player.getInventory().updateDatabase();
                player.sendPacket(iu);
            }
            else {
                addOfflineItem(objId, reward[0], reward[1]);
            }
        }
    }
    
    private static void addOfflineItem(final int ownerId, final int itemId, final int count) {
        final Item item = ItemTable.getInstance().getTemplate(itemId);
        final int objectId = IdFactory.getInstance().getNextId();
        try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            if (count > 1 && !item.isStackable()) {
                return;
            }
            final PreparedStatement statement = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, ownerId);
            statement.setInt(2, item.getItemId());
            statement.setInt(3, count);
            statement.setString(4, "INVENTORY");
            statement.setInt(5, 0);
            statement.setInt(6, 0);
            statement.setInt(7, objectId);
            statement.setInt(8, 0);
            statement.setInt(9, 0);
            statement.setInt(10, -1);
            statement.setLong(11, 0L);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e) {
            PvPEvent._log.severe("Could not update item char: " + e);
            e.printStackTrace();
        }
    }
    
    public static void getTopHtml(final Player activeChar) {
        if (getInstance().isActive()) {
        	
			NpcHtmlMessage htm = new NpcHtmlMessage(5);
			StringBuilder tb = new StringBuilder();
			tb.append("<html>");
			tb.append("<body>");
			tb.append("<center>");
			tb.append("<img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>");
			tb.append("<table border=\"1\" width=\"300\">");
			tb.append("<tr>");
			tb.append("<td><center>Rank</center></td>");
			tb.append("<td><center>Character</center></td>");
			tb.append("<td><center>Pvp's</center></td>");
			tb.append("<td><center>Status</center></td>");
			tb.append("</tr>");
			
            try (final Connection con = L2DatabaseFactory.getInstance().getConnection()) {
                final PreparedStatement statement = con.prepareStatement("SELECT char_name,event_pvp,accesslevel,online FROM characters ORDER BY event_pvp DESC LIMIT 15");
                final ResultSet result = statement.executeQuery();
                int pos = 0;
                while (result.next()) {
                    final int accessLevel = result.getInt("accesslevel");
                    if (accessLevel > 0) {
                        continue;
                    }
                    final int pvpKills = result.getInt("event_pvp");
                    if (pvpKills == 0) {
                        continue;
                    }
                    final String pl = result.getString("char_name");

                    String status;
					pos++;
					String statu = result.getString("online");
					if (statu.equals("1")) {
						status = "<font color=00FF00>Online</font>";
					} else {
						status = "<font color=FF0000>Offline</font>";
					} 
					tb.append("<tr>");
					tb.append("<td><center><font color =\"AAAAAA\">" + pos + "</font></center></td>");
					tb.append("<td><center><font color=00FFFF>" + pl + "</font></center></td>");
					tb.append("<td><center>" + pvpKills + "</center></td>");
					tb.append("<td><center>" + status + "</center></td>");
					tb.append("</tr>");
                }
                statement.close();
                result.close();
                con.close();
            }
            catch (Exception e) {
                PvPEvent._log.warning("Error while selecting top 15 pvp from database: " + e);
            }
            
			tb.append("</table>");
			tb.append("<br>");
			tb.append("<br>");
			tb.append("<a action=\"bypass voiced_ranking\">Back to Rankings</a>");
			tb.append("</center>");
			tb.append("</body>");
			tb.append("</html>");
			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
            return;
        }
        activeChar.sendMessage("PvP Event is not in progress!");
    }
    
    public static PvPEvent getInstance() {
        return SingletonHolder._instance;
    }
    
    static {
        _log = Logger.getLogger(PvPEvent.class.getName());
    }
    
    private static class SingletonHolder
    {
        protected static final PvPEvent _instance;
        
        static {
            _instance = new PvPEvent();
        }
    }
}
