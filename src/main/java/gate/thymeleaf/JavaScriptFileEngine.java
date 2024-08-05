package gate.thymeleaf;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.Writer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public interface JavaScriptFileEngine
{

	public void process(String filename, IContext context, Writer writer);

	public String process(String filename, IContext context);

	@ApplicationScoped
	public static class FileEngineImpl implements JavaScriptFileEngine
	{

		private final TemplateEngine templateEngine;

		public FileEngineImpl()
		{

			this.templateEngine = new TemplateEngine();
			var resolver = new ClassLoaderTemplateResolver();
			resolver.setTemplateMode(TemplateMode.JAVASCRIPT);
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
