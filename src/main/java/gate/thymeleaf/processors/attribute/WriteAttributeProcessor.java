package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class WriteAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public WriteAttributeProcessor()
	{
		super(null, "write");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var value = extract(element, handler, "g:write").orElseThrow();
		var string = Converter.toString(expression.evaluate(value));
		handler.setBody(string, true);
	}
}
