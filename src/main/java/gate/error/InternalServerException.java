package gate.error;

import gate.annotation.Catcher;
import gate.catcher.InternalServerExceptionCatcher;

/**
 * Signals that a requested resource could not be found.
 */
@Catcher(InternalServerExceptionCatcher.class)
public class InternalServerException extends AppException
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

}
