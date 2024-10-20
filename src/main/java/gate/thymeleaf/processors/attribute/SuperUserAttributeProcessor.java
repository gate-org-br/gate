package gate.thymeleaf.processors.attribute;

import gate.annotation.Current;
import gate.entity.User;
import gate.thymeleaf.Precedence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@ApplicationScoped
public class SuperUserAttributeProcessor extends AttributeProcessor
{

	@Inject
	@Current
	@RequestScoped
	User user;

	public SuperUserAttributeProcessor()
	{
		super(null, "superuser");
	}

	@Override
	public void process(ITemplateContext context, IProcessableElementTag element, IElementTagStructureHandler handler)
	{
		if (user.isSuperUser())
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
