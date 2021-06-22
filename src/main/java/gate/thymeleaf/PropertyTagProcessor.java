package gate.thymeleaf;

import gate.converter.Converter;
import gate.lang.property.Property;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class PropertyTagProcessor extends AbstractAttributeTagProcessor
{

	protected Object screen;
	protected Property property;

	public PropertyTagProcessor(TemplateMode templateMode, String dialectPrefix, String elementName,
		boolean prefixElementName, String attributeName, boolean prefixAttributeName,
		int precedence, boolean removeAttribute)
	{
		super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence, removeAttribute);
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String type,
		IElementTagStructureHandler tag)
	{

		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
		screen = parser.parseExpression(context, "${screen}").execute(context);
		property = Property.getProperty(screen.getClass(), element.getAttributeValue("g:property"));
		tag.removeAttribute("g:property");

		tag.setAttribute("name", property.toString());

		property.getConstraints().stream()
			.filter(e -> !element.hasAttribute(e.getName()))
			.forEachOrdered(e -> tag.setAttribute(e.getName(), e.getValue().toString()));

		if (!element.hasAttribute("mask"))
		{
			String mask = property.getMask();
			if (mask != null && !mask.isEmpty())
				tag.setAttribute("mask", mask);
		}

		if (!element.hasAttribute("title"))
		{
			String description = property.getDescription();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getDisplayName();
				if (displayName != null && !displayName.isEmpty())
					tag.setAttribute("title", displayName);
			} else
				tag.setAttribute("title", description);
		}

		if (!element.hasAttribute("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				tag.setAttribute("data-tooltip", tooltip);
		}

		if (!element.hasAttribute("placeholder"))
		{
			String placeholder = property.getPlaceholder();
			if (placeholder != null && !placeholder.isEmpty())
				tag.setAttribute("placeholder", placeholder);
		}

		if (!element.hasAttribute("type"))
			tag.setAttribute("type", type);

		if (!element.hasAttribute("value"))
			tag.setAttribute("value", property.toString().endsWith("[]")
				? "" : Converter.toString(property.getValue(screen)));
	}

}
