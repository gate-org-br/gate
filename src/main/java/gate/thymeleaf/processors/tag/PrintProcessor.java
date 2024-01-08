package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PrintProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public PrintProcessor()
	{
		super("print");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (!element.hasAttribute("value"))
			throw new TemplateProcessingException("Missing required attribute value on g:print");

		var value = expression.create().evaluate(element.getAttributeValue("value"));
		String string = Converter.toText(value, element.getAttributeValue("format"));
		if (string.isBlank() && element.hasAttribute("empty"))
			string = Converter.toText(expression.create().evaluate(element.getAttributeValue("empty")));
		string = string.replaceAll("\\n", "<br/>");
		handler.replaceWith(string, false);
	}

}
