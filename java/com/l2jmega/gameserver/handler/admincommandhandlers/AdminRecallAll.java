/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmega.gameserver.handler.admincommandhandlers;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.math.MathUtil;

import com.l2jmega.gameserver.handler.IAdminCommandHandler;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.serverpackets.ConfirmDlg;

/**
 * @author Mayke
 */
public class AdminRecallAll implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_recallall"
	};
	public static boolean isAdminSummoning = false;
	public static int x = 0;
	public static int y = 0;
	public static int z = 0;
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_recallall"))
		{
			x = activeChar.getX();
			y = activeChar.getY();
			z = activeChar.getZ();
			isAdminSummoning = true;
			
			for (Player player : World.getInstance().getPlayers())
			{
				try
				{
					final boolean bishop = (player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.ELVEN_ELDER);
					
					if (player.isAlikeDead() || player._inEventTvT || player.isAio() || player.isAioEterno() || player._inEventCTF || player.isInArenaEvent() || player.isArenaProtection() || player.isOlympiadProtection() || player.isInsideZone(ZoneId.CUSTOM) || player.isPhantom() || player.isInStoreMode() || player.isRooted() || player.getKarma() > 0 || player.isInOlympiadMode() || player.isOlympiadProtection() || player.isFestivalParticipant() || bishop)
						continue;
					
					if (!MathUtil.checkIfInRange(0, activeChar, player, false))
					{
						ThreadPool.schedule(new Restore(), 15000);
						ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
						confirm.addString("GM Server");
						confirm.addZoneName(activeChar.getPosition());
						confirm.addTime(45000);
						confirm.addRequesterId(activeChar.getObjectId());
						player.sendPacket(confirm);
					}
					player = null;
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			
		}
		return false;
		
	}
	
	class Restore implements Runnable
	{
		@Override
		public void run()
		{
			x = 0;
			y = 0;
			z = 0;
			isAdminSummoning = false;
		}
		
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}