package gate.thymeleaf.processors.attribute.property;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LabelAttributeProcessor extends ReadOnlyAttributeProcessor
{

	public LabelAttributeProcessor()
	{
		super("label");

	}
}
