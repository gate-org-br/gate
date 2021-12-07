package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpression;
import javax.inject.Inject;

public class DateTimeLocalProcessor extends ISOInputProcessor
{

	@Inject
	ELExpression expression;

	public DateTimeLocalProcessor()
	{
		super("datetime-local");
	}
}
