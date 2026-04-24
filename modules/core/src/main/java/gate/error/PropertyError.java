package gate.error;

/**
 * Signals a error related to a property.
 *
 * @author davins
 */
public class PropertyError extends RuntimeException
{

	public PropertyError(String message)
	{
		super(message);
	}

	public PropertyError(String message, Object... args)
	{
		super(String.format(message, args));
	}
}
