package gate.adapter.jsonConverter;

import gate.error.ConversionException;
import gate.lang.json.JsonElement;
import gate.lang.json.JsonString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class DefaultJsonConverter implements JsonConverter
{
	private final Method valueOf;

	public DefaultJsonConverter(Method valueOf)
	{
		this.valueOf = valueOf;
	}

	@Override public Object ofJson(Type genericType, JsonElement element)
	{
		if (element == null)
			return null;

		try
		{
			return valueOf.invoke(null, element instanceof JsonString string ? string.unwrap() : element.toString());
		} catch (IllegalAccessException ex)
		{
			throw new ConversionException(ex, ex.getMessage());
		} catch (InvocationTargetException ex)
		{
			Throwable cause = ex.getCause();
			if (cause instanceof ConversionException conversionException)
				throw conversionException;
			throw new ConversionException(cause, cause.getMessage());
		}
	}

	@Override public JsonElement toJson(Class<?> type, Object object) {return object != null ? JsonElement.wrap(object.toString()) : null;}
}
