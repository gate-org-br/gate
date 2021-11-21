package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import gate.type.Attributes;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class THeadProcessor extends ModelProcessor
{

	public THeadProcessor()
	{
		super("thead");
	}

	@Override
	protected void doProcess(Model model)
	{
		Attributes attributes = new Attributes();
		model.attributes().filter(e -> e.getValue() != null)
			.forEach(e -> attributes.put(e.getAttributeCompleteName(), e.getValue()));
		model.replaceTag("thead", attributes);
	}

}
