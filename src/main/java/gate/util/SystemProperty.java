package gate.util;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Optional;

public class SystemProperty
{
	public static Optional<String> get(String property)
	{
		String value = System.getProperty(property);
		if (value != null)
			return Optional.of(value);

		value = System.getenv(property);
		if (value != null)
			return Optional.of(value);

		return ConfigProvider.getConfig()
				.getOptionalValue(property, String.class);
	}
}