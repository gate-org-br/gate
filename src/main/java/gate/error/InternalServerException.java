package gate.error;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Signals that a requested resource could not be found.
 */
public class InternalServerException extends HttpException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an NotFoundException.
	 */
	public InternalServerException()
	{
		super("Erro de sistema");
	}

	/**
	 * Constructs an NotFoundException with the specified message.
	 *
	 * @param message the exception message
	 */
	public InternalServerException(String message)
	{
		super(message);
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

}
