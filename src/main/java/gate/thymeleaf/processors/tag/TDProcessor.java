package gate.thymeleaf.processors.tag;

import gate.converter.Converter;
import gate.thymeleaf.ELExpression;
import gate.type.Attributes;
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
public class TDProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	public TDProcessor()
	{
		super("td");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		if (element instanceof IStandaloneElementTag)
		{
			var value = attributes.remove("value");
			value = expression.evaluate((String) value);

			var format = attributes.remove("format");
			format = expression.evaluate((String) format);

			String text = Converter.toText(value, (String) format);

			if (text.isBlank() && attributes.containsKey("empty"))
			{
				var empty = attributes.remove("empty");
				empty = expression.evaluate((String) empty);
				text = Converter.toText(empty);
			}

			text = text.replaceAll("\\n", "<br/>");
			text = "<td " + attributes + ">" + text + "</td>";
			replaceWith(context, model, handler, text);
		} else
			replaceTag(context, model, handler, "td", attributes);
	}

}
