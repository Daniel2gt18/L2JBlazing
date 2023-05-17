package com.l2jmega.gameserver.network.clientpackets;

import com.l2jmega.gameserver.data.CharTemplateTable;
import com.l2jmega.gameserver.model.base.ClassId;
import com.l2jmega.gameserver.network.serverpackets.CharTemplates;

public final class NewCharacter extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		CharTemplates ct = new CharTemplates();
		
		ct.addChar(CharTemplateTable.getInstance().getTemplate(0));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.HUMAN_FIGHTER));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.HUMAN_MYSTIC));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ELVEN_FIGHTER));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ELVEN_MYSTIC));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DARK_FIGHTER));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DARK_MYSTIC));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ORC_FIGHTER));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.ORC_MYSTIC));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.DWARVEN_FIGHTER));
		
		sendPacket(ct);
	}
}