package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PrintAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public PrintAttributeProcessor()
	{
		super(null, "print");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var value = extract(element, handler, "g:print").orElseThrow();
		var format = extract(element, handler, "g:format").orElse(null);
		var empty = extract(element, handler, "g:empty").orElse(null);

		value = Converter.toText(expression.evaluate(value), format);
		if (value.isBlank() && empty != null)
			value = Converter.toText(expression.evaluate(empty));
		value = value.replaceAll("\\n", "<br/>");
		handler.setBody(value, true);
	}
}
