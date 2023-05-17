package com.l2jmega.gameserver.network;

import hwid.Hwid;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.mmocore.MMOClient;
import com.l2jmega.commons.mmocore.MMOConnection;
import com.l2jmega.commons.mmocore.ReceivablePacket;

import com.l2jmega.Config;
import com.l2jmega.L2DatabaseFactory;
import com.l2jmega.events.ArenaTask;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.LoginServerThread;
import com.l2jmega.gameserver.data.PlayerNameTable;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.model.CharSelectInfoPackage;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jmega.gameserver.network.serverpackets.ServerClose;
import com.l2jmega.gameserver.LoginServerThread.SessionKey;

/**
 * Represents a client connected on Game Server
 * @author KenM
 */
public final class L2GameClient extends MMOClient<MMOConnection<L2GameClient>> implements Runnable
{
	protected static final Logger _log = Logger.getLogger(L2GameClient.class.getName());
	
	public static enum GameClientState
	{
		CONNECTED, // client has just connected
		AUTHED, // client has authed but doesnt has character attached to it yet
		ENTERING, // client is currently loading his Player instance, but didn't end
		IN_GAME // client has selected a char and is in game
	}
	
	public GameClientState _state;
	
	// Info
	private String _accountName;
	private SessionKey _sessionId;
	private Player _activeChar;
	private final ReentrantLock _activeCharLock = new ReentrantLock();
	
	@SuppressWarnings("unused")
	private boolean _isAuthedGG;
	private final long _connectionStartTime;
	private CharSelectInfoPackage[] _charSlotMapping = null;
	
	// floodprotectors
	private final long[] _floodProtectors = new long[FloodProtectors.Action.VALUES_LENGTH];
	
	// Task
	protected final ScheduledFuture<?> _autoSaveInDB;
	protected ScheduledFuture<?> _cleanupTask = null;
	
	public GameCrypt _crypt;
	private final ClientStats _stats;
	
	private boolean _isDetached = false;
	
	private final ArrayBlockingQueue<ReceivablePacket<L2GameClient>> _packetQueue;
	private final ReentrantLock _queueLock = new ReentrantLock();
	
	public L2GameClient(MMOConnection<L2GameClient> con)
	{
		super(con);
		_state = GameClientState.CONNECTED;
		_connectionStartTime = System.currentTimeMillis();
		_crypt = new GameCrypt();
		_stats = new ClientStats();
		_packetQueue = new ArrayBlockingQueue<>(Config.CLIENT_PACKET_QUEUE_SIZE);
		
		_autoSaveInDB = ThreadPool.scheduleAtFixedRate(new AutoSaveTask(), 300000L, 900000L);
	}
	
	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		_crypt.setKey(key);
		
		if (Hwid.isProtectionOn())
			key = Hwid.getKey(key);
		
		return key;
	}
	
	public GameClientState getState()
	{
		return _state;
	}
	
	public void setState(GameClientState pState)
	{
		if (_state != pState)
		{
			_state = pState;
			_packetQueue.clear();
		}
	}
	
	public ClientStats getStats()
	{
		return _stats;
	}
	
	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}
	
	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		_crypt.decrypt(buf.array(), buf.position(), size);
		return true;
	}
	
	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		_crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}
	
	public Player getActiveChar()
	{
		return _activeChar;
	}
	
	public void setActiveChar(Player pActiveChar)
	{
		_activeChar = pActiveChar;
	}
	
	public ReentrantLock getActiveCharLock()
	{
		return _activeCharLock;
	}
	
	public long[] getFloodProtectors()
	{
		return _floodProtectors;
	}
	
	public void setGameGuardOk(boolean val)
	{
		_isAuthedGG = val;
	}
	
	public void setAccountName(String pAccountName)
	{
		_accountName = pAccountName;
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public void setSessionId(SessionKey sk)
	{
		_sessionId = sk;
	}
	
	public SessionKey getSessionId()
	{
		return _sessionId;
	}
	
	public void sendPacket(L2GameServerPacket gsp)
	{
		if (_isDetached)
			return;
		
		getConnection().sendPacket(gsp);
		gsp.runImpl();
	}
	
	public boolean isDetached()
	{
		return _isDetached;
	}
	
	public void setDetached(boolean b)
	{
		_isDetached = b;
	}
	
	/**
	 * Method to handle character deletion
	 * @param charslot The slot to check.
	 * @return a byte: <li>-1: Error: No char was found for such charslot, caught exception, etc... <li>0: character is not member of any clan, proceed with deletion <li>1: character is member of a clan, but not clan leader <li>2: character is clan leader
	 */
	public byte markToDeleteChar(int charslot)
	{
		int objid = getObjectIdForSlot(charslot);
		
		if (objid < 0)
			return -1;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT clanId FROM characters WHERE obj_id=?");
			statement.setInt(1, objid);
			ResultSet rs = statement.executeQuery();
			
			rs.next();
			
			int clanId = rs.getInt(1);
			byte answer = 0;
			if (clanId != 0)
			{
				Clan clan = ClanTable.getInstance().getClan(clanId);
				
				if (clan == null)
					answer = 0; // jeezes!
				else if (clan.getLeaderId() == objid)
					answer = 2;
				else
					answer = 1;
			}
			
			rs.close();
			statement.close();
			
			// Setting delete time
			if (answer == 0)
			{
				if (Config.DELETE_DAYS == 0)
					deleteCharByObjId(objid);
				else
				{
					statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
					statement.setLong(1, System.currentTimeMillis() + Config.DELETE_DAYS * 86400000L);
					statement.setInt(2, objid);
					statement.execute();
					statement.close();
				}
			}
			
			return answer;
		}
		catch (Exception e)
		{
			if (Config.DEBUG)
				_log.log(Level.SEVERE, "Error updating delete time of character.", e);
			
			return -1;
		}
	}
	
	public void markRestoredChar(int charslot)
	{
		final int objid = getObjectIdForSlot(charslot);
		if (objid < 0)
			return;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			if (Config.DEBUG)
				_log.log(Level.SEVERE, "Error restoring character.", e);
		}
	}
	
	public static void deleteCharByObjId(int objid)
	{
		if (objid < 0)
			return;
		
		PlayerNameTable.getInstance().removePlayer(objid);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			
			statement = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? OR friend_id=?");
			statement.setInt(1, objid);
			statement.setInt(2, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_quests WHERE charId=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM heroes WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM augmentations WHERE item_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM character_raid_points WHERE char_id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
			statement.setInt(1, objid);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			if (Config.DEBUG)
				_log.log(Level.SEVERE, "Error deleting character.", e);
		}
	}
	
	public Player loadCharFromDisk(int slot)
	{
		final int objectId = getObjectIdForSlot(slot);
		if (objectId < 0)
			return null;
		
		Player player = World.getInstance().getPlayer(objectId);
		if (player != null)
		{
			// exploit prevention, should not happens in normal way
			_log.severe("Attempt of double login: " + player.getName() + "(" + objectId + ") " + getAccountName());
			
			if (player.getClient() != null)
				player.getClient().closeNow();
			else
				player.deleteMe();
			
			return null;
		}
		
		player = Player.restore(objectId);
		if (player != null)
		{
			player.setRunning(); // running is default
			player.standUp(); // standing is default
			
			player.setOnlineStatus(true, false);
			World.getInstance().addPlayer(player);
		}
		else
			_log.severe("L2GameClient: could not restore in slot: " + slot);
		
		return player;
	}
	
	/**
	 * @param chars
	 */
	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping = chars;
	}
	
	public CharSelectInfoPackage getCharSelection(int charslot)
	{
		if ((_charSlotMapping == null) || (charslot < 0) || (charslot >= _charSlotMapping.length))
			return null;
		
		return _charSlotMapping[charslot];
	}
	
	public void close(L2GameServerPacket gsp)
	{
		getConnection().close(gsp);
	}
	
	/**
	 * @param charslot
	 * @return
	 */
	private int getObjectIdForSlot(int charslot)
	{
		final CharSelectInfoPackage info = getCharSelection(charslot);
		if (info == null)
		{
			_log.warning(toString() + " tried to delete Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		return info.getObjectId();
	}
	
	@Override
	protected void onForcedDisconnection()
	{
		if (Config.DEBUG)
			_log.fine("Client " + toString() + " disconnected abnormally.");
	}
	
	@Override
	protected void onDisconnection()
	{
		// no long running tasks here, do it async
		try
		{
			ThreadPool.execute(new DisconnectTask());
		}
		catch (RejectedExecutionException e)
		{
			// server is closing
		}
	}
	
	/**
	 * Close client connection with {@link ServerClose} packet
	 */
	public void closeNow()
	{
		_isDetached = true; // prevents more packets execution
		close(ServerClose.STATIC_PACKET);
		synchronized (this)
		{
			if (_cleanupTask != null)
				cancelCleanup();
			
			_cleanupTask = ThreadPool.schedule(new CleanupTask(), 0); // instant
		}
	}
	
	/**
	 * Produces the best possible string representation of this client.
	 */
	@Override
	public String toString()
	{
		try
		{
			final InetAddress address = getConnection().getInetAddress();
			switch (getState())
			{
				case CONNECTED:
					return "[IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case AUTHED:
					return "[Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case ENTERING:
				case IN_GAME:
					return "[Character: " + (getActiveChar() == null ? "disconnected" : getActiveChar().getName()) + " - Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				default:
					throw new IllegalStateException("Missing state on switch");
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}
	
	protected class DisconnectTask implements Runnable
	{
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			boolean fast = true;
			
			try
			{
				
				Player player = getActiveChar();
				if (player != null) // this should only happen on connection loss
				{
					if (player.isArenaProtection())
					{
						player.setXYZ(ArenaTask.loc1x(), ArenaTask.loc1y(), ArenaTask.loc1z());
						if (player.isInArenaEvent())
						{
							player.getAppearance().setTitleColor(player._originalTitleColorTournament);
							player.setTitle(player._originalTitleTournament);
							player.broadcastUserInfo();
							player.broadcastTitleInfo();
						}
					}
					
					if (player.isWindowActive())
						player.setXYZ(81256, 149192, -3464);
					
					if (player._inEventTvT)
						TvT.onDisconnect(player);
					
					if (player._inEventCTF)
						CTF.onDisconnect(player);
					
					if ((player.isInStoreMode() && Config.OFFLINE_TRADE_ENABLE) || (player.isCrafting() && Config.OFFLINE_CRAFT_ENABLE))
					{
						player.leaveParty();
						if (Config.OFFLINE_SET_NAME_COLOR)
						{
							player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
							player.broadcastUserInfo();
						}
						return;
					}
					
					// Decrease boxes number
					if (player._active_boxes != -1)
						player.decreaseBoxes();
					
				}
				
				if (getActiveChar() != null && !isDetached())
				{
					setDetached(true);
					fast = !getActiveChar().isInCombat() && !getActiveChar().isLocked();
				}
				cleanMe(fast);
			}
			catch (Exception e1)
			{
				if (Config.DEBUG)
					_log.log(Level.WARNING, "error while disconnecting client", e1);
			}
		}
	}
	
	public void cleanMe(boolean fast)
	{
		try
		{
			synchronized (this)
			{
				if (_cleanupTask == null)
					_cleanupTask = ThreadPool.schedule(new CleanupTask(), fast ? 5 : 15000L);
			}
		}
		catch (Exception e1)
		{
			_log.log(Level.WARNING, "Error during cleanup.", e1);
		}
	}
	
	protected class CleanupTask implements Runnable
	{
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			try
			{
				// we are going to manually save the char below thus we can force the cancel
				if (_autoSaveInDB != null)
					_autoSaveInDB.cancel(true);
				
				Player player = getActiveChar();
				if (player != null) // this should only happen on connection loss
				{
					if (player.isArenaProtection())
					{
						player.setXYZ(ArenaTask.loc1x(), ArenaTask.loc1y(), ArenaTask.loc1z());
						if (player.isInArenaEvent())
						{
							player.getAppearance().setTitleColor(player._originalTitleColorTournament);
							player.setTitle(player._originalTitleTournament);
							player.broadcastUserInfo();
							player.broadcastTitleInfo();
						}
					}
					
					if (player.isWindowActive())
						player.setXYZ(81256, 149192, -3464);
					
					if (player._inEventTvT)
						TvT.onDisconnect(player);
					
					if (player._inEventCTF)
						CTF.onDisconnect(player);
					
					// Decrease boxes number
					if (player._active_boxes != -1)
						player.decreaseBoxes();
					
				}
				
				if (getActiveChar() != null) // this should only happen on connection loss
				{
					if (getActiveChar().isLocked())
						_log.log(Level.WARNING, getActiveChar().getName() + " is still performing subclass actions during disconnect.");
					
					// prevent closing again
					getActiveChar().setClient(null);
					
					if (getActiveChar().isOnline())
						getActiveChar().deleteMe();
				}
				setActiveChar(null);
			}
			catch (Exception e1)
			{
				_log.log(Level.WARNING, "Error while cleanup client.", e1);
			}
			finally
			{
				LoginServerThread.getInstance().sendLogout(getAccountName());
			}
		}
	}
	
	protected class AutoSaveTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (getActiveChar() != null && getActiveChar().isOnline())
				{
					getActiveChar().store();
					
					if (getActiveChar().getPet() != null)
						getActiveChar().getPet().store();
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error on AutoSaveTask.", e);
			}
		}
	}
	
	/**
	 * @return false if client can receive packets. True if detached, or flood detected, or queue overflow detected and queue still not empty.
	 */
	public boolean dropPacket()
	{
		if (_isDetached) // detached clients can't receive any packets
			return true;
		
		// flood protection
		if (getStats().countPacket(_packetQueue.size()))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		return getStats().dropPacket();
	}
	
	/**
	 * Counts buffer underflow exceptions.
	 */
	public void onBufferUnderflow()
	{
		if (getStats().countUnderflowException())
		{
			if (Config.DEBUG)
				_log.severe("Client " + toString() + " - Disconnected: Too many buffer underflow exceptions.");
			
			closeNow();
			return;
		}
		if (_state == GameClientState.CONNECTED) // in CONNECTED state kick client immediately
		{
			if (Config.PACKET_HANDLER_DEBUG)
				_log.severe("Client " + toString() + " - Disconnected, too many buffer underflows in non-authed state.");
			closeNow();
		}
	}
	
	/**
	 * Counts unknown packets
	 */
	public void onUnknownPacket()
	{
		if (getStats().countUnknownPacket())
		{
			if (Config.DEBUG)
				_log.severe("Client " + toString() + " - Disconnected: Too many unknown packets.");
			
			closeNow();
			return;
		}
		if (_state == GameClientState.CONNECTED) // in CONNECTED state kick client immediately
		{
			if (Config.PACKET_HANDLER_DEBUG)
				_log.severe("Client " + toString() + " - Disconnected, too many unknown packets in non-authed state.");
			closeNow();
		}
	}
	
	/**
	 * Add packet to the queue and start worker thread if needed
	 * @param packet The packet to execute.
	 */
	public void execute(ReceivablePacket<L2GameClient> packet)
	{
		if (getStats().countFloods())
		{
			if (Config.DEBUG)
				_log.severe("Client " + toString() + " - Disconnected, too many floods:" + getStats().longFloods + " long and " + getStats().shortFloods + " short.");
			
			closeNow();
			return;
		}
		
		if (!_packetQueue.offer(packet))
		{
			if (getStats().countQueueOverflow())
			{
				if (Config.DEBUG)
					_log.severe("Client " + toString() + " - Disconnected, too many queue overflows.");
				
				closeNow();
			}
			else
				sendPacket(ActionFailed.STATIC_PACKET);
			
			return;
		}
		
		if (_queueLock.isLocked()) // already processing
			return;
		
		try
		{
			if (_state == GameClientState.CONNECTED && getStats().processedPackets > 3)
			{
				if (Config.PACKET_HANDLER_DEBUG)
					_log.severe("Client " + toString() + " - Disconnected, too many packets in non-authed state.");
				
				closeNow();
				return;
			}
			
			ThreadPool.execute(this);
		}
		catch (RejectedExecutionException e)
		{
		}
	}
	
	@Override
	public void run()
	{
		if (!_queueLock.tryLock())
			return;
		
		try
		{
			int count = 0;
			ReceivablePacket<L2GameClient> packet;
			while (true)
			{
				packet = _packetQueue.poll();
				if (packet == null) // queue is empty
					return;
				
				if (_isDetached) // clear queue immediately after detach
				{
					_packetQueue.clear();
					return;
				}
				
				try
				{
					packet.run();
				}
				catch (Exception e)
				{
					if (Config.DEBUG)
						_log.severe("Exception during execution " + packet.getClass().getSimpleName() + ", client: " + toString() + "," + e.getMessage());
				}
				
				count++;
				if (getStats().countBurst(count))
					return;
			}
		}
		finally
		{
			_queueLock.unlock();
		}
	}
	
	private boolean cancelCleanup()
	{
		final Future<?> task = _cleanupTask;
		if (task != null)
		{
			_cleanupTask = null;
			return task.cancel(true);
		}
		return false;
	}
	
	// HWID
	private String _playerName = "";
	private String _loginName = "";
	private int _playerId = 0;
	private String _hwid = "";
	private int revision = 0;
	
	public final String getPlayerName()
	{
		return _playerName;
	}
	
	public void setPlayerName(String name)
	{
		_playerName = name;
	}
	
	public void setPlayerId(int plId)
	{
		_playerId = plId;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public final String getHWID()
	{
		return _hwid;
	}
	
	public void setHWID(String hwid)
	{
		_hwid = hwid;
	}
	
	public void setRevision(int revision)
	{
		this.revision = revision;
	}
	
	public int getRevision()
	{
		return this.revision;
	}
	
	public final String getLoginName()
	{
		return _loginName;
	}
	
	public void setLoginName(String name)
	{
		_loginName = name;
	}
}