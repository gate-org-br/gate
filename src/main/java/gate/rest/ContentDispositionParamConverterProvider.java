package gate.rest;

import gate.error.ConversionException;
import gate.lang.contentDisposition.ContentDisposition;
import gate.lang.contentType.ContentType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

public class ContentDispositionParamConverterProvider implements ParamConverterProvider
{

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType,
			Type genericType,
			Annotation[] annotations)
	{
		if (rawType.equals(ContentType.class))
			return (ParamConverter<T>) new ContentDispositionParamConverter();
		return null;
	}

	private static class ContentDispositionParamConverter
			implements ParamConverter<ContentDisposition>
	{

		@Override
		public ContentDisposition fromString(String value)
		{
			return ContentDisposition.valueOf(value);
		}

		@Override
		public String toString(ContentDisposition value)
		{
			return value.toString();
		}
	}
}
