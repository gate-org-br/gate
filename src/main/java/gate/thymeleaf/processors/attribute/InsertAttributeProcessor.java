package gate.thymeleaf.processors.attribute;

import gate.type.Attributes;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.web.IWebExchange;

@ApplicationScoped
public class InsertAttributeProcessor extends AttributeModelProcessor
{

	public InsertAttributeProcessor()
	{
		super(null, "insert");

	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);
		Attributes attributes
			= Stream.of(element.getAllAttributes())
				.filter(e -> e.getValue() != null)
				.filter(e -> !"g:insert".equals(e.getAttributeCompleteName()))
				.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
					e -> e.getValue(), (a, b) -> a, Attributes::new));

		IWebExchange exchange = ((IWebContext) context).getExchange();

		IModel content = (IModel) ((LinkedList) exchange.getAttributeValue("g-template-content")).removeLast();
		replaceTag(context, model, handler, element.getElementCompleteName(), attributes);
		model.insertModel(1, content);
	}

}
