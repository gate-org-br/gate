package gate.adapter.renderer;

import gate.adapter.converter.Converter;
import gate.type.LocalTimeInterval;

public class LocalTimeIntervalRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalTimeInterval interval
				? LocalTimeInterval.formatter(format).format(interval)
				: "";
	}
}
