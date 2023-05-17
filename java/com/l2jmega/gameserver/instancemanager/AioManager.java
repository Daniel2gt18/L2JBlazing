package com.l2jmega.gameserver.instancemanager;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.handler.admincommandhandlers.AdminAiox;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmega.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmega.gameserver.network.serverpackets.ItemList;
import com.l2jmega.gameserver.network.serverpackets.StatusUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

public class AioManager {
  private static final Logger _log = Logger.getLogger(AioManager.class.getName());
  
  private final Map<Integer, Long> _aios;
  
  protected final Map<Integer, Long> _aiosTask;
  
  private ScheduledFuture<?> _scheduler;
  
  public static AioManager getInstance() {
    return SingletonHolder._instance;
  }
  
  protected AioManager() {
    this._aios = new ConcurrentHashMap<>();
    this._aiosTask = new ConcurrentHashMap<>();
    this._scheduler = ThreadPool.scheduleAtFixedRate(new AioTask(), 1000L, 1000L);
    load();
  }
  
  public void reload() {
    this._aios.clear();
    this._aiosTask.clear();
    if (this._scheduler != null)
      this._scheduler.cancel(true); 
    this._scheduler = ThreadPool.scheduleAtFixedRate(new AioTask(), 1000L, 1000L);
    load();
  }
  
  public void load() {
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("SELECT objectId, duration FROM character_aio ORDER BY objectId");
      ResultSet rs = statement.executeQuery();
      while (rs.next())
        this._aios.put(Integer.valueOf(rs.getInt("objectId")), Long.valueOf(rs.getLong("duration"))); 
      rs.close();
      statement.close();
    } catch (Exception e) {
      _log.warning("Exception: AioManager load: " + e.getMessage());
    } 
    _log.info("AioManager: Loaded " + this._aios.size() + " characters with aio privileges.");
  }
  
  public void addAio(int objectId, long duration) {
    this._aios.put(Integer.valueOf(objectId), Long.valueOf(duration));
    this._aiosTask.put(Integer.valueOf(objectId), Long.valueOf(duration));
    addAioPrivileges(objectId, true);
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("INSERT INTO character_aio (objectId, duration) VALUES (?, ?)");
      statement.setInt(1, objectId);
      statement.setLong(2, duration);
      statement.execute();
      statement.close();
    } catch (Exception e) {
      _log.warning("Exception: AioManager addAio: " + e.getMessage());
    } 
  }
  
  public void updateAio(int objectId, long duration) {
    duration += this._aios.get(Integer.valueOf(objectId)).longValue();
    this._aios.put(Integer.valueOf(objectId), Long.valueOf(duration));
    this._aiosTask.put(Integer.valueOf(objectId), Long.valueOf(duration));
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("UPDATE character_aio SET duration = ? WHERE objectId = ?");
      statement.setLong(1, duration);
      statement.setInt(2, objectId);
      statement.execute();
      statement.close();
    } catch (Exception e) {
      _log.warning("Exception: AioManager updateAio: " + e.getMessage());
    } 
  }
  
  public void removeAio(int objectId) {
    this._aios.remove(Integer.valueOf(objectId));
    this._aiosTask.remove(Integer.valueOf(objectId));
    removeAioPrivileges(objectId, false);
    try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
      PreparedStatement statement = con.prepareStatement("DELETE FROM character_aio WHERE objectId = ?");
      statement.setInt(1, objectId);
      statement.execute();
      statement.close();
    } catch (Exception e) {
      _log.warning("Exception: AioManager removeAio: " + e.getMessage());
    } 
  }
  
  public boolean hasAioPrivileges(int objectId) {
    return this._aios.containsKey(Integer.valueOf(objectId));
  }
  
  public long getAioDuration(int objectId) {
    return this._aios.get(Integer.valueOf(objectId)).longValue();
  }
  
  public void addAioTask(int objectId, long duration) {
    this._aiosTask.put(Integer.valueOf(objectId), Long.valueOf(duration));
  }
  
  public void removeAioTask(int objectId) {
    this._aiosTask.remove(Integer.valueOf(objectId));
  }
  
  public void addAioPrivileges(int objectId, boolean apply) {
    Player player = World.getInstance().getPlayer(objectId);
    player.broadcastUserInfo();
    giveAioItems(player);
    if (Config.ALLOW_AIO_ITEM) {
      player.addItem("", Config.AIO_ITEMID, 1, player, true);
      player.getInventory().equipItem(player.getInventory().getItemByItemId(Config.AIO_ITEMID));
    } 
    player.sendPacket(new InventoryUpdate());
    player.sendPacket(new ItemList(player, true));
    player.sendPacket(new StatusUpdate(player));
    player.rewardAioSkills();
    player.giveAvailableSkills();
    player.sendSkillList();
  }
  
  public void removeAioPrivileges(int objectId, boolean apply) {
    Player player = World.getInstance().getPlayer(objectId);
    player.broadcastUserInfo();
    player.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
    ItemInstance item = player.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, player, null);
    player.sendPacket(new ItemList(player, true));
    player.getInventory().reloadEquippedItems();
    InventoryUpdate iu = new InventoryUpdate();
    
    player.getAppearance().setNameColor(16777215);
    player.getAppearance().setTitleColor(16777215);
    if (Config.CHANGE_AIO_NAME)
      AdminAiox.nameaio("[AIO]", player); 

    iu.addItem(item);
    player.lostAioSkills();
    player.giveAvailableSkills();
    player.sendSkillList();
  }
  
  public class AioTask implements Runnable {
    @Override
	public final void run() {
      if (AioManager.this._aiosTask.isEmpty())
        return; 
      for (Map.Entry<Integer, Long> entry : AioManager.this._aiosTask.entrySet()) {
        long duration = entry.getValue().longValue();
        if (System.currentTimeMillis() > duration) {
          int objectId = entry.getKey().intValue();
          AioManager.this.removeAio(objectId);
          Player player = World.getInstance().getPlayer(objectId);
          player.sendPacket(new ExShowScreenMessage("Your aio privileges were removed.", 10000));
        } 
      } 
    }
  }
  
  public static void giveAioItems(Player activeChar) {
    for (int[] reward : Config.AIO_CHAR_ITENS) {
      ItemInstance PhewPew1 = activeChar.getInventory().addItem("Aiox Items", reward[0], reward[1], activeChar, null);
      activeChar.getInventory().equipItemAndRecord(PhewPew1);
    } 
  }
  
  private static class SingletonHolder {
    protected static final AioManager _instance = new AioManager();
  }
}
