package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Sequence;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class InputAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	Sequence sequence;

	@Inject
	ELExpression expression;

	public InputAttributeProcessor()
	{
		super("input");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		var type = "text";
		if (element.hasAttribute("type"))
			type = element.getAttributeValue("type");
		else
			handler.setAttribute("type", type);

		handler.setAttribute("value",
			"date".equalsIgnoreCase(type)
			|| "datetime-local".equalsIgnoreCase(type)
			? Converter.toISOString(value)
			: Converter.toString(value));

		if ("text".equalsIgnoreCase(type))
		{
			if (!element.hasAttribute("data-mask"))
			{
				String mask = property.getMask();
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

					optionLabel = Converter.toText(optionLabel);
					optionValue = Converter.toString(optionValue);
					String string = String.format("<option data-value='%s'>%s</option>", optionValue, optionLabel);
					model.add(context.getModelFactory().createText(string));

				}
				model.add(context.getModelFactory().createText("</datalist " + parameters + ">"));

				handler.insertBefore(model);
			}
		}
	}
}
