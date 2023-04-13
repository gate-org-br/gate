package gate.rest.exception;

import gate.error.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbidenExceptionMapper implements ExceptionMapper<ForbiddenException>
{

	@Override
	public Response toResponse(ForbiddenException ex)
	{
		return Response.status(Response.Status.FORBIDDEN)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
