package gate.error;

import gate.annotation.Catcher;
import gate.catcher.ForbiddenExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that the current user has no access to a resource.
 */
@Catcher(ForbiddenExceptionCatcher.class)
public class ForbiddenException extends HttpException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new ForbiddenException.
	 */
	public ForbiddenException()
	{
		super("Acesso negado");
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_FORBIDDEN;
	}
}
