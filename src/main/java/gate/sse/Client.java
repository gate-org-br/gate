package gate.sse;

import gate.entity.User;
import gate.event.AppEvent;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Client
{

	private final User subject;
	private ServletOutputStream out;
	private final AsyncContext asyncContext;

	public Client(User user, AsyncContext asyncContext)
	{
		this.subject = user;
		this.asyncContext = asyncContext;
	}

	public synchronized void ping()
	{
		try
		{
			if (out == null)
			{
				out = asyncContext.getResponse().getOutputStream();
			}
			out.print("event: ping\n");
			out.print("data: ok\n");
			out.print("\n");
			out.flush();
			asyncContext.getResponse().flushBuffer();
		} catch (IOException | RuntimeException ex)
		{
			try
			{
				asyncContext.complete();
			} catch (IllegalStateException ignored)
			{
			}
		}
	}

	public synchronized void send(AppEvent event)
	{
		if (event.checkAccess(subject))
		{
			try
			{
				if (out == null)
				{
					out = asyncContext.getResponse().getOutputStream();
				}
				java.lang.String data = Base64.getEncoder().encodeToString(event.toString().getBytes(StandardCharsets.UTF_8));
				out.print("event: message\n");
				out.print("data: " + data + "\n");
				out.print("\n");
				out.flush();
				asyncContext.getResponse().flushBuffer();
			} catch (IOException | RuntimeException ex)
			{
				try
				{
					asyncContext.complete();
				} catch (IllegalStateException ignored)
				{
				}
			}
		}
	}

	public void close()
	{
		try
		{
			if (out != null)
				out.close();
		} catch (IOException ignored)
		{
		}
	}

}
