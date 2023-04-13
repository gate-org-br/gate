package gate.error;

import gate.annotation.Catcher;
import gate.catcher.NotFoundExceptionCatcher;

/**
 * Signals that a requested resource could not be found.
 */
@Catcher(NotFoundExceptionCatcher.class)
public class NotFoundException extends AppException
{

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

}
