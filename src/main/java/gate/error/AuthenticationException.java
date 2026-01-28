package gate.error;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Exception thrown when an authentication attempt fails.
 *
 * <p>
 * This exception indicates that the client was not able to prove its identity,
 * either due to invalid credentials, expired authentication data, or failed
 * security validations (such as OAuth state, PKCE, nonce, or token checks).
 * </p>
 *
 * <p>
 * When thrown, it is automatically handled by {@link HttpExceptionCatcher},
 * resulting in an HTTP 401 (Unauthorized) response.
 * </p>
 */
@Catcher(HttpExceptionCatcher.class)
public class AuthenticationException extends HttpException
{

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Authentication could not be completed";
	
	/**
	 * Creates an AuthenticationException with a default message
	 */
	public AuthenticationException()
	{
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates an AuthenticationException with a default message and the
	 * underlying cause.
	 *
	 * @param cause the original exception that caused the authentication
	 * failure
	 */
	public AuthenticationException(Throwable cause)
	{
		super(DEFAULT_MESSAGE, cause);
	}

	/**
	 * Creates an AuthenticationException with a custom message.
	 *
	 * @param message the message describing the authentication error
	 */
	public AuthenticationException(String message)
	{
		super(message);
	}

	/**
	 * Creates an AuthenticationException with a custom message and an
	 * underlying cause.
	 *
	 * @param message the message describing the authentication error
	 * @param cause the original exception that caused the failure
	 */
	public AuthenticationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Returns the HTTP status code associated with this exception.
	 *
	 * @return 401 (Unauthorized)
	 */
	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_UNAUTHORIZED;
	}

}
