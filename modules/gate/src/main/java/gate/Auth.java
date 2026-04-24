package gate;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.catalog.SessionCatalog;
import gate.entity.User;
import gate.error.BadRequestException;
import gate.error.HttpException;
import gate.http.ScreenServletRequest;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;

@WebServlet("/Auth")
public class Auth extends HttpServlet
{
	@Inject
	@Current
	Authenticator authenticator;

	@Inject
	SessionCatalog sessionCatalog;

	@Serial
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
				var request = new ScreenServletRequest(httpServletRequest);

				if (!authenticator.hasCredentials(request))
					throw new BadRequestException("Missing credentials");

				User user = authenticator.authenticate(request, response);
				if (user == null)
					throw new BadRequestException("Attempt to login without provinding valid credentials");

				var session = sessionCatalog.create(user);
				writer.write(session);
			} catch (HttpException ex)
			{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				writer.write(ex.getMessage());
			} catch (RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Internal server error");
			}
		}
	}
}