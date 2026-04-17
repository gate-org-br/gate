package gate.error;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

/**
 * Represents an unexpected server-side failure while processing a request.
 * <p>
 * This exception maps to HTTP status code {@code 500 (Internal Server Error)}
 * and should be used when the request is valid, but the server cannot complete
 * it because of an internal error.
 */
public class InternalServerException extends HttpException
{

	private static final String MESSAGE = "Erro de sistema";
	@Serial private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception with the default internal error message.
	 */
	public InternalServerException()
	{
		super(MESSAGE);
	}

	/**
	 * Creates an exception with a custom message and root cause.
	 *
	 * @param message a description of the internal error
	 * @param cause   the original exception that caused this failure
	 */
	public InternalServerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Creates an exception with the default internal error message and root cause.
	 *
	 * @param cause the original exception that caused this failure
	 */
	public InternalServerException(Throwable cause)
	{
		super(MESSAGE, cause);
	}

	/**
	 * Creates an exception with a custom message.
	 *
	 * @param message a description of the internal error
	 */
	public InternalServerException(String message)
	{
		super(message);
	}

	/**
	 * Returns the HTTP status code associated with this exception.
	 *
	 * @return {@code 500 (Internal Server Error)}
	 */
	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

}