package gate.error;

/**
 * Signals that the username informed by the current user is invalid.
 */
public class InvalidUsernameException extends AuthenticationException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidUsernameException.
	 */
	public InvalidUsernameException()
	{
		super("Usuário inválido");
	}
}
