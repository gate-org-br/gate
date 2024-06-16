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
	@Current
	private Authenticator authenticator;

	@Inject
	GateControl control;

	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		try (Writer writer = response.getWriter())
		{

			try
			{
				User user = authenticator.authenticate(control,
					new ScreenServletRequest(request), response);
				if (user == null)
					throw new BadRequestException("Attempt to login without provinding valid credentials");
				writer.write(Credentials.create(user));
			} catch (AuthenticationException ex)
			{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				writer.write(ex.getMessage());
			} catch (HttpException ex)
			{
				response.setStatus(ex.getStatusCode());
				writer.write(ex.getMessage());
			} catch (HierarchyException
				| RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Internal server error");
			}
		}
	}
}
