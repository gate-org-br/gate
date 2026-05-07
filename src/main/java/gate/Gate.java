package gate;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.base.Screen;
import gate.catcher.Catcher;
import gate.entity.User;
import gate.error.*;
import gate.event.AppEvent;
import gate.event.EventClient;
import gate.event.LoginEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.RequestCommand;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.security.Credentials;
import gate.type.TempFile;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.context.ThreadContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;
import java.util.Locale;

@MultipartConfig
@WebServlet(value = "/Gate/*", asyncSupported = true)
public class Gate extends HttpServlet
{
	static final String HTML = "/views/Gate.html";

	@Any
	@Inject
	Instance<Handler> handlers;

	@Any
	@Inject
	Instance<Catcher> catchers;

	@Inject
	@Current
	Authenticator authenticator;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	Logger logger;
	@Inject
	Call mainAction;
	@Inject
	Event<AppEvent> event;
	@Inject
	Credentials credentials;
	@Inject
	ThreadContext threadContext;
	@Inject
	HTMLCommandHandler htmlCommandHandler;

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException
	{
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);

		try
		{
			httpServletRequest.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setLocale(Locale.getDefault());

			var command = request.getCommand();
			request.setAttribute("MODULE", command.module());
			request.setAttribute("SCREEN", command.screen());
			request.setAttribute("ACTION", command.action());

			if (authenticate(request, response, command))
				return;

			User user = userInstance.get();
			Call call = Call.of(command.module(), command.screen(), command.action());
			if (!call.checkAccess(user))
				if (user != null && user.getId() != null)
					throw new ForbiddenException();
				else
					throw new UnauthorizedException();

			Screen screen = CDI.current().select(call.getType()).get();
			request.setAttribute("screen", screen);
			request.setAttribute("action", call.getMethod());
			screen.prepare(request, response);

			if (call.getMethod().isAnnotationPresent(Cors.class))
				response.enableCors(request.getHeader("Origin"));

			if (call.getMethod().isAnnotationPresent(Asynchronous.class))
				executeAsync(user, request, response, screen, call.getMethod());
			else
				execute(user, request, response, screen, call.getMethod());

		} catch (AuthenticationException ex)
		{
			if (authenticator.provider(request, response) != null)
			{
				var type = Catcher.getCatcher(ex.getClass());
				Catcher catcher = catchers.select(type).get();
				catcher.catches(request, response, ex);
			} else
			{
				httpServletRequest.setAttribute("messages", Collections.singletonList(ex.getMessage()));
				httpServletRequest.setAttribute("exception", ex);
				htmlCommandHandler.handle(httpServletRequest, response, HTML);
			}
		} catch (RuntimeException ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(request, response, ex);
		}
	}

	private boolean authenticate(ScreenServletRequest request,
	                             ScreenServletResponse response, RequestCommand command)
			throws IOException
	{
		if (authenticator.hasCredentials(request))
		{
			User user = authenticator.authenticate(request, response);
			if (user != null)
			{
				event.fireAsync(new LoginEvent(user));
				var subject = new Credentials.Subject(user.getId(), Instant.now());
				response.createSessionCookie(request, credentials.createToken(subject));
			}
			request.setAttribute(User.class.getName(), user);

			if (command.isEmpty())
			{
				if (mainAction == Call.NONE)
					throw new InternalServerException("No main action defined");
				response.sendRedirect(mainAction.toString());
				return true;
			}
		} else if (command.isEmpty())
		{
			String provider = authenticator.provider(request, response);
			if (provider != null)
				response.sendRedirect(provider);
			else
				htmlCommandHandler.handle(request, response, HTML);
			return true;
		}
		return false;
	}

	private void execute(User user, ScreenServletRequest request, ScreenServletResponse response, Screen screen,
	                     Method method)
	{
		GateContext.init(new GateContext(user));
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
		} finally
		{
			GateContext.close();
		}
	}

	private void executeAsync(User user, ScreenServletRequest request, HttpServletResponse response,
	                          Screen screen, Method method)
	{
		response.setContentLengthLong(-1);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/event-stream");
		response.setHeader("X-Accel-Buffering", "no");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Transfer-Encoding", "chunked");

		AsyncContext asyncContext = request.startAsync(request, response);
		asyncContext.setTimeout(0);
		asyncContext.start(threadContext.contextualRunnable(() ->
		{
			GateContext.init(new GateContext(user));
			try (var progress = Progress.create(user, new EventClient(user, asyncContext)))
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
						handler.handle(request, response, progress, result);
					}
				} catch (AppException ex)
				{
					progress.abort(ex.getMessage());
				} catch (Throwable ex)
				{
					progress.abort(ex.getMessage());
					throw ex;
				} finally
				{
					TempFile.cleanup();
				}
			} catch (Throwable ex) {logger.error(ex.getMessage(), ex);} finally {GateContext.close();}
		}));
	}
}