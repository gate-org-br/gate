package gate.error;

import gate.annotation.Catcher;
import gate.catcher.UnauthorizedExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that the client must authenticate itself to access the requested resource.
 */
@Catcher(UnauthorizedExceptionCatcher.class)
public class UnauthorizedException extends HttpException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an UnauthorizedException.
	 */
	public UnauthorizedException()
	{
		super("Tentativa de acessar recurso protegido sem se autenticar");
	}

	/**
	 * Constructs an UnauthorizedException.
	 *
	 * @param message the exception message
	 */
	public UnauthorizedException(String message)
	{
		super(message);
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_UNAUTHORIZED;
	}
}
