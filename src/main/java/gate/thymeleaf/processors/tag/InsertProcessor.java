package gate.thymeleaf.processors.tag;

import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.LinkedList;

@ApplicationScoped
public class InsertProcessor extends TagModelProcessor
{

	public InsertProcessor()
	{
		super("insert");

	}

	@Override
	public void process(ITemplateContext context, IModel model,
			IElementModelStructureHandler handler)
	{
		var exchange = ((IWebContext) context).getExchange();
		IModel content = (IModel) ((LinkedList<?>) exchange.getAttributeValue("g-template-content"))
				.removeLast();
		model.reset();
		model.insertModel(0, content);

	}
}
