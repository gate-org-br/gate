package gate.converter.custom;

import gate.converter.ObjectConverter;
import gate.lang.property.Property;

public class PropertyConverter extends ObjectConverter
{

	@Override
	public String toText(Class<?> type, Object object)
	{
		if (object == null)
			return "";
		String name = ((Property) object).getDisplayName();
		if (name == null)
			name = object.toString();
		return name;
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		if (object == null)
			return "";
		String name = ((Property) object).getDisplayName();
		if (name == null)
			name = object.toString();
		return String.format(format, name);
	}
}
