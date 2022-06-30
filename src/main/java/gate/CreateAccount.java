package gate;

import gate.constraint.Constraints;
import gate.entity.User;
import gate.error.AppException;
import gate.error.ConstraintViolationException;
import gate.error.ConversionException;
import gate.handler.HTMLCommandHandler;
import gate.sql.insert.Insert;
import gate.type.Phone;
import gate.util.ScreenServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

@MultipartConfig
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet
{

	@Inject
	private Logger logger;

	@Inject
	private Control control;

	@Inject
	private HTMLCommandHandler htmlHanlder;

	static final String HTML = "/views/CreateAccount.html";
	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		if (request.getMethod().equals("POST"))
		{
			try
			{
				ScreenServletRequest req = new ScreenServletRequest(request);
				gate.entity.User user = new gate.entity.User();

				user.setUsername(req.getParameter(String.class, "user.username"));
				if (user.getUsername() == null)
					throw new AppException("Entre com o login desejado.");

				user.setName(req.getParameter("user.name"));
				if (user.getName() == null)
					throw new AppException("Entre com o seu nome completo.");

				user.setPassword(req.getParameter(String.class, "user.password"));
				if (user.getPassword() == null)
					throw new AppException("Entre com a sua senha.");

				user.setRepeat(req.getParameter(String.class, "user.repeat"));
				if (user.getRepeat() == null)
					throw new AppException("Tecla a sua senha novamente.");

				if (!user.getPassword().equals(user.getRepeat()))
					throw new AppException("As senhas digitadas não conferem.");

				user.setEmail(req.getParameter(String.class, "user.email"));
				if (user.getEmail() == null)
					throw new AppException("Informe o seu endereço de email.");

				user.setDescription(req.getParameter(String.class, "user.details"));
				if (user.getDescription() == null)
					throw new AppException("Informe a empresa onde trabalha.");

				user.setActive(true);
				user.setPhone(req.getParameter(Phone.class, "user.phone"));
				user.setCellPhone(req.getParameter(Phone.class, "user.cellPhone"));

				control.insert(user);
				request.setAttribute("messages", "Seu cadastro foi enviado para aprovação. Você será notificado quando aprovado.");

				htmlHanlder.handle(request, response, Gate.HTML);
			} catch (AppException e)
			{
				request.setAttribute("messages", e.getMessages());
				htmlHanlder.handle(request, response, HTML);
			} catch (ConversionException ex)
			{
				logger.error(ex.getMessage(), ex);
			}
		}

		htmlHanlder.handle(request, response, HTML);
	}

	@Dependent
	public static class Control extends gate.base.Control
	{

		public void insert(User value) throws AppException
		{
			Constraints.validate(value, "active", "username", "name",
				"email", "details", "phone", "cellPhone", "CPF");

			try ( Dao dao = new Dao())
			{
				dao.insert(value);
			}
		}

		public static class Dao extends gate.base.Dao
		{

			public void insert(User user) throws ConstraintViolationException
			{
				Insert.into("gate.Uzer")
					.set("active", user.getActive())
					.set("username", user.getUsername())
					.set("password", user.getPassword())
					.set("name", user.getName())
					.set("email", user.getEmail())
					.set("details", user.getDescription())
					.set("phone", user.getPhone())
					.set("cellPhone", user.getCellPhone())
					.set("CPF", user.getCPF())
					.set("registration", LocalDate.now())
					.build()
					.connect(getLink())
					.execute();
			}
		}
	}
}
