package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Sequence;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.StringJoiner;
import java.util.function.Function;
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

		if (!attributes.containsKey("data-mask"))
		{
			String mask = property.getMask();
			if (mask != null && !mask.isEmpty())
				attributes.put("data-mask", mask);
		}

		if (attributes.containsKey("value"))
			attributes.put("value", Converter.toString(expression.evaluate((String) attributes.get("value"))));
		else if (!property.toString().endsWith("[]"))
			attributes.put("value", Converter.toString(property.getValue(screen)));

		if (attributes.containsKey("options"))
		{
			var options = expression.evaluate((String) attributes.remove("options"));
			var labels = extract(element, handler, "labels").map(expression::function).orElse(Function.identity());
			var values = extract(element, handler, "values").map(expression::function).orElse(Function.identity());

			Attributes parameters = new Attributes();
			String id = "datalist-" + sequence.next();
			parameters.put("id", id);
			attributes.put("list", id);

			StringJoiner string = new StringJoiner(System.lineSeparator());
			string.add("<datalist " + parameters + ">");

			for (Object option : Toolkit.iterable(options))
			{
				var label = Converter.toText(labels.apply(option));
				var value = Converter.toString(values.apply(option));
				string.add(String.format("<option data-value='%s'>%s</option>", value, label));
			}

			string.add("</datalist>");

			string.add("<input " + attributes + "/>");
			handler.replaceWith(string.toString(), true);
		} else
			handler.replaceWith("<input " + attributes + "/>", true);
	}

}
