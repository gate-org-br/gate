package gate.thymeleaf.processors.attribute;

import gate.tags.TagLib;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class StacktraceAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

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
			.map(expression::evaluate)
			.filter(object -> object instanceof Throwable)
			.map(throwable -> (Throwable) throwable)
			.map(TagLib::format)
			.ifPresentOrElse(exception -> handler.setBody(exception, false), handler::removeElement);
	}
}
