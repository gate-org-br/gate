package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
import gate.util.Icons;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class ExitProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	private static final Icons.Icon DEFAULT = Icons.getIcon("exit");

	public ExitProcessor()
	{
		super("exit");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		attributes.put("href", "Gate");

		if (element instanceof IStandaloneElementTag)
		{
			StringJoiner body = new StringJoiner("");

			if (attributes.containsKey("name"))
			{
				var name = attributes.remove("name");
				name = expression.evaluate((String) name);
				body.add(Converter.toText(name));
			}

			if (attributes.containsKey("icon"))
			{
				var icon = attributes.remove("icon");
				icon = expression.evaluate((String) icon);
				icon = Icons.getInstance().get((String) icon).orElse(DEFAULT);
				body.add("<i>" + icon + "</i>");
			}

			replaceWith(context, model, handler, "<a " + attributes + ">" + body + "</a>");
		} else
			replaceTag(context, model, handler, "a", attributes);
	}
}
