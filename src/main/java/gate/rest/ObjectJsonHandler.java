package gate.rest;

import gate.converter.Converter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ObjectJsonHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object>
{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
	{
		return true;
	}

	@Override
	public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		entityStream.write(Converter.toJson(value).getBytes());
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream in)
		throws IOException, WebApplicationException
	{
		if (genericType instanceof ParameterizedType parameterizedType)
		{
			if (parameterizedType.getActualTypeArguments().length > 0)
				genericType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
		}
		String string = new String(in.readAllBytes());
		return Converter.fromJson(type, genericType, string);
	}
}
