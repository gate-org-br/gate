package gate.thymeleaf.processors.tag;

import gate.annotation.Color;
import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class LabelProcessor extends TagProcessor
{

	@Inject
	ELExpression expression;

	public LabelProcessor()
	{
		super("label");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		Screen screen = (Screen) request.getAttribute("screen");

		var property = extract(element, handler, "property")
			.map(e -> Property.getProperty(screen.getClass(), e))
			.orElseThrow(() -> new TemplateProcessingException("Missing required attribute property on g:label"));

		var format = extract(element, handler, "format").orElse(null);
		var empty = extract(element, handler, "empty").orElse(null);

		Attributes attributes = new Attributes();

		var value = property.getValue(screen);

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

		String string = Converter.toText(value, format);
		if (string.isBlank() && empty != null)
			string = Converter.toText(expression.evaluate(empty));
		string = string.replaceAll("\\n", "<br/>");

		handler.replaceWith("<label " + attributes + ">" + string + "</label>", true);
	}
}
