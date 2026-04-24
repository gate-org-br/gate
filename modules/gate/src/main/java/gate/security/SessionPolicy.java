package gate.security;

import gate.converter.Converter;
import gate.util.SystemProperty;
import java.time.Duration;

public final class SessionPolicy
{

	public static final SessionPolicy CURRENT = new SessionPolicy();

	private final boolean statelessToken;
	private final boolean revocable;
	private final Duration idleTimeout;
	private final Duration timeout;

	private SessionPolicy()
	{
		statelessToken = SystemProperty.get("gate.auth.session.stateless-token")
				.map(e -> Converter.fromString(Boolean.class, e))
				.orElse(false);

		revocable = SystemProperty.get("gate.auth.session.revocable")
				.map(e -> Converter.fromString(Boolean.class, e))
				.orElse(false);

		idleTimeout = SystemProperty.get("gate.auth.session.idle_timeout")
				.map(e -> Converter.getConverter(Duration.class).ofString(Duration.class, e))
				.map(Duration.class::cast)
				.orElse(Duration.ofHours(1));

		timeout = SystemProperty.get("gate.auth.session.timeout")
				.map(e -> Converter.getConverter(Duration.class).ofString(Duration.class, e))
				.map(Duration.class::cast)
				.orElse(Duration.ofHours(24));
	}

	public boolean statelessToken()
	{
		return statelessToken;
	}

	public boolean revocable()
	{
		return revocable;
	}

	public Duration idleTimeout()
	{
		return idleTimeout;
	}

	public Duration timeout()
	{
		return timeout;
	}
}
