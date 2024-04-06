package gate.thymeleaf.processors.tag;

import gate.type.Attributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class TFootProcessor extends TagModelProcessor
{

	public TFootProcessor()
	{
		super("tfoot");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		Attributes attributes = Stream.of(element.getAllAttributes())
			.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
				e -> e.getValue(), (a, b) -> a, Attributes::new));

		replaceTag(context, model, handler, "tfoot", attributes);
	}

}
