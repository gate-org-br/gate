package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.HierarchyException;
import gate.error.HttpException;
import gate.http.ScreenServletRequest;
import gate.io.Credentials;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet("/Auth")
public class Auth extends HttpServlet
{

	@Inject
	Credentials credentials;

	@Inject
	@Current
	@RequestScoped
	Authenticator authenticator;

	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException
	{
		httpServletRequest.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		try (Writer writer = response.getWriter())
		{

			try
			{
				User user = authenticator.authenticate(new ScreenServletRequest(httpServletRequest), response);
				if (user == null)
					throw new BadRequestException("Attempt to login without provinding valid credentials");
				writer.write(credentials.subject(user));
			} catch (AuthenticationException ex)
			{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				writer.write(ex.getMessage());
			} catch (HttpException ex)
			{
				response.setStatus(ex.getStatusCode());
				writer.write(ex.getMessage());
			} catch (RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Internal server error");
			}
		}
	}
}
