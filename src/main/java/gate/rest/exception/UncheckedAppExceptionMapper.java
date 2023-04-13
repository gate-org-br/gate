package gate.rest.exception;

import gate.error.UncheckedAppException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
