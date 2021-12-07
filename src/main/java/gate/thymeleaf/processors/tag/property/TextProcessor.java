package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Sequence;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class TextProcessor extends PropertyProcessor
{

	@Inject
	Sequence sequence;

	@Inject
	ELExpression expression;

	public TextProcessor()
	{
		super("text");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		if (!attributes.containsKey("data-mask"))
		{
			String mask = property.getMask();
			if (mask != null && !mask.isEmpty())
				attributes.put("data-mask", mask);
		}

		attributes.put("type", "text");

		if (attributes.containsKey("value"))
			attributes.put("value", Converter.toString(expression.evaluate((String) attributes.get("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", Converter.toString(property.getValue(screen)));

		if (attributes.containsKey("options"))
		{
			var options = expression.evaluate((String) attributes.remove("options"));

			Attributes parameters = new Attributes();
			String id = "datalist-" + sequence.next();
			parameters.put("id", id);
			attributes.put("list", id);

			StringJoiner string = new StringJoiner(System.lineSeparator());
			string.add("<datalist " + parameters + ">");

			String labels = (String) attributes.remove("labels");
			String values = (String) attributes.remove("values");

			for (Object option : Toolkit.iterable(options))
			{
				Object optionLabel = option;
				if (labels != null)
					optionLabel = expression.evaluate(labels, option);

				Object optionValue = option;
				if (values != null)
					optionValue = expression.evaluate(values, option);

				string.add(String.format("<option data-value='%s'>%s</option>",
					Converter.toString(optionValue), Converter.toText(optionLabel)));

			}
			string.add("</datalist>");

			string.add("<input " + attributes + "/>");
			handler.replaceWith(string.toString(), true);
		} else
			handler.replaceWith("<input " + attributes + "/>", true);
	}

}
