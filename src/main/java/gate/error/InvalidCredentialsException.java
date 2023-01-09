package gate.error;

/**
 * Signals that the password informed by the current user is invalid.
 */
public class InvalidCredentialsException extends AuthenticationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidCredentialsException.
	 */
	public InvalidCredentialsException()
	{
		super("Credenciais inválidas");
	}

	/**
	 * Constructs an InvalidCredentialsException.
	 *
	 * @param message the message of the exception created
	 */
	public InvalidCredentialsException(String message)
	{
		super(message);
	}
}
