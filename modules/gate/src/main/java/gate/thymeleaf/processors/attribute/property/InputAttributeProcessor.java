package gate.thymeleaf.processors.attribute.property;

import gate.adapter.renderer.Renderer;

import gate.adapter.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Sequence;
import gate.type.Attributes;
import gate.util.Toolkit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.function.Function;

@ApplicationScoped
public class InputAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	Sequence sequence;

	public InputAttributeProcessor()
	{
		super("input");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
	                    IElementTagStructureHandler handler, Object screen, Property property, Object value)
	{
		var type = "text";
		if (element.hasAttribute("type"))
			type = element.getAttributeValue("type");
		else
			handler.setAttribute("type", type);

		handler.setAttribute("value", usesISOString(type)
				? Converter.toISOString(value)
				: Converter.toString(value));

		if ("text".equalsIgnoreCase(type))
		{
			if (!element.hasAttribute("data-mask"))
			{
				String mask = property.getMetadata().mask();
				if (mask != null && !mask.isEmpty())
					handler.setAttribute("data-mask", mask);
			}

			var options = extract(element, handler, "g:options").map(expression::evaluate).orElse(null);
			if (options != null)
			{
				Attributes parameters = new Attributes();
				String id = "datalist-" + sequence.next();
				parameters.put("id", id);
				handler.setAttribute("list", id);

				IModel model = context.getModelFactory().createModel();

				model.add(context.getModelFactory().createText("<datalist " + parameters + ">"));

				var labels = extract(element, handler, "g:labels").map(expression::function).orElse(Function.identity());
				var values = extract(element, handler, "g:values").map(expression::function).orElse(Function.identity());

				for (Object option : Toolkit.iterable(options))
				{
					Object optionLabel = labels.apply(option);
					Object optionValue = values.apply(option);

					optionLabel = Renderer.render(optionLabel);
					optionValue = Converter.toString(optionValue);
					String string = String.format("<option data-value='%s'>%s</option>", optionValue, optionLabel);
					model.add(context.getModelFactory().createText(string));

				}
				model.add(context.getModelFactory().createText("</datalist " + parameters + ">"));

				handler.insertBefore(model);
			}
		}
	}

	private boolean usesISOString(String type)
	{
		return "date".equalsIgnoreCase(type)
		       || "datetime-local".equalsIgnoreCase(type)
		       || "month".equalsIgnoreCase(type)
		       || "number".equalsIgnoreCase(type)
		       || "range".equalsIgnoreCase(type)
		       || "time".equalsIgnoreCase(type);
	}
}
