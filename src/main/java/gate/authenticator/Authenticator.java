package gate.authenticator;

import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator
{

	public Object authenticate(HttpServletRequest request,
		HttpServletResponse response)
		throws AuthenticatorException,
		InvalidPasswordException,
		InvalidUsernameException,
		DefaultPasswordException,
		HierarchyException;

}
