package gate;

import gate.entity.User;
import gate.event.AppEvent;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class SSEClient
{
    private final User subject;
    private ServletOutputStream out;
    private final AsyncContext asyncContext;
    private boolean closed;

    SSEClient(User user, AsyncContext asyncContext)
    {
        this.subject = user;
        this.asyncContext = asyncContext;
    }

    synchronized boolean send(AppEvent event)
    {
        if (closed)
            return false;

        try
        {
            if (!event.checkAccess(subject))
                return true;

            if (out == null)
                out = asyncContext.getResponse().getOutputStream();

            var data = Base64.getEncoder()
                    .encodeToString(event.toString()
                            .getBytes(StandardCharsets.UTF_8));

            out.println("event: message");
            out.println("data: " + data);
            out.println();
            out.flush();
            asyncContext.getResponse().flushBuffer();
            return true;
        } catch (Exception ex) {return false;}
    }

    synchronized boolean heartbeat()
    {
        if (closed)
            return false;

        try
        {
            if (out == null)
                out = asyncContext.getResponse().getOutputStream();
            out.print(": heartbeat\n\n");
            out.flush();
            return true;
        } catch (Exception e)
        {
            close();
            return false;
        }
    }

    synchronized void close()
    {
        if (closed)
            return;

        closed = true;
        try
        {
            if (out != null)
                out.close();
        } catch (Exception ignored)
        {
        }
        try
        {
            asyncContext.complete();
        } catch (Exception ignored)
        {
        }
    }
}
