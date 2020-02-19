package gate.error;

/**
 * Signals that the password informed by the current user is invalid.
 */
public class InvalidCredentialsException extends AuthenticationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidPasswordException.
	 */
	public InvalidCredentialsException()
	{
		super("Credenciais inválidas");
	}
}
