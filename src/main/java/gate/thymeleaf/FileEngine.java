package gate.thymeleaf;

import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public interface FileEngine
{

	public void process(String filename, IContext context, Writer writer);

	public String process(String filename, IContext context);

	@ApplicationScoped
	public static class FileEngineImpl implements FileEngine
	{

		private final TemplateEngine templateEngine;

		@Inject
		public FileEngineImpl(GateDialect dialect)
		{

			this.templateEngine = new TemplateEngine();

			templateEngine.addDialect(dialect);
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
