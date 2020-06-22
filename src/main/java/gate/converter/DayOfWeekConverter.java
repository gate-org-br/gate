package gate.converter;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class DayOfWeekConverter extends EnumConverter
{

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.FULL, Locale.getDefault()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.valueOf(format), Locale.getDefault()) : "";
	}

}
