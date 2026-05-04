package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalDate date
				? DateTimeFormatter.ofPattern(format).format(date)
				: "";
	}
}
