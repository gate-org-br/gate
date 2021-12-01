package gate.thymeleaf.processors.attribute;

import gate.entity.User;
import gate.thymeleaf.ELExpression;
import gate.thymeleaf.Precedence;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SuperUserAttributeProcessor extends AttributeProcessor
{

	@Inject
	ELExpression expression;

	public SuperUserAttributeProcessor()
	{
		super(null, "superuser");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		HttpServletRequest request = ((IWebContext) context).getRequest();
		User user = (User) request.getSession().getAttribute(User.class.getName());

		if (user != null && user.isSuperUser())
			handler.removeAttribute("g:superuser");
		else
			handler.removeElement();
	}

	@Override
	public int getPrecedence()
	{
		return Precedence.HIGH;
	}
}
