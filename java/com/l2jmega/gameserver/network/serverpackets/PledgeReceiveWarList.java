package com.l2jmega.gameserver.network.serverpackets;

import java.util.List;

import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.model.pledge.Clan;

/**
 * _clanList can be either War List (_tab == 0) or Attacker List.
 * @author -Wooden-
 */
public class PledgeReceiveWarList extends L2GameServerPacket
{
	private final List<Integer> _clanList;
	private final int _tab;
	private final int _page;
	
	public PledgeReceiveWarList(List<Integer> clanList, int tab, int page)
	{
		_clanList = clanList;
		_tab = tab;
		_page = page;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x3e);
		writeD(_tab);
		writeD(_page);
		writeD((_tab == 0) ? _clanList.size() : (_page == 0) ? (_clanList.size() >= 13) ? 13 : _clanList.size() : _clanList.size() % (13 * _page));
		
		int index = 0;
		for (int clanId : _clanList)
		{
			Clan clan = ClanTable.getInstance().getClan(clanId);
			if (clan == null)
				continue;
			
			if (_tab != 0)
			{
				if (index < _page * 13)
				{
					index++;
					continue;
				}
				
				if (index == (_page + 1) * 13)
					break;
				
				index++;
			}
			
			writeS(clan.getName());
			writeD(_tab);
			writeD(_page);
		}
	}
}