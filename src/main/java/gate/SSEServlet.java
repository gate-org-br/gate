package gate;

import gate.annotation.Current;
import gate.catcher.UnauthorizedExceptionCatcher;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.http.ScreenServletRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/SSE", asyncSupported = true)
public class SSEServlet extends HttpServlet
{
    @Inject
    UnauthorizedExceptionCatcher catcher;

    @Inject
    @Current
    @RequestScoped
    Instance<User> userInstance;

    @Inject
    SSEClients clients;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException
    {
        response.addHeader("Cache-Control", "no-cache");
        ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

        try
        {
            User user = userInstance.get();
            if (user == null || user.getId() == null)
                throw new UnauthorizedException();

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/event-stream");

            AsyncContext context = request.startAsync();
            context.setTimeout(TimeUnit.HOURS.toMillis(1));

            SSEClient client = new SSEClient(user.unwrap(), context);
            clients.add(client);

            context.addListener(new AsyncListener()
            {
                @Override
                public void onStartAsync(AsyncEvent event)
                {
                }

                @Override
                public void onComplete(AsyncEvent event)
                {
                    clients.remove(client);
                }

                @Override
                public void onError(AsyncEvent event)
                {
                    onComplete(event);
                }

                @Override
                public void onTimeout(AsyncEvent event)
                {
                    onComplete(event);
                }
            });
        } catch (UnauthorizedException ex)
        {
            catcher.catches(request, response, ex);
        }
    }
}