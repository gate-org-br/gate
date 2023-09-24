package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.HierarchyException;
import gate.http.ScreenServletRequest;
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
	private Authenticator authenticator;

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest httpServletRequest,
		HttpServletResponse response)
		throws IOException
	{
		httpServletRequest.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{
			try
			{
				User user = authenticator.authenticate(request, response);
				if (user == null)
					throw new AuthenticatorException("Attempt to login without provinding valid credentials");
				writer.write(String.format("{status: 'success', value: '%s'}", Credentials.create(user)));
			} catch (AuthenticationException
				| AuthenticatorException
				| HierarchyException
				| BadRequestException ex)
			{
				writer.write(String.format("{status: 'error', value: '%s'}",
					ex.getMessage()));
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest httpServletRequest,
		HttpServletResponse response)
		throws IOException
	{
		httpServletRequest.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		try (Writer writer = response.getWriter())
		{

			try
			{
				User user = authenticator.authenticate(request, response);
				if (user == null)
					throw new AuthenticationException("Attempt to login without provinding valid credentials");
				writer.write(Credentials.create(user));
			} catch (AuthenticationException | BadRequestException ex)
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
			} catch (RuntimeException ex)
			{
				response.setStatus(400);
				writer.write("Attempt to login without provinding valid credentials");
			}
		}
	}
}
