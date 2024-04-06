package gate.catcher;

import gate.annotation.Current;
import gate.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import org.slf4j.Logger;

@ApplicationScoped
public class ThrowableCatcher implements Catcher
{

	@Inject
	@Current
	@RequestScoped
	User user;

	@Inject
	Logger logger;

	@Override
	public void catches(HttpServletRequest request,
		HttpServletResponse response, Throwable exception)
	{
		response.setStatus(500);
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");

		try (PrintWriter writer = response.getWriter())
		{
			if (user != null && user.isSuperUser())
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
