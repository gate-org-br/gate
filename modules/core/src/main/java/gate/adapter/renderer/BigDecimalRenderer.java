package gate.adapter.renderer;

import gate.i18n.CurrentLocale;

public class BigDecimalRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(CurrentLocale.get(), format, object) : "";
	}
}
