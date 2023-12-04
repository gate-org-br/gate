package gate.util;

import java.util.Optional;
import org.eclipse.microprofile.config.ConfigProvider;

public class SystemProperty
{

	public static Optional<String> get(String property)
	{
		return Optional.ofNullable(System.getProperty(property, System.getenv(property)))
			.or(() -> ConfigProvider.getConfig().getOptionalValue(property, String.class));
	}
}
