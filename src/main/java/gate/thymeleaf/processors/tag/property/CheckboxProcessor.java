package gate.thymeleaf.processors.tag.property;

import gate.base.Screen;
import gate.converter.Converter;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Toolkit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class CheckboxProcessor extends PropertyProcessor
{

	@Inject
	ELExpression expression;

	public CheckboxProcessor()
	{
		super("checkbox");
	}

	@Override
	protected void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler,
		Screen screen, Property property, Attributes attributes)
	{
		attributes.put("type", getElement());

		var value = attributes.get("value");
		value = expression.evaluate((String) value);
		attributes.put("value", Converter.toString(value));

		if (attributes.containsKey("checked"))
		{
			var checked = attributes.get("checked");
			checked = expression.evaluate((String) checked);
			if (Boolean.TRUE.equals(checked))
				attributes.put("checked", "checked");
			else
				attributes.remove("checked");
		} else if (Toolkit.collection(property.getValue(screen)).contains(value))
			attributes.put("checked", "checked");

		handler.replaceWith("<input " + attributes + "/>", true);
	}
}
