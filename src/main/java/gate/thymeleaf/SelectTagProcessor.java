package gate.thymeleaf;

import gate.converter.Converter;
import gate.type.Attributes;
import gate.util.Toolkit;
import java.util.Arrays;
import java.util.Objects;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

public class SelectTagProcessor extends PropertyTagProcessor
{

	public SelectTagProcessor()
	{
		super("select");
	}

	@Override
	protected void doProcess(ITemplateContext context,
		IProcessableElementTag element,
		AttributeName name,
		String propertyName,
		IElementTagStructureHandler tag)
	{
		super.doProcess(context, element, name, propertyName, tag);
		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

		Object options = null;
		if (element.hasAttribute("options"))
		{
			IStandardExpression expression = parser.parseExpression(context, element.getAttribute("options").getValue());
			options = expression.execute(context);
			tag.removeAttribute("options");
		} else if (Boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (boolean.class.isAssignableFrom(property.getRawType()))
			options = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
		else if (Enum.class.isAssignableFrom(property.getRawType()))
			options = property.getRawType().getEnumConstants();
		else
			throw new TemplateInputException("No option defined for property " + property.toString());

		StringBuilder body = new StringBuilder();
		Object value = property.getValue(screen);
		print(body, Toolkit.iterable(options), value);
		tag.setBody(body, false);
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
