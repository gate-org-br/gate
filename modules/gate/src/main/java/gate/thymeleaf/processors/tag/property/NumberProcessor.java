package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NumberProcessor extends ISOInputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public NumberProcessor()
	{
		super("number");
	}
}
