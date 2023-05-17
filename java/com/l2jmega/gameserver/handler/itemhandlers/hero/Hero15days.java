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
package com.l2jmega.gameserver.handler.itemhandlers.hero;

import com.l2jmega.gameserver.handler.IItemHandler;
import com.l2jmega.gameserver.instancemanager.HeroManager;
import com.l2jmega.gameserver.model.actor.Playable;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.network.serverpackets.ExShowScreenMessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * @author MeGaPacK
 */
public class Hero15days implements IItemHandler
{
	
	protected static final Logger LOGGER = Logger.getLogger(Hero15days.class.getName());
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("SYS: Voce nao pode fazer isso.");
			return;
		}
		
		if (activeChar.isHero())
		{
			activeChar.sendMessage("SYS: Voce ja esta com status Hero.");
			return;
		}
		
		playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
		
		int mes = 15;
		if (HeroManager.getInstance().hasHeroPrivileges(activeChar.getObjectId()))
		{
			long _daysleft;
			final long now = Calendar.getInstance().getTimeInMillis();
			long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
			final long endDay = duration;
			
			_daysleft = ((endDay - now) / 86400000) + mes + 1;
			
			long end_day;
			final Calendar calendar = Calendar.getInstance();
			if (_daysleft >= 30)
			{
				while (_daysleft >= 30)
				{
					if (calendar.get(Calendar.MONTH) == 11)
						calendar.roll(Calendar.YEAR, true);
					calendar.roll(Calendar.MONTH, true);
					_daysleft -= 30;
				}
			}
			
			if (_daysleft < 30 && _daysleft > 0)
			{
				while (_daysleft > 0)
				{
					if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
						calendar.roll(Calendar.MONTH, true);
					if (calendar.get(Calendar.DATE) == 30)
					{
						if (calendar.get(Calendar.MONTH) == 11)
							calendar.roll(Calendar.YEAR, true);
						calendar.roll(Calendar.MONTH, true);
						
					}
					calendar.roll(Calendar.DATE, true);
					_daysleft--;
				}
			}
			
			end_day = calendar.getTimeInMillis();
			HeroManager.getInstance().updateHero(activeChar.getObjectId(), end_day);
		}
		else
		{
			long end_day;
			final Calendar calendar = Calendar.getInstance();
			if (mes >= 30)
			{
				while (mes >= 30)
				{
					if (calendar.get(Calendar.MONTH) == 11)
						calendar.roll(Calendar.YEAR, true);
					calendar.roll(Calendar.MONTH, true);
					mes -= 30;
				}
			}
			
			if (mes < 30 && mes > 0)
			{
				while (mes > 0)
				{
					if (calendar.get(Calendar.DATE) == 28 && calendar.get(Calendar.MONTH) == 1)
						calendar.roll(Calendar.MONTH, true);
					if (calendar.get(Calendar.DATE) == 30)
					{
						if (calendar.get(Calendar.MONTH) == 11)
							calendar.roll(Calendar.YEAR, true);
						calendar.roll(Calendar.MONTH, true);
						
					}
					calendar.roll(Calendar.DATE, true);
					mes--;
				}
			}
			
			end_day = calendar.getTimeInMillis();
			HeroManager.getInstance().addHero(activeChar.getObjectId(), end_day);
		}
		
		long _daysleft;
		final long now = Calendar.getInstance().getTimeInMillis();
		long duration = HeroManager.getInstance().getHeroDuration(activeChar.getObjectId());
		final long endDay = duration;
		_daysleft = ((endDay - now) / 86400000);
		if (_daysleft < 270)
		{
			activeChar.sendPacket(new ExShowScreenMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".", 10000));
			activeChar.sendMessage("Your Hero privileges ends at " + new SimpleDateFormat("dd MMM, HH:mm").format(new Date(duration)) + ".");
		}
		
	}
}
