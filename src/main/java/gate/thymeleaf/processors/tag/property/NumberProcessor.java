package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Sequence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class NumberProcessor extends InputProcessor
{

	@Inject
	Sequence sequence;

	@Inject
	ELExpression expression;

	public NumberProcessor()
	{
		super("number");
	}
}
