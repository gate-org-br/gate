package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.unbescape.html.HtmlEscape;

@ApplicationScoped
public class PrintProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public PrintProcessor()
	{
		super("print");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (!element.hasAttribute("value"))
			throw new TemplateProcessingException("Missing required attribute value on g:print");

		var value = expression.evaluate(element.getAttributeValue("value"));
		String string = Converter.render(value, element.getAttributeValue("format"));
		if (string.isBlank() && element.hasAttribute("empty"))
			string = Converter.render(expression.evaluate(element.getAttributeValue("empty")));
		string = HtmlEscape.escapeHtml4Xml(string);
		string = string.replaceAll("\\n", "<br/>");
		handler.replaceWith(string, false);
	}

}