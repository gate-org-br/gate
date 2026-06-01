package gate.adapter.handler;

import gate.Progress;
import gate.http.StringServletResponse;
import gate.thymeleaf.CDIWebContext;
import gate.thymeleaf.HTMLFileEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.IContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

@ApplicationScoped
public class HTMLCommandHandler implements Handler
{

	@Inject
	HTMLFileEngine engine;

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
			IContext context = new CDIWebContext(request.getLocale(),
					jakartaServletWebApplication.buildExchange(request, response), beanManager);
			StringWriter buffer = new StringWriter();
			engine.process(filename, context, buffer);
			response.setContentType("text/html");
			response.getWriter().write(buffer.toString());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}

	@Override
	public void handle(HttpServletRequest request,
	                   Progress progress, Object value)
	{
		var captured = new StringServletResponse();
		IContext context = new CDIWebContext(request.getLocale(),
				jakartaServletWebApplication.buildExchange(request, captured), beanManager);
		engine.process(value.toString(), context, captured.getWriter());
		progress.result("text/html", null, captured.toString());
	}
}