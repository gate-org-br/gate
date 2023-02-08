package gate.rest;

import gate.lang.json.JsonElement;
import java.io.DataInputStream;
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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JsonElementHandler implements MessageBodyWriter<JsonElement>, MessageBodyReader<JsonElement>
{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
	{
		return JsonElement.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(JsonElement value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		entityStream.write(value.toString().getBytes());
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return JsonElement.class.isAssignableFrom(type);
	}

	@Override
	public JsonElement readFrom(Class<JsonElement> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream in)
		throws IOException, WebApplicationException
	{
		try (DataInputStream reader = new DataInputStream(in))
		{
			return JsonElement.parse(reader.readUTF());
		}
	}
}
