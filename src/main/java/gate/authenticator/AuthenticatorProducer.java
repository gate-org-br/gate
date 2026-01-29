package gate.authenticator;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import gate.GateControl;
import gate.annotation.Current;
import gate.entity.App;
import gate.error.AuthenticatorException;
import gate.security.CryptoKeys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author davins
 *
 * Produces a valid authenticator
 */
@ApplicationScoped
public class AuthenticatorProducer implements Serializable
{

	@Inject
	@Current
	App app;

	@Inject
	CryptoKeys keys;

	@Inject
	GateControl control;

	@Inject
	HttpServletRequest request;

	private final Map<String, Map<String, Authenticator>> authenticators = new ConcurrentHashMap<>();

	@Current
	@Produces
	@RequestScoped
	@Named("authenticator")
	public Authenticator get()
	{
		String context = app.getId().toLowerCase();
		String index = Objects.requireNonNullElse(request.getParameter("authenticator"), "default");

		return authenticators
				.computeIfAbsent(context, k -> new ConcurrentHashMap<>())
				.computeIfAbsent(index, key ->
				{
					AuthConfig config = new AuthConfig(keys, context, key);

					if (config.getProperty("type").isPresent())
						return switch (config.getProperty("type").get())
						{
							case "database" ->
								new DatabaseAuthenticator(control, config);
							case "ldap" ->
								new LDAPAuthenticator(control, config);
							case "oidc" ->
								new OIDCAuthenticator(control, config);
							default ->
								throw new AuthenticatorException("Invalid authenticator type");
						};

					if (!"default".equals(key))
						throw new AuthenticatorException("Invalid authenticator");

					return new DatabaseAuthenticator(control, config);
				});
	}

}
