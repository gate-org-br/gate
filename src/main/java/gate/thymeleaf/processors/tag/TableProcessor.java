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
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class TableProcessor extends TagModelProcessor
{

	@Inject
	ELExpression expression;

	public TableProcessor()
	{
		super("table");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		if (!attributes.containsKey("condition")
			|| (boolean) expression.evaluate((String) attributes.remove("condition")))
		{
			attributes.remove("otherwise");
			replaceTag(context, model, handler, "table", attributes);
		} else if (attributes.containsKey("otherwise"))
		{
			var otherwise = attributes.remove("otherwise");
			otherwise = expression.evaluate((String) otherwise);
			otherwise = Converter.toText(otherwise);
			otherwise = "<div class='TEXT'><h1>" + otherwise + "</h1></div>";
			replaceWith(context, model, handler, otherwise.toString());
		} else
			model.reset();
	}

}
