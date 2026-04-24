package gate.error;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that a request precondition was not fulfilled.
 */
public class PreconditionFailedException extends HttpException
{

	/**
	 * Constructs an PreconditionFailedException with the specified message.
	 *
	 * @param message the exception message
	 */
	public PreconditionFailedException(String message)
	{
		super(message);
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_PRECONDITION_FAILED;
	}
}