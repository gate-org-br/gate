package gate.thymeleaf.processors.tag.property;

import gate.lang.property.Property;
import gate.thymeleaf.Model;
import gate.thymeleaf.processors.tag.ModelProcessor;
import gate.type.Attributes;
import org.thymeleaf.exceptions.TemplateProcessingException;

public abstract class PropertyProcessor extends ModelProcessor
{

	public PropertyProcessor(String name)
	{
		super(name);

	}

	@Override
	protected void doProcess(Model model)
	{
		if (!model.has("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:" + model.getName());

		Attributes attributes = new Attributes();
		model.stream()
			.filter(e -> !"property".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		Property property = Property.getProperty(model.screen().getClass(), model.get("property"));
		attributes.put("name", property.toString());

		property.getConstraints().stream()
			.filter(e -> !model.has(e.getName()))
			.forEachOrdered(e -> attributes.put(e.getName(), e.getValue().toString()));

		if (!attributes.containsKey("mask"))
		{
			String mask = property.getMask();
			if (mask != null && !mask.isEmpty())
				attributes.put("mask", mask);
		}

		if (!attributes.containsKey("title"))
		{
			String description = property.getDescription();
			if (description == null || description.isEmpty())
			{
				String displayName = property.getDisplayName();
				if (displayName != null && !displayName.isEmpty())
					attributes.put("title", displayName);
			} else
				attributes.put("title", description);
		}

		if (!attributes.containsKey("data-tooltip"))
		{
			String tooltip = property.getTooltip();
			if (tooltip != null && !tooltip.isEmpty())
				attributes.put("data-tooltip", tooltip);
		}

		if (!attributes.containsKey("placeholder"))
		{
			String placeholder = property.getPlaceholder();
			if (placeholder != null && !placeholder.isEmpty())
				attributes.put("placeholder", placeholder);
		}

		process(model, property, attributes);

	}

	protected abstract void process(Model model, Property property, Attributes attributes);

}
