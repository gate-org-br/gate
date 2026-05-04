package gate.adapter.renderer;

import gate.adapter.converter.Converter;
import gate.type.YearMonthInterval;

public class YearMonthIntervalRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof YearMonthInterval interval
				? YearMonthInterval.formatter(format).format(interval)
				: "";
	}
}
