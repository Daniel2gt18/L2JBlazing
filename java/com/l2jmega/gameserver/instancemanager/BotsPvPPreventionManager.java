package com.l2jmega.gameserver.instancemanager;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

import com.l2jmega.commons.concurrent.ThreadPool;

import com.l2jmega.Config;

import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.serverpackets.PlaySound;
import com.l2jmega.gameserver.network.serverpackets.PledgeCrest;
import com.l2jmega.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmega.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @Adaptacao Revengeance
 */

public class BotsPvPPreventionManager
{
	private class PlayerData
	{
		public PlayerData()
		{
			firstWindow = true;
		}
		
		public int mainpattern;
		public List<Integer> options = new ArrayList<>();
		public boolean firstWindow;
		public int patternid;
	}
	
	protected Random _randomize;
	protected static Map<Integer, Integer> _pvpcounter;
	protected static Map<Integer, Future<?>> _beginvalidation;
	protected static Map<Integer, PlayerData> _validation;
	protected static Map<Integer, byte[]> _images;
	protected int WINDOW_DELAY = 3; // delay used to generate new window if previous have been closed.
	protected int VALIDATION_TIME = Config.VALIDATION_TIME * 1000;
	
	public static final BotsPvPPreventionManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	BotsPvPPreventionManager()
	{
		_randomize = new Random();
		_pvpcounter = new HashMap<>();
		_beginvalidation = new HashMap<>();
		_validation = new HashMap<>();
		_images = new HashMap<>();
		_beginvalidation = new HashMap<>();
		
		getimages();
	}
	
	public void updatecounter(Creature player, Creature target)
	{
		if ((player instanceof Player) && (target instanceof Player))
		{
			Player killer = (Player) player;
			
			if (_validation.get(killer.getObjectId()) == null)
			{
				int count = 1;
				if (_pvpcounter.get(killer.getObjectId()) != null)
				{
					count = _pvpcounter.get(killer.getObjectId()) + 1;
				}
				
				if ((Config.PVP_KILLS_COUNTER + Rnd.get(0, 20)) < count)
				{
					validationtasks(killer);
					_pvpcounter.remove(killer.getObjectId());
				}
				else
				{
					_pvpcounter.put(killer.getObjectId(), count);
				}
			}
		}
	}
	
	private static void getimages()
	{
		String CRESTS_DIR = "data/html/mods/prevention";
		
		final File directory = new File(CRESTS_DIR);
		directory.mkdirs();
		
		int i = 0;
		for (File file : directory.listFiles())
		{
			if (!file.getName().endsWith(".dds"))
				continue;
			
			byte[] data;
			
			try (RandomAccessFile f = new RandomAccessFile(file, "r"))
			{
				data = new byte[(int) f.length()];
				f.readFully(data);
			}
			catch (Exception e)
			{
				continue;
			}
			_images.put(i, data);
			i++;
		}
	}
	
	static void validationwindow(Player player)
	{
		PlayerData container = _validation.get(player.getObjectId());
		StringBuilder tb = new StringBuilder();
		StringUtil.append(tb, "<html>");
		StringUtil.append(tb, "<body><center>");
		StringUtil.append(tb, "<br><br><font color=\"a2a0a2\">in order to prove you are a human being<br1>you've to</font> <font color=\"b09979\">match colours within generated pattern:</font>");
		
		// generated main pattern.
		StringUtil.append(tb, "<br><br><img src=\"Crest.crest_" + Config.SERVER_ID + "_" + (_validation.get(player.getObjectId()).patternid) + "\" width=\"32\" height=\"32\"></td></tr>");
		StringUtil.append(tb, "<br><br><font color=b09979>click-on pattern of your choice beneath:</font>");
		
		// generate random colours.
		StringUtil.append(tb, "<table><tr>");
		for (int i = 0; i < container.options.size(); i++)
		{
			StringUtil.append(tb, "<td><button action=\"link antipvp_" + i + "\" width=32 height=32 back=\"Crest.crest_" + Config.SERVER_ID + "_" + (container.options.get(i) + 1500) + "\" fore=\"Crest.crest_" + Config.SERVER_ID + "_" + (container.options.get(i) + 1500) + "\"></td>");
		}
		StringUtil.append(tb, "</tr></table>");
		StringUtil.append(tb, "</center></body>");
		StringUtil.append(tb, "</html>");
		
		String msg = HtmCache.getInstance().getHtm("data/html/mods/index_prevention.htm");
		msg = msg.replaceAll("%list%", tb.toString());
		player.sendPacket(new TutorialShowHtml(msg));
	}
	
	public void validationtasks(Player player)
	{
		PlayerData container = new PlayerData();
		randomizeimages(container, player);
		
		for (int i = 0; i < container.options.size(); i++)
		{
			PledgeCrest packet = new PledgeCrest((container.options.get(i) + 1500), _images.get(container.options.get(i)));
			player.sendPacket(packet);
			
		}
		
		PledgeCrest packet = new PledgeCrest(container.patternid, _images.get(container.options.get(container.mainpattern)));
		player.sendPacket(packet);
		
		_validation.put(player.getObjectId(), container);
		
		Future<?> newTask = ThreadPool.schedule(new ReportCheckTask(player), VALIDATION_TIME);
		ThreadPool.schedule(new countdown(player, VALIDATION_TIME / 1000), 0);
		_beginvalidation.put(player.getObjectId(), newTask);
	}
	
	protected void randomizeimages(PlayerData container, Player player)
	{
		int buttonscount = 3;
		int imagescount = _images.size();
		
		for (int i = 0; i < buttonscount; i++)
		{
			int next = _randomize.nextInt(imagescount);
			while (container.options.indexOf(next) > -1)
			{
				next = _randomize.nextInt(imagescount);
			}
			container.options.add(next);
		}
		
		int mainIndex = _randomize.nextInt(buttonscount);
		container.mainpattern = mainIndex;
		
		Calendar token = Calendar.getInstance();
		String uniquetoken = Integer.toString(token.get(Calendar.DAY_OF_MONTH)) + Integer.toString(token.get(Calendar.HOUR_OF_DAY)) + Integer.toString(token.get(Calendar.MINUTE)) + Integer.toString(token.get(Calendar.SECOND)) + Integer.toString(token.get(Calendar.MILLISECOND) / 100);
		container.patternid = Integer.parseInt(uniquetoken);
	}
	
	protected void banpunishment(Player player)
	{
		_validation.remove(player.getObjectId());
		_beginvalidation.get(player.getObjectId()).cancel(true);
		_beginvalidation.remove(player.getObjectId());
		
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				for (Player p : World.getInstance().getPlayers())
				{
					String all_hwids = p.getHWID();
					
					if (player.isOnline())
					{
						if (all_hwids.equals(player.getHWID()))
							p.logout();
					}
				}
			}
		}, 100);
		
		if (player.isOnline())
			player.getClient().closeNow();
	}
	
	public void Classes(String command, final Player activeChar)
	{
		if (command.startsWith("antipvp"))
		{
			AnalyseBypass(command, activeChar);
			activeChar.sendPacket(new PlaySound("ItemSound.quest_accept"));
			activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		}
	}
	
	public void AnalyseBypass(String command, Player player)
	{
		if (!_validation.containsKey(player.getObjectId()))
			return;
		
		String params = command.substring(command.indexOf("_") + 1);
		
		if (params.startsWith("continue"))
		{
			validationwindow(player);
			_validation.get(player.getObjectId()).firstWindow = false;
			return;
		}
		
		int choosenoption = -1;
		if (tryParseInt(params))
		{
			choosenoption = Integer.parseInt(params);
		}
		
		if (choosenoption > -1)
		{
			PlayerData playerData = _validation.get(player.getObjectId());
			if (choosenoption != playerData.mainpattern)
			{
				banpunishment(player);
			}
			else
			{
				player.sendMessage("Congratulations, colours match!");
				player.setWindowActive(false);
				_validation.remove(player.getObjectId());
				_beginvalidation.get(player.getObjectId()).cancel(true);
				_beginvalidation.remove(player.getObjectId());
			}
		}
	}
	
	protected class countdown implements Runnable
	{
		private final Player _player;
		private int _time;
		
		public countdown(Player player, int time)
		{
			_time = time;
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isOnline())
			{
				if (_validation.containsKey(_player.getObjectId()) && _validation.get(_player.getObjectId()).firstWindow)
				{
					if (_time % WINDOW_DELAY == 0)
					{
						validationwindow(_player);
						_validation.get(_player.getObjectId()).firstWindow = false;
					}
				}
				
				switch (_time)
				{
					case 300:
					case 240:
					case 180:
					case 120:
					case 60:
						_player.sendMessage(_time / 60 + " minute(s) to match colors.");
						break;
					case 30:
					case 10:
					case 5:
					case 4:
					case 3:
					case 2:
					case 1:
						_player.sendMessage(_time + " second(s) to match colors!");
						break;
				}
				if (_time > 1 && _validation.containsKey(_player.getObjectId()))
				{
					ThreadPool.schedule(new countdown(_player, _time - 1), 1000);
				}
			}
		}
	}
	
	protected boolean tryParseInt(String value)
	{
		try
		{
			Integer.parseInt(value);
			return true;
		}
		
		catch (NumberFormatException e)
		{
			return false;
		}
	}
	
	public void CaptchaSuccessfull(Player player)
	{
		if (_validation.get(player.getObjectId()) != null)
		{
			_validation.remove(player.getObjectId());
		}
	}
	
	public Boolean IsAlredyInReportMode(Player player)
	{
		if (_validation.get(player.getObjectId()) != null)
		{
			return true;
		}
		return false;
	}
	
	private class ReportCheckTask implements Runnable
	{
		private final Player _player;
		
		public ReportCheckTask(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_validation.get(_player.getObjectId()) != null)
			{
				banpunishment(_player);
			}
		}
	}
	
	public static final void Link(Player player, String request)
	{
		BotsPvPPreventionManager.getInstance().Classes(request, player);
	}
	
	private static class SingletonHolder
	{
		protected static final BotsPvPPreventionManager _instance = new BotsPvPPreventionManager();
	}
}
