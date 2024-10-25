package gate.rest;

import gate.converter.Converter;
import gate.converter.ObjectConverter;
import gate.error.ConversionException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ParameterConverterProvider implements ParamConverterProvider
{

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType,
		Type genericType, Annotation[] annotations)
	{
		if (rawType.isPrimitive()
			|| Converter.getConverter(rawType) instanceof ObjectConverter)
			return null;
		return new ParameterConverter<>(rawType);
	}

	private static class ParameterConverter<T> implements ParamConverter<T>
	{

		private final Class<T> type;

		private ParameterConverter(Class<T> type)
		{
			this.type = type;
		}

		@Override
		public T fromString(String value)
		{
			try
			{
				return Converter.fromString(type, value);
			} catch (ConversionException ex)
			{
				throw new IllegalArgumentException(ex.getMessage(), ex);
			}
		}

		@Override
		public String toString(T value)
		{
			return Converter.toString(value);
		}
	}
}
