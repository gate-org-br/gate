package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TDProcessor extends ModelProcessor
{

	public TDProcessor()
	{
		super("td");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.stream().filter(e -> e.getValue() != null)
			.filter(e -> !"value".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(),
			e.getValue()));

		Expression expression = Expression.of(model.getContext());
		if (model.isStandalone())
		{
			var value = expression.evaluate(model.get("value"));
			String string = Converter.toText(value, model.get("format"));
			if (string.isBlank() && model.has("empty"))
				string = Converter.toText(expression.evaluate(model.get("empty")));
			string = string.replaceAll("\\n", "<br/>");
			model.replaceAll("<td " + attributes + ">" + string + "</td>");
		} else
			model.replaceTag("td", attributes);
	}
}
