package gate.thymeleaf.processors.attribute.request;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ModuleAttributeProcessor extends RequestAttributeProcessor
{

	public ModuleAttributeProcessor()
	{
		super("module");
	}
}
