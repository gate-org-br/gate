package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.OIDCAuthenticator;
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
	GateControl control;

	@Current
	@Produces
	@ApplicationScoped
	@Named("authenticator")
	public Authenticator get()
	{
		return switch (SystemProperty.get("gate.auth.type").orElse("db"))
		{
			case "db" ->
				DatabaseAuthenticator.of(control);

			case "ldap" ->
				LDAPAuthenticator.of(control);

			case "oidc" ->
				OIDCAuthenticator.of(control);

			default ->
				DatabaseAuthenticator.of(control);
		};
	}
}
