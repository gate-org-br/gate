package gate.rest;

import gate.converter.Converter;
import gate.converter.custom.EntityConverter;
import gate.lang.property.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class ObjectTextHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object>
{

	private static final EntityConverter ENTITY_CONVERTER = new EntityConverter();

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
	{
		return true;
	}

	@Override
	public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		if (Entity.isEntity(type))
			entityStream.write(ENTITY_CONVERTER.toString(type, value).getBytes());
		entityStream.write(Converter.toString(value).getBytes());
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
		String string = new String(in.readAllBytes());
		if (Entity.isEntity(type))
			return ENTITY_CONVERTER.ofString(type, string);
		return Converter.fromString(type, string);
	}
}
