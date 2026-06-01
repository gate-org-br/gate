package gate.adapter.renderer;

import gate.adapter.converter.Converter;

public class ObjectRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		return Converter.toString(object);
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format,
				render(type, object));
	}
}