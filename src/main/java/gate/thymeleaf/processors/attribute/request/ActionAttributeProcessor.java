package gate.thymeleaf.processors.attribute.request;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActionAttributeProcessor extends RequestAttributeProcessor
{

	public ActionAttributeProcessor()
	{
		super("action");
	}
}
