package gate.adapter.renderer;

import gate.adapter.converter.Converter;
import gate.type.LocalDateInterval;

public class LocalDateIntervalRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalDateInterval interval
				? LocalDateInterval.formatter(format).format(interval)
				: "";
	}
}
