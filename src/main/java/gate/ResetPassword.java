package gate;

import gate.entity.User;
import gate.error.AppException;
import gate.error.InvalidUsernameException;
import gate.messaging.Messenger;
import gate.type.mime.MimeMail;
import gate.util.PasswordGenerator;
import java.io.IOException;
import java.util.Collections;
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
	private GateControl control;

	@Inject
	private Messenger messenger;

	private static final long serialVersionUID = 1L;

	static final String JSP = "/WEB-INF/views/ResetPassword.jsp";

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		try
		{
			if (request.getMethod().equals("POST"))
			{
				User user = control
					.select(request.getParameter("user.username"))
					.setPassword(PasswordGenerator.generate());

				if (user.getEmail() == null)
					throw new AppException("Você não definiu um email para o qual sua nova senha possa ser enviada");

				messenger.post(user.getEmail(), MimeMail.of("Redefinição de senha",
					"Sua senha foi redefinida para " + user.getPassword()));

				control.update(user, user.getPassword());

				request.setAttribute("messages", new String[]
				{
					"Sua senha foi redefinida e enviada para " + user.getEmail()
				});
				request.getRequestDispatcher("/WEB-INF/views/Gate.jsp")
					.forward(request, response);
			}
		} catch (AppException ex)
		{
			request.setAttribute("messages", ex.getMessages());
		} catch (InvalidUsernameException ex)
		{
			request.setAttribute("messages", Collections.singletonList(ex.getMessage()));
		}

		request.getRequestDispatcher(ResetPassword.JSP).forward(request, response);
	}
}
