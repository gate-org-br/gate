package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CaptionProcessor extends ModelProcessor
{

	@Inject
	ELExpression expression;

	public CaptionProcessor()
	{
		super("caption");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.attributes()
			.filter(e -> e.getValue() != null)
			.filter(e -> !"value".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		if (model.isStandalone())
		{
			var value = expression.evaluate(model.get("value"));
			String string = Converter.toText(value, model.get("format"));
			if (string.isBlank() && model.has("empty"))
				string = model.get("empty");
			string = string.replaceAll("\\n", "<br/>");
			model.replaceAll("<caption " + attributes + ">" + string + "</caption>");
		} else
			model.replaceTag("caption", attributes);
	}
}
