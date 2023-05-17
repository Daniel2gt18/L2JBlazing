package com.l2jmega.gameserver.network;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

public class AioProtection implements Runnable {
  private Player _activeChar;
  
  public AioProtection(Player activeChar) {
    this._activeChar = activeChar;
  }
  
  @Override
public void run() {
    if (this._activeChar.isOnline() && !this._activeChar.isInsideZone(ZoneId.PEACE)) {
      this._activeChar.sendPacket(new ExShowScreenMessage("Aio Buffer not allowed outside the peace zone ..", 6000, 2, true));
      this._activeChar.teleToLocation(82849, 147948, -3470, 0);
    } 
  }
}
