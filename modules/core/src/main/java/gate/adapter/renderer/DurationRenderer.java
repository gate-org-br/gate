package gate.adapter.renderer;

import gate.adapter.converter.Converter;

import java.time.Duration;

public class DurationRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (!(object instanceof Duration duration))
			return "";
		return Converter.toString(duration);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		if (!(object instanceof Duration duration))
			return "";

		return switch (format.toLowerCase())
		{
			case "hh" -> String.format("%02d", duration.toDaysPart() * 24 + duration.toHoursPart());
			case "hh:mm" -> String.format("%02d:%02d", duration.toDaysPart() * 24 + duration.toHoursPart(), duration.toMinutesPart());
			case "hh:mm:ss" ->
					String.format("%02d:%02d:%02d", duration.toDaysPart() * 24 + duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
			case "dd:hh:mm:ss" ->
					String.format("%02d:%02d:%02d:%02d", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
			default -> String.format(format, Converter.toString(duration));
		};
	}
}