package gate.util;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Optional;

public class SystemProperty
{

	public static Optional<String> get(String property)
	{
		return Optional.ofNullable(System.getProperty(property, System.getenv(property)))
			.or(() ->
			{
				try
				{
					return ConfigProvider.getConfig().getOptionalValue(property, String.class);
				} catch (IllegalStateException ex)
				{
					return Optional.empty();
				}
			});
	}
}
