package gate.authenticator;

import gate.util.SystemProperty;
import java.util.Optional;

public class AuthConfig
{

	private final String context;
	private final String name;

	public AuthConfig(String context, String name)
	{
		this.context = context;
		this.name = name;
	}

	public Optional<String> getProperty(String key)
	{
		if ("default".equals(name))
			return SystemProperty.get(context + ".auth.default." + key)
				.or(() -> SystemProperty.get(context + ".auth." + key))
				.or(() -> SystemProperty.get("gate.auth.default." + key))
				.or(() -> SystemProperty.get("gate.auth." + key));

		return SystemProperty.get(context + ".auth." + name + "." + key)
			.or(() -> SystemProperty.get(context + ".auth." + key))
			.or(() -> SystemProperty.get("gate.auth." + name + "." + key))
			.or(() -> SystemProperty.get("gate.auth." + key));
	}

	public String context()
	{
		return context;
	}

	public String name()
	{
		return name;
	}
}
