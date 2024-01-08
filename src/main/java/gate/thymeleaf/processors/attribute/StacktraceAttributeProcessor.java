package gate.thymeleaf.processors.attribute;

import gate.thymeleaf.ELExpressionFactory;
import gate.util.Toolkit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public StacktraceAttributeProcessor()
	{
		super(null, "stacktrace");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		extract(element, handler, "g:stacktrace")
			.map(expression.create()::evaluate)
			.filter(object -> object instanceof Throwable)
			.map(throwable -> (Throwable) throwable)
			.map(Toolkit::format)
			.ifPresentOrElse(exception -> handler.setBody(exception, false), handler::removeElement);
	}
}
