package gate;

import gate.adapter.catcher.Catcher;
import gate.adapter.handler.HTMLCommandHandler;
import gate.adapter.handler.Handler;
import gate.annotation.Current;
import gate.authenticator.Authenticator;
import gate.base.Screen;
import gate.catalog.SessionCatalog;
import gate.entity.User;
import gate.error.*;
import gate.event.AppEvent;
import gate.event.LoginEvent;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import gate.i18n.CurrentLocale;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
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
import java.util.Collections;

@MultipartConfig
@WebServlet(value = "/Gate/*", asyncSupported = true)
public class Gate extends HttpServlet
{

	static final String HTML = "/views/Gate.html";

	@Inject Logger logger;
	@Inject Calls actionRegistry;
	@Inject Event<AppEvent> event;
	@Inject ThreadContext threadContext;
	@Inject SessionCatalog sessionCatalog;

	@Any
	@Inject
	Instance<Handler> handlers;

	@Any
	@Inject
	Instance<Catcher> catchers;

	@Any
	@Inject
	Instance<Screen> screens;

	@Inject
	@Current
	Authenticator authenticator;

	@Inject
	@Current
	Instance<User> userInstance;

	@Override
	public void service(HttpServletRequest httpServletRequest,
	                    HttpServletResponse httpServletResponse)
			throws IOException
	{
		httpServletResponse.addHeader("Vary", "X-G-Fragment");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);
		ScreenServletResponse response = new ScreenServletResponse(httpServletResponse);

		try
		{
			var command = request.getCommand();
			request.setAttribute("MODULE", command.module());
			request.setAttribute("SCREEN", command.screen());
			request.setAttribute("ACTION", command.action());
			request.setAttribute("METHOD", request.getMethod());

			if (command.isDefault()
			    && (actionRegistry.getMainAction() == null
			        || !authenticator.hasCredentials(request)))
			{
				String provider = authenticator.provider(request, response);
				if (provider != null)
					response.sendRedirect(provider);
				else
					handlers.select(HTMLCommandHandler.class).get().handle(httpServletRequest,
							response, HTML);
				return;
			}

			User user = userInstance.get();
			if (authenticator.hasCredentials(request))
			{
				user = authenticator.authenticate(request, response);
				if (user != null)
				{
					var session = sessionCatalog.create(user);
					response.createSessionCookie(request, session);
					event.fireAsync(new LoginEvent(user));

					if (actionRegistry.getMainAction() != null && command.isDefault())
					{
						response.sendRedirect(actionRegistry.getMainAction().command().toString());
						return;
					}
				}
				request.setAttribute(User.class.getName(), user);
			}

			Call call = actionRegistry.get(command)
					.orElseThrow(() -> new BadRequestException(command));
			if (!call.allowsHttpMethod(request.getMethod().toUpperCase()))
				throw new MethodNotAllowedException();

			if (!call.accessRule().allows(user))
				if (user != null && user.getId() != null)
					throw new ForbiddenException();
				else
					throw new UnauthorizedException();

			Screen screen = screens.select(call.screen()).get();
			request.setAttribute("screen", screen);
			request.setAttribute("action", call.method());
			screen.prepare(request, response);

			if (call.cors())
				response.enableCors(request.getHeader("Origin"));

			if (call.asynchronous())
				executeAsync(user, request, response, screen, call.method());
			else
				execute(request, response, screen, call.method());

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
				Handler handler = handlers.select(HTMLCommandHandler.class).get();
				handler.handle(httpServletRequest, response, HTML);
			}
		} catch (RuntimeException ex)
		{
			var type = Catcher.getCatcher(ex.getClass());
			Catcher catcher = catchers.select(type).get();
			catcher.catches(request, response, ex);
		}
	}


	private void execute(ScreenServletRequest request, ScreenServletResponse response, Screen screen,
	                     Method method)
	{
		CurrentLocale.set(request.getLocale());
		try
		{
			Object result = screen.execute(method);
			if (result != null)
				handlers.select(Handler.getHandler(method, result)).get()
						.handle(request, response, result);
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
			try (var progress = Progress.create(asyncContext, user))
			{
				try
				{
					Object result = screen.execute(method);
					if (result != null)
						handlers.select(Handler.getHandler(method, result)).get()
								.handle(request, progress, result);
				} catch (AppException ex)
				{
					progress.abort(ex.getMessage());
				} catch (Throwable ex)
				{
					progress.abort(ex.getMessage());
					logger.error(ex.getMessage(), ex);
				}
			}
		}));
	}
}