package gate.thymeleaf.processors.tag;

import gate.Command;
import gate.entity.User;
import gate.thymeleaf.processors.attribute.AttributeProcessor;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SecureProcessor extends AttributeProcessor
{

	public SecureProcessor()
	{
		super("secure", null);
	}

	@Override
	public void process(ITemplateContext context,
		IProcessableElementTag element,
		IElementTagStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		User user = (User) request.getSession().getAttribute(User.class.getName());

		if (Command.of(request,
			element.getAttributeValue("module"),
			element.getAttributeValue("screen"),
			element.getAttributeValue("action"))
			.checkAccess(user))
			handler.removeTags();
		else
			handler.removeElement();
	}
}
