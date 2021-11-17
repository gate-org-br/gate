package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CaptionProcessor extends ModelProcessor
{

	public CaptionProcessor()
	{
		super("caption");
	}

	@Override
	protected void doProcess(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		Attributes attributes = new Attributes();
		model.stream()
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
