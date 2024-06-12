package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.http.BasicAuthorization;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import gate.util.SystemProperty;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletResponse;

public class LDAPAuthenticator implements Authenticator
{

	private final GateControl control;

	private final String server;
	private final String clientUsername;
	private final String clientPassword;
	private final String securityProtocol;
	private final String rootContext;
	private final String developer = SystemProperty.get("gate.developer").orElse(null);

	private LDAPAuthenticator(GateControl control,
		String server, String securityProtocol,
		String clientUsername, String clientPassword,
		String rootContext)
	{
		this.control = control;
		this.server = server;
		this.securityProtocol = securityProtocol;
		this.clientUsername = clientUsername;
		this.clientPassword = clientPassword;
		this.rootContext = rootContext;
	}

	public static LDAPAuthenticator of(GateControl control, String app, String server)
	{

		String securityProtocol = SystemProperty.get(app + ".auth.ldap.security_protocol")
			.or(() -> SystemProperty.get("gate.auth.ldap.security_protocol"))
			.orElse(null);

		String cilentUsername = SystemProperty.get(app + ".auth.ldap.client_username")
			.or(() -> SystemProperty.get("gate.auth.ldap.client_username"))
			.orElse(null);

		String clientPassword = SystemProperty.get(app + ".auth.ldap.client_password")
			.or(() -> SystemProperty.get("gate.auth.ldap.client_password"))
			.orElse(null);

		String rootContext = SystemProperty.get(app + ".auth.ldap.root_context")
			.or(() -> SystemProperty.get("gate.auth.ldap.root_context"))
			.orElse("");

		return new LDAPAuthenticator(control, server, securityProtocol, cilentUsername, clientPassword, rootContext);
	}

	public static LDAPAuthenticator of(GateControl control, String app)
	{
		String server = SystemProperty.get(app + ".auth.ldap.server")
			.or(() -> SystemProperty.get("gate.auth.ldap.server"))
			.orElseThrow(() -> new AuthenticatorException("Missing gate.auth.ldap.server system property"));

		return of(control, app, server);
	}

	@Override
	public String provider(ScreenServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	private DirContext getDirContext(String username, String password) throws NamingException
	{
		@SuppressWarnings("UseOfObsoleteCollectionType")
		Hashtable<String, String> parameters = new Hashtable<>();
		parameters.put(Context.SECURITY_AUTHENTICATION, "simple");
		parameters.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		parameters.put(Context.PROVIDER_URL, server);
		parameters.put(Context.SECURITY_PRINCIPAL, username);
		parameters.put(Context.SECURITY_CREDENTIALS, password);
		parameters.put(Context.SECURITY_PROTOCOL, securityProtocol);

		return new InitialDirContext(parameters);
	}

	private String getUniqueID(String username, String password, BasicAuthorization authorization) throws NamingException
	{
		DirContext serverContext = getDirContext(username, password);
		try
		{

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> enumeration = serverContext.search(rootContext, "(|(dn={0})(cn={1})(mail={2}))",
				new Object[]
				{
					authorization.username(), authorization.username(), authorization.username()
				}, controls);

			return enumeration.hasMore() ? enumeration.next().getNameInNamespace() : null;
		} finally
		{
			serverContext.close();
		}
	}

	@Override
	public User authenticate(ScreenServletRequest request, HttpServletResponse response)
		throws gate.error.AuthenticationException, HierarchyException
	{
		var authorization = request.getBasicAuthorization().orElse(null);

		if (authorization == null)
			return developer != null ? control.select(developer) : null;

		User user = control.select(authorization.username());

		try
		{
			if (clientUsername != null && clientPassword != null)
			{
				String dn = getUniqueID(clientUsername, clientPassword, authorization);
				if (dn == null)
				{
					if (MD5.digest(authorization.username()).toString()
						.equals(user.getPassword()))
						throw new DefaultPasswordException();

					if (!MD5.digest(authorization.password())
						.toString().equals(user.getPassword()))
						throw new InvalidPasswordException();
				} else
					getDirContext(dn, authorization.password())
						.close();
			} else
				getDirContext(authorization.username(), authorization.password())
					.close();

			return user;

		} catch (CommunicationException ex)
		{
			throw new AuthenticatorException(ex);
		} catch (NamingException ex)
		{
			if (ex.getCause() instanceof AuthenticationException)
				throw new InvalidPasswordException();
			else
				throw new AuthenticatorException(ex);
		}
	}

	@Override
	public String logoutUri(gate.http.ScreenServletRequest request)
	{
		return null;
	}
}
