package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpression;
import javax.inject.Inject;

public class DateProcessor extends ISOInputProcessor
{

	@Inject
	ELExpression expression;

	public DateProcessor()
	{
		super("date");
	}
}
