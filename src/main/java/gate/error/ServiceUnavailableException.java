package gate.error;

import gate.annotation.Catcher;
import gate.catcher.InternalServerExceptionCatcher;

/**
 * Signals that a requested resource could not be found.
 */
@Catcher(InternalServerExceptionCatcher.class)
public class ServiceUnavailableException extends InternalServerException
{

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

}
