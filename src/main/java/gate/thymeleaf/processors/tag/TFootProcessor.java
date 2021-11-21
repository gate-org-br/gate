package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TFootProcessor extends ModelProcessor
{

	public TFootProcessor()
	{
		super("tfoot");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.attributes().filter(e -> e.getValue() != null)
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
		model.replaceTag("tfoot", attributes);
	}

}
