package gate.thymeleaf;

import gate.lang.property.Property;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import static org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor.PRECEDENCE;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class PropertyTagProcessor extends AbstractAttributeTagProcessor
{

	protected Object screen;
	protected Property property;

	public PropertyTagProcessor(String elementName)
	{
		super(TemplateMode.HTML,
			"g",
			elementName,
			false,
			"property",
			true,
			PRECEDENCE,
			true);
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String propertyName,
		IElementTagStructureHandler tag)
	{

		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
		screen = parser.parseExpression(context, "${screen}").execute(context);
		property = Property.getProperty(screen.getClass(), propertyName);

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

	}
}
