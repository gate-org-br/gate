package gate.authenticator;

import gate.util.SystemProperty;
import java.util.Optional;

public class Config
{

	private final String context;

	public Config(String context)
	{
		this.context = context;
	}

	public Optional<String> getProperty(String key)
	{
		return SystemProperty.get(context + ".auth." + key)
			.or(() -> SystemProperty.get("gate.auth." + key));
	}

}
