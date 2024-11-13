package gate.rest;

import gate.lang.contentDisposition.ContentDisposition;
import gate.lang.contentType.ContentType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
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
			try
			{
				return ContentDisposition.parse(value);
			} catch (ParseException ex)
			{
				throw new IllegalArgumentException(value + " is not a valid content type header");
			}
		}

		@Override
		public String toString(ContentDisposition value)
		{
			return value.toString();
		}
	}
}
