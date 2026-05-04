package gate.adapter.renderer;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class EnumSetRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? ((EnumSet<?>) object).stream()
				.map(Renderer::render)
				.collect(Collectors.joining(", ")) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, render(type, object)) : "";
	}
}
