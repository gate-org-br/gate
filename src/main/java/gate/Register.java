package gate;

import gateconsole.contol.UserControl;
import gate.error.AppException;
import gate.error.ConversionException;
import gate.type.Phone;
import gate.util.ScreenServletRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/Register")
public class Register extends HttpServlet
{

	static final String JSP = "/WEB-INF/views/Register.jsp";

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (request.getMethod().equals("POST"))
		{
			try
			{
				ScreenServletRequest req = new ScreenServletRequest(request);
				gate.entity.User user = new gate.entity.User();

				user.setUserID(req.getParameter(String.class, "user.userID"));
				if (user.getUserID() == null)
					throw new AppException("Entre com o login desejado.");

				user.setName(req.getParameter("user.name"));
				if (user.getName() == null)
					throw new AppException("Entre com o seu nome completo.");

				user.setPasswd(req.getParameter(String.class, "user.passwd"));
				if (user.getPasswd() == null)
					throw new AppException("Entre com a sua senha.");

				user.setRepeat(req.getParameter(String.class, "user.repeat"));
				if (user.getRepeat() == null)
					throw new AppException("Tecla a sua senha novamente.");

				if (!user.getPasswd().equals(user.getRepeat()))
					throw new AppException("As senhas digitadas não conferem.");

				user.setEmail(req.getParameter(String.class, "user.email"));
				if (user.getEmail() == null)
					throw new AppException("Informe o seu endereço de email.");

				user.setDetails(req.getParameter(String.class, "user.details"));
				if (user.getDetails() == null)
					throw new AppException("Informe a empresa onde trabalha.");

				user.setActive(Boolean.TRUE);
				user.setPhone(req.getParameter(Phone.class, "user.phone"));
				user.setCellPhone(req.getParameter(Phone.class, "user.cellPhone"));

				new UserControl().insert(user);
				request.setAttribute("messages", "Seu cadastro foi enviado para aprovação. Você será notificado quando aprovado.");
				request.getRequestDispatcher(Gate.GATE_JSP).forward(request, response);
			} catch (AppException e)
			{
				request.setAttribute("messages", e.getMessages());
				request.getRequestDispatcher(Register.JSP).forward(request, response);
			} catch (ConversionException ex)
			{
				Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		request.getRequestDispatcher(Register.JSP).forward(request, response);
	}
}
