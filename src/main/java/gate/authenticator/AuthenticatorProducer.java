package gate.authenticator;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.Config;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.LDAPWithDatabaseFallbackAuthenticator;
import gate.authenticator.OIDCAuthenticator;
import gate.entity.App;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.io.Serializable;

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

	@Current
	@Produces
	@ApplicationScoped
	public Authenticator get()
	{
		Config config = new Config(app.getId().toLowerCase());
		return switch (config.getProperty("type").orElse("default"))
		{
			case "db" ->
				new DatabaseAuthenticator(config);

			case "ldap" ->
				new LDAPAuthenticator(config);

			case "oidc" ->
				new OIDCAuthenticator(config);

			case "ldap-with-database-fallback" ->
				new LDAPWithDatabaseFallbackAuthenticator(config);
			default ->
				new DatabaseAuthenticator(config);
		};
	}

}
