package com.l2jmega.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.gameserver.model.memo.AbstractMemo;

/**
 * A global, server-size, container for variables of any type, which can be then saved/restored upon server restart. It extends {@link AbstractMemo}.
 */
@SuppressWarnings("serial")
public class ServerMemoTable extends AbstractMemo
{
	private static final Logger LOG = Logger.getLogger(ServerMemoTable.class.getName());
	
	private static final String SELECT_QUERY = "SELECT * FROM server_memo";
	private static final String DELETE_QUERY = "DELETE FROM server_memo";
	private static final String INSERT_QUERY = "INSERT INTO server_memo (var, value) VALUES (?, ?)";
	
	protected ServerMemoTable()
	{
		restoreMe();
	}
	
	@Override
	public boolean restoreMe()
	{
		// Restore previous variables.
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			Statement st = con.createStatement();
			
			ResultSet rset = st.executeQuery(SELECT_QUERY);
			while (rset.next())
				set(rset.getString("var"), rset.getString("value"));
			
			rset.close();
			st.close();
		}
		catch (SQLException e)
		{
			LOG.log(Level.SEVERE, "Couldn't restore server variables.", e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		LOG.info("Loaded " + size() + " server variables.");
		return true;
	}
	
	@Override
	public boolean storeMe()
	{
		// No changes, nothing to store.
		if (!hasChanges())
			return false;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			// Clear previous entries.
			Statement del = con.createStatement();
			del.execute(DELETE_QUERY);
			del.close();
			
			// Insert all variables.
			PreparedStatement st = con.prepareStatement(INSERT_QUERY);
			for (Entry<String, Object> entry : entrySet())
			{
				st.setString(1, entry.getKey());
				st.setString(2, String.valueOf(entry.getValue()));
				st.addBatch();
			}
			st.executeBatch();
			st.close();
		}
		catch (SQLException e)
		{
			LOG.log(Level.SEVERE, "Couldn't save server variables to database.", e);
			return false;
		}
		finally
		{
			compareAndSetChanges(true, false);
		}
		LOG.info("Stored " + size() + " server variables.");
		return true;
	}
	
	public static final ServerMemoTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerMemoTable INSTANCE = new ServerMemoTable();
	}
}