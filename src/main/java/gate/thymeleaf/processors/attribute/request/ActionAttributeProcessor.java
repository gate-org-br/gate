package gate.thymeleaf.processors.attribute.request;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActionAttributeProcessor extends RequestAttributeProcessor
{

	public ActionAttributeProcessor()
	{
		super("action");
	}
}
