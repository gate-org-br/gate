package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class AbstractPropertyAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpressionFactory expressionFactory;

	public AbstractPropertyAttributeProcessor(String element)
	{
		super(element, "property");
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		Screen screen = (Screen) request.getAttribute("screen");

		var name = element.getAttributeValue("g:property");
		name = (String) expressionFactory.create().evaluate(name);
		Property property = Property.getProperty(screen.getClass(), name);

		handler.removeAttribute("g:property");
		process(context, element, handler, screen, property);
	}

	public abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property);
}
