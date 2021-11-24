package gate;

import gate.annotation.Asynchronous;
import gate.annotation.Cors;
import gate.annotation.Current;
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
import gate.event.LoginEvent;
import gate.handler.Handler;
import gate.io.Credentials;
import gate.type.Result;
import gate.util.ScreenServletRequest;
import gate.util.Toolkit;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
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
import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundLiteral;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.manager.api.WeldManager;
import org.slf4j.Logger;

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
	private GateControl control;

	@Resource
	private ManagedThreadFactory managedThreadFactory;

	@Inject
	private Logger logger;

	@Inject
	private Event<LoginEvent> event;

	@Any
	@Inject
	Instance<Screen> screens;

	@Any
	@Inject
	Instance<Handler> handlers;

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

			User user = Credentials.of(request).orElseGet(() -> (User) request.getSession().getAttribute(User.class.getName()));

			if (Toolkit.isEmpty(MODULE, SCREEN, ACTION))

			{
				if (request.getSession(false) != null)
					request.getSession().invalidate();
				getServletContext().getRequestDispatcher(GATE_JSP).forward(request, response);
			} else
			{
				String username = request.getParameter("$username");
				String password = request.getParameter("$password");

				if (Toolkit.notEmpty(username, password))
				{
					request.getSession().setAttribute(User.class.getName(),
						user = control.select(org, username, password));
					event.fireAsync(new LoginEvent(user));
				} else if (httpServletRequest.getUserPrincipal() != null
					&& !Toolkit.isEmpty(httpServletRequest.getUserPrincipal().getName()))
					request.getSession().setAttribute(User.class.getName(), user = control.select(httpServletRequest.getUserPrincipal().getName()));

				Command command = Command.of(MODULE, SCREEN, ACTION);
				if (!command.checkAccess(user))
					throw new AccessDeniedException();

				Screen screen = screens.select(command.getType()).get();
				request.setAttribute("screen", screen);
				request.setAttribute("action", command.getMethod());
				screen.prepare(request, response);

				if (command.getMethod().isAnnotationPresent(Cors.class))
				{
					response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
					response.setHeader("Access-Control-Allow-Credentials", "true");
					response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
				}

				if (command.getMethod().isAnnotationPresent(Asynchronous.class))
					executeAsynchronous(httpServletRequest, response, user, screen, command.getMethod());
				else
					execute(httpServletRequest, response, screen, command.getMethod());
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
			logger.error(ex.getCause().getMessage(), ex.getCause());
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (DuplicateException | InvalidCircularRelationException | NotFoundException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Banco de dados inconsistente"));
			httpServletRequest.setAttribute("exception", ex);
			logger.error(ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (IOException | IllegalAccessException | ServletException | RuntimeException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Erro de sistema"));
			httpServletRequest.setAttribute("exception", ex);
			logger.error(ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		} catch (InvocationTargetException ex)
		{
			if (ex.getCause() instanceof AppException)
			{
				handlers.select(Handler.getHandler(AppException.class)).get()
					.handle(httpServletRequest, response, ex.getCause());
			} else
			{
				httpServletRequest.setAttribute("messages", Collections.singletonList("Erro de sistema"));
				httpServletRequest.setAttribute("exception", ex.getTargetException());
				logger.error(ex.getCause().getMessage(), ex.getCause());
				httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
			}
		} catch (InvalidCredentialsException ex)
		{
			handlers.select(Handler.getHandler(Result.class)).get()
				.handle(httpServletRequest, response, ex.getCause());
		} catch (ReflectiveOperationException ex)
		{
			httpServletRequest.setAttribute("messages", Collections.singletonList("Erro de sistema"));
			httpServletRequest.setAttribute("exception", ex);
			logger.error(ex.getMessage(), ex);
			httpServletRequest.getRequestDispatcher(GATE_JSP).forward(httpServletRequest, response);
		}
	}

	private void execute(HttpServletRequest request,
		HttpServletResponse response, Screen screen, Method method)
		throws RuntimeException, IllegalAccessException, InvocationTargetException,
		ReflectiveOperationException
	{

		try
		{
			Object result = screen.execute(method);
			if (result != null)
				if (method.isAnnotationPresent(gate.annotation.Handler.class))
					handlers.select(method.getAnnotation(gate.annotation.Handler.class).value())
						.get().handle(request, response, result);
				else
					handlers.select(Handler.getHandler(result.getClass())).get().handle(request, response, result);
		} catch (InvocationTargetException ex)
		{
			if (ex.getTargetException() instanceof AppException)
				gate.annotation.Catcher.Extractor.extract(method)
					.getConstructor().newInstance()
					.execute(request, response, (AppException) ex.getTargetException());
			else
				throw ex;
		}
	}

	private void executeAsynchronous(HttpServletRequest request,
		HttpServletResponse response, User user, Screen screen, Method method)
	{
		Progress progress = Progress.create(org, app, user);
		request.setAttribute("process", progress.getProcess());
		handlers.select(Handler.getHandler(Integer.class)).get().handle(request, response, progress.getProcess());

		Map<Class<? extends Annotation>, Collection<ContextualInstance<?>>> scopeToContextualInstances = new HashMap<>();
		CDI.current().select(WeldManager.class).get().getActiveWeldAlterableContexts()
			.forEach(context -> scopeToContextualInstances.put(context.getScope(), context.getAllContextualInstances()));

		managedThreadFactory.newThread(() ->
		{
			Progress.bind(progress);
			try
			{
				Thread.sleep(1000);

				WeldManager weldManager = CDI.current().select(WeldManager.class).get();
				BoundRequestContext requestContext = weldManager.instance().select(BoundRequestContext.class, BoundLiteral.INSTANCE).get();
				BoundSessionContext sessionContext = weldManager.instance().select(BoundSessionContext.class, BoundLiteral.INSTANCE).get();
				BoundConversationContext conversationContext = weldManager.instance().select(BoundConversationContext.class, BoundLiteral.INSTANCE).get();

				Map<String, Object> sessionMap = new HashMap<>();
				Map<String, Object> requestMap = new HashMap<>();
				BoundRequest boundRequest = new MutableBoundRequest(requestMap, sessionMap);

				requestContext.associate(requestMap);
				requestContext.activate();
				sessionContext.associate(sessionMap);
				sessionContext.activate();
				conversationContext.associate(boundRequest);
				conversationContext.activate();

				if (scopeToContextualInstances.get(requestContext.getScope()) != null)
					requestContext.clearAndSet(scopeToContextualInstances.get(requestContext.getScope()));
				if (scopeToContextualInstances.get(sessionContext.getScope()) != null)
					sessionContext.clearAndSet(scopeToContextualInstances.get(sessionContext.getScope()));
				if (scopeToContextualInstances.get(conversationContext.getScope()) != null)
					conversationContext.clearAndSet(scopeToContextualInstances.get(conversationContext.getScope()));

				Object url = screen.execute(method);

				requestContext.deactivate();
				conversationContext.deactivate();
				sessionContext.deactivate();

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
				logger.error(ex.getMessage(), ex);
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

	}
}
