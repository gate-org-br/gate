package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EMailProcessor extends InputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public EMailProcessor()
	{
		super("email");
	}

}
