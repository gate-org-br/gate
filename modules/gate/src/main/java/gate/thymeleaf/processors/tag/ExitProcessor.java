package gate.thymeleaf.processors.tag;

import gate.adapter.converter.Converter;
import gate.icon.Icon;
import gate.icon.Icons;
import gate.thymeleaf.ELExpressionFactory;
import gate.type.Attributes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ExitProcessor extends TagModelProcessor
{

	@Inject
	ELExpressionFactory expression;

	private static final Icon DEFAULT = Icons.getIcon("exit");

	public ExitProcessor()
	{
		super("exit");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		Attributes attributes = Stream.of(element.getAllAttributes())
				.collect(Collectors.toMap(IAttribute::getAttributeCompleteName,
						IAttribute::getValue, (a, b) -> a, Attributes::new));

		attributes.put("href", "Gate");

		if (element instanceof IStandaloneElementTag)
		{
			StringJoiner body = new StringJoiner("");

			if (attributes.containsKey("name"))
			{
				var name = attributes.remove("name");
				name = expression.create().evaluate((String) name);
				body.add(Converter.render(name));
			}

			if (attributes.containsKey("icon"))
			{
				var icon = attributes.remove("icon");
				icon = expression.create().evaluate((String) icon);
				icon = Icons.getInstance().get((String) icon).orElse(DEFAULT);
				body.add("<g-icon>" + icon + "</g-icon>");
			}

			replaceWith(context, model, handler, "<a " + attributes + ">" + body + "</a>");
		} else
			replaceTag(context, model, handler, "a", attributes);
	}
}