package gate;

import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.ConstraintViolationException;
import gate.error.InvalidPasswordException;
import gate.error.NotFoundException;
import gate.http.ScreenServletRequest;
import gate.type.MD5;
import java.io.IOException;
import java.io.Writer;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/SetupPassword")
public class SetupPassword extends HttpServlet
{

	@Inject
	private PasswordControl control;

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		httpServletRequest.setCharacterEncoding("UTF-8");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{
			try
			{

				var authorization = request.getBasicAuthorization()
					.orElseThrow(() -> new BadRequestException("Missing user credentials"));

				User user = control.select(authorization.username());
				if (!user.getPassword().equals(MD5.digest(authorization.password()).toString()))
					throw new InvalidPasswordException();

				control.update(user, request.getBody().trim());

			} catch (AuthenticationException | BadRequestException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			} catch (ConstraintViolationException | NotFoundException | RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Erro de sistema");
			}
		}
	}
}
