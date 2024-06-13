package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.LDAPWithDatabaseFallbackAuthenticator;
import gate.authenticator.OIDCAuthenticator;
import gate.entity.App;
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
				LDAPAuthenticator.of(control, app.getId().toLowerCase());

			case "oidc" ->
				OIDCAuthenticator.of(control, app.getId().toLowerCase());

			case "ldap-with-database-fallback" ->
				LDAPWithDatabaseFallbackAuthenticator.of(control, app.getId().toLowerCase());

			default ->
				DatabaseAuthenticator.of(control);
		};
	}
}
