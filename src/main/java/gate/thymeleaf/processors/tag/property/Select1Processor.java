package gate.thymeleaf.processors.tag.property;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Select1Processor extends CheckableProcessor
{

	public Select1Processor()
	{
		super("select1");
	}

	@Override
	protected String getComponentName()
	{
		return "radio";
	}
}
