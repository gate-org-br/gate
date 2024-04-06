package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class ConditionProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expression;

	public ConditionProcessor()
	{
		super("condition");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		if (!element.hasAttribute("if"))
			throw new TemplateProcessingException("Missing required attribute if on g:condition");

		if ((boolean) expression.create().evaluate(element.getAttributeValue("if")))
			handler.removeTags();
		else if (element.hasAttribute("otherwise"))
		{
			String otherwise = element.getAttributeValue("otherwise");
			otherwise = Converter.toText(expression.create().evaluate(otherwise));
			otherwise = "<div class='TEXT'><h1>" + otherwise + "</h1></div>";
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
