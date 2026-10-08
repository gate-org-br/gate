package gate.adapter.renderer;

import gate.adapter.converter.Converter;
import gate.type.LocalDateTimeInterval;

public class LocalDateTimeIntervalRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalDateTimeInterval interval
				? LocalDateTimeInterval.formatter(format).format(interval)
				: "";
	}
}
