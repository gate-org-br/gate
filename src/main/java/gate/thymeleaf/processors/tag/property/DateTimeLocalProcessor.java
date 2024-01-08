package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import javax.inject.Inject;

public class DateTimeLocalProcessor extends ISOInputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public DateTimeLocalProcessor()
	{
		super("datetime-local");
	}
}
