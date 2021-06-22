package gate.thymeleaf;

import gate.converter.Converter;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public class TypeAttributeTagProcessor extends PropertyTagProcessor
{

	public TypeAttributeTagProcessor()
	{
		super(TemplateMode.HTML,
			"g",
			"input",
			false,
			"type",
			true,
			PRECEDENCE,
			true);
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String type,
		IElementTagStructureHandler tag)
	{
		super.doProcess(context, element, name, type, tag);

		if (!element.hasAttribute("type"))
			tag.setAttribute("type", type);

		if (!element.hasAttribute("value"))
			tag.setAttribute("value", property.toString().endsWith("[]")
				? "" : Converter.toString(property.getValue(screen)));
	}

}
