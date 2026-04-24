package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class WriteAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public WriteAttributeProcessor()
	{
		super(null, "write");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var value = extract(element, handler, "g:write").orElseThrow();
		var string = Converter.toString(expression.create().evaluate(value));
		handler.setBody(string, true);
	}
}
