package gate.error;

/**
 * Signals an error on the authentication service.
 */
public class AuthenticatorException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an AuthenticatorException with the specified cause.
	 *
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 * nonexistent or unknown.
	 */
	public AuthenticatorException(Throwable cause)
	{
		super("Erro no serviço de autenticação", cause);
	}

	/**
	 * Constructs an AuthenticatorException with the specified message.
	 *
	 * @param message The exception message.
	 */
	public AuthenticatorException(String message)
	{
		super(message);
	}

	/**
	 * Constructs an AuthenticatorException with the specified message and cause.
	 *
	 * @param message The exception message.
	 * @param cause The cause, which is saved for later retrieval by the getCause() method. A null value is permitted, and indicates that the cause is
	 * nonexistent or unknown.
	 */
	public AuthenticatorException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
