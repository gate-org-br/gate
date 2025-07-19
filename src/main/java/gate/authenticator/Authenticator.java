package gate.authenticator;

import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.http.ScreenServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Authenticator
{

	public String provider(ScreenServletRequest request,
		HttpServletResponse response) throws AuthenticatorException;

	public User authenticate(ScreenServletRequest request,
		HttpServletResponse response)
		throws AuthenticationException,
		HttpException,
		HierarchyException, IOException;

	public String logoutUri(ScreenServletRequest request);

	public boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException;

	public User getUser(ScreenServletRequest request) throws AuthenticationException, IOException;

	public abstract Type getType();

	public enum Type
	{
		DATABASE, LDAP, OIDC
	}
}
