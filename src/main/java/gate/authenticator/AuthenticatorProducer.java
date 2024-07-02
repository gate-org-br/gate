package gate.authenticator;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.App;
import gate.error.AuthenticatorException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author davins
 *
 * Produces a valid authenticator
 */
public class AuthenticatorProducer implements Serializable
{

	@Inject
	@Current
	App app;

	@Inject
	GateControl control;

	@Inject
	HttpServletRequest request;

	private final Map<String, Authenticator> authenticators = new ConcurrentHashMap<>();

	@Current
	@Produces
	@RequestScoped
	public Authenticator get()
	{
		String authenticator
			= Objects.requireNonNullElse(request.getParameter("authenticator"), "default");

		return authenticators.computeIfAbsent(authenticator, index ->
		{
			String context = app.getId().toLowerCase();
			AuthConfig config
				= new AuthConfig(context, index);

			if (config.getProperty("type").isPresent())
				return switch (config.getProperty("type").get())
				{
					case "database" ->
						new DatabaseAuthenticator(control, config);
					case "ldap" ->
						new LDAPAuthenticator(control, config);
					case "oidc" ->
						new OIDCAuthenticator(control, config);
					default -> throw new AuthenticatorException("Invalid authenticator type");
				};

			if (!"default".equals(authenticator))
				throw new AuthenticatorException("Invalid authenticator");

			return new DatabaseAuthenticator(control, config);
		});
	}
}
