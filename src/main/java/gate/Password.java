package gate;

import gate.entity.User;
import gate.error.AppException;
import gate.type.DateTime;
import gate.util.Toolkit;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/Password")
public class Password extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Inject
	private GateControl control;

	static final String JSP = "/WEB-INF/views/Password.jsp";

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{

		try
		{
			request.setAttribute("now", new DateTime());

			if (request.getMethod().equals("POST"))
			{
				User user = new User();
				user.setUserID(request.getParameter("user.userID"));
				user.setPasswd(request.getParameter("user.passwd"));
				user.setChange(request.getParameter("user.change"));
				user.setRepeat(request.getParameter("user.repeat"));

				if (Toolkit.isEmpty(user.getUserID()) || user.getUserID().length() > 64)
					throw new AppException("O campo login é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getPasswd()) || user.getPasswd().length() > 64)
					throw new AppException("O campo senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getChange()) || user.getChange().length() > 64)
					throw new AppException("O campo nova senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getRepeat()) || user.getRepeat().length() > 64)
					throw new AppException("O campo nova senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (!user.getChange().equals(user.getRepeat()))
					throw new AppException("Os campos de nova senha devem ser preenchidos exatamente como mesmo texto.");

				if (!control.update(user))
					throw new AppException("Usuário se senha inválidos.");

				request.setAttribute("messages", new String[]
				{
					"Sua senha foi alterada com sucesso."
				});
				request.getRequestDispatcher("/WEB-INF/views/Gate.jsp")
						.forward(request, response);
				return;
			}
		} catch (AppException e)
		{
			request.setAttribute("messages", e.getMessages());
		}

		request.getRequestDispatcher(Password.JSP).forward(request, response);
	}
}
