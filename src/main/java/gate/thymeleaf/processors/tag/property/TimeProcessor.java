package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TimeProcessor extends InputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public TimeProcessor()
	{
		super("time");
	}

}
