package gate.thymeleaf.processors.tag.property;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SelectNProcessor extends CheckableProcessor
{

	public SelectNProcessor()
	{
		super("selectn");
	}

	@Override
	protected String getComponentName()
	{
		return "checkbox";
	}
}
