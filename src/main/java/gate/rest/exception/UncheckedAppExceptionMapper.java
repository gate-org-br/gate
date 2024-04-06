package gate.rest.exception;

import gate.error.UncheckedAppException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UncheckedAppExceptionMapper implements ExceptionMapper<UncheckedAppException>
{

	@Override
	public Response toResponse(UncheckedAppException ex)
	{
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(ex.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
