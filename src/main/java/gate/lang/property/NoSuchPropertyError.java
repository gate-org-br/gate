package gate.lang.property;

/**
 * Signals that the class doesn't have a property of a specified name.
 *
 * @author davins
 */
public class NoSuchPropertyError extends PropertyError
{

	NoSuchPropertyError(Class<?> type, String property)
	{
		super(String.format("No property named %s found on %s", property, type.getName()));
	}
}
