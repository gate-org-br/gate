package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class WriteProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public WriteProcessor()
	{
		super("write");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (!element.hasAttribute("value"))
			throw new TemplateProcessingException("Missing required attribute value on g:write");

		var value = expression.create().evaluate(element.getAttributeValue("value"));
		String string = Converter.toString(value);
		handler.replaceWith(string, false);
	}

}
