package gate.thymeleaf.processors.tag.iterable;

import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.model.IModel;

@ApplicationScoped
public class TBodyProcessor extends IterableProcessor
{

	public TBodyProcessor()
	{
		super("tbody");
	}

	@Override
	protected void process(Model model)
	{
		Attributes attributes = new Attributes();
		model.attributes().filter(e -> e.getValue() != null)
			.filter(e -> !"source".equals(e.getAttributeCompleteName()))
			.filter(e -> !"target".equals(e.getAttributeCompleteName()))
			.filter(e -> !"depth".equals(e.getAttributeCompleteName()))
			.filter(e -> !"index".equals(e.getAttributeCompleteName()))
			.filter(e -> !"children".equals(e.getAttributeCompleteName()))
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));

		model.removeTag();
		IModel content = model.cloneModel();

		model.removeAll();
		iterate(model, content);

		model.encloseWith("tbody", attributes);
	}
}
