package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class HiddenProcessor extends InputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public HiddenProcessor()
	{
		super("hidden");
	}
}
