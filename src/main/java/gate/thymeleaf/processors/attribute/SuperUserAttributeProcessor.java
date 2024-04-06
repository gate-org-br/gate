package gate.thymeleaf.processors.attribute;

import gate.entity.User;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.web.IWebExchange;

@ApplicationScoped
public class SuperUserAttributeProcessor extends AttributeProcessor
{

	public SuperUserAttributeProcessor()
	{
		super(null, "superuser");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		IWebExchange exchange = ((IWebContext) context).getExchange();
		User user = (User) exchange.getSession().getAttributeValue(User.class.getName());

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
