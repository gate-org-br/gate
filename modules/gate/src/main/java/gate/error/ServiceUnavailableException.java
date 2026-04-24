package gate.error;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

/**
 * Signals that a requested resource could not be found.
 */
public class ServiceUnavailableException extends InternalServerException
{

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an NotFoundException.
	 */
	public ServiceUnavailableException()
	{
		super("Tentativa de acessar serviço indisponível");
	}

	/**
	 * Constructs an NotFoundException with the specified message.
	 *
	 * @param message the exception message
	 */
	public ServiceUnavailableException(String message)
	{
		super(message);
	}

	@Override
	public int getStatusCode()
	{
		return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
	}

}