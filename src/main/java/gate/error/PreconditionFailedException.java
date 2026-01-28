package gate.error;

import gate.annotation.Catcher;
import gate.catcher.HttpExceptionCatcher;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that a request precondition was not fulfilled.
 */
@Catcher(HttpExceptionCatcher.class)
public class PreconditionFailedException extends HttpException
{

	private static final long serialVersionUID = 1L;

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
