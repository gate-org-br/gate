package gate.thymeleaf.processors.tag;

import gate.thymeleaf.Model;
import gate.util.Icons;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ApplicationScoped
public class IconProcessor extends ModelProcessor
{

	public IconProcessor()
	{
		super("icon");
	}

	@Override
	protected void doProcess(Model model)
	{
		if (!model.has("type"))
			throw new TemplateProcessingException("Missing required attribute type on g:icon");
		model.replaceAll("<i>" + Icons.getIcon(model.get("type")) + "</i>");
	}

}
