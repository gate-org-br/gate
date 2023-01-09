package gate.rest.exception;

import gate.error.ServiceUnavailableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceUnavailableExceptionMapper implements ExceptionMapper<ServiceUnavailableException>
{

	@Override
	public Response toResponse(ServiceUnavailableException ex)
	{
		return Response.status(Response.Status.SERVICE_UNAVAILABLE)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
