package gate.adapter.handler;

import gate.Progress;
import gate.http.StringServletResponse;
import gate.thymeleaf.CDIWebContext;
import gate.thymeleaf.JavaScriptFileEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.IContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

@ApplicationScoped
public class JavaScriptHandler implements Handler
{

	@Inject
	JavaScriptFileEngine engine;

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
			response.setContentType("application/javascript");
			IContext context = new CDIWebContext(request.getLocale(),
					jakartaServletWebApplication.buildExchange(request, response), beanManager);
			engine.process(filename, context, writer);
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
		progress.result("application/javascript", null, captured.toString());
	}
}