package com.l2jmega.gameserver.instancemanager;

import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;

public class VIPINFO implements Runnable {
  private Player _activeChar;
  
  public VIPINFO(Player activeChar) {
    _activeChar = activeChar;
  }
  
  @Override
public void run() {
    if (_activeChar.isOnline()) {
      String htmFile = "data/html/mods/vip.htm";
      String htmContent = HtmCache.getInstance().getHtm(htmFile);
      if (htmContent != null) {
        NpcHtmlMessage doacaoHtml = new NpcHtmlMessage(1);
        doacaoHtml.setHtml(htmContent);
        if (!_activeChar.getHWID().equals("")) {
          doacaoHtml.replace("%ip%", _activeChar.getHWID());
        } else {
          doacaoHtml.replace("%ip%", "Indisponivel ..");
        }  
        _activeChar.sendPacket(doacaoHtml);
      } else {
        _activeChar.sendMessage("ERROR, INFORME A STAFF DO SERVIDOR.");
      } 
    } 
  }
}