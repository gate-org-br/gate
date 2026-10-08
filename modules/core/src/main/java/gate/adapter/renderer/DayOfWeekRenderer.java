package gate.adapter.renderer;

import gate.i18n.CurrentLocale;

import java.time.DayOfWeek;
import java.time.format.TextStyle;

public class DayOfWeekRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((DayOfWeek) object)
								.getDisplayName(TextStyle.FULL, CurrentLocale.get()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? ((DayOfWeek) object).getDisplayName(TextStyle.valueOf(format), CurrentLocale.get()) : "";
	}
}
