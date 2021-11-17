package gate.thymeleaf.processors.attribute;

import gate.annotation.Icon;
import gate.thymeleaf.Expression;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class IconAttributeProcessor extends AttributeProcessor
{

	public IconAttributeProcessor()
	{
		super("icon");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		var type = element.getAttributeValue("g:icon");
		handler.removeAttribute("g:icon");
		var value = Expression.of(context).evaluate(type);
		var icon = Icon.Extractor.extract(value).orElse(Icons.UNKNOWN);
		handler.setAttribute("data-icon", icon.toString());
	}
}
