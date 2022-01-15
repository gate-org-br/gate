package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IfProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public IfProcessor()
	{
		super("if");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		if (!element.hasAttribute("condition"))
			throw new TemplateProcessingException("Missing required attribute condition on g:if");

		if ((boolean) expression.evaluate(element.getAttributeValue("condition")))
		{
			handler.removeTags();
		} else if (element.hasAttribute("otherwise"))
		{
			String otherwise = element.getAttributeValue("otherwise");
			otherwise = Converter.toText(expression.evaluate(otherwise));
			handler.replaceWith(otherwise, false);
		} else
			handler.removeElement();
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}
