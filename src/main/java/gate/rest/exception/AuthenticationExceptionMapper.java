package gate.rest.exception;

import gate.error.AuthenticationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException>
{

	@Override
	public Response toResponse(AuthenticationException ex)
	{
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity(ex.getMessage())
				.type(MediaType.TEXT_PLAIN)
				.build();
	}
}
