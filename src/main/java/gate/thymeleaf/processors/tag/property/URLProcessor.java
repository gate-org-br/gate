package gate.thymeleaf.processors.tag.property;

import gate.thymeleaf.ELExpressionFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class URLProcessor extends InputProcessor
{

	@Inject
	ELExpressionFactory expression;

	public URLProcessor()
	{
		super("url");
	}

}
