package gate.rest;

import gate.lang.contentType.ContentType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class ContentTypeParamConverterProvider implements ParamConverterProvider
{

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType,
			Type genericType,
			Annotation[] annotations)
	{
		if (rawType.equals(ContentType.class))
			return (ParamConverter<T>) new ContentTypeParamConverter();
		return null;
	}

	private static class ContentTypeParamConverter
			implements ParamConverter<ContentType>
	{

		@Override
		public ContentType fromString(String value)
		{
			return ContentType.parse(value);
		}

		@Override
		public String toString(ContentType value)
		{
			return value.toString();
		}
	}
}
