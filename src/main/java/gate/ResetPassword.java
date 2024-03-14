package gate;

import gate.entity.User;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.ConstraintViolationException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.http.ScreenServletRequest;
import gate.io.Token;
import gate.messaging.Messenger;
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
@WebServlet("/ResetPassword")
public class ResetPassword extends HttpServlet
{

	@Inject
	private PasswordControl control;

	@Inject
	private Messenger messenger;

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		httpServletRequest.setCharacterEncoding("UTF-8");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{

			try
			{

				User user = control.select(request.getParameter("username"));

				if (user.getEmail() == null)
					throw new BadRequestException("Você não definiu um email para o qual seu token possa ser enviado");

				System.out.println(Token.create(user));
//				messenger.post(user.getEmail(), MimeMail.of("Redefinição de senha",
//					"Utilize este token para redefinir sua senha: " + Token.create(user)));

			} catch (BadRequestException | InvalidUsernameException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			}
//catch (MessageException | RuntimeException ex)
//			{
//				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//				writer.write("Erro de sistema");
//			}
		}
	}

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
				String token = request.getBearerAuthorization().map(e -> e.token())
					.orElseThrow(() -> new BadRequestException("Credentials not supplied"));
				User user = Token.parse(token);

				control.update(user, request.getBody().trim());

			} catch (BadRequestException | NotFoundException | AuthenticationException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			} catch (ConstraintViolationException | RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Erro de sistema");
			}
		}
	}

}
