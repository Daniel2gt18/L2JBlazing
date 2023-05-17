package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

public class CloseGame
  implements Runnable
{
  private final Player _player;
  private int _time;
  
  public CloseGame(Player player, int time)
  {
    this._time = time;
    this._player = player;
  }
  
  @Override
public void run()
  {
    if (this._player.isOnline())
    {
      switch (this._time)
      {
      case 60: 
      case 120: 
      case 180: 
      case 240: 
      case 300: 
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time / 60 + " minute(s) ..");
        break;
      case 20: 
      case 30: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 3000));
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 15: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 3000));        
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 10: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 3000));        
       this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 5: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 1500));       
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 4: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 1500));
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 3: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 1500));
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 2: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 1500));
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        break;
      case 1: 
          this._player.sendPacket(new ExShowScreenMessage("" + this._time + " ..", 1500));
        this._player.sendChatMessage(0, 0, "SYS", "Desconecting in " + this._time + " second(s) ..");
        this._player.logout(true);
      }
      if (this._time > 1) {
        ThreadPool.schedule(new CloseGame(_player, _time - 1), 1000L);
      }
    }
  }
}
