package gate.adapter.renderer;

import gate.lang.property.Property;

public class PropertyRenderer implements Renderer
{
	@Override
	public String render(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		String name = ((Property) object).getMetadata().name();
		if (name == null)
			name = object.toString();
		return name;
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		if (object == null)
			return "";
		String name = ((Property) object).getMetadata().name();
		if (name == null)
			name = object.toString();
		return String.format(format, name);
	}
}
