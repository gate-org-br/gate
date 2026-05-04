package gate.adapter.renderer;

import gate.i18n.CurrentLocale;

import java.time.Month;
import java.time.format.TextStyle;

public class MonthRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.FULL, CurrentLocale.get()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? ((Month) object).getDisplayName(TextStyle.valueOf(format), CurrentLocale.get()) : "";
	}
}
