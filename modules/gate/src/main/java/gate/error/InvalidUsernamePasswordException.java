package gate.error;

import java.io.Serial;

/**
 * Signals that the username informed by the current user is invalid.
 */
public class InvalidUsernamePasswordException extends AuthenticationException
{

	@Serial private static final long serialVersionUID = 1L;

	/**
	 * Constructs an InvalidUsernameException.
	 */
	public InvalidUsernamePasswordException()
	{
		super("Usuário ou senha inválidos");
	}
}