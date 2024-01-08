package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SubmitProcessor extends InputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public SubmitProcessor()
	{
		super("submit");
	}
}
