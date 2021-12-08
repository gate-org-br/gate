package gate.thymeleaf.processors.attribute.property;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SpanAttributeProcessor extends ReadOnlyAttributeProcessor
{

	public SpanAttributeProcessor()
	{
		super("span");

	}
}
