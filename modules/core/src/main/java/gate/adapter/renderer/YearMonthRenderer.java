package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof YearMonth yearMonth
				? DateTimeFormatter.ofPattern(format).format(yearMonth)
				: "";
	}
}
