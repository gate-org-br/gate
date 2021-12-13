package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.lang.property.Property;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public abstract class AbstractPropertyAttributeProcessor extends AttributeProcessor
{

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
		Property property = Property.getProperty(screen.getClass(),
			element.getAttributeValue("g:property"));
		handler.removeAttribute("g:property");
		process(context, element, handler, screen, property);
	}

	public abstract void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property);
}
