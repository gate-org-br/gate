package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import gate.util.Icons;
import java.util.StringJoiner;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HideProcessor extends ModelProcessor
{

	public HideProcessor()
	{
		super("hide");
	}

	@Override
	protected void doProcess(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		Attributes attributes = new Attributes();
		model.stream()
			.filter(e -> e.getValue() != null)
			.filter(e -> !"name".equals(e.getAttributeCompleteName()))
			.filter(e -> !"icon".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		attributes.put("href", "#");
		attributes.put("target", "_hide");

		if (model.isStandalone())
		{
			StringJoiner body = new StringJoiner("");
			if (model.has("name"))
				body.add(Converter.toText(expression.evaluate(model.get("name"))));

			if (model.has("icon"))
				body.add("<i>" + Icons.getInstance().get(model.get("icon"))
					.orElse(Icons.getIcon("return")).toString() + "</i>");

			model.replaceAll("<a " + attributes + ">" + body + "</a>");
		} else
			model.replaceTag("a", attributes);
	}
}
