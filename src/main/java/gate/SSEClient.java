package gate;

import gate.entity.User;
import gate.event.AppEvent;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class SSEClient implements Heartbeat
{
    private final User subject;
    private ServletOutputStream out;
    private final AsyncContext asyncContext;

    SSEClient(User user, AsyncContext asyncContext)
    {
        this.subject = user;
        this.asyncContext = asyncContext;
    }

    synchronized boolean send(AppEvent event)
    {
        if (event.checkAccess(subject))
        {
            try
            {
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
            } catch (IOException | RuntimeException ex)
            {
                asyncContext.complete();
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized boolean heartbeat()
    {
        try
        {
            if (out == null)
                out = asyncContext.getResponse().getOutputStream();
            out.println(": heartbeat");
            out.println();
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
        try
        {
            if (out != null)
                out.close();
        } catch (Exception ignored)
        {
        }
    }
}
