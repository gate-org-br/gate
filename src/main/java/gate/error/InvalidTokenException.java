package gate.error;

/**
 * Signals that the user credentials are invalid.
 */
public class InvalidTokenException extends AuthenticationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidTokenException.
	 */
	public InvalidTokenException()
	{
		super("Credenciais inválidas");
	}
}
