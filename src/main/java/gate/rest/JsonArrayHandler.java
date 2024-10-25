package gate.rest;

import gate.lang.json.JsonArray;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JsonArrayHandler implements MessageBodyWriter<JsonArray>, MessageBodyReader<JsonArray>
{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
	{
		return type == JsonArray.class;
	}

	@Override
	public void writeTo(JsonArray value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		var string = JsonArray.format(value);
		entityStream.write(string.getBytes());
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return type == JsonArray.class;
	}

	@Override
	public JsonArray readFrom(Class<JsonArray> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream in)
			throws IOException, WebApplicationException
	{
		try (DataInputStream reader = new DataInputStream(in))
		{
			String string = new String(reader.readAllBytes(), "UTF-8");
			return JsonArray.parse(string);
		}
	}
}
