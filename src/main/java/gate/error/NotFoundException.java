package gate.error;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

/**
 * Signals that a requested resource could not be found.
 */
public class NotFoundException extends HttpException
{

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an NotFoundException.
	 */
	public NotFoundException()
	{
		super("Tentativa de acessar registro inexistente");
	}

	/**
	 * Constructs an NotFoundException with the specified message.
	 *
	 * @param message the exception message
	 */
	public NotFoundException(String message)
	{
		super(message);
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_NOT_FOUND;
	}
}