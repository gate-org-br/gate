package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalDateTime dateTime
				? DateTimeFormatter.ofPattern(format).format(dateTime)
				: "";
	}
}
