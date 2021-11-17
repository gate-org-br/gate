package gate.thymeleaf.processors.tag.property;

import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateInputException;

@ApplicationScoped
public class SelectProcessor extends PropertyProcessor
{

	public SelectProcessor()
	{
		super("select");
	}

	@Override
	protected void process(Model model, Property property, Attributes attributes)
	{
		Expression expression = Expression.of(model.getContext());
		Object options = null;
		if (model.has("options"))
			options = expression.evaluate(model.get("options"));
		else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		model.removeAll();
		model.add("<select " + attributes + ">");
		model.add("<option></option>");
		print(model, Toolkit.iterable(options), property.getValue(model.screen()));
		model.add("</select>");

	}

	private void print(Model model, Iterable<?> options, Object value)
	{
		for (Object option : options)
		{
			Attributes attributes = new Attributes();
			if (Objects.equals(option, value))
				attributes.put("selected", "selected");
			attributes.put("value", Converter.toString(option));
			model.add("<option " + attributes + ">" + Converter.toText(option) + "</option>");
		}
	}
}
