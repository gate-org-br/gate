package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.Year;
import java.time.format.DateTimeFormatter;

public class YearRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof Year year
				? DateTimeFormatter.ofPattern(format).format(year)
				: "";
	}
}
