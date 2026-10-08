package gate.error;

/**
 * Signals that the class doesn't have a property of a specified name.
 *
 * @author davins
 */
public class NoSuchPropertyError extends PropertyError
{

	private final Class<?> type;
	private final String property;

	public NoSuchPropertyError(Class<?> type, String property)
	{
		super(String.format("No property named %s found on %s", property, type.getName()));
		this.type = type;
		this.property = property;
	}

	public Class<?> getType()
	{
		return type;
	}

	public String getProperty()
	{
		return property;
	}
}
