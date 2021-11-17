package gate.handler;

import gate.thymeleaf.CDIWebContext;
import gate.thymeleaf.FileEngine;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.IContext;

@ApplicationScoped
public class HTMLCommandHandler implements Handler
{

	@Inject
	FileEngine engine;

	@Inject
	private BeanManager beanManager;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			String filename = value.toString();
			Writer writer = response.getWriter();
			IContext context = new CDIWebContext(request, response,
				beanManager, request.getServletContext());
			engine.process(filename, context, writer);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

}
