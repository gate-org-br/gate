package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpression;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class URLProcessor extends InputProcessor
{

	@Inject
	ELExpression expression;

	public URLProcessor()
	{
		super("url");
	}

}
