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
		attributes.put("type", "text");
		if (element.hasAttribute("value"))
			attributes.put("value", Converter.toString(expression.evaluate(element.getAttributeValue("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", Converter.toString(property.getValue(screen)));

		if (element.hasAttribute("options"))
		{
			var options = expression.evaluate(element.getAttributeValue("options"));
			handler.removeAttribute("options");

			Attributes parameters = new Attributes();
			String id = "datalist-" + sequence.next();
			parameters.put("id", id);
			attributes.put("list", id);

			StringJoiner string = new StringJoiner(System.lineSeparator());
			string.add("<datalist " + parameters + ">");
			for (Object option : Toolkit.iterable(options))
			{
				Object optionLabel = option;
				if (element.hasAttribute("labels"))
				{
					optionLabel = expression.evaluate(element.getAttributeValue("labels"), option);
					handler.removeAttribute("labels");
				}

				Object optionValue = option;
				if (element.hasAttribute("values"))
				{
					optionValue = expression.evaluate(element.getAttributeValue("values"), option);
					handler.removeAttribute("values");
				}

				string.add(String.format("<option data-value='%s'>%s</option>",
					Converter.toString(optionValue), Converter.toText(optionLabel)));

			}
			string.add("</datalist>");

			string.add("<input " + attributes + "/>");
			handler.replaceWith(string.toString(), false);
		} else
			handler.replaceWith("<input " + attributes + "/>", false);
	}

}
