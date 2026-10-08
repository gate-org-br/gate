package gate.adapter.converter;

import gate.error.ConversionException;
import gate.lang.json.JsonElement;

import java.lang.reflect.Type;

public class JsonElementConverter implements Converter
{
	@Override
	public Object ofString(Type type, String string) throws ConversionException
	{
		if (string != null)
		{
			string = string.trim();
			if (!string.isEmpty())
				return JsonElement.parse(string);
		}
		return null;
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		if (object != null)
			return JsonElement.stringify((JsonElement) object);
		return null;
	}
}