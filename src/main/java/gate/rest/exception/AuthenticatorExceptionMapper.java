package gate.rest.exception;

import gate.error.AuthenticatorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticatorExceptionMapper implements ExceptionMapper<AuthenticatorException>
{

	@Override
	public Response toResponse(AuthenticatorException ex)
	{
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("Error when trying to authenticate")
				.type(MediaType.TEXT_PLAIN)
				.build();
	}
}
