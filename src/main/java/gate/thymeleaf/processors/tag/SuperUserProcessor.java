package gate.thymeleaf.processors.tag;

import gate.entity.User;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
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
		var session = ((IWebContext) context).getExchange().getSession();
		User user = (User) session.getAttributeValue(User.class.getName());
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
