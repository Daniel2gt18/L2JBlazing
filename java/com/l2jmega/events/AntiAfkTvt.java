package com.l2jmega.events;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;

/**
 * @author Phear3d
 */

public class AntiAfkTvt
{
	// Debug
	static boolean debug = false;
	
	// Delay between location checks , Default 15000 ms (1 minute)
	private final int CheckDelay = 20000;
	
	static Logger _log = Logger.getLogger(AntiAfkTvt.class.getName());
	static ArrayList<String> TvTPlayerList = new ArrayList<>();
	static String[] Splitter;
	static int xx, yy, zz, SameLoc;
	static Player _player;
	
	
	private AntiAfkTvt()
	{
		if (Config.DEBUG_TVT)
		{
			_log.info("WARN: Auto-Kick TVT System initiated.");
		}
		ThreadPool.scheduleAtFixedRate(new AntiAfk(), 20000, CheckDelay);
	}
	
	private class AntiAfk implements Runnable
	{
		@Override
		public void run()
		{
			if (TvT.is_started())
			{
				synchronized (TvT._players)
				{
					
					// Iterate over all participated player instances in this team
					if (TvT._players.size() <= 1)
					{
						return;
					}
					
					for (Player playerInstance : TvT._players)
					{
						if (playerInstance != null && playerInstance.isOnline() && !playerInstance.isDead() && !playerInstance.isPhantom() && !playerInstance.isGM() && !playerInstance.isImmobilized() && !playerInstance.isParalyzed() && playerInstance.isKickProtection())
						{
							_player = playerInstance;
							AddTvTSpawnInfo(playerInstance.getName(), playerInstance.getX(), playerInstance.getY(), playerInstance.getZ());
							if (debug)
								System.err.println("TvT Player: " + playerInstance.getName() + " " + playerInstance.getX() + " " + playerInstance.getY() + " " + playerInstance.getZ());
						}
					}
				}
			}
			else
			{
				TvTPlayerList.clear();
			}
		}
	}
	
	static void AddTvTSpawnInfo(String name, int _x, int _y, int _z)
	{
		if (!CheckTvTSpawnInfo(name))
		{
			String temp = name + ":" + Integer.toString(_x) + ":" + Integer.toString(_y) + ":" + Integer.toString(_z) + ":1";
			TvTPlayerList.add(temp);
		}
		else
		{
			Object[] elements = TvTPlayerList.toArray();
			for (int i = 0; i < elements.length; i++)
			{
				Splitter = ((String) elements[i]).split(":");
				String nameVal = Splitter[0];
				if (name.equals(nameVal))
				{
					GetTvTSpawnInfo(name);
					if (_x == xx && _y == yy && _z == zz && _player.isAttackingNow() == false && _player.isCastingNow() == false && _player.isOnline() == true && _player.isParalyzed() == false)
					{
						++SameLoc;
						if (SameLoc >= 4)// Kick after 4 same x/y/z, location checks
						{
							
							// kick here
							if (debug)
							{
								System.out.println("WANR: " + _player.getName() + " Foi kickado do evento por inatividade.");
							}
							
							for (Player allgms : World.getAllGMs())
								allgms.sendMessage("[SYS]: " + _player.getName() + " Foi kickado do evento por inatividade.");
							
							TvTPlayerList.remove(i);
							setUserData(_player);
							
							return;
						}
						TvTPlayerList.remove(i);
						String temp = name + ":" + Integer.toString(_x) + ":" + Integer.toString(_y) + ":" + Integer.toString(_z) + ":" + SameLoc;
						TvTPlayerList.add(temp);
						return;
					}
					TvTPlayerList.remove(i);
					String temp = name + ":" + Integer.toString(_x) + ":" + Integer.toString(_y) + ":" + Integer.toString(_z) + ":1";
					TvTPlayerList.add(temp);
				}
			}
			
		}
	}
	
	private static boolean CheckTvTSpawnInfo(String name)
	{
		
		Object[] elements = TvTPlayerList.toArray();
		for (int i = 0; i < elements.length; i++)
		{
			Splitter = ((String) elements[i]).split(":");
			String nameVal = Splitter[0];
			if (name.equals(nameVal))
			{
				return true;
			}
		}
		return false;
	}
	
	private static void GetTvTSpawnInfo(String name)
	{
		
		Object[] elements = TvTPlayerList.toArray();
		for (int i = 0; i < elements.length; i++)
		{
			Splitter = ((String) elements[i]).split(":");
			String nameVal = Splitter[0];
			if (name.equals(nameVal))
			{
				xx = Integer.parseInt(Splitter[1]);
				yy = Integer.parseInt(Splitter[2]);
				zz = Integer.parseInt(Splitter[3]);
				SameLoc = Integer.parseInt(Splitter[4]);
			}
		}
	}
	
	public static AntiAfkTvt getInstance()
	{
		return SingletonHolder._instance;
	}
	
	
	private static class SingletonHolder
	{
		protected static final AntiAfkTvt _instance = new AntiAfkTvt();
	}
	
	public static void main(String[] args)
	{
		AntiAfkTvt.getInstance();
	}
	
	public static void setUserData(Player player)
	{
		player.teleToLocation(player.getLastX(), player.getLastY(), player.getLastZ(), 50);
		TvT.removePlayer(player);
		player.logout();
	}
	
}