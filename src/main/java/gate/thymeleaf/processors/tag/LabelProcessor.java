package gate.thymeleaf.processors.tag;

import gate.annotation.Color;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class LabelProcessor extends ModelProcessor
{

	public LabelProcessor()
	{
		super("label");
	}

	@Override
	protected void doProcess(Model model)
	{

		if (!model.has("property"))
			throw new TemplateProcessingException("Missing required attribute property on g:label");

		Expression expression = Expression.of(model.getContext());

		Attributes attributes = new Attributes();
		model.stream()
			.filter(e -> !"property".equals(e.getAttributeCompleteName()))
			.filter(e -> !"format".equals(e.getAttributeCompleteName()))
			.filter(e -> !"empty".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		var property = Property.getProperty(model.screen().getClass(), model.get("property"));
		var value = property.getValue(model.screen());

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

		if (!attributes.containsKey("style"))
			Color.Extractor.extract(value)
				.or(() -> Optional.ofNullable(property.getColor()))
				.ifPresent(e -> attributes.put("style", "color: " + e));

		String string = Converter.toText(value, model.get("format"));
		if (string.isBlank() && model.has("empty"))
			string = Converter.toText(expression.evaluate(model.get("empty")));
		string = string.replaceAll("\\n", "<br/>");

		model.replaceAll("<label " + attributes + ">" + string + "</label>");
	}

}
