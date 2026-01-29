package gate.error;

import gate.annotation.Catcher;
import gate.catcher.UnauthorizedExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that the user could not be authenticated.
 */
@Catcher(UnauthorizedExceptionCatcher.class)
public class AuthenticationException extends HttpException
{

	private static final long serialVersionUID = 1L;

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_UNAUTHORIZED;
	}

	/**
	 * Constructs an AuthenticationException.
	 */
	public AuthenticationException()
	{
		super("Authentication could not be completed");
	}

	/**
	 * Constructs an AuthenticationException.
	 */
	public AuthenticationException(Throwable cause)
	{
		super("Authentication could not be completed", cause);
	}
	
	/**
	 * Constructs an AuthenticationException with the specified message.
	 *
	 * @param message the error message
	 */
	public AuthenticationException(String message)
	{
		super(message);
	}
}
