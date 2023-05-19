package gate.catcher;

import gate.annotation.Current;
import gate.entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;

@ApplicationScoped
public class ThrowableCatcher implements Catcher
{

	@Inject
	@Current
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
