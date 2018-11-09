package gate;

import gate.annotation.Background;
import gate.annotation.BackgroundProcess;
import gate.annotation.Current;
import gate.annotation.Public;
import gate.annotation.Strict;
import gate.base.Screen;
import gate.entity.App;
import gate.entity.Org;
import gate.entity.User;
import gate.error.AccessDeniedException;
import gate.error.AppError;
import gate.error.AuthenticatorException;
import gate.error.DefaultPasswordException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidRequestException;
import gate.error.InvalidServiceException;
import gate.error.InvalidUsernameException;
import gate.handler.Handler;
import gate.io.Credentials;
import gate.util.ScreenServletRequest;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
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
	private ManagedExecutorService service;

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

				if (!Toolkit.isEmpty(username)
						&& !Toolkit.isEmpty(password))
					session.setUser(user = control.select(org, username, password));

				Class<Screen> clazz = Screen.getScreen(MODULE, SCREEN).orElseThrow(InvalidRequestException::new);
				Method method = Screen.getAction(clazz, ACTION).orElseThrow(InvalidRequestException::new);
				if (!Gate.checkAccess(user, MODULE, SCREEN, ACTION, clazz, method))
					throw new AccessDeniedException();

				Screen screen = Screen.create(clazz);
				request.setAttribute("screen", screen);
				screen.prepare(request, response);

				if (method.isAnnotationPresent(Background.class)
						|| method.isAnnotationPresent(BackgroundProcess.class))
				{
					if (method.isAnnotationPresent(Background.class)
							&& method.isAnnotationPresent(BackgroundProcess.class))
						throw new java.lang.IllegalArgumentException("Attempt to define a screen method annotaned with both Background and BackgroundProcess");

					Progress progress = Progress.create(org, app, user);
					request.setAttribute("process", progress.getProcess());

					service.submit(() ->
					{
						Progress.bind(progress);
						try
						{
							Object url = screen.execute(method);
							if (url != null)
								Progress.redirect(url.toString());
						} catch (InvocationTargetException ex)
						{
							if (Progress.Status.PENDING.equals(Progress.status()))
								Progress.cancel(ex.getCause().getMessage());
						} catch (RuntimeException | IllegalAccessException ex)
						{
							if (Progress.Status.PENDING.equals(Progress.status()))
								Progress.cancel(ex.getMessage());
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
					});

					if (method.isAnnotationPresent(Background.class))
						getServletContext()
								.getRequestDispatcher(method.getAnnotation(Background.class).value())
								.forward(request, response);
					else
						Handler.getHandler(Integer.class).handle(httpServletRequest, response, progress.getProcess());
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
			httpServletRequest.setAttribute("messages", Arrays.asList(ex.getMessage()));
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (DefaultPasswordException ex)
		{
			httpServletRequest.setAttribute("messages", Arrays.asList(ex.getMessage()));
			httpServletRequest.getRequestDispatcher(Password.JSP).forward(httpServletRequest, response);
		} catch (AuthenticatorException | AppError ex)
		{
			httpServletRequest.setAttribute("messages", Arrays.asList(ex.getMessage()));
			httpServletRequest.setAttribute("exception", ex.getCause());
			Logger.getGlobal().log(Level.SEVERE, ex.getCause().getMessage(), ex.getCause());
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (InvocationTargetException ex)
		{
			httpServletRequest.setAttribute("messages", Arrays.asList("Erro de sistema"));
			httpServletRequest.setAttribute("exception", ex.getTargetException());
			Logger.getGlobal().log(Level.SEVERE, ex.getTargetException().getMessage(), ex.getTargetException());
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (IOException | IllegalAccessException | ServletException | RuntimeException ex)
		{
			httpServletRequest.setAttribute("messages", Arrays.asList("Erro de sistema"));
			httpServletRequest.setAttribute("exception", ex);
			Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		}
	}

	public static boolean checkAccess(User user,
			String module,
			String screen,
			String action,
			Class<?> clazz,
			Method method)
	{
		return clazz.isAnnotationPresent(Public.class)
				|| method.isAnnotationPresent(Public.class)
				|| (user != null
				&& user.checkAccess(method.isAnnotationPresent(Strict.class)
						|| clazz.isAnnotationPresent(Strict.class), module, screen, action));
	}

}
