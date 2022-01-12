package gate.thymeleaf.processors.tag;

import gate.tags.TagLib;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public StacktraceProcessor()
	{
		super("stacktrace");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		Throwable exception = extract(element, handler, "exception")
			.map(expression::evaluate)
			.filter(e -> e instanceof Throwable)
			.map(e -> (Throwable) e)
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute exception on g:stacktrace"));

		handler.replaceWith(TagLib.format(exception), false);
	}
}
