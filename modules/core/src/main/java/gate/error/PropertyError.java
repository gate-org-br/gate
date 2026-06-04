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

	public PropertyError(Class<?> type, String property, String detail)
	{
		super(String.format("Invalid property '%s' on %s: %s.", property, type.getName(), detail));
	}

	public PropertyError(Class<?> type, String property, String expectation, Object token)
	{
		super(String.format("Invalid property '%s' on %s: %s. Found token: %s.",
				property, type.getName(), expectation,
				token != null ? String.format("'%s'", token) : "<end of property>"));
	}
}
