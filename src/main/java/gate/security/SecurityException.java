package gate.security;

public class SecurityException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public SecurityException(String message)
	{
		super(message);
	}

	public SecurityException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
