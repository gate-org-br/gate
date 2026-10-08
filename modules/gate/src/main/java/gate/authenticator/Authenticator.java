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

	String provider(ScreenServletRequest request,
	                HttpServletResponse response) throws AuthenticatorException;

	User authenticate(ScreenServletRequest request,
	                  HttpServletResponse response)
			throws HttpException,
			       HierarchyException, IOException;

	String logoutUri(ScreenServletRequest request);

	boolean hasCredentials(ScreenServletRequest request) throws AuthenticationException;

	Type getType();

	enum Type
	{
		DATABASE, LDAP, OIDC
	}
}