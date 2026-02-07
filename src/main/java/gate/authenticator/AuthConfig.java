package gate.authenticator;

import gate.security.CryptoKeys;
import gate.util.SystemProperty;

import java.util.Optional;

public record AuthConfig(CryptoKeys keys, String context, String name)
	{

	public Optional<String> getProperty(String key)
	{
		if ("default".equals(name))
			return SystemProperty.get(context + ".auth.default." + key)
				.or(() -> SystemProperty.get(context + ".auth." + key))
				.or(() -> SystemProperty.get("gate.auth.default." + key))
				.or(() -> SystemProperty.get("gate.auth." + key));

		return SystemProperty.get(context + ".auth." + name + "." + key)
			.or(() -> SystemProperty.get("gate.auth." + name + "." + key));
	}
}