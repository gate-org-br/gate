package gate;

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

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;
import java.time.Instant;

@WebServlet("/Auth")
public class Auth extends HttpServlet
{

	@Inject
	@Current
	Authenticator authenticator;

	@Inject Credentials credentials;

	@Serial private static final long serialVersionUID = 1L;

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
					throw new BadRequestException("Attempt to login without providing valid credentials");
				var subject = new Credentials.Subject(user.getId(), Instant.now());
				writer.write(credentials.createToken(subject));
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