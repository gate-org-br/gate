package gate.thymeleaf.processors.tag.iterable;

import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class IteratorProcessor extends IterableProcessor
{

	public IteratorProcessor()
	{
		super("iterator");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		removeTag(context, model, handler);
		IModel content = model.cloneModel();
		model.reset();

		iterate(context, model, handler, element, content);
	}
}
