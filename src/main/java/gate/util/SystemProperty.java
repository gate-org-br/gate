package gate.util;

import java.util.Optional;

public class SystemProperty
{

	public static Optional<String> get(String property)
	{
		return Optional.ofNullable(System.getProperty(property, System.getenv(property)));
	}
}
