package gate.rest;

import gate.converter.Converter;
import gate.converter.ObjectConverter;
import gate.error.ConversionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class ParameterConverterProvider implements ParamConverterProvider
{

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType,
		Type genericType, Annotation[] annotations)
	{
		if (Converter.getConverter(rawType) instanceof ObjectConverter)
			return null;
		return new ParameterConverter<>(rawType);
	}

	private static class ParameterConverter<T> implements ParamConverter<T>
	{

		private Class<T> type;

		private ParameterConverter(Class<T> type)
		{
			this.type = type;
		}

		@Override
		public T fromString(String value)
		{
			try
			{
				return (T) Converter.fromString(type, value);
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
