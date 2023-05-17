package com.l2jmega.commons.logging.formatter;

import java.util.logging.LogRecord;

import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.logging.MasterFormatter;

import com.l2jmega.gameserver.model.item.instance.ItemInstance;

public class ItemLogFormatter extends MasterFormatter
{
	@Override
	public String format(LogRecord record)
	{
		final StringBuilder sb = new StringBuilder();
		
		StringUtil.append(sb, "[", getFormatedDate(record.getMillis()), "] ", SPACE, record.getMessage(), SPACE);
		
		for (Object p : record.getParameters())
		{
			if (p == null)
				continue;
			
			if (p instanceof ItemInstance)
			{
				final ItemInstance item = (ItemInstance) p;
				
				StringUtil.append(sb, item.getCount(), SPACE);
				
				if (item.getEnchantLevel() > 0)
					StringUtil.append(sb, "+", item.getEnchantLevel(), " ");
				
				StringUtil.append(sb, item.getItem().getName(), SPACE, item.getObjectId());
			}
			else
				sb.append(p.toString());
			
			sb.append(SPACE);
		}
		sb.append(CRLF);
		
		return sb.toString();
	}
}