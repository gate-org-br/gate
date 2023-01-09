package gate.rest.exception;

import gate.error.ConversionException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConversionExceptionMapper implements ExceptionMapper<ConversionException>
{

	@Override
	public Response toResponse(ConversionException ex)
	{
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
