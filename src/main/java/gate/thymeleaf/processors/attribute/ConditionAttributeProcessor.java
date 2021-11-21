package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class ConditionAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public ConditionAttributeProcessor()
	{
		super(null, "condition");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if ((boolean) expression.evaluate(element.getAttributeValue("g:condition")))
		{
			handler.removeAttribute("g:condition");
			handler.removeAttribute("g:otherwise");
		} else if (element.hasAttribute("g:otherwise"))
		{
			String otherwise = element.getAttributeValue("g:otherwise");
			otherwise = Converter.toText(expression.evaluate(otherwise));
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
