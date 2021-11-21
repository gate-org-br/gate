package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SelectProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public SelectProcessor()
	{
		super("select");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		Object options = null;
		if (element.hasAttribute("options"))
			options = expression.evaluate(element.getAttributeValue("options"));
		else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		String labels = extract(element, handler, "labels").orElse(null);
		String values = extract(element, handler, "values").orElse(null);

		StringJoiner string = new StringJoiner(System.lineSeparator());
		string.add("<select " + attributes + ">");
		string.add("<option></option>");
		print(string, Toolkit.iterable(options), labels, values, property.getValue(screen));
		string.add("</select>");
		handler.replaceWith(string.toString(), false);

	}

	private void print(StringJoiner string, Iterable<?> options, String labels, String values, Object value)
	{
		for (Object option : options)
		{
			var label = Converter.toText(labels != null
				? expression.evaluate(labels, option)
				: option);

			if (values != null)
				option = expression.evaluate(values, option);

			Attributes attributes = new Attributes();

			if (Objects.equals(option, value))
				attributes.put("selected", "selected");

			attributes.put("value", Converter.toString(option));

			string.add("<option " + attributes + ">" + label + "</option>");
		}
	}
}
