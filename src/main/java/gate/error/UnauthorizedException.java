package gate.error;

/**
 * Signals that the client must authenticate itself to access the requested resource.
 */
public class UnauthorizedException extends AppException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an UnauthorizedException.
	 */
	public UnauthorizedException()
	{
		super("Tentativa de acessar recurso protegido sem se autenticar");
	}

	/**
	 * Constructs an UnauthorizedException.
	 *
	 * @param message the exception message
	 */
	public UnauthorizedException(String message)
	{
		super(message);
	}
}
