package gate.rest.exception;

import gate.error.InvalidCredentialsException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidCredentialsExceptionMapper implements ExceptionMapper<InvalidCredentialsException>
{

	@Override
	public Response toResponse(InvalidCredentialsException ex)
	{
		return Response.status(Response.Status.UNAUTHORIZED)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
