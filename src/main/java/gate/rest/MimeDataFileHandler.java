package gate.rest;

import gate.type.mime.MimeDataFile;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

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
