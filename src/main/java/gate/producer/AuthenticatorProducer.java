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
		switch (SystemProperty.get("gate.auth.type").orElse("default"))
		{
			case "db":
				return DatabaseAuthenticator.of(control);

			case "ldap":
				return LDAPAuthenticator.of(control);

			case "oidc":
				return OIDCAuthenticator.of(control);

			default:
			case "default":
				if (org.getAuthenticators() != null
					&& !org.getAuthenticators().isEmpty())
					return LDAPAuthenticator.of(control,
						org.getAuthenticators().get(0));
				else
					return DatabaseAuthenticator.of(control);
		}
	}
}
