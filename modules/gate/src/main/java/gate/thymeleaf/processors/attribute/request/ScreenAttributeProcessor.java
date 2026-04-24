package gate.thymeleaf.processors.attribute.request;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScreenAttributeProcessor extends RequestAttributeProcessor
{

	public ScreenAttributeProcessor()
	{
		super("screen");
	}
}
