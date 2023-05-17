package com.l2jmega.gameserver.handler.admincommandhandlers;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.data.SpawnTable;
import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.network.L2GameClient;
import com.l2jmega.gameserver.network.L2GameClient.GameClientState;
import com.l2jmega.gameserver.network.SystemMessageId;

import phantom.Phantom_Attack;
import phantom.Phantom_Farm;
import phantom.Phantom_Town;

/**
 * This class handles following admin commands: - delete = deletes target
 */
public class AdminDelete implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_delete",
		"admin_deleteallphantomtown",
		"admin_deleteallphantompvp",
		"admin_deleteallphantomfarm"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		
		if (command.equals("admin_delete"))
		{
			WorldObject target = activeChar.getTarget();
			Player player = null;
			
			if (target != null && target instanceof Player)
			{
				player = (Player) target;
				if (player.isPhantom())
				{
					if (player.isPhantomArchMage() || player.isPhantomMysticMuse() || player.isPhantomStormScream())
						Phantom_Attack.removePhantom(player);
					
					final L2GameClient client = player.getClient();
					// detach the client from the char so that the connection isnt closed in the deleteMe
					player.setClient(null);
					// removing player from the world
					player.deleteMe();
					client.setActiveChar(null);
					client.setState(GameClientState.AUTHED);
				}
			}
			else
				handleDelete(activeChar);
			
			handleDelete(activeChar);
		}
		if (command.startsWith("admin_deleteallphantomtown"))
		{			
			for (Player player : World.getInstance().getPlayers())
			{
				if (player.isPhantom() && !player.isPhantomArcher() && !player.isPhantomArchMage() && !player.isPhantomMysticMuse() && !player.isPhantomStormScream() && !player.isFarmArchMage() && !player.isFarmMysticMuse() && !player.isFarmStormScream())
				{
					Phantom_Town.removePhantom(player);
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
				final L2GameClient client = player.getClient();
				// detach the client from the char so that the connection isnt closed in the deleteMe
				player.setClient(null);
				// removing player from the world
				player.deleteMe();
				client.setActiveChar(null);
				client.setState(GameClientState.AUTHED);
					}
				}, Rnd.get(10000, 30000));
				}
			}
			activeChar.sendMessage("Deletando os phantom town.");
			
		}
		if (command.startsWith("admin_deleteallphantompvp"))
		{			
			for (Player player : World.getInstance().getPlayers())
			{
				if (player.isPhantomArchMage() || player.isPhantomMysticMuse() || player.isPhantomStormScream())
				{
					Phantom_Attack.removePhantom(player);
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
				final L2GameClient client = player.getClient();
				// detach the client from the char so that the connection isnt closed in the deleteMe
				player.setClient(null);
				// removing player from the world
				player.deleteMe();
				client.setActiveChar(null);
				client.setState(GameClientState.AUTHED);
					}
				}, Rnd.get(10000, 30000));
				}
			}
			activeChar.sendMessage("Deletando os phantom pvp.");
		}
		if (command.startsWith("admin_deleteallphantomfarm"))
		{			
			for (Player player : World.getInstance().getPlayers())
			{
				if (player.isFarmArchMage() || player.isFarmMysticMuse() || player.isFarmStormScream())
				{
					Phantom_Farm.removePhantom(player);
				ThreadPool.schedule(new Runnable()
				{
					@Override
					public void run()
					{
				final L2GameClient client = player.getClient();
				// detach the client from the char so that the connection isnt closed in the deleteMe
				player.setClient(null);
				// removing player from the world
				player.deleteMe();
				client.setActiveChar(null);
				client.setState(GameClientState.AUTHED);
					}
				}, Rnd.get(10000, 30000));
				}
			}
			activeChar.sendMessage("Deletando os phantom farm.");
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void handleDelete(Player activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 7)
			return;
		
		WorldObject obj = activeChar.getTarget();
		if (obj != null && obj instanceof Npc)
		{
			Npc target = (Npc) obj;
			
			L2Spawn spawn = target.getSpawn();
			if (spawn != null)
			{
				spawn.setRespawnState(false);
				
				if (RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcId()))
					RaidBossSpawnManager.getInstance().deleteSpawn(spawn, true);
				else
					SpawnTable.getInstance().deleteSpawn(spawn, true);
			}
			target.deleteMe();
			
			activeChar.sendMessage("Deleted " + target.getName() + " from " + target.getObjectId() + ".");
		}
		else
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
	}
}