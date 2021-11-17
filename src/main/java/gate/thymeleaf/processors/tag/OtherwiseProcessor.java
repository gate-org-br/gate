package gate.thymeleaf.processors.tag;

import gate.thymeleaf.*;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class OtherwiseProcessor extends ModelProcessor
{

	public OtherwiseProcessor()
	{
		super("otherwise");
	}

	@Override
	protected void doProcess(Model model)
	{
		throw new TemplateProcessingException("Attempt to use a g:otherwise outside a g:choose");
	}
}
