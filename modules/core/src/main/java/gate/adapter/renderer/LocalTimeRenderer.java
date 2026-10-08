package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Converter.toString(object) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object instanceof LocalTime time
				? DateTimeFormatter.ofPattern(format).format(time)
				: "";
	}
}
