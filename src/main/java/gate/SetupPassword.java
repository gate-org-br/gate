package gate;

import java.io.IOException;
import java.io.Writer;

import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.http.BasicAuthorization;
import gate.http.ScreenServletRequest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/SetupPassword")
public class SetupPassword extends HttpServlet
{
	@Inject
	@Current
	Authenticator authenticator;

	@Inject
	private PasswordControl control;

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		httpServletRequest.setCharacterEncoding("UTF-8");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{
			try
			{

				if (request.getAuthorization() instanceof BasicAuthorization)
				{
					var user = authenticator.getUser(request);
					control.update(user, request.getBody().trim());
				} else
					throw new BadRequestException("Missing user credentials");

			} catch (AuthenticationException | BadRequestException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			} catch (RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Erro de sistema");
			}
		}
	}
}
