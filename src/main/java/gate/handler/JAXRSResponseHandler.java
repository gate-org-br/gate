package gate.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;

@ApplicationScoped
public class JAXRSResponseHandler implements Handler
{

	@Inject
	private Providers providers;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		Response res = (Response) value;

		response.setStatus(res.getStatus());
		res.getHeaders().entrySet()
			.forEach(h -> h.getValue().stream().filter(v -> v instanceof String)
			.map(v -> (String) v).forEach(v -> response.addHeader(h.getKey(), v)));

		Object entity = res.getEntity();

		if (entity != null)
		{
			try
			{
				String mediaType = res.getHeaderString(HttpHeaders.CONTENT_TYPE);
				if (mediaType == null)
					mediaType = "application/json";
				MediaType responseMediaType = MediaType.valueOf(mediaType);

				var type = (Class<Object>) entity.getClass();

				MessageBodyWriter<Object> writer = providers.getMessageBodyWriter(type, null, new Annotation[]
				{
				}, responseMediaType);

				if (writer == null)
					throw new IOException("No message body writer found for the media type: " + mediaType);

				OutputStream outputStream = response.getOutputStream();
				writer.writeTo(entity, entity.getClass(), null, new Annotation[]
				{
				}, responseMediaType, new MultivaluedHashMap<>(), outputStream);
				outputStream.flush();
			} catch (IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
	}
}
