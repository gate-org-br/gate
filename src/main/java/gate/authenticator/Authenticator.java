package gate.authenticator;

import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator
{

	public String provider(HttpServletRequest request,
		HttpServletResponse response) throws AuthenticatorException;

	public User authenticate(HttpServletRequest request,
		HttpServletResponse response)
		throws AuthenticatorException,
		AuthenticationException,
		InvalidPasswordException,
		InvalidUsernameException,
		DefaultPasswordException,
		HierarchyException,
		BadRequestException;

}
