package gate.adapter.renderer;

import gate.annotation.Name;

public class EnumRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? Name.Extractor.extract(object).orElse(object.toString()) : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, render(type, object)) : "";
	}
}
