package gate.error;

/**
 * Signals that the user could not be authenticated.
 */
public class AuthenticationException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an AuthenticationException.
	 */
	public AuthenticationException()
	{
		super("Usuario/Senha inválidos");
	}

	/**
	 * Constructs an AuthenticationException with the specified message.
	 *
	 * @param message the error message
	 */
	public AuthenticationException(String message)
	{
		super(message);
	}
}
