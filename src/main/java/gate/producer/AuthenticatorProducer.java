package gate.producer;

import gate.GateControl;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.authenticator.DatabaseAuthenticator;
import gate.authenticator.LDAPAuthenticator;
import gate.authenticator.OIDCAuthenticator;
import gate.entity.Org;
import gate.util.SystemProperty;
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
	Org org;

	@Inject
	GateControl control;

	@Current
	@Produces
	@ApplicationScoped
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
