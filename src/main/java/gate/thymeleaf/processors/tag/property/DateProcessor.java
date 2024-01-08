package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import javax.inject.Inject;

public class DateProcessor extends ISOInputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public DateProcessor()
	{
		super("date");
	}
}
