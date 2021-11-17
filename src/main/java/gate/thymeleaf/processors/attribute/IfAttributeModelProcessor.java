package gate.thymeleaf.processors.attribute;

import gate.converter.Converter;
import gate.thymeleaf.Expression;
import gate.thymeleaf.Model;
import gate.type.Attributes;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IfAttributeModelProcessor extends AttributeModelProcessor
{

	private static final Set<String> BLOCK_ELEMENT = Set.of("table", "div");

	public IfAttributeModelProcessor()
	{
		super("if");
	}

	@Override
	public void process(Model model)
	{
		Expression expression = Expression.of(model.getContext());
		if (!(boolean) expression.evaluate(model.get("g:if")))
		{
			model.removeAll();

			if (model.has("g:otherwise"))
			{
				var otherwise = model.get("g:otherwise");
				otherwise = Converter.toText(expression.evaluate(otherwise));

				if (BLOCK_ELEMENT.contains(model.tag()))
					model.add("<div class='TEXT'><h1>")
						.add(otherwise)
						.add("</h1></div>");
				else
					model.add(otherwise);
			}
		} else
		{
			Attributes attributes = new Attributes();
			model.stream()
				.filter(e -> e.getValue() != null)
				.filter(e -> !"g:if".equals(e.getAttributeCompleteName()))
				.filter(e -> !"g:otherwise".equals(e.getAttributeCompleteName()))
				.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
			model.replaceTag(model.tag(), attributes);
		}
	}
}
