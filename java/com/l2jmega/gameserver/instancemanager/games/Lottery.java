package com.l2jmega.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.util.Broadcast;

public class Lottery
{
	public static final long SECOND = 1000;
	public static final long MINUTE = 60000;
	
	protected static final Logger _log = Logger.getLogger(Lottery.class.getName());
	
	private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
	private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
	private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
	private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
	private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 and idnr = ?";
	
	protected int _number;
	protected int _prize;
	protected boolean _isSellingTickets;
	protected boolean _isStarted;
	protected long _endDate;
	
	protected Lottery()
	{
		_number = 1;
		_prize = Config.ALT_LOTTERY_PRIZE;
		_isSellingTickets = false;
		_isStarted = false;
		_endDate = System.currentTimeMillis();
		
		if (Config.ALLOW_LOTTERY)
			(new StartLottery()).run();
	}
	
	public static Lottery getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public int getId()
	{
		return _number;
	}
	
	public int getPrize()
	{
		return _prize;
	}
	
	public long getEndDate()
	{
		return _endDate;
	}
	
	public void increasePrize(int count)
	{
		_prize += count;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(UPDATE_PRICE);
			statement.setInt(1, getPrize());
			statement.setInt(2, getPrize());
			statement.setInt(3, getId());
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Lottery: Could not increase current lottery prize: " + e.getMessage(), e);
		}
	}
	
	public boolean isSellableTickets()
	{
		return _isSellingTickets;
	}
	
	public boolean isStarted()
	{
		return _isStarted;
	}
	
	private class StartLottery implements Runnable
	{
		protected StartLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(SELECT_LAST_LOTTERY);
				ResultSet rset = statement.executeQuery();
				
				if (rset.next())
				{
					_number = rset.getInt("idnr");
					
					if (rset.getInt("finished") == 1)
					{
						_number++;
						_prize = rset.getInt("newprize");
					}
					else
					{
						_prize = rset.getInt("prize");
						_endDate = rset.getLong("enddate");
						
						if (_endDate <= System.currentTimeMillis() + 2 * MINUTE)
						{
							(new FinishLottery()).run();
							rset.close();
							statement.close();
							return;
						}
						
						if (_endDate > System.currentTimeMillis())
						{
							_isStarted = true;
							ThreadPool.schedule(new FinishLottery(), _endDate - System.currentTimeMillis());
							
							if (_endDate > System.currentTimeMillis() + 12 * MINUTE)
							{
								_isSellingTickets = true;
								ThreadPool.schedule(new StopSellingTickets(), _endDate - System.currentTimeMillis() - 10 * MINUTE);
							}
							rset.close();
							statement.close();
							return;
						}
					}
				}
				rset.close();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.log(Level.WARNING, "Lottery: Could not restore lottery data: " + e.getMessage(), e);
			}
			
			_isSellingTickets = true;
			_isStarted = true;
			
			Broadcast.announceToOnlinePlayers("Lottery tickets are now available for Lucky Lottery #" + getId() + ".");
			Calendar finishtime = Calendar.getInstance();
			finishtime.setTimeInMillis(_endDate);
			finishtime.set(Calendar.MINUTE, 0);
			finishtime.set(Calendar.SECOND, 0);
			
			if (finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				_endDate = finishtime.getTimeInMillis();
				_endDate += 604800000;
			}
			else
			{
				finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				finishtime.set(Calendar.HOUR_OF_DAY, 19);
				_endDate = finishtime.getTimeInMillis();
			}
			
			ThreadPool.schedule(new StopSellingTickets(), _endDate - System.currentTimeMillis() - 10 * MINUTE);
			ThreadPool.schedule(new FinishLottery(), _endDate - System.currentTimeMillis());
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(INSERT_LOTTERY);
				statement.setInt(1, 1);
				statement.setInt(2, getId());
				statement.setLong(3, getEndDate());
				statement.setInt(4, getPrize());
				statement.setInt(5, getPrize());
				statement.execute();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.log(Level.WARNING, "Lottery: Could not store new lottery data: " + e.getMessage(), e);
			}
		}
	}
	
	private class StopSellingTickets implements Runnable
	{
		protected StopSellingTickets()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			_isSellingTickets = false;
			
			Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.LOTTERY_TICKET_SALES_TEMP_SUSPENDED));
		}
	}
	
	private class FinishLottery implements Runnable
	{
		protected FinishLottery()
		{
			// Do nothing
		}
		
		@Override
		public void run()
		{
			int[] luckynums = new int[5];
			int luckynum = 0;
			
			for (int i = 0; i < 5; i++)
			{
				boolean found = true;
				
				while (found)
				{
					luckynum = Rnd.get(20) + 1;
					found = false;
					
					for (int j = 0; j < i; j++)
						if (luckynums[j] == luckynum)
							found = true;
				}
				
				luckynums[i] = luckynum;
			}
			
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (luckynums[i] < 17)
					enchant += Math.pow(2, luckynums[i] - 1);
				else
					type2 += Math.pow(2, luckynums[i] - 17);
			}
			
			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(SELECT_LOTTERY_ITEM);
				statement.setInt(1, getId());
				ResultSet rset = statement.executeQuery();
				
				while (rset.next())
				{
					int curenchant = rset.getInt("enchant_level") & enchant;
					int curtype2 = rset.getInt("custom_type2") & type2;
					
					if (curenchant == 0 && curtype2 == 0)
						continue;
					
					int count = 0;
					
					for (int i = 1; i <= 16; i++)
					{
						int val = curenchant / 2;
						
						if (val != (double) curenchant / 2)
							count++;
						
						int val2 = curtype2 / 2;
						
						if (val2 != (double) curtype2 / 2)
							count++;
						
						curenchant = val;
						curtype2 = val2;
					}
					
					if (count == 5)
						count1++;
					else if (count == 4)
						count2++;
					else if (count == 3)
						count3++;
					else if (count > 0)
						count4++;
				}
				rset.close();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.log(Level.WARNING, "Lottery: Could restore lottery data: " + e.getMessage(), e);
			}
			
			int prize4 = count4 * Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
			int prize1 = 0;
			int prize2 = 0;
			int prize3 = 0;
			
			if (count1 > 0)
				prize1 = (int) ((getPrize() - prize4) * Config.ALT_LOTTERY_5_NUMBER_RATE / count1);
			
			if (count2 > 0)
				prize2 = (int) ((getPrize() - prize4) * Config.ALT_LOTTERY_4_NUMBER_RATE / count2);
			
			if (count3 > 0)
				prize3 = (int) ((getPrize() - prize4) * Config.ALT_LOTTERY_3_NUMBER_RATE / count3);
			
			// Calculate new prize.
			int newPrize = Config.ALT_LOTTERY_PRIZE + getPrize() - (prize1 + prize2 + prize3 + prize4);
			
			if (count1 > 0) // There are winners.
				Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.AMOUNT_FOR_WINNER_S1_IS_S2_ADENA_WE_HAVE_S3_PRIZE_WINNER).addNumber(getId()).addNumber(getPrize()).addNumber(count1));
			else
				// There are no winners.
				Broadcast.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.AMOUNT_FOR_LOTTERY_S1_IS_S2_ADENA_NO_WINNER).addNumber(getId()).addNumber(getPrize()));
		
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement(UPDATE_LOTTERY);
				statement.setInt(1, getPrize());
				statement.setInt(2, newPrize);
				statement.setInt(3, enchant);
				statement.setInt(4, type2);
				statement.setInt(5, prize1);
				statement.setInt(6, prize2);
				statement.setInt(7, prize3);
				statement.setInt(8, getId());
				statement.execute();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.log(Level.WARNING, "Lottery: Could not store finished lottery data: " + e.getMessage(), e);
			}
			
			ThreadPool.schedule(new StartLottery(), MINUTE);
			_number++;
			
			_isStarted = false;
		}
	}
	
	public static int[] decodeNumbers(int enchant, int type2)
	{
		int res[] = new int[5];
		int id = 0;
		int nr = 1;
		
		while (enchant > 0)
		{
			int val = enchant / 2;
			if (val != (double) enchant / 2)
				res[id++] = nr;
			
			enchant /= 2;
			nr++;
		}
		
		nr = 17;
		
		while (type2 > 0)
		{
			int val = type2 / 2;
			if (val != (double) type2 / 2)
				res[id++] = nr;
			
			type2 /= 2;
			nr++;
		}
		
		return res;
	}
	
	public static int[] checkTicket(ItemInstance item)
	{
		return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
	}
	
	public static int[] checkTicket(int id, int enchant, int type2)
	{
		int res[] =
		{
			0,
			0
		};
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT_LOTTERY_TICKET);
			statement.setInt(1, id);
			ResultSet rset = statement.executeQuery();
			
			if (rset.next())
			{
				int curenchant = rset.getInt("number1") & enchant;
				int curtype2 = rset.getInt("number2") & type2;
				
				if (curenchant == 0 && curtype2 == 0)
				{
					rset.close();
					statement.close();
					return res;
				}
				
				int count = 0;
				
				for (int i = 1; i <= 16; i++)
				{
					int val = curenchant / 2;
					if (val != (double) curenchant / 2)
						count++;
					
					int val2 = curtype2 / 2;
					if (val2 != (double) curtype2 / 2)
						count++;
					
					curenchant = val;
					curtype2 = val2;
				}
				
				switch (count)
				{
					case 0:
						break;
					
					case 5:
						res[0] = 1;
						res[1] = rset.getInt("prize1");
						break;
					
					case 4:
						res[0] = 2;
						res[1] = rset.getInt("prize2");
						break;
					
					case 3:
						res[0] = 3;
						res[1] = rset.getInt("prize3");
						break;
					
					default:
						res[0] = 4;
						res[1] = Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
				}
			}
			
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "Lottery: Could not check lottery ticket #" + id + ": " + e.getMessage(), e);
		}
		
		return res;
	}
	
	private static class SingletonHolder
	{
		protected static final Lottery _instance = new Lottery();
	}
}