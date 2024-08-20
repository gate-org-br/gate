package gate.thymeleaf;

import gate.thymeleaf.processors.attribute.property.NotAttributeProcessor;
import gate.thymeleaf.processors.attribute.property.SetAttributeProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Writer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public interface HTMLFileEngine
{

	public void process(String filename, IContext context, Writer writer);

	public String process(String filename, IContext context);

	@ApplicationScoped
	public static class FileEngineImpl implements HTMLFileEngine
	{

		private final TemplateEngine templateEngine;

		@Inject
		public FileEngineImpl(GateDialect dialect,
			SetAttributeProcessor set,
			NotAttributeProcessor not)
		{

			this.templateEngine = new TemplateEngine();

			templateEngine.addDialect(dialect);
			templateEngine.addDialect(set);
			templateEngine.addDialect(not);
			var resolver = new ClassLoaderTemplateResolver();
			resolver.setTemplateMode(TemplateMode.HTML);

			templateEngine.setTemplateResolver(resolver);
		}

		@Override
		public void process(String filename, IContext context, Writer writer)
		{
			templateEngine.process(filename, context, writer);
		}

		@Override
		public String process(String filename, IContext context)
		{
			return templateEngine.process(filename, context);
		}
	}
}
