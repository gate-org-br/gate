package gate.thymeleaf.processors.tag;

import gate.annotation.Current;
import gate.entity.User;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class SuperUserProcessor extends TagModelProcessor
{

	@Inject
	@Current
	@RequestScoped
	User user;

	public SuperUserProcessor()
	{
		super("superuser");
	}

	@Override
	public void process(ITemplateContext context, IModel model, IElementModelStructureHandler handler)
	{
		if (user.isSuperUser())
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
