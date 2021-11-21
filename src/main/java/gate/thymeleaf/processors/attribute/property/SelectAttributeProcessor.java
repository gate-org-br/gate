package gate.thymeleaf.processors.attribute.property;

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
public class SelectAttributeProcessor extends FormControlAttributeProcessor
{

	@Inject
	ELExpression expression;

	public SelectAttributeProcessor()
	{
		super("select");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		Object options = null;
		if (element.hasAttribute("g:options"))
		{
			options = expression.evaluate(element.getAttributeValue("g:options"));
			handler.removeAttribute("g:options");
		} else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		String labels = extract(element, handler, "g:labels").orElse(null);
		String values = extract(element, handler, "g:values").orElse(null);

		StringJoiner body = new StringJoiner(System.lineSeparator());
		body.add("<option></option>");
		print(body, Toolkit.iterable(options), labels, values, value);
		handler.setBody(body.toString(), false);
	}

	private String print(StringJoiner result,
		Iterable<?> options,
		String labels, String values,
		Object value)
	{
		for (Object option : options)
		{
			var label = Converter.toText(labels != null
				? expression.evaluate(labels, option) : option);

			if (values != null)
				option = expression.evaluate(values, option);

			Attributes attributes = new Attributes();

			attributes.put("value", Converter.toString(option));
			if (Objects.equals(option, value))
				attributes.put("selected", "selected");

			result.add("<option " + attributes + ">" + label + "</option>");
		}
		return result.toString();
	}

}
