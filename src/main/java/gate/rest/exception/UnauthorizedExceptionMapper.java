package gate.rest.exception;

import gate.error.UnauthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException>
{

	@Override
	public Response toResponse(UnauthorizedException ex)
	{
		return Response.status(Response.Status.UNAUTHORIZED)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
