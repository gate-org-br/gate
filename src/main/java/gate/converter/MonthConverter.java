package gate.converter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthConverter extends EnumConverter
{
	
	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}
	
	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}
	
}
