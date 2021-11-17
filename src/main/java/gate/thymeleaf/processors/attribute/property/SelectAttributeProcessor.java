package gate.thymeleaf.processors.attribute.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.Expression;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SelectAttributeProcessor extends FormControlAttributeProcessor
{

	public SelectAttributeProcessor()
	{
		super("select");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element,
		IElementTagStructureHandler handler, Screen screen, Property property, Object value)
	{
		Object options = null;
		if (element.hasAttribute("options"))
		{
			Expression expression = Expression.of(context);
			options = expression.evaluate(element.getAttributeValue("options"));
			handler.removeAttribute("options");
		} else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		StringBuilder body = new StringBuilder("<option></option>");
		print(body, Toolkit.iterable(options), value);
		handler.setBody(body, false);
	}

	private String print(StringBuilder result, Iterable<?> options, Object value)
	{
		for (Object option : options)
		{
			Attributes attributes = new Attributes();
			attributes.put("value", Converter.toString(option));
			if (Objects.equals(option, value))
				attributes.put("selected", "selected");
			result.append("<option ")
				.append(attributes)
				.append(">")
				.append(Converter.toText(option))
				.append("</option>");
		}
		return result.toString();
	}

}
