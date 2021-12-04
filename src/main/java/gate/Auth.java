package gate;

import gate.annotation.Current;
import gate.entity.Org;
import gate.entity.User;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.DuplicateException;
import gate.error.InvalidCircularRelationException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidServiceException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.io.Credentials;
import java.io.IOException;
import java.io.Writer;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Auth")
public class Auth extends HttpServlet
{

	@Inject
	@Current
	private Org org;

	private static final long serialVersionUID = 1L;

	@Inject
	private GateControl control;

	@Override
	public void service(HttpServletRequest request,
		HttpServletResponse response)
		throws IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try ( Writer writer = response.getWriter())
		{
			try
			{
				User user = control.select(org, username, password);
				String credentials = Credentials.create(user);
				writer.write(String.format("{status: 'success', value: '%s'}",
					credentials));
			} catch (InvalidServiceException
				| InvalidUsernameException
				| AuthenticatorException
				| InvalidPasswordException
				| DefaultPasswordException
				| DuplicateException
				| InvalidCircularRelationException
				| NotFoundException ex)
			{
				writer.write(String.format("{status: 'error', value: '%s'}",
					ex.getMessage()));
			}
		}
	}
}
