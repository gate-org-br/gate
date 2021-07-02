package gate.thymeleaf;

import gate.converter.Converter;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

public class TextAreaTagProcessor extends PropertyTagProcessor
{

	public TextAreaTagProcessor()
	{
		super("textarea");
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String propertyName,
		IElementTagStructureHandler tag)
	{
		super.doProcess(context, element, name, propertyName, tag);

		if (element.hasAttribute("value"))
			tag.setBody(Converter.toString(element.getAttribute("value")), false);
		else if (!property.toString().endsWith("[]"))
			tag.setBody(Converter.toString(property.getValue(screen)), false);
		else
			tag.setBody("", false);
	}

}
