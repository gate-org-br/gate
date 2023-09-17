package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OIDCAuthenticator implements Authenticator
{

	private final GateControl control;
	private final String provider;
	private final String client_id;
	private final String client_secret;
	private final String redirect_uri;

	public OIDCAuthenticator(GateControl control,
		String provider,
		String client_id,
		String client_secret,
		String redirect_uri)
	{
		this.control = control;
		this.provider = provider;
		this.redirect_uri = redirect_uri;
		this.client_id = client_id;
		this.client_secret = client_secret;
	}

	@Override
	public String provider(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	public static OIDCAuthenticator of(GateControl control)
	{
		String provider = System.getProperty("gate.auth.oidc.provider", System.getenv("gate.auth.oidc.provider"));
		if (provider == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.provider system property");

		String client_id = System.getProperty("gate.auth.oidc.client_id", System.getenv("gate.auth.oidc.client_id"));
		if (client_id == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.client_id system property");

		String client_secret = System.getProperty("gate.auth.oidc.client_secret", System.getenv("gate.auth.oidc.client_secret"));
		if (client_secret == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.client_secret system property");

		String redirect_uri = System.getProperty("gate.auth.oidc.redirect_uri", System.getenv("gate.auth.oidc.redirect_uri"));
		if (redirect_uri == null)
			throw new java.lang.IllegalArgumentException("Missing gate.auth.oidc.redirect_uri system property");

		return new OIDCAuthenticator(control, provider, client_id, client_secret, redirect_uri);
	}

	@Override
	public User authenticate(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticatorException, InvalidPasswordException,
		InvalidUsernameException, HierarchyException, DefaultPasswordException
	{
		return null;
	}
}
