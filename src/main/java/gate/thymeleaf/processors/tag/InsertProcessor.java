package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InsertProcessor extends ModelProcessor
{

	public InsertProcessor()
	{
		super("insert");

	}

	@Override
	protected void doProcess(Model model)
	{
		model.replaceAll(model.request().getAttribute("g-template-content").toString());
	}
}
