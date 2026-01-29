package gate;

import java.io.IOException;
import java.io.Writer;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.HttpException;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Auth")
public class Auth extends HttpServlet
{

	@Inject
	Credentials credentials;

	@Inject
	@Current
	Authenticator authenticator;

	@Inject
	GateControl control;

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

				var token = Credentials.SubjectToken.create(user.getId());
				control.update(user, token.iat());
				writer.write(credentials.fromToken(token));
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
