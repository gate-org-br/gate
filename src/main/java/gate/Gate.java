package gate;

import gate.annotation.Annotations;
import gate.annotation.Asynchronous;
import gate.annotation.Current;
import gate.annotation.Disabled;
import gate.annotation.Public;
import gate.annotation.Superuser;
import gate.base.Screen;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import gate.error.AccessDeniedException;
import gate.error.AppError;
import gate.error.AppException;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.DuplicateException;
import gate.error.InvalidCircularRelationException;
import gate.error.InvalidCredentialsException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidRequestException;
import gate.error.InvalidServiceException;
import gate.error.InvalidUsernameException;
import gate.error.NotFoundException;
import gate.handler.Handler;
import gate.io.Credentials;
import gate.type.Result;
import gate.util.ScreenServletRequest;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/Gate")
public class Gate extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	@Inject
	@Current
	private Org org;

	@Inject
	@Current
	private App app;

	@Inject
	private Session session;

	@Inject
	private GateControl control;

	@Resource
	private ManagedThreadFactory managedThreadFactory;

	static final String GATE_JSP = "/WEB-INF/views/Gate.jsp";

	static
	{
		Locale.setDefault(new Locale("pt", "BR"));
	}

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			httpServletRequest.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setLocale(Locale.getDefault());
			ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
			String MODULE = request.getParameter("MODULE");
			String SCREEN = request.getParameter("SCREEN");
			String ACTION = request.getParameter("ACTION");
			request.setAttribute("ACTION", ACTION);
			request.setAttribute("MODULE", MODULE);
			request.setAttribute("SCREEN", SCREEN);

			User user = Credentials.of(request).orElseGet(() -> session.getUser());

			if (Toolkit.isEmpty(MODULE)
				&& Toolkit.isEmpty(SCREEN)
				&& Toolkit.isEmpty(ACTION))

			{
				if (request.getSession(false) != null)
					request.getSession().invalidate();
				session = CDI.current().select(Session.class).get();
				getServletContext().getRequestDispatcher(GATE_JSP).forward(request, response);
			} else
			{
				String username = request.getParameter("$userid");
				String password = request.getParameter("$passwd");

				if (!Toolkit.isEmpty(username) && !Toolkit.isEmpty(password))
					session.setUser(user = control.select(org, username, password));
				else if (httpServletRequest.getUserPrincipal() != null
					&& !Toolkit.isEmpty(httpServletRequest.getUserPrincipal().getName()))
					session.setUser(user = control.select(httpServletRequest.getUserPrincipal().getName()));

				Class<Screen> clazz = Screen.getScreen(MODULE, SCREEN).orElseThrow(InvalidRequestException::new);
				Method method = Screen.getAction(clazz, ACTION).orElseThrow(InvalidRequestException::new);
				if (!Gate.checkAccess(user, MODULE, SCREEN, ACTION, clazz, method))
					throw new AccessDeniedException();

				Screen screen = Screen.create(clazz);
				request.setAttribute("screen", screen);
				request.setAttribute("action", method);
				screen.prepare(request, response);

				if (method.isAnnotationPresent(Asynchronous.class))
				{
					Progress progress = Progress.create(org, app, user);
					request.setAttribute("process", progress.getProcess());
					Handler.getHandler(Integer.class).handle(httpServletRequest, response, progress.getProcess());

					managedThreadFactory.newThread(() ->
					{
						Progress.bind(progress);
						try
						{
							Thread.sleep(1000);
							Object url = screen.execute(method);
							if (url != null)
								Progress.redirect(url.toString());
						} catch (InvocationTargetException ex)
						{
							if (Progress.Status.PENDING.equals(Progress.status()))
								Progress.cancel(ex.getCause().getMessage());
							else
								Progress.message(ex.getCause().getMessage());
						} catch (RuntimeException | IllegalAccessException | InterruptedException ex)
						{
							if (Progress.Status.PENDING.equals(Progress.status()))
								Progress.cancel(ex.getMessage());
							else
								Progress.message(ex.getMessage());
							Logger.getLogger(Gate.class.getName()).log(Level.SEVERE, null, ex);
						} finally
						{
							try
							{
								Thread.sleep(5000);
								Progress.dispose();
							} catch (InterruptedException ex)
							{
								Progress.dispose();
							}
						}
					}).start();
				} else
				{
					Object result = screen.execute(method);
					if (result != null)
						if (method.isAnnotationPresent(gate.annotation.Handler.class))
							Handler.getInstance(method.getAnnotation(gate.annotation.Handler.class).value())
								.handle(request, response, result);
						else
							Handler.getHandler(result.getClass()).handle(request, response, result);
				}
			}

		} catch (InvalidUsernameException
			| InvalidPasswordException
			| InvalidRequestException
			| AccessDeniedException
			| InvalidServiceException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (DefaultPasswordException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			httpServletRequest.getRequestDispatcher(SetupPassword.JSP).forward(httpServletRequest, response);
		} catch (AuthenticatorException | AppError ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			httpServletRequest.setAttribute("exception", ex.getCause());
			Logger.getGlobal().log(Level.SEVERE, ex.getCause().getMessage(), ex.getCause());
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (DuplicateException | InvalidCircularRelationException | NotFoundException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Banco de dados inconsistente"));
			httpServletRequest.setAttribute("exception", ex);
			Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (IOException | IllegalAccessException | ServletException | RuntimeException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Erro de sistema"));
			httpServletRequest.setAttribute("exception", ex);
			Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (InvocationTargetException ex)
		{
			if (ex.getCause() instanceof AppException)
			{
				Handler.getHandler(AppException.class).
					handle(httpServletRequest, response, ex.getCause());
			} else
			{
				httpServletRequest.setAttribute("messages", Collections.singletonList("Erro de sistema"));
				httpServletRequest.setAttribute("exception", ex.getTargetException());
				Logger.getGlobal().log(Level.SEVERE, ex.getTargetException().getMessage(), ex.getTargetException());
				httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
			}
		} catch (InvalidCredentialsException ex)
		{
			Handler.getHandler(Result.class)
				.handle(httpServletRequest, response, Result.error(ex.getMessage()));

		}
	}

	public static boolean checkAccess(User user,
		String module,
		String screen,
		String action,
		Class<?> clazz,
		Method method)
	{
		if (Annotations.exists(Disabled.class, clazz, method))
			return false;

		if (Annotations.exists(Superuser.class, clazz, method))
			return user != null && user.isSuperUser();

		if (Annotations.exists(Public.class, clazz, method))
			return true;

		return user != null && user.checkAccess(module, screen, action);
	}

}
