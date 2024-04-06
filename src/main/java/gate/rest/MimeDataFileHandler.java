package gate.rest;

import gate.type.mime.MimeDataFile;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class MimeDataFileHandler implements MessageBodyWriter<MimeDataFile>
{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] antns, MediaType mt)
	{
		return MimeDataFile.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(MimeDataFile value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		httpHeaders.putSingle("Content-Type",
			value.getType() + "/" + value.getSubType());
		entityStream.write(value.getData());
	}

}
