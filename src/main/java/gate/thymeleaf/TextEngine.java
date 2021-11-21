package gate.thymeleaf;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import ognl.OgnlRuntime;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public interface TextEngine
{

	public void process(String string, IContext context, Writer writer);

	public String process(String string, IContext context);

	public String process(IModel imodel, IContext context);

	@ApplicationScoped
	public static class TextEngineImpl implements TextEngine
	{

		private final TemplateEngine templateEngine;

		@Inject
		public TextEngineImpl(GateDialect dialect)
		{
			OgnlRuntime.setSecurityManager(null);
			this.templateEngine = new TemplateEngine();
			templateEngine.addDialect(dialect);
			templateEngine.setTemplateResolver(new StringTemplateResolver());
		}

		@Override
		public void process(String string, IContext context, Writer writer)
		{
			templateEngine.process(string, context, writer);
		}

		@Override
		public String process(String string, IContext context)
		{
			return templateEngine.process(string, context);
		}

		@Override
		public String process(IModel imodel, IContext context)
		{
			try ( StringWriter writer = new StringWriter())
			{
				imodel.write(writer);
				writer.flush();
				return templateEngine.process(writer.toString(), context);
			} catch (IOException ex)
			{
				throw new UncheckedIOException(ex);
			}
		}

	}

}
