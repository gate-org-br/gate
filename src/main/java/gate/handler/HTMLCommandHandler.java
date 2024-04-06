package gate.handler;

import gate.Progress;
import gate.thymeleaf.CDIWebContext;
import gate.thymeleaf.FileEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import org.thymeleaf.context.IContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@ApplicationScoped
public class HTMLCommandHandler implements Handler
{

	@Inject
	FileEngine engine;

	@Inject
	private BeanManager beanManager;

	@Inject
	JakartaServletWebApplication jakartaServletWebApplication;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object value)
	{
		try
		{
			String filename = value.toString();
			Writer writer = response.getWriter();
			response.setContentType("text/html");
			IContext context = new CDIWebContext(request.getLocale(), jakartaServletWebApplication.buildExchange(request, response), beanManager);
			engine.process(filename, context, writer);
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		Progress progress, Object value)
	{

		try (StringWriter writer = new StringWriter())
		{
			String filename = value.toString();
			IContext context = new CDIWebContext(request.getLocale(), jakartaServletWebApplication.buildExchange(request, response), beanManager);
			engine.process(filename, context, writer);
			progress.result("text/html", null, writer.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
