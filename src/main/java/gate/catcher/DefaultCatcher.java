package gate.catcher;

import gate.thymeleaf.CDIWebContext;
import gate.thymeleaf.FileEngine;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Collections;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.thymeleaf.context.IContext;

@ApplicationScoped
public class DefaultCatcher implements Catcher
{

	@Inject
	Logger logger;

	@Inject
	FileEngine engine;

	@Inject
	private BeanManager beanManager;

	@Override
	public void catches(HttpServletRequest request,
		HttpServletResponse response, Throwable exception)
	{
		try
		{
			request.setAttribute("messages",
				Collections.singletonList("Erro de sistema"));
			request.setAttribute("exception", exception);
			logger.error(exception.getMessage(), exception);

			Writer writer = response.getWriter();
			IContext context = new CDIWebContext(request, response,
				beanManager, request.getServletContext());
			engine.process("/views/Gate.html", context, writer);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
