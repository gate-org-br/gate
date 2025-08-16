package gate.thymeleaf.processors.tag;

import gate.constraint.Maxlength;
import gate.constraint.Required;
import gate.lang.property.Property;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class PropertiesProcessor extends TagProcessor
{

	@Inject
	ELExpressionFactory expressionFactory;

	public PropertiesProcessor()
	{
		super("properties");

	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{

		Attributes attributes = Stream.of(element.getAllAttributes())
			.filter(e -> "source".equals(e.getAttributeCompleteName()))
			.filter(e -> "caption".equals(e.getAttributeCompleteName()))
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		StringJoiner table = new StringJoiner("\n");
		table.add(String.format("<table %s>", attributes.toString()));

		if (element.getAttribute("caption") != null)
			table.add(String.format("<caption>%s</caption>", element.getAttribute("caption").getValue()));

		table.add("<colgroup>");
		table.add("<col style='width: 40px'/>");
		table.add("<col style='width: 160px'/>");
		table.add("<col style='width: 80px'/>");
		table.add("<col style='width: 80px'/>");
		table.add("<col/>");
		table.add("</colgroup>");

		table.add("<thead>");
		table.add("<tr>");
		table.add("<th style='text-align: center'>#</th>");
		table.add("<th>Nome</th>");
		table.add("<th style='text-align: center'>Requerido</th>");
		table.add("<th style='text-align: center'>Tam Max</th>");
		table.add("<th>Descrição</th>");
		table.add("</tr>");
		table.add("</thead>");

		table.add("<tbody>");

		var properties = Optional.ofNullable(element.getAttribute("source"))
			.map(e -> e.getValue())
			.map(expressionFactory.create()::evaluate)
			.map(e -> (List<Property>) e)
			.orElseThrow(() -> new TemplateInputException("Not source attribute defined for properties tag"));

		int i = 0;
		for (Property property : properties)
		{
			String name = property.getDisplayName();
			if (name == null)
				name = property.toString();
			table.add("<tr>");
			table.add(String.format("<td style='text-align: center'>%02d</td>", ++i));
			table.add(String.format("<td>%s</td>", Objects.requireNonNullElse(name, "")));

			table.add(String.format("<td style='text-align: center'>%s</td>", property.getAttributes().get(1)
				.getConstraints().stream().anyMatch(e -> e instanceof Required.Implementation) ? "Sim" : "Não"));

			table.add(String.format("<td style='text-align: center'>%s</td>", property.getConstraints().stream()
				.filter(e -> e instanceof Maxlength.Implementation).map(e -> e.getValue().toString()).findFirst().orElse("N/A")));

			table.add(String.format("<td>%s</td>", Objects.requireNonNullElse(property.getDescription(), "")));
			table.add("</tr>");
		}
		table.add("</tbody>");

		table.add("</table>");

		handler.replaceWith(table.toString(), false);
	}
}
