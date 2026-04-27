package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class DefaultConverter implements Converter
{
	private final Method factoryMethod;

	public DefaultConverter(Method factoryMethod)
	{
		this.factoryMethod = factoryMethod;
	}

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null)
			return null;
		string = string.trim();
		if (string.isEmpty())
			return null;

		try
		{
			return factoryMethod.invoke(null, string);
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

	@Override
	public String toString(Class<?> type, Object object)
	{
		return object != null ? object.toString() : "";
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString().trim() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, render(type, object));
	}

	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType)
			throws ConversionException
	{
		try
		{
			Object value = switch (scanner.getCurrent().getType())
			{
				case NULL -> null;
				case STRING -> factoryMethod.invoke(null, scanner.getCurrent().toString());
				default -> throw new ConversionException("Expected json string and found "
				                                         + scanner.getCurrent().getType());
			};
			scanner.scan();
			return value;
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object != null)
			writer.write(JsonToken.Type.STRING, object.toString());
		else
			writer.write(JsonToken.Type.NULL, null);
	}

	@Override
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		if (object != null)
			writer.write(JsonToken.Type.STRING, object.toString());
		else
			writer.write(JsonToken.Type.NULL, null);
	}
}