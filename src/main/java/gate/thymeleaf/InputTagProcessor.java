package gate.thymeleaf;

import gate.converter.Converter;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public class InputTagProcessor extends PropertyTagProcessor
{

	public InputTagProcessor()
	{
		super("input");
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String propertyName,
		IElementTagStructureHandler tag)
	{
		super.doProcess(context, element, name, propertyName, tag);

		if (!element.hasAttribute("value"))
			tag.setAttribute("value", property.toString().endsWith("[]")
				? "" : Converter.toString(property.getValue(screen)));
	}

}
