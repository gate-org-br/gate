package gate.converter.custom;

import gate.converter.ObjectConverter;
import gate.lang.property.Property;

public class PropertyConverter extends ObjectConverter
{

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? ((Property) object).getName().orElse(object.toString()) : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return object != null ? String.format(format, ((Property) object).getName().orElse(object.toString())) : "";
	}
}
