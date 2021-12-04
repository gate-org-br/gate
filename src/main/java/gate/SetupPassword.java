package gate;

import gate.entity.User;
import gate.error.AppException;
import gate.handler.HTMLCommandHandler;
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
@WebServlet("/SetupPassword")
public class SetupPassword extends HttpServlet
{

	@Inject
	private GateControl control;

	@Inject
	private HTMLCommandHandler htmlHanlder;

	private static final long serialVersionUID = 1L;

	static final String HTML = "/views/SetupPassword.html";

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		try
		{
			request.setAttribute("now", DateTime.now());

			if (request.getMethod().equals("POST"))
			{
				User user = new User();
				user.setUsername(request.getParameter("user.username"));
				user.setPassword(request.getParameter("user.password"));
				user.setChange(request.getParameter("user.change"));
				user.setRepeat(request.getParameter("user.repeat"));

				if (Toolkit.isEmpty(user.getUsername()) || user.getUsername().length() > 64)
					throw new AppException("O campo login é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getPassword()) || user.getPassword().length() > 64)
					throw new AppException("O campo senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getChange()) || user.getChange().length() > 64)
					throw new AppException("O campo nova senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (Toolkit.isEmpty(user.getRepeat()) || user.getRepeat().length() > 64)
					throw new AppException("O campo nova senha é obrigatório e deve conter no máximo 64 caracteres.");

				if (!user.getChange().equals(user.getRepeat()))
					throw new AppException("Os campos de nova senha devem ser preenchidos exatamente como mesmo texto.");

				control.update(user);

				request.setAttribute("messages", new String[]
				{
					"Sua senha foi alterada com sucesso."
				});
				htmlHanlder.handle(request, response, Gate.HTML);
				return;
			}
		} catch (AppException e)
		{
			request.setAttribute("messages", e.getMessages());
		}

		htmlHanlder.handle(request, response, HTML);
	}
}
