package gate.thymeleaf.processors.tag;

import gate.thymeleaf.ELExpressionFactory;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public StacktraceProcessor()
	{
		super("stacktrace");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		Throwable exception = extract(element, handler, "exception")
			.map(expression.create()::evaluate)
			.filter(e -> e instanceof Throwable)
			.map(e -> (Throwable) e)
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute exception on g:stacktrace"));

		handler.replaceWith(Toolkit.format(exception), false);
	}
}
