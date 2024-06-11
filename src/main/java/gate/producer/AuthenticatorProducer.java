package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.LDAPWithDatabaseFallbackAuthenticator;
import gate.authenticator.OIDCAuthenticator;
import gate.entity.App;
import gate.entity.Org;
import gate.util.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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
	Org org;

	@Inject
	@Current
	App app;

	@Inject
	GateControl control;

	@Current
	@Produces
	@ApplicationScoped
	@Named("authenticator")
	public Authenticator get()
	{
		return switch (SystemProperty.get(app + ".auth.type")
			.or(() -> SystemProperty.get("gate.auth.type"))
			.orElse("database"))
		{
			case "database" ->
				DatabaseAuthenticator.of(control);

			case "ldap" ->
				LDAPAuthenticator.of(app.getId().toLowerCase(), control);

			case "oidc" ->
				OIDCAuthenticator.of(app.getId().toLowerCase(), control);

			case "ldap-with-database-fallback" ->
				LDAPWithDatabaseFallbackAuthenticator.of(app.getId().toLowerCase(), control);

			default ->
				DatabaseAuthenticator.of(control);
		};
	}
}
