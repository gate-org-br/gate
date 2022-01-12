package gate;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.Current;
import gate.base.Screen;
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
import gate.event.LoginEvent;
import gate.handler.AppExceptionHandler;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.handler.IntegerHandler;
import gate.handler.ResultHandler;
import gate.io.Credentials;
import gate.type.Result;
import gate.util.ScreenServletRequest;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@MultipartConfig
@WebServlet(value = "/Gate", asyncSupported = true)
public class Gate extends HttpServlet
{

	static final String HTML = "/views/Gate.html";
	private static final long serialVersionUID = 1L;

	@Inject
	@Current
	Org org;

	@Inject
	Logger logger;

	@Inject
	GateControl control;

	@Inject
	Event<LoginEvent> event;

	@Any
	@Inject
	Instance<Handler> handlers;

	@Inject
	@ConfigProperty(name = "gate.denveloper")
	Optional<String> denveloper;

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

			User user = Credentials.of(request).orElseGet(() -> (User) request.getSession().getAttribute(User.class.getName()));

			if (Toolkit.isEmpty(MODULE, SCREEN, ACTION))

			{
				if (request.getSession(false) != null)
					request.getSession().invalidate();
				handlers.select(HTMLCommandHandler.class).get()
					.handle(httpServletRequest, response, HTML);
			} else
			{
				String username = request.getParameter("$username");
				String password = request.getParameter("$password");

				if (Toolkit.notEmpty(username, password))
				{
					user = control.select(org, username, password);
					request.getSession().setAttribute(User.class.getName(), user);
					event.fireAsync(new LoginEvent(user));
				} else if (httpServletRequest.getUserPrincipal() != null
					&& !Toolkit.isEmpty(httpServletRequest.getUserPrincipal().getName()))
				{
					user = control.select(httpServletRequest.getUserPrincipal().getName());
					request.getSession().setAttribute(User.class.getName(), user);
				} else if (user == null && denveloper.isPresent())
				{
					user = control.select(denveloper.orElseThrow());
					request.getSession().setAttribute(User.class.getName(), user);
					event.fireAsync(new LoginEvent(user));
				}

				Call call = Call.of(MODULE, SCREEN, ACTION);
				if (!call.checkAccess(user))
					throw new AccessDeniedException();

				Screen screen = CDI.current().select(call.getType()).get();
				request.setAttribute("screen", screen);
				request.setAttribute("action", call.getMethod());
				screen.prepare(request, response);

				if (call.getMethod().isAnnotationPresent(Cors.class))
					setCorsHeaders(request, response);

				if (call.getMethod().isAnnotationPresent(Asynchronous.class))
					executeAsync(httpServletRequest, response, screen, call.getMethod());
				else
					execute(httpServletRequest, response, screen, call.getMethod());
			}

		} catch (InvalidUsernameException
			| InvalidPasswordException
			| InvalidRequestException
			| AccessDeniedException
			| InvalidServiceException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(httpServletRequest, response, HTML);
		} catch (DefaultPasswordException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(httpServletRequest, response, SetupPassword.HTML);
		} catch (AuthenticatorException | AppError ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
			httpServletRequest.setAttribute("exception", ex.getCause());
			logger.error(ex.getCause().getMessage(), ex.getCause());
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(httpServletRequest, response, HTML);
		} catch (DuplicateException | InvalidCircularRelationException | NotFoundException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Banco de dados inconsistente"));
			httpServletRequest.setAttribute("exception", ex);
			logger.error(ex.getMessage(), ex);
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(httpServletRequest, response, HTML);
		} catch (InvalidCredentialsException ex)
		{
			Handler handler = handlers.select(ResultHandler.class).get();
			handler.handle(httpServletRequest, response, Result.error(ex.getMessage()));
		}
	}

	private void execute(HttpServletRequest request, HttpServletResponse response, Screen screen, Method method)
	{
		try
		{
			Object result = screen.execute(method);
			if (result != null)
			{
				var type = method.isAnnotationPresent(gate.annotation.Handler.class)
					? method.getAnnotation(gate.annotation.Handler.class).value()
					: Handler.getHandler(result.getClass());
				var handler = handlers.select(type).get();
				handler.handle(request, response, result);
			}
		} catch (AppException ex)
		{
			Handler handler = handlers.select(AppExceptionHandler.class).get();
			handler.handle(request, response, ex);
		} catch (Throwable ex)
		{
			request.setAttribute("messages", Collections.singletonList("Erro de sistema"));
			request.setAttribute("exception", ex);
			logger.error(ex.getMessage(), ex);
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(request, response, HTML);
		}
	}

	private void executeAsync(HttpServletRequest request, HttpServletResponse response, Screen screen, Method method)
	{
		try
		{
			request.startAsync();
			Progress progress = Progress.create();
			Handler handler = handlers.select(IntegerHandler.class).get();
			handler.handle(request, response, progress.getProcess());
			request.getAsyncContext().complete();

			Thread.sleep(1000);
			Object url = screen.execute(method);
			if (url != null)
				Progress.redirect(url.toString());
		} catch (AppException ex)
		{
			if (Progress.status() == Progress.Status.PENDING
				|| Progress.status() == Progress.Status.CREATED)
				Progress.cancel(ex.getMessage());
			else
				Progress.message(ex.getMessage());
		} catch (Throwable ex)
		{
			if (Progress.status() == Progress.Status.PENDING
				|| Progress.status() == Progress.Status.CREATED)
				Progress.cancel(ex.getMessage());
			else
				Progress.message(ex.getMessage());
			logger.error(ex.getMessage(), ex);
		} finally
		{
			if (request.isAsyncStarted())
				request.getAsyncContext().complete();
			Progress.dispose();
		}
	}

	public void setCorsHeaders(HttpServletRequest request, HttpServletResponse response)
	{
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
	}
}
