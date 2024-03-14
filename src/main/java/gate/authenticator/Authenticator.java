package gate.authenticator;

import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.http.ScreenServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator
{

	public String provider(ScreenServletRequest request,
		HttpServletResponse response) throws AuthenticatorException;

	public User authenticate(ScreenServletRequest request,
		HttpServletResponse response)
		throws AuthenticationException,
		InvalidPasswordException,
		InvalidUsernameException,
		DefaultPasswordException,
		HierarchyException,
		BadRequestException;

	public String logoutUri(ScreenServletRequest request);

	public abstract Type getType();

	public enum Type
	{
		DATABASE, LDAP, OIDC
	}
}
