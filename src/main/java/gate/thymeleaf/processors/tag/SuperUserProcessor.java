package gate.thymeleaf.processors.tag;

import gate.entity.User;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class SuperUserProcessor extends TagModelProcessor
{

	public SuperUserProcessor()
	{
		super("superuser");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		var request = ((IWebContext) context).getRequest();
		User user = (User) request.getSession().getAttribute(User.class.getName());
		if (user != null && user.isSuperUser())
			removeTag(context, model, handler);
		else
			model.reset();
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}

}
