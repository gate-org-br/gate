package gate.producer;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.Config;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.OIDCAuthenticator;
import gate.entity.App;
import gate.entity.Org;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author davins
 *
 * Produces a valid authenticator
 */
public class AuthenticatorProducer implements Serializable
{

	@Inject
	@Current
	Org org;

	@Inject
	@Current
	App app;

	@Current
	@Produces
	@ApplicationScoped
	public Authenticator get()
	{
		Config config = new Config(app.getId().toLowerCase());
		switch (config.getProperty("type").orElse("default"))
		{
			case "db":
				return new DatabaseAuthenticator(config);

			case "ldap":
				return new LDAPAuthenticator(config);

			case "oidc":
				return new OIDCAuthenticator(config);

			default:
			case "default":
				if (org.getAuthenticators() != null
					&& !org.getAuthenticators().isEmpty())
					return new LDAPAuthenticator(config, org.getAuthenticators().get(0));
				else
					return new DatabaseAuthenticator(config);
		}
	}
}
