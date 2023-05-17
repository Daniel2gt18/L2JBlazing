package com.l2jmega.gameserver.network.serverpackets;

import static com.l2jmega.gameserver.data.xml.MultisellData.PAGE_SIZE;

import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.model.multisell.Entry;
import com.l2jmega.gameserver.model.multisell.Ingredient;
import com.l2jmega.gameserver.model.multisell.ListContainer;

public class MultiSellList extends L2GameServerPacket
{
	private final ListContainer _list;
	
	private int _index;
	private int _size;
	
	private boolean _finished;
	
	public MultiSellList(ListContainer list, int index)
	{
		_list = list;
		_index = index;
		
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
			_finished = true;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xd0);
		writeD(_list.getId()); // list id
		writeD(1 + (_index / PAGE_SIZE)); // page
		writeD(_finished ? 1 : 0); // finished
		writeD(PAGE_SIZE); // size of pages
		writeD(_size); // list lenght
		
		while (_size-- > 0)
		{
			Entry ent = _list.getEntries().get(_index++);
			
			writeD(ent.getId());
			writeD(0x00); // C6
			writeD(0x00); // C6
			writeC(ent.isStackable() ? 1 : 0);
			writeH(ent.getProducts().size());
			writeH(ent.getIngredients().size());
			
			for (Ingredient i : ent.getProducts())
			{
				writeH(i.getItemId());
				writeD(ItemTable.getInstance().getTemplate(i.getItemId()).getBodyPart());
				writeH(ItemTable.getInstance().getTemplate(i.getItemId()).getType2());
				writeD(i.getItemCount());
				writeH(i.getEnchantLevel()); // enchtant lvl
				writeD(0x00); // C6
				writeD(0x00); // C6
			}
			
			for (Ingredient i : ent.getIngredients())
			{
				final int items = i.getItemId();
				int typeE = 65335;
				if (items != 65336 && items != 65436)
				{
					typeE = ItemTable.getInstance().getTemplate(i.getItemId()).getType2();
				}
				writeH(items); // ID
				writeH(typeE);
				writeD(i.getItemCount()); // Count
				writeH(i.getEnchantLevel()); // Enchant Level
				writeD(0x00); // C6
				writeD(0x00); // C6
			}
		}
	}
}