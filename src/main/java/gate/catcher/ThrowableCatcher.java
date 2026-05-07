package gate.catcher;

import gate.annotation.Current;
import gate.entity.User;
import gate.error.UnauthorizedException;
import gate.http.ScreenServletRequest;
import gate.http.ScreenServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

@ApplicationScoped
public class ThrowableCatcher implements Catcher
{

	@Inject
	@Current
	Instance<User> userInstance;

	@Inject
	Logger logger;

	@Inject
	UnauthorizedExceptionCatcher unauthorizedExceptionCatcher;

	@Override
	public void catches(ScreenServletRequest request,
	                    ScreenServletResponse response, Throwable exception)
	{

		if (exception == null)
			return;

		for (Throwable current = exception; current != null; current = current.getCause())
			if (current instanceof UnauthorizedException)
			{
				unauthorizedExceptionCatcher.catches(request, response, current);
				return;
			}

		response.setStatus(500);
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");

		try (PrintWriter writer = response.getWriter())
		{
				if (userInstance.get().isSuperUser())
				exception.printStackTrace(writer);
			else
				writer.write("Erro de sistema: procure o suporte para informar o ocorrido");
			writer.flush();
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		} finally
		{
			logger.error(exception.getMessage(), exception);
		}
	}
}
