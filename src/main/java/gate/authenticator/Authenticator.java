package gate.authenticator;

import gate.GateControl;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.http.ScreenServletRequest;
import gate.util.SystemProperty;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator
{

	public abstract String provider(ScreenServletRequest request,
		HttpServletResponse response) throws AuthenticatorException;

	public abstract User authenticate(GateControl control, ScreenServletRequest request,
		HttpServletResponse response)
		throws AuthenticationException,
		InvalidPasswordException,
		InvalidUsernameException,
		DefaultPasswordException,
		HierarchyException,
		BadRequestException;

	public abstract String logoutUri(ScreenServletRequest request);

}
