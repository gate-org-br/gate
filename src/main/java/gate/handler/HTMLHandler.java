package gate.handler;

import gate.thymeleaf.CDIWebContextFactory;
import gate.thymeleaf.GateDialect;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class HTMLHandler implements Handler
{

	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		handleTemplate(request, response, value);
	}

	public static void handleTemplate(HttpServletRequest request,
		HttpServletResponse response, Object value)
	{
		try
		{
			CDIWebContextFactory factory = CDI.current().select(CDIWebContextFactory.class).get();
			ServletContext servletContext = CDI.current().select(ServletContext.class).get();
			IWebContext ctx = factory.create(request, response);
			TemplateEngine engine = new TemplateEngine();
			engine.addDialect(new LayoutDialect());
			engine.addDialect(new GateDialect());
			ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(servletContext);
			resolver.setTemplateMode(TemplateMode.HTML);
			engine.setTemplateResolver(resolver);
			engine.process(value.toString(), ctx, response.getWriter());
		} catch (IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
	}
}
