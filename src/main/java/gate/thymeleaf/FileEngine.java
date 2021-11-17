package gate.thymeleaf;

import java.io.Writer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public interface FileEngine
{

	public void process(String filename, IContext context, Writer writer);

	public String process(String filename, IContext context);

	@ApplicationScoped
	public static class FileEngineImpl implements FileEngine
	{

		private final SpringTemplateEngine templateEngine;

		@Inject
		public FileEngineImpl(GateDialect dialect)
		{

			this.templateEngine = new SpringTemplateEngine();

			templateEngine.addDialect(dialect);
			var resolver = new ClassLoaderTemplateResolver();
			resolver.setTemplateMode(TemplateMode.HTML);
			templateEngine.setTemplateResolver(resolver);
			templateEngine.setEnableSpringELCompiler(true);
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
