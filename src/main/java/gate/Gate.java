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
import gate.event.LoginEvent;
import gate.handler.HTMLCommandHandler;
import gate.handler.Handler;
import gate.http.ScreenServletRequest;
import gate.security.Credentials;
import gate.type.TempFile;
import gate.util.Toolkit;
import jakarta.enterprise.context.RequestScoped;
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
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@MultipartConfig
@WebServlet(value = "/Gate/*", asyncSupported = true)
public class Gate extends HttpServlet
{

    public static final String SUBJECT_COOKIE = "subject";
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
    @RequestScoped
    Authenticator authenticator;

    @Inject
    Call mainAction;

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    ThreadContext threadContext;

    @Inject
    Credentials credentials;

    @Inject
    @Current
    @RequestScoped
    Instance<User> userInstance;

    static
    {
        Locale.setDefault(new Locale("pt", "BR"));
    }

    @Override
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException
    {
        ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

        try
        {
            User user = userInstance.get();

            httpServletRequest.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setLocale(Locale.getDefault());

            String MODULE = request.getParameter("MODULE");
            String SCREEN = request.getParameter("SCREEN");
            String ACTION = request.getParameter("ACTION");
            if (Toolkit.isEmpty(MODULE, SCREEN, ACTION) && httpServletRequest.getPathInfo() != null)
            {
                List<String> path = Toolkit.parsePath(httpServletRequest.getPathInfo());
                MODULE = !path.isEmpty() ? path.get(0) : null;
                SCREEN = path.size() >= 2 ? path.get(1) : null;
                ACTION = path.size() >= 3 ? path.get(2) : null;
            }

            request.setAttribute("ACTION", ACTION);
            request.setAttribute("MODULE", MODULE);
            request.setAttribute("SCREEN", SCREEN);

            if (Toolkit.isEmpty(MODULE, SCREEN, ACTION)
                    && !authenticator.hasCredentials(request))
            {
                String provider = authenticator.provider(request, response);
                if (provider != null)
                    response.sendRedirect(provider);
                else
                    handlers.select(HTMLCommandHandler.class).get().handle(httpServletRequest, response, HTML);
                return;
            }

            if (authenticator.hasCredentials(request))
            {
                user = authenticator.authenticate(request, response);
                if (user != null)
                {
                    event.fireAsync(new LoginEvent(user));
                    response.addCookie(CookieFactory.create(SUBJECT_COOKIE, credentials.subject(user)));
                }
                request.setAttribute(User.class.getName(), user);

                if (Toolkit.isEmpty(MODULE, SCREEN, ACTION))
                {
                    if (mainAction == Call.NONE)
                        throw new InternalServerException("No main action defined");
                    response.sendRedirect(mainAction.toString());
                    return;
                }
            }

            Call call = Call.of(MODULE, SCREEN, ACTION);
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
            {
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
            }

            if (call.getMethod().isAnnotationPresent(Asynchronous.class))
                executeAsync(request, response, screen, call.getMethod());
            else
                execute(httpServletRequest, response, screen, call.getMethod());

        } catch (AuthenticationException ex)
        {
            if (authenticator.provider(request, response) != null)
            {
                var type = Catcher.getCatcher(ex.getClass());
                Catcher catcher = catchers.select(type).get();
                catcher.catches(httpServletRequest, response, ex);
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

    private void executeAsync(ScreenServletRequest request, HttpServletResponse response,
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

        Runnable contextualTask = threadContext.contextualRunnable(() ->
        {
            try (Writer writer = response.getWriter())
            {
                Progress progress = Progress.create(writer);
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
                Progress.finish();
                TempFile.cleanup();
                asyncContext.complete();
            }
        });

        asyncContext.start(contextualTask);
    }
}
