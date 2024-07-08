package gate.error;

/**
 * Signals that the user could not be authenticated.
 */
public class AuthenticationException extends HttpException
{

	private static final long serialVersionUID = 1L;

	@Override
	public int getStatusCode()
	{
		return 401;
	}

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
