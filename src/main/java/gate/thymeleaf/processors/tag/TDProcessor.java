package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TDProcessor extends ModelProcessor
{

	@Inject
	ELExpression expression;

	public TDProcessor()
	{
		super("td");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.attributes().filter(e -> e.getValue() != null)
			.filter(e -> !"value".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(),
			e.getValue()));

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
