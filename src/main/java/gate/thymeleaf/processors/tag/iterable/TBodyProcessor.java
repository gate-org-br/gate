package gate.thymeleaf.processors.tag.iterable;

import gate.type.Attributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class TBodyProcessor extends IterableProcessor
{

	public TBodyProcessor()
	{
		super("tbody");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		IProcessableElementTag element = (IProcessableElementTag) model.get(0);

		Attributes attributes
			= Stream.of(element.getAllAttributes()).filter(e -> e.getValue() != null)
				.filter(e -> !"source".equals(e.getAttributeCompleteName()))
				.filter(e -> !"target".equals(e.getAttributeCompleteName()))
				.filter(e -> !"depth".equals(e.getAttributeCompleteName()))
				.filter(e -> !"index".equals(e.getAttributeCompleteName()))
				.filter(e -> !"children".equals(e.getAttributeCompleteName()))
				.collect(Collectors.toMap(e -> e.getAttributeCompleteName(),
					e -> e.getValue(), (a, b) -> a, Attributes::new));

		removeTag(context, model, handler);
		IModel content = model.cloneModel();
		model.reset();

		add(context, model, handler, "<tbody " + attributes + ">");
		iterate(context, model, handler, element, content);
		add(context, model, handler, "</tbody>");
	}
}
