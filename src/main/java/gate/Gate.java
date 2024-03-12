package gate;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.base.Screen;
import gate.catcher.Catcher;
import gate.entity.User;
import gate.error.AppException;
import gate.error.AuthenticationException;
import gate.error.AuthenticatorException;
import gate.error.BadRequestException;
import gate.error.DefaultPasswordException;
import gate.error.ForbiddenException;
import gate.error.HierarchyException;
import gate.error.InvalidPasswordException;
import gate.error.InvalidUsernameException;
import gate.error.UnauthorizedException;
import gate.event.LoginEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.ScreenServletRequest;
import gate.io.Credentials;
import gate.util.Toolkit;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@MultipartConfig
@WebServlet(value = "/Gate/*", asyncSupported = true)
public class Gate extends HttpServlet
{

	static final String HTML = "/views/Gate.html";
	private static final long serialVersionUID = 1L;

	@Inject
	Logger logger;

	@Inject
	GateControl control;

	@Inject
	Event<LoginEvent> event;

	@Any
	@Inject
	Instance<Handler> handlers;

	@Any
	@Inject
	Instance<Catcher> catchers;

	@Inject
	@Current
	private Authenticator authenticator;

	@Inject
	@ConfigProperty(name = "gate.developer")
	Optional<String> developer;

	static
	{
		Locale.setDefault(new Locale("pt", "BR"));
	}

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			Request.set(httpServletRequest);
			httpServletRequest.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setLocale(Locale.getDefault());
			ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

			String MODULE = request.getParameter("MODULE");
			String SCREEN = request.getParameter("SCREEN");
			String ACTION = request.getParameter("ACTION");
			if (Toolkit.isEmpty(MODULE, SCREEN, ACTION) && httpServletRequest.getPathInfo() != null)
			{
				List<String> path = Toolkit.parsePath(httpServletRequest.getPathInfo());
				MODULE = path.size() >= 1 ? path.get(0) : null;
				SCREEN = path.size() >= 2 ? path.get(1) : null;
				ACTION = path.size() >= 3 ? path.get(2) : null;
			}

			request.setAttribute("ACTION", ACTION);
			request.setAttribute("MODULE", MODULE);
			request.setAttribute("SCREEN", SCREEN);

			if (Toolkit.isEmpty(MODULE, SCREEN, ACTION))
			{
				if (request.getSession(false) != null)
				{
					request.getSession().invalidate();
					String logoutUri = authenticator.logoutUri(request);
					if (logoutUri != null)
					{
						response.sendRedirect(logoutUri);
						return;
					}
				}

				String provider = authenticator
					.provider(request, response);
				if (provider != null)
					response.sendRedirect(provider);
				else
					handlers.select(HTMLCommandHandler.class).get()
						.handle(httpServletRequest, response, HTML);
			} else
			{
				User user = null;
				Call call = Call.of(MODULE, SCREEN, ACTION);

				if (Credentials.isPresent(httpServletRequest))
				{
					user = Credentials.of(request).orElseThrow();
					request.setAttribute(User.class.getName(), user);
				} else if (request.getSession(false) != null
					&& request.getSession()
						.getAttribute(User.class.getName()) != null)
				{
					user = (User) request.getSession()
						.getAttribute(User.class.getName());
				} else if (!call.isPublic())
				{
					user = authenticator.authenticate(request, response);
					if (user == null && developer.isPresent())
						user = control.select(developer.get());
					if (user != null)
					{
						event.fireAsync(new LoginEvent(user));
						request.getSession().setAttribute(User.class.getName(), user);
					}
				}

				if (!call.checkAccess(user))
					if (user != null)
						throw new ForbiddenException();
					else
						throw new UnauthorizedException();

				Screen screen = CDI.current().select(call.getType()).get();
				request.setAttribute("screen", screen);
				request.setAttribute("action", call.getMethod());
				screen.prepare(request, response);

				if (call.getMethod().isAnnotationPresent(Cors.class))
				{
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Credentials", "true");
					response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
					response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
					response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
				}

				if (call.getMethod().isAnnotationPresent(Asynchronous.class))
					executeAsync(user, httpServletRequest, response, screen, call.getMethod());
				else
					execute(httpServletRequest, response, screen, call.getMethod());
			}

		} catch (DefaultPasswordException
			| InvalidUsernameException
			| InvalidPasswordException ex)
		{
			httpServletRequest.setAttribute("messages",
				Collections.singletonList(ex.getMessage()));
			Handler handler = handlers.select(HTMLCommandHandler.class).get();
			handler.handle(httpServletRequest, response, HTML);
		} catch (AuthenticationException
			| AuthenticatorException
			| BadRequestException
			| ForbiddenException
			| HierarchyException
			| UnauthorizedException
			| IOException ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(httpServletRequest, response, ex);
		} catch (RuntimeException ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(httpServletRequest, response, ex);
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
		} catch (Throwable ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(request, response, ex);
		}
	}

	private void executeAsync(User user, HttpServletRequest request,
		HttpServletResponse response, Screen screen, Method method)
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/event-stream");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");

		AsyncContext asyncContext
			= request.startAsync(request, response);
		asyncContext.setTimeout(0);

		asyncContext.start(() ->
		{
			Request.set(request);
			Progress progress = null;
			try (Writer writer = response.getWriter())
			{
				progress = Progress.create(user, writer);
				Object result = screen.execute(method);
				var type = method.isAnnotationPresent(gate.annotation.Handler.class)
					? method.getAnnotation(gate.annotation.Handler.class).value()
					: Handler.getHandler(result.getClass());
				var handler = handlers.select(type).get();
				handler.handle(request, response,
					progress, result);
				progress.close();
			} catch (AppException ex)
			{
				if (progress != null)
					progress.abort(ex.getMessage());
			} catch (Throwable ex)
			{
				if (progress != null)
					progress.abort(ex.getMessage());
				logger.error(ex.getMessage(), ex);
			} finally
			{
				asyncContext.complete();
			}
		});
	}
}
