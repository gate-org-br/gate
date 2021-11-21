package gate.thymeleaf.processors.attribute;

import gate.annotation.Icon;
import gate.thymeleaf.ELExpression;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IconAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public IconAttributeProcessor()
	{
		super(null, "icon");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		handler.setAttribute("data-icon",
			extract(element, handler, "g:icon")
				.map(expression::evaluate)
				.flatMap(Icon.Extractor::extract)
				.orElse(Icons.UNKNOWN)
				.toString());
	}
}
