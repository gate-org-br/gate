package gate.thymeleaf.processors.attribute.property;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TDAttributeProcessor extends ReadOnlyAttributeProcessor
{

	public TDAttributeProcessor()
	{
		super("td");

	}
}
