package gate.lang.property;

/**
 * Signals a error related to a property.
 *
 * @author davins
 */
public class PropertyError extends RuntimeException
{

	PropertyError(String message)
	{
		super(message);
	}

	PropertyError(String message, Object... args)
	{
		super(String.format(message, args));
	}
}
