package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PrintAttributeProcessor extends AttributeProcessor
{

	public PrintAttributeProcessor()
	{
		super("print");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		var value = Expression.of(context).evaluate(element.getAttributeValue("g:print"));
		var string = Converter.toText(value);
		handler.setBody(string, true);
		handler.removeAttribute("g:print");
	}
}
