package gate.rest.exception;

import gate.error.AppException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException>
{

	@Override
	public Response toResponse(AppException ex)
	{
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
