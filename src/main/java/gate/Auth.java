package gate;

import gate.annotation.Current;
import gate.entity.Org;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.HierarchyException;
import gate.io.Credentials;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Base64;
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
	public void doGet(HttpServletRequest request,
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
			} catch (AuthenticationException
				| AuthenticatorException
				| HierarchyException ex)
			{
				writer.write(String.format("{status: 'error', value: '%s'}",
					ex.getMessage()));
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest request,
		HttpServletResponse response)
		throws IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		try ( Writer writer = response.getWriter())
		{

			String authHeader = request.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Basic"))
			{
				String encoded = authHeader.substring("Basic".length()).trim();

				try
				{
					String decoded = new String(Base64.getDecoder().decode(encoded), Charset.forName("UTF-8"));

					int index = decoded.indexOf(":");
					String username = decoded.substring(0, index);
					String password = decoded.substring(index + 1);

					try
					{
						User user = control.select(org, username, password);
						writer.write(Credentials.create(user));
					} catch (AuthenticationException ex)
					{
						response.setStatus(400);
						writer.write(ex.getMessage());
					} catch (AuthenticatorException ex)
					{
						response.setStatus(503);
						writer.write(ex.getMessage());
					} catch (HierarchyException ex)
					{
						response.setStatus(503);
						writer.write("Internal server error");
					}
				} catch (RuntimeException ex)
				{
					response.setStatus(400);
					writer.write("Invalid basic authorization header");
				}
			} else
			{
				response.setStatus(401);
				writer.write("Attempt to login without providing username and password using http basic authentication");
			}
		}
	}

}
