package com.l2jmega.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import com.l2jmega.commons.concurrent.ThreadPool;

import hwid.Hwid;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.model.CharSelectInfoPackage;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.FloodProtectors;
import com.l2jmega.gameserver.network.FloodProtectors.Action;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GameClient.GameClientState;
import com.l2jmega.gameserver.network.serverpackets.CharSelected;
import com.l2jmega.gameserver.network.serverpackets.SSQInfo;
import com.l2jmega.gameserver.network.serverpackets.TutorialShowHtml;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;
	
	@SuppressWarnings("unused")
	private int _unk1; // new in C4
	@SuppressWarnings("unused")
	private int _unk2; // new in C4
	@SuppressWarnings("unused")
	private int _unk3; // new in C4
	@SuppressWarnings("unused")
	private int _unk4; // new in C4
	
	@Override
	protected void readImpl()
	{
		_charSlot = readD();
		_unk1 = readH();
		_unk2 = readD();
		_unk3 = readD();
		_unk4 = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2GameClient client = getClient();
		if (!FloodProtectors.performAction(client, Action.CHARACTER_SELECT))
			return;
		
		// we should always be able to acquire the lock but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getActiveCharLock().tryLock())
		{
			try
			{
				// should always be null but if not then this is repeated packet and nothing should be done here
				if (client.getActiveChar() == null)
				{
					final CharSelectInfoPackage info = client.getCharSelection(_charSlot);
					if (info == null || info.getAccessLevel() < 0)
						return;
					
					// Load up character from disk
					final Player cha = client.loadCharFromDisk(_charSlot);
					if (cha == null)
						return;
					
					cha.setClient(client);
					client.setActiveChar(cha);
					cha.setOnlineStatus(true, true);
					
					sendPacket(SSQInfo.sendSky());
					
					if (!Hwid.checkPlayerWithHWID(client, cha.getObjectId(), cha.getName()))
						return;
					
					cha.ReloadBlockChat(false);
					
					if (cha.ChatProtection(cha.getHWID()) && ((cha.getChatBanTimer() - 1500) > System.currentTimeMillis()))
						cha.setChatBlock(true);
					
					if (ChargebackHwid(client.getHWID()))
					{
						_log.info("------------------------------------------------------------------------------");
						_log.info("CHARGEBACK HWID : [" + cha.getName() + "] - HWID: [" + cha.getHWID() + "] --> CALOTEIRO!");
						_log.info("------------------------------------------------------------------------------");
						cha.setAccountAccesslevel(-100);
						ThreadPool.schedule(new Runnable()
						{
							@Override
							public void run()
							{
								addBlock(cha.getClient().getConnection().getInetAddress().getHostAddress());
								getClient().closeNow();
							}
						}, 100);
						return;
					}
					else if (BanedHwid(cha.getHWID()))
					{
						addBlock(cha.getClient().getConnection().getInetAddress().getHostAddress());
						_log.info("Player Name: [" + cha.getName() + "] - HWID: [" + client.getHWID() + "] --> Banido !");
						getClient().closeNow();
						return;
					}
					
					client.setState(GameClientState.ENTERING);
					
					sendPacket(new CharSelected(cha, client.getSessionId().playOkID1));
				}
			}
			finally
			{
				client.getActiveCharLock().unlock();
			}
		}
	}
	
	public static void addBlock(String ip)
	{
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO ip_protection (ip) VALUES (?)");
			statement.setString(1, ip);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("CharacterSelected -> addBlock: " + e.getMessage());
		}
	}
	
	public static void addBlockHwid(String name, String hwid)
	{
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("INSERT INTO banned_hwid (char_name,hwid) VALUES (?,?)");
			statement.setString(1, name);
			statement.setString(2, hwid);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("CharacterSelected -> addBlockHwid: " + e.getMessage());
		}
	}
	
	public synchronized boolean BanedHwid(String hwidban)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hwid FROM banned_hwid WHERE hwid=?");
			statement.setString(1, hwidban);
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "BanedHwid: " + e.getMessage(), e);
		}
		return result;
	}
	
	public synchronized boolean GM_HWID(String hwidban)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hwid FROM gm_hwid WHERE hwid=?");
			statement.setString(1, hwidban);
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "GM_HWID: " + e.getMessage(), e);
		}
		return result;
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	@SuppressWarnings("null")
	public static void Ban_Hwid_Time(Player activeChar)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement statement = con.prepareStatement("SELECT duration FROM antibot_hwid WHERE hwid=?");
			statement.setString(1, activeChar.getHWID());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				long duration = rset.getLong("duration");
				
				String msg = HtmCache.getInstance().getHtm("data/html/mods/protect/banned_time.htm");
				msg = msg.replaceAll("%time%", new SimpleDateFormat("HH:mm").format(new Date(duration)).toString());
				msg = msg.replaceAll("%date%", sdf.format(new Date(System.currentTimeMillis())).toString());
				activeChar.sendPacket(new TutorialShowHtml(msg));
				
			}
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Ban_Hwid_Time: " + e.getMessage(), e);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			con = null;
		}
	}
	
	public synchronized boolean ChargebackHwid(String hwidban)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hwid FROM chargeback_hwid WHERE hwid=?");
			statement.setString(1, hwidban);
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
		return result;
	}
	
	public synchronized boolean check_hwid_antibot(String hwid)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hwid FROM antibot_hwid WHERE hwid=?");
			statement.setString(1, hwid);
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "check_hwid_antibot: " + e.getMessage(), e);
		}
		return result;
		
	}
	
}