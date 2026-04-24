package gate.thymeleaf.processors.attribute;

import gate.annotation.Icon;
import gate.icon.Icons;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IconTagAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expression;

	public IconTagAttributeProcessor()
	{
		super("i", "icon");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var type = element.getAttributeValue("g:icon");
		handler.removeAttribute("g:icon");
		var value = expression.create().evaluate(type);
		var icon = Icon.Extractor.extract(value).orElse(Icons.UNKNOWN);
		handler.setBody(icon.toString(), false);
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}
