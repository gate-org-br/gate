package gate.thymeleaf.processors.attribute.request;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ModuleAttributeProcessor extends RequestAttributeProcessor
{

	public ModuleAttributeProcessor()
	{
		super("module");
	}
}
