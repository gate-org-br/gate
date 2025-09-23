package gate;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.microprofile.context.ThreadContext;
import org.slf4j.Logger;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.base.Screen;
import gate.catcher.Catcher;
import gate.entity.User;
import gate.error.AppException;
import gate.error.AuthenticationException;
import gate.error.BadRequestException;
import gate.error.ForbiddenException;
import gate.error.UnauthorizedException;
import gate.event.AppEvent;
import gate.event.LoginEvent;
import gate.event.LogoffEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.security.Credentials;
import gate.type.RequestCommand;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet(value = "/Gate/*", asyncSupported = true)
public class Gate extends HttpServlet
{

	static final String HTML = "/views/Gate.html";

	@Inject
	Logger logger;

	@Inject
	Event<AppEvent> event;

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
	Call mainAction;

	@Inject
	ThreadContext threadContext;

	@Inject
	Credentials credentials;

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	GateControl control;

	static
	{
		Locale.setDefault(Locale.of("pt", "BR"));
	}

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
		throws ServletException, IOException
	{
		httpServletResponse.addHeader("Vary", "X-G-Fragment");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);

		try
		{
			User user = userInstance.get();

			httpServletRequest.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setLocale(Locale.getDefault());

			var command = request.getCommand();
			request.setAttribute("MODULE", command.module());
			request.setAttribute("SCREEN", command.screen());
			request.setAttribute("ACTION", command.action());
			request.setAttribute("METHOD", request.getMethod());

			if (command.equals(RequestCommand.DEFAULT)
				&& (mainAction == Call.NONE || !authenticator.hasCredentials(request)))
			{
				if (user.getId() != null)
				{
					event.fire(new LogoffEvent(user));
					control.update(user, LocalDateTime.now(ZoneOffset.UTC));
					response.deleteSubjectCookie();
					String logoutUri = authenticator.logoutUri(request);
					if (logoutUri != null)
					{
						response.sendRedirect(logoutUri);
						return;
					}
				}

				String provider = authenticator.provider(request, response);
				if (provider != null)
					response.sendRedirect(provider);
				else
					handlers.select(HTMLCommandHandler.class).get().handle(httpServletRequest,
						response, HTML);
				return;
			}

			Call call = command.equals(RequestCommand.DEFAULT)
				? mainAction : Call.of(command);

			if (!call.checkMethod(request.getMethod()))
				throw new BadRequestException();

			if (authenticator.hasCredentials(request))
			{
				user = authenticator.authenticate(request, response);
				if (user != null)
				{
					event.fireAsync(new LoginEvent(user));
					var token = Credentials.SubjectToken.create(user.getId());
					control.update(user, token.iat());
					response.createSubjectCookie(credentials.fromToken(token));
				}
				request.setAttribute(User.class.getName(), user);
			}

			if (!call.checkAccess(user))
				if (user != null && user.getId() != null)
					throw new ForbiddenException();
				else
					throw new UnauthorizedException();

			Screen screen = CDI.current().select(call.type()).get();
			request.setAttribute("screen", screen);
			request.setAttribute("action", call.method());
			screen.prepare(request, response);

			if (call.method().isAnnotationPresent(Cors.class))
				response.enableCors(request.getHeader("Origin"));

			if (call.method().isAnnotationPresent(Asynchronous.class))
				executeAsync(user, request, response, screen, call.method());
			else
				execute(httpServletRequest, response, screen, call.method());

		} catch (AuthenticationException ex)
		{
			if (authenticator.provider(request, response) != null)
			{
				var type = Catcher.getCatcher(ex.getClass());
				Catcher catcher = catchers.select(type).get();
				catcher.catches(httpServletRequest, response, ex);
			} else
			{
				httpServletRequest.setAttribute("messages",
					Collections.singletonList(ex.getMessage()));
				httpServletRequest.setAttribute("exception", ex);
				Handler handler = handlers.select(HTMLCommandHandler.class).get();
				handler.handle(httpServletRequest, response, HTML);
			}
		} catch (RuntimeException ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(httpServletRequest, response, ex);
		}
	}

	private void execute(HttpServletRequest request, HttpServletResponse response, Screen screen,
		Method method)
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

	private void executeAsync(User user, ScreenServletRequest request, HttpServletResponse response,
		Screen screen, Method method)
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/event-stream");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");

		AsyncContext asyncContext = request.startAsync(request, response);
		asyncContext.setTimeout(0);

		Runnable contextualTask = threadContext.contextualRunnable(() ->
		{
			try (Writer writer = response.getWriter())
			{
				Progress progress = Progress.create(user, writer);
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
					progress.close();
				} catch (AppException ex)
				{
					progress.abort(ex.getMessage());
				} catch (Throwable ex)
				{
					progress.abort(ex.getMessage());
					logger.error(ex.getMessage(), ex);
				}
			} catch (IOException ex)
			{
				logger.error(ex.getMessage(), ex);
			} finally
			{
				asyncContext.complete();
			}
		});

		asyncContext.start(contextualTask);
	}
}
